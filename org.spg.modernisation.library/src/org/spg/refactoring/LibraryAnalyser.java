/*******************************************************************************
 * Copyright (c) 2017 University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Simos Gerasimou - initial API and implementation
 ******************************************************************************/
package org.spg.refactoring;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.swing.text.Utilities;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.IFunctionDeclaration;
import org.eclipse.cdt.core.model.IMethodDeclaration;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.spg.refactoring.utilities.MessageUtility;
import org.spg.refactoring.utilities.Utility;

//TODO: We can use this class to analyse the APIs and extract function definitions for similarity checking 

public class LibraryAnalyser {

	/** project index */
	protected IIndex projectIndex = null;

	/** Set with the top elements in an obsolete library: 
	 * e.g., namespace, class, structs, unions, global variables, methods not in classes etc*/
	private Set<ICElement> libCElements;
	

	
	public LibraryAnalyser() {
		this.libCElements		= new HashSet<ICElement>();
	}
	
	
	public void analyseLibrary(ITranslationUnit tu, File analysisDir){
		try {
			StringBuilder str = new StringBuilder();
			
			tu.accept(new ICElementVisitor() {
				@Override
				//if a CElement of those given below is reached, do not parse its children
				public boolean visit(ICElement element) throws CoreException {
					int elementType = element.getElementType();
					switch (elementType){
						case (ICElement.C_CLASS):
						{							
//							System.out.println(element +"\t"+ element.getClass() +"\t"+ element.getElementType());							
							return true;
						}
						case (ICElement.C_FUNCTION):	
						case (ICElement.C_FUNCTION_DECLARATION):	
						case (ICElement.C_METHOD):
						case (ICElement.C_METHOD_DECLARATION):
						{
							IFunctionDeclaration f = (IFunctionDeclaration)element;
//							System.out.println(element +"\t"+ element.getClass() +"\t"+ element.getElementType());
							if (f instanceof IMethodDeclaration){
								IMethodDeclaration m = (IMethodDeclaration)f;
								str.append(m.getVisibility().toString().toLowerCase() +" ");
								str.append(m.isVirtual() ? "virtual " : "");
							}
							str.append(f.getReturnType() +" "+ f.getSignature() + System.lineSeparator());
							return true;
						}
						case (ICElement.C_VARIABLE):	
						case (ICElement.C_ENUMERATION):
						case (ICElement.C_STRUCT):	
						case (ICElement.C_UNION):	
						case (ICElement.C_VARIABLE_DECLARATION):	
						case (ICElement.C_TEMPLATE_CLASS):	
						case (ICElement.C_TEMPLATE_STRUCT):	
						case (ICElement.C_TEMPLATE_UNION):	
						case (ICElement.C_TEMPLATE_FUNCTION):
						{
//							System.out.println(element +"\t"+ element.getClass() +"\t"+ element.getElementType());							
							return true;
						}	
					}
					return true;
				}
			});
			
			String fileName = analysisDir +  File.separator + tu.getElementName() + "_analysis.txt";
			Utility.exportToFile(fileName, str.toString(), false);
			
		} 
		catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	
	public void analyseLibrary(IIndex index, HashMap<ITranslationUnit, IASTTranslationUnit> libASTCache){
		try {
	 		this.projectIndex = index;
	 		
			//generate CElements Set
			generateCElementsSet(libASTCache.keySet());
						
			System.out.println("\n");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	
	private void generateCElementsSet(Collection<ITranslationUnit> tuSet) throws CoreException{
		MessageUtility.writeToConsole("Console", "Generating CElements sets for selected project");

		//check if the headers have namespace
		for (ITranslationUnit tu : tuSet){
			if (tu.isHeaderUnit() ){
				tu.accept(new ICElementVisitor() {
					@Override
					//if a CElement of those given below is reached, do not parse its children
					public boolean visit(ICElement element) throws CoreException { 
						if (   element.getElementType() == ICElement.C_CLASS
						    || element.getElementType() == ICElement.C_ENUMERATION
				    		|| element.getElementType() == ICElement.C_STRUCT
				    		|| element.getElementType() == ICElement.C_UNION
				    		|| element.getElementType() == ICElement.C_FUNCTION
				    		|| element.getElementType() == ICElement.C_FUNCTION_DECLARATION
				    		|| element.getElementType() == ICElement.C_VARIABLE
				    		|| element.getElementType() == ICElement.C_VARIABLE_DECLARATION
				    		|| element.getElementType() == ICElement.C_TEMPLATE_CLASS
				    		|| element.getElementType() == ICElement.C_TEMPLATE_STRUCT
				    		|| element.getElementType() == ICElement.C_TEMPLATE_UNION
				    		|| element.getElementType() == ICElement.C_TEMPLATE_FUNCTION
							){								
							libCElements.add(element);
							return true;
						} 
						else if (element.getElementType() == ICElement.C_NAMESPACE){
							libCElements.add(element);
							return false;
						}
						return true;
					}
				});
			}
		}		
	}
}
