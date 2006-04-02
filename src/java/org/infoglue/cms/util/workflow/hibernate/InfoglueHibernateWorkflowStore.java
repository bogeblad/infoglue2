package org.infoglue.cms.util.workflow.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.util.InfogluePropertySet;
import org.infoglue.deliver.util.CacheController;

import net.sf.hibernate.SessionFactory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.cached.CachingPropertySet;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.spi.hibernate.HibernateWorkflowStore;


/**
 * Quickfix
 */
public class InfoglueHibernateWorkflowStore extends HibernateWorkflowStore {
	/**
	 * 
	 */
	private SessionFactory sessionFactory;
	
	/**
	 * 
	 */
    public InfoglueHibernateWorkflowStore() {}

	/**
	 * 
	 */
    public InfoglueHibernateWorkflowStore(SessionFactory sessionFactory) throws StoreException {
		super(sessionFactory);
		this.sessionFactory = sessionFactory;
    }

    

	/**
	 * 
	 */
    public void init(Map props) throws StoreException {
		sessionFactory = (SessionFactory) props.get("sessionFactory");
		super.init(props);
    }

	/**
	 * 
	 */
    public PropertySet getPropertySet(long entryId) {
        
    	String key = "psCache_" + entryId;
    	PropertySet ps = (PropertySet)CacheController.getCachedObject("propertySetCache", key);
    	
    	if(ps == null)
    	{
	    	HashMap args = new HashMap();
	        args.put("entityName", "OSWorkflowEntry");
	        args.put("entityId", new Long(entryId));
	
			InfoglueDefaultHibernateConfigurationProvider configurationProvider = new InfoglueDefaultHibernateConfigurationProvider();
	        configurationProvider.setSessionFactory(sessionFactory);
	
	        args.put("configurationProvider", configurationProvider);
	
	        
			ps = new CachingPropertySet();
			
			Map args2 = new HashMap();
			args2.put("PropertySet", PropertySetManager.getInstance("hibernate", args));
			args2.put("bulkload", new Boolean(true));
			
			ps.init(new HashMap(), args2);
			
			CacheController.cacheObject("propertySetCache", key, ps);
    		//logger.info("Caching propertySet for entry: " + entryId + ":" + ps);
			
	        ps = PropertySetManager.getInstance("hibernate", args);
    		
    	}
    	
        return ps;
    }
}
