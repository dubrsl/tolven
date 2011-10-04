<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<% response.sendRedirect(request.getContextPath() + "/vestibule/selectAccount.jsf"); %>
<%--
The taglib directive below imports the JSTL library. If you uncomment it,
you must also add the JSTL library to the project. The Add Library... action
on Libraries node in Projects view can be used to add the JSTL 1.1 library.
--%>
<%--
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
--%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
   "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="Cache-Control" content="no-cache" />
        <meta http-equiv="expires" content="0">
    </head>
    <body>
    	This page only shows if redirects to /vestibule/selectAccount.jsf are not working correctly
    </body>
</html>
