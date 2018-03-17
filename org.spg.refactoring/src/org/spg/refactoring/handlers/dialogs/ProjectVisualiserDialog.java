package org.spg.refactoring.handlers.dialogs;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.utilities.MessageUtility;

public class ProjectVisualiserDialog extends AbstractRefactorerDialog {

	private Label mysqlLabel;
	private Label nodeLabel;
	
	private Text mysqlText;
	private Text nodeText;
	
	public final static String MYSQL		  = "mysql";
	public final static String NODE			  = "node";
	
	private String mysql;
	private String node;
	
	
	public ProjectVisualiserDialog() {
		super();
		title 	= "Visualisation configuration";
		message = "Please provide the details for visualisation"; 
	}
	
	protected void createGroups(Composite parent) {
		super.createGroups(parent);
		createCityGroup(parent);
	}
	
	protected void createCityGroup(Composite parent) {
		final Composite groupContent = createGroupContainer(parent, "JSCity details", 3);
		
		mysqlLabel = new Label(groupContent, SWT.NONE);
		mysqlLabel.setText("MySQL path");

		mysqlText = new Text(groupContent, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		mysqlText.setLayoutData(new GridData(GridData.FILL_BOTH));
		mysqlText.setEditable(true);

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
		nodeText.setEditable(true);

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
	
	
	protected void loadProperties() {
		if (properties == null) return;
		super.loadProperties();
		mysqlText.setText(properties.getProperty(MYSQL));
		nodeText.setText(properties.getProperty(NODE));
	}
	
	protected void storeProperties() {
		super.storeProperties();
		if (mysql != null)
			properties.put(MYSQL, 			String.join(",", mysql));
		if (node != null)
			properties.put(NODE, 			String.join(",", node));			
	}
}