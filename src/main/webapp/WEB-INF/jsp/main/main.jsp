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

            var url = "jspView.do?jsp=adm/sys/admList";
            $('#contentFrame').attr('src', url);
            // $('.content').load(url);

        })

        function resizeTopIframe(dynheight) {
            $("#contentFrame").height(parseInt(dynheight) + 10);
        }
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

            <iframe src="" id="contentFrame" style="width: 100%;height: 100%;border: 0px;" scrolling="no" onload=""></iframe>
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