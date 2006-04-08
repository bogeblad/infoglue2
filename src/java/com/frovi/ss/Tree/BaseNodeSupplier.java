package com.frovi.ss.Tree;

import java.util.Collection;

import org.exolab.castor.jdo.Database;
import org.infoglue.cms.exception.SystemException;

/**
 * BaseNodeSupplier.java
 * Created on 2002-sep-30 
 * @author Stefan Sik, ss@frovi.com 
 * ss
 */
public abstract class BaseNodeSupplier implements INodeSupplier
{
	private BaseNode rootNode = null;
	
	public boolean hasChildren()
	{
		return true;
	}

	public boolean hasChildren(Integer nodeId) throws SystemException, Exception
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

	
    /**
     * Begins a transaction on the named database
     */
     
    protected void beginTransaction(Database db) throws SystemException
    {
        try
        {
            db.begin();
        }
        catch(Exception e)
        {
			e.printStackTrace();
            throw new SystemException("An error occurred when we tried to begin an transaction. Reason:" + e.getMessage(), e);    
        }
    }
    
 
    
    /**
     * Ends a transaction on the named database
     */
     
    protected void commitTransaction(Database db) throws SystemException
    {
        try
        {
            db.commit();
            db.close();
        }
        catch(Exception e)
        {
			e.printStackTrace();
            throw new SystemException("An error occurred when we tried to commit an transaction. Reason:" + e.getMessage(), e);    
        }
    }
 
 
    /**
     * Rollbacks a transaction on the named database if there is an open transaction
     */
     
    protected void rollbackTransaction(Database db) throws SystemException
    {
        try
        {
        	if (db.isActive())
        	{
	            db.rollback();
				db.close();
        	}
        }
        catch(Exception e)
        {
			e.printStackTrace();
            throw new SystemException("An error occurred when we tried to rollback an transaction. Reason:" + e.getMessage(), e);    
        }
    }

}
