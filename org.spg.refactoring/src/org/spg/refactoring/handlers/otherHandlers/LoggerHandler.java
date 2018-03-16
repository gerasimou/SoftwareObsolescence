package org.spg.refactoring.handlers.otherHandlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spg.refactoring.utilities.MessageUtility;

import logger.MyPropertyDefiner;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class LoggerHandler extends AbstractHandler {

	private static final Logger LOG = LoggerFactory.getLogger (LoggerHandler.class);

	public LoggerHandler() {
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();


		LOG.debug("Debug log");
		LOG.info("info");
		LOG.error(MyPropertyDefiner.class.getProtectionDomain().getCodeSource().getLocation().getPath());
		
		MessageUtility.showMessage(shell, MessageDialog.INFORMATION, 
									"Refactoring",
									"Refactor project was executed.");
		return null;
	}
}
