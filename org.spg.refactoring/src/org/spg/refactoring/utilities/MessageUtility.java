package org.spg.refactoring.utilities;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class MessageUtility {

	
	protected static MessageConsole findConsole (String consoleName){
		ConsolePlugin consolePlugin 	= ConsolePlugin.getDefault();
		IConsoleManager consoleManager	= consolePlugin.getConsoleManager();
		IConsole existingConsoles[]		= consoleManager.getConsoles();
		//try to find an existing console
		for (int i=0; i<existingConsoles.length; i++){
			if (consoleName.equals(existingConsoles[i].getName()))
				return (MessageConsole) existingConsoles[i];
		}
		
		//if it does not exist, create a new one
		MessageConsole newConsole = new MessageConsole(consoleName, null);
		consoleManager.addConsoles(new IConsole[]{newConsole});
		return newConsole;
	}
	
	
	public static void writeToConsole(String consoleName, String message){
		MessageConsole console 		= findConsole(consoleName);
		MessageConsoleStream out	= console.newMessageStream();
		out.println(message);		
	}
	
	
	public static boolean  showMessage(Shell shell, int kind, String title, String message){
		return MessageDialog.open(kind, shell, title, message, SWT.NONE);  
	}
	
}
