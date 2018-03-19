package org.spg.refactoring.handlers.utilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public class SelectionUtility {

	private SelectionUtility() {}

	
	
	/**
	 * Return the selected project 
	 * @return
	 */
	public static IProject getSelectedProject(){
		IProject project = null; 
		//get the current workbench page
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//get the current selection
		ISelection selection = page.getSelection();
		if (selection instanceof ITreeSelection){
			ITreeSelection treeSelection = (ITreeSelection)selection;
			//get selected object
			Object selectedObject = treeSelection.getFirstElement();
			//get the IProject
			project= Platform.getAdapterManager().getAdapter(selectedObject, IProject.class);
			
			return project;
		}
		else 
			throw new NullPointerException("Selected project is NULL");
	}
	
	
	/**
	 * Return the selected file 
	 * @return
	 */
	public static IFile getSelectedFile(){
		IFile file= null; 
		//get the current workbench page
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//get the current selection
		ISelection selection = page.getSelection();
		if (selection instanceof ITreeSelection){
			ITreeSelection treeSelection = (ITreeSelection)selection;
			//get selected object
			Object selectedObject = treeSelection.getFirstElement();
			//get the IProject
			file= Platform.getAdapterManager().getAdapter(selectedObject, IFile.class);
			
			return file;
		}
		else 
			throw new NullPointerException("Selected project is NULL");
	}
}
