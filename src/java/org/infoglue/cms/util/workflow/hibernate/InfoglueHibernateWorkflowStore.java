package org.infoglue.cms.util.workflow.hibernate;

import java.util.HashMap;
import java.util.Map;

import net.sf.hibernate.SessionFactory;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
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
        HashMap args = new HashMap();
        args.put("entityName", "OSWorkflowEntry");
        args.put("entityId", new Long(entryId));

		InfoglueDefaultHibernateConfigurationProvider configurationProvider = new InfoglueDefaultHibernateConfigurationProvider();
        configurationProvider.setSessionFactory(sessionFactory);

        args.put("configurationProvider", configurationProvider);

        return PropertySetManager.getInstance("hibernate", args);
    }
}
