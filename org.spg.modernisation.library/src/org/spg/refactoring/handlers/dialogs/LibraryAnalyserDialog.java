/*******************************************************************************
 * Copyright (c) 2016 The University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Simos Gerasimou - initial API and implementation
 ******************************************************************************/

package org.spg.refactoring.handlers.dialogs;

import org.eclipse.swt.widgets.Composite;

public class LibraryAnalyserDialog extends AbstractRefactorerDialog{
	public LibraryAnalyserDialog() {
		super();
		this.title 		= "Obsolete library configuration";
		this.message	= "Please provide the details for the obsolete library";
	}
	
	
	@Override
	protected void createGroups(Composite parent) {
		
	}
}