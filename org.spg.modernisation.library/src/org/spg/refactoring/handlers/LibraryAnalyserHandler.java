package org.spg.refactoring.handlers;

import java.io.File;

import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.LibraryAnalyser;
import org.spg.refactoring.handlers.dialogs.LibraryAnalyserDialog;
import org.spg.refactoring.handlers.utilities.SelectionUtility;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

public class LibraryAnalyserHandler extends AbstractRefactorerHandler {
	/** Dialog Messages*/
	
	
	public LibraryAnalyserHandler() {
		super();
		dialog 			= new LibraryAnalyserDialog();
		this.title 		= "Analysing project";
		this.message	= "Project analysis will begin now, OK?";
	}

	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		IFile file 			= null;
		IProject project	= null;
		try{
			file 	= SelectionUtility.getSelectedFile();
			project = file.getProject();
			//check if the file is a C file
			ICProject cproject = CdtUtilities.getICProject(project);

			if (cproject != null){

				//show library dialog
//				dialog.create(project.getName(), project.getLocation().toOSString());
//				int reply = dialog.open();
//				if (reply != TitleAreaDialog.OK)
//					return null;		

				boolean OK = MessageUtility.showMessage(shell, MessageDialog.CONFIRM, title, 
						message);		
				if (!OK)
					return null;
				
				//create directory for analysis results
				File analysisDir = new File(project.getLocationURI().getPath().toString() 
											+ File.separator + "SMILO" + File.separator + "interface");
				if (!analysisDir.exists()){
					boolean result = analysisDir.mkdirs();
					if (!result)
						MessageUtility.showMessage(shell, MessageDialog.ERROR, "Creating project analysis directory", 
								"There was something wrong with creating directory interface. Please investigate!");
				}
				
				//get library dialogue properties		
				LibraryAnalyser libAnalyser = new LibraryAnalyser();
				ITranslationUnit tu = CoreModelUtil.findTranslationUnit(file);
				libAnalyser.analyseLibrary(tu, analysisDir);
				
				MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
						   "Library Analysis", 
						   	String.format("Analysing project %s completed successfully.", project.getName()));	
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
	

	@Override
	protected void executeRefactoringTask(IProject project, File analysisDir, StringProperties properties){
		//get library dialogue properties		
//		LibraryAnalyser libAnalyser = new LibraryAnalyser();
//		libAnalyser.analyseLibrary();
		
		MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
				   "Library Analysis", 
				   	String.format("Analysing project %s completed successfully.", project.getName()));
	}

}
