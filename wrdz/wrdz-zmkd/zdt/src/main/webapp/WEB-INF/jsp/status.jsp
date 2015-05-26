<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<c:if test="${it.status == 'NEW' || it.status == 'RUNNING' }">
		<meta http-equiv="refresh" content="60;URL='${it.url}'"/>
	</c:if>
	<title>
		<c:choose>
			<c:when test="${it.status == 'NEW' }">Prepared for processing...</c:when>
			<c:when test="${it.status == 'RUNNING' }">Processing...</c:when>
			<c:when test="${it.status == 'ERROR' }">Error!</c:when>
		</c:choose>
	</title>
</head>
<body>
	<c:choose>
		<c:when test="${it.status == 'NEW' }">Prepared for processing...</c:when>
		<c:when test="${it.status == 'RUNNING' }">Processing...</c:when>
		<c:when test="${it.status == 'ERROR' }">Error!</c:when>
	</c:choose>
</body>
</html>