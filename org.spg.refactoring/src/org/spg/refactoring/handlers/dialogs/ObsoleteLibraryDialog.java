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


import org.eclipse.epsilon.common.dt.launching.dialogs.BrowseWorkspaceUtil;
import org.eclipse.epsilon.common.util.StringProperties;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;


public class ObsoleteLibraryDialog extends TitleAreaDialog{

	private StringProperties properties;	
	
	private Label oldNamespaceLabel;
	private Label oldHeaderLabel;

	private Text oldNamespaceText;
	private Text oldHeaderText;
	
	public final static String OLD_NAMESPACE = "old_namespace";
	public final static String OLD_HEADER 	  = "old_header";
	
	
	public ObsoleteLibraryDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite superControl = (Composite) super.createDialogArea(parent);
		
		
		this.setTitle("Obsolete library configuration");
		this.setMessage("Please provide the details for the obsolete library");
//		this.getShell().setText("New library details");
		
		Composite control = new Composite(superControl, SWT.FILL);
		control.setLayout(new GridLayout(1,true));
		control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createGroups(parent);
		
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, "org.eclipse.epsilon.help.emc_dialogs");
		
		loadProperties();

		//preconfigured details
		oldNamespaceText.setText("tinyxml2");
		oldHeaderText.setText("tinyxml2.cpp, tinyxml2.h");
		
		control.layout();
		control.pack();
		
		return control;
	}
	
	
	protected void createGroups(Composite parent) {
		createExistingProjectGroup(parent);
	}
	
	protected void createExistingProjectGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Existing project details", 3);
		
		oldNamespaceLabel = new Label(groupContent, SWT.NONE);
		oldNamespaceLabel.setText("Namespace");

		oldNamespaceText = new Text(groupContent, SWT.BORDER);
		oldNamespaceText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(groupContent, SWT.NONE);
		
		oldHeaderLabel = new Label(groupContent, SWT.NONE);
		oldHeaderLabel.setText("Library files (header)");

		oldHeaderText = new Text(groupContent, SWT.BORDER);
		oldHeaderText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Button browseFile = new Button(groupContent, SWT.NONE); 
		browseFile.setText("Browse Workspace..."); 
		browseFile.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String file = BrowseWorkspaceUtil.browseFilePath(getShell(), 
						"CSV files in the workspace", "Select a CSV file", "", null);
				if (file != null){
					oldHeaderText.setText(file);
				}
			}
		});
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
		oldHeaderText.setText(properties.getProperty(OLD_HEADER));
		oldNamespaceText.setText(properties.getProperty(OLD_NAMESPACE));
	}
	
	protected void storeProperties() {
		properties = new StringProperties();
		properties.put(OLD_NAMESPACE, oldNamespaceText.getText().replaceAll("\\s",""));
		properties.put(OLD_HEADER, oldHeaderText.getText().replaceAll("\\s",""));
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