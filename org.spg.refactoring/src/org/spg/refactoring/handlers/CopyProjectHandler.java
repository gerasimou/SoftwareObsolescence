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
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class CopyProjectHandler extends AbstractHandler {

	public CopyProjectHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

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
					CdtUtilities.copyProject(project, project.getName() + "_copy");
				}
			}
		} 
		catch (NullPointerException | CoreException e) {
			MessageUtility.writeToConsole("Console", e.getMessage());
			MessageUtility.showMessage(shell, MessageDialog.ERROR, 
									   "Unexpected Project Nature", 
									   	String.format("Expected a C/C++ Project but got a %s instead.\nProcessing Terminated.", project.getName()));
			e.printStackTrace();
		}
		
		MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
									"Refactoring",
									"Refactor project was executed.");
		return null;
	}
}
