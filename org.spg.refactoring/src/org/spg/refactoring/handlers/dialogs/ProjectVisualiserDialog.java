package org.spg.refactoring.handlers.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
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
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

public class ProjectVisualiserDialog extends TitleAreaDialog {

	private StringProperties properties;	

	private Label headerLabel;
	private Label exclusionLabel;
	private Label mysqlLabel;
	private Label nodeLabel;
	
	private Text headerText;
	private Text exclusionText;
	private Text mysqlText;
	private Text nodeText;
	
	public final static String LIB_HEADERS 	  = "old_header";
	public final static String EXCLUDED_FILES = "old_namespace";	
	public final static String MYSQL		  = "mysql";
	public final static String NODE			  = "node";
	
	private String[] libHeaders;
	private String[] excludedFiles;
	private String mysql;
	private String node;
	
	private String path;

	
	public ProjectVisualiserDialog() {
		super(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
	}
	
	
	@Override
	protected Control createDialogArea(Composite parent) {
		
		Composite superControl = (Composite) super.createDialogArea(parent);
		
		
		this.setTitle("Visualisation configuration");
		this.setMessage("Please provide the details for visualisation");
		
		Composite control = new Composite(superControl, SWT.FILL);
		control.setLayout(new GridLayout(1,true));
		control.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createGroups(parent);
				
		loadProperties();
		
		control.layout();
		control.pack();
		
		return control;
	}
	
	
	protected void createGroups(Composite parent) {
		createSelectionGroup(parent);
		createExclusionGroup(parent);
		createCityGroup(parent);
	}

	
	protected void createSelectionGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Library details", 3);
		
		headerLabel = new Label(groupContent, SWT.NONE);
		headerLabel.setText("Header files");

		headerText = new Text(groupContent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		headerText.setLayoutData(new GridData(GridData.FILL_BOTH));
		headerText.setEditable(false);

		final Button selectBtn = new Button(groupContent, SWT.NONE); 
		selectBtn.setText("Select..."); 
		selectBtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String[] extensions	= new String[] {"*.h"};
				String[] names 		= new String[] {"Header"};
				FilesSelectionDialog fileSelection = new FilesSelectionDialog(path, extensions, names);
				libHeaders = fileSelection.getSelectedFiles();
				if (libHeaders!= null && libHeaders.length > 0)
					headerText.setText(String.join(",\n",libHeaders));
			}
		});
	}


	protected void createExclusionGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "Files that  should not be parsed", 3);
		
		exclusionLabel = new Label(groupContent, SWT.NONE);
		exclusionLabel.setText("Excluded files");

		exclusionText = new Text(groupContent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		exclusionText.setLayoutData(new GridData(GridData.FILL_BOTH));
		exclusionText.setEditable(false);

		final Button selectBtn = new Button(groupContent, SWT.NONE); 
		selectBtn.setText("Select..."); 
		selectBtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String[] extensions	= new String[] {"*.cpp", "*.*"};
				String[] names 		= new String[] {"cpp", "All files"};
				FilesSelectionDialog fileSelection = new FilesSelectionDialog(path, extensions, names);
				excludedFiles = fileSelection.getSelectedFiles();
				if (excludedFiles!= null && excludedFiles.length > 0)
					exclusionText.setText(String.join(",\n",excludedFiles));
			}
		});
	}
	
	
	protected void createCityGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "JSCity details", 3);
		
		mysqlLabel = new Label(groupContent, SWT.NONE);
		mysqlLabel.setText("MySQL path");

		mysqlText = new Text(groupContent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		mysqlText.setLayoutData(new GridData(GridData.FILL_BOTH));
		mysqlText.setEditable(false);

		final Button selectBtn = new Button(groupContent, SWT.NONE); 
		selectBtn.setText("Select..."); 
		selectBtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String[] extensions	= new String[] {"*.*"};
				String[] names 		= new String[] {"All files"};
				FilesSelectionDialog fileSelection = new FilesSelectionDialog(path, extensions, names);
				String[] selectedFiles = fileSelection.getSelectedFiles();
				if (selectedFiles.length > 1)
					MessageUtility.showMessage(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
											  MessageDialog.ERROR, "Selecting MySQL application", 
											  "Multiple applications for MySQL were selected. Please select only the appropriate MySQL application");
				else if (!new File(selectedFiles[0]).canExecute())
						MessageUtility.showMessage(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
								  MessageDialog.ERROR, "Selecting MySQL application", 
								  "The selected MySQL application is not executable.Please select the appropriate MySQL application");
						
				else{
					mysql = selectedFiles[0];
					mysqlText.setText(mysql);
				}
			}
		});

		nodeLabel = new Label(groupContent, SWT.NONE);
		nodeLabel.setText("Node path");

		nodeText = new Text(groupContent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		nodeText.setLayoutData(new GridData(GridData.FILL_BOTH));
		nodeText.setEditable(false);

		final Button selectNodeBtn = new Button(groupContent, SWT.NONE); 
		selectNodeBtn.setText("Select..."); 
		selectNodeBtn.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				String[] extensions	= new String[] {"*.*"};
				String[] names 		= new String[] {"All files"};
				FilesSelectionDialog fileSelection = new FilesSelectionDialog(path, extensions, names);

				String[] selectedFiles = fileSelection.getSelectedFiles();
				if (selectedFiles.length > 1)
					MessageUtility.showMessage(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
											  MessageDialog.ERROR, "Selecting node application", 
							  				 "Multiple applications for Node were selected. Please select only the appropriate Node application");
				else if (!new File(selectedFiles[0]).canExecute())
					MessageUtility.showMessage(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
							  MessageDialog.ERROR, "Selecting Node application",  
							  "The selected Node application is not executable.Please select the appropriate Node application");					
				else{
					node = selectedFiles[0]; 
					nodeText.setText(node);
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


	public void create (String projectName, String projectPath){
		this.path = projectPath;

		super.create();		
	}
	
	
	protected void loadProperties() {
		if (properties == null) return;
		//Null is captured if StringProperties class
		headerText.setText(properties.getProperty(LIB_HEADERS));
		exclusionText.setText(properties.getProperty(EXCLUDED_FILES));
		mysqlText.setText(properties.getProperty(MYSQL));
		nodeText.setText(properties.getProperty(NODE));
	}
	
	protected void storeProperties() {
		properties = new StringProperties();
		if (libHeaders != null)
			properties.put(LIB_HEADERS, 	String.join(",", libHeaders));
		if (excludedFiles != null)
			properties.put(EXCLUDED_FILES, 	String.join(",", excludedFiles));
		if (mysql != null)
			properties.put(MYSQL, 			String.join(",", mysql));
		if (node != null)
			properties.put(NODE, 			String.join(",", node));			
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