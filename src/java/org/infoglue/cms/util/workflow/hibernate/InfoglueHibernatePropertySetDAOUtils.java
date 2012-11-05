package org.infoglue.cms.util.workflow.hibernate;

import org.infoglue.deliver.util.Timer;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAOUtils;
import com.opensymphony.module.propertyset.hibernate.PropertySetItem;


/**
 * Quickfix
 */
public class InfoglueHibernatePropertySetDAOUtils extends HibernatePropertySetDAOUtils {
	/**
	 * 
	 */
    public static PropertySetItem getItem(Session session, String entityName, Long entityId, String key) throws HibernateException {
    	//Timer t = new Timer();
    	PropertySetItem psItem = (PropertySetItem) session.load(InfogluePropertySetItemImpl.class, new InfogluePropertySetItemImpl(entityName, entityId.longValue(), key));
    	//t.printElapsedTime("Getting:" + entityName + ":" + entityId + ":" + key);
        return psItem;
    }
}
