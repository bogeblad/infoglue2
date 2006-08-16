<%@ page import="java.lang.management.*" %>
<%@ page import="java.util.*" %>

<%
	System.out.println("reached the protected resource protectedRedirect.jsp");
	String returnAddress = request.getParameter("returnAddress");
    System.out.println("returnAddress:" + returnAddress);
	response.sendRedirect(returnAddress);
%>                        