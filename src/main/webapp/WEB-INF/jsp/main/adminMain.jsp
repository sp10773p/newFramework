<%--
  Created by IntelliJ IDEA.
  User: sdh
  Date: 2017-09-15
  Time: 오전 8:42
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <%@ include file="/WEB-INF/include/include-main-define.jspf" %>
    <script>
        $(function(){

            $.comm.setGlobalVar("sessionDiv", "${sessionDiv}"); // 사이트 구분
            $.comm.setGlobalVar("GLOBAL_LOGIN_USER_ID", "${userId}"); // 사용자 ID



            var url = "jspView.do?jsp=adm/sys/admList";
            $('.content').load(url);
        })
    </script>
</head>
<body class="hold-transition skin-blue sidebar-mini">
<div class="wrapper">
    <!-- Main Header -->
    <%@ include file="/WEB-INF/include/include-main-header.jspf" %>
    <!-- Left side column. contains the logo and sidebar -->
    <%@ include file="/WEB-INF/include/include-main-left.jspf" %>

    <div>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
            <h1 id="subTitle">사용자 관리<%--<small>Optional description</small>--%></h1>
            <ol class="breadcrumb">
                <li>
                    <a href="#"><i class="fa fa-dashboard"></i>Level</a>
                </li>
                <li class="active">Here</li>
            </ol>
        </section>

        <%-- Main Content --%>
        <section class="content">

        </section>
    </div>
    <!-- /.content-wrapper -->
    </div>
    <!-- Main Footer -->
    <%@ include file="/WEB-INF/include/include-main-footer.jspf" %>
    <!-- Control Sidebar -->
    <%@ include file="/WEB-INF/include/include-main-sidebar.jspf" %>

</div>
</body>
</html>