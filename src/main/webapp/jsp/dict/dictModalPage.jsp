<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="modal-header">
    <h4 class="modal-title">${fn == 'insert' ? '新增' : '修改'}字典</h4>
    <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span>
    </button>
</div>
<div class="modal-body">
    <form id="actionForm" method="post" action="${fn}">
        <div class="form-group">
            <label for="unid">唯一标识</label>
            <input type="text" class="form-control" id="unid" name="unid" value="${dict.unid}" readonly>
        </div>
        <div class="form-group">
            <label for="type">字典类型</label>
            <input type="text" class="form-control required" id="type" name="type" placeholder="字典类型"
                   value="${dict.type}">
            <div class="invalid-feedback"></div>
        </div>
        <div class="form-group">
            <label for="name">字典名称</label>
            <input type="text" class="form-control required" id="name" name="name" placeholder="字典名称"
                   value="${dict.name}">
            <div class="invalid-feedback"></div>
        </div>
        <div class="form-group">
            <label for="value">字典值</label>
            <input type="text" class="form-control required" id="value" name="value" placeholder="字典值"
                   value="${dict.value}">
            <div class="invalid-feedback"></div>
        </div>
    </form>
</div>
<div class="modal-footer">
    <button type="button" id="submitBtn" class="btn btn-primary" onclick="submit()">提交</button>
</div>

<script type="text/javascript">
    function submit() {
        if (!checkForm()) {
            return false;
        }

        $.ajax({
            async: false,
            url: $("#actionForm").attr("action"),
            type: "POST",
            dataType: "json",
            data: $("#actionForm").serializeArray(),
            contentType: "application/x-www-form-urlencoded",
            success: function (data, textStatus, jqXHR) {
                alert(data.message);
                if (data.code == "200") {
                    $("#userModal").modal("hide");
                    refresh();
                }
            },
            error: function (jqXHR, status, error) {
                alert("通讯失败");
            }
        });
    }

    //必填验证
    function checkForm() {
        let checkFlag = true;

        $(".required").each(function () {
            const value = $(this).val();
            const feedback = $(this).parent().find(".invalid-feedback");
            if (value == "" || value == 0) {
                feedback.html("不允许为空");
                $(this).removeClass("is-valid");
                $(this).addClass("is-invalid");
                checkFlag = false;
            } else {
                feedback.innerHTML = "";
                $(this).removeClass("is-invalid");
                $(this).addClass("is-valid");
            }
        });

        return checkFlag;
    }
</script>
