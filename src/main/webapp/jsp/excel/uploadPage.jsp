<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<head>
    <meta charset="UTF-8">
    <!-- 为了让 Bootstrap 开发的网站对移动设备友好，确保适当的绘制和触屏缩放，需要在网页的 head 之中添加 viewport meta 标签 -->
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">

    <script type="text/javascript" src="${pluginPath}/bootstrap-fileinput/js/fileinput.min.js"></script>
    <script type="text/javascript" src="${pluginPath}/bootstrap-fileinput/js/locales/zh.js"></script>
    <link rel="stylesheet" href="${pluginPath}/bootstrap-fileinput/css/fileinput.min.css">
    <link rel="stylesheet" href="${pluginPath}/font-awesome/css/font-awesome.min.css">
</head>

<div class="modal-header">
    <h4 class="modal-title">上传表格</h4>
    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
    </button>
</div>
<div class="modal-body">
    <form id="userForm">
        <div class="form-group">
            <input id="uploadFile" name="uploadFile" type="file" class="file"
                   data-language="zh">
        </div>
    </form>
</div>
<div class="modal-footer">
<%--    <button type="button" id="submitBtn" class="btn btn-primary" onclick="submit()">提交</button>--%>
</div>

<script type="text/javascript">

    $(function () {

        $("#uploadFile").fileinput('refresh', {
            dropZoneTitle: '拖拽文件到这里',
            browseOnZoneClick: true,
            uploadUrl: "${basePath}/excel/upload",
            allowedFileExtensions: ['xlsx']
        });

        $('#uploadFile').on('fileuploaded', function(event, data, previewId, index, fileId) {
            const form = data.form, files = data.files, extra = data.extra,
                response = data.response, reader = data.reader;
            console.log('response=' + JSON.stringify(response));
            if (response.code == 200) {
                alert(response.data.msg);
            }
            // console.log('File uploaded triggered', fileId);
        });
    })

    function submit() {
        <%--$.ajax({--%>
        <%--    async: false,--%>
        <%--    url: "${basePath}/excel/upload",--%>
        <%--    type: "post",--%>
        <%--    dataType: "json",--%>
        <%--    contentType: "application/json",--%>
        <%--    data: JSON.stringify({--%>
        <%--        projectUnid: "123"--%>
        <%--    }),--%>

        <%--    success: function (data, textStatus, jqXHR) {--%>
        <%--        alert(data.message);--%>
        <%--        if (data.code == "200") {--%>
        <%--            $("#modalWin").modal("hide");--%>
        <%--            refresh();--%>
        <%--        }--%>
        <%--    },--%>
        <%--    error: function (jqXHR, status, error) {--%>
        <%--        alert("通讯失败");--%>
        <%--    }--%>
        <%--});--%>
    }
</script>
