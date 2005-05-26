package org.infoglue.cms.util.workflow.hibernate;

import java.util.Map;

import com.opensymphony.module.propertyset.PropertyException;
import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.hibernate.HibernateConfigurationProvider;
import com.opensymphony.module.propertyset.hibernate.HibernatePropertySet;
import com.opensymphony.module.propertyset.hibernate.PropertySetItem;

/**
 * Quickfix
 */
public class InfoGlueHibernatePropertySet extends HibernatePropertySet {
	private static final String CONFIGURATION_PROVIDER_ARGUMENT = "configurationProvider";
	
	
    private HibernateConfigurationProvider configProvider;
    private Long entityId;
    private String entityName;

	
	/**
	 * 
	 */
	public InfoGlueHibernatePropertySet() {
		super();
	}

	/**
	 * 
	 */
    public boolean supportsType(int type) {
		return (type == PropertySet.DATA) ? true : super.supportsType(type);
    }

	/**
	 * 
	 */
    public void init(Map config, Map args) {
        super.init(config, args);
        this.configProvider = (HibernateConfigurationProvider) args.get(CONFIGURATION_PROVIDER_ARGUMENT);
        this.entityId       = (Long) args.get("entityId");
        this.entityName     = (String) args.get("entityName");
    }
	
	/**
	 * 
	 */
    private PropertySetItem findByKey(String key) throws PropertyException {
        return configProvider.getPropertySetDAO().findByKey(entityName, entityId, key);
    }

	/**
	 * 
	 */
    protected void setImpl(int type, String key, Object value) throws PropertyException {
		if(type != PropertySet.DATA)
			super.setImpl(type, key, value);
		else
			setDataImpl(type, key, value);
    }

	/**
	 * 
	 */
    protected void setDataImpl(int type, String key, Object value) throws PropertyException {
		PropertySetItem item = configProvider.getPropertySetDAO().findByKey(entityName, entityId, key);
        boolean update = (item == null) ? false : true;
        if (item == null)
            item = configProvider.getPropertySetDAO().create(entityName, entityId.longValue(), key);
        else if (item.getType() != type)
            throw new PropertyException("Existing key '" + key + "' does not have matching type of " + type);
		
        System.out.println("Class value:" + value.getClass().getName());
        System.out.println("Class item:" + item.getClass().getName());
		((InfogluePropertySetItem) item).setDataVal(((com.opensymphony.util.Data) value).getBytes());
		
        item.setType(type);
        configProvider.getPropertySetDAO().setImpl(item, update);
    }

	/**
	 * 
	 */
    protected Object get(int type, String key) throws PropertyException {
		if(type != PropertySet.DATA)
			return super.get(type, key);
		return getData(type, key);
    }
	
	/**
	 * 
	 */
	private Object getData(int type, String key) throws PropertyException {
        final PropertySetItem item = findByKey(key);
        if (item == null)
            return null;
        if (item.getType() != type)
            throw new PropertyException("key '" + key + "' does not have matching type of " + type);
		return new com.opensymphony.util.Data(((InfogluePropertySetItem) item).getDataVal());
    }
}