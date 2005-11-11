package org.infoglue.deliver.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.lang.management.ThreadInfo;
import java.lang.reflect.Method;
 
public class ThreadMX
{
    private static int STACK_TRACE_DEPTH = 20;
  
    public static String stackInfo(String separator)
    {
	StringBuffer buf = new StringBuffer();
	ThreadMXBean bean = ManagementFactory.getThreadMXBean();
	Class bean_class = ThreadMXBean.class;
 
	buf.append("live threads: ");
	buf.append(call(bean, bean_class, "getThreadCount"));
	buf.append(separator);
 
	buf.append("peak live threads: ");
	buf.append(call(bean, bean_class, "getPeakThreadCount"));
	buf.append(separator);
 
	buf.append("total started threads: ");
	buf.append(call(bean, bean_class, "getTotalStartedThreadCount"));
	buf.append(separator);
 
	buf.append("live daemon threads: ");
	buf.append(call(bean, bean_class, "getDaemonThreadCount"));
	buf.append(separator);
 
	buf.append("contention monitoring supported: ");
	buf.append(call(bean, bean_class, "isThreadContentionMonitoringSupported"));
	buf.append(separator);
 
	buf.append("contention monitoring enabled: ");
	buf.append(call(bean, bean_class, "isThreadContentionMonitoringEnabled"));
	buf.append(separator);
 
	buf.append("current thread cpu time: ");
	buf.append(call(bean, bean_class, "getCurrentThreadCpuTime"));
	buf.append(separator);
 
	buf.append("current thread user time: ");
	buf.append(call(bean, bean_class, "getCurrentThreadUserTime"));
	buf.append(separator);
 
	buf.append("thread cpu time supported: ");
	buf.append(call(bean, bean_class, "isThreadCpuTimeSupported"));
	buf.append(separator);
 
	buf.append("current thread cpu time supported: ");
	buf.append(call(bean, bean_class, "isCurrentThreadCpuTimeSupported"));
	buf.append(separator);
 
	buf.append("thread cpu time enabled: ");
	buf.append(call(bean, bean_class, "isThreadCpuTimeEnabled"));
	buf.append(separator);
 
	buf.append("deadlocked threads: ");
	try {
	    long ids[] = bean.findMonitorDeadlockedThreads();
	    if (ids == null || ids.length == 0) {
		buf.append("none");
	    } else {
		for (int n = 0; n < ids.length; n++) {
		    if (n != 0)
			buf.append(", ");
		    buf.append(ids[n]);
		}
	    }
	} catch (Exception e) {
	    buf.append("[call to findMonitorDeadlockedThreads failed: " + e + "]");
	}
	buf.append(separator);
 
	buf.append("thread stack dumps: ");
	dumpThreads(buf, bean, separator);
 
	return buf.toString();
    }
 
    private static void dumpThreads(StringBuffer buf, ThreadMXBean bean, String separator)
    {
	try {
	    long ids[] = bean.getAllThreadIds();
	    if (ids == null) {
		buf.append("null thread id array?!");
		return;
	    }
 
	    buf.append(ids.length + " threads:");
	    buf.append(separator);
	    ThreadInfo info[] = bean.getThreadInfo(ids, STACK_TRACE_DEPTH);
	    if (info == null) {
		buf.append("null thread info array?!");
		return;
	    }
 
	    Class info_class = ThreadInfo.class;
 
	    for (int n = 0; n < info.length; n++) {
		if (n != 0)
		    buf.append(separator);
 
		ThreadInfo thread = info[n];
		if (thread == null) {
		    buf.append("null thread in info[" + n + "]?!");
		    continue;
		}
		buf.append("thread ");
		buf.append(call(thread, info_class, "getThreadId"));
 
		buf.append(" \"");
		buf.append(call(thread, info_class, "getThreadName"));
		buf.append("\"");
 
		buf.append(": state ");
		buf.append(call(thread, info_class, "getThreadState"));
 
		buf.append(", blocked time ");
		buf.append(call(thread, info_class, "getBlockedTime"));
 
		buf.append(", blocked count ");
		buf.append(call(thread, info_class, "getBlockedCount"));
 
		buf.append(", waited time ");
		buf.append(call(thread, info_class, "getWaitedTime"));
 
		buf.append(", waited count ");
		buf.append(call(thread, info_class, "getWaitedCount"));
 
		buf.append(", lock name ");
		buf.append(call(thread, info_class, "getLockName"));
 
		buf.append(", lock owner ");
		buf.append(call(thread, info_class, "getLockOwnerId"));
 
		buf.append(", lock owner name ");
		buf.append(call(thread, info_class, "getLockOwnerName"));
 
		buf.append(", suspended ");
		buf.append(call(thread, info_class, "isSuspended"));
 
		buf.append(", in native ");
		buf.append(call(thread, info_class, "isInNative"));
 
		buf.append(separator);
		dumpStacks(buf, thread, separator);
	    }
 
	} catch (Exception e) {
	    buf.append("[error getting thread data: " + e + "]");
	}
    }
 
    private static void dumpStacks(StringBuffer buf, ThreadInfo thread, String separator)
    {
	try {
	    StackTraceElement stack[] = thread.getStackTrace();
	    if (stack == null || stack.length == 0) {
		buf.append("\tno stack trace available");
		return;
	    }
	    for (int n = 0; n < stack.length; n++) {
		if (n != 0)
		    buf.append(separator);
 
		StackTraceElement frame = stack[n];
		if (frame == null) {
		    buf.append("\tnull stack frame");
		    continue;
		}
		buf.append("\t");
		buf.append(frame.toString()); 
	    }
	} catch (Exception e) {
	    buf.append("[error getting thread stack trace: " + e + "]");
	}
    }
 
    private static String call(Object thiz, Class clazz, String method_name)
    {
	try {
	    Method method = clazz.getMethod(method_name, (Class[]) null);
	    Object result = method.invoke(thiz, (Object[]) null);
 
	    if (result == null)
		return "[null]";
 
	    String result_str = result.toString();
	    if (result_str == null) // Paranoia
		return "[null toString()]";
	    return result_str;
 
	} catch (Exception e) {
	    return "[call to " + method_name + " failed: " + e + "]";
	}
    }
}
