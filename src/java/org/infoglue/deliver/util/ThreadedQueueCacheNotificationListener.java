/**
 * 
 */
package org.infoglue.deliver.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Erik Stenbäcka
 */
public abstract class ThreadedQueueCacheNotificationListener implements CacheNotificationListener
{
	private AtomicBoolean isRunning;
	private Queue<String> notifications;

	public ThreadedQueueCacheNotificationListener()
	{
		this.isRunning = new AtomicBoolean(false);
		this.notifications = new LinkedList<String>();
	}
	
	private void processNotification()
	{
		if (isRunning.compareAndSet(false, true))
		{
			new Thread() {

				@Override
				public void run()
				{
					String notification = null;
					do
					{
						synchronized (notifications)
						{
							notification = notifications.poll();
							if (notification == null)
							{
								isRunning.set(false);
							}
						}

						if (notification != null)
						{
							handleNotification(notification);
						}
					}
					while(notification != null);
				}
			}.start();
		}
	}

	private void queueNotification(String className)
	{
		synchronized (notifications)
		{
			notifications.add(className);
		}
		processNotification();
	}

	@Override
	public void cacheChanged(String className)
	{
		queueNotification(className);
	}

	protected abstract void handleNotification(String className);

}
