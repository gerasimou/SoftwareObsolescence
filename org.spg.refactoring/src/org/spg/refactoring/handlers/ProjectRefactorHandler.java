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
package org.spg.refactoring.handlers;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.RefactoringProjectDialog;
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

public class ProjectRefactorHandler extends AbstractRefactorerHandler {
	public ProjectRefactorHandler() {
		dialog = new RefactoringProjectDialog();
		this.title 		= "Refactor project";
		this.message	= "Do you want to refactor the selected project?";
	}


	@Override
	protected void executeRefactoringTask(IProject project, File analysisDir, StringProperties properties) throws Exception {
		//get library dialogue properties
		String[] libHeaders       	= properties.getProperty(RefactoringProjectDialog.LIB_HEADERS).split(",");
		String[] excludedFiles		= properties.getProperty(RefactoringProjectDialog.EXCLUDED_FILES).split(",");
		String newProject			= properties.getProperty(RefactoringProjectDialog.NEW_PROJECT);
		String newLibrary			= properties.getProperty(RefactoringProjectDialog.NEW_LIBRARY);
		String newNamespace			= properties.getProperty(RefactoringProjectDialog.NEW_NAMESPACE);
		
		System.out.println("Library headers:\t" + Arrays.toString(libHeaders));
		System.out.println("Excluded files:\t"  + Arrays.toString(excludedFiles));
		System.out.println("New project:\t"     + newProject);
		System.out.println("New library:\t"     + newLibrary);
		System.out.println("New namespace:\t"     + newNamespace);
		
		RefactoringProject refactoring = new RefactoringProject(libHeaders, excludedFiles, newProject, newLibrary, newNamespace);
		boolean refactorOK = refactoring.refactor(project);
		
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

}
