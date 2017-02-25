package org.spg.refactoring;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementVisitor;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.CoreException;

public class LibraryAnalyser {

	/** Set with the top elements in an obsolete library: 
	 * e.g., namespace, class, structs, unions, global variables, methods not in classes etc*/
	private Set<ICElement> libCElements;
	
	public LibraryAnalyser() {
		this.libCElements		= new HashSet<ICElement>();
	}
	
	
	protected void analyseLibrary(HashMap<ITranslationUnit, IASTTranslationUnit> libASTCache){
		try {
			//generate CElements Set
			generateCElementsSet(libASTCache.keySet());
			
			generateASTElementsSet(libASTCache);
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}
	
	
	private void generateCElementsSet(Collection<ITranslationUnit> tuSet) throws CoreException{
		//check if the headers have namespace
		for (ITranslationUnit tu : tuSet){
			if (tu.isHeaderUnit() ){
				tu.accept(new ICElementVisitor() {
					@Override
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
							System.out.println(element.getElementName() +"\t"+ element.getClass().getSimpleName() +"\t"+ element.getElementType());
							libCElements.add(element);
							return false;
						} 
						else if (element.getElementType() == ICElement.C_NAMESPACE){
							System.out.println(element.getElementName() +"\t"+ element.getClass().getSimpleName() +"\t"+ element.getElementType());
							RefactoringProject.OLD_NAMESPACES.add(element.getElementName());
							libCElements.add(element);
							return false;
						}
						return true;
					}
				});
			}
		}		
	}
	
	
	
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


		protected NameFinderASTVisitor(){
			shouldVisitNamespaces				= true;
			shouldVisitDeclarations 			= true;
//			shouldVisitNames 					= true;
//			shouldVisitExpressions				= true;
//			shouldVisitParameterDeclarations	= true;
		}
		
		@Override
		public int visit(ICPPASTNamespaceDefinition namespaceDefinition) {
			Class c = namespaceDefinition.getClass();
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
					Class c = declaration.getClass();
					System.out.println(((IASTCompositeTypeSpecifier) declSpecifier).getName().toString());
					System.out.println(c);
					return PROCESS_SKIP;					
				}
				else if (declSpecifier instanceof IASTEnumerationSpecifier){
					Class c = declaration.getClass();
					System.out.println(((IASTEnumerationSpecifier) declSpecifier).getName().toString());
					System.out.println(c);
					return PROCESS_SKIP;										
				}
			}
			return PROCESS_CONTINUE;
		}



	}

}
