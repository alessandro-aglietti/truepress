<?xml version="1.0" encoding="utf-8"?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:if test="${!(empty param.locale)}">
  <fmt:setLocale value="${param.locale}" />
</c:if>
<fmt:setBundle basename="i18n.i18n" var="i18n"/>

<!DOCTYPE html>
<html lang="it">
	<head>
		<meta charset="UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title><fmt:message key="press.title" bundle="${i18n}" /></title>
		
		<link type="text/css" rel="stylesheet" href="/css/press.css" />
		
		<!-- JS -->
		<script type="text/javascript" src="/vendor/json2.js"></script>
		<!-- http://api.jquery.com/ -->
		<script type="text/javascript" src="/vendor/jquery/jquery-1.9.1.min.js"></script>
		<script type="text/javascript" src="/vendor/jquery/jquery-migrate-1.2.0.js"></script>
		<script type="text/javascript" src="/vendor/jquery/jquery-ui.js"></script>
		
		<script type="text/javascript" src="/js/press.js"></script>
		<script type="text/javascript">
			var BOX_URL = "${it.boxurl}";
		</script>
		
		<script>
		  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
		  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
		  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
		  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');
		
		  ga('create', 'UA-42278310-1', 'press-us.appspot.com');
		  ga('send', 'pageview');
		
		</script>
	</head>
	<body>
		<h2><fmt:message key="press.h2" bundle="${i18n}" /></h2>
		
		<c:forEach var="box" items="${it.boxs}">
			<div class="box" id="${box.id}" style="position:absolute; top: ${box.top}px; left: ${box.left}px; width: ${box.width}px; height: ${box.height}px; ">
				<div class="content">
					<c:if test="${fn:length(box.rss) gt 0}">
						<c:if test="${fn:length(box.rss[0].entries) gt 0}">
							<a href="${box.rss[0].entries[0].url}"><h4>${box.rss[0].entries[0].title}</h4></a>
							<p>${box.rss[0].entries[0].content}</p>
							<p><i><fmt:formatDate value="${box.rss[0].entries[0].updated}" type="both" dateStyle="long" timeStyle="long"/></i></p>
						</c:if>
					</c:if>
				</div>
				<div class="actionBar"><a href="${it.boxurl}/${box.id}"><fmt:message key="press.addrss" bundle="${i18n}" /></a> -- <a href="${it.boxurl}/${box.id}/del"><fmt:message key="press.deletebox" bundle="${i18n}" /></a></div>
			</div>
		</c:forEach>
		
	</body>
</html>