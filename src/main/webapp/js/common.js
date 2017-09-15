(function($) {
	
	$.comm = {
        /***
         * 비동기방식으로 서버요청할 경우 사용하는 공통함수
         * @param url : 요청 URL
         * @param data : 파라미터 데이터
         * @param successCallback : 성공시 callback 함수
         * @param actionNm : 요청 서비스 설명
         * @param errorCallback : 에러시 callback 함수
         * @param async : 비동기 여부
         * @param ownerObj : 콜백함수에서 인자로 넘겨줄 현재 Object
         * @param isAjaxConvert : getAjaxData 함수를 태울지 여부 (default false)
         */
        send: function (url, data, successCallback, actionNm, errorCallback, async, ownerObj, isAjaxConvert) {
            var result =
                        $.ajax({
                            type: 'POST',
                            url: url,
                            dataType: 'json',
                            cache: false,
                            async: $.type(async) === 'undefined' || $.type(async) === 'null' ? true : async,
                            processData: false,
                            contentType: "application/json; charset=UTF-8",
                            enctype: 'multipart/form-data',
                            data: (isAjaxConvert == true ? data : $.comm.getAjaxData(data, actionNm)),
                            beforeSend: function (xhr) {
                                xhr.setRequestHeader("AJAX"                , true);
                                xhr.setRequestHeader("ACTION_MENU_ID"      , $.page.getMenuId());
                                xhr.setRequestHeader("sessionDiv"          , $.page.getSessionDiv() );
                                xhr.setRequestHeader("GLOBAL_LOGIN_USER_ID", $.comm.getGlobalVar("GLOBAL_LOGIN_USER_ID"));
                                if($.comm.getGlobalVar("api_consumer_key") != null) xhr.setRequestHeader("api_consumer_key", $.comm.getGlobalVar("api_consumer_key"));

                                $.comm.wait(true);
                            },
                            successCallback: successCallback,
                            errorCallback: errorCallback,
                            ownerObj: ownerObj,
                            success: function (data, status) {
                                if(!$.comm.isNull(data.code)){

                                    if(data.code == "E00000002"){ // 세션이 만료되었습니다.
                                        alert($.comm.getMessage(data["code"]));


                                        var div = $.comm.getGlobalVar("sessionDiv");
                                        var url = "/";
                                        if(div == 'M'){
                                            url = "/admin"
                                        }else if(div == 'B'){
                                            url = "/mobile"
                                        }

                                        $.comm.mainLocation(url);
                                        return;

                                    }else if(data.code == "E00000003"){ // 시스템에러가 발생하였습니다.
                                        alert($.comm.getMessage(data["code"]));
                                        console.log(data["msg"]);

                                        return;
                                    }
                                }

                                if(!$.comm.isNull(data["code"])){
                                    alert($.comm.getMessage(data["code"]));
                                }else{
                                    if(!$.comm.isNull(data["msg"])){
                                        alert(data["msg"]);
                                    }
                                }

                                if(!$.comm.isNull(this.successCallback)) {
                                    this.successCallback(data, status, this.ownerObj);
                                }

                                if(this.async == false){
                                    return data;
                                }
                            },
                            complete:function(){
                                $.comm.wait(false);
                                //$('.wrap-loading').addClass('display-none'); //이미지 감추기 처리
                            },
                            error: ($.comm.isNull(errorCallback)) ?
                                function (request,status,error) {
                                    console.log("code:"+request.status+"\n"+"message:"+request.responseText+"\n"+"error:"+error);
                                } : errorCallback
                        });

            if(async == false){
                if(result != null && result.responseText != null){
                    return JSON.parse(result.responseText);
                }else{
                    return {};
                }
            }
        },
        /***
         * 동기방식으로 서버요청할 경우 사용하는 공통함수
         * @param url : 요청 URL
         * @param data : 파라미터 데이터
         * @param actionNm : 요청 서비스 설명
         * @returns {Object}
         */
        sendSync: function (url, data, actionNm) {
            return this.send(url, data, null, actionNm, null, false);
        },
        /***
         * form ID를 데이터로 서버요청할 경우 사용하는 공통함수
         * @param url : 요청 URL
         * @param formId : Form ID
         * @param successCallback : 성공시 callback 함수
         * @param actionNm : 요청 서비스 설명
         * @param errorCallback : 에러시 callback 함수
         * @param async : 비동기 여부
         * @param etcParams : form 내부 이외의 파라미터 지정
         */
        sendForm: function (url, formId, successCallback, actionNm, errorCallback, async, etcParams) {
            if(this.validation(formId) == false){
                return;
            }

            var paramObj = this.getFormData(formId);
            this.send(url, $.extend(paramObj, etcParams), successCallback, actionNm, errorCallback, async);
        },
        /**
         * ajax Form Submit을 실행
         * multipart를 지원하므로 (파일 업로드 가능)
         * @param url 요청 URL
         * @param formId 전송데이터 폼 ID
         * @param successCallback 요청성공시 콜백함수
         * @param actionNm 요청 서비스 설명
         */
        submit : function (url, formId, successCallback, actionNm) {
            if($.comm.isNull(formId)) {
                formId = "commonForm";
                if($('#'+formId).length == 0){
                    var form = $("<form>");
                    form.attr("id", formId);
                    $("body").append(form);
                }
            }

            if(this.validation(formId) == false){
                return;
            }

            if(!$.comm.isNull(actionNm)){
                $('#'+formId).append("<input type='hidden' name='ACTION_NM' value='" + actionNm +"'");
            }

            $.comm.wait(true);
            $('#'+formId).ajaxForm({
                url: url,
                type: "POST",
                enctype: "multipart/form-data",
                successCallback: successCallback,
                headers :{
                    "sessionDiv": $.comm.getGlobalVar("sessionDiv"),
                    "GLOBAL_LOGIN_USER_ID": $.comm.getGlobalVar("GLOBAL_LOGIN_USER_ID")
                },
                success: function(responseText, statusText){
                    //eval(responseText);
                    if(!$.comm.isNull(this.successCallback)) {
                        this.successCallback(eval(responseText), statusText);
                    }
                },
                error: function(x, e){
                    if(x.status == 0){
                        alert("네트워크를 체크해주세요");

                    }else if(x.status == 404){
                        alert("페이즈를 찾을수 없습니다.");

                    }else if(x.status == 500){
                        alert("서버에러가 발생하였습니다.");

                    }else if(e == "parsererror"){
                        alert("Error. Parsing JSON Request failed.");

                    }else if(e == "timeout"){
                        alert("시간을 초과하였습니다.");

                    }else{
                        alert("알수없는 에러가 발생하였습니다.");
                        console.log(x.responseText);

                    }
                },
                complete: function () {
                    $.comm.wait(false);
                }
            });

            $('#'+formId).submit();
        },
        /***
         * 파일 다운로드
         * @param data
         * @param actNm
         * @param url
         */
        fileDownload : function (data, actNm, url) {
            if($.comm.isNull(url)) url = "/common/fileDownload.do";
            if($.comm.isNull(actNm)) actNm = "파일다운로드";

            if($.comm.isNull(data)){
                data = "";
            }

            data = typeof data == 'string' ? data : decodeURIComponent($.param(data));
            var inputs = '';
            $.each(data.split('&'), function(){
                var pair = this.split('=');
                inputs+='<input type="hidden" name="'+ pair[0] +'" value="'+ pair[1] +'" />';
            });

            inputs += $.page.createMenuInfoInputStr();
            inputs += $.page.createSessionDivInputStr();
            inputs += $.page.createActionNmInputStr(actNm);

            if ($('#iframe_file_download').length == 0) {
                var iframeref = $('<iframe style="display:none" height=0 width=0 id="iframe_file_download" name="iframe_file_download"></iframe>');
                $("body").append(iframeref);

            }

            $('<form action="'+ url +'" method="post" target="iframe_file_download">'+inputs+'</form>').appendTo('body').submit().remove();
        },
        getFormData : function (formId) {
            var paramObj = {};

            var selectBox = $("select[disabled=disabled]");
            selectBox.removeAttr("disabled");

            var a = $('#' + formId).serializeArray();
            $.each(a, function () {
                if(this.value != null && this.value != ''){
                    // 날짜필드이면 '-' 삭제
                    if($('#'+this.name).is('[datefield]')){
                        this.value = this.value.trim().replace(/\/|-/g, '');

                    // 숫자필드
                    }else if($('#'+this.name).is('[numberOnly]')){
                        this.value = $.comm.numberWithoutCommas(this.value.trim());
                    }

                    if(paramObj[this.name]) {
						if(typeof paramObj[this.name] == 'string'){
							paramObj[this.name] = [paramObj[this.name], this.value];
						}else{
							paramObj[this.name].push(this.value);
						}
		    		}else{
						paramObj[this.name] = this.value;	
		    		}
                }
            })

            selectBox.attr("disabled", true);
            return paramObj;
        },
        /**
         * 인자의 Object의 key를 ID로 가지는 객체(TAG)에 value를 지정
         *  - formIdArr 가 있을 경우 해당 폼을 reset
         * @param data : 바인드할 데이터 ( 데이터타입 : Object )
         * @param formIdArr : reset 할 form 정보 ( 테이터타입 : string or array )
         */
        bindData: function (data, formIdArr) {
            if (!$.comm.isNull(formIdArr)){
                if ($.type(formIdArr) == "string"){
                    $('#' + formIdArr)[0].reset();

                }else if ($.type(formIdArr) == 'array') {
                    $.each(formIdArr, function (i, formId) {
                        $('#' + formId)[0].reset();
                    })
                }
            }

            for(var key in data){
                var val = data[key];
                if($.comm.isNull(key) || $('#' + key).length == 0)
                    continue;

                var obj = $('#' + key)[0];
                if(obj.tagName.toLowerCase() == "span" || obj.tagName.toLowerCase() == "strong") {
                    $('#' + key).html(val);
                }else if(obj.tagName.toLowerCase() == "select"){
                    $('#' + key).val(val).attr("selected", "selected").trigger("chosen:updated");
                }else if(obj.tagName.toLowerCase() == "input" && obj.type.toLowerCase() == 'checkbox'){
                    $('input:checkbox[name="'+key+'"]').each(function() {
                        if(this.value == val){
                            this.checked = true;
                        }
                    });
                }else{
                    if($("#"+key).is("[datefield]")){
                        val = val.trim().replace(/\/|-/g, '');
                        if(val.length == 8){
                            val = val.toDate("YYYYMMDD").format("YYYY-MM-DD");
                        }
                    }

                    $('#' + key).val(val);
                }
            }
        },
        /***
         * select tag에 쿼리ID의 조회결과 를 바인드 한다.
         * @param targetId : select tag id
         * @param qKey     : 조회할 쿼리 ID
         * @param isAll    : '선택' option 을 추가할지 여부 (default : false)
         * @param likeCode : 공통코드의 CODE를 LIKE로 검색
         * @param likeName : 공통코드의 CODE_NM을 LIKE로 검색
         * @param filterStr : Filter 기능 (','(콤마) 를 기준으로 필터)
         * @param isAutoComplete : 자동완성기능 동작 여부( false - default, true )
         */
        bindCustCombo: function (targetId, qKey, isAll, likeCode, likeName, filterStr, isAutoComplete) {
            if($.comm.isNull(targetId)){
                alert("SELECT TAG ID 가 없습니다.(1번째 인자)");
                return;
            }
            if($.comm.isNull(qKey)){
                alert("쿼리 ID가 없습니다.(2번째 인자)");
                return;
            }

            var params = {"qKey":qKey};
            if(!$.comm.isNull(likeCode)){
                params["LIKE_CODE"] = likeCode;
            }
            if(!$.comm.isNull(likeName)){
                params["LIKE_NAME"] = likeName;
            }

            var ret = this.sendSync("/common/selectList.do", params);
            var items = ret.dataList;

            var obj = $('#' + targetId);

            $('option', obj).remove(); // 콤보 Remove

            if(isAll && isAll == true) {
                obj.append($('<option>', {
                    value: '',
                    text : '선택'
                }));
            }

            for(var i=0; i<items.length; i++){
                var item = items[i];

                if(!$.comm.isNull(filterStr)){
                    if(filterStr.indexOf(item.CODE) < 0){
                        continue;
                    }
                }
                obj.append($('<option>', {
                    value: item.CODE,
                    text : item.CODE_NM
                }));
            }

            if (isAutoComplete){
                obj.chosen();
                if($(obj).parent("li").length > 0){
                    $(obj).parent("li").find(".chosen-single").css("line-height", "28px");
                }
            }
        },
        /***
         * select tag에 공통코드를 Filter 문자열을 비교하여 해당 코드만 바인드 한다.
         * @param targetId : select tag id
         * @param qKey     : 조회할 쿼리 ID
         * @param isAll    : '선택' option 을 추가할지 여부 (default : false)
         * @param filterStr : Filter 기능 (','(콤마) 를 기준으로 필터)
         * @param isAutoComplete : 자동완성기능 동작 여부( false - default, true )
         */
        bindFilterCustCombo: function(targetId, qKey, isAll, filterStr, isAutoComplete){
            this.bindCustCombo(targetId, qKey, isAll, null, null, filterStr, isAutoComplete);
        },
        /***
         * select tag에 공통코드를 바인드 한다.
         * @param targetId : select tag id
         * @param std      : 공통코드 ID
         * @param isAll    : '선택' option 을 추가할지 여부 (default : false)
         * @param likeCode : 공통코드의 CODE를 LIKE로 검색
         * @param likeName : 공통코드의 CODE_NM을 LIKE로 검색
         * @param filterStr : Filter 기능 (','(콤마) 를 기준으로 필터)
         * @param valueType : option의 표현방식을 지정
         *                      ( 1 : CODE_NM - default, 2 : CODE, 3 : [CODE]CODE_NM )
         * @param isAutoComplete : 자동완성기능 동작 여부( false - default, true )
         */
        bindCombo: function (targetId, std, isAll, likeCode, likeName, filterStr, valueType, bindCombos, isAutoComplete) {
            if($.comm.isNull(targetId)){
                alert("SELECT TAG ID 가 없습니다.(1번째 인자)");
                return;
            }
            if($.comm.isNull(std)){
                alert("CLASS_ID가 없습니다.(2번째 인자)");
                return;
            }

        	if(bindCombos) {
        		bindCombos.addComboInfo([targetId, std, isAll, likeCode, likeName, filterStr, valueType]);
        		
        		return;
        	}
        	
            var params = {"CLASS_ID":std};
            if(!$.comm.isNull(likeCode)){
                params["LIKE_CODE"] = likeCode;
            }
            if(!$.comm.isNull(likeName)){
                params["LIKE_NAME"] = likeName;
            }

            var items = [];
            var commCodeObj = {};
            var cachedCommCode = $.comm.getGlobalVar("CMM_STD_CODE");

            if(!$.comm.isNull(cachedCommCode)){
                commCodeObj = JSON.parse(cachedCommCode);
            }

            if(!$.comm.isNull(commCodeObj[std])){
                items = commCodeObj[std];

            }else{
                var ret = this.sendSync("/common/selectCommCodeForCombo.do", params);
                items = ret.dataList;
                commCodeObj[std] = items;
                $.comm.setGlobalVar("CMM_STD_CODE", JSON.stringify(commCodeObj));
            }

            var obj = $('#' + targetId);

            var orgValue = obj.val();

            $('option', obj).remove(); // 콤보 Remove

            if(isAll && isAll == true) {
                obj.append($('<option>', {
                    value: '',
                    text : '선택'
                }));
            }

            for(var i=0; i<items.length; i++){
                var item = items[i];

                if(!$.comm.isNull(filterStr)){
                    if(filterStr.indexOf(item.CODE) < 0){
                        continue;
                    }
                }

                if($.comm.isNull(valueType))valueType = 1;

                var text = item.CODE_NM;
                if(valueType == 2){
                    text = item.CODE;

                }else if(valueType == 3){
                    text = item.COMPLX_CODE_NM;
                }
                obj.append($('<option>', {
                    value: item.CODE,
                    text : text
                }));
            }

            if(!$.comm.isNull(orgValue)){
                obj.val(orgValue);
            }

            if (isAutoComplete){
                obj.chosen();
                if($(obj).parent("li").length > 0){
                    $(obj).parent("li").find(".chosen-single").css("line-height", "28px");
                }
            }
        },
        /***
         * select tags에 공통코드를 바인드 한다.
         * LIKE 검색은 지원하지 않는다.
         */
        bindCombos : (function() {
    		var combosInfo = {};
    		this.draw = function() {
    			var codesInfo = [];
    			var params = {"CODES_INFO" : codesInfo};
    			
    			for (var i in combosInfo) {
    				codesInfo[codesInfo.length] = {'CLASS_ID' : combosInfo[i][1]};
    			}

				var ret = $.comm.sendSync("/common/selectCommCodesForCombos.do", params);
				items = ret.dataList;

	            var cachedCommCode = $.comm.getGlobalVar("CMM_STD_CODE");
	            var commCodeObj = {};

	            if(!$.comm.isNull(cachedCommCode)){
	                commCodeObj = JSON.parse(cachedCommCode);
	            }

    			for (var i in combosInfo) {
    				commCodeObj[combosInfo[i][1]] = [];
    			}

    			for (var i = 0; i < items.length; i++) {
    				commCodeObj[items[i]['CLASS_ID']][commCodeObj[items[i]['CLASS_ID']].length] = items[i];
    			}

    			$.comm.setGlobalVar("CMM_STD_CODE", JSON.stringify(commCodeObj));

    			for (var i in combosInfo) {
    	            var obj = $('#' + i);
    	            var isAll = combosInfo[i][2];
    	            var filterStr = combosInfo[i][3];
    	            var valueType = combosInfo[i][4];
    	            var isAutoComplete = combosInfo[i][5];

    	            $('option', obj).remove(); // 콤보 Remove

    	            if(isAll && isAll == true) {
    	                obj.append($('<option>', {
    	                    value: '',
    	                    text : '선택'
    	                }));
    	            }

    	            var items = commCodeObj[combosInfo[i][1]];

    	            for(var i=0; i<items.length; i++){
    	                var item = items[i];

    	                if(!$.comm.isNull(filterStr)){
    	                    if(filterStr.indexOf(item.CODE) < 0){
    	                        continue;
    	                    }
    	                }

    	                if($.comm.isNull(valueType))valueType = 1;

    	                var text = item.CODE_NM;
    	                if(valueType == 2){
    	                    text = item.CODE;

    	                }else if(valueType == 3){
    	                    text = item.COMPLX_CODE_NM;
    	                }
    	                obj.append($('<option>', {
    	                    value: item.CODE,
    	                    text : text
    	                }));
    	            }

    	            if (isAutoComplete){
                        obj.chosen();
                        if($(obj).parent("li").length > 0){
                            $(obj).parent("li").find(".chosen-single").css("line-height", "28px");
                        }
                    }
    			}

    			this.clearComboInfo();
    		};
    		
    		/***
             * select tag에 공통코드를 바인드 한다.
             * @param targetId : select tag id
             * @param std      : 공통코드 ID
             * @param isAll    : '선택' option 을 추가할지 여부 (default : false)
             * @param filterStr : Filter 기능 (','(콤마) 를 기준으로 필터)
             * @param valueType : option의 표현방식을 지정( 1 : CODE_NM - default, 2 : CODE, 3 : [CODE]CODE_NM )
             * @param isAutoComplete : 자동완성기능 동작 여부( false - default, true )
             */
    		this.addComboInfo = function(targetId, std, isAll, filterStr, valueType, isAutoComplete) {
	            var cachedCommCode = $.comm.getGlobalVar("CMM_STD_CODE");
	            var commCodeObj = {};

	            if(!$.comm.isNull(cachedCommCode)){
	                commCodeObj = JSON.parse(cachedCommCode);
	            }

	            //if($.comm.isNull(commCodeObj[std])){
	            	combosInfo[targetId] = [targetId, std, isAll, filterStr, valueType, isAutoComplete];
	            //}
    		};
    		
    		this.clearComboInfo = function() {
    			combosInfo = {};
    		};
    		
    		return this;
    	})(),        
        /***
         * select tag에 공통코드를 Filter 문자열을 비교하여 해당 코드만 바인드 한다.
         * @param targetId : select tag id
         * @param std      : 공통코드 ID
         * @param isAll    : '선택' option 을 추가할지 여부 (default : false)
         * @param filterStr : Filter 기능 (','(콤마) 를 기준으로 필터)
         * @param valueType : option의 표현방식을 지정
         *                      ( 1 : CODE_NM - default, 2 : CODE, 3 : [CODE]CODE_NM )
         */
        bindFilterCombo: function(targetId, std, isAll, filterStr, valueType){
            this.bindCombo(targetId, std, isAll, null, null, filterStr, ($.comm.isNull(valueType) ? 1 : valueType));
        },
        /***
         * 공통코드 반환한다.
         * @param std      : 공통코드 ID
         * @param likeCode : 공통코드의 CODE를 LIKE로 검색
         * @param likeName : 공통코드의 CODE_NM을 LIKE로 검색
         * @param filterStr : Filter 기능 (','(콤마) 를 기준으로 필터)
         */
        getCommCode: function (std, likeCode, likeName) {
            if($.comm.isNull(std)){
                alert("CLASS_ID가 없습니다.(1번째 인자)");
                return;
            }

            var params = {"CLASS_ID":std};
            if(!$.comm.isNull(likeCode)){
                params["LIKE_CODE"] = likeCode;
            }
            if(!$.comm.isNull(likeName)){
                params["LIKE_NAME"] = likeName;
            }

            var ret = this.sendSync("/common/selectCommCode.do", params);
            return ret.dataList;
        },
        getCommCodeToCombo: function (commCodeList) {
            var ret = [];
            $.each(commCodeList, function () {
                ret.push({"code":this.CODE, "value":this.CODE_NM});
            })

            return ret;
        },
        /***
         * 화면 이동시 사용하는 공통 함수
         * @param jsp    : jsp 명
         * @param params : 파라메터
         * @param div    : 화면구분 ( 메뉴에 등록 되어 있지 않은 화면을 로드시 인자로 넘긴다(M:어드민, W:사용자)
         */
        forward: function (jsp, params, div) {
            if($.comm.isNull(jsp)){
                alert("jsp 인자(첫번째 인자)는 필수 입니다.");
                return;
            }

            $("#commonForm")[0].reset();
            $("#commonForm").empty();

            for(var p in params){
                $("#commonForm").append($("<input type='hidden' name='" + p + "' value='" + params[p] + "' >"));
            }

            $("#commonForm").append($("<input type='hidden' name='IS_FRAME_PAGE' value='Y' >"));

            if(!$.comm.isNull(div)){
                $("#commonForm").append($("<input type='hidden' name='MENU_DIV' value='" + div + "' >"));
            }else{
                $("#commonForm").append($("<input type='hidden' name='MENU_DIV' value='" + $.comm.getGlobalVar("sessionDiv") + "' >"));
            }

            // 현재페이지 정보
            var inputParam = {};
            var inputData = $(':input');
            if(inputData != null){
                $.each(inputData, function () {
                    if(!$.comm.isNull(this.id)){
                        if(this.type == "checkbox"){
                            if($(this).is(":checked")){
                                inputParam[this.id] = this.value;
                            }
                        }else{
                            inputParam[this.id] = this.value;
                        }
                    }
                })
            }

            var selectData = $('select');
            if(selectData != null){
                $.each(selectData, function () {
                    if(!$.comm.isNull(this.id) && !$.comm.isNull(this.value)){
                        inputParam[this.id] = this.value;
                    }
                })
            }

            var menuId = $.page.getMenuId();
            var pageInitParam = $.comm.getGlobalVar("PAGE_PARAMETER");
            var pageObj = {};
            if(!$.comm.isNull(pageInitParam)){
                pageObj = JSON.parse(pageInitParam);

            }

            pageObj[menuId] = inputParam;

            $.comm.setGlobalVar("PAGE_PARAMETER", JSON.stringify(pageObj));

            var hrefArr = location.href.split('?');
            if(hrefArr.length > 1){
                $("#commonForm").append($("<input type='hidden' name='PRE_PAGE' value='" + hrefArr[1] + "' >"));
            }

            $("#commonForm").attr("method", "post");
            $("#commonForm").attr("action", "/jspView.do?jsp="+jsp).submit();

        },
        logout: function (url) {
            var form = $("<form>");

            $("body").append(form);

            form.append($("<input type='hidden' name='sessionDiv' value='" + $.comm.getGlobalVar("sessionDiv") + "' >"));
            form.attr("method", "post");
            form.attr("action", url).submit();
        },
        /***
         * 상세 페이지에서 목록 페이지로 (뒤로) 이동시 사용
         *  - 이전 페이지로 이동
         */
        pageBack: function () {
            var prePage = $('#PRE_PAGE').val();

            $("#commonForm")[0].reset();
            $("#commonForm").empty();

            var urlArr = prePage.split("&");
            var jsp = urlArr[0].split("=")[1];

            for(var i=1; i<urlArr.length; i++){
                var params = urlArr[i];
                $("#commonForm").append($("<input type='hidden' name='" + params.split("=")[0] + "' value='" + params.split("=")[1] + "' >"));
            }

            $("#commonForm").append($("<input type='hidden' name='IS_FRAME_PAGE' value='Y' >"));
            $("#commonForm").append($("<input type='hidden' name='IS_PAGE_BACK' value='T' >"));
            $("#commonForm").append($("<input type='hidden' name='sessionDiv' value='" + $.comm.getGlobalVar("sessionDiv") + "' >"));

            $("#commonForm").attr("method", "post");
            $("#commonForm").attr("action", "/jspView.do?jsp="+jsp).submit();
        },
        bindAsListener: function (func, obj) {
            return function () {
                return func.apply(obj, arguments);
            }
        },
        getTarget: function (event) {
            if (event == null) return null;
            if (event.target) return event.target;
            else if (event.srcElement) return event.srcElement;
            return null;
        },
        getAjaxData: function (obj, actNm) {
            var d = {};
            if(!this.isNull(obj)){
                if ($.type(obj) == 'object') {
                    d.data = obj;
                }
                else if ($.type(obj) == 'array') {
                    d.dataList = obj;
                } else {
                    throw Error('lllegal argument !!!!');
                }
            }
            d.status = '0';
            d.msg = '';

            if(this.isNull(d.data)){
                d.data = {};
            }else{
                var titleParam = {};
                for(var key in d.data){
                    if($("label[for='"+key+"']")){
                        titleParam[key] = $("label[for='"+key+"']").html();
                    }
                }

                d.data["titleParameter"] = titleParam;
            }

            if ($.page.existMenuId()) {
                d.data["ACTION_MENU_ID"] = $.page.getMenuId();
            }

            if ($.page.existMenuNm()) {
                d.data["ACTION_MENU_NM"] = $.page.getMenuNm();
            }

            if ($.page.existMenuDiv()) {
                d.data["ACTION_MENU_DIV"] = $.page.getMenuDiv();
            }

            if(!this.isNull(actNm)){
                d.data["ACTION_NM"] = actNm;
            }

            return encodeURIComponent((JSON.stringify(d)).split("null").join(''));
        },
        /***
         * 메시지 코드로 메시지를 호출
         * @param code  : 메세지 코드
         * @param argObj : 메세지의 아규먼트 ( string, Array)
         * @returns {*}
         */
        getMessage: function (code, argObj) {
            var msgObj = sessionStorage.getItem("MESSAGE_OBJ");

            if(msgObj == null){
                var data = $.comm.sendSync('/common/getMessage.do');
                if(data.data) {
                    sessionStorage.setItem("MESSAGE_OBJ", JSON.stringify(data.data));
                    msgObj = data.data;
                }
            }else{
                msgObj = JSON.parse(sessionStorage.getItem("MESSAGE_OBJ"));
            }

            try{
                var msg = msgObj[code];
                if(argObj != null) {
                    if (Array.isArray(argObj)) {
                        $.each(argObj, function (idx, a) {
                            msg = msg.replace('$' + (idx+1), a);
                        })
                    } else {
                        msg = msg.replace('$', argObj);
                    }
                }

                return msg.replace(/\\n/g, "\n");
            }catch(e){
                console.log("$.comm.getMessage() error : " + e);
                return '[' + code + "] 메세지가 존재하지 않습니다.";
            }
        },
        getGlobalVar: function (key) {
            return sessionStorage.getItem(key);
        },
        setGlobalVar: function (key, val) {
            return sessionStorage.setItem(key, val);
        },
        removeGlobalVar: function (key) {
            sessionStorage.removeItem(key);
        },
        /***
         * 인자의 byte 길이를 리턴
         * @param txt
         */
        bytelength: function (str, isUnicode) {
            if (isUnicode){
                var stringByteLength = (function (s, b, i, c) {
                    for (b = i = 0; c = s.charCodeAt(i++); b += c >> 11 ? 3 : c >> 7 ? 2 : 1);
                    return b
                })(str);

                return stringByteLength;

            }else{
                var byteNum=0;
                for(var i=0;i<str.length;i++){
                    byteNum+=(str.charCodeAt(i)>127)?2:1;
                }
                return byteNum;

            }
        },
        /***
         * 지정한 객체의 disable 처리
         * @param idObj : id 속성
         * @param bool  : true, flase
         */
        disabled: function (idObj, bool){
            var arr = [];
            if ($.type(idObj) == 'string') {
                arr[0] = idObj;
            }else if ($.type(idObj) == 'array') {
                arr = idObj;
            }else {
                throw Error('lllegal argument !!!!');
            }

            $.each(arr, function (idx, id) {
                var obj = $('#' + id)[0];

                if(obj.tagName.toLowerCase() == "input"){
                    $('#' + id).attr("readonly", bool);

                    if(bool == false){
                        $('#' + id).removeClass("readonly");

                    }else{
                        $('#' + id).addClass("readonly");

                    }

                }else if(obj.tagName.toLowerCase() == "a" || obj.tagName.toLowerCase() == "button" || obj.tagName.toLowerCase() == "select"){
                    if(bool) {
                        $('#' + id).attr("disabled", bool);
                        if(obj.tagName.toLowerCase() == "select"){
                            $('#' + id).css("background-color", "#f6f6f6");
                        }
                    }else{
                        $('#' + id).removeAttr("disabled");
                        if(obj.tagName.toLowerCase() == "select") {
                            $('#' + id).css("background-color", "#ffffff");
                        }
                    }
                }
            })
        },
        /***
         * 지정한 객체의 enable 처리
         * @param idObj : id 속성
         * @param bool  : true, flase
         */
        enabled: function (idObj, bool) {
            this.disabled(idObj, !bool);
        },
        /***
         * 지정한 객체의 display를 조정
         * @param idObj : id 속성
         * @param bool  : true-show, false-hide
         */
        display: function (idObj, bool) {
            var arr = [];
            if ($.type(idObj) == 'string') {
                arr[0] = idObj;
            }else if ($.type(idObj) == 'array') {
                arr = idObj;
            }else {
                throw Error('lllegal argument !!!!');
            }

            $.each(arr, function (idx, id) {
                if(bool == true){
                    $('#' + id).show();
                }else{
                    $('#' + id).hide();
                }
            })
        },
        readonly: function (idObj, bool) {
            var arr = [];
            if ($.type(idObj) == 'string') {
                arr[0] = idObj;
            }else if ($.type(idObj) == 'array') {
                arr = idObj;
            }else {
                throw Error('lllegal argument !!!!');
            }

            $.each(arr, function (idx, id) {
                if(bool == false){
                    $('#' + id).removeClass("readonly");
                }else{
                    $('#' + id).addClass("readonly");
                }

                $('#' + id).attr("readonly", bool);
            })
        },
        getJQueryObject: function (n) {
            var o = null;

            if ($.type(n) === 'object') {
                o = $(n);
            }
            else if ($.type(n) === 'string') {
                o = $('#' + n);
                if (o.length == 0) {
                    o = $('[name=' + n + ']');
                    if (o.length > 0) {
                        o = $(o.get(0));
                    }
                }
            } else {
                throw Error('lllegal argument !');
            }

            return o;
        },
        /***
         * 인자가 Function 객체인지 여부를 리턴
         * @param v
         * @returns {boolean}
         */
        /*isFunction: function (v) {
            if ($.type(v) === 'string') {
                try {
                    if ($.type(eval(v)) === 'function') {
                        return true;
                    } else {
                        return false;
                    }
                } catch (e) {
                    return false
                }
            } else {
                if ($.type(v) === 'function') {
                    return true;
                } else {
                    return false;
                }
            }
        },*/
        getElIdOrNm: function (e) {
            var id = e.attr('id');
            if ($.type(id) === 'undefined') {
                id = e.attr('nm');
            }
            return id;
        },
        getElNmOrId: function (e) {
            var nm = e.attr('name');
            if ($.type(nm) === 'undefined') {
                nm = e.attr('id');
            }
            return nm;
        },
        isNull: function (obj) {
            if($.type(obj) === 'undefined' || $.type(obj) === 'null') return true;
            if (obj == null) return true;
            if (obj == "NaN") return true;
            if (new String(obj).valueOf() == "undefined") return true;
            var chkStr = new String(obj);
            if( chkStr.valueOf() == "undefined" ) return true;
            if (chkStr == null) return true;
            if (chkStr.toString().length == 0 ) return true;
            return false;
        },
        /***
         * 필수 항목 표시
         *  - TAG 속성에 mandantory 속성이 있으면
         *    해당 TAG의 ID를 for 속성을 가진 label의 TEXT의 color를 변경
         */
        mandantoryExp : function () {
            var selector = $("*[mandantory]");
            selector.each(function(idx, oinput){
                var id = $(oinput).get(0).id;
                if($("label[for='"+id+"']")){

                    var div = $.comm.getGlobalVar("sessionDiv");
                    if(div == 'W'){ // 사용자 업무 사이트만 타이틀을 빨간색으로
                        //$("label[for='"+id+"']").css("color", "#e64e4e");
                        $("label[for='"+id+"']").parent("td").css("color", "#e64e4e");
                    }
                    $("label[for='"+id+"']").parent("td").find("span").remove();
                    $("label[for='"+id+"']").parent("td").append("<span> *</span>");
                }
            });

        },
        /**
         * 필수항목 지정/해제
         * @param idObj : 필수항목 지정/해제할 id (string or array)
         * @param bool : true / false
         */
        setMandantory : function (idObj, bool) {
            var arr = [];
            if ($.type(idObj) == 'string') {
                arr[0] = idObj;
            }else if ($.type(idObj) == 'array') {
                arr = idObj;
            }else {
                throw Error('lllegal argument !!!!');
            }

            $.each(arr, function (idx, id) {
                if(bool == true){
                    $('#' + id).attr("mandantory", "true");

                    if($("label[for='"+id+"']")){
                        var div = $.comm.getGlobalVar("sessionDiv");
                        if(div == 'W'){ // 사용자 업무 사이트만 타이틀을 빨간색으로
                            $("label[for='"+id+"']").css("color", "#e64e4e");
                        }
                        $("label[for='"+id+"']").parent("td").find("span").remove();
                        $("label[for='"+id+"']").parent("td").append("<span> *</span>");
                    }

                }else{
                    $('#' + id).removeAttr("mandantory");

                    if($("label[for='"+id+"']")){
                        var div = $.comm.getGlobalVar("sessionDiv");
                        if(div == 'W'){ // 사용자 업무 사이트만 타이틀을 빨간색으로
                            $("label[for='"+id+"']").css("color", "#555");
                        }
                        $("label[for='"+id+"']").parent("td").find("span").remove();
                    }
                }
            })
        },
        /***
         * 기간을 가지는 날짜필드 셋팅
         * @param dateIds[Array] : 각 index에 인자는 하이푼으로 구분('formDateId-toDateId)
         */
        dueCalendar : function (dateIds) {
            for(var i=0; i<dateIds.length; i++){
                var due = dateIds[i];
                var fromDateId = due.split("-")[0];
                var toDateId = due.split("-")[1];

                $('#'+fromDateId).datepicker("option", "onClose",
                    function (selectedDate) {
                        if(selectedDate.length == 8){
                            selectedDate = selectedDate.toDate(null).format("YYYY-MM-DD");
                        }else{
                            var val = selectedDate.trim().replace(/\/|-/g, '');
                            selectedDate = val.toDate("YYYYMMDD").format("YYYY-MM-DD");
                        }
                        $('#' + toDateId).datepicker( "option", "minDate", selectedDate );
                    }
                );
                $('#'+toDateId).datepicker("option", "onClose",
                    function (selectedDate) {
                        if(selectedDate.length == 8){
                            selectedDate = selectedDate.toDate(null).format("YYYY-MM-DD");
                        }else{
                            var val = selectedDate.trim().replace(/\/|-/g, '');
                            selectedDate = val.toDate("YYYYMMDD").format("YYYY-MM-DD");
                        }
                        $('#' + fromDateId).datepicker( "option", "maxDate", selectedDate );
                    }
                );
            }
        },
        /***
         * 1. TAG 속성에 datefield 속성이 있으면 해당 TAG에 CALENDAR 기능을 적용
         *   - datefield의 값이 존재 하면 기본 날짜를 셋팅
         *   - 숫자+속성 : 숫자는 현재날짜를 기준으로 속성에 따라 가감
         *   - 예) 7일전 : '-7'
         *         2달전 : '-2m'
         *         1년전 : '-1y'
         *  2. TAG 속성에 toDataefield 속성이 있으면 해당 TAG와 toDataefield 속성의 값에 지정된 TAG를 dueCalendar로 지정 ( dueCalendar함수를 호출 )
         *    - 기간의 자동 유효성 검사가 됨
         */
        initCalendar : function () {
            var dueDateArr = new Array();
            var selector = $("*[datefield]");


            selector.each(function(idx, oinput){
                var id = $(oinput).get(0).id;
                //$("#"+id).attr("style", "width: 100px; margin-right: 5px");

                $("#"+id).datepicker();

                $("#"+id).css("datepicker");

                // onBlur시 날짜 유효성 검사
                $("#"+id).blur(function() {
                    if(!$.date.isValidDate(this.value)){
                        this.value = "";
                    }else{
                        var val = this.value.trim().replace(/\/|-/g, '');
                        if(val.length == 8){
                            this.value = val.toDate("YYYYMMDD").format("YYYY-MM-DD");
                        }
                    }
                });

                var dateVal = $("#"+id).attr("datefield");
                if(!$.comm.isNull(dateVal)){
                    var currDate = new Date();
                    var pattern = "d"; // default 일
                    if(dateVal.indexOf("m") > 0){
                        pattern = "M";
                        dateVal = dateVal.replace("m", "");

                    }else if(dateVal.indexOf("y") > 0){
                        pattern = "y";
                        dateVal = dateVal.replace("y", "");

                    }

                    var amount = parseInt($.trim(dateVal));
                    var val = currDate.dateAdd2(pattern, amount);

                    $("#"+id).datepicker("setDate", val);
                }

                var toDateId = $("#"+id).attr("toDataefield");
                if(!$.comm.isNull(toDateId)){
                    dueDateArr.push(id+"-"+toDateId);
                }
            });
            
            if(dueDateArr.length > 0) this.dueCalendar(dueDateArr);
        },
        /***
         * TAG 속성에 selectfield 속성이 있으면 해당 TAG의 ID로 공통코드를 바인드
         *   - bindCombo 함수의 간략하게 설정 하기 위함
         *   - bindCombo(해당ID, 해당ID, true) 로 호출
         */
        initSelect : function () {
            var selector = $("*[selectfield]");
            selector.each(function(idx, oinput){
                var id = $(oinput).get(0).id;
                var viewType = $('#' + id).attr("selectfield");
                $.comm.bindCombo(id, id, true, null, null, null, viewType);
            });
        },
        /***
         * TAG 속성에 length 속성을 갖는 객체의 데이터 길이를 minlength, maxlength 속성 셋팅
         *   - length의 값은 minlength와 maxlength를 ','(콤마)를 구분하여 지정하며 콤마 없이
         *      하나의 숫자만 입력하면 maxlength만 체크
         *   - 예) min/max체크 : length="3,10" =>입력값이 3자리 이상 10자리 이하여야 함
         *         max체크 : length="10" => 입력값이 10자리 이하여야함
         * ※ 길이는 byte 길이
         */
        initLength : function () {
            function init(selector) {
                selector.each(function(idx, obj){
                    var id = $(obj).get(0).id;
                    var e = $(obj);
                    if(e.get(0).tagName.toLowerCase() == 'input' && e.attr('type').toLowerCase() == 'text'){
                        var len = $('#' + id).attr("length");

                        var arr = len.split(",");
                        if(arr.length == 0){
                            alert("length의 속성값을 확인 하세요");
                            return false;
                        }

                        // maxlength 만 체크
                        if(arr.length == 1){
                            $('#' + id).attr('maxlength', arr[0]);

                        }
                        if(arr.length == 2){
                            $('#' + id).attr('minlength', arr[0]);
                            $('#' + id).attr('maxlength', arr[1]);
                        }
                    }
                })
            }

            init($("*[length]"));
        },
        validation: function (formId) {
            // 필수값 체크
            if(this.mandCheck(formId) == false){
                return false;
            }

            // 입력값 길이 체크
            if(this.lengthCheck(formId) == false){
                return false;
            }

            return true;
        },
        /***
         * TAG 속성에 length 속성을 갖는 객체의 데이터 길이 체크
         *   - length의 값은 minlength와 maxlength를 ','(콤마)를 구분하여 지정하며 콤마 없이
         *      하나의 숫자만 입력하면 maxlength만 체크
         *   - 예) min/max체크 : length="3,10" =>입력값이 3자리 이상 10자리 이하여야 함
         *         max체크 : length="10" => 입력값이 10자리 이하여야함
         * ※ 길이는 byte 길이
         */
        lengthCheck: function (formId) {
            var frm = new JForm();
            var selector;

            function init(selector) {
                selector.each(function(idx, obj){
                    var id = $(obj).get(0).id;

                    var name = id;
                    if($("label[for='"+id+"']")){
                        name = $("label[for='"+id+"']").html();
                    }

                    var e = $(obj);
                    if(e.get(0).tagName.toLowerCase() == 'textarea' ||
                        (e.get(0).tagName.toLowerCase() == 'input' && e.attr('type').toLowerCase() == 'text')){
                        var len = $('#' + id).attr("length");

                        var isUnicode = false;
                        if ($('#' + id).is("[unicode]")){
                            isUnicode = Boolean($('#' + id).attr("unicode"));
                        }

                        frm.add(new JLength(name, id, len, isUnicode));
                    }
                })
            }

            if($.comm.isNull(formId)){
                selector = $("*[length]");
            }else{
                selector = $('#'+formId).find("*[length]");
            }

            init(selector);

            return frm.validate();
        },
        /***
         * TAG 속성에 mandantory 속성을 갖는 객체의 필수 체크
         */
        mandCheck: function (formId) {
            var frm = new JForm();
            var selector;

            if($.comm.isNull(formId)){
                selector = $("*[mandantory]");
            }else{
                selector = $('#'+formId).find("*[mandantory]");
            }

            selector.each(function(idx, obj){
                var id = $(obj).get(0).id;

                var name = "";
                if($("label[for='"+id+"']")){
                    name = $("label[for='"+id+"']").html();
                }

                //Title이 없으면 체크안함
                if(!$.comm.isNull(name)){
                    var e = $(obj);
                    if(e.get(0).tagName.toLowerCase() == 'input'){

                        if(e.attr('type').toLowerCase() == 'text'){
                            frm.add(new JText(name, id));
                        }else if(e.attr('type').toLowerCase() == 'checkbox'){
                            //TODO 구현해야함
                        }
                    }else if(e.get(0).tagName.toLowerCase() == 'select'){
                        frm.add(new JSelect(name, id));
                    }
                }
            });

            return frm.validate();
        },
        dueCalendarCheck : function (formId) {
            var a = $('#' + formId).serializeArray();
            var bool = true;
            $.each(a, function () {
                if ($('#'+this.name).is('[datefield]') && $('#'+this.name).is('[todataefield]')) {
                    var fromDate = this.value;
                    var toDate = $('#'+$('#'+this.name).attr("todataefield")).val();

                    if($.comm.isNull(fromDate) && !$.comm.isNull(toDate)){
                        $(this).focus();
                        bool = false;
                    }
                    if(!$.comm.isNull(fromDate) && $.comm.isNull(toDate)){
                        $('#'+$('#'+this.name).attr("todataefield")).focus();
                        bool = false;
                    }

                    if(bool == false){
                        alert($.comm.getMessage("W00000039", $("label[for='"+this.name+"']").html()));//날짜기간을 확인하세요.[$];
                        return bool;
                    }
                }
            })

            return bool;
        },
        initBtnAuth: function (menuAuth, menuId) {
            if($.comm.isNull(menuAuth) || $.comm.isNull(menuId)){
                return;
            }
            var auth = JSON.parse(menuAuth);
            var data = auth[menuId];

            $.each(data, function () {
                var btnId = this.toString();
                $('#' + btnId).remove();
            })
        },
        phoneFormat : function(id){
            var num = $('#' + id).val().replace(/\/|-/g, '');;

            if($.comm.isNull(num)) return;

            var phone_num = num.replace(/(^02.{0}|^01.{1}|[0-9]{3})([0-9]+)([0-9]{4})/, "$1-$2-$3");
            $('#' + id).val(phone_num);
        },
        /***
         * window.open 을 호출한다.
         * @param id  :팝업창의 id
         * @param url : url
         * @param width : 팝업창의 넓이
         * @param height : 팝업창의 높이
         */
        open : function (id, url, width, height) {
            $.fn.popupwindow({
                url:url,
                windowName:id+"_popup",
                center:1,
                height : height,
                width : width,
                dimmed : 0
            });
        },
        /**
         * POST 방식으로 window open
         * @param url
         * @param name
         * @param params
         */
        postOpen : function (url, name, params) {
            var formId = "WINDOW_POST_OPEN_ID_123456789";

            var submitForm = $('#'+formId);
            if(submitForm.length > 0){
                submitForm.remove();
            }

            submitForm = $("<form>");
            submitForm.attr("id", formId);
            submitForm.attr("method", "POST");
            submitForm.attr("action", url);
            submitForm.attr("target", name);

            $("body").append(submitForm);

            function createHidden(pObj, id, value) {
                var input = $("<input>");
                input.attr("name", id);
                input.attr("id"  , id);
                input.attr("type", "hidden");

                var val = (JSON.stringify(value)).split("null").join('');
                if(val.substring(0, 1) == '"') val = val.substring(1, val.length);
                if(val.substring(val.length -1, val.length)) val = val.substring(0, val.length-1);

                input.val(val);

                $(pObj).append(input);
            }

            for(var key in params) {
                if (params[key] instanceof Object || params[key] instanceof Function){
                } else {
                    createHidden(submitForm, key, params[key]);
                }
            }

            //window.open("", name, "dependent=no,top=10,left=10,directories=yes,menubar=yes,toolbar=yes,location=yes,status=yes,resizable=yes");
            submitForm.submit();
        },
        /***
         * window.showModalDialog와 같이 팝업창으로 인자를 넘기고
         * 팜업창이 닫힐때 지정된 callback 함수로 리턴값을 넘긴다.
         * @param url : url
         * @param spec : 팝업창의 feature
         * @param callBack : callback 함수
         * @param dimmed : 백그라운드 처리 여부 (default : true)
         */
        dialog : function (url, spec, callBack) {
            var profile ={
                url:url,
                createnew:1,
                center:1,
                height:740,
                width:700,
                scrollbars:0,
                resizable:1,
                status:0,
                dimmed:1,
                onUnload:(callBack ? callBack : function () {})
            };

            var mdattrs = spec.split(";");
            for (var i = 0; i < mdattrs.length; i++) {
                var mdattr = mdattrs[i].split(":");

                var n = mdattr[0];
                var v = mdattr[1];
                if (n) {
                    n = n.trim().toLowerCase();
                }
                if (v) {
                    v = v.trim().toLowerCase();
                }

                if (n == "dialogheight" || n == "height") {
                    profile.height = v.replace("px", "");
                } else if (n == "dialogwidth" || n == "width") {
                    profile.width = v.replace("px", "");
                } else if (n == "resizable") {
                    profile.resizable = v;
                } else if (n == "scroll") {
                    profile.scrollbars = v;
                } else if (n == "status") {
                    profile.status = v;
                } else if (n == "windowname") {
                    profile.windowName = v;
                } else {
                    // no-op
                }
            }

            // 이전 사용한 'ModalReturnVal'이 남아 있을수 있어 삭제
            $.comm.removeGlobalVar("PopUpWindowReturnVal");

            $.fn.popupwindow(profile);
        },
        setDimmed : function (bool) {
            var val = (bool ? "block" : "none");
            var winDim = $("#main_win_dim");
            if(winDim.length > 0){
                winDim.css("display", val);
            }else{
                var str = "parent";
                var cnt = 10;
                while (cnt > 0) {
                    var obj = eval(str + ".document");
                    if ($(obj).find('#main_win_dim').length > 0) {
                        winDim = $(obj).find('#main_win_dim');
                        break;
                    }

                    str = str + ".parent";
                    cnt--;
                }

                winDim.css("display", val);
            }
        },
        wait : function (bool) {
            if($('.dim_laoding').length == 0) return;

            var dimCnt = $.comm.getGlobalVar("dimCnt");
            if($.comm.isNull(dimCnt)) dimCnt = 0;
            else dimCnt = parseInt(dimCnt);

            //console.log(bool + " / dimCnt : " + dimCnt);

            if(bool){
                if(dimCnt == 0) $.comm.setGlobalVar("dimCnt", 1);
                else $.comm.setGlobalVar("dimCnt", dimCnt+1);

                $('.dim_laoding').show();
            }else{
                if(dimCnt <= 1) {
                    $.comm.setGlobalVar("dimCnt", 0);
                    $('.dim_laoding').hide();
                }else{
                    $.comm.setGlobalVar("dimCnt", dimCnt-1);
                }
            }
        },
        /**
         * layerPopup을 이용한 dialog
         * @param pMsg : '\n'을 구분자로 new line 생성됨
         * @param pTitle : 알림의 타이틀 (defualt : 알림)
         * @param width : 알림의 넓이 (duaflt : 400px)
         * @param height : 알림의 높이 (duaflt : 220px)
         */
        alert : function (pMsg, pTitle, width, height) {
            var wTop = $(window).scrollTop();
            var url = '/layer/dialog.html';

            var body      = $('body');
            var container = $('#container');
            var layer     = $('#layer');
            var header    = $('#header');
            if(layer.length == 0){
                var str = "parent";
                var cnt = 10;
                while(cnt > 0){
                    var obj = eval(str + ".document");
                    if($(obj).find('#layer').length > 0){
                        layer     = $(obj).find('#layer');
                        body      = $(obj).find('body');
                        container = $(obj).find('#container');
                        header    = $(obj).find('#header');
                        break;
                    }

                    str = str + ".parent";
                    cnt--;
                }
            }

            layer.load(url, function() {
                layer.fadeIn(300);
                body.addClass('layer');
                container.css('top', -wTop + 'px');
                $(".layer_content.inner-box").niceScroll();
            });

            header.css('top', '0');

            setTimeout(function(){
                var dialog    = $('.layerContainer.dialog');
                var content   = $('.layerContainer.dialog > .layer_content');
                var title     = $('.layerContainer.dialog > .layerTitle > h1');
                if(title.length == 0){
                    var str = "parent";
                    var cnt = 10;
                    while(cnt > 0){
                        var obj = eval(str + ".document");
                        if($(obj).find('#layer').length > 0){
                            dialog    = $(obj).find('.layerContainer.dialog');
                            content   = $(obj).find('.layerContainer.dialog > .layer_content');
                            title     = $(obj).find('.layerContainer.dialog > .layerTitle > h1')
                            break;
                        }

                        str = str + ".parent";
                        cnt--;
                    }
                }

                if(!$.comm.isNull(width)){
                    dialog.css('width', width+"px");
                }
                if(!$.comm.isNull(height)){
                    dialog.css('height', height+"px");
                }

                if(!$.comm.isNull(pTitle)){
                    title.html(pTitle);
                }else{
                    title.html('알림');
                }

                if(!$.comm.isNull(pMsg)){
                    var arr = pMsg.split('\n');
                    var msgStr = "";
                    $.each(arr, function () {
                        msgStr +="<p>"+this.toString()+"</p>";
                    })
                    content.empty();
                    content.html(msgStr);
                }
            }, 400);
        },
        /***
         * modal을 호출할때 팝업창의 인자를 지정
         * @param args : 인자 Object
         */
        setModalArguments : function (obj) {
            if ($.type(obj) == 'object' || $.type(obj) == 'array') {
                obj = JSON.stringify(obj);
            }
            $.comm.setGlobalVar("PopUpWindowArguments", obj);
        },
        /***
         * 팝업창에서 인자를 받는 함수
         * @returns {*}
         */
        getModalArguments : function () {
            var ret = opener.$.comm.getGlobalVar("PopUpWindowArguments");
            if ($.type(ret) == 'string' && (ret.indexOf("{") == 0 || ret.indexOf("[") == 0)) {
                ret = JSON.parse(ret);
            }

            return ret;
        },
        /***
         * 팝업창에서 부모창으로 리턴값을 지정
         * @param obj : 결과값 Object or Array
         */
        setModalReturnVal : function (obj) {
            if ($.type(obj) == 'object' || $.type(obj) == 'array') {
                obj = JSON.stringify(obj);
            }
            opener.$.comm.setGlobalVar("PopUpWindowReturnVal", obj);
        },
        /***
         * 팝업창을 호출한 화면에서 결과값을 반환 받는다
         * @returns {*}
         */
        getModalReturnVal : function () {
            var ret = $.comm.getGlobalVar("PopUpWindowReturnVal");
            if ($.type(ret) == 'string' && (ret.indexOf("{") == 0 || ret.indexOf("[") == 0)) {
                ret = JSON.parse(ret);
            }

            $.comm.removeGlobalVar("PopUpWindowReturnVal");

            return ret;
        },
        /***
         * 공통코드 조회 팝업을 호출
         * @param classId : 공통코드 CLASS ID
         * @param callback : 결과값을 받을 cllback 함수
         * @param initCode : 공통코드 팝업창에서 '코드' 조회값을 지정
         * @param initCodeNm : 공통코드 팝업창에서 '코드명' 조회값을 지정
         */
        commCodePop : function (classId, callback, initCode, initCodeNm) {
            var args = {"CLASS_ID":classId};
            if($.comm.isNull(initCode)){
                args["CODE"] = initCode;
            }
            if($.comm.isNull(initCodeNm)){
                args["CODE_NM"] = initCodeNm;
            }

            $.comm.setModalArguments(args);
            var spec = "dialogWidth:800px;dialogHeight:840px;scroll:auto;status:no;center:yes;resizable:yes;windowName:COMM_CODE_POPUP";
            $.comm.dialog("/jspView.do?jsp=cmm/popup/commPopup", spec, callback);
        },
        /***
         * 숫자 3자리마다 콤마를 찍는다.
         * @param x
         * @returns {string}
         */
        numberWithCommas : function (x) {
            return String(x).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
        },
        /***
         * 숫자 콤마를 삭제
         * @param x
         * @returns {string}
         */
        numberWithoutCommas : function (x) {
            return String(x).replace(/(,)/g, "");
        },
        /**
         * 트리 생성
         * @param treeDivId : 트리를 생성할 div Id
         * @param treeDataList : 트리를 생성할 데이터 리스트
         * @param levelColId  : Level 컬럼 Id
         * @param nameColId : 트리 Leaf 명 Id
         * @param onclickFunctionNm : 트리 Leaf 클릭시 호출 함수명
         * @param functionParamColId1 : 트리 Leaf 클릭시 함수 인자1
         * @param functionParamColId2 : 트리 Leaf 클릭시 함수 인자2
         * @param functionParamColId3 : 트리 Leaf 클릭시 함수 인자3
         * @param isCheck : 트리에 체크박스 생성 여부 (default : false)
         * @param checkFnObj : 트리에 체크박스가 있을때 체크박스 클릭시 호출 함수
         * @param treeOption : dTree Option
         */
        drawTree: function(treeDivId, treeDataList, levelColId, nameColId, onclickFunctionNm, functionParamColId1, functionParamColId2, functionParamColId3, isCheck, checkFnObj, treeOption){
            $("#" + treeDivId).empty();

            var preMenuLevel = 0;
            var ul = $("<ul>");
            var li1, li2, li3, li4;
            var ul1, ul2, ul3;
            $.each(treeDataList, function (index, data) {
                var menuLevel = data[levelColId];
                var menuNm    = data[nameColId];
                var chk       = data["CHK"];

                var anchor = $('<a>');

                if(!$.comm.isNull(onclickFunctionNm)){
                    anchor.on('click', function () {
                        $("li > a[style='font-weight: bold;']").css("font-weight", "normal");
                        $(this).css("font-weight", "bold");
                    })
                    anchor.attr("onclick", onclickFunctionNm+"('" + data[functionParamColId1] + "', '" + data[functionParamColId2] +"', '" + data[functionParamColId3] +"')");
                }
                anchor.html(menuNm);

                var check;
                if(isCheck && isCheck == true){
                    check = $("<input>");
                    check.attr("type", "checkbox");
                    check.attr("name", "check");
                    check.attr("id", data[functionParamColId1]);

                    check.attr("style", "display: inline-block");

                    // Menu tree일때 사용하기위함
                    if(functionParamColId2 == "PMENU_ID"){
                        check.attr("pid", data[functionParamColId2]);
                    }

                    if(chk == "1"){
                        check.attr("checked", "checked");
                    }
                }

                if(menuLevel != 0 && preMenuLevel != 0 &&
                    menuLevel < preMenuLevel){

                    ul.append(li1);
                }
                if(menuLevel == 0){
                    var li = $('<li>');
                    li.html("&nbsp;");
                    li.append(anchor);

                    ul.append(li);

                }else if(menuLevel == 1){
                    li1 = $('<li>');
                    if(check)li1.append(check);
                    li1.append(anchor);

                    ul.append(li1);
                }else if(menuLevel == 2){
                    li2 = $('<li>');
                    if(check)li2.append(check);
                    li2.append(anchor);

                    if(preMenuLevel < menuLevel){
                        ul1 = $('<ul>');
                        li1.append(ul1);
                    }

                    ul1.append(li2);
                }else if(menuLevel == 3){
                    li3 = $('<li>');
                    if(check)li3.append(check);
                    li3.append(anchor);

                    if(preMenuLevel < menuLevel){
                        ul2 = $('<ul>');
                        li2.append(ul2);
                    }

                    ul2.append(li3);

                }else if(menuLevel == 4){
                    li4 = $('<li>');
                    if(check)li4.append(check);
                    li4.append(anchor);

                    if(preMenuLevel < menuLevel){
                        ul3 = $('<ul>');
                        li3.append(ul3);
                    }

                    ul3.append(li4);
                }

                preMenuLevel = menuLevel;

            });

            $('#'+treeDivId).attr("class", "dTree");
            $('#'+treeDivId).append(ul);
            $('#'+treeDivId).dTree(treeOption);


            if(!$.comm.isNull(checkFnObj)){
                (function(checkFnObj) {
                    $('input[name="check"]').click(function () {
                        return checkFnObj.call(this);
                    });
                })(checkFnObj);
            }
        },
        /**
         * nicEditor 생성
         * @param id : 내용영역 id
         * @param height : 내용영역 높이
         */
        editor : function (id, height) {
            var nEditor = new nicEditor();
            nEditor.panelInstance(id);
            if(!$.comm.isNull(height)){
                $(nEditor.nicInstances[0].elm).css("max-height", height+"px");
            }
            $(nEditor.nicInstances[0].elm).css("overflow", "auto");
            $(nEditor.nicInstances[0].elm).css("padding", "5px");

            return nEditor;
        },
        /**
         * nicEditor의 내용을 리턴
         * @param id
         * @returns {*}
         */
        getContents : function (id) {
            return nicEditors.findEditor(id).getContent();
        },
        /**
         * nicEditor의 내용을 바인딩
         * @param id
         * @param html
         * @returns {*}
         */
        setContents : function (id, html) {
            nicEditors.findEditor(id).setContent(html);
        },
        /**
         * nicEditor의 내용을 textarea에 적용
         * @param id
         * @returns {*}
         */
        saveContent : function (id) {
            return nicEditors.findEditor(id).saveContent();
        },
        /**
         * 페이지 forward 후 다시 로드 될때 파라미터 셋팅 수동
         */
        initPageParam : function () {
            try {
                var inputData = PAGE_GLOBAL_VAR["PAGE_PARAM"];
                if (!$.comm.isNull(inputData)) {
                    $.comm.bindData(inputData);
                }
            }catch(e){console.log(e)}
        },
        /**
         * 페이지 forward 후 다시 로드 될때 파라미터 리턴
         */
        getInitPageParam : function () {
            try {
                if(PAGE_GLOBAL_VAR){
                    var inputData = PAGE_GLOBAL_VAR["PAGE_PARAM"];
                    if ($.comm.isNull(inputData)) {
                        return null;
                    }

                    return inputData;
                }

                return null;
            }catch(e){}
        },
        /**
         * 팝업등에서 메인화면의 location을 url로 이동
         * @param url
         */
        mainLocation : function (url) {
            if(!$.comm.isNull(window.opener)) {
                window.opener.$.comm.mainLocation(url);
                window.close();
            }else{
                if(!$.comm.isNull(window.parent)){
                    window.parent.location.href = url;
                }else{
                    window.location.href = url;
                }
            }
        },
        chartColor : function(index){
            var arr = ["#15a4fa", "#00bcd5", "#8bc24a", "#cddc39", "#dcb339"];

            if(index >= arr.length){
                index = 0;
            }

            return arr[index];
        },
        chartOptions : function(){
            var options = {
                width: 295,
                height: 250,
                bar: {groupWidth: "60%"},
                legend: { position: "none" },
                chartArea: {top:19,width:'70%'},
                backgroundColor:'none'
            };

            return options;
        },
        chartView : function (dataTable){
            var data = google.visualization.arrayToDataTable(dataTable);

            var view = new google.visualization.DataView(data);
            view.setColumns([0, 1,
                { calc: "stringify",
                    sourceColumn: 1,
                    type: "string",
                    role: "annotation" },
                2]);

            return view;
        },
        /**
         * 조회영역 PK 로직 설정
         */
        initWrapSearch : function(){
            var pk = $("*[pk]");

            var forms = [];
            pk.each(function(idx, obj){
                forms.push($(obj).closest("form"));
            })

            $.each(forms, function(i, obj){
                new wrapSearch($(obj).attr("id"));
            })
        },
        /**
         * Tooltip 설정
         * @param type : light, success, info, warning, danger
         * @param objIdArr : id ( string or array )
         * @param msgArr   : message ( string or array)
         */
        tooltip : function (type, objIdArr, msgArr) {
            var arr = [];
            var msg = [];
            if ($.type(objIdArr) == "string") {
                arr[0] = objIdArr;

            }else if ($.type(objIdArr) != "array"){
                throw Error('lllegal argument !');

            }
            if ($.type(msgArr) == "string") {
                msg[0] = msgArr;

            }else if ($.type(msgArr) != "array"){
                throw Error('lllegal argument !');

            }

            for (var index in arr){
                var obj = $('#'+arr[index]);
                var m = obj.val();
                if (!$.comm.isNull(msg[index])){
                    m = msg[index];
                }

                var chosenObj = obj.next("#"+obj.attr("id")+"_chosen").find(".chosen-single");
                if (type == "danger"){
                    obj.css("border-color", "#EA2803");
                    obj.css("box-shadow", "0 0 10px #EA2803");
                    chosenObj.css("border-color", "#EA2803");
                    chosenObj.css("box-shadow", "0 0 10px #EA2803");
                }
                obj.addClass("js-mytooltip system-mytooltip--base");
                obj.attr("data-mytooltip-custom-class", "align-center");
                obj.attr("data-mytooltip-direction", "top");
                obj.attr("data-mytooltip-theme", type);
                obj.attr("data-mytooltip-content", m);
                obj.myTooltip('update');
                
                chosenObj.addClass("js-mytooltip system-mytooltip--base");
                chosenObj.attr("data-mytooltip-custom-class", "align-center");
                chosenObj.attr("data-mytooltip-direction", "top");
                chosenObj.attr("data-mytooltip-theme", type);
                chosenObj.attr("data-mytooltip-content", m);
                chosenObj.myTooltip('update');
            }

        }
    }

    $.shortcut = function(shortcuts){
        if(shortcuts){
            $(document).keyup(function(e){
                var el = e.srcElement ? e.srcElement : e.target;
                if( e.ctrlKey && e.shiftKey && !$(el).is(":input") ){
                    $.each(shortcuts, function(keycode, fnc){
                        if(e.which==keycode && fnc ) fnc();
                    });
                }
            });
        }
    };

    $.page = {
        existMenuId : function () {
            return $('#ACTION_MENU_ID').length > 0 && !$.comm.isNull($('#ACTION_MENU_ID').val());
        },
        existMenuNm : function () {
            return $('#ACTION_MENU_NM').length > 0 && !$.comm.isNull($('#ACTION_MENU_NM').val());
        },
        existMenuDiv : function () {
            return $('#ACTION_MENU_DIV').length > 0 && !$.comm.isNull($('#ACTION_MENU_DIV').val());
        },
        getMenuId : function () {
            return $('#ACTION_MENU_ID').val();
        },
        getMenuNm : function () {
            return $('#ACTION_MENU_NM').val();
        },
        getMenuDiv : function () {
            return $('#ACTION_MENU_DIV').val();
        },
        getSessionDiv : function () {
            return $.comm.getGlobalVar("sessionDiv");
        },
        createMenuIdInputStr : function () {
            if(this.existMenuId()){
                return "<input type='hidden' name='ACTION_MENU_ID' value='" + encodeURIComponent($('#ACTION_MENU_ID').val()) + "'>";
            }
            return "";
        },
        createMenuNmInputStr : function () {
            if(this.existMenuNm()){
                return "<input type='hidden' name='ACTION_MENU_NM' value='" + encodeURIComponent($('#ACTION_MENU_NM').val()) + "'>";
            }
            return "";
        },
        createMenuDivInputStr : function () {
            if(this.existMenuDiv()){
                return "<input type='hidden' name='ACTION_MENU_DIV' value='" + $('#ACTION_MENU_DIV').val() + "'>";
            }
            return "";
        },
        createSessionDivInputStr : function () {
            return '<input type="hidden" name="sessionDiv" id="sessionDiv" value="'+ $.comm.getGlobalVar("sessionDiv") +'" />';
        },
        createActionNmInputStr : function (actionNm) {
            return "<input type='hidden' name='ACTION_NM' value='" + encodeURIComponent(actionNm) + "'>";
        },
        createMenuInfoInputStr : function () {
            return this.createMenuIdInputStr() + this.createMenuNmInputStr() + this.createMenuDivInputStr();
        }
    }

})(jQuery);

function wrapSearch (formId){
    this.formId = formId;
    this.defaultParam = {};
    this.defaultDateParam = [];
    this.modifyParam = null;

    this.initDefaultParam();
    this.initWrapSearch();
}

wrapSearch.prototype = {
    initDefaultParam : function(){
        var dueDatefield = "";
        var formEle = $("#" + this.formId)[0].elements;
        for(var i=0; i<formEle.length; i++){
            var obj = formEle[i];
            if((obj.tagName == "INPUT" && obj.type == "text") || obj.tagName == "SELECT"){
                var id  = $(obj).attr("id");
                var val = $(obj).val();
                if($(obj).is('[datefield]') && !$.comm.isNull(val)){
                    if(dueDatefield.indexOf(id) > -1){
                        continue;
                    }

                    var tmp = {};

                    val = val.trim().replace(/\/|-/g, '');
                    var toDateId = $("#"+id).attr("toDataefield");
                    if(!$.comm.isNull(toDateId)){
                        dueDatefield += id +",";
                        dueDatefield += toDateId +",";

                        tmp[id] = val;
                        tmp[toDateId] = $("#"+toDateId).val().trim().replace(/\/|-/g, '');

                        this.defaultDateParam[this.defaultDateParam.length] = tmp;
                    }else{
                        tmp[id] = val;
                        this.defaultDateParam[this.defaultDateParam.length] = tmp;
                    }
                }else{
                    if(!$.comm.isNull(id)){
                        this.defaultParam[id] = val;
                    }
                }
            }
        }
    },
    // pk 그룹 필드에 값이 있는지 여부 반환
    isModPkGroup : function(obj){
        var list = $("#" + this.formId).find('input[pk]');
        var bool = false; // 값존재 여부
        for(var i=0; i<list.length; i++){
            var obj = list[i];
            if(!$.comm.isNull($(obj).val())){
                bool = true;
            }
        }

        return bool;
    },
    initWrapSearch : function(){
        var objList = $("#" + this.formId).find('input[pk]');
        for(var i in objList){
            var obj = objList[i];
            obj.onfocus = $.comm.bindAsListener(this.onFocus, this);
            obj.onblur  = $.comm.bindAsListener(this.onBlur, this);
        }
    },
    /**
     * 변경된 INPUT(text)의 필드만 남기고 나머지 INPUT(text)는 공백처리
     * 날짜 기간이 있을 경우 하나라도 변경되면 둘다 남김
     */
    onFocus: function(event){
        var target = $.comm.getTarget(event);
        //  pk 그룹 필드들중 값이 있으면 리턴
        if(this.isModPkGroup(target)){
            return;
        }

        this.modifyParam = null;
        for(var id in this.defaultParam){
            var obj = $('#' + id);

            var currVal = $(obj).val();
            var orgVal  = this.defaultParam[id];
            if(obj[0].tagName == "INPUT" && currVal == orgVal){
                $(obj).val("");
            }else{
                if(this.modifyParam == null){
                    this.modifyParam = {};
                }
                this.modifyParam[id] = currVal;
            }
        }

        for(var i=0; i<this.defaultDateParam.length; i++){
            var dateField = this.defaultDateParam[i];

            var isMod = false;
            var isBlank = false;
            for(var key in dateField){
                var orgVal  = dateField[key];
                var currVal = $('#' + key).val();
                if(!$.comm.isNull(currVal)){
                    currVal = currVal.trim().replace(/\/|-/g, '');
                }

                if(isMod == false && orgVal != currVal){
                    isMod = true;
                }

                if(isBlank == false && $.comm.isNull(currVal)){
                    isBlank = true;
                }
            }

            for (var key in dateField) {
                var currVal = $('#' + key).val();
                if (!$.comm.isNull(currVal)) {
                    currVal = currVal.trim().replace(/\/|-/g, '');
                }

                // 공백이 없고 변경되었을때
                if(isMod && !isBlank) {
                    if (this.modifyParam == null) {
                        this.modifyParam = {};
                    }
                    this.modifyParam[key] = currVal;
                }

                $('#' + key).val("");
            }
        }
    },
    onBlur: function(event){
        var target = $.comm.getTarget(event);
        //  pk 그룹 필드들중 값이 있으면 리턴
        if(this.isModPkGroup(target)){
            return;
        }

        // 조회영역의 필드가 변경되었는지 여부
        for(var id in this.defaultParam){
            var val     = this.defaultParam[id];
            if(this.modifyParam && !$.comm.isNull(this.modifyParam[id])){
                val = this.modifyParam[id];
            }

            if($('#'+id).is('[datefield]')){
                val = val.trim().replace(/\/|-/g, '');
                if(val.length == 8){
                    val = val.toDate("YYYYMMDD").format("YYYY-MM-DD");
                }
            }
            $('#'+id).val(val);

        }
        for(var i=0; i<this.defaultDateParam.length; i++){
            var dateField = this.defaultDateParam[i];

            for(var key in dateField){
                var val = dateField[key];
                if(this.modifyParam && !$.comm.isNull(this.modifyParam[key])){
                    val = this.modifyParam[key];
                }

                val = val.trim().replace(/\/|-/g, '');
                if(val.length == 8){
                    val = val.toDate("YYYYMMDD").format("YYYY-MM-DD");
                }

                $('#'+key).val(val);
            }
        }
    }

}


/*
 *  HTML Form Validation
 */

function JForm() {

    this.children = [];

    this.add = function(child) {
        this.children[this.children.length] = child;
        return this;
    };
    this.clear = function() {
        this.children = [];
    };
    this.last = function() {
        return this.children[this.children.length - 1]
    };
    this.validate = function() {
        for (var i = 0; i < this.children.length; i++) {
            if (!this.children[i].validate()) {
                return false;
            }
        }
        return true;
    };
}

/***
 * 길이 체크 ( byte length로 체크 )
 * @param name
 * @param object
 * @param len
 * @constructor
 */
function JLength(name, object, len, isUnicode){
    this.name = name;
    this.object = $.comm.getJQueryObject(object);
    this.len = len;
    this.minlength = len.split(",")[0];
    this.maxlength = len.split(",")[1];
    this.isUnicode = isUnicode;

    this.validate = function() {
        var value = this.object == null ? '' : this.object.val();
        if(this.object.is('[numberOnly]')){
            value = $.comm.numberWithoutCommas(value);
        }
        var valLen = $.comm.bytelength(value, this.isUnicode);
        var min = 0;
        var max = 0;
        var arr = this.len.split(",");

        if(arr.length == 0){
            alert("length의 속성값을 확인 하세요");
            return false;
        }

        // maxlength 만 체크
        if(arr.length == 1){
            max = parseInt(arr[0]);

        }
        if(arr.length == 2){
            min = parseInt(arr[0]);
            max = parseInt(arr[1]);
        }

        if(min > valLen){
            alert($.comm.getMessage("W00000005", this.name)); // $의 글자수가 최소 글자수 미만 입니다.
            return this.focus();
        }

        if(max < valLen){
            alert($.comm.getMessage("W00000006", this.name)); // $의 글자수가 최대 글자수를 초과 하였습니다.
            return this.focus();
        }

        return true;
    };
    this.focus = function() {
        if (this.object != null ) {
            this.object.focus();
        }
        return false;
    };
}

/**
 *	Mandatory Validation.
 */
function JText(name, object, nullCheck) {
	this.name = name;
    this.object = $.comm.getJQueryObject(object);
    this.nullCheck = ($.type (nullCheck) === 'null' || $.type (nullCheck) === 'undefined') ? true : nullCheck;
    
    this.validate = function() {
    	var value = this.object == null ? '' : this.object.val();
    	if (this.nullCheck && $.trim(value).length == 0) {
    		alert($.comm.getMessage("W00000004", this.name)); // $을(를) 입력하십시오.
    		return this.focus();
    	}
    	return true;
    };
    this.focus = function() {
    	if (this.object != null ) {
    		this.object.focus();
    	}
        return false;
    };
}


/**
 *	Select Html Tag Validation.
 */
function JSelect(name, object, nullCheck, defaultValue) {
    this.name = name;
    this.object = $.comm.getJQueryObject(object);
    this.nullCheck = ($.type (nullCheck) === 'null' || $.type (nullCheck) === 'undefined') ? true : nullCheck;
    this.defaultValue = ($.type (defaultValue) === 'null' || $.type (defaultValue) === 'undefined') ? '' : defaultValue;

    this.validate =  function() {
    	var value = this.object == null ? '' : this.object.val();
    	if (this.nullCheck && ((this.defaultValue.length == 0 && $.trim(value).length == 0) || (this.defaultValue == $.trim(value)))) {
            alert($.comm.getMessage("W00000004", this.name)); // $을(를) 입력하십시오.
    		return this.focus();
    	}
    	return true;
    };
    this.focus = function() {
    	if (this.object != null ) {
    		this.object.focus();
    	}
        return false;
    };
}

/**
 *	Date Type Validation.
 */
function JDate(name, object, nullCheck) {
	this.name = name;
    this.object = $.comm.getJQueryObject(object);
    this.nullCheck = ($.type (nullCheck) === 'null' || $.type (nullCheck) === 'undefined') ? true : nullCheck;
	
    this.validate =  function() {
    	var value = this.object == null ? '' : this.object.val();
    	if (this.nullCheck && !$.date.isValidDate(value)) {
            alert($.comm.getMessage("W00000004", this.name)); // $을(를) 입력하십시오.
    		return this.focus();
    	}
    	return true;
    };
    this.focus = function() {
    	if (this.object != null ) {
    		this.object.focus();
    	}
        return false;
    };
}

/**
 *	Number Type Validation.
 */
function JNumeric(name, object, nullCheck) {

	this.name = name;
    this.object = $.comm.getJQueryObject(object);
    this.nullCheck = ($.type (nullCheck) === 'null' || $.type (nullCheck) === 'undefined') ? true : nullCheck;
	
    this.validate =  function() {
    	var value = this.object == null ? '' : this.object.val().replace(/,/ig,'');
    	if (this.nullCheck && !$.isNumeric(value)) {
            alert($.comm.getMessage("W00000004", this.name)); // $을(를) 입력하십시오.
    		return this.focus();
    	}
    	return true;
    };
    this.focus = function() {
    	if (this.object != null ) {
    		this.object.focus();
    	}
        return false;
    };
}

function JEmail(name) {
    this.name = name;
    this.object = $.comm.getJQueryObject(name);

    this.validate =  function() {
        var value = this.object == null ? '' : this.object.val();
        if ($.trim(value).length > 0) {
            var regEmail = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
            if (!regEmail.test(value)) {
                alert($.comm.getMessage("W00000009")); //이메일 주소가 유효하지 않습니다.
                this.object.focus();
                return false;
            }
        }

        return true;
    };
    this.focus = function() {
        if (this.object != null ) {
            this.object.focus();
        }
        return false;
    };
}

function JPhone(name) {
    this.name = name;
    this.object = $.comm.getJQueryObject(name);

    this.validate =  function() {
        var value = this.object == null ? '' : this.object.val();
        if ($.trim(value).length > 0) {
            var regExp = /^01([0|1|6|7|8|9]?)-?([0-9]{3,4})-?([0-9]{4})$/;
            if (!regExp.test(value)) {
                alert($.comm.getMessage("W00000011")); //휴대폰번호가 유효하지 않습니다.
                this.object.focus();
                return false;
            }
        }

        return true;
    };
    this.focus = function() {
        if (this.object != null ) {
            this.object.focus();
        }
        return false;
    };
}

function JTel(name) {
    this.name = name;
    this.object = $.comm.getJQueryObject(name);

    this.validate =  function() {
        var value = this.object == null ? '' : this.object.val();
        if ($.trim(value).length > 0) {
            var regExp = /^\d{2,3}-\d{3,4}-\d{4}$/;
            if (!regExp.test(value)) {
                alert($.comm.getMessage("W00000010")); //전화번호가 유효하지 않습니다.
                this.object.focus();
                return false;
            }
        }

        return true;
    };
    this.focus = function() {
        if (this.object != null ) {
            this.object.focus();
        }
        return false;
    };
}

function JCustom(fn) {
	if (!$.isFunction(fn)) {
		throw Error('lllegal argument !');
	}
	this.fn = $.type(fn) === 'string' ? eval(fn) : fn;  
	this.validate = fn;
}

function gfn_unitTestPopup(isTest){
    if(isTest == "true"){
        $.shortcut({
            123 : function(){ // ctrl + shift + F12 ( 단위테스트 팝업 )
                var menuId = $.page.getMenuId();
                var menuNm = $.page.getMenuNm();
                $.comm.open("UNIT_TEST_POPUP" + menuId, "/jspView.do?jsp=cmm/popup/unitTestPopup&TARGET_ID="+menuId + "&TARGET_NM="+menuNm, 1400, 900);
            }
        });
    }
}

function gfn_closeDimLoading(){
    $('.dim_laoding').css('display','none');
    $.comm.setGlobalVar("dimCnt", 0);
}

/**
 *	List Validation.
 */
var ListValidate = {
	
		select : function(sel, typ) {
			sel = $.type(sel) === 'string' ? $(sel) : sel;
			if (sel.length == 0) {
				alert(($.type(typ) == 'null' || $.type(typ) === 'undefined') ? '선택된 내역이 없습니다.' : (typ + ' 위해 선택된 내역이 없습니다.'));
				return false;
			}
			return true;
		},
		
		onlyone : function(sel) {
			sel = $.type(sel) === 'string' ? $(sel) : sel;
			 if (sel.length > 1) {
				alert('한건만 선택해야 합니다. 확인하십시오.');
				return false;
			}			
			return true;
		},
		
		delConfirm : function() {
			if (!confirm($.comm.getMessage("C00000001"))) { // 삭제 하시겠습니까?
				return false;
			}
			return true;
		}
};
