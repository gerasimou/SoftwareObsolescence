package org.spg.refactoring.handlers;

import java.io.File;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.AbstractRefactorerDialog;
import org.spg.refactoring.handlers.dialogs.ProjectAnalyserDialog;
import org.spg.refactoring.handlers.utilities.SelectionUtility;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

public abstract class AbstractRefactorerHandler extends AbstractHandler {
	/** Shell handler*/
	protected Shell shell = null;

	/** Library dialog*/
	AbstractRefactorerDialog dialog;
	
	/** Dialog Messages*/
	protected String title;
	protected String message;

	
	public AbstractRefactorerHandler() {
		dialog = new ProjectAnalyserDialog();
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
				dialog.create(project.getName(), project.getLocation().toOSString());
				int reply = dialog.open();
				if (reply != TitleAreaDialog.OK)
					return null;		

				boolean OK = MessageUtility.showMessage(shell, MessageDialog.CONFIRM, title, 
						message);		
				if (!OK)
					return null;
				
				//create directory for analysis results
				File analysisDir = new File(project.getLocationURI().getPath().toString() + File.separator + "SMILO");
				if (!analysisDir.exists()){
					boolean result = analysisDir.mkdir();
					if (!result)
						MessageUtility.showMessage(shell, MessageDialog.ERROR, "Creating project analysis directory", 
								"There was something wrong with creating directory ProjectAnalysis. Please investigate!");
				}
				
				
				executeRefactoringTask(project, analysisDir, dialog.getProperties());				
			}
			else 
				throw new NullPointerException("Project " + project.getName() + " is not C/C++");
		} 
		catch (Exception e) {
			MessageUtility.writeToConsole("Console", e.getMessage());
			MessageUtility.showMessage(shell, MessageDialog.ERROR, 
									   "Unexpected Project Nature", 
									   	String.format("Expected a C/C++ Project but got a %s instead.\nProcessing Terminated.", project.getName()));
			e.printStackTrace();
		}
		return null;
	}
	
	
	protected abstract void executeRefactoringTask(IProject project, File analysisDir, StringProperties properties) throws Exception;

}
