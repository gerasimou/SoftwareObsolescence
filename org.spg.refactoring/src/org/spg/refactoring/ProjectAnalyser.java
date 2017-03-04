package org.spg.refactoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.DOMException;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTExpression;
import org.eclipse.cdt.core.dom.ast.IASTFieldReference;
import org.eclipse.cdt.core.dom.ast.IASTFunctionCallExpression;
import org.eclipse.cdt.core.dom.ast.IASTIdExpression;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.ICompositeType;
import org.eclipse.cdt.core.dom.ast.IEnumeration;
import org.eclipse.cdt.core.dom.ast.IFunction;
import org.eclipse.cdt.core.dom.ast.IProblemBinding;
import org.eclipse.cdt.core.dom.ast.ITypedef;
import org.eclipse.cdt.core.dom.ast.IVariable;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTName;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTNamedTypeSpecifier;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPASTParameterDeclaration;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPBase;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPConstructor;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMethod;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPParameter;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.cdt.internal.core.dom.parser.cpp.ICPPUnknownBinding;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.epsilon.common.util.ListSet;
import org.spg.refactoring.utilities.Digraph;
import org.spg.refactoring.utilities.MessageUtility;


@SuppressWarnings("restriction")
public class ProjectAnalyser {

	/** project index */
	protected IIndex projectIndex = null;

	/** Pairs of elements-potential name from standard C++ library that should be included using #include directives*/
	LinkedHashMap<IASTName, String> includeDirectivesMap; 
	
	/** Map that between classes and members (functions, methods etc) */
	Map<ICPPClassType, List<ICPPMember>> classMembersMap;

	/** List keeping the translation units using the old library*/
	List<ITranslationUnit> tusUsingLibList;

	/** Keep refactoring information*/
	NamesSet 	namesSet 	 = new NamesSet();
	BindingsSet bindingsSet  = new BindingsSet();
	//FIXME: not correct, do not use this list
	List<IASTNode> nodesList = new ArrayList<IASTNode>();

//	CDTSet<IASTName> namesSet2 	  = new CDTSet<IASTName>(IASTName.class);
//	CDTSet<IBinding> bindingsSet2 = new CDTSet<IBinding>(IBinding.class);
//	CDTSet<IASTNode> nodesList2   = new CDTSet<IASTNode>(IASTNode.class);
	
	/** Refactoring element*/
	RefactoringProject refactoring;
	
	
	
	public ProjectAnalyser(RefactoringProject refProject) {
		this.refactoring 		  = refProject;
		this.includeDirectivesMap = new LinkedHashMap<IASTName, String>();
		this.tusUsingLibList 	  = new ArrayList<ITranslationUnit>();
	}

	
 	/** 
 	 * Analyse selected project to extract all necessary information for refactoring
 	 * @param project
 	 * @throws CoreException
 	 */
 	protected void analyseExistingProject(IIndex index, HashMap<ITranslationUnit, IASTTranslationUnit>  astCache) throws CoreException{
 		this.projectIndex = index;
 		
		/**Pairs of ITranslationUnit, List<IASTName>, where List
		 * <IASTName> keeps the IASTNames used from the legacy library **/
		HashMap<ITranslationUnit, List<IASTName>> libraryCache = new HashMap<>();
		
		// for each translation unit get its AST
		for (ITranslationUnit tu : astCache.keySet()) {

//			if (tu.getElementName().contains("xmlfunctions") ){
//				System.out.println("analysing " + tu.getElementName());
//			}
			
			NameFinderASTVisitor fcVisitor = new NameFinderASTVisitor();
			astCache.get(tu).accept(fcVisitor);
			
			// if the list is not empty, then it uses the legacy library --> add it to cached library
			if (fcVisitor.libraryCallsExist()) {
				
//				libraryCache.put(tu, fcVisitor.namesList);
				if ( (fcVisitor.namesSet.size() != fcVisitor.nodesList.size()) 
						|| (fcVisitor.bindingsSet.size() != fcVisitor.nodesList.size()) )
					System.out.println("Something is wrong with the lists");
				
				libraryCache.put(tu, fcVisitor.namesList);
				namesSet.addAll(fcVisitor.namesSet);
				bindingsSet.addAll(fcVisitor.bindingsSet);
				nodesList.addAll(fcVisitor.nodesList);
				tusUsingLibList.add(tu);
			}
		}
					
		System.out.println("\nAffected files:\t" + Arrays.toString(tusUsingLibList.toArray()));
		
		//check for library uses within the same library
		MessageUtility.writeToConsole("Console", "Checking class inheritance and method signature.");
		checkReferences();

		System.out.println(namesSet.size() +"\t"+ bindingsSet.size() +"\t"+ nodesList.size());
		for (int i=0; i<namesSet.size(); i++){
			System.out.println(namesSet.getList().get(i) +"\t"+ bindingsSet.getList().get(i).getClass().getSimpleName());// +"\t"+ nodesList.get(i));
		}
		
		//find mappings class - members
		classMembersMap = createClassMembersMapping();
		printClassMembersMap();
 	}
 	
 	
 	private void printClassMembersMap(){
 		StringBuilder str = new StringBuilder();
		for (ICompositeType composite : classMembersMap.keySet()){
			str.setLength(0);
			str.append(composite.getName() +":\t");
			Iterator<ICPPMember> it =  classMembersMap.get(composite).iterator();
			while (it.hasNext()){
				str.append(it.next().getName());
				if (it.hasNext())
					str.append(",");
			}
			System.out.println(str.toString());
		}
 	}
 	
 	private void checkReferences () {
 		try {
 			BindingsSet bindings = new BindingsSet();
 			bindings.addAll(bindingsSet);

			projectIndex.acquireReadLock();
	 		for (IBinding binding : bindings){
	 			if (binding instanceof ICPPClassType){
//	 				System.out.println(binding + "\t ICPPClassType");
	 				ICPPClassType classBinding = (ICPPClassType)binding;	 	
	 				
	 				//check inheritance of this class
	 				checkClassInheritance(classBinding);
	 				
	 				//check constructors' signatures
	 				for (ICPPConstructor constructor : classBinding.getConstructors()){
	 					checkMethodSignature(constructor);
	 				}
	 			}
	 			else if (binding instanceof IEnumeration){
//	 				System.out.println(binding + "\t IEnumeration");
	 			}
	 			else if (binding instanceof ICPPMethod){
//	 				System.out.println(binding + "IMethod");
	 				//check the signature of this method 
	 				checkMethodSignature((ICPPMethod)binding);
	 			}
	 		}	
 		} 
 		catch (InterruptedException | CoreException | DOMException  e) {		
 			e.printStackTrace();		
 		}
 		finally{
 			projectIndex.releaseReadLock();
 		}
 	}

 	
 	/**
 	 * Check inheritance for a given class
 	 * @param binding
 	 * @throws CoreException
 	 */
 	private void checkClassInheritance (ICPPClassType classBinding) throws CoreException{
		for (ICPPBase baseClazz : classBinding.getBases()){
			IBinding baseBinding = baseClazz.getBaseClass();
			
			if (baseBinding instanceof ICPPClassType){
				//if the base binding (base class) is not in the bindings set, 
				if (!bindingsSet.contains(baseBinding)){
					IIndexName[] cDefs = projectIndex.findNames(baseBinding, IIndex.FIND_DEFINITIONS);
					if (cDefs.length > 0){						
						bindingsSet.add(baseBinding);
						ICPPASTName nameNode = (ICPPASTName) refactoring.findNodeFromIndex(cDefs[0], false, ICPPASTName.class);
						namesSet.add(nameNode);
					}
					//recursively check the base binding (parent class)
					checkClassInheritance((ICPPClassType)baseBinding);
				}
			}	
		}
 	}
 	
 	
 	private void checkMethodSignature(ICPPMethod methodBinding) throws CoreException, DOMException, InterruptedException{
		IIndexName[] methodDecls = projectIndex.findNames(methodBinding, IIndex.FIND_DECLARATIONS);
		if (methodDecls.length > 0){						
			ICPPASTFunctionDeclarator methodDecl = (ICPPASTFunctionDeclarator) refactoring.findNodeFromIndex(methodDecls[0], false, ICPPASTFunctionDeclarator.class);
			
			//check return type
			IASTNode parent = methodDecl.getParent(); 
			IASTDeclSpecifier returnDeclSpecifier = null;
			if (parent instanceof ICPPASTFunctionDefinition)
				returnDeclSpecifier = ((ICPPASTFunctionDefinition)parent).getDeclSpecifier();
			else if (parent instanceof IASTSimpleDeclaration)
				returnDeclSpecifier = ((IASTSimpleDeclaration)parent).getDeclSpecifier();
			
			if (returnDeclSpecifier instanceof ICPPASTNamedTypeSpecifier){
				checkDeclSpecifier(returnDeclSpecifier);
			}
			
			//check parameters
			ICPPASTParameterDeclaration paramDecls[] = methodDecl.getParameters();
			for (ICPPASTParameterDeclaration paramDecl :paramDecls ){
				IASTDeclSpecifier paramDeclSpecifier = paramDecl.getDeclSpecifier();
				
				checkDeclSpecifier(paramDeclSpecifier);
			}
		}
 	}
 	
 	
 	private void checkDeclSpecifier(IASTDeclSpecifier declSpecifier) throws CoreException, DOMException, InterruptedException{
		//if it's not not a simple specifier (void, int, double, etc.) 
		//1) if it's part of the legacy library, include it in the set of elements to be migrated
		//2) if it's part of a standard c++ library, add it to the set of include directives
		if (declSpecifier instanceof IASTNamedTypeSpecifier){
			IASTName declSpecifierName 	= ((IASTNamedTypeSpecifier) declSpecifier).getName();
			IBinding declSpecifierBinding	= declSpecifierName.resolveBinding(); 
			
			//find where the param specifier is defined
			if ( (declSpecifierBinding instanceof ICompositeType) || (declSpecifierBinding instanceof IEnumeration) ){
				IASTNode node= checkBindingGeneral(declSpecifierBinding, IIndex.FIND_DEFINITIONS, true, true);
						
				if (node!=null){
					bindingsSet.add(declSpecifierBinding);
					namesSet.add(declSpecifierName);	
				}
				return;
			}
			else if (declSpecifierBinding instanceof ITypedef){//it is an include directive from the c++ libs
				IASTNode node= checkBindingGeneral(declSpecifierBinding, IIndex.FIND_DEFINITIONS, false, false);
				
				if (node!=null){
					System.out.println(node +"\t"+ node.getContainingFilename() +"\t"+ node.getTranslationUnit().getFilePath());
					includeDirectivesMap.put(declSpecifierName, node.getContainingFilename());
				}
				return;
			}
		}
 	}
 

	/**
	 * Given the binding set, generate a hash map that comprises the 
	 * @return
	 */
	private LinkedHashMap<ICPPClassType, List<ICPPMember>> createClassMembersMapping(){
		HashMap<ICPPClassType, List<ICPPMember>> classMembersMap = new HashMap<ICPPClassType, List<ICPPMember>>(); 
		
		//sort binding set: 1st classes/enumerations, then methods and variables
		//TODO: this breaks the consistency between the bindingsSet and sorted set; not needed for now
		BindingsSet bindingsSetSorted = new BindingsSet();
 		for (IBinding binding : bindingsSet){
 			if ( (binding instanceof ICompositeType) || (binding instanceof IEnumeration) )
 				bindingsSetSorted.add(binding);
 		}
 		for (IBinding binding : bindingsSet){
 			if ( (binding instanceof IFunction) || (binding instanceof IVariable) )
 				bindingsSetSorted.add(binding); 			
 		}
 		bindingsSet = bindingsSetSorted;
		
 		for (IBinding binding : bindingsSet){			
			//if it is a member of a class (method or field) & its owner is actually a class
			if ( (binding instanceof ICPPMember) && (binding.getOwner() instanceof ICPPClassType) ){					
				ICPPMember 	  classMember = (ICPPMember)binding;
				ICPPClassType owningClass = (ICPPClassType)binding.getOwner();

				//if this class is not in the hashmap, create an arraylist and add the mapping
				if (!classMembersMap.containsKey(owningClass)){
					ArrayList<ICPPMember> membersList = new ArrayList<ICPPMember>();
					membersList.addAll(Arrays.asList(owningClass.getConstructors()));//add class constructors
					membersList.add(classMember); //add the member
					classMembersMap.put(owningClass, membersList);
				}
				//if the class exists in the hashmap, simply add the member to the list
				else{
					classMembersMap.get(owningClass).add(classMember);
				}
			}
			//if it is a class and does exist in the hashmap, create a mapping with an empty array list
			else if (binding instanceof ICPPClassType){
				ICPPClassType owningClass = (ICPPClassType)binding;
				if (!classMembersMap.containsKey(owningClass)){
					ArrayList<ICPPMember> membersList = new ArrayList<ICPPMember>();
					membersList.addAll(Arrays.asList(owningClass.getConstructors()));//add class constructors
					classMembersMap.put(owningClass, membersList);
				}
//				else //duplicates should not exists at this point
//					throw new IllegalArgumentException("Class " + owningClass.getName() + " already exists in hashmap");
			}
		}
		
		//once the mapping is done, do a dependency/inheritance topological sorting of the classes
		//do determine their insertion order in the header file
		//TODO: optimise this; it can be embedded into the previous for loop, or in checkClassInheritance()
		Digraph<ICPPClassType> bindingsGraph = new Digraph<ICPPClassType>();
		for (ICPPClassType classBinding : classMembersMap.keySet()){		
			bindingsGraph.add(classBinding);

			//find base classes and add them to the DAG
			for (ICPPBase baseClazz : classBinding.getBases()){
				IBinding baseBinding = baseClazz.getBaseClass();
					
				//if the base binding (base class) is not in the bindings set
				//then checkClassInheritance() failed
				if (!bindingsSet.contains(baseBinding))
					throw new NoSuchElementException("Base class " + baseBinding + "not exists in bindings set!");
					
				if (baseBinding instanceof ICPPClassType)
					bindingsGraph.add((ICPPClassType)baseBinding, classBinding);
			}
		}
        System.out.println("\nA topological sort of the vertices: " + bindingsGraph.topSort());
		
		List<ICPPClassType> topSortedBindings = bindingsGraph.topSort();
		LinkedHashMap<ICPPClassType, List<ICPPMember>> sortedClassMembersMap = new LinkedHashMap<ICPPClassType, List<ICPPMember>>(); 
		for (ICPPClassType binding : topSortedBindings){
			sortedClassMembersMap.put(binding, classMembersMap.get(binding));
		}
		
		return sortedClassMembersMap;
	}
	
	
	/**
	 * Check if the given binding exists in the project index
	 * @param binding
	 * @param indexFlags
	 * @param indexLocked
	 * @return the IAST node corresponding to this item, NULL if it doesn't exist
	 * @throws CoreException
	 * @throws InterruptedException
	 */
	private IASTNode checkBindingGeneral (IBinding binding, int indexFlags, boolean indexLocked, boolean fromObsoleteLibrary) 
			throws CoreException, InterruptedException{
		if ( !((binding instanceof ICompositeType) ||
			   (binding instanceof IEnumeration)   ||
			   (binding instanceof IFunction) 	   ||
			   (binding instanceof ITypedef))       ){
			throw new IllegalArgumentException(binding + " has unexpected binding class " + binding.getClass());
		}
		
//		boolean exists = false;
		IASTNode node  = null;
		projectIndex.acquireReadLock();
		IIndexName[] defs = projectIndex.findNames(binding, indexFlags);
		for (IIndexName dd : defs){
			String path = dd.getFileLocation().getFileName();
			
			boolean valid = false;
			if ( (binding instanceof ICompositeType) || (binding instanceof IEnumeration))
				valid =  dd.isDefinition();
			else if (binding instanceof IFunction)
				valid =  dd.isDeclaration();
			else if (binding instanceof ITypedef)
				valid =  true;
			
			if (valid && (!fromObsoleteLibrary || RefactoringProject.OLD_HEADERS.contains(path))){
//			if (dd.isDefinition() && RefactoringProject.OLD_HEADERS.contains(path)){
				System.out.print(binding.getName() + "\t"+path +"\t"+ dd.isBaseSpecifier() +"\t"+ dd.isDeclaration() +"\t"+ dd.isDefinition() +"\t");
				node = refactoring.findNodeFromIndex(dd, indexLocked, IASTNode.class);
				if (node!=null)
					System.out.println(node. getPropertyInParent());
//				exists = true;
				break;
			}
		}
		projectIndex.releaseReadLock();
		return node;
	}
	
	
	
	
	
	
 	
	private class NameFinderASTVisitor extends ASTVisitor {
		/** List keeping all important IASTNames**/
		private NamesSet namesSet;
		
		/** Set keeping all important IASTNames, but doesn't accept duplicates **/
		private List<IASTName> namesList;

		/** List keeping all important IBindings, but doesn't accept duplicates **/
		private BindingsSet bindingsSet;
		
		
		private List<IASTNode> nodesList;


		// static initialiser: executed when the class is loaded
		{
			shouldVisitNames 					= true;
			shouldVisitExpressions				= true;
			shouldVisitParameterDeclarations	= true;
			shouldVisitDeclarations 			= true;
			//
			namesSet 		= new NamesSet();
			bindingsSet  	= new BindingsSet();
			namesList		= new ArrayList<IASTName>();
			nodesList		= new ArrayList<IASTNode>();
					
		}


		private boolean libraryCallsExist(){
			return namesSet.size()>0;
		}

		
		/**
		 * This is to capture: 
		 * (1) functions that belong to a class: e.g., {@code xmlDoc.LoadFile(filename)} 
		 * (2) function that <b>do not</b> belong to a class: e.g., {@code printf("%s", filename)}
		 */
		@Override
		public int visit(IASTExpression exp) {
			if (!(exp instanceof IASTFunctionCallExpression))
				return PROCESS_CONTINUE;
			IASTFunctionCallExpression funcCallExp = (IASTFunctionCallExpression) exp;

			IASTExpression funcExpression = funcCallExp.getFunctionNameExpression();

			IASTName name = null;
			IASTNode node = null; 
			
			// Functions that belong to a class: e.g.,
			// xmlDoc.LoadFile(filename);
			if (funcExpression instanceof IASTFieldReference) {
				// get the field reference for this: e.g., xmlDoc.LoadFile
				IASTFieldReference fieldRef = (IASTFieldReference) funcExpression;
				// get the name
				name = fieldRef.getFieldName();
				node = fieldRef;
			}
			// Functions that *do not* belong to a class: 
			//e.g., printf("%s", filename);
			else if (funcExpression instanceof IASTIdExpression) {
				// get the function name: e.g., printf
				IASTIdExpression idExp = (IASTIdExpression) funcExpression;
				// get the name
				name = idExp.getName();
				node = idExp;
			}

			if (name != null) {
				// get the binding
				IBinding binding = name.resolveBinding();
				// check whether this binding is part of the legacy library
//				checkBinding(name, binding, node);
				boolean inLibrary = checkBinding(binding);
				if (inLibrary)
					appendToLists(name, binding, node);
				
			}
			return PROCESS_CONTINUE;
		}

		/**
		 * This is to capture parameters in function definitions: e.g.,
		 * {@code void testParamDecl (XMLDocument doc, const char *filename)}
		 */
		@Override
		public int visit(IASTParameterDeclaration pDecl) {
			// parameters in function definitions: 
			//e.g., void testParamDecl (XMLDocument doc, const char *filename){
			if (!(pDecl.getDeclSpecifier() instanceof IASTNamedTypeSpecifier))
				return PROCESS_CONTINUE;

			// find bindings for the declaration specifier
			IASTName declSpecifierName = ((IASTNamedTypeSpecifier) pDecl.getDeclSpecifier()).getName();
			// get the binding
			IBinding binding = declSpecifierName.resolveBinding();

			// check whether this binding is part of the legacy library
//			checkBinding(declSpecifierName, binding, pDecl);
			boolean inLibrary = checkBinding(binding);
			if (inLibrary)
				appendToLists(declSpecifierName, binding, pDecl);

			return PROCESS_CONTINUE;
		}

		
		/**
		 * This is to capture: 
		 * (1) declarations (constructors),e.g.,  {@code XMLDocument xmlDoc}; 
		 * (2) overloaded function declarations, e.g,  
		 * {@code virtual bool VisitEnter(const XMLElement &element, const XMLAttribute *attr) override}
		 */
		@Override
		public int visit(IASTDeclaration decl) {
			if (!(decl instanceof IASTSimpleDeclaration))
				return PROCESS_CONTINUE;
			IASTSimpleDeclaration simpleDecl = (IASTSimpleDeclaration) decl;

			// check if there are any declarators: they should, otherwise there
			// would be a compilation error (e.g., int ;)
			if ((simpleDecl.getDeclSpecifier() instanceof IASTNamedTypeSpecifier)){				

				// find bindings for the declaration specifier
				IASTName declSpecifierName = ((IASTNamedTypeSpecifier) simpleDecl.getDeclSpecifier()).getName();
				IBinding binding = declSpecifierName.resolveBinding();
	
				// check whether this binding is part of the legacy library
//				checkBinding(declSpecifierName, binding, simpleDecl);
				boolean inLibrary = checkBinding(binding);
				if (inLibrary)
					appendToLists(declSpecifierName, binding, simpleDecl);

			}
			//TODO: Need to fix this --> method overloading not working properly yet...
			else if ( (simpleDecl.getDeclarators().length == 1) &&
					  (simpleDecl.getDeclarators()[0] instanceof ICPPASTFunctionDeclarator) ){
				ICPPASTFunctionDeclarator funDecl = (ICPPASTFunctionDeclarator) simpleDecl.getDeclarators()[0];
				if (funDecl.isOverride()){
					IASTName funDeclName = funDecl.getName();
					IBinding binding = funDeclName.resolveBinding();
					
					//check if it a method; since this methods overloads another function --> there exists a base class (superclass) 
					if (binding instanceof ICPPMethod){
						ICPPMethod methodBinding = (ICPPMethod)binding;
						
						//get superclasses
						ICPPBase baseClasses[] = methodBinding.getClassOwner().getBases();
						for (ICPPBase baseClass : baseClasses){
							if ( (baseClass.getBaseClass() instanceof ICPPClassType) && 
								 (checkBinding(baseClass.getBaseClass())) ){
								ICPPClassType clazz = (ICPPClassType)baseClass.getBaseClass();
								for (ICPPMethod clazzMethod : clazz.getAllDeclaredMethods()){
									if (checkOverloadedMethodsSignature(methodBinding, clazzMethod)){
										System.out.println("Original Method found\t" + simpleDecl.getRawSignature() +"\t"+ simpleDecl.getTranslationUnit().getOriginatingTranslationUnit());
										appendToLists(funDeclName, clazzMethod, simpleDecl);
									}
								}
							}
						}
					}
				}
			}
 
			return PROCESS_CONTINUE;
		}

		
		
		/**
		 * Check if a method actually matches the signature of this original (base) method
		 * To this end, it checks (1) name; (2) return type; (3) signature
		 * @param overloadMethod
		 * @param originalMethod
		 * @throws DOMException 
		 */
		private boolean checkOverloadedMethodsSignature (ICPPMethod originalMethod, ICPPMethod overloadMethod) {
			if (!originalMethod.getName().equals(overloadMethod.getName()))
				return false;
			if (originalMethod.getParameters().length != overloadMethod.getParameters().length)
				return false;
			ICPPParameter originalMethodParams[] = originalMethod.getParameters();
			ICPPParameter overloadMethodParams[] = overloadMethod.getParameters();
			for (int paramIndex=0; paramIndex<originalMethodParams.length; paramIndex++){
				ICPPParameter originalParam =  originalMethodParams[paramIndex];
				ICPPParameter overloadParam =  overloadMethodParams[paramIndex];
//				System.out.println(originalParam.getType().toString() +"\t"+ overloadParam.getType().toString());
					if (!originalParam.getType().isSameType(overloadParam.getType()))
							return false;
			}
			return true;
		}
		
		
		private boolean checkBinding(IBinding binding) {
			try {				
				//if it's a template or unknown binding
				if ( (binding==null) 
						|| (binding instanceof IProblemBinding) 
						|| (binding.getScope()==null) 
						|| (binding instanceof ICPPUnknownBinding) 
						){
//					System.out.println("NULL\t" + binding +"\t"+ binding.getClass().getSimpleName());
					return false;
				}
				//if it's a method/function
				else if (binding instanceof IFunction){ 
					boolean exists = false;
					projectIndex.acquireReadLock();
					IIndexName[] declDefs = projectIndex.findNames(binding, IIndex.FIND_DECLARATIONS_DEFINITIONS);
					for (IIndexName dd : declDefs){
						String path = dd.getFileLocation().getFileName();
						if (dd.isDeclaration() && RefactoringProject.OLD_HEADERS.contains(path)){
							System.out.print(binding.getName() + "\t"+path +"\t"+ dd.isBaseSpecifier() +"\t"+ dd.isDeclaration() +"\t"+ dd.isDefinition() +"\t");
							IASTNode node = refactoring.findNodeFromIndex(dd, true, IASTNode.class);
							if (node!=null)
								System.out.println(node. getPropertyInParent());
							exists = true;
							break;
						}
					}
					projectIndex.releaseReadLock();
					return exists;
				}
				//if it's a class/structure etc
				else if ( (binding instanceof ICompositeType) || (binding instanceof IEnumeration) ){
					boolean exists = false;
					projectIndex.acquireReadLock();
					IIndexName[] defs = projectIndex.findNames(binding, IIndex.FIND_DEFINITIONS);
					for (IIndexName dd : defs){
						String path = dd.getFileLocation().getFileName();
						if (dd.isDefinition() && RefactoringProject.OLD_HEADERS.contains(path)){
							System.out.print(binding.getName() + "\t"+path +"\t"+ dd.isBaseSpecifier() +"\t"+ dd.isDeclaration() +"\t"+ dd.isDefinition() +"\t");
							IASTNode node = refactoring.findNodeFromIndex(dd, true, IASTNode.class);
							if (node!=null)
								System.out.println(node. getPropertyInParent());
							exists = true;
							break;
						}
					}
					projectIndex.releaseReadLock();
					return exists;
				}
				else if (binding instanceof IVariable){
//					throw new IllegalArgumentException(binding.getName() +"\t"+ binding.getClass().getName());					
//					boolean exists = false;
					projectIndex.acquireReadLock();
					IIndexName[] defs = projectIndex.findNames(binding, IIndex.FIND_DECLARATIONS_DEFINITIONS);
					for (IIndexName dd : defs){
						String path = dd.getFileLocation().getFileName();
//						if (dd.isDefinition() && RefactoringProject.OLD_HEADERS.contains(path)){
						if (RefactoringProject.OLD_HEADERS.contains(path)){
							System.out.print(binding.getName() + "\t"+path +"\t"+ dd.isBaseSpecifier() +"\t"+ dd.isDeclaration() +"\t"+ dd.isDefinition() +"\t");
							IASTNode node = refactoring.findNodeFromIndex(dd, true, IASTNode.class);
							if (node!=null)
								System.out.println(node. getPropertyInParent());
//							exists = true;
//							break;
						}
					}
					projectIndex.releaseReadLock();
//					return exists;
					return false;
				}
//				else 
//					throw new IllegalArgumentException(binding.getName() +"\t"+ binding.getClass().getName());
				
//				IScope scope = binding.getScope();
//				while ((scope != null) 
//						&& (!(scope instanceof ICPPNamespaceScope))){ 
////						&& (!(scope instanceof ICPPClassScope))) {
//					scope = scope.getParent();
//				}
//
//				if ((scope != null) 
//						&&(scope.getScopeName() != null)
//						&& (RefactoringProject.OLD_NAMESPACES.contains(scope.getScopeName().toString())))
//					return true;

			} catch (DOMException | NullPointerException | CoreException | InterruptedException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException  e){
				System.err.println(e.getMessage());
			}
			
			return false;
		}
		
		
		private void appendToLists(IASTName name, IBinding binding, IASTNode node){
			boolean added = this.namesSet.add(name);
			this.namesList.add(name);
			if (added){
				this.bindingsSet.add(binding);
				this.nodesList.add(node);
			}
		}
	}
	
	
	private static class NamesSet extends ListSet<IASTName> {
		@Override
		public boolean contains(Object o) {
			for (IASTName e : storage) {
				if (e == o || e.getLastName().toString().equals(((IASTName) o).getLastName().toString())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean add(IASTName e) {
			if (contains(e)) {
				return false;
			} else {
				return storage.add(e.getLastName());
			}
		}
	}
	
	
	public static class BindingsSet extends ListSet<IBinding> {
		@Override
		public boolean contains(Object o) {
			for (IBinding e : storage) {
				if (e == o || e.getName().equals(((IBinding) o).getName().toString())) {
					return true;
				}
			}
			return false;
		}

		@Override
		public boolean add(IBinding e) {
			if (contains(e)) {
				return false;
			} else {
				return storage.add(e);
			}
		}
	}

	
	protected BindingsSet getBindings(){
		return this.bindingsSet;
	}

	
	protected LinkedHashMap<IASTName, String> getIncludeDirectives(){
		return this.includeDirectivesMap;
	}
	
	
	protected Map<ICPPClassType, List<ICPPMember>> getClassMembersMap(){
		return this.classMembersMap;
	}
	
	
	protected Collection<String> getTUsUsingLib (){
		Collection<String> tusLibSet = new ListSet<String>();
		for (ITranslationUnit tu : tusUsingLibList){
			tusLibSet.add(tu.getElementName());
		}
		return tusLibSet;
	}

	
	protected boolean tuExists (String tuName){
		for (ITranslationUnit tu : tusUsingLibList){
			if (tu.getElementName().equals(tuName.trim()))
				return true;
		}
		return false;
	}
}
