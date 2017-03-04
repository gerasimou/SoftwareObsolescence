/*******************************************************************************
 * Copyright (c) 2016 University of York.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Simos Gerasimou - initial API and implementation
 ******************************************************************************/
package org.spg.refactoring.old;

import java.util.HashMap;
import java.util.List;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.index.IIndex;
import org.eclipse.cdt.core.index.IIndexName;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.spg.refactoring.utilities.CdtUtilities;
import org.spg.refactoring.utilities.MessageUtility;


public class ProjectParser {

	/** project */
	private IProject  project  = null;
	private ICProject cProject = null;

	/** project index */
	private IIndex projectIndex = null;
	
	/** Pairs of ITranslationUnit, IASTTranslationUnit **/
	private HashMap<ITranslationUnit, IASTTranslationUnit> astCache = new HashMap<ITranslationUnit, IASTTranslationUnit>(); 

	
	
	/** Class constructor */
	public ProjectParser(IProject project) {
		//get existing cProject
		try {
			this.project		= project;
			this.cProject		= CdtUtilities.getICProject(project);
			this.projectIndex	= CCorePlugin.getIndexManager().getIndex(cProject);			
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}


	/**
 	 * Parse project and for each translation unit generate its AST
 	 * @throws CoreException
	 */
 	public void parse() throws CoreException {
		if (!astCache.isEmpty())
			return;			
		
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
	protected synchronized IASTNode findNodeFromIndex(IIndexName indexName, Class...classes){
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
	private boolean nodeIsInstance (Class [] classes, IASTNode node){
		for (Class clazz: classes){
			if (clazz.isInstance(node))
				return true;
		}
		return false;
	}
	
	
	protected IIndex getProjectIndex(){
		return this.projectIndex;
	}
	
//	protected HashMap<ITranslationUnit, IASTTranslationUnit> geASTCache(){
//		return astCache;
//	}
}
