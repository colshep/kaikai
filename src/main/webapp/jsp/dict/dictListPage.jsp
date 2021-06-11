<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.plunger.util.DictUtil"%>
<%@ taglib uri="http://www.plunger.com/DictUtil" prefix="DictUtil"%>

<html>
<head>
    <meta charset="UTF-8">
    <!-- 为了让 Bootstrap 开发的网站对移动设备友好，确保适当的绘制和触屏缩放，需要在网页的 head 之中添加 viewport meta 标签 -->
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <!-- 常用js和css引入 -->
    <%@ include file="../common.jsp" %>

    <title>字典管理</title>
</head>
<body>
<div class="container">
    <h2>字典管理</h2>
    <button type="button" class="btn btn-outline-dark btn-sm" data-target="#modalWin" onclick="bindData('insert')">新增
    </button>
    <button type="button" class="btn btn-outline-dark btn-sm" onclick="refresh()">查询</button>
    <button type="button" class="btn btn-outline-dark btn-sm" onclick="refreshCache()">刷新缓存</button>
    <table class="table table-hover">
        <thead class="thead-dark">
        <tr>
<%--            <th>唯一标识</th>--%>
            <th>字典类型</th>
            <th>字典名称</th>
            <th>字典值</th>
            <th>备注</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${dictList}" var="item" varStatus="vs">
            <tr>
<%--                <td style="vertical-align: middle;">${item.unid}</td>--%>
                <td>${item.type}</td>
                <td>${item.name}</td>
                <td>${item.value}</td>
                <td>${item.memo}</td>
                <td>
                    <button type="button" class="btn btn-outline-dark btn-sm" data-target="#modalWin"
                            onclick="bindData('${item.unid}')">修改
                    </button>
                    <button type="button" class="btn btn-outline-dark btn-sm" onclick="deleteByUnid('${item.unid}')">删除
                    </button>
                </td>
            </tr>
        </c:forEach>

        </tbody>
    </table>

    <div id="modalWin" class="modal fade">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                            aria-hidden="true">×</span></button>
                    <h4 class="modal-title"></h4>
                </div>
                <div class="modal-body">
                    等待结果，请稍后...
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
    function bindData(unid) {
        $("#modalWin").modal({
            backdrop: 'static',     // 点击空白不关闭
            keyboard: false,        // 按键盘esc也不会关闭
        });
        $("#modalWin").find('.modal-content').load("modal/" + unid);
    }

    // 每次隐藏时，清除数据。确保点击时，重新加载
    $("#modalWin").on("hidden.bs.modal", function () {
        $(this).removeData("bs.modal");
        $(this).find(".modal-content").children().remove();
    });

    function refresh() {
        window.location.href = "list";
    }

    function deleteByUnid(unid) {
        $.ajax({
            async: false,
            url: "delete/" + unid,
            type: "get",
            dataType: "json",
            //data: $("#userForm").serializeArray(),
            //contentType: "application/x-www-form-urlencoded",
            success: function (data, textStatus, jqXHR) {
                alert(data.message);
                if (data.code == "200") {
                    refresh();
                }
            },
            error: function (jqXHR, status, error) {
                alert("通讯失败");
            }
        });
    }

    function refreshCache() {
        $.ajax({
            async: false,
            url: "refreshCache",
            type: "get",
            dataType: "json",
            success: function (data, textStatus, jqXHR) {
                alert(data.message);
                if (data.code == "200") {
                    refresh();
                }
            },
            error: function (jqXHR, status, error) {
                alert("通讯失败");
            }
        });
    }

</script>

</body>
</html>
