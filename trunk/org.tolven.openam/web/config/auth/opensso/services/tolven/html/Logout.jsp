<%--
   DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
  
   Copyright (c) 2005 Sun Microsystems Inc. All Rights Reserved
  
   The contents of this file are subject to the terms
   of the Common Development and Distribution License
   (the License). You may not use this file except in
   compliance with the License.
                                                                                
   You can obtain a copy of the License at
   https://opensso.dev.java.net/public/CDDLv1.0.html or
   opensso/legal/CDDLv1.0.txt
   See the License for the specific language governing
   permission and limitations under the License.
                                                                                
   When distributing Covered Code, include this CDDL
   Header Notice in each file and include the License file
   at opensso/legal/CDDLv1.0.txt.
   If applicable, add the following below the CDDL Header,
   with the fields enclosed by brackets [] replaced by
   your own identifying information:
   "Portions Copyrighted [year] [name of copyright owner]"
                                                                                
   $Id$
                                                                                
--%>




<html>

<%@page info="Logout" language="java"%>
<%@taglib uri="/WEB-INF/jato.tld" prefix="jato"%>
<%@taglib uri="/WEB-INF/auth.tld" prefix="auth"%>
<jato:useViewBean className="com.sun.identity.authentication.UI.LogoutViewBean">


<%@ page contentType="text/html" %>

<head>
<title><jato:text name="htmlTitle_Logout" /></title>

<% 
String ServiceURI = (String) viewBean.getDisplayFieldValue(viewBean.SERVICE_URI);
%>

<link rel="stylesheet" href="<%= ServiceURI %>/css/styles.css" type="text/css" />
<script language="JavaScript" src="<%= ServiceURI %>/js/browserVersion.js"></script>
<script language="JavaScript" src="<%= ServiceURI %>/js/auth.js"></script>

<script language="JavaScript">
    writeCSS('<%= ServiceURI %>');
</script>
<script type="text/javascript"><!--// Empty script so IE5.0 Windows will draw table and button borders
//-->
</script>
</head>

<body class="LogBdy">

<table border="0" cellpadding="0" cellspacing="0" align="center" title="">
    <tr>
      <td width="50%"><img src="<%= ServiceURI %>/images/dot.gif" width="1" height="1" alt="" /></td>
      <td><img src="<%= ServiceURI %>/images/dot.gif" width="728" height="1" alt="" /></td>
      <td width="50%"><img src="<%= ServiceURI %>/images/dot.gif" width="1" height="1" alt="" /></td>
    </tr>
    <tr class="LogTopBnd" style="background-image: url(<%= ServiceURI %>/images/gradlogtop.jpg); 
    background-repeat: repeat-x; background-position: left top;">
      <td>&nbsp;</td>
      <td><img src="<%= ServiceURI %>/images/dot.gif" width="1" height="30" alt="" /></td>
      <td>&nbsp;</td>
    </tr>
    <tr>
      <td class="LogMidBnd" style="background-image: url(<%= ServiceURI %>/images/gradlogsides.jpg);
        background-repeat:repeat-x;background-position:left top;">&nbsp;</td>
      <td class="LogCntTd" style="background-image: url(<%= ServiceURI %>/images/tolven-login-backimage.jpg);
        background-repeat:no-repeat;background-position:center top;" height="600" align="center" valign="middle">
        <table border="0" background="<%= ServiceURI %>/images/dot.gif" cellpadding="0" cellspacing="0" 
        width="100%" title="">
          <tr>
            <td width="260"><img src="<%= ServiceURI %>/images/dot.gif" width="260" height="245" alt="" /></td>
            <td width="415" bgcolor="#ffffff" valign="top"><img src="<%= ServiceURI %>/images/dot.gif" width="30" height="1" alt="" />
              <table border="0" cellspacing="0" cellpadding="0">
                <tr>
                  <td colspan="2">
                      <img src="<%= ServiceURI %>/images/dot.gif" width="1" height="25" alt="" />                                
                  </td>
                </tr>              
                <tr>
                  <td>&nbsp;</td>
                  <td><div class="logErr"><table align="center" border="0" cellpadding="0" cellspacing="0" 
                    class="AlrtTbl" title="">
                <tr>
                <td valign="middle">
                <div class="AlrtInfTxt"> 
                <img name="Login.AlertImage" src="<%= ServiceURI %>/images/info_large.gif" alt="Logout" 
                height="21" width="21" />
                <!-- logout message -->
                <jato:content name="ContentTextBoxLogout">
                <jato:text name="txtLogout" />
                </jato:content>        
                </div>
                <div class="AlrtMsgTxt">
                <!-- back to login page hyperlink -->
                <jato:content name="ContentHrefLogout">
                <p><auth:href name="urlLogin"
                fireDisplayEvents='true'><jato:text
                name="txtGotoLoginAfterLogout" /></auth:href></p>
                </jato:content>
                </div>
                </td></tr></table></div></td>
                </tr>
                
                <tr>
                <td>&nbsp;</td>
                </tr>
            <tr>
            <td><img src="<%= ServiceURI %>/images/dot.gif" 
            width="1" height="33" alt="" /></td>
            <td>&nbsp;</td>
            </tr>
        </table>
      </td>
      <td width="45"><img src="<%= ServiceURI %>/images/dot.gif" 
      width="45" height="245" alt="" /></td>
    </tr>
    </table>
    </td>
    <td class="LogMidBnd" style="background-image: url(<%= ServiceURI %>/images/gradlogsides.jpg);
    background-repeat:repeat-x;background-position:left top;">&nbsp;</td>
    </tr>
    <tr class="LogBotBnd" style="background-image: url(<%= ServiceURI %>/images/gradlogbot.jpg);
    background-repeat:repeat-x;background-position:left top;">
      <td>&nbsp;</td>
      <td><div class="logCpy"><span class="logTxtCpy">
        <auth:resBundle bundleName="amAuthUI" resourceKey="copyright.notice" /></span></div>
      </td>
      <td>&nbsp;</td>
    </tr>
  </table>
</body>

</jato:useViewBean>
</html>
