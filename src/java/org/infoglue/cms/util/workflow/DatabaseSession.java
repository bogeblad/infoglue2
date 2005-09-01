package org.infoglue.cms.util.workflow;

import org.apache.log4j.Logger;
import org.exolab.castor.jdo.Database;
import org.infoglue.cms.controllers.kernel.impl.simple.CastorDatabaseService;

import com.opensymphony.workflow.WorkflowException;

public class DatabaseSession {
	/**
	 * 
	 */
    private final static Logger logger = Logger.getLogger(DatabaseSession.class.getName());

	/**
	 * 
	 */
	private Database db;
	
	/**
	 * 
	 */
	private boolean rollbackOnly;
	
	
	
	/**
	 * 
	 */
	public DatabaseSession() { super(); }

	/**
	 * 
	 */
	public void setRollbackOnly() { rollbackOnly = true; }
	
	/**
	 * 
	 */
	public Database getDB() throws WorkflowException {
		if(db == null) {
		    try {
				db = CastorDatabaseService.getDatabase();
				db.begin();
		    } catch(Exception e) {
				logger.error("Unable to create database", e);
				throw new WorkflowException(e);
		    }
		}
		return db;
	}
	
	/**
	 * 
	 */
	public void releaseDB() throws WorkflowException {
		logger.debug("WorkflowDatabase.releaseDB()");
		if(db != null && db.isActive()) {
			if(rollbackOnly)
				rollback();
			else
				commit();
		}
	}
	
	/**
	 * 
	 */
	private void rollback() throws WorkflowException {
		logger.debug("WorkflowDatabase.rollback()");
		try {
			db.rollback();
			db.close();
		} catch(Exception e) {
			logger.error("Unable to rollback database; bailing out.", e);
			throw new WorkflowException(e);
		}
	}

	/**
	 * 
	 */
	private void commit() throws WorkflowException {
		logger.debug("WorkflowDatabase.commit()");
		try {
			db.commit();
			db.close();
		} catch(Exception e) {
			try {
				logger.error("Unable to commit/close database; trying to rollback.", e);
				db.rollback();
				db.close();
				throw e;
			} catch(Exception ee) {
				logger.error("Unable to rollback database; bailing out.", ee);
				throw new WorkflowException(ee);
			}
		}
	}
}
