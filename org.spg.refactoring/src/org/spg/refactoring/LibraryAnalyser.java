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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spg.refactoring.utilities.MessageUtility;

//TODO: We can use this class to analyse the APIs and extract function definitions for similarity checking 

public class LibraryAnalyser {

	/** project index */
	protected IIndex projectIndex = null;

	/** Set with the top elements in an obsolete library: 
	 * e.g., namespace, class, structs, unions, global variables, methods not in classes etc*/
	private Set<ICElement> libCElements;
	
	/** Logger instance*/
	Logger LOG = LoggerFactory.getLogger (LibraryAnalyser.class);

	
	public LibraryAnalyser() {
		this.libCElements		= new HashSet<ICElement>();
	}
	
	
	protected void analyseLibrary(IIndex index, HashMap<ITranslationUnit, IASTTranslationUnit> libASTCache){
		try {
	 		this.projectIndex = index;
	 		
			//generate CElements Set
			generateCElementsSet(libASTCache.keySet());
			
//			generateASTElementsSet(libASTCache);
//			extractLibSuperClasses(libASTCache);
			
			System.out.println("\n");
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	
	private void generateCElementsSet(Collection<ITranslationUnit> tuSet) throws CoreException{
		MessageUtility.writeToConsole("Console", "Generating CElements sets for selected project");
		LOG.info("Generating CElements sets for selected project");

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
							LOG.info(element.getElementName() +"\t"+ element.getClass().getSimpleName() +"\t"+ element.getElementType());
							libCElements.add(element);
							return true;
						} 
						else if (element.getElementType() == ICElement.C_NAMESPACE){
							LOG.info(element.getElementName() +"\t"+ element.getClass().getSimpleName() +"\t"+ element.getElementType());
							RefactoringProject.LIB_NAMESPACES.add(element.getElementName());
							libCElements.add(element);
							return false;
						}
						return true;
					}
				});
			}
		}		
	}
	
	
	@SuppressWarnings("unused")
	private void extractLibSuperClasses(HashMap<ITranslationUnit, IASTTranslationUnit> libASTCache){
		//check if the headers have namespace
		for (ITranslationUnit tu : libASTCache.keySet()){
			if (tu.isHeaderUnit() ){
				IASTTranslationUnit ast = libASTCache.get(tu);
				ast.accept(new ASTVisitor() {
					{
						shouldVisitBaseSpecifiers = true;
					}
					
					@Override
					public int visit(ICPPASTBaseSpecifier baseSpecifier) {
						ICPPASTNameSpecifier name = baseSpecifier.getNameSpecifier();
						IBinding binding = name.resolveBinding();
						if (binding instanceof ICPPClassType){
							try {
								projectIndex.acquireReadLock();
								IIndexName[] defs = projectIndex.findNames(binding, IIndex.FIND_DEFINITIONS);
								for (IIndexName dd : defs){
									if (dd.isDeclaration()){
										String path = dd.getFileLocation().getFileName();
										RefactoringProject.LIB_HEADERS.add(path);
									}
								}
							} catch (InterruptedException | CoreException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						return PROCESS_CONTINUE;
					}
				});
			}
		}	
	}
	
	
	@SuppressWarnings("unused")
	private void generateASTElementsSet(HashMap<ITranslationUnit, IASTTranslationUnit> libASTCache){
		//check if the headers have namespace
		for (ITranslationUnit tu : libASTCache.keySet()){
			if (tu.isHeaderUnit() ){
				IASTTranslationUnit ast = libASTCache.get(tu);
				NameFinderASTVisitor visitor = new NameFinderASTVisitor();
				ast.accept(visitor);
			}
		}
	}
	
	
	
	
	private class NameFinderASTVisitor extends ASTVisitor {
		/** List keeping all important IASTNames**/
		List<Class<?>> nodesList = new ArrayList<Class<?>>();

		protected NameFinderASTVisitor(){
			shouldVisitNamespaces				= true;
			shouldVisitDeclarations 			= true;
//			shouldVisitNames 					= true;
//			shouldVisitExpressions				= true;
//			shouldVisitParameterDeclarations	= true;
		}
		
		@Override
		public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
			Class<? extends ICPPASTNamespaceDefinition> c = namespaceDefinition.getClass();
			System.out.println(namespaceDefinition.getName().toString());
			System.out.println(c);
			return PROCESS_SKIP;
		}

		@Override
		public int visit(IASTDeclaration declaration) {
			if (declaration instanceof IASTSimpleDeclaration){
				IASTDeclSpecifier declSpecifier = ((IASTSimpleDeclaration)declaration).getDeclSpecifier();
				
				//if it is a class, struct, union
				if (declSpecifier instanceof IASTCompositeTypeSpecifier){
					Class<?> c = declSpecifier.getClass();
					System.out.println(((IASTCompositeTypeSpecifier) declSpecifier).getName().toString());
					System.out.println(c);
					nodesList.add(c);
					return PROCESS_SKIP;					
				}
				else if (declSpecifier instanceof IASTEnumerationSpecifier){
					Class<?> c = declSpecifier.getClass();
					System.out.println(((IASTEnumerationSpecifier) declSpecifier).getName().toString());
					System.out.println(c);
					nodesList.add(c);
					return PROCESS_SKIP;										
				}
			}
			return PROCESS_CONTINUE;
		}
	}

}
