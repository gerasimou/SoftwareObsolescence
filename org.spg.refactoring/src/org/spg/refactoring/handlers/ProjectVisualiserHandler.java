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
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.spg.refactoring.ProjectVisualiserMDE;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.ProjectAnalyserDialog;
import org.spg.refactoring.handlers.dialogs.ProjectVisualiserDialog;
import org.spg.refactoring.utilities.Utility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ProjectVisualiserHandler extends AbstractRefactorerHandler{
	/** Process for server.js*/
	private Process serverProcess;
	
	/** Server pid*/
	private int serverPid;
	
	/**JSCity details */
	final String slash				= File.separator;
	private final String thisClass			= ProjectVisualiserHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile();
	private final String path 	 			= thisClass + "JSCity" + slash + "js" + slash; 
//			"/Users/sgerasimou/Documents/Git/ModernSoftware/JSCity/js/";
	private final String jsonPath	 		= path + "backend" + slash; 
//			"/Users/sgerasimou/Documents/Git/ModernSoftware/JSCity/js/backend/";
	private final String server 	 		= "server.js";
	private String MySQL; //= "/usr/local/bin/mysql.server";
	private String NODE;  //= "/usr/local/bin/node";
	private final String GENERATOR_SCRIPT	= "generatorPromises9.js";


	
	public ProjectVisualiserHandler() {
		super();
		dialog 			= new ProjectVisualiserDialog();
		this.title 		= "Showing City";
		this.message	= "Project analysis will begin now, OK?";
		serverProcess 	= null;
		serverPid	  	= -1;		
	}

	
	protected void executeRefactoringTask(IProject project, File analysisDir, StringProperties properties) throws PartInitException, MalformedURLException{
		//get library dialogue properties
		String[] libHeaders       	= properties.getProperty(ProjectAnalyserDialog.LIB_HEADERS).split(",");
		String[] excludedFiles		= properties.getProperty(ProjectAnalyserDialog.EXCLUDED_FILES).split(",");
		MySQL						= properties.getProperty(ProjectVisualiserDialog.MYSQL);
		NODE						= properties.getProperty(ProjectVisualiserDialog.NODE);
		
		//analyse project
		RefactoringProject refactoring = new RefactoringProject(libHeaders, excludedFiles, null, null, null);
		refactoring.analyseOnly(project, analysisDir);

		//get TUs using legacy library
		Map<String,String> tusUsingMap = refactoring.getTUsUsingMapAsString();
		
		//run visualiser
//		ProjectVisualiser vis = new ProjectVisualiser();
//		String JSONfileFullPath = vis.run(project, analysisDir.getAbsolutePath(), tusUsingMap);

		//run MDE-based visualiser
		ProjectVisualiserMDE vis = new ProjectVisualiserMDE();
		String JSONfileFullPath = vis.run(project, analysisDir.getAbsolutePath(), tusUsingMap);

		
		//If a JSON file is generated, start mysql and add the ciy to the database
		if (JSONfileFullPath!=null){
			
			startMySQLDatabase();
			
			runGeneratorScript(JSONfileFullPath);

			startJSCityServer();
			
			//show browser
			int style = IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.STATUS;
			IWebBrowser browser = WorkbenchBrowserSupport.getInstance().createBrowser(style, "MyBrowserID", "MyBrowserName", "MyBrowser Tooltip");
			browser.openURL(new URL("http://localhost:8888/"));	
		}
	}

	
	
	/**
	 * Start mysql database
	 */
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
	
	
	private void runGeneratorScript(String JSONfileFullPath){
		try {
			File dir = new File(jsonPath);
			if (!dir.exists())
				throw new FileNotFoundException(String.format("Directory %s not found", dir));
			
			File file = new File(JSONfileFullPath);
			if (!file.exists())
				throw new FileNotFoundException(String.format("File %s not found", file));
			
			String[] nodeCommand = {NODE, jsonPath + GENERATOR_SCRIPT, "-f", JSONfileFullPath};
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
			
			String[] nodeCommand = {NODE, path + server };
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
	
	
	/**
	 * Check if JSCity server is alive
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
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
	
	
	/**
	 * Check if Mysql server is alive
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
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
