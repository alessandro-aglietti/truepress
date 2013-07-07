<?xml version="1.0" encoding="utf-8"?>

<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${!(empty param.locale)}">
  <fmt:setLocale value="${param.locale}" />
</c:if>
<fmt:setBundle basename="i18n.i18n" var="i18n"/>

<!DOCTYPE html>
<html lang="it">
	<head>
		<meta charset="UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<title><fmt:message key="home.title" bundle="${i18n}" /></title>
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
		<h2><fmt:message key="box.h2" bundle="${i18n}" /></h2>
		
		<h3><fmt:message key="box.h3" bundle="${i18n}" /></h3>
		<ul>
			<c:forEach var="rss" items="${it.box.rss}">
				<li>${rss.url}</li>
			</c:forEach>
		</ul>
		<form method="POST" action="${it.boxurl}">
			<input type="text" name="rssurl" placeholder="<fmt:message key="box.rssplaceholder" bundle="${i18n}" />"/>
			<input type="submit" value="<fmt:message key="box.save" bundle="${i18n}" />" />
		</form>
	</body>
</html>