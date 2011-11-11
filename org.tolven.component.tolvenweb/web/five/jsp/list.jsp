<?xml version="1.0" encoding="UTF-8" ?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0"
		xmlns:c="http://java.sun.com/jsp/jstl/core">
    <jsp:directive.page language="java"
        contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" />
	
	<jsp:useBean id="tolBean" scope="request" class="org.tolven.web.TolvenJSPBean">
		<jsp:setProperty name="tolBean" property="element" /> 
	    <jsp:scriptlet>	
		    tolBean.setAccountUser(org.tolven.core.TolvenRequest.getInstance().getAccountUser());
		    tolBean.setTolvenNow(org.tolven.core.TolvenRequest.getInstance().getNow());
	    </jsp:scriptlet>
	</jsp:useBean>

	<div style="padding:1em">
		<p>The following is a simple table displaying Tolven MenuData. It does not have all the bells and 
		whistles that the are used in the primary list but it does illustrate how the backend works.</p>
		<table style="border:black thin solid">
			<thead>
				<tr>
					<th style="border:black thin solid;padding:0.25em">date01</th>
					<th style="border:black thin solid;padding:0.25em">string01</th>
					<th style="border:black thin solid;padding:0.25em">string02</th>
					<th style="border:black thin solid;padding:0.25em">string03</th>
					<th style="border:black thin solid;padding:0.25em">string04</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="md" begin="0" items="${tolBean.menuDataList}"> 
					<tr>
						<td style="border:black thin solid;padding:0.25em"><c:out value="${md.date01}"/></td>
						<td style="border:black thin solid;padding:0.25em"><c:out value="${md.string01}"/></td>
						<td style="border:black thin solid;padding:0.25em"><c:out value="${md.string02}"/></td>
						<td style="border:black thin solid;padding:0.25em"><c:out value="${md.string03}"/></td>
						<td style="border:black thin solid;padding:0.25em"><c:out value="${md.string04}"/></td>
					</tr>
			     </c:forEach>
		    </tbody>
	     </table>
	 </div>
</jsp:root>