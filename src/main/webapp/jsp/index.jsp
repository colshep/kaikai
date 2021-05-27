<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <meta charset="UTF-8">
    <!-- 为了让 Bootstrap 开发的网站对移动设备友好，确保适当的绘制和触屏缩放，需要在网页的 head 之中添加 viewport meta 标签 -->
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- 常用js和css引入 -->
    <%@ include file="common.jsp" %>

    <title>Plunger Project</title>
</head>
<body>
<div class="container-fluid">
    <div>
        <h1>主页</h1>
        <a style="float:left" href="users/list" class="btn btn-outline-primary" role="button">用户管理</a>
        <a style="float:right" href="login" class="btn btn-outline-dark" role="button">登录</a>
    </div>
    <img class="img-fluid" src="${imgPath}/cloud.jpg" alt="cloud">
</div>
</body>
</html>
