/* ===============================================================================
 *
 * Part of the InfoGlue Content Management Platform (www.infoglue.org)
 *
 * ===============================================================================
 *
 *  Copyright (C)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2, as published by the
 * Free Software Foundation. See the file LICENSE.html for more information.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, including the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc. / 59 Temple
 * Place, Suite 330 / Boston, MA 02111-1307 / USA.
 *
 * ===============================================================================
 */

package org.infoglue.deliver.util;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

/**
 * Base class for asynchronous cache notification listeners. A cache notification listener needs to
 * finish quickly in order to not slow down the system. If a time-consuming operation needs to be performed
 * it should do so in a separate thread. That is purpose of the abstract class; provide a standardized
 * way to spawn a worker thread for a heavy operation.
 * 
 * Each received notification is put in a queue and pushed in order of arrival. The abstract method 
 * {@link #handleNotification(String)} is called for each notification.
 * 
 * @author Erik Stenbäcka
 */
public abstract class ThreadedQueueCacheNotificationListener implements CacheNotificationListener
{
	private static final Logger logger = Logger.getLogger(ThreadedQueueCacheNotificationListener.class);
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
					try
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
								try
								{
									handleNotification(notification);
								}
								catch (Throwable tr)
								{
									logger.error("An error occured when executing handleNotification for notification: " +  notification);
									logger.warn("An error occured when executing handleNotification for notification", tr);
								}
							}
						}
						while(notification != null);
					}
					finally
					{
						// Safety call. Should never have to set false here.
						isRunning.set(false);
					}
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

	/**
	 * Called from a worker thread after a cache eviction has occurred. Since the notifications are
	 * are executed in order and only one at a time it is not guaranteed that this method will be called
	 * right after the eviction occurred. The lateness of the execution depends of the size of the queue
	 * and the execution time of each execution of this method.
	 * @param className The cache class that was evicted.
	 */
	protected abstract void handleNotification(String className);

}
