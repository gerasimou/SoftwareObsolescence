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


import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


public class NewLibraryDialog extends TitleAreaDialog{

	private StringProperties properties;	
	
	private Label newProjectLabel;
	private Label newLibraryLabel;
	private Label newNamespaceLabel;

	private Text newProjectText;
	private Text newLibraryText;
	private Text newNamespaceText;
	
	public final static String NEW_PROJECT   = "new_project";
	public final static String NEW_LIBRARY   = "new_library";
	public final static String NEW_NAMESPACE = "new_namespace";
	
	
	public NewLibraryDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite superControl = (Composite) super.createDialogArea(parent);
		
		
		this.setTitle("New library configuration");
		this.setMessage("Please provide the details for the new library");
//		this.getShell().setText("New library details");
		
		Composite control = new Composite(superControl, SWT.FILL);
		control.setLayout(new GridLayout(1,true));
		control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createGroups(parent);
		
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, "org.eclipse.epsilon.help.emc_dialogs");
		
		loadProperties();

		//preconfigured details
		newProjectText.setText("XMLExampleNew");
		newNamespaceText.setText("myNewLib");
		newLibraryText.setText("myNewLib");
		
		control.layout();
		control.pack();
		
		return control;
	}
	
	
	protected void createGroups(Composite parent) {
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
	
	protected static Composite createGroupContainer(Composite parent, String text, int columns) {
		final Group group = new Group(parent, SWT.FILL);
		
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText(text);
		group.setLayout(new GridLayout(1,false));
		
		final Composite groupContent = new Composite(group, SWT.FILL);
		groupContent.setLayout(new GridLayout(columns, false));
		groupContent.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		return groupContent;
	}
	
	protected void loadProperties() {
		if (properties == null) return;
		newProjectText.setText(properties.getProperty(NEW_PROJECT));
		newLibraryText.setText(properties.getProperty(NEW_LIBRARY));
		newNamespaceText.setText(properties.getProperty(NEW_NAMESPACE));
	}
	
	protected void storeProperties() {
		properties = new StringProperties();
		properties.put(NEW_PROJECT, newProjectText.getText().replaceAll("\\s",""));
		properties.put(NEW_LIBRARY, newLibraryText.getText().replaceAll("\\s",""));
		properties.put(NEW_NAMESPACE, newNamespaceText.getText().replaceAll("\\s",""));
	}

	
	@Override
	protected void okPressed() {
		storeProperties();
		super.okPressed();
	}
	
	@Override
	protected void setShellStyle(int newShellStyle) {
		   super.setShellStyle(newShellStyle | SWT.RESIZE);
	}
	
	public StringProperties getProperties(){
		return properties;
	}
}