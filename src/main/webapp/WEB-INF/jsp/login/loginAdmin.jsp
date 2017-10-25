<%--
  Created by IntelliJ IDEA.
  User: sdh
  Date: 2017-09-21
  Time: 오후 12:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/include/include-main-define.jspf" %>

    <script>
        var saveIdKey = "ucssystemAdminId";

        $(function(){
            $('#userId').on('keypress', function(event) {
                if (event.which === 13) {
                    event.preventDefault();
                    $('#userPw').focus();
                }
            });
            $('#userPw').on('keypress', function(event) {
                if (event.which === 13) {
                    event.preventDefault();
                    $('#btnLogin').trigger('click');
                }
            });

            $('#btnLogin').on('click', function(event) {
                var e = $('#userId');
                if ($.trim(e.val()) === '') {
                    alert($.comm.getMessage("W00000067")); // 아이디를 입력해 주십시오.
                    e.focus();
                    return false;
                }
                e = $('#userPw');
                if ($.trim(e.val()) === '') {
                    alert($.comm.getMessage("W00000068")); // 비밀번호를 입력해 주십시오.
                    e.focus();
                    return false;
                }
                loginAction();
            });

            if(!$.comm.isNull('${msg}')){
                alert('${msg}');
            }

            var saveIdVal = localStorage.getItem(saveIdKey);

            if(!$.comm.isNull(saveIdVal)){
                $('#userId').val(saveIdVal);
                $('#saveId').prop("checked", true);
                $('#userPw').focus();

            }else{
                $('#userId').focus();
            }
        })

        function fn_saveid() {
            if ($('#saveId').prop("checked") == true) {
                localStorage.setItem(saveIdKey, $('#userId').val());
            }else{
                localStorage.removeItem(saveIdKey);
            }
        }

        function loginAction() {
            fn_saveid();
            $('form:first').attr('method', 'post');
            $('form:first').attr('action', 'loginAction.do').submit();
        }xt.xml

    </script>
</head>
<body>
<div class="wrapper">
    <div class="box box-info" style=" width: 450px; margin-top: 200px; margin-left: auto; margin-right: auto;">
        <div class="box-header with-border">
            <h3 class="box-title" style="font-size: 30px;">Login</h3>
        </div>
        <form class="form-horizontal">
            <input type="hidden" name="sessionDiv" value="${Constant.ADM_SESSION_DIV.getCode()}"/>
            <div class="box-body">
                <div class="form-group">
                    <label for="userId" class="col-sm-3 control-label">아이디</label>
                    <div class="col-sm-9">
                        <input type="text" class="form-control" id="userId" name="userId" placeholder="아이디">
                    </div>
                </div>
                <div class="form-group">
                    <label for="userPw" class="col-sm-3 control-label">비밀번호</label>
                    <div class="col-sm-9">
                        <input type="password" class="form-control" id="userPw" name="userPw" placeholder="비밀번호">
                    </div>
                </div>
                <div class="form-group">
                    <div class="col-sm-offset-3 col-sm-9">
                        <div class="checkbox">
                            <label>
                                <input type="checkbox" id="saveId" style="width: 13px; height: 13px;"> Remember me
                            </label>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.box-body -->
            <div class="box-footer" style="text-align: center;">
                <button type="submit" class="btn btn-info" id="btnLogin">로그인</button>
            </div>
            <!-- /.box-footer -->
        </form>
    </div>
</div>
</body>
</html>

