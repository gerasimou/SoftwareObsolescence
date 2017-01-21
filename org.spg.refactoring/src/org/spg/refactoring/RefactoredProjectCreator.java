package org.spg.refactoring;

import java.io.File;
import java.nio.file.NoSuchFileException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTEnumerationSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTLiteralExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTNode.CopyStyle;
import org.eclipse.cdt.core.dom.ast.IASTReturnStatement;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTCompositeTypeSpecifier.ICPPASTBaseSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTConstructorChainInitializer;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTElaboratedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamespaceDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTQualifiedName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTVisibilityLabel;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPEnumeration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPNodeFactory;
import org.eclipse.cdt.core.dom.rewrite.ASTRewrite;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModelUtil;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.IInclude;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.core.model.IUsing;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.spg.refactoring.ProjectAnalyser.BindingsSet;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

public class RefactoredProjectCreator {
	/** project */
	protected ICProject cproject = null;

	/** project index */
	protected IIndex projectIndex = null;
	
	/** Pairs of ITranslationUnit, IASTTranslationUnit **/
	HashMap<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<ITranslationUnit, IASTTranslationUnit>();

	RefactoringProject refactoring;

	
	public RefactoredProjectCreator(RefactoringProject refProject, HashMap<ITranslationUnit, IASTTranslationUnit>  astCache) {
		this.astCache = astCache;
		this.refactoring = refProject;
	}
	
	
 	/**
 	 * After analysing the selected project, create a new (refactored) project based on the adapter pattern 
 	 * @param classMembersMap
 	 * @throws Exception
 	 */
 	protected void createRefactoredProject(ICProject project, IIndex index, BindingsSet bindingsSet, 
 											LinkedHashMap<IASTName, String> includeDirectivesMap, Map<ICPPClassType, List<ICPPMember>> classMembersMap) throws Exception{
 		this.projectIndex = index;
 		this.cproject	  = project;	
 		
		
		//create header file
		MessageUtility.writeToConsole("Console", "Creating library header file: " + RefactoringProject.NEW_LIBRARYhpp);
		createHeader(bindingsSet, includeDirectivesMap, classMembersMap);

		//create source file
		MessageUtility.writeToConsole("Console", "Creating library source file: " + RefactoringProject.NEW_LIBRARYcpp);
		createSource(classMembersMap);
 	}
 	
 	
 	/**
 	 * Create the header for this library
 	 * @param classMembersMap
 	 */
	private void createHeader (BindingsSet bindingsSet, LinkedHashMap<IASTName, String> includeDirectivesMap, Map<ICPPClassType, List<ICPPMember>> classMembersMap) {
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
			for (IASTName name : includeDirectivesMap.keySet()){
				String includeDirective = includeDirectivesMap.get(name);
				IASTName includeDir = nodeFactory.newName("#include <" + includeDirective +">");
				rewriter.insertBefore(headerAST, null, includeDir, null);
			}
			
			//3) add namespace definition
			ICPPASTNamespaceDefinition nsDef = nodeFactory.newNamespaceDefinition(nodeFactory.newName(RefactoringProject.NEW_NAMESPACE));
			
			//4) create forward declarations
			refactorForwardDeclarations(nsDef, nodeFactory, classMembersMap.keySet());
			
			//5) Refactor enumerations
			refactorEnumerations(bindingsSet, nsDef);
			
			//6) Refactor classes and methods
			refactorClasses(nodeFactory, classMembersMap, nsDef);
			
			//7) add namespace to ast
			rewriter.insertBefore(headerAST, null, nsDef, null);
			
			//8) add endif preprocessor statement
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
			
			//2) add using directives
//			ICPPASTUsingDirective usingDirective = nodeFactory.newUsingDirective(nodeFactory.newName("tinyxml2"));
//			rewriter.insertBefore(libAST, null, usingDirective, null);

			//3) add namespace definition
			ICPPASTNamespaceDefinition nsDef = nodeFactory.newNamespaceDefinition(nodeFactory.newName(RefactoringProject.NEW_NAMESPACE));
			
			//4) Refactor classes and methods
			refactorFunctionImplementations(nodeFactory, classMembersMap, nsDef);
			
			//6) add namespace to ast
			rewriter.insertBefore(sourceAST, null, nsDef, null);
			rewriter.rewriteAST().perform(new NullProgressMonitor()); 
		} 
		catch (NoSuchFileException | CoreException e) {
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
						
						IASTNode fdecl = refactoring.findNodeFromIndex(def, IASTSimpleDeclaration.class);
						
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
				ICPPASTCompositeTypeSpecifier classNode = (ICPPASTCompositeTypeSpecifier)refactoring.findNodeFromIndex(classDefs[0], ICPPASTCompositeTypeSpecifier.class);
				for (ICPPASTBaseSpecifier superclass :classNode.getBaseSpecifiers()){
					newClass.addBaseSpecifier(superclass.copy(CopyStyle.withLocations));
				}
							
				
				//create members of this class, group the based on their visibilities (//TODO optimise this)
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
							
							IASTSimpleDeclaration node = (IASTSimpleDeclaration)refactoring.findNodeFromIndex(mDecl, IASTSimpleDeclaration.class);
							IASTSimpleDeclaration newDeclaration = (node).copy(CopyStyle.withLocations);
							
							newClass.addMemberDeclaration(newDeclaration);
						}
						else {//if no declaration exists, try to find a definition and extract the declaration
							IIndexName[] memberDefs = projectIndex.findNames(member, IIndex.FIND_DEFINITIONS);
							if (memberDefs.length > 0){ // its size should be 1
								IIndexName mDef    = memberDefs[0];
								
								ICPPASTFunctionDefinition node 	  = (ICPPASTFunctionDefinition) refactoring.findNodeFromIndex(mDef, ICPPASTFunctionDefinition.class);
								IASTFunctionDeclarator declarator = node.getDeclarator().copy(CopyStyle.withLocations); 
								IASTDeclSpecifier      specifier  = node.getDeclSpecifier().copy(CopyStyle.withLocations);
								IASTSimpleDeclaration newDeclaration = nodeFactory.newSimpleDeclaration(specifier);
								
								newDeclaration.addDeclarator(declarator);						
								newClass.addMemberDeclaration(newDeclaration);	
							}
						}
					}//end member
				}//end visibitilies
	
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
	 * @throws DOMException 
	 */
	private void refactorFunctionImplementations (ICPPNodeFactory nodeFactory, Map<ICPPClassType, List<ICPPMember>> classMembersMap, 
												 	ICPPASTNamespaceDefinition nsDef) throws CoreException{		
		try {
			projectIndex.acquireReadLock();

			for (ICPPClassType owningclass : classMembersMap.keySet()){
	
				//create function definitions
				List<ICPPMember> membersList = classMembersMap.get(owningclass);
				for (ICPPMember member : membersList){
				
					if (member instanceof ICPPMethod){
						//add definition
						IIndexName[] methodDefs = projectIndex.findNames(member, IIndex.FIND_DEFINITIONS);
						
						if (methodDefs.length > 0){ // its size should be 1
							IIndexName mDef    = methodDefs[0];
							ICPPASTFunctionDefinition node = (ICPPASTFunctionDefinition)refactoring.findNodeFromIndex(mDef, ICPPASTFunctionDefinition.class);
							
							//create new function definition
							//we need to do it manually because of the chain initialisers, added later in this function
	//						ICPPASTFunctionDefinition newFunctionDef = node.copy(CopyStyle.withLocations);
	//						newFunctionDef.setBody(nodeFactory.newCompoundStatement());
							ICPPASTFunctionDefinition newFunctionDef = nodeFactory.newFunctionDefinition(node.getDeclSpecifier().copy(CopyStyle.withLocations),
									  																	 node.getDeclarator().copy(CopyStyle.withLocations),
									  																	 nodeFactory.newCompoundStatement());
																			
							//manage function declarator qualified names so that are in the form Class::Function(...){}, in cpp file. 
							//if the specifier of this function does not include class name (e.g., defined in header) modify this
							ICPPASTFunctionDeclarator fDecl = (ICPPASTFunctionDeclarator) newFunctionDef.getDeclarator();
							if (!(fDecl.getName() instanceof ICPPASTQualifiedName)){
								ICPPASTQualifiedName qualName =  nodeFactory.newQualifiedName(new String[]{owningclass.getName()}, member.getName());
								fDecl.setName(qualName);
							}
							
							//manage initialiser lists: any constructor (superclass) initialisers should be added here 
							for (ICPPASTConstructorChainInitializer initialiser: node.getMemberInitializers()){
								IASTName initialiserName = initialiser.getMemberInitializerId();
								IBinding initialiserBinding = initialiserName.resolveBinding(); 
								
								if ( (initialiserBinding instanceof ICPPConstructor) ){
									IBinding bindingClass = initialiserBinding.getOwner();
									if (classMembersMap.keySet().contains(bindingClass))
										newFunctionDef.addMemberInitializer(initialiser.copy(CopyStyle.withLocations));
									else 
										throw new IllegalArgumentException("Class " + bindingClass + "not found!");	
								}
							}
							
							//manage return function specifiers
							IASTDeclSpecifier declSpecifier 	= node.getDeclSpecifier();
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
										IASTEnumerationSpecifier enumNode 	  = (IASTEnumerationSpecifier) refactoring.findNodeFromIndex(enumDef, IASTEnumerationSpecifier.class);
										if (enumNode.getEnumerators().length !=1)
											returnStatement.setReturnValue(nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_false, enumNode.getEnumerators()[0].getName().toString()));
										else 
											throw new IllegalArgumentException("Enumerator " + declBinding + "not found!");	
									}
								}
								else
									returnStatement.setReturnValue(nodeFactory.newLiteralExpression(IASTLiteralExpression.lk_nullptr, "NULL"));
									
							}
							IASTCompoundStatement compoundStatement = (IASTCompoundStatement) newFunctionDef.getBody();
							compoundStatement.addStatement(returnStatement);
	
	
							//add the new definition to the namespace
							nsDef.addDeclaration(newFunctionDef);
						}
						
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
	 * Refactor files (.h & .cpp) that use the old library to start using the new library
	 * @throws CModelException 
	 */
	protected void refactorAffectedFiles() throws CModelException{
		Set<String> oldLib = new HashSet<String>(Arrays.asList(RefactoringProject.OLD_HEADERS));
		 
		// for each translation unit get its AST
		for (ITranslationUnit tu : astCache.keySet()) {
			System.out.println(tu.getFile().getFullPath());
			String filename = tu.getElementName(); 
			if (oldLib.contains(filename)){
				for (IInclude include : tu.getIncludes()){
					System.out.println(include.getElementName());
				}				
				for (IUsing using : tu.getUsings()){
					System.out.println(using.getElementName());
				}
			}
		}
	}
}
