package org.spg.refactoring.handlers.dialogs;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spg.refactoring.RefactoringProject;

public class FilesSelectionDialog{
    private Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    private FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);
    
    private StringBuffer sb = new StringBuffer();
    private String selectedFilesFullPath[];
  
    private String fileFilterPath;
    
	/** Logger instance*/
	Logger LOG = LoggerFactory.getLogger (FilesSelectionDialog.class);

    
    public FilesSelectionDialog(String path, String[] filterExtensions, String[] filterNames) {
    	this.fileFilterPath 	= path;
    	
    	fileDialog.setText("Please select the files and click OK");
    	fileDialog.setFilterPath(fileFilterPath);
//    	System.out.println(fileDialog.getFilterPath());
    	fileDialog.setFilterExtensions(filterExtensions);
    	fileDialog.setFilterNames(filterNames);
    	
        processDialog();
    }
    
    
    private void processDialog(){
        String firstFile = fileDialog.open();

        if (firstFile != null) {
            fileFilterPath 	= fileDialog.getFilterPath();
            String selectedFiles[] = fileDialog.getFileNames();
            selectedFilesFullPath = new String[selectedFiles.length];
            
            for (int i = 0; i < selectedFiles.length; i++) {
                File file = new File(selectedFiles[i]);
                if (file.exists()){
                	sb = sb.append(selectedFiles[i] + " \n");
                	selectedFilesFullPath[i] = selectedFiles[i];
                }
                else{
                    sb = sb.append(fileFilterPath + File.separator+ selectedFiles[i] + " \n");
                	selectedFilesFullPath[i] = fileFilterPath + File.separator+ selectedFiles[i];
                }
            }
        }
        LOG.info("\nSelected files:\n" + sb);
    }
    
    
    protected String[] getSelectedFiles(){
    	return this.selectedFilesFullPath;
    }

    
    public static void main(String[] args) {
    	String[] filterExtensions = new String[] {"*.*"};
    	String[] filterNames	  = new String[] { "All files"};
        new FilesSelectionDialog("/Users/sgerasimou/Documents/Git/MBAC/SACM-UML-Profile", filterExtensions, filterNames);
    }
}