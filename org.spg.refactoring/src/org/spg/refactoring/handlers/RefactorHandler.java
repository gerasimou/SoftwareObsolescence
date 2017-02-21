package org.spg.refactoring.handlers;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.LibraryDetailsDialog;
import org.spg.refactoring.handlers.utilities.SelectionUtility;
import org.spg.refactoring.old.RefactoringAST;
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

		//show library dialog
		LibraryDetailsDialog libraryDialog = new LibraryDetailsDialog();
		libraryDialog.create();
		int reply = libraryDialog.open();
		if (reply != TitleAreaDialog.OK)
			return null;		
		
		boolean OK = MessageUtility.showMessage(shell, MessageDialog.CONFIRM, "Refactor project", 
				"Do you want to refactor the selected project?");		

		if (!OK)
			return null;
		
		IProject project = null;
		try{
			project = SelectionUtility.getSelectedProject();
			//check if the project is a C project
			ICProject cproject = CdtUtilities.getICProject(project);

			if (cproject != null){
					
				//get library dialogue properties
				StringProperties properties = libraryDialog.getProperties();
				String[] oldHeader       	= properties.getProperty(LibraryDetailsDialog.OLD_HEADER).split(",");
				String oldNamespace			= properties.getProperty(LibraryDetailsDialog.OLD_NAMESPACE);
				String newProject			= properties.getProperty(LibraryDetailsDialog.NEW_PROJECT);
				String newLibrary			= properties.getProperty(LibraryDetailsDialog.NEW_LIBRARY);
				String newNamespace			= properties.getProperty(LibraryDetailsDialog.NEW_NAMESPACE);
				
//					RefactoringAST refactorProject = new RefactoringAST(cproject, oldHeader, oldNamespace, newProject, newLibrary, newNamespace);
				RefactoringProject refactorProject = new RefactoringProject(oldHeader, oldNamespace, newProject, newLibrary, newNamespace);
				boolean refactorOK = refactorProject.refactor(project);
				
				if (refactorOK)					
					MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
							   "Refactoring completed", 
							   	String.format("Refactoring project %s completed successfully.", project.getName()));
				else{
					MessageUtility.showMessage(shell, MessageDialog.ERROR, 
							   "Refactoring error", 
							   	String.format("Something went wrong with refactoring project %s. Please check the log.", project.getName()));
				}
			}
			else 
				throw new NullPointerException("Project " + project.getName() + " is not C/C++");
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
