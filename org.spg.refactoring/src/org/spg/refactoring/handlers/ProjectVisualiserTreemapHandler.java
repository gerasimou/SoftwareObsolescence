package org.spg.refactoring.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.epsilon.common.util.FileUtil;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.internal.browser.WorkbenchBrowserSupport;
import org.spg.refactoring.ProjectVisualiserMDE;
import org.spg.refactoring.RefactoringProject;
import org.spg.refactoring.handlers.dialogs.ProjectAnalyserDialog;
import org.spg.refactoring.handlers.dialogs.ProjectVisualiserTreemapDialog;
import org.spg.refactoring.utilities.Utility;
import org.spg.refactoring.utilities.fromEpsilon.StringProperties;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ProjectVisualiserTreemapHandler extends AbstractRefactorerHandler{
	
	/**JSCity details */
	final String slash				= File.separator;
	private final String thisClass			= ProjectVisualiserTreemapHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile();
	private final String path 	 			= thisClass + "JSCity" + slash + "js" + slash; 
//			"/Users/sgerasimou/Documents/Git/ModernSoftware/JSCity/js/";
	private final String jsonPath	 		= path + "backend" + slash; 


	
	public ProjectVisualiserTreemapHandler() {
		super();
		dialog 			= new ProjectVisualiserTreemapDialog();
		this.title 		= "Showing TreeMap";
		this.message	= "Project analysis will begin now, OK?";
	}

	
	protected void executeRefactoringTask(IProject project, File analysisDir, StringProperties properties) throws PartInitException, IOException{
		//get library dialogue properties
		String[] libHeaders       	= properties.getProperty(ProjectAnalyserDialog.LIB_HEADERS).split(",");
		String[] excludedFiles		= properties.getProperty(ProjectAnalyserDialog.EXCLUDED_FILES).split(",");
		
		//analyse project
		RefactoringProject refactoring = new RefactoringProject(libHeaders, excludedFiles, null, null, null);
		refactoring.analyseOnly(project, analysisDir);

		//get TUs using legacy library
		Map<String,String> tusUsingMap = refactoring.getTUsUsingMapAsString();
		
		//run visualiser
//		ProjectVisualiser vis = new ProjectVisualiser();
//		String JSONfileFullPath = vis.run(project, analysisDir.getAbsolutePath(), tusUsingMap);

		//run MDE-based visualiser
		ProjectVisualiserMDE vis 	= new ProjectVisualiserMDE();
		String JSONfile				= vis.runJSONGenerator(project, analysisDir.getAbsolutePath(), tusUsingMap, VisualisationMetaphor.TREEMAP);

		//copy treemap html to the appropriate directory
		String treeMapDir = analysisDir.getAbsolutePath() + File.separator + "TreeMap";
		FileUtil.copy(new File("/Users/sgerasimou/Documents/Git/ModernSoftware/org.spg.refactoring/TreeMap"), 
				   new File(treeMapDir));

		//modify treemap js 
		String treemapJSfile 	=  treeMapDir + File.separator + "treemap.js";
		String js 				= Utility.readFile(treemapJSfile).replace("JSONFILE", JSONfile);
		Utility.exportToFile(treemapJSfile, js, false);

		//If a JSON file is generated, start mysql and add the ciy to the database
		if (JSONfile!=null){			
			//show browser
			int style = IWorkbenchBrowserSupport.AS_EDITOR | IWorkbenchBrowserSupport.LOCATION_BAR | IWorkbenchBrowserSupport.STATUS;
			IWebBrowser browser = WorkbenchBrowserSupport.getInstance().createBrowser(style, "MyBrowserID", "MyBrowserName", "MyBrowser Tooltip");
			browser.openURL(new URL("file:///" + treeMapDir +File.separator+"index.html"));	
		}	
	}
}
