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
package org.spg.refactoring.utilities;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.index.IIndexFile;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICContainer;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.model.ISourceRoot;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.settings.model.ICProjectDescription;
import org.eclipse.cdt.core.settings.model.ICProjectDescriptionManager;
import org.eclipse.cdt.core.settings.model.extension.CConfigurationData;
import org.eclipse.cdt.managedbuilder.core.IBuilder;
import org.eclipse.cdt.managedbuilder.core.ManagedBuildManager;
import org.eclipse.cdt.managedbuilder.internal.core.Configuration;
import org.eclipse.cdt.managedbuilder.internal.core.ManagedProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;

public class CdtUtilities {
	private CdtUtilities() {}
	
	
	/** 
	 * Get a list of projects for the current workspace
	 */
	public static List<IProject> getIProjects(){
		//get current workspace
		IWorkspace workspace 	= ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root		= workspace.getRoot();
		//get all projects in this workspace
		IProject[] projects = root.getProjects();
		
		return Arrays.asList(projects);
	}
	
	
	/** 
	 * Get the IProject with the given project name
	 * @return 
	*/
	public static IProject getIProject (String projectName){
		//get current workspace
		IWorkspace workspace 	= ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root		= workspace.getRoot();
		//get all projects in this workspace
		IProject[] projects = root.getProjects();
		//fing the project with the given name
		for (IProject project : projects){
			if (project.getName().equals(projectName)){
				return project;
			}
		}
		throw new NoSuchElementException(String.format("Project %s not found", projectName));
	}
	
	
	/**
	 * Create an ICProject from an IProject
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	public static ICProject getICProject (IProject project) throws CoreException{
		ICProject cProject;
		if (project.isOpen() && project.hasNature(org.eclipse.cdt.core.CProjectNature.C_NATURE_ID)){
			cProject = CoreModel.getDefault().create(project);
			return cProject;
		}
		return null;
	}

	
	/** 
	 * Get the ICProject with the given project name
	 * @return 
	 * @throws CoreException 
	*/
	public static ICProject getICProject (String projectName) throws CoreException{
		return CdtUtilities.getICProject(getIProject(projectName));
	}
	
	
	/**
	 * Get a list of ICProjects
	 * @param projects
	 * @return
	 * @throws CoreException 
	 */
	public static List<ICProject> getICProjects (List<IProject> projects) throws CoreException{
		List<ICProject> sourceProjects = new ArrayList<ICProject>();
		for (IProject project : projects){
			ICProject cProject = getICProject(project);
			if(cProject != null){
				sourceProjects.add(cProject);
			}
		}
		return sourceProjects;
	}
	
	
	
	public static IFile createNewFile (ICProject cproject, String folderName, String filename){
		try {
			//check if the project exists and is open, it should be, but check anyway
			IProject project = cproject.getProject();
			if (!project.exists())
				throw new NoSuchElementException ("Project " + project.getName() +" does not exist!");
			if (!project.isOpen())
				project.open(null);
				
			//get the folder
			IFolder folder  = project.getFolder(folderName);  
			if (!folder.exists())
				folder.create(IResource.NONE, true, null);
			
			//check if this directory is a source directory
			if (!cproject.isOnSourceRoot(folder))
				throw new IllegalArgumentException("Directory " + folderName + " is not a source directory");
	
			//create new IFile
			IFile file = folder.getFile(filename);
			if (!file.exists()){
				InputStream source = new ByteArrayInputStream("".getBytes());
			    file.create(source, IResource.NONE, null);
			}
			return file;
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static boolean appendToFile (IFile file, String output){
		try {
			if (!file.exists())
				return false;
			
			InputStream source = new ByteArrayInputStream(output.getBytes());

			file.appendContents(source, IFile.FORCE, null);
			return true;
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}
		
	
	/**
	 * Returns as List all the translation units for the given project.
	 * This function considers all the source directories and sub-directories of this projects
	 * and excludes any translation units whose name is within {@code excludedFiles} array
	 * @param cproject the current C/C++ project
	 * @param excludedFiles an array of filenames for which a translation unit <b>won't</b> be generated
	 * @return
	 */
	public static List<ITranslationUnit> getProjectTranslationUnits (ICProject cproject,  String[] excludedFiles) {
		List<ITranslationUnit> tuList = new ArrayList<ITranslationUnit>();
		
		HashSet<String> excludedFilesSet = new HashSet<String>();
		if (excludedFiles != null)
			excludedFilesSet.addAll(Arrays.asList(excludedFiles));
		
		//get source folders
		try {
			for (ISourceRoot sourceRoot : cproject.getSourceRoots()){
//				System.out.println(sourceRoot.getLocationURI().getPath());
				//get all elements
				for (ICElement element : sourceRoot.getChildren()){
					//if it is a container (i.e., a source folder)
//					System.out.println(element.getElementName() +"\t"+ element.getElementType() +"\t"+element.getClass());
					if (element.getElementType() == ICElement.C_CCONTAINER){
						recursiveContainerTraversal((ICContainer)element, tuList, excludedFilesSet);
					}
					else{
						ITranslationUnit tu		= (ITranslationUnit) element;
						if (! excludedFilesSet.contains((tu.getFile().getLocation().toString())))
							tuList.add(tu);
					}

				}
			}
		} catch (CModelException e) {
			e.printStackTrace();
		}
		return tuList;
	}
	
	
	private static void recursiveContainerTraversal (ICContainer container, List<ITranslationUnit> tuList, HashSet<String> excludedFilesSet) throws CModelException{
		for (ICContainer inContainer : container.getCContainers()){
			recursiveContainerTraversal(inContainer, tuList, excludedFilesSet);
		}
		
		for (ITranslationUnit tu : container.getTranslationUnits()){
			if (! excludedFilesSet.contains((tu.getFile().getLocation().toString())))
				tuList.add(tu);			
		}
	}
	
	
	
	/**
	 * Returns as List all the translation units for the given project.
	 * This function considers all the source directories and sub-directories of this project
	 * @param currentCproject the current C/C++ project
	 * @return
	 */
	public static List<ICElement> getICElementsFromProject(IParent parent,  Class<?> clazz, List<ICElement> list) {				
		try {
			for (ICElement element : parent.getChildren()){
					if (clazz.isInstance(element)){
						list.add(element);
					}
					
					if (element instanceof ICContainer){
						getICElementsFromProject((IParent)element, clazz, list);
					}				
			}
		}
		catch (CModelException e){
			e.printStackTrace();
		}
		return list;			
	}
	
	
	/**
	 * Returns as List all the ICElements (TranslationUnits & ICContainer) for the given project.
	 * This function analyses all the source directories and sub-directories of this project
	 * @param currentCproject the current C/C++ project
	 * @return
	 */
	public static List<Object> getElementsFromProject(IParent parent,  Class<?> clazz, List<Object> list) {				
		try {
			for (ICElement element : parent.getChildren()){
					if (clazz.isInstance(element)){
						list.add(element);
					}
					else if (element instanceof IParent && !element.getElementName().equals("Debug")){
						getElementsFromProject((IParent)element, clazz, list);
					}				
			}
		}
		catch (CModelException e){
			e.printStackTrace();
		}
		return list;			
	}
	
	
	/**
	 * Given an index name, return the corresponding translation unit
	 * @see CxxAstUtils.getTranslationUnitFromIndexName(IIndexName decl)
	 * @param decl
	 * @return
	 * @throws CoreException
	 */
	public static ITranslationUnit getTranslationUnitFromIndexName(IIndexName decl) throws CoreException {
		IIndexFile file = decl.getFile();
		if (file != null) {
			return CoreModelUtil.findTranslationUnitForLocation(file.getLocation().getURI(), null);	
		}
		return null;
	}


	/**
	 * Create a new empty project
	 * @param projectName
	 * @return
	 */
	public static boolean createProject (String projectName){
		try {
			IProgressMonitor progressMonitor = new NullProgressMonitor();
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject project = root.getProject(projectName);
			project.create(progressMonitor);
			project.open(progressMonitor);	
			return true;
		} catch (CoreException e) {		
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Copy the given project into the workspace under the newProject name
	 * @param project
	 * @param newProject
	 * @return
	 * @throws CoreException
	 */
	public static IProject copyProject(IProject project, String newProject) throws CoreException {
		try{			
		    IProgressMonitor monitor = new NullProgressMonitor();
		    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		    IProjectDescription projectDescription = project.getDescription();
	
		    // create clone project in workspace
		    IProjectDescription cloneDescription = workspaceRoot.getWorkspace().newProjectDescription(newProject);
		    // copy project files
		    project.copy(cloneDescription, true, monitor);
		    IProject clone = workspaceRoot.getProject(newProject);
		    
		    // copy the project properties
		    cloneDescription.setNatureIds(projectDescription.getNatureIds());
		    cloneDescription.setReferencedProjects(projectDescription.getReferencedProjects());
		    cloneDescription.setDynamicReferences(projectDescription.getDynamicReferences());
		    cloneDescription.setBuildSpec(projectDescription.getBuildSpec());
		    cloneDescription.setReferencedProjects(projectDescription.getReferencedProjects());
		    clone.setDescription(cloneDescription, null);
		    return clone;
		}
		catch (CoreException e){
			e.printStackTrace();
			MessageUtility.writeToConsole("console", e.getMessage());
			return null;
		}
	}
	
	
	/**
	 * Create a new C++ project
	 * @param projectName
	 * @throws OperationCanceledException
	 * @throws CoreException
	 */
	@SuppressWarnings("restriction")
	public static void createNewProject(String projectName) throws OperationCanceledException, CoreException{
		// Create and persist Standard Makefile project
		{
		// Create model project and accompanied project description
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();

		IProject newProjectHandle = root.getProject(projectName +"_test");
		Assert.isNotNull(newProjectHandle);
		Assert.isTrue(!newProjectHandle.exists());

		IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
		IProject project 				= CCorePlugin.getDefault().createCDTProject(description, newProjectHandle, new NullProgressMonitor());
		Assert.isTrue(newProjectHandle.isOpen());

		ICProjectDescriptionManager mngr = CoreModel.getDefault().getProjectDescriptionManager();
		ICProjectDescription des 		 = mngr.createProjectDescription(project, false);
		ManagedProject mProj 			 = new ManagedProject(des);

		Configuration cfg = new Configuration(mProj, null, "your.configuration.id", "YourConfigurationName");

		IBuilder bld = cfg.getEditableBuilder();
		Assert.isNotNull(bld);
		Assert.isTrue(!bld.isInternalBuilder());

		bld.setManagedBuildOn(false);

		CConfigurationData data = cfg.getConfigurationData();
		Assert.isNotNull(data);
		des.createConfiguration(ManagedBuildManager.CFG_DATA_PROVIDER_ID, data);

		// Persist the project description
		mngr.setProjectDescription(project, des);

		project.close(null);
		}
	}
	
}
