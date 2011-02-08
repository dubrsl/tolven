<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page isErrorPage="true"%>
<%@ page import="org.tolven.util.ExceptionFormatter" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Access Authorization Denied</title>
	</head>
	<body>
		<div class="errorPane">
			<h3>Access Authorization Denied</h3>
			<p><%=	ExceptionFormatter.toSimpleString(exception, "<br/>\n")%></p>
		</div>
	</body>
</html>
