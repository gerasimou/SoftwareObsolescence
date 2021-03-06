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

import java.nio.file.NoSuchFileException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStandardFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNameSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTUsingDirective;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPEnumeration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IInclude;
import org.eclipse.cdt.core.model.INamespace;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.IUsing;
import org.eclipse.cdt.internal.core.model.CreateUsingOperation;
import org.eclipse.cdt.internal.core.model.TranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.spg.refactoring.ProjectAnalyser.BindingsSet;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

public class ProjectRefactorer {
	/** project */
	protected ICProject cproject = null;

	/** project index */
	protected IIndex projectIndex = null;
	
	/** */
	RefactoringProject refactoring;

	boolean changed = false;

	
	public ProjectRefactorer(RefactoringProject refProject) {
		this.refactoring = refProject;
	}
	
	
 	/**
 	 * After analysing the selected project, create a new (refactored) project based on the adapter pattern 
 	 * @param classMembersMap
 	 * @throws Exception
 	 */
 	protected void createRefactoredProject(ICProject project, IIndex index, BindingsSet bindingsSet, 
 											Map<String, IASTName> includeDirectivesMap, Map<ICPPClassType, List<ICPPMember>> classMembersMap,
 											HashMap<ITranslationUnit, IASTTranslationUnit> projectASTCache, Collection<ITranslationUnit> tusUsingLib,
 											Collection<IASTPreprocessorMacroDefinition> macrosList) throws Exception{
 		this.projectIndex = index;
 		this.cproject	  = project;	
 		
		
		//create header file
 		System.out.println("\nCreating library header file: " + RefactoringProject.NEW_LIBRARYhpp);
		MessageUtility.writeToConsole("Console", "Creating library header file: " + RefactoringProject.NEW_LIBRARYhpp);
		createHeader(bindingsSet, includeDirectivesMap, classMembersMap, macrosList);

		//create source file
 		System.out.println("\nCreating library source file: " + RefactoringProject.NEW_LIBRARYcpp);
		MessageUtility.writeToConsole("Console", "Creating library source file: " + RefactoringProject.NEW_LIBRARYcpp);
		createSource(classMembersMap);
		
		//refactor the files that use the original library (include and using directives)
		refactorIncludeDirectives(tusUsingLib);
		refactorUsingDirectives(tusUsingLib, projectASTCache);
		refactorFullyQualifiedNames(tusUsingLib, projectASTCache);
 	}
 	
 	
 	/**
 	 * Create the header for this library
 	 * @param classMembersMap
 	 */
	private void createHeader (BindingsSet bindingsSet, Map<String, IASTName> includeDirectivesMap, Map<ICPPClassType, List<ICPPMember>> classMembersMap,
							   Collection<IASTPreprocessorMacroDefinition> macrosList) {
		try {
			//
			IFile file = CdtUtilities.createNewFile(cproject, RefactoringProject.NEW_DIR, RefactoringProject.NEW_LIBRARYhpp);
			if (file == null)
				throw new NoSuchFileException("Could not create header file " + RefactoringProject.NEW_DIR + "/" + RefactoringProject.NEW_LIBRARYhpp);

			// Create translation unit for file
			ITranslationUnit libTU = CoreModelUtil.findTranslationUnit(file);
			// get ast
			IASTTranslationUnit headerAST = libTU.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
			// get rewriter
			ASTRewrite rewriter = ASTRewrite.create(headerAST);
			// get node factory
			ICPPNodeFactory nodeFactory = (ICPPNodeFactory) headerAST.getASTNodeFactory();

			//1) Add preprocessor ifdef statements
			IASTName ifnDefStm  = nodeFactory.newName("#ifndef "+ RefactoringProject.NEW_NAMESPACE +"_INCLUDED");
			IASTName defStm 	= nodeFactory.newName("#define "+ RefactoringProject.NEW_NAMESPACE +"_INCLUDED");
			rewriter.insertBefore(headerAST, null, ifnDefStm, null);
			rewriter.insertBefore(headerAST, null, defStm, null);
			
			//2) add include directives
			for (String includeDirective : includeDirectivesMap.keySet()){
//				String includeDirective = includeDirectivesMap.get(name);
				IASTName includeDir = nodeFactory.newName("#include <" + includeDirective +">");
				rewriter.insertBefore(headerAST, null, includeDir, null);
			}
			
			//3) add namespace definition
			ICPPASTNamespaceDefinition nsDef = nodeFactory.newNamespaceDefinition(nodeFactory.newName(RefactoringProject.NEW_NAMESPACE));
			
			//4) add macro definitions
			for (IASTPreprocessorMacroDefinition macroDef : macrosList){
				IASTName macro = nodeFactory.newName(macroDef.getRawSignature());
				rewriter.insertBefore(headerAST, null, macro, null);
			}
			
			//5) create forward declarations
			refactorForwardDeclarations(nsDef, nodeFactory, classMembersMap.keySet());
			
			//5) Refactor enumerations
			refactorEnumerations(bindingsSet, nsDef);
			
			//7) Refactor classes and methods
			refactorClasses(nodeFactory, classMembersMap, nsDef);
			
			//8) add namespace to ast
			rewriter.insertBefore(headerAST, null, nsDef, null);
			
			//9) add endif preprocessor statement
			IASTName endIfStm 	= nodeFactory.newName("#endif //" + RefactoringProject.NEW_NAMESPACE +"_INCLUDED");
			rewriter.insertBefore(headerAST, null, endIfStm, null);

			rewriter.rewriteAST().perform(new NullProgressMonitor()); 
		} 
		catch (NoSuchFileException | CoreException e) {
			e.printStackTrace();
		}
	}
	
	
 	/**
 	 * Create the source for this library
 	 * @param classMembersMap
 	 */
	private void createSource (Map<ICPPClassType, List<ICPPMember>> classMembersMap) {
		try {
			//
			IFile file = CdtUtilities.createNewFile(cproject, RefactoringProject.NEW_DIR, RefactoringProject.NEW_LIBRARYcpp);
			if (file == null)
				throw new NoSuchFileException("Could not create source file " + RefactoringProject.NEW_DIR + "/" + RefactoringProject.NEW_LIBRARYcpp);

			// Create translation unit for file
			ITranslationUnit libTU = CoreModelUtil.findTranslationUnit(file);
			// get ast
			IASTTranslationUnit sourceAST = libTU.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
			// get rewriter
			ASTRewrite rewriter = ASTRewrite.create(sourceAST);
			// get node factory
			ICPPNodeFactory nodeFactory = (ICPPNodeFactory) sourceAST.getASTNodeFactory();
			
			//1) add include directives
			IASTName myLibInclude = nodeFactory.newName("#include \"" + RefactoringProject.NEW_LIBRARYhpp +"\"");
			rewriter.insertBefore(sourceAST, null, myLibInclude, null);
			IASTName iostreamINclude = nodeFactory.newName("#include <iostream>");
			rewriter.insertBefore(sourceAST, null, iostreamINclude, null);
			
			//2) add namespace definition
			ICPPASTNamespaceDefinition nsDef = nodeFactory.newNamespaceDefinition(nodeFactory.newName(RefactoringProject.NEW_NAMESPACE));
			
			//3) Refactor classes and methods
			refactorFunctionDefinitions(nodeFactory, classMembersMap, nsDef);
			
			//4) add namespace to ast
			rewriter.insertBefore(sourceAST, null, nsDef, null);
			rewriter.rewriteAST().perform(new NullProgressMonitor()); 
		} 
		catch (NoSuchFileException | CoreException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Create forward declarations
	 * @param nsDef
	 * @throws CoreException
	 */
	private void refactorForwardDeclarations (ICPPASTNamespaceDefinition nsDef, ICPPNodeFactory nodeFactory, Set<ICPPClassType> classBindings) throws CoreException{		
		for (ICPPClassType binding : classBindings){
			//add forward declaration for classes
			if (binding instanceof ICPPClassType){
				IASTName name 							 = nodeFactory.newName(binding.getName());
				ICPPASTElaboratedTypeSpecifier specifier = nodeFactory.newElaboratedTypeSpecifier(ICPPASTCompositeTypeSpecifier.k_class, name);				
				IASTSimpleDeclaration declaration		 = nodeFactory.newSimpleDeclaration(specifier);
				nsDef.addDeclaration(declaration);
			}
		}
	}
	
	
	/**
	 * Refactor enumeration
	 * @param nsDef
	 * @throws CoreException
	 */
	private void refactorEnumerations (BindingsSet bindingsSet, ICPPASTNamespaceDefinition nsDef) throws CoreException{
		try {
			projectIndex.acquireReadLock();
		
			for (int index=0; index<bindingsSet.size(); index++){
				IBinding binding = bindingsSet.getList().get(index);
				
				//do something with the enumeration
				if (binding instanceof IEnumeration){
					
					//get enumeration binding
					IEnumeration enumeration = (IEnumeration)binding;
					
					//find definitions
					IIndexName[] defs = projectIndex.findDefinitions(enumeration);
					
					if (defs.length > 0){
						IIndexName def    = defs[0];
						
						IASTNode fdecl = refactoring.findNodeFromIndex(def, false, IASTSimpleDeclaration.class);
						
						IASTSimpleDeclaration enumDeclaration = ((IASTSimpleDeclaration)fdecl).copy(CopyStyle.withLocations);
						
						//append enumeration to namespace
						nsDef.addDeclaration(enumDeclaration);
					}
				}
			}
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
 		finally{
 			projectIndex.releaseReadLock();
 		}
	}
	
	
	/**
	 * Refactor classes and methods
	 * @param nsDef
	 * @param nodeFactory
	 * @throws CoreException
	 * @throws DOMException 
	 */
	private void refactorClasses (ICPPNodeFactory nodeFactory, Map<ICPPClassType, List<ICPPMember>> classMembersMap, ICPPASTNamespaceDefinition nsDef) throws CoreException{		
		try {
			projectIndex.acquireReadLock();

			for (ICPPClassType owningclass : classMembersMap.keySet()){			
				
				//create the class
				IIndexName[] classDefs = projectIndex.findNames(owningclass, IIndex.FIND_DEFINITIONS);
				if (classDefs.length != 1 )
					throw new NoSuchElementException("Class " + owningclass.getName() +" has "+ classDefs.length +" definitions!");
			
				ICPPASTCompositeTypeSpecifier newClass = nodeFactory.newCompositeTypeSpecifier(ICPPASTCompositeTypeSpecifier.k_class, nodeFactory.
																		newName(owningclass.getName()));
				
				//if the class inherits from other classes, append this
				ICPPASTCompositeTypeSpecifier classNode = (ICPPASTCompositeTypeSpecifier)refactoring.findNodeFromIndex(classDefs[0], false, ICPPASTCompositeTypeSpecifier.class);
				for (ICPPASTBaseSpecifier superclass :classNode.getBaseSpecifiers()){
					newClass.addBaseSpecifier(superclass.copy(CopyStyle.withLocations));
				}
							
				
				//create members of this class, group the based on their visibilities (//TODO optimise this)
				if (classMembersMap.get(owningclass)!=null){
					List<ICPPMember> membersList = classMembersMap.get(owningclass);
					int visibilities[] = new int[]{ICPPASTVisibilityLabel.v_public, ICPPASTVisibilityLabel.v_protected, ICPPASTVisibilityLabel.v_private};
					
					for (int visibility : visibilities){
						boolean visLabelAdded = false;
						
						for (ICPPMember member : membersList){
							if (member.getVisibility() != visibility)
								continue;
							
							//add visibility label
							if (!visLabelAdded){
								newClass.addDeclaration(nodeFactory.newVisibilityLabel(member.getVisibility()));
								visLabelAdded = true;
							}
			
							//add declaration
							IIndexName[] memberDecls = projectIndex.findNames(member, IIndex.FIND_DECLARATIONS); //. Declarations(constructor);
							if (memberDecls.length > 0){ // its size should be 1
								IIndexName mDecl    = memberDecls[0];
								
								IASTSimpleDeclaration node = (IASTSimpleDeclaration)refactoring.findNodeFromIndex(mDecl, false, IASTSimpleDeclaration.class);
								IASTSimpleDeclaration newDeclaration = (node).copy(CopyStyle.withLocations);
								
								newClass.addMemberDeclaration(newDeclaration);
							}
							else {//if no declaration exists, try to find a definition and extract the declaration
								IIndexName[] memberDefs = projectIndex.findNames(member, IIndex.FIND_DEFINITIONS);
								if (memberDefs.length > 0){ // its size should be 1
									IIndexName mDef    = memberDefs[0];
									
									ICPPASTFunctionDefinition node 	  = (ICPPASTFunctionDefinition) refactoring.findNodeFromIndex(mDef, false, ICPPASTFunctionDefinition.class);
									IASTFunctionDeclarator declarator = node.getDeclarator().copy(CopyStyle.withLocations); 
									IASTDeclSpecifier      specifier  = node.getDeclSpecifier().copy(CopyStyle.withLocations);
									IASTSimpleDeclaration newDeclaration = nodeFactory.newSimpleDeclaration(specifier);
									
									newDeclaration.addDeclarator(declarator);						
									newClass.addMemberDeclaration(newDeclaration);	
								}
							}
						}//end member
					}//end visibitilies
				}
	
				//add the new class to the namespace
				IASTSimpleDeclaration newDeclaration = nodeFactory.newSimpleDeclaration(newClass);
				nsDef.addDeclaration(newDeclaration);
			}
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
 		finally{
 			projectIndex.releaseReadLock();
 		}
	}
	
	
	/**
	 * Refactor classes and methods
	 * @param nsDef
	 * @param nodeFactory
	 * @throws CoreException
	 * @throws IllegalAccessException 
	 * @throws DOMException 
	 */
	private void refactorFunctionDefinitions (ICPPNodeFactory nodeFactory, Map<ICPPClassType, List<ICPPMember>> classMembersMap, 
												 	ICPPASTNamespaceDefinition nsDef) throws CoreException, IllegalAccessException{		
		try {
			projectIndex.acquireReadLock();

			for (ICPPClassType owningclass : classMembersMap.keySet()){
	
				//create function definitions
				List<ICPPMember> membersList = classMembersMap.get(owningclass);
				for (ICPPMember member : membersList){
				
					if (member instanceof ICPPMethod){
						//add definition
						IIndexName[] methodDeclsDefs = projectIndex.findNames(member, IIndex.FIND_DECLARATIONS_DEFINITIONS);
						
						if (methodDeclsDefs.length > 0){ // its size should be > 0
							
							for (IIndexName dd : methodDeclsDefs){
								String path = dd.getFileLocation().getFileName();
								if (dd.isDeclaration()){// && RefactoringProject.LIB_HEADERS.contains(path)){
									
									IASTNode node = refactoring.findNodeFromIndex(dd, false, IASTSimpleDeclaration.class, IASTFunctionDefinition.class);
									if (node!=null){
										
										ICPPASTFunctionDefinition newFunctionDef = null;
										IASTDeclSpecifier 	  declSpecifier  = null;
										
										//if it is a declaration, i.e., only the signature exists in header file &
										//it has only one declarator 
										if (node instanceof IASTSimpleDeclaration){
											IASTSimpleDeclaration declaration = (IASTSimpleDeclaration) node;
											
											//check that only one declarator exists
											if ( (declaration.getDeclarators().length != 1) || (! (declaration.getDeclarators()[0] instanceof IASTFunctionDeclarator)) )
												throw new IllegalArgumentException("Unexpected declarator:\n" + node.getRawSignature());
											
											//create new function definition
											//we need to do it manually because of the chain initialisers, added later in this function
											newFunctionDef 	= nodeFactory.newFunctionDefinition(declaration.getDeclSpecifier().copy(CopyStyle.withLocations),
																							   (IASTFunctionDeclarator) declaration.getDeclarators()[0].copy(CopyStyle.withLocations),
																							   nodeFactory.newCompoundStatement());
																														
											declSpecifier	= declaration.getDeclSpecifier();
										}
										//else if it is a definition, i.e., it's an inline function in header file
										else if (node instanceof IASTFunctionDefinition){
											IASTFunctionDefinition definition = (IASTFunctionDefinition) node;
											
											newFunctionDef 	= nodeFactory.newFunctionDefinition(definition.getDeclSpecifier().copy(CopyStyle.withLocations),
																							   definition.getDeclarator().copy(CopyStyle.withLocations),
																							   nodeFactory.newCompoundStatement());
											
											declSpecifier	= definition.getDeclSpecifier();
											
										} 
										
										
										//manage function declarator qualified names so they are in the form Class::Function(...){}, in cpp file. 
										//if the specifier of this function does not include class name (e.g., defined in header) modify this
										ICPPASTFunctionDeclarator fDecl = (ICPPASTFunctionDeclarator) newFunctionDef.getDeclarator();
										if (!(fDecl.getName() instanceof ICPPASTQualifiedName)){
											ICPPASTQualifiedName qualName =  nodeFactory.newQualifiedName(new String[]{owningclass.getName()}, member.getName());
											fDecl.setName(qualName);
										}

										
										//C++ manage initialiser lists: any constructor (superclass) initialisers should be added here
										//e.g., XMLDocument::XMLDocument( bool processEntities, Whitespace whitespace ) : XMLNode( 0 )
										if (member instanceof ICPPConstructor){
											ICPPConstructor constructor =  (ICPPConstructor)member;
											ICPPASTConstructorChainInitializer initialiser = manageConstructorInitialisers(nodeFactory, classMembersMap, constructor);
											if (initialiser != null)
												newFunctionDef.addMemberInitializer(initialiser);											
										}
										

										//manage return function specifiers
										IASTReturnStatement returnStatement	 = manageReturnStatement(nodeFactory, declSpecifier);
										IASTCompoundStatement compoundStatement = (IASTCompoundStatement) newFunctionDef.getBody();
										compoundStatement.addStatement(returnStatement);
										
										
										//clean the new function definition: remove any virtual identifiers, e.g., virtual bool XMLVisitor::VisitEnter(const XMLElement&, const XMLAttribute*){}
										if (newFunctionDef.getDeclSpecifier() instanceof ICPPASTDeclSpecifier){
											ICPPASTDeclSpecifier specifier = (ICPPASTDeclSpecifier) newFunctionDef.getDeclSpecifier();
											specifier.setVirtual(false);
										}
										
										
										//clean the new function definition: remove any storage specifiers in function implementation, e.g., static void TiXmlBase::SetCondenseWhiteSpace(bool condense) {
										newFunctionDef.getDeclSpecifier().setStorageClass(IASTDeclSpecifier.sc_unspecified);

										
										//clean the new function definition: remove any parameter initialisers, e.g., bool XMLVisitor::VisitEnter(const XMLElement&, const XMLAttribute* = X){}
										IASTStandardFunctionDeclarator funDeclarator = (IASTStandardFunctionDeclarator) newFunctionDef.getDeclarator();
										for (IASTParameterDeclaration paramDeclaration : funDeclarator.getParameters()){
											paramDeclaration.getDeclarator().setInitializer(null);// (nodeFactory.newEqualsInitializer(nodeFactory.newIdExpression(nodeFactory.newName(""))));
										}


										//add the new definition to the namespace
										nsDef.addDeclaration(newFunctionDef);										
										break;
									}
								}
							}
						}//if (methodDeclsDefs.length > 0){

					}//if (member instanceof ICPPMethod)
				}//for ICPPMember member
			}//for ICPPClassType owningclass			
		} 
		catch (InterruptedException e) {
			e.printStackTrace();
		}
 		finally{
 			projectIndex.releaseReadLock();
 		}
	}
	
	
	private ICPPASTConstructorChainInitializer manageConstructorInitialisers(ICPPNodeFactory nodeFactory, 
																 Map<ICPPClassType, List<ICPPMember>> classMembersMap, 
		 						   								 ICPPConstructor constructor) throws CoreException{		
		ICPPBase bases[] = constructor.getClassOwner().getBases();
		for (ICPPBase base : bases){
			ICPPClassType baseClazz = (ICPPClassType)base.getBaseClass();
			
			//the superclass should be in the map
			if (classMembersMap.containsKey(baseClazz)) {
				ICPPParameter[] constructorParams = null;
				//TODO: How to select the most appropriate superclass constructor? 
				//      At the moment, select the first non-private && non-default copy or no-argument constructor
				for (ICPPConstructor baseClassConstructor: baseClazz.getConstructors()){
					if (baseClassConstructor.getVisibility() != ICPPConstructor.v_private){
						IIndexName[] constructorDeclsDefs = projectIndex.findNames(baseClassConstructor, IIndex.FIND_DECLARATIONS_DEFINITIONS);
						if (constructorDeclsDefs.length > 0){//i.e., it's not one of the default copy or no-argument constructors
							constructorParams		  = baseClassConstructor.getParameters();
							break;
						}
					}
				}
				//populate list of constructor params
				if (constructorParams != null) {
					//init lit expressions array
					IASTLiteralExpression litExpressions[] = new IASTLiteralExpression[constructorParams.length];
					//populate array with custom variables
					for (int i=0; i < constructorParams.length; i++){
						ICPPParameter param 	= constructorParams[i];
						IType 		  paramType	= param.getType();
						if (paramType instanceof IBasicType)
							litExpressions[i] = nodeFactory.newLiteralExpression(IASTLiteralExpression. lk_nullptr, "NULL");
						else if (paramType instanceof ICPPEnumeration){
							litExpressions[i] = nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_integer_constant, ((ICPPEnumeration)paramType).getEnumerators()[0].getName());
						}
						else
							litExpressions[i] = nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_nullptr, "nullptr");
					}
//					Arrays.fill(litExpressions, nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_nullptr, "nullptr"));
					ICPPASTConstructorChainInitializer constructorChainInitialiser = nodeFactory.newConstructorChainInitializer(nodeFactory.newName(baseClazz.getName()), 
							   																	   nodeFactory.newConstructorInitializer(litExpressions));
					return constructorChainInitialiser;
				}
			}
			else 
				throw new IllegalArgumentException("Class " + baseClazz + "not found!");
		}
		return null;	
	}

	
	private IASTReturnStatement manageReturnStatement(ICPPNodeFactory nodeFactory, IASTDeclSpecifier declSpecifier) throws CoreException{
		IASTReturnStatement returnStatement	= nodeFactory.newReturnStatement(null);
		//if the return type is simple specifier except void --> add a null return statement
		if ( (declSpecifier instanceof IASTSimpleDeclSpecifier) && 
			 (((IASTSimpleDeclSpecifier)declSpecifier).getType() > IASTSimpleDeclSpecifier.t_void) ){
			returnStatement.setReturnValue(nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_nullptr, "NULL"));
		}
		//if the return type is a qualified name (e.g., class, enumeration etc) --> 
		//   if it is a class --> add a null return statement
		//   if it is an enumerator --> select a random enum item
		else if (declSpecifier instanceof ICPPASTNamedTypeSpecifier){
			IASTName declName 		= ((ICPPASTNamedTypeSpecifier) declSpecifier).getName();
			IBinding declBinding	= declName.resolveBinding();

			if (declBinding instanceof ICPPEnumeration){
				IIndexName[] enumDefs = projectIndex.findNames(declBinding, IIndex.FIND_DEFINITIONS);
				if (enumDefs.length > 0){ // its size should be 1
					IIndexName enumDef    = enumDefs[0];
					IASTEnumerationSpecifier enumNode 	  = (IASTEnumerationSpecifier) refactoring.findNodeFromIndex(enumDef, false, IASTEnumerationSpecifier.class);
					if (enumNode.getEnumerators().length !=1)
						returnStatement.setReturnValue(nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_false, enumNode.getEnumerators()[0].getName().toString()));
					else 
						throw new IllegalArgumentException("Enumerator " + declBinding + "not found!");	
				}
			}
			else
				returnStatement.setReturnValue(nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_nullptr, "NULL"));
				
		}
		return returnStatement;
	}
	

	/**
	 * Refactor include directives in files (.h & .cpp) that use the old library to start using the new library
	 * @throws CoreException 
	 */
	private void refactorIncludeDirectives(Collection<ITranslationUnit> tusUsingLib) throws CoreException{
		for (ITranslationUnit tu : tusUsingLib){

			//change include directives
			for (IInclude include : tu.getIncludes()){
				for (String oldLibHeader : RefactoringProject.LIB_HEADERS){
					if (include.getFullFileName().contains(oldLibHeader)){
//							System.err.println("Removing include " + include.getElementName() +" from "+ tu.getFile().getFullPath());
						//delete previous include
						include.delete(false,  new NullProgressMonitor());
						tu.save(new NullProgressMonitor(), true);
						
						//find insert position
						ICElement sibling = null;
						if (tu.getIncludes().length > 0)
							sibling = tu.getIncludes()[tu.getIncludes().length-1];
						else if (tu.getUsings().length > 0)
							sibling = tu.getUsings()[0];
						else if (tu.getNamespaces().length > 0)
							sibling = tu.getNamespaces()[0];
						
						//add new include directive
						tu.createInclude(RefactoringProject.NEW_INCLUDE_DIRECTIVE, true, 
											sibling, new NullProgressMonitor());
						tu.save(new NullProgressMonitor(), true);
//							include.rename(RefactoringProject.NEW_LIBRARYhpp, true, new NullProgressMonitor());
					}
				}
			}
		}			
	}
	 
	
	
	/**
	 * Refactor using directives in files (.h & .cpp) that use the old library to start using the new library
	 * @param tusUsingLib
	 * @throws CoreException
	 */
	private void refactorUsingDirectives (Collection<ITranslationUnit> tusUsingLib, HashMap<ITranslationUnit, IASTTranslationUnit> projectASTCache) throws CoreException{
		for (ITranslationUnit tu : tusUsingLib){
	
			// get ast
			IASTTranslationUnit tuAST = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);//projectASTCache.get(tu);
			if (tuAST == null)
				throw new NullPointerException("Couldn't find an AST for TU: " + tu.getElementName());

			// get rewriter
			ASTRewrite rewriter = ASTRewrite.create(tuAST);
			
			// get node factory
			ICPPNodeFactory nodeFactory = (ICPPNodeFactory) tuAST.getASTNodeFactory();
			
			tuAST.accept(new ASTVisitor() {
				{
					shouldVisitDeclarations = true;
				}
				
				@Override
				public int visit(IASTDeclaration element) {
					if (element instanceof ICPPASTUsingDirective) {
						ICPPASTUsingDirective using = (ICPPASTUsingDirective)element;
						if (RefactoringProject.LIB_NAMESPACES.contains(using.getQualifiedName().toString())){
							System.out.println("Found\t" + using.getQualifiedName().toString());
	
							ICPPASTUsingDirective newUsing = (ICPPASTUsingDirective) nodeFactory.newUsingDirective(nodeFactory.newName(RefactoringProject.NEW_NAMESPACE));
							
							rewriter.replace(element, newUsing, null);
							try {
								rewriter.rewriteAST().perform(new NullProgressMonitor());
							} 
							catch (CoreException e) {
								e.printStackTrace();
							} 
						}
					}
					return PROCESS_CONTINUE;
				}
			});
		}
	}
	
	
	
	private void refactorFullyQualifiedNames (Collection<ITranslationUnit> tusUsingLib, HashMap<ITranslationUnit, IASTTranslationUnit> projectASTCache) throws CoreException {
		for (ITranslationUnit tu : tusUsingLib){
			
			do {
				changed = false;

				// get ast
				IASTTranslationUnit tuAST = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);//projectASTCache.get(tu);
				if (tuAST == null)
					throw new NullPointerException("Couldn't find an AST for TU: " + tu.getElementName());
				projectASTCache.put(tu, tuAST);
				 
				// get rewriter
				ASTRewrite rewriter = ASTRewrite.create(tuAST);
				
				// get node factory
				ICPPNodeFactory nodeFactory = (ICPPNodeFactory) tuAST.getASTNodeFactory();
				
				// List of changes
//				List<Change> changeList = new ArrayList<Change>();
			
			
				tuAST.accept(new ASTVisitor() {
					// static initialiser: executed when the class is loaded
					{
						shouldVisitParameterDeclarations	= true;
						shouldVisitDeclarations 			= true;
					}
	
					@Override
					public int visit(IASTDeclaration decl) {
						if (!(decl instanceof IASTSimpleDeclaration))
							return PROCESS_CONTINUE; 
						IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) decl;
	
						// check if there are any declarators: they should, otherwise there
						// would be a compilation error (e.g., int ;)
						if ((simpleDecl.getDeclSpecifier() instanceof IASTNamedTypeSpecifier)){				
							// find bindings for the declaration specifier
							IASTNamedTypeSpecifier declSpecifier = ((IASTNamedTypeSpecifier) simpleDecl.getDeclSpecifier());
							processDeclarationSpecifier(declSpecifier);
						}
						if (changed)
							return PROCESS_ABORT;
						return PROCESS_CONTINUE;
					}
					
					
					@Override
					public int visit(IASTParameterDeclaration pDecl) {
						// check if there are any declarators: they should, otherwise there
						// would be a compilation error (e.g., int ;)
						if ((pDecl.getDeclSpecifier() instanceof IASTNamedTypeSpecifier)){
							// find bindings for the declaration specifier
							IASTNamedTypeSpecifier declSpecifier = ((IASTNamedTypeSpecifier) pDecl.getDeclSpecifier());
							processDeclarationSpecifier(declSpecifier);
						}
						if (changed)
							return PROCESS_ABORT;
						return PROCESS_CONTINUE;
					}
					
					
					private void processDeclarationSpecifier(IASTNamedTypeSpecifier declSpecifier ){
						try {
							IASTName declSpecifierName = declSpecifier.getName();
							if (declSpecifierName instanceof ICPPASTQualifiedName){
								ICPPASTQualifiedName qualifiedName 		= (ICPPASTQualifiedName)declSpecifierName;
//								System.out.println(qualifiedName +"\t"+ qualifiedName.isQualified() +"\t"+ qualifiedName.isFullyQualified());
								
								for (ICPPASTNameSpecifier nameSpecifier : qualifiedName.getQualifier()){
									if (!RefactoringProject.LIB_NAMESPACES.contains(nameSpecifier.toString()))
										return;
//									System.out.println(nameSpecifier.toString());
								}
								ICPPASTQualifiedName newQualifiedName	= nodeFactory.newQualifiedName(new String[]{RefactoringProject.NEW_NAMESPACE}, qualifiedName.getLastName().toString());
								rewriter.replace(qualifiedName, newQualifiedName, null);
								rewriter.rewriteAST().perform(new NullProgressMonitor());
								
								changed = true;
	
	//							Change c = rewriter.rewriteAST();
	//							changeList.add(c);
	//							declSpecifier.setName(newQualifiedName);//AST froze, does not work
//								System.out.println(Arrays.toString(qualifiedName.getQualifier()));
//								System.out.println(declSpecifier.getRawSignature());
							} 
						}
						catch (CoreException e) {
							e.printStackTrace();
						}
					}
				});
			}
			while (changed);
			
			
//			for (Change c : changeList)
//				c.perform(new NullProgressMonitor());
		}
	}
	
	
	@Deprecated
	private void refactorUsingDirectivesOld(Collection<ITranslationUnit> tusUsingLib) throws CModelException{
		for (ITranslationUnit tu : tusUsingLib){
		
			//change the namespace
			for (IUsing using : tu.getUsings()){
				if (RefactoringProject.LIB_NAMESPACES.contains(using.getElementName())){
	//				namespace. rename("BOOOB", true, new NullProgressMonitor());
	//				System.err.println("Removing using " + using.getElementName() +" from "+ tu.getFile().getFullPath());
					//delete previous using directive
					using.delete(true,  new NullProgressMonitor());
					tu.save(new NullProgressMonitor(), true);
					
					//find insert position
					ICElement sibling = null; 
					if (tu.getUsings().length > 0)
						sibling = tu.getUsings()[tu.getUsings().length-1];
					else if (tu.getIncludes().length > 0)
						sibling = tu.getIncludes()[tu.getIncludes().length-1];
					else if (tu.getNamespaces().length > 0)
						sibling = tu.getNamespaces()[0];
					
	
					//add new using directive
					myTranslationUnit myTU = new myTranslationUnit(tu.getParent(), tu.getFile(), 
												tu.isHeaderUnit()?CCorePlugin.CONTENT_TYPE_CXXHEADER:CCorePlugin.CONTENT_TYPE_CXXSOURCE);
					myTU.createUsing(RefactoringProject.NEW_NAMESPACE, true, sibling, new NullProgressMonitor());
					
					myTU.save(new NullProgressMonitor(), true);
				}
			}
		}
	}
	
	
	@Deprecated
	private void checkNamespaceUsing (ITranslationUnit tu, IParent parent) throws CModelException{
		List<ICElement> namespaces = parent.getChildrenOfType(ICElement.C_NAMESPACE);
		for (ICElement namespace : namespaces){
			rename(tu, ((INamespace)namespace));
		
			checkNamespaceUsing(tu, (INamespace)namespace);			
		}
	}

	
	@Deprecated
	private void rename(ITranslationUnit tu, IParent parent) throws CModelException{
		List<ICElement> usings		  = parent.getChildrenOfType(ICElement.C_USING);
		for (ICElement using : usings){
			if (RefactoringProject.LIB_NAMESPACES.contains(((IUsing)using).getElementName())){				
//				//delete previous using directive
//				((IUsing)using).delete(true,  new NullProgressMonitor());
//				tu.save(new NullProgressMonitor(), true);			
//
//				//find insert position
//				ICElement sibling = null; 
//				if (parent.getChildrenOfType(ICElement.C_USING).size() > 0)
//					sibling = parent.getChildrenOfType(ICElement.C_USING).get(parent.getChildrenOfType(ICElement.C_USING).size()-1); 					
//				else if (parent.getChildrenOfType(ICElement.C_INCLUDE).size() > 0)
//					sibling = parent.getChildrenOfType(ICElement.C_INCLUDE).get(parent.getChildrenOfType(ICElement.C_INCLUDE).size()-1); 					
//				else if (parent.getChildrenOfType(ICElement.C_NAMESPACE).size() > 0)
//					sibling = parent.getChildrenOfType(ICElement.C_NAMESPACE).get(0);
//				else
//					sibling = parent.getChildren()[0];
//				
//				//add new using directive
//				myTranslationUnit myTU = new myTranslationUnit(tu.getParent(), tu.getFile(), 
//											tu.isHeaderUnit()?CCorePlugin.CONTENT_TYPE_CXXHEADER:CCorePlugin.CONTENT_TYPE_CXXSOURCE);
//				myTU.createUsing(RefactoringProject.NEW_NAMESPACE, true, sibling, new NullProgressMonitor());
//				
//				myTU.save(new NullProgressMonitor(), true);					
			}
		}		
		
		
//		//Doesn't work
//		List<ICElement> usings		  = parent.getChildrenOfType(ICElement.C_USING);
//		for (ICElement using : usings){
//			if (RefactoringProject.LIB_NAMESPACES.contains(((IUsing)using).getElementName())){
//				((IUsing)using).rename(RefactoringProject.NEW_NAMESPACE, true, new NullProgressMonitor());
//			}
//		}
	}
	
	
	/**
	 * Bug in TranslationUnit.createUsing(String usingName, boolean isDirective, ICElement sibling, IProgressMonitor monitor)
	 * instead of CreateUsingOperation(usingName, isDirective, this), it calls CreateIncludeOperation(usingName, isDirective, this); 
	 * @author sgerasimou
	 *
	 */
	@SuppressWarnings("restriction")
	class myTranslationUnit extends TranslationUnit{

		
		public myTranslationUnit(ICElement parent, IFile file , String id) {
			super(parent, file, id);
		}
		
		@Override
		public IUsing createUsing(String usingName, boolean isDirective, ICElement sibling,
				IProgressMonitor monitor) throws CModelException {
			CreateUsingOperation op = new CreateUsingOperation(usingName, isDirective, this);
			if (sibling != null) {
				op.createBefore(sibling);
			}
			op.runOperation(monitor);
			return getUsing(usingName);
		}
	}
}
