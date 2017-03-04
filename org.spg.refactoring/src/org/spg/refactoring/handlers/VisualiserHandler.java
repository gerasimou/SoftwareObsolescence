package org.spg.refactoring.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.ProjectVisualiser;
import org.spg.refactoring.handlers.utilities.SelectionUtility;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class VisualiserHandler extends AbstractHandler {
	
	/** Process for server.js*/
	Process serverProcess;
	
	/** Server pid*/
	int serverPid;
	
	/**JSCity details */
	final String path 	 			= "/Users/sgerasimou/Documents/Git/ModernSoftware/JSCity/js/";
	final String jsonPath	 		= "/Users/sgerasimou/Documents/Git/ModernSoftware/JSCity/js/backend/";
	final String server 	 		= "server.js";
	final String MySQL				= "/usr/local/bin/mysql.server";
	final String NODE				= "/usr/local/bin/node";
	final String GENERATOR_SCRIPT	= "generatorPromises9.js";

	
	
	public VisualiserHandler() {
//		this.setBaseEnabled(true);
		serverProcess = null;
		serverPid	  = -1;
	}

	
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
									"Showing City",
									"Showing JSCity.");
		
		
		IProject project = null; 
		try{
			project = SelectionUtility.getSelectedProject();
			//check if the project is a C project
			ICProject cproject = CdtUtilities.getICProject(project);

			if (cproject != null){

				String[] oldHeader       	= new String[]{"tinyxml.cpp", "tinyxml.h"}; 
//				String oldNamespace			= "tinyxml";
//				ProjectAnalyserNew analyser = new ProjectAnalyserNew(project, oldHeader, oldNamespace);
//				analyser.analyseProject();
				RefactoringProject refactoring = new RefactoringProject(oldHeader, null, null, null, null);
				refactoring.analyseOnly(project);
				Collection<String> tusUsing = refactoring.getTUsUsingLib();

				
				ProjectVisualiser vis = new ProjectVisualiser();
				String jsonFile = vis.run(project, jsonPath, tusUsing);
				
				//TODO
				if (jsonFile!=null){
					
					startMySQLDatabase();
					
					runGeneratorScript(jsonFile);

					startJSCityServer();
					
					//show browser
					int style = IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.STATUS;
					IWebBrowser browser = WorkbenchBrowserSupport.getInstance().createBrowser(style, "MyBrowserID", "MyBrowserName", "MyBrowser Tooltip");
					browser.openURL(new URL("http://localhost:8888/"));
				}
			}	
		} 
		catch (NullPointerException | CoreException | MalformedURLException e) {
			MessageUtility.writeToConsole("Console", e.getMessage());
			MessageUtility.showMessage(shell, MessageDialog.ERROR, 
									   "Unexpected Project Nature", 
									   	String.format("Expected a C/C++ Project but got a %s instead.\nProcessing Terminated.", project.getName()));
			e.printStackTrace();
		}
		return null;
	}
	
	
	private void startMySQLDatabase(){
		try {
			//check if mySQL is already running
			if (isMySQLAlive())
				return;
			
			String[] nodeCommand = {MySQL, "start"};
			ProcessBuilder pb = new ProcessBuilder(nodeCommand);
			pb.start();
			System.out.println("MySQL database started.");
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

	}
	
	private void runGeneratorScript(String jsonFile){
		try {
			File dir = new File(jsonPath);
			if (!dir.exists())
				throw new FileNotFoundException(String.format("Directory %s not found", dir));
			
			File file = new File(jsonPath + jsonFile);
			if (!file.exists())
				throw new FileNotFoundException(String.format("File %s not found", file));
			
			String[] nodeCommand = {NODE, jsonPath + GENERATOR_SCRIPT, "-f", jsonPath + jsonFile};
			ProcessBuilder pb = new ProcessBuilder(nodeCommand);
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			Process p = pb.start();
			p.waitFor();
			System.out.println("City added to database.");
		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Start server.sh
	 */
	private void startJSCityServer(){
		try {
			File file = new File(path + server);
			
			if (!file.exists())
				throw new FileNotFoundException("server.js not found in directory " + path);
			
			//check if server.js is already running
			if (isServerAlive())
				return;
			
			String[] nodeCommand = {"/usr/local/bin/node", path + server };
			ProcessBuilder pb = new ProcessBuilder(nodeCommand);
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			serverProcess = pb.start();
			
			if(serverProcess.getClass().getName().equals("java.lang.UNIXProcess")) {
			  /* get the PID on unix/linux systems */
			  try {
			    Field f = serverProcess.getClass().getDeclaredField("pid");
			    f.setAccessible(true);
			    System.out.println(f.getInt(serverProcess));
			    serverPid = f.getInt(serverProcess);
			  } 
			  catch (Throwable e) {
			  }	
			}
//			p.waitFor();
		} 
		catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	private boolean isServerAlive() throws IOException, InterruptedException{
		String comStr	 	=  "ps aux | grep " + server +" | grep -v grep | awk '{print $2}'";//get only pids
		String[] command   	= { "/bin/bash", "-c", comStr };
		boolean serverAlive 	= false;
		
	    StringBuffer output	= new StringBuffer();
		ProcessBuilder pb 	= new ProcessBuilder(command);
		Process p 			= pb.start();
		p.waitFor();
		
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line 		  = "";           
	    while ((line = reader.readLine())!= null) {
	        output.append(line + "\n");
	        if (Integer.parseInt(line.trim()) == serverPid)
	        	serverAlive = true;
	    }

		return serverAlive;
	}
	
	
	private boolean isMySQLAlive() throws IOException, InterruptedException{
		String comStr	 	=  "ps aux | grep mysql | grep -v grep | awk '{print $11}'";//get only pids
		String[] command   	= { "/bin/bash", "-c", comStr };
		boolean mySQLAlive 	= false;
		
	    StringBuffer output	= new StringBuffer();
		ProcessBuilder pb 	= new ProcessBuilder(command);
		Process p 			= pb.start();
		p.waitFor();
		
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	    String line 		  = "";           
	    while ((line = reader.readLine())!= null) {
	        output.append(line + "\n");
	        if (line.contains("mysql"))
	        	mySQLAlive = true;
	    }
	    
		return mySQLAlive;
	}
}
