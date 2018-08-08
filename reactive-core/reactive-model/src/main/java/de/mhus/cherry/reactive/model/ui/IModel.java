package de.mhus.cherry.reactive.model.ui;

import java.util.UUID;

import de.mhus.cherry.reactive.model.engine.EngineMessage;

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
	
	/**
	 * Return the node id if exists
	 * @return The id or null
	 */
	UUID getNodeId();
	
	/**
	 * Return the runtime messages if exists
	 * @return The messages or null
	 */
	EngineMessage[] getRuntimeMessages();
	
}
