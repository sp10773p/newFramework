<%@ taglib prefix="attr" uri="/WEB-INF/tlds/attr" %>
<%--
  Created by IntelliJ IDEA.
  User: sdh
  Date: 2017-09-20
  Time: 오전 8:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <%@ include file="/WEB-INF/include/include-header.jspf" %>
    <script>
        $(function () {
            //Date range picker
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
<body>
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
                        <label for="F_REG_DTM" class="col-sm-4">가입신청일</label><label for="T_REG_DTM" style="display: none;">가입신청일</label>
                        <div class="col-sm-8 dateSearch">
                            <%--<input type="text" class="form-control" id="REG_DTM">--%>
                            <input type="text" id="F_REG_DTM" name="F_REG_DTM" <attr:datefield to="T_REG_DTM"/>><span>~</span>
                            <input type="text" id="T_REG_DTM" name="T_REG_DTM" <attr:datefield/> >
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="USER_STATUS" class="col-sm-4">가입상태</label>
                        <div class="col-sm-8">
                            <select class="w143" id="USER_STATUS" name="USER_STATUS">
                                <option value="">전체</option>
                                <option value="A">가입요청</option>
                                <option value="B">가입승인</option>
                                <option value="C">사용중지</option>
                                <option value="D">탈퇴요청</option>
                                <option value="E">탈퇴승인</option>
                            </select>
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="USE_CHK" class="col-sm-4">사용여부</label>
                        <div class="col-sm-8">
                            <select class="w143" id="USE_CHK" name="USE_CHK">
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
                        <label for="SEARCH_TXT" class="col-sm-4">검색조건</label><label for="SEARCH_COL" style="display: none;">검색조건</label>
                        <div class="col-sm-8">
                            <select id="SEARCH_COL" name="SEARCH_COL" class="w120">
                                <option value="BIZ_NO" selected="">사업자등록번호</option>
                                <option value="USER_ID">사용자ID</option>
                                <option value="USER_NM">사용자명</option>
                            </select>
                            <input id="SEARCH_TXT" name="SEARCH_TXT" type="text" style="width:calc(100% - 125px)">
                        </div>
                    </div>
                </div>
                <div class="col-md-4">
                    <div class="form-group">
                        <label for="USER_DIV" class="col-sm-4">사용자구분</label>
                        <div class="col-sm-8">
                            <select id="USER_DIV" name="USER_DIV" class="w143">
                                <option value="">선택</option>
                            </select>
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
                <h3 class="box-title"><%--Hover Data Table--%></h3>
                <div class="box-tools pull-right">
                    <button type="button" class="btn btn-block btn-info btn-sm" style="width: 120px;">Excel</button>
                </div>
            </div>
            <div class="box-body">
                <div id="gridLayer" style="height: 430px"></div>
                <div class="bottom_util"><div class="paging" id="gridPagingLayer"></div></div>
            </div>
        </div>
    </div>
</div>
<!-- /.row -->
</body>
</html>
