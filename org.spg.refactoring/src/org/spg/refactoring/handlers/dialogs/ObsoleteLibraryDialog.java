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
	
	private Label headerLabel;
	private Label exclusionLabel;
	
	private Text headerText;
	private Text exclusionText;
	
	public final static String LIB_HEADERS 	  = "old_header";
	public final static String EXCLUDED_FILES = "old_namespace";	
	
	private String[] libHeaders;
	private String[] excludedFiles;
	
	private String path;
	
	public ObsoleteLibraryDialog(String path) {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		this.path = path;
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
		headerText.setText("tinyxml2.h");
		exclusionText.setText("tinyxml2.cpp");
		
		control.layout();
		control.pack();
		
		return control;
	}
	
	
	protected void createGroups(Composite parent) {
//		createExistingProjectGroup(parent);
		createSelectionGroup(parent);
		createExclusionGroup(parent);
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
						"Library files in the workspace", "Select a header file", "", null);
				if (file != null){
					oldHeaderText.setText(file);
				}
			}
		});
	}
	
	
	protected void createSelectionGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Library details", 3);
		
		headerLabel = new Label(groupContent, SWT.NONE);
		headerLabel.setText("Header files");

		headerText = new Text(groupContent, SWT.BORDER);
		headerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button selectBtn = new Button(groupContent, SWT.NONE); 
		selectBtn.setText("Select..."); 
		selectBtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String[] extensions	= new String[] {"*.h"};
				String[] names 		= new String[] {"Header"};
				FilesSelection fileSelection = new FilesSelection(path, extensions, names);
				libHeaders = fileSelection.getSelectedFiles();
				if (libHeaders!= null && libHeaders.length > 0)
					headerText.setText(String.join(",",libHeaders));
			}
		});
	}
	
	
	protected void createExclusionGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Files that  should not be parsed", 3);
		
		exclusionLabel = new Label(groupContent, SWT.NONE);
		exclusionLabel.setText("Excluded files");

		exclusionText = new Text(groupContent, SWT.BORDER);
		exclusionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button selectBtn = new Button(groupContent, SWT.NONE); 
		selectBtn.setText("Select..."); 
		selectBtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String[] extensions	= new String[] {"*.cpp", "*.*"};
				String[] names 		= new String[] {"cpp", "All files"};
				FilesSelection fileSelection = new FilesSelection(path, extensions, names);
				excludedFiles = fileSelection.getSelectedFiles();
				if (excludedFiles!= null && excludedFiles.length > 0)
					exclusionText.setText(String.join(",",excludedFiles));
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
		oldHeaderText.setText(properties.getProperty(LIB_HEADERS));
		oldNamespaceText.setText(properties.getProperty(EXCLUDED_FILES));
	}
	
	protected void storeProperties() {
		properties = new StringProperties();
		properties.put(LIB_HEADERS, 	String.join(",", libHeaders));
		properties.put(EXCLUDED_FILES, 	String.join(",", excludedFiles));
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