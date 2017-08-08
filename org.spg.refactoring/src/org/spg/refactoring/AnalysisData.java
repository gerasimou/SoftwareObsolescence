package org.spg.refactoring;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.cdt.core.dom.ast.ASTNodeProperty;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IBinding;

/** Class that keeps all analysis data
 *  It can be extended accordingly
 * @author sgerasimou
 *
 */
public class AnalysisData {
	/** List that keeps analysis data */
	List<Datum> dataList;
	
	
	/**
	 * Class constructor
	 */
	public AnalysisData() {
		this.dataList = new ArrayList<AnalysisData.Datum>();
	}
	
	
	/**
	 * Add a new datum to analysisData list
	 * @param fLocation
	 * @param startingLine
	 * @param endingLine
	 * @param prop
	 * @param sign
	 */
	protected void add(IASTNode node, IBinding binding){
		dataList.add(new Datum(node, binding));
		System.out.println(dataList.get(dataList.size()-1).toString());
	}

	
	/**
	 * Get the specified by index i datum
	 * @param i
	 * @return
	 */
	protected Datum getDatum(int i){
		return this.dataList.get(i);
	}
	
	
	protected List<Datum> getDataList(){
		return this.dataList;
	}
	
	protected void clear(){
		this.dataList.clear();
	}
	
	protected class Datum{
		/** AST Node*/
		private IASTNode node;
		
		/** Binding for this node*/
		private IBinding binding; 
		
		/** File where a node is located */
		private String fileLocation;
		
		/** Command starting line number*/
		private int startingLineNumber;
		
		/** Command ending line number*/
		private int endingLineNumber;
		
		/** Node property*/
		private ASTNodeProperty propertyInParent;
		
		/** Command signature*/
		private String signature;
		
		
		/**
		 * Class constructor: create a new datum object
		 * @param fLocation
		 * @param startingLine
		 * @param endingLine
		 * @param prop
		 * @param sign
		 */
		protected Datum(IASTNode node, IBinding binding){
			this.node				= node;
			this.binding			= binding;
			this.fileLocation 		= node.getFileLocation().getFileName();
			this.startingLineNumber = node.getFileLocation().getStartingLineNumber();
			this.endingLineNumber	= node.getFileLocation().getEndingLineNumber();
			this.propertyInParent	= node.getPropertyInParent();
			this.signature			= node.getRawSignature();
		}
		
		
		public String toString(){
			return fileLocation +" (l:"+ startingLineNumber +"-"+ endingLineNumber +")\n\t"+ propertyInParent +"\n\t"+ signature +"\n";
		}
	}
	
}
