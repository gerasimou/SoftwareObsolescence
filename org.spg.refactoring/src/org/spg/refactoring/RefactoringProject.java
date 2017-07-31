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

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
import org.eclipse.cdt.core.index.IIndexManager;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.ProjectAnalyser.BindingsSet;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

public class RefactoringProject {

	/** project */
	protected ICProject currentCProject = null;
	protected ICProject newCProject		= null;

	/** project index */
	protected IIndex projectIndex = null;
	
	protected static String 			NEW_PROJECT;
	protected static String 			NEW_LIBRARYhpp;
	protected static String 			NEW_LIBRARYcpp;
	public static String 				NEW_NAMESPACE;
	protected static String 			NEW_DIR;
	protected static String 			NEW_INCLUDE_DIRECTIVE;
	protected static Set<String> 		LIB_NAMESPACES;
	protected static Set<String> 		LIB_HEADERS;
	protected static Set<String>		EXCLUDED_FILES;

	/** Pairs of ITranslationUnit, IASTTranslationUnit **/
	HashMap<ITranslationUnit, IASTTranslationUnit> projectASTCache;
	
	/** Pairs of ITranslationUnit, IASTTranslationUnit for the obsolete library**/
	HashMap<ITranslationUnit, IASTTranslationUnit> libASTCache;
		

	LibraryAnalyser libraryAnalyser = new LibraryAnalyser();
	ProjectAnalyser projectAnalyser = new ProjectAnalyser(this);
	ProjectRefactorer refactorer 	= new ProjectRefactorer(this);

	
	
	/** Class constructor */
	public RefactoringProject(String[] libHeaders, String[] excludedFiles, String newProject, String newLibrary, String newNamespace) {
		LIB_NAMESPACES			= new HashSet<String>();
		LIB_HEADERS				= new HashSet<String>(Arrays.asList(libHeaders));
		EXCLUDED_FILES			= new HashSet<String>(Arrays.asList(excludedFiles));
		NEW_PROJECT				= newProject;
		NEW_NAMESPACE  			= newNamespace;
		NEW_LIBRARYcpp			= newLibrary +".cpp";
		NEW_LIBRARYhpp			= newLibrary +".hpp";
		NEW_DIR					= "src/" + newLibrary;
		NEW_INCLUDE_DIRECTIVE	= newLibrary +"/"+  NEW_LIBRARYhpp;
		
		this.projectASTCache 		= new HashMap<ITranslationUnit, IASTTranslationUnit>();
		this.libASTCache 			= new HashMap<ITranslationUnit, IASTTranslationUnit>();
	}


	/**
	 * The main refactoring method
	 */
 	@SuppressWarnings("restriction")
	public boolean refactor(IProject project) {
		try {
			//1) copy project
			IProject newProject		= CdtUtilities.copyProject(project, RefactoringProject.NEW_PROJECT);
			if (newProject == null)
				throw new Exception("There was something wrong with copying project " + currentCProject.getProject().getName());

			//2) modify user selected files to match the structure of the copied project
			modifySelectionsToNewProject(project, newProject);
			
			//3) get cProject and index it: currentCProject == newCProject since we operate on the copied project
			this.currentCProject	= CdtUtilities.getICProject(newProject);
			this.newCProject 		= CdtUtilities.getICProject(newProject);
			CCorePlugin.getIndexManager().reindex(currentCProject);
			CCorePlugin.getIndexManager().joinIndexer(IIndexManager.FOREVER, new NullProgressMonitor()); // wait for the indexing job to complete.
			this.projectIndex 		= CCorePlugin.getIndexManager().getIndex(currentCProject);			
			
			//4) find all translation units
			MessageUtility.writeToConsole("Console", "Generating ASTs for selected project.");
			parseProject();

			System.out.println("\nLib:\t" 	+ libASTCache.size());
			for (ITranslationUnit tu : libASTCache.keySet())
				System.out.println(tu.getFile().getFullPath());
			System.out.println("\nProject:\t" + projectASTCache.size());
			for (ITranslationUnit tu : projectASTCache.keySet())
				System.out.println(tu.getFile().getFullPath());
			
			//5) analyse library
			libraryAnalyser.analyseLibrary(projectIndex, libASTCache);
			
			//6) analyse project
			projectAnalyser.analyseExistingProject(projectIndex, projectASTCache);
						
			
			/** Get refactoring information*/
			BindingsSet bindingsSet  							 = projectAnalyser.getBindings();
			Map<IASTName, String> includeDirectivesMap 			 = projectAnalyser.getIncludeDirectives();
			Map<ICPPClassType, List<ICPPMember>> classMembersMap = projectAnalyser.getClassMembersMap();
			Collection<ITranslationUnit> tusUsingLib			 = projectAnalyser.getTUsUsingLib();
				
//			doRefactor(tusUsingLib);
			
			//7) refactor
			refactorer.createRefactoredProject(newCProject, projectIndex, bindingsSet, includeDirectivesMap, classMembersMap, projectASTCache, tusUsingLib);
			
			System.out.println("DONE");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				//if an exception is thrown --> delete the newly created project
				if (newCProject!=null)
					newCProject.getProject().delete(false, new NullProgressMonitor());
			} catch (CoreException e1) {
				e1.printStackTrace();
				return false;
			}
			return false;
		}
	}
 	
 	
	public boolean analyseOnly (IProject project){
		try {
			//get existing cProject
			this.currentCProject	= CdtUtilities.getICProject(project);
			this.projectIndex 		= CCorePlugin.getIndexManager().getIndex(currentCProject); 
			
			//1) find all translation units
			MessageUtility.writeToConsole("Console", "Generating ASTs for selected project.");
			parseProject();

			//2) analyse project
			libraryAnalyser.analyseLibrary(projectIndex, libASTCache);
			projectAnalyser = new ProjectAnalyser(this);
			projectAnalyser.analyseExistingProject(projectIndex, projectASTCache);
			
//			for (ITranslationUnit tuEntry : projectAnalyser.tusUsingLibMap.keySet()){
			for (Map.Entry<ITranslationUnit, Integer> entry : projectAnalyser. tusUsingLibMap.entrySet()){
				System.out.println(entry.getKey() +"\t"+ entry.getKey().getLocation() +"\t"+ entry.getValue());
			}
			
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	@SuppressWarnings("restriction")
	private void doRefactor(Collection<ITranslationUnit> tusUsingLib) throws CoreException, InvocationTargetException, InterruptedException{
		try{
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
//			refactorer.refactorFullyQualifiedNames(tusUsingLib, );
			
//			ITranslationUnit tu = (ITranslationUnit) tusUsingLib.toArray()[0];
	//		CRefactory.getInstance().rename(shell, tu.getIncludes()[0]);
	//		CRenameAction rename = new CRenameAction();
			
//			CRefactoringArgument refArgument	= new CRefactoringArgument( tu.getIncludes()[0]);
//			CRenameProcessor refProcessor		= new CRenameProcessor(CRefactory.getInstance(), refArgument);
//			refProcessor.setReplacementText("replacementInclude");
			
//			CRenameRefactoring renRefactoring = new CRenameRefactoring(refProcessor);
	//		RenameSupport.openDialog(shell, renRefactoring);
	//		renRefactoring.checkInitialConditions(new NullProgressMonitor());
//			refProcessor.lockIndex();
	////		renRefactoring.set
	//		Change c = renRefactoring.createChange(new NullProgressMonitor());
	//        CRenameRefactoringPreferences preferences = new CRenameRefactoringPreferences();
	//        refProcessor.setSelectedOptions(preferences.getOptions());
	//        refProcessor.setExhaustiveSearchScope(preferences.getScope());
	//        refProcessor.setWorkingSetName(preferences.getWorkingSet());
//			RenameSupport renameSupport  = RenameSupport.create(refProcessor);
//			renameSupport.perform(shell, CUIPlugin.getActivePage().getWorkbenchWindow());
//			refProcessor.unlockIndex();
	//		RenameSupport.openDialog(shell, refactoring);
		}
		catch (Exception e){
			e.printStackTrace();
		}
 	}
 	
 	
	
	/**
	 * Since we work with a copy project we also need to change the absolute paths
	 * @param project
	 * @param newProject
	 */
 	private void modifySelectionsToNewProject(IProject project, IProject newProject){
		System.out.println("\nModifying selections to new project");
		
		Set<String> headersSet = new HashSet<String>();
		String projectPath 		= project.getLocation().toOSString();
		String newProjectPath	= newProject.getLocation().toOSString();
		for (String libHeader : LIB_HEADERS){
			if (libHeader.contains(projectPath)){
				String libHeaderNewProject = libHeader.replace(projectPath, newProjectPath);
				headersSet.add(libHeaderNewProject);
			}
		}
		LIB_HEADERS = headersSet;
		System.out.println("Headers:\t" + LIB_HEADERS);
		
		Set<String> excludedFilesSet = new HashSet<String>();
		for (String excFile : EXCLUDED_FILES){
			if (excFile.contains(projectPath)){
				String excFileNewProject = excFile.replace(projectPath, newProjectPath);
				excludedFilesSet.add(excFileNewProject);
			}
		}
		EXCLUDED_FILES = excludedFilesSet;
		System.out.println("Excluded files:\t" + EXCLUDED_FILES);
 	}
 	
 	
 	/**
 	 * Parse project and for each translation unit generate its AST
 	 * @throws CoreException
 	 */
 	private void parseProject() throws CoreException{
		MessageUtility.writeToConsole("Console", "Generating ASTs for project "+ currentCProject.getElementName());
		System.out.println("\nGenerating ASTs for project " + currentCProject.getElementName());

		List<ITranslationUnit> tuList = CdtUtilities.getProjectTranslationUnits(currentCProject,EXCLUDED_FILES);

		// for each translation unit get its AST
		for (ITranslationUnit tu : tuList) {
			// get AST for that translation unit
			System.out.println(tu.getElementName());
			IASTTranslationUnit ast = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
			// cache the tu & ast pair
//			System.out.println(tu.get			getFileLocation().getFileName();
			if (LIB_HEADERS.contains(tu.getFile().getLocation().toString()))
				libASTCache.put(tu, ast);
			else
				projectASTCache.put(tu, ast);
		}
 	}

 	
 		
 	/**
	 * Given an index name and a set of class names, this function searches the 
	 * parent of the node with that name until it finds the parent which is instance of
 	 * @param indexName
 	 * @param classes
 	 * @return
 	 */
	@SuppressWarnings("rawtypes")
	protected IASTNode findNodeFromIndex(IIndexName indexName, boolean locked, Class...classes){
		try {
			//find translation unit & corresponding ast, cache ast if necessary
			ITranslationUnit tu;
			tu = CdtUtilities.getTranslationUnitFromIndexName(indexName);
			IASTTranslationUnit ast = null;
			if (projectASTCache.containsKey(tu)){
				ast = projectASTCache.get(tu);
			}
			else if (libASTCache.containsKey(tu)){
				ast = libASTCache.get(tu);
			}
			else{//then it's a native library (e.g., stdio.h); do we need in the AST?
				if (locked)
					return null;
				ast = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
				projectASTCache.put(tu, ast);
			}
			
			//find 
			IASTNode node = ast.getNodeSelector(null).findEnclosingNode(indexName.getNodeOffset(), indexName.getNodeLength());
//			IASTName name = (IASTName)node;
			
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
	
	
	public Collection<String> getTUsUsingLibAsString (){
		return projectAnalyser.getTUsUsingLibAsString();
	}
	
	public Map<String, String> getTUsUsingMapAsString (){
		return projectAnalyser.getTUsUsingMapAsString();
	}

}
