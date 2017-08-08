/*******************************************************************************
 * Copyright (c) 2017 University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Simos Gerasimou - initial API and implementation
 ******************************************************************************/
package org.spg.refactoring;

import java.io.File;
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
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spg.refactoring.AnalysisData.Datum;
import org.spg.refactoring.ProjectAnalyser.BindingsSet;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.Utility;

import exceptions.RefactoringException;

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
	
	/** Logger instance*/
	Logger LOG = LoggerFactory.getLogger (RefactoringProject.class);

		

	private LibraryAnalyser libraryAnalyser = new LibraryAnalyser();
	private ProjectAnalyser projectAnalyser = new ProjectAnalyser(this);
	private ProjectRefactorer refactorer 	= new ProjectRefactorer(this);

	
	
	
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
	public boolean refactor(IProject project) {
		try {
			//1) copy project
			IProject newProject		= CdtUtilities.copyProject(project, RefactoringProject.NEW_PROJECT);
			if (newProject == null){
				String msg = "There was something wrong with copying project " + project.getName();
				LOG.error(msg, new RefactoringException(msg));
			}
			LOG.info("Project " + newProject.getName() +" copied successfully");

			
			//2) modify user selected files to match the structure of the copied project
			modifySelectionsToNewProject(project, newProject);
			
			//3) get cProject and index it: currentCProject == newCProject since we operate on the copied project
			this.currentCProject	= CdtUtilities.getICProject(newProject);
			this.newCProject 		= CdtUtilities.getICProject(newProject);
			CCorePlugin.getIndexManager().reindex(currentCProject);
			CCorePlugin.getIndexManager().joinIndexer(IIndexManager.FOREVER, new NullProgressMonitor()); // wait for indexing to complete.
			this.projectIndex 		= CCorePlugin.getIndexManager().getIndex(currentCProject);			
			
			//4) find all translation units
			parseProject();

			LOG.info("\nLib:\t" 	+ libASTCache.size());
			for (ITranslationUnit tu : libASTCache.keySet())
				LOG.info(tu.getFile().getFullPath().toOSString());
			System.out.println("\nProject:\t" + projectASTCache.size());
			for (ITranslationUnit tu : projectASTCache.keySet())
				LOG.info(tu.getFile().getFullPath().toOSString());
			
			//5) analyse library
			libraryAnalyser.analyseLibrary(projectIndex, libASTCache);
			
			//6) analyse project
			projectAnalyser.analyseExistingProject(projectIndex, projectASTCache);
						
			
			/** Get refactoring information*/
			BindingsSet bindingsSet  							 	= projectAnalyser.getBindings();
			Map<String, IASTName> includeDirectivesMap 			 	= projectAnalyser.getIncludeDirectives();
			Map<ICPPClassType, List<ICPPMember>> classMembersMap 	= projectAnalyser.getClassMembersMap();
			Collection<ITranslationUnit> tusUsingLib			 	= projectAnalyser.getTUsUsingLib();
			Collection<IASTPreprocessorMacroDefinition> macrosList	= projectAnalyser.getMacrosList();
						
			//7) refactor
			refactorer.createRefactoredProject(	newCProject, projectIndex, bindingsSet, includeDirectivesMap, 
												classMembersMap, projectASTCache, tusUsingLib, macrosList);
			
			LOG.info("DONE");
			
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
 	
 	
	public boolean analyseOnly (IProject project, File analysisDir){
		try {
			//get existing cProject
			this.currentCProject	= CdtUtilities.getICProject(project);
			this.projectIndex 		= CCorePlugin.getIndexManager().getIndex(currentCProject); 
			
			//1) find all translation units
			parseProject();

			//2) analyse project
			libraryAnalyser.analyseLibrary(projectIndex, libASTCache);
			projectAnalyser = new ProjectAnalyser(this);
			projectAnalyser.analyseExistingProject(projectIndex, projectASTCache);
			
			/** Get refactoring information*/
//			BindingsSet bindingsSet  							 	= projectAnalyser.getBindings();
//			Map<String, IASTName> includeDirectivesMap 			 	= projectAnalyser.getIncludeDirectives();
			Map<ICPPClassType, List<ICPPMember>> classMembersMap 	= projectAnalyser.getClassMembersMap();
			Collection<ITranslationUnit> tusUsingLib			 	= projectAnalyser.getTUsUsingLib();
//			Collection<IASTPreprocessorMacroDefinition> macrosList	= projectAnalyser.getMacrosList();
			AnalysisData analysisData								= projectAnalyser.getAnalysisData();
			
			exportAnalysisResults(analysisDir, tusUsingLib, classMembersMap, analysisData);
			
			for (Map.Entry<ITranslationUnit, Integer> entry : projectAnalyser. tusUsingLibMap.entrySet()){
				LOG.info(entry.getKey() +"\t"+ entry.getKey().getLocation() +"\t"+ entry.getValue());
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
 		LOG.info("Modifying selections to new project");
		
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
		LOG.info("Headers:\t" + LIB_HEADERS);
		
		Set<String> excludedFilesSet = new HashSet<String>();
		for (String excFile : EXCLUDED_FILES){
			if (excFile.contains(projectPath)){
				String excFileNewProject = excFile.replace(projectPath, newProjectPath);
				excludedFilesSet.add(excFileNewProject);
			}
		}
		EXCLUDED_FILES = excludedFilesSet;
		LOG.info("Excluded files:\t" + EXCLUDED_FILES);
 	}
 	
 	
 	/**
 	 * Parse project and for each translation unit generate its AST
 	 * and store it in the appropriate collection
 	 * @throws CoreException
 	 */
 	private void parseProject() throws CoreException{
		MessageUtility.writeToConsole("Console", "Generating ASTs for project "+ currentCProject.getElementName());
		LOG.info("Generating ASTs for project " + currentCProject.getElementName());

		List<ITranslationUnit> tuList = CdtUtilities.getProjectTranslationUnits(currentCProject,EXCLUDED_FILES);

		// for each translation unit get its AST
		for (ITranslationUnit tu : tuList) {
			// get AST for that translation unit
			LOG.info(tu.getElementName());
			IASTTranslationUnit ast = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
			// cache the tu & ast pair
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
			else{//then it's a native library (e.g., stdio.h); do we need it in the AST?
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
		
	
	public Map<String, String> getTUsUsingMapAsString (){
		return projectAnalyser.getTUsUsingMapAsString();
	}
	
	
	private void exportAnalysisResults(File analysisDir, Collection<ITranslationUnit> tusUsingLib, 
									   Map<ICPPClassType, List<ICPPMember>> classMembersMap, AnalysisData analysisData){
		//export files using the old library
		String fileFullPath = analysisDir.getAbsolutePath() + File.separator + "FilesUsingOldLib.txt";
		StringBuilder str = new StringBuilder();
		for (ITranslationUnit tu : tusUsingLib){
			str.append(tu.getFile().getLocationURI().getPath().toString() +"\n");
		}
		Utility.exportToFile(fileFullPath, str.toString(), false);
		
		 
		//export detailed data of files/classes/functions using the old lib
		fileFullPath = analysisDir.getAbsolutePath() + File.separator + "UsingOldLibDetails.txt";
		str.setLength(0);
		for (AnalysisData.Datum datum: analysisData.getDataList()){
			str.append(datum.toString());
			str.append(System.lineSeparator());
		}
		Utility.exportToFile(fileFullPath, str.toString(), false);
		
		//export class-member mapping
		fileFullPath = analysisDir.getAbsolutePath() + File.separator + "ClassMemberMapping.txt";
		str.setLength(0);
		for (ICompositeType composite : classMembersMap.keySet()){
			str.append(composite.getName() +":\t");
			str.append(composite.getClass().getName() +":\t");
			if (classMembersMap.get(composite)!=null)
				str.append(System.lineSeparator() +"\t"+ Arrays.toString(classMembersMap.get(composite).toArray()));
			str.append(System.lineSeparator());
		}
		Utility.exportToFile(fileFullPath, str.toString(), false);

	}

}
