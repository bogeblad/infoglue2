package com.frovi.ss.Tree;

import java.util.Collection;

/**
 * BaseNodeSupplier.java
 * Created on 2002-sep-30 
 * @author Stefan Sik, ss@frovi.com 
 * ss
 */
public abstract class BaseNodeSupplier implements INodeSupplier
{
	private Integer defaultRootNodeId = new Integer(0);
	private BaseNode rootNode = null;
	
	public boolean hasChildren()
	{
		return true;
	}

	public boolean hasChildren(Integer nodeId)
	{
		// Base functionallity, typically this method is overridden
		// for performance reasons
		Collection tmp = getChildContainerNodes(nodeId);
		Collection tmp2 = getChildLeafNodes(nodeId);
		return (tmp.size() + tmp2.size()) > 0;
	}


	/**
	 * Sets the rootNode.
	 * @param rootNode The rootNode to set
	 */
	protected void setRootNode(BaseNode rootNode)
	{
		this.rootNode = rootNode;
	}

	/**
	 * Returns the rootNode.
	 * @return BaseNode
	 */
	public BaseNode getRootNode()
	{
		return rootNode;
	}

}
