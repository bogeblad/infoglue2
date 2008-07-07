<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html>
<head>
	<title>InfoGlue Error</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta http-equiv="Expires" content="Tue, 01 Jan 1980 1:00:00 GMT" />
	<meta http-equiv="Cache-Control" content="no-cache" />
	<meta http-equiv="Pragma" content="no-cache" />

	<style>
		
		body {
			color            : #123456;
			background-color : #FFFFFF;
			font-family      : verdana, arial, sans-serif;
			font-size        : 8pt;
		}
		
		.loginbox {
			background-color : #FFFFFF;
			border-style	 : solid;
			border-width	 : 1px;
			border-color	 : #cecbce;
		}

		.borderedCell {
			background-color : #FFFFFF;
			border-style	 : solid;
			border-width	 : 1px;
			border-color	 : #cecbce;
		}
		
		td {
			font-family      : verdana, arial, sans-serif;
			font-size        : 8pt;
			color		 	 : #333333;
		}
		
		.input {
			font-family      : verdana, arial, sans-serif;
			font-size        : 8pt;
			border-style	 : solid;
			border-width	 : 1px;	
			border-color     : #cecbce;
		}
		
		div.fullymarginalized {
			margin-top		 : 20%;
			margin-bottom	 : 20%;
			width 			 : 100%;	
		}

		td.headline {
			font-size        : 10pt;
			font-weight		 : bold;
		}

	</style> 
	
	<script type="text/javascript" language="Javascript">
	<!--
	
		function expandAndFocus()
		{
			document.inputForm.elements[0].focus();
		}			

		function toggleStacktrace()
		{
			var stacktrace = document.getElementById("stacktrace");
			if(stacktrace.style.display == "block")
				stacktrace.style.display = "none";
			else
				stacktrace.style.display = "block";
		}
		-->
	</script>

</head>

<body>

<div class="fullymarginalized">

<table class="loginbox" align="center" border="1" cellspacing="5" cellpadding="0">
<tr>
	<td valign="top" class="borderedCell"><img src="images/login.jpg" width="130" height="237"/></td>
	<td valign="top" class="borderedCell">
		<table align="center" border="0" cellspacing="0" cellpadding="0" width="200">
		<tr>
			<td colspan="2" style="background-image: url(images/errorHeaderBackground.gif); background-repeat: repeat-x;" align="center"><img src="images/error.jpg"></td>
		</tr>	
		<tr>
			<td colspan="2"><img src="images/trans.gif" width="1" height="20"></td>
		</tr>
		<tr>
			<td><img src="images/trans.gif" width="20" height="1"></td>
			<td>
				Page not Found or an error occurred. Either way we could not serve your request.<br/><br/>
				System message: <c:out value="${requestScope.error.message}"/>
			</td>
		</tr>
		</table>
	</td>
</tr>	
</table>

</div>
 
</body>
</html>

<%
System.out.println("Error.jsp called");
String errorUrl = (String)pageContext.getRequest().getAttribute("javax.servlet.error.request_uri");
System.out.println("Possible errorUrl:" + errorUrl);

Exception e = (Exception)pageContext.getRequest().getAttribute("error");
if(e != null)
{
  System.out.println("Error: " + e.getMessage());
  System.out.println(e.getStackTrace()[0].toString());
  System.out.println(e.getStackTrace()[1].toString());
  System.out.println(e.getStackTrace()[2].toString());
  System.out.println(e.getStackTrace()[3].toString());
  System.out.println(e.getStackTrace()[4].toString());
  System.out.println(e.getStackTrace()[5].toString());
}
%>