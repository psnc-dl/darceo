<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<title>Available delivery plans</title>
</head>
<body>
	<c:forEach items="${it.plans}" var="plan" varStatus="planLoop">
		<div style="margin: 30px 10px">
			<form action="${it.url}/${planLoop.count - 1}" method="post">
				${plan.delivery.serviceName} [${plan.delivery.executionCost}]
				<ul>
					<c:forEach items="${plan.conversionPaths}" var="path">
						<li>
							<c:forEach items="${path.transformations}" var="transformation" varStatus="transLoop">
								<c:if test="${transLoop.first}">${transformation.inputFileFormat.puid} : </c:if>
								${transformation.serviceName}
								<c:if test="${!transLoop.last}"> -> </c:if>
								<c:if test="${transLoop.last}"> : ${transformation.outputFileFormat.puid}</c:if>
							</c:forEach>
							[${path.executionCost}]
						</li>
					</c:forEach>
				</ul>
				<input type="submit" value="Choose" name="Choose"/>
			</form>
		</div>
	</c:forEach>
</body>
</html>