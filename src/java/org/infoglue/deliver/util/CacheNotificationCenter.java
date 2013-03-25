/**
 * 
 */
package org.infoglue.deliver.util;

import java.util.ArrayList;
import java.util.List;

/**
 * The class acts a intermediate between the CahceController and CacheNotificationListener.
 * 
 * @author Erik Stenbäcka
 */
public class CacheNotificationCenter
{
	private static CacheNotificationCenter center;
	
	private List<CacheNotificationListener> listeners;
	
	public static CacheNotificationCenter getCenter()
	{
		if (center == null)
		{
			center = new CacheNotificationCenter();
		}
		return center;
	}

	private CacheNotificationCenter()
	{
		this.listeners = new ArrayList<CacheNotificationListener>();
	}
	
	public void addListener(CacheNotificationListener listener)
	{
		this.listeners.add(listener);
	}

	public void removeListener(CacheNotificationListener listener)
	{
		this.listeners.remove(listener);
	}

	public void notify(String className)
	{
		List<CacheNotificationListener> localListeners = new ArrayList<CacheNotificationListener>(this.listeners);
		for (CacheNotificationListener listener : localListeners)
		{
			listener.cacheChanged(className);
		}
	}

}
