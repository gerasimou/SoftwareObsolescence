/*******************************************************************************
 * Copyright (c) 2016 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Simos Gerasimou - initial API and implementation
 ******************************************************************************/

package org.spg.refactoring.handlers.dialogs;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class RefactoringProjectDialog extends AbstractRefactorerDialog{

	private Label newProjectLabel;
	private Label newLibraryLabel;
	private Label newNamespaceLabel;

	private Text newProjectText;
	private Text newLibraryText;
	private Text newNamespaceText;
	
	public final static String NEW_PROJECT   = "new_project";
	public final static String NEW_LIBRARY   = "new_library";
	public final static String NEW_NAMESPACE = "new_namespace";	

	
	
	public RefactoringProjectDialog() {
		super();
		title 	= "Project refactoring configuration";
		message = "Please provide the necessary information for refactoring the project"; 

	}
	
	protected void createGroups(Composite parent) {
		super.createGroups(parent);
		createNewProjectGroup(parent);
	}
	
	protected void createNewProjectGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Refactored project details", 2);
		
		newProjectLabel = new Label(groupContent, SWT.NONE);
		newProjectLabel.setText("Project name");
		
		newProjectText = new Text(groupContent, SWT.BORDER);
		newProjectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		newLibraryLabel = new Label(groupContent, SWT.NONE);
		newLibraryLabel.setText("Library name");
		
		newLibraryText = new Text(groupContent, SWT.BORDER);
		newLibraryText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		newNamespaceLabel = new Label(groupContent, SWT.NONE);
		newNamespaceLabel.setText("Namespace");
		
		newNamespaceText = new Text(groupContent, SWT.BORDER);
		newNamespaceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	
		groupContent.layout();
		groupContent.pack();
	}
	
	
	public void create (String projectName, String projectPath){
		super.create(projectName, projectPath);
		
		//preconfigured details
		newProjectText.setText(projectName + "Refactored");
		newNamespaceText.setText("myNewLib");
		newLibraryText.setText("myNewLib");
	}
	
	
	protected void loadProperties() {
		if (properties == null) return;
		super.loadProperties();
		newProjectText.setText(properties.getProperty(NEW_PROJECT));
		newLibraryText.setText(properties.getProperty(NEW_LIBRARY));
		newNamespaceText.setText(properties.getProperty(NEW_NAMESPACE));
	}
	
	protected void storeProperties() {
		super.storeProperties();
		if (!newProjectText.getText().isEmpty())
			properties.put(NEW_PROJECT, newProjectText.getText().replaceAll("\\s",""));
		if (!newLibraryText.getText().isEmpty())
			properties.put(NEW_LIBRARY, newLibraryText.getText().replaceAll("\\s",""));
		if (!newNamespaceText.getText().isEmpty())
			properties.put(NEW_NAMESPACE, newNamespaceText.getText().replaceAll("\\s",""));
	}	
}