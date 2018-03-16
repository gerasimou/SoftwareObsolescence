package org.spg.refactoring.handlers.otherHandlers;

import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.spg.refactoring.handlers.utilities.SelectionUtility;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class PrintMessageHandler extends AbstractHandler {
	/** Shell handler*/
	private Shell shell = null;

	public PrintMessageHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
//		Bundle bundle = FrameworkUtil.getBundle(getClass());
//		IPath stateLoc = Platform.getStateLocation(bundle);
		
		IProject project = null;
		try{
			project = SelectionUtility.getSelectedProject();
			//check if the project is a C project
			ICProject cproject = CdtUtilities.getICProject(project);

			if (cproject != null){
				String p1 = cproject.getLocationURI().getPath().toString();
				String p2 = cproject.getPath().toOSString();
				String p3 = PrintMessageHandler.class.getProtectionDomain().getCodeSource().getLocation().getFile();

				MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
												"Printing",	"A simple message.\n" + p1 +"\n"+ p2 +"\n"+ p3);
			}	
		} 
		catch (NullPointerException | CoreException e) {
			MessageUtility.writeToConsole("Console", e.getMessage());
			MessageUtility.showMessage(shell, MessageDialog.ERROR, 
									   "Unexpected Project Nature", 
									   	String.format("Expected a C/C++ Project but got a %s instead.\nProcessing Terminated.", project.getName()));
			e.printStackTrace();
		}
		return null;
	
	}
}
