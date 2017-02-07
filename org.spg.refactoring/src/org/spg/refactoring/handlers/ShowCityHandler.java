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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.spg.refactoring.utilities.MessageUtility;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ShowCityHandler extends AbstractHandler {
	
	/** Process for server.js*/
	Process serverProcess;
	
	/** Server pid*/
	int serverPid;
	
	/**JSCity details */
	String path 	 = "/Users/sgerasimou/Documents/Git/ModernSoftware/JSCity/js/";
	String server 	 = "server.js";

	
	
	public ShowCityHandler() {
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
		
		int style = IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.STATUS;
		IWebBrowser browser;
		try {
			startJSCityServer();
			
			browser = WorkbenchBrowserSupport.getInstance().createBrowser(style, "MyBrowserID", "MyBrowserName", "MyBrowser Tooltip");
			browser.openURL(new URL("http://localhost:8888/"));
		} catch (PartInitException | MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
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
}
