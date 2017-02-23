/*******************************************************************************
 * Copyright (c) 2016 University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Simos Gerasimou - initial API and implementation
 ******************************************************************************/
package org.spg.refactoring;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.spg.refactoring.ProjectAnalyser.BindingsSet;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

public class RefactoringProject {

	/** project */
	protected ICProject currentCProject = null;
	protected ICProject newCProject		= null;

	/** project index */
	protected IIndex projectIndex = null;
	
	protected static String NEW_PROJECT;
	protected static String NEW_LIBRARYhpp;
	protected static String NEW_LIBRARYcpp;
	public static String NEW_NAMESPACE;
	protected static String NEW_DIR;
	protected static String NEW_INCLUDE_DIRECTIVE;
	protected static Set<String> OLD_NAMESPACES;
	protected static String[] OLD_HEADERS;

	/** Pairs of ITranslationUnit, IASTTranslationUnit **/
	HashMap<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<ITranslationUnit, IASTTranslationUnit>();

	/** Pairs of elements-potential name from standard C++ library that should be included using #include directives*/
	LinkedHashMap<IASTName, String> includeDirectivesMap = new LinkedHashMap<IASTName, String>();
	

	ProjectAnalyser analyser = new ProjectAnalyser(this);
	RefactoredProjectCreator refactorer = new RefactoredProjectCreator(this);

	
	
	/** Class constructor */
	public RefactoringProject(String[] oldHeader, String oldNamespace,
							String newProject, String newLibrary, String newNamespace) {
		NEW_PROJECT				= newProject;
		NEW_NAMESPACE  			= newNamespace;
		NEW_LIBRARYcpp			= newLibrary +".cpp";
		NEW_LIBRARYhpp			= newLibrary +".hpp";
		NEW_DIR					= "src/" + newLibrary;
		NEW_INCLUDE_DIRECTIVE	= newLibrary +"/"+  NEW_LIBRARYhpp;
		OLD_NAMESPACES			= new HashSet<String>(Arrays.asList(oldNamespace));
		OLD_HEADERS				= oldHeader;
	}


	/**
	 * The main refactoring method
	 */
 	public boolean refactor(IProject project) {
		try {
			
			//get existing cProject
			this.currentCProject		= CdtUtilities.getICProject(project);
			this.projectIndex 	= CCorePlugin.getIndexManager().getIndex(currentCProject);			
			
			//1) find all translation units
			MessageUtility.writeToConsole("Console", "Generating ASTs for selected project.");
			parseProject();

			//2) analyse project
			analyser.analyseExistingProject(projectIndex, astCache);
						
			//3) copy project
			IProject newProject	= CdtUtilities.copyProject(currentCProject.getProject(), RefactoringProject.NEW_PROJECT);
			if (newProject == null)
				throw new Exception("There was something wrong with copying project " + currentCProject.getProject().getName());
			newCProject = CdtUtilities.getICProject(newProject);
			
			/** Get refactoring information*/
			BindingsSet bindingsSet  							 = analyser.getBindings();
			Map<IASTName, String> includeDirectivesMap 			 = analyser.getIncludeDirectives();
			Map<ICPPClassType, List<ICPPMember>> classMembersMap = analyser.getClassMembersMap();
			Collection<String> tusUsingLib					 	 = analyser.getTUsUsingLib();
			
			//5) create refactored project
			refactorer.createRefactoredProject(newCProject, projectIndex, bindingsSet, includeDirectivesMap, classMembersMap, tusUsingLib);
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				//if an exception is thrown --> delete the newly created project
				newCProject.getProject().delete(false, new NullProgressMonitor());
			} catch (CoreException e1) {
				e1.printStackTrace();
				return false;
			}
			return false;
		}
	}
 	
 	
 	/**
 	 * Parse project and for each translation unit generate its AST
 	 * @throws CoreException
 	 */
 	private void parseProject() throws CoreException{
		List<ITranslationUnit> tuList = CdtUtilities.getProjectTranslationUnits(currentCProject, RefactoringProject.OLD_HEADERS);

		// for each translation unit get its AST
		for (ITranslationUnit tu : tuList) {
			// get AST for that translation unit
			System.out.println(tu.getElementName());
			IASTTranslationUnit ast = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
			// cache the tu & ast pair
			astCache.put(tu, ast);
		}
 	}

 	
 		
 	/**
	 * Given an index name and a set of class names, this function searches the 
	 * parent of the node with that name until it finds the parent which is instance of
	 * 
	 */
	@SuppressWarnings("rawtypes")
	protected IASTNode findNodeFromIndex(IIndexName indexName, Class...classes){
		try {
			//find translation unit & corresponding ast, cache ast if necessary
			ITranslationUnit tu;
			tu = CdtUtilities.getTranslationUnitFromIndexName(indexName);
			IASTTranslationUnit ast = null;
			if (astCache.containsKey(tu)){
				ast = astCache.get(tu);
			}
			else{
				ast = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
				astCache.put(tu, ast);
			}
			
			//find enumeration 
			IASTName name = (IASTName) ast.getNodeSelector(null).findEnclosingNode(indexName.getNodeOffset(), indexName.getNodeLength());
			IASTNode node = name;
			
			while ( (node != null) && !(nodeIsInstance(classes, node)) ){
				node =  node.getParent();
			}
			assert (nodeIsInstance(classes, node));
			return node;
		} 
		catch (CoreException e){
			e.printStackTrace();
		}
		return null;
	}
		
	
	/**
	 * Checks if this node is instance of any of the given classes
	 * @param classes
	 * @param node
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean nodeIsInstance (Class [] classes, IASTNode node){
		for (Class clazz: classes){
			if (clazz.isInstance(node))
				return true;
		}
		return false;
	}




	public boolean analyseOnly (IProject project){
		try {
			//get existing cProject
			this.currentCProject		= CdtUtilities.getICProject(project);
			this.projectIndex 	= CCorePlugin.getIndexManager().getIndex(currentCProject); 
			
			//1) find all translation units
			MessageUtility.writeToConsole("Console", "Generating ASTs for selected project.");
			parseProject();

			//2) analyse project
			analyser.analyseExistingProject(projectIndex, astCache);
			
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Collection<String> getTUsUsingLib (){
		return analyser.getTUsUsingLib();
	}
}
