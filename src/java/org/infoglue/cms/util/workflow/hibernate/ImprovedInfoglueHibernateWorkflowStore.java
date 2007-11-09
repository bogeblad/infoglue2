package org.infoglue.cms.util.workflow.hibernate;

import java.util.HashMap;
import java.util.Map;

import org.infoglue.cms.applications.workflowtool.util.InfogluePropertySet;
import org.infoglue.deliver.util.CacheController;
import org.infoglue.deliver.util.Timer;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.propertyset.cached.CachingPropertySet;
import com.opensymphony.workflow.StoreException;
import com.opensymphony.workflow.spi.hibernate.AbstractHibernateWorkflowStore;
import com.opensymphony.workflow.spi.hibernate.HibernateWorkflowStore;
import com.opensymphony.workflow.spi.hibernate.NewHibernateWorkflowStore;
import com.opensymphony.workflow.util.PropertySetDelegate;


/**
 * This is the new Improved Hibernate workflow store. Works much better with session handling.
 */
public class ImprovedInfoglueHibernateWorkflowStore extends AbstractHibernateWorkflowStore 
{
	private Session session = null;
	private SessionFactory sessionFactory = null;
	
	private Session getSession()
	{
		return session;
	}

	private void setSession(Session session)
	{
		this.session = session;
	}

	private SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}

	private void setSessionFactory(SessionFactory sessionFactory)
	{
		this.sessionFactory = sessionFactory;
	}

    public ImprovedInfoglueHibernateWorkflowStore() 
    {
        super();
		//System.out.println("Constructor 1 ImprovedInfoglueHibernateWorkflowStore...");
    }

    // Now session management is delegated to user
    
    public void init(Map props) throws StoreException 
    {
    	//System.out.println("props in init: " + props + " in " + this.hashCode());
    	
    	setSessionFactory((SessionFactory) props.get("sessionFactory"));
    	setSession((Session) props.get("session"));

        setPropertySetDelegate((PropertySetDelegate) props.get("propertySetDelegate"));
    }

    protected Object execute(InternalCallback action) throws StoreException 
    {
    	try 
        {
            return action.doInHibernate(getSession());
        } 
        catch (HibernateException e) 
        {
        	//System.out.println("Session in error:" + getSession().hashCode());
        	//e.printStackTrace();
        	throw new StoreException(e);
        }
    }

	/**
	 * 
	 */
    //public InfoglueHibernateWorkflowStore() {}

	/**
	 * 
	 */
    public ImprovedInfoglueHibernateWorkflowStore(SessionFactory sessionFactory) throws StoreException 
    {
		//super(sessionFactory);
		super();
		this.setSessionFactory(sessionFactory);
		//System.out.println("Constructor ImprovedInfoglueHibernateWorkflowStore...");
    }

    

	/**
	 * 
	 */
    /*
    public void init(Map props) throws StoreException 
    {
    	sessionFactory = (SessionFactory) props.get("sessionFactory");
		super.init(props);
    }
    */

	/**
	 * 
	 */
    public PropertySet getPropertySet(long entryId) 
    {
    	//if(true)
    	//	return null;
    	
    	String key = "psCache_" + entryId;
    	PropertySet ps = (PropertySet)CacheController.getCachedObject("propertySetCache", key);
    	
    	if(ps == null)
    	{
    		try
    		{
		    	HashMap args = new HashMap();
		        args.put("entityName", "OSWorkflowEntry");
		        args.put("entityId", new Long(entryId));
		
				InfoglueDefaultHibernateConfigurationProvider configurationProvider = new InfoglueDefaultHibernateConfigurationProvider();
		        configurationProvider.setSessionFactory(getSessionFactory());
		
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
    		catch(Exception e)
    		{
    			e.printStackTrace();
    			
    			try
    			{
					System.out.println("\n\nRestoring the session factory....");
	    			//sessionFactory.close();
					this.setSessionFactory(new Configuration().configure().buildSessionFactory());
					
					InfoglueDefaultHibernateConfigurationProvider configurationProvider = new InfoglueDefaultHibernateConfigurationProvider();
			        configurationProvider.setSessionFactory(this.getSessionFactory());
			
			        Map args = new HashMap();
			        args.put("configurationProvider", configurationProvider);
			        
					ps = new CachingPropertySet();
					
					Map args2 = new HashMap();
					args2.put("PropertySet", PropertySetManager.getInstance("hibernate", args));
					args2.put("bulkload", new Boolean(true));
					
					ps.init(new HashMap(), args2);
    			}
				catch (HibernateException he)
				{
					he.printStackTrace();
				}
    		}
    	}
    	
        return ps;
    }
     
}
