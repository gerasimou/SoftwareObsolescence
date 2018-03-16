package org.spg.refactoring.handlers;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.spg.refactoring.ProjectModelGenerator;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.ProjectAnalyserDialog;
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

public class ModelGeneratorHandler extends AbstractRefactorerHandler {
	/** Dialog Messages*/
	
	
	public ModelGeneratorHandler() {
		super();
		dialog 			= new ProjectAnalyserDialog();
		this.title 		= "Generating model for project";
		this.message	= "Model generation will begin now, OK?";
	}


	@Override
	protected void executeRefactoringTask(IProject project, File analysisDir, StringProperties properties){
		//get library dialogue properties
//		String[] libHeaders       	= properties.getProperty(ProjectAnalyserDialog.LIB_HEADERS).split(",");
//		String[] excludedFiles		= properties.getProperty(ProjectAnalyserDialog.EXCLUDED_FILES).split(",");

//		RefactoringProject refactoring = new RefactoringProject(libHeaders, excludedFiles, null, null, null);
//		refactoring.analyseOnly(project, analysisDir);	
		
		ProjectModelGenerator modelGenerator = new ProjectModelGenerator();
		modelGenerator.generateProjectModel(project);
		
		MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
				   "Model generation completed", 
				   	String.format("Generating model for project %s completed successfully.", project.getName()));

	}

}
