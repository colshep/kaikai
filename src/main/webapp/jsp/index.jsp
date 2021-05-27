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
        <a style="float:left" class="btn btn-outline-primary" role="button" onclick="openModal('${basePath}/excel/modal')">开始上传</a>
        <a style="float:right" href="login" class="btn btn-outline-dark" role="button">登录</a>
    </div>
    <img class="img-fluid" src="${imgPath}/cloud.jpg" alt="cloud">

    <div id="modalWin" class="modal fade">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">×</span></button>
                    <h4 class="modal-title"></h4>
                </div>
                <div class="modal-body">
                    等待结果，请稍候...
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script type="text/javascript">
    // 模态窗口加载数据
    function openModal(url) {
        $("#modalWin").modal({
            backdrop: 'static',     // 点击空白不关闭
            keyboard: false,        // 按键盘esc也不会关闭
        });
        $("#modalWin").find('.modal-content').load(url);
    }

    // 每次隐藏时，清除数据。确保点击时，重新加载
    $("#modalWin").on("hidden.bs.modal", function () {
        $(this).removeData("bs.modal");
        $(this).find(".modal-content").children().remove();
    });

    function refresh() {
        window.location.href = "";
    }
</script>

</body>
</html>
