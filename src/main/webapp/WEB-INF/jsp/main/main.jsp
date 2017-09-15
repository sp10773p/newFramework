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
    <%@ include file="/WEB-INF/include/include-header.jspf" %>
    <script>
        $(function () {
            //Date range picker
            var dateObj = $('#REG_DTM').daterangepicker();
            var headers = [
                {"HEAD_TEXT": "사용자ID"       , "WIDTH": "100", "FIELD_NAME": "USER_ID", "LINK": "fn_detail"},
                {"HEAD_TEXT": "사용자구분"     , "WIDTH": "80" , "FIELD_NAME": "USER_DIV_NM"},
                {"HEAD_TEXT": "가입상태"       , "WIDTH": "100", "FIELD_NAME": "USER_STATUS_NM"},
                {"HEAD_TEXT": "사업자등록번호" , "WIDTH": "120", "FIELD_NAME": "BIZ_NO"},
                {"HEAD_TEXT": "사용자명"       , "WIDTH": "150", "FIELD_NAME": "USER_NM", "ALIGN":"left"},
                {"HEAD_TEXT": "전화번호"       , "WIDTH": "90" , "FIELD_NAME": "TEL_NO"},
                {"HEAD_TEXT": "휴대폰번호"     , "WIDTH": "90" , "FIELD_NAME": "HP_NO"},
                {"HEAD_TEXT": "이메일"         , "WIDTH": "150", "FIELD_NAME": "EMAIL"},
                {"HEAD_TEXT": "가입신청일자"   , "WIDTH": "100", "FIELD_NAME": "REG_DTM"},
                {"HEAD_TEXT": "가입승인일자"   , "WIDTH": "100", "FIELD_NAME": "APPROVAL_DTM"},
                {"HEAD_TEXT": "사용여부"       , "WIDTH": "60" , "FIELD_NAME": "USE_CHK"},
                {"HEAD_TEXT": "팩스번호"       , "WIDTH": "90" , "FIELD_NAME": "FAX_NO"},
                {"HEAD_TEXT": "업태"           , "WIDTH": "100", "FIELD_NAME": "BIZ_CONDITION"},
                {"HEAD_TEXT": "종목"           , "WIDTH": "100", "FIELD_NAME": "BIZ_LINE"}
            ];

            var gridWrapper = new GridWrapper({
                "actNm"             : "사용자 조회",
                "targetLayer"       : "gridLayer",
                "qKey"              : "usr.selectUsrList",
                "requestUrl"        : "/usr/selectUsrList.do",
                "headers"           : headers,
                "paramsFormId"      : "searchForm",
                "gridNaviId"        : "gridPagingLayer",
                "onlyOneCheck"      : true,
                "firstLoad"         : false,
                //"postScript"        : fn_setCheckDisabled,
                "defaultSort"       : "REG_DTM DESC",
                "controllers": [
                    {"btnName": "btnSearch", "type": "S"},
                    {"btnName": "btnExcel", "type": "EXCEL"}
                ]
            });
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
                <li><a href="#"><i class="fa fa-dashboard"></i>Level</a></li>
                <li class="active">Here</li>
            </ol>
        </section>

        <%-- Main Content --%>
        <section class="content">
            <%-- Search Area--%>
            <div class="box box-info">
                <div class="box-header with-border">
                    <h3 class="box-title"><button type="button" class="btn btn-block btn-info btn-sm" style="width: 120px;">Search</button></h3>
                    <div class="box-tools pull-right">
                        <button type="button" class="btn btn-box-tool" data-widget="collapse" data-toggle="tooltip" title="" data-original-title="Collapse" style="float:right;">
                            <i class="fa fa-minus"></i>
                        </button>
                    </div>
                </div>
                <form class="form-horizontal" id="searchForm">
                    <div class="box-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="REG_DTM" class="col-sm-4">가입신청일</label>
                                    <div class="col-sm-8">
                                        <input type="text" class="form-control" id="REG_DTM">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="USER_STATUS" class="col-sm-4">가입상태</label>
                                    <div class="col-sm-8">
                                        <select class="form-control" id="USER_STATUS" name="USER_STATUS">
                                            <option>option 1</option>
                                            <option>option 2</option>
                                            <option>option 3</option>
                                            <option>option 4</option>
                                            <option>option 5</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="USE_CHK" class="col-sm-4">사용여부</label>
                                    <div class="col-sm-8">
                                        <select class="form-control" id="USE_CHK" name="USE_CHK">
                                            <option value="">선택</option>
                                            <option value="Y">사용</option>
                                            <option value="N">미사용</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="REG_DTM" class="col-sm-4">가입신청일</label>
                                    <div class="col-sm-8">
                                        <input type="text" class="form-control" id="REG_DTM">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="USER_STATUS" class="col-sm-4">가입상태</label>
                                    <div class="col-sm-8">
                                        <select class="form-control" id="USER_STATUS">
                                            <option>option 1</option>
                                            <option>option 2</option>
                                            <option>option 3</option>
                                            <option>option 4</option>
                                            <option>option 5</option>
                                        </select>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label for="inputPassword3" class="col-sm-4">Password</label>
                                    <div class="col-sm-8">
                                        <input type="email" class="form-control" id="exampleInputEmail1" placeholder="Enter email">
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <!-- /.box -->
            <%-- / Search Area--%>


            <div class="row">
                <div class="col-xs-12">
                    <div class="box">
                        <div class="box-header">
                            <h3 class="box-title">Hover Data Table</h3>
                        </div>
                        <div class="box-body">
                            <div id="gridLayer" style="height: 430px"></div>
                            <div class="bottom_util"><div class="paging" id="gridPagingLayer"></div></div>
                        </div>
                    </div>
                </div>
            </div>
            <!-- /.row -->
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