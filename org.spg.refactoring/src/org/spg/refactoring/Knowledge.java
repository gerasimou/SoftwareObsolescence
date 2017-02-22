package org.spg.refactoring;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPMember;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;

public class Knowledge {

	/** Pairs of ITranslationUnit, IASTTranslationUnit **/
	private static HashMap<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<ITranslationUnit, IASTTranslationUnit>(); 
	
	/** Pairs of elements-potential name from standard C++ library that should be included using #include directives*/
	private static Map<IASTName, String> includeDirectivesMap;
	
	/** Map that between classes and members (functions, methods etc) */
	private static Map<ICPPClassType, List<ICPPMember>> classMembersMap;

	/** List keeping the translation units using the old library*/
	private static List<ITranslationUnit> tusUsingLibList;
	
	/** Project name*/
	private static String projectName;
	
	/** project */
	private static IProject  project  = null;
	private static ICProject cProject = null;

	/** project index */
	private static IIndex projectIndex = null;

	
	private Knowledge() {}

	
	/**
 	 * Parse project and for each translation unit generate its AST
 	 * @throws CoreException
	 */
 	protected synchronized static void parse(IProject project) throws CoreException {
 		//check if project parsing is required
 		if (!Knowledge.projectAnalysisNeded(project.getName()))
 			return;
 		
 		clearKnowledge();
		
		cProject		= CdtUtilities.getICProject(project);
		projectIndex	= CCorePlugin.getIndexManager().getIndex(cProject); 
		
		//find all project translation units
		MessageUtility.writeToConsole("Console", "Generating ASTs for selected project.");
		List<ITranslationUnit> tuList = CdtUtilities.getProjectTranslationUnits(cProject, null);

		// for each translation unit get its AST
		for (ITranslationUnit tu : tuList) {
			// get AST for that translation unit
			IASTTranslationUnit ast = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);

			// cache the tu & ast pair
			astCache.put(tu, ast);
		}

		//add ASTs to knowledge
		Knowledge.setASTCache(astCache);
		Knowledge.setProjectName(project.getName());
	}
 	
 	
	/**
	 * Given an index name and a set of class names, this function searches the 
	 * parent of the node with that name until it finds the parent which is instance of
	 * 
	 */
	@SuppressWarnings("rawtypes")
	protected synchronized static IASTNode findNodeFromIndex(IIndexName indexName, Class...classes){
		try {
			//find translation unit & corresponding ast, cache ast if necessary
			ITranslationUnit tu;
			tu = CdtUtilities.getTranslationUnitFromIndexName(indexName);
			IASTTranslationUnit ast = null;
			if (astCache.containsKey(tu)){
				ast = astCache.get(tu);
			}
			else{
				ast = tu.getAST(projectIndex, ITranslationUnit.AST_SKIP_INDEXED_HEADERS);
				astCache.put(tu, ast);
			}
			
			//find enumeration 
			IASTName name = (IASTName) ast.getNodeSelector(null).findEnclosingNode(indexName.getNodeOffset(), indexName.getNodeLength());
			IASTNode node = name;
			
			while ( (node != null) && !(nodeIsInstance(classes, node)) ){
				node =  node.getParent();
			}
			assert (nodeIsInstance(classes, node));
			return node;
		} 
		catch (CoreException e){
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * Checks if this node is instance of any of the given classes
	 * @param classes
	 * @param node
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private static boolean nodeIsInstance (Class [] classes, IASTNode node){
		for (Class clazz: classes){
			if (clazz.isInstance(node))
				return true;
		}
		return false;
	}
 	
 	
 	
	
	protected synchronized static void setASTCache(HashMap<ITranslationUnit, IASTTranslationUnit> cache){
		astCache = cache;
	}
	
	protected synchronized static HashMap<ITranslationUnit, IASTTranslationUnit> getASTCache(){
		return astCache;
	}

	protected synchronized static void setIncludeDirectivesMap(Map<IASTName, String> map){
		includeDirectivesMap = map;
	}

	protected synchronized static Map<IASTName, String>  getIncludeDirectivesMap(){
		return includeDirectivesMap;
	}

	protected synchronized static void setClassMemberMap(Map<ICPPClassType, List<ICPPMember>> map){
		classMembersMap = map;
	}

	protected synchronized static Map<ICPPClassType, List<ICPPMember>>   getClassMemberMap(){
		return classMembersMap;
	}
	
	protected synchronized static void setTUusingList(List<ITranslationUnit> list){
		tusUsingLibList = list;
	}

	protected synchronized static List<ITranslationUnit>   getTUusingList(){
		return tusUsingLibList;
	}
	
	protected synchronized static void setProjectName (String pName){
		projectName = pName.trim();
	}
	
	
	protected synchronized static boolean projectAnalysisNeded (String pName){
		if ( (classMembersMap == null) || (includeDirectivesMap == null) || 
			 (tusUsingLibList == null) || (!projectName.equals(pName.trim())))  
			return true;
		return false;
	}
	
	
	protected synchronized static boolean tuExists (String tuName){
		for (ITranslationUnit tu : tusUsingLibList){
			if (tu.getElementName().equals(tuName.trim()))
				return true;
		}
		return false;
	}
	
	
	protected synchronized static IIndex getProjectIndex(){
		return projectIndex;
	}
	
	private static void clearKnowledge(){
		if (astCache != null)
			astCache.clear();
		if (classMembersMap != null)
			classMembersMap.clear();
		if (includeDirectivesMap != null)
			includeDirectivesMap.clear();
		if (tusUsingLibList != null)
			tusUsingLibList.clear();
	}
	
}
