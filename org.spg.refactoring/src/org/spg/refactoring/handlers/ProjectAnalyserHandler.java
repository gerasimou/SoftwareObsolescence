package org.spg.refactoring.handlers;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.ProjectAnalyserDialog;
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

public class ProjectAnalyserHandler extends AbstractRefactorerHandler {
	/** Dialog Messages*/
	
	
	public ProjectAnalyserHandler() {
		super();
		dialog 			= new ProjectAnalyserDialog();
		this.title 		= "Analysing project";
		this.message	= "Project analysis will begin now, OK?";
	}


	@Override
	protected void executeRefactoringTask(IProject project, File analysisDir, StringProperties properties){
		//get library dialogue properties
		String[] libHeaders       	= properties.getProperty(ProjectAnalyserDialog.LIB_HEADERS).split(",");
		String[] excludedFiles		= properties.getProperty(ProjectAnalyserDialog.EXCLUDED_FILES).split(",");

		RefactoringProject refactoring = new RefactoringProject(libHeaders, excludedFiles, null, null, null);
		refactoring.analyseOnly(project, analysisDir);	
		
		MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
				   "Analysis completed", 
				   	String.format("Analysing project %s completed successfully.", project.getName()));

	}

}
