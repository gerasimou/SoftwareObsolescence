package org.spg.refactoring.handlers.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

public class FilesSelection {
    private Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    private FileDialog fileDialog = new FileDialog(shell, SWT.MULTI);
    
    private StringBuffer sb = new StringBuffer();
    private String selectedFiles[];
    
    private String fileFilterPath;
    private String[] filterExtensions;
    private String[] filterNames;

    
    public FilesSelection(String path, String[] filterExtensions, String[] filterNames) {
    	this.fileFilterPath 	= path;
    	this.filterExtensions	= filterExtensions;
    	this.filterNames		= filterNames;
    	
        processDialog();
    }
    
    
    private void processDialog(){
    	fileDialog.setText("Please select the files and click OK");
        fileDialog.setFilterPath(fileFilterPath);
        fileDialog.setFilterExtensions(filterExtensions);
        fileDialog.setFilterNames(filterNames);
        
        String firstFile = fileDialog.open();

        if (firstFile != null) {
            fileFilterPath 	= fileDialog.getFilterPath();
            selectedFiles	= fileDialog.getFileNames();

            for (int i = 0; i < selectedFiles.length; i++) {
                sb = sb.append(selectedFiles[i] + " \n");
            }
        }
        System.out.println(sb);
    }
    
    
    protected String[] getSelectedFiles(){
    	return this.selectedFiles;
    }

    
    public static void main(String[] args) {
    	String[] filterExtensions = new String[] {"*.*"};
    	String[] filterNames	  = new String[] { "All files"};
        new FilesSelection("/Users/sgerasimou/Documents/Git/MBAC/SACM-UML-Profile", filterExtensions, filterNames);
    }
}