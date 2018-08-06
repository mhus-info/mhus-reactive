package de.mhus.cherry.reactive.model.ui;

public interface IModel {

	/**
	 * Return the predecessor if exists.
	 * @return Description or null
	 */
	INodeDescription getPredecessor();
	
	/**
	 * Return possible outputs
	 * @return Descriptions
	 */
	INodeDescription[] getOutputs();
	
	/**
	 * Return information about the current node
	 * @return Description
	 */
	INodeDescription getNode();
	
}
