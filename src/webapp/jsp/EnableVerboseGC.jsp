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

        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
        if(request.getParameter("verbose").equalsIgnoreCase("true"))
		{
	        mbean.setVerbose(true);
		}
		else
		{
        	mbean.setVerbose(false);
		}

%>
<html>
<head>
  <title>Verbose changed</title>
</head>
<body>
        <h1>Verbose changed</h1>
        Verbose: <%=  mbean.isVerbose() %>
</body>
</html>
<%
}
%>
