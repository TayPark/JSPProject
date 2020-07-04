<%@ page language = "java" contentType = "text/html; charset = UTF-8" pageEncoding = "UTF-8"
	isELIgnored = "false" %>

<%@ taglib prefix = "fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>    
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>

<c:set var = "contextPath" value = "${pageContext.request.contextPath}" />    
 
<%
	request.setCharacterEncoding("UTF-8");
%> 


<html>
<head>
	<meta charset = "UTF-8">
   	<title> 로그인 </title>
</head>
	<body>
	<form method = "post" action = "${contextPath}/user/login.do">
	<h1 style = "text-align:center"> 로그인  </h1>
		<table align = "center">
		    <tr>
		       <td width = "200"> <p align = "right"> 아이디 </td>
		       <td width = "400"> <input type = "text" name = "id"> </td>
		    </tr>
		    <tr>
		        <td width = "200"> <p align = "right"> 비밀번호 </td>
		        <td width = "400"> <input type = "password" name = "pw"> </td>
		    </tr>
		    <tr>
		        <td width = "200"> <p>&nbsp;</p> </td>
		        <td width = "400">
			       <input type = "submit" value = "로그인">
		       </td>
		   </tr>
		</table>
	</form>
</body>
</html>
