<%@ page isELIgnored="false" %>
<%@ page language="java" pageEncoding="UTF-8" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
%>
<c:set var="basePath" value="<%=basePath%>" scope="session"/>
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="session"/>
<c:set var="imgPath" value="${basePath}/img" scope="session"/>
<c:set var="cssPath" value="${basePath}/css" scope="session"/>
<c:set var="jsPath" value="${basePath}/js" scope="session"/>
<c:set var="pluginPath" value="${basePath}/plugin" scope="session"/>

<!-- constants variable -->
<script type="text/javascript">
    var basePath = '${basePath}';
    var contextPath = '${contextPath}';
    var imgPath = '${imgPath}';
    var cssPath = '${cssPath}';
    var jsPath = '${jsPath}';
    var pluginPath = '${pluginPath}';
</script>

<!-- 新 Bootstrap4 核心 CSS 文件 -->
<link rel="stylesheet" href="${cssPath}/bootstrap.min.css">
<!-- 一些自定义公共样式 -->
<link rel="stylesheet" href="${cssPath}/custom.css">
<!-- jQuery文件。务必在bootstrap.min.js 之前引入 -->
<script type="text/javascript" src="${jsPath}/jquery.min.js"></script>
<!-- Bootstrap4.5.2 + popper.min.js -->
<script type="text/javascript" src="${jsPath}/bootstrap.bundle.min.js"></script>
