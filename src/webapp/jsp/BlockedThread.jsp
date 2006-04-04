<%@ page import="java.lang.management.*" %>
<%@ page import="java.util.*" %>
<%@ page import="org.infoglue.cms.controllers.kernel.impl.simple.ServerNodeController" %>

<%
    if(!ServerNodeController.getController().getIsIPAllowed(request))
    {
        response.setContentType("text/plain");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        out.println("You have no access to this view - talk to your administrator if you should.");
    }
	else
	{
		try
		{
			String action = request.getParameter("action");
			String threadId = request.getParameter("threadId");
			if(action != null && threadId != null && action.equals("kill"))
			{
		    	ThreadGroup tg = Thread.currentThread().getThreadGroup();
			    int n = tg.activeCount();
		        Thread[] threadArray = new Thread[n];
		        n = tg.enumerate(threadArray, false);
		        for (int i=0; i<n; i++) 
		        {
		           	String currentThreadId = "" + threadArray[i].getId();
		           	if(currentThreadId.equals(threadId))
		           	{
		        	   out.print("Killing " + currentThreadId);
		        	   threadArray[i].stop();
		        	}
		        }  
				response.sendRedirect("BlockedThread.jsp");
				return;
			}
		}
		catch(Exception e)
		{
			out.print("Error:" + e.getMessage());
		}
		
	    ThreadMXBean t = ManagementFactory.getThreadMXBean();
	%>
		<html>
		<head>
		  <title>JVM Blocked Thread Monitor</title>
		</head>
		<body>
	    <table border="0" width="100%">
	    <tr><td align="center"><h3>Thread MXBean</h3></td></tr>
	
	    <tr><td align="center"><h4>All suspicious Threads</h4></td></tr>
	<%
	    long threads[] = t.getAllThreadIds();
	    ThreadInfo[] tinfo = t.getThreadInfo(threads, 15);
	
	    for (int i=0; i<tinfo.length; i++)
	    {
			ThreadInfo e = tinfo[i];
	
	        StackTraceElement[] el = e.getStackTrace();
	        
	        String stackString = "";
	        if (el != null && el.length != 0)
	        {
	            for (int n = 0; n < el.length; n++)
	            {
	            	StackTraceElement frame = el[n];
	            	if (frame == null)
	            		stackString += "&nbsp;&nbsp;&nbsp;&nbsp;null stack frame" + "<br/>";
	            	else	
	                	stackString += "&nbsp;&nbsp;&nbsp;&nbsp;null stack frame" + frame.toString() + "<br/>";
				}                    
	       	}
			
			long threadId = threads[i];
	    	long cpuTime = t.getThreadCpuTime(threadId) / 10000000;
	    	long userTime = t.getThreadUserTime(threadId) / 10000000;
	            
	        long blockedTime = e.getBlockedTime();
	        long waitedTime = e.getWaitedTime();
			
			//Only list infoglue threads except redirect filter.			
			if(stackString.indexOf("org.infoglue") > -1 && stackString.indexOf("RedirectFilter.java") == -1)
			{
	        %>
	        <tr><td align="center">
	        <%
	        out.print("<br/>" + e.getThreadName() + "<br/>" + " " + " Thread id = " + e.getThreadId() + " " + e.getThreadState() + "(" + cpuTime + ":" + userTime + ":" + blockedTime + ":" + waitedTime + ")");
	        out.print("<br/><a href=\"BlockedThread.jsp?action=kill&threadId=" + threadId + "\">Kill thread</a><br/>");
	        out.print(stackString);
	        %>
	        </td></tr>
	        <%
	        }
	    }
		%>
		</table>
		</body>
		</html>
		<%
	}
%>                        