package org.spg.refactoring.handlers;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.RefactoringAST;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

public class RefactorHandler extends AbstractHandler {
	/** Shell handler*/
	private Shell shell = null;
	
	/** Excluded files*/
	private String[] libFiles = new String[]{"tinyxml2.cpp", "tinyxml2.h"};

	
	public RefactorHandler() {
	}


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		boolean answer = MessageUtility.showMessage(shell, MessageDialog.CONFIRM, "Refactor project", 
													"Do you want to refactor the selected project?");
		if (!answer)
			return null;
		
		IProject project = null; 
		//get the current workbench page
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		//get the current selection
		ISelection selection = page.getSelection();
		try{
			if (selection instanceof ITreeSelection){
				ITreeSelection treeSelection = (ITreeSelection)selection;
				//get selected object
				Object selectedObject = treeSelection.getFirstElement();
				//get the IProject
				project= Platform.getAdapterManager().getAdapter(selectedObject, IProject.class);
				//check if the project is a C project
				ICProject cproject = CdtUtilities.getICProject(project);
				if (cproject != null){
					RefactoringAST refactorProject = new RefactoringAST(cproject);
					refactorProject.refactor(libFiles);
					MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
							   "Refactoring completed", 
							   	String.format("Refactoring project %s completed successfully.", project.getName()));
				}
				else 
					throw new NullPointerException("Project " + project.getName() + " is not C/C++");
			}
		} 
		catch (NullPointerException | CoreException e) {
			MessageUtility.writeToConsole("Console", e.getMessage());
			MessageUtility.showMessage(shell, MessageDialog.ERROR, 
									   "Unexpected Project Nature", 
									   	String.format("Expected a C/C++ Project but got a %s instead.\nProcessing Terminated.", project.getName()));
			e.printStackTrace();
		}
		return null;
	}

}
