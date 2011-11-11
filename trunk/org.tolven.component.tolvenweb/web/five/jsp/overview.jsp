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
		<p>The majority of the Tolven application uses Java Server Faces and Facelets. However, this 
		technical overview section uses jsp, a structure with fewer moving parts and familiar to a larger population.
		This page exposes important aspects of the mechanisms that drive the Tolven application and EJB architecture.</p>
		<p>Each user session is linked to an AccountUser entity. AccountUser, maintained by an Account Administrator,
		authorizes one user to log into one account (be it family, clinic, or hospital). The AccountUser
		is validated by a security filter and then made available to the the servlet. A user is not allowed to
		switch to a different account via AccountUser without some formality which Tolven calls the vestibule.
		</p> 
		<pre>
			accountUser.id=${tolBean.accountUser.id}
			accountUser.user.ldapUID=${tolBean.accountUser.user.ldapUID}
		</pre>
		<p>A transaction filter surrounds each request and at the same time, creates a timestamp that is
		used as a consistent time throughout the transaction. The timestamp is UTC but displays here 
		in the locale of the server.</p>
		<pre>
			now=${tolBean.tolvenNow}
		</pre>
		<p>The Account entity represents the primary unit of security in Tolven, a partitioning if you will. For security reasons, Tolven
		never trusts the browser for the account number. It's not a secret, but the server always uses the authenticated Account
		known to the server when responding to browser requests. In effect, there is always an implicit
		qualification by account when doing a query.</p>
		<pre>
			accountUser.account.id=${tolBean.accountUser.account.id}
			accountUser.account.title=${tolBean.accountUser.account.title}
		</pre>
		<p>The Tolven application is conceptually arranged as a tree. At the root is the AccountType (eCHR, ePHR, etc). Next level 
		down usually has the top-level tabs, menu items, and content pages containing lists, drilldown items, portlets, 
		wizards, etc. </p>
		<p>In order to work with this tree in the browser, a DOM-friendly element identifier is used. This identifier can be used as the ID of a div and
		can also be parsed in order to find parent menu items easily. While the javascript loads page components lazily, it is this
		element id (called path in MenuData) that identifies the pane being displayed.</p>
		<pre>
			element=${tolBean.element}
		</pre>
		<p>The element id is an instance identifier. If the numbers embedded in this element id are removed from the element, 
		then result identifies the MenuStructure entity which defines the instance.</p>
		<pre>
			menuStructure.Path=${tolBean.menuStructure.path}
		</pre>
		<p>The menuStructure id can then be used to find the metadata for this particular menu item. </p>
		<pre>
			menuStructure.text=${tolBean.menuStructure.text}
			menuStructure.role=${tolBean.menuStructure.role}
		</pre>
	 </div>
</jsp:root>