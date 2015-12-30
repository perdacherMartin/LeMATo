package lemato.clustering

import lemato.nopersistence.SignificanceBucket
import groovy.json.JsonBuilder

class DendrogramNode {

	DendrogramNode parent = null 
	Long docCount
	Double score
	String keyword	
	List<DendrogramNode> children = []

	/**
	 * regular constructor
	 * @param  bucket 
	 */
	public DendrogramNode(SignificanceBucket bucket){
		this.keyword = bucket.key
		this.docCount = bucket.docCount
		this.score = bucket.score
	}

	/**
	 * constructor with two existing childs, to build up the dendrogram tree
	 * @param  childA 
	 * @param  childB 
	 */
	public DendrogramNode(DendrogramNode childA, DendrogramNode childB){		
		// use properties of the most frequent term in parent node
		if ( childA.docCount > childB.docCount ){
			this.keyword = childA.keyword
			this.docCount = childA.docCount
			this.score = childA.score
		}else{
			this.keyword = childB.keyword
			this.docCount = childB.docCount
			this.score = childB.score
		}

		this.append(childA)
		this.append(childB)
	}

	/**
	 * [append a child to the tree structure]
	 * @param  child [description]
	 * @return       [description]
	 */
	public Boolean append(DendrogramNode child){
		children << child
		child.setParent(this)
	}

	/**
	 * [build up json structure for tree. similar to http://bl.ocks.org/robschmuecker/7880033#flare.json]
	 * @return [Json string]
	 */
	public String toJson(){
		StringBuilder result = new StringBuilder("{\"name\": \"${keyword}\"")

		if ( children.size() > 0 ){
			result.append(", ")
			result.append("\"children\": [")

			children.eachWithIndex{ child, i ->
				if ( i > 0 ){
					result.append(", ")
				}
				result.append( child.toJson() ) 
			}

			result.append("]")
		}else{
			result.append(",\"score\": \"${this.score}\",")
			result.append("\"docCount\": \"${this.docCount}\"")
		}
		result.append("}")
		return result.toString()
	}

	public Integer getChildrenSum(){
		Integer sum = 0
		this.children.each { child ->
			sum = sum + child.getChildrenSum()
		}

		return sum + this.children.size()
	}

	public String toString() {
		return "name: ${this.keyword} children: ${this.getChildrenSum()}\n"
	}

}