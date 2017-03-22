package org.spg.refactoring.handlers;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.ObsoleteLibraryDialog;
import org.spg.refactoring.handlers.utilities.SelectionUtility;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

public class ProjectAnalyserHandler extends AbstractHandler {
	/** Shell handler*/
	private Shell shell = null;

	/** Library dialog*/
	ObsoleteLibraryDialog libraryDialog;
	
	public ProjectAnalyserHandler() {
		libraryDialog = new ObsoleteLibraryDialog();
	}


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		IProject project = null;
		try{
			project = SelectionUtility.getSelectedProject();
			//check if the project is a C project
			ICProject cproject = CdtUtilities.getICProject(project);

			if (cproject != null){

				//show library dialog
				libraryDialog.setDialogPath(project.getLocation().toOSString());				
				libraryDialog.create();
				libraryDialog.create();
				int reply = libraryDialog.open();
				if (reply != TitleAreaDialog.OK)
					return null;		

				boolean OK = MessageUtility.showMessage(shell, MessageDialog.CONFIRM, "Analysing project", 
						"Project analysis will begin now, OK?");		
				if (!OK)
					return null;
				
				//get library dialogue properties
				StringProperties properties = libraryDialog.getProperties();
				String[] libHeaders       	= properties.getProperty(ObsoleteLibraryDialog.LIB_HEADERS).split(",");
				String[] excludedFiles		= properties.getProperty(ObsoleteLibraryDialog.EXCLUDED_FILES).split(",");

				RefactoringProject refactoring = new RefactoringProject(libHeaders, excludedFiles, null, null, null);
				refactoring.analyseOnly(project);

				
				MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
						   "Analysis completed", 
						   	String.format("Analysing project %s completed successfully.", project.getName()));
			}
			else 
				throw new NullPointerException("Project " + project.getName() + " is not C/C++");
		} 
		catch (NullPointerException | CoreException e) {
			MessageUtility.writeToConsole("Console", e.getMessage());
			MessageUtility.showMessage(shell, MessageDialog.ERROR, 
						   "Analysis error", 
						   	String.format("Something went wrong with analysing project %s. Please check the log.", project.getName()));
			e.printStackTrace();
		}
		return null;
	}

}
