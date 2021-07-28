/**------------------------------------------------------------
 * File Name      : common.js
 * Description    : BOQ Admin Custom Javascript Library
 * Modify History : Just Created.
 ------------------------------------------------------------*/

/**------------------------------------------------------------
 * BOQ Admin Global Variable
 ------------------------------------------------------------*/
var strAlertCallBack = "";
var gisCSRFToken     = false;
var strPreModalId    = "";
var isPreModalOpen   = false;

/**------------------------------------------------------------
 * JQuery Cached Query
 ------------------------------------------------------------*/
var C$ = function(query) {
    this.cache = this.cache || {};

    if(!this.cache[query]) {
        this.cache[query] = jQuery(query);
    }

    return this.cache[query];
};

/**------------------------------------------------------------
 * BOQ Admin Global Constants
 ------------------------------------------------------------*/
var COMMON = {
    CSRFID           : "RequestVerificationToken",
    BLOCKDIVID       : "divPageBlock",
    COMMONERRORMSG   : "Occured error.",
    EXCEPTIONMSG     : "예외가 발생하였습니다.",
    LOGINURL         : "/login",
    SUCCESSMSG       : "작업이 완료되었습니다.",
    FAILEDMSG        : "작업이 실패하였습니다.",
    SESSIONEXPIRECODE: 499,
    INVALIDAUTHCODE  : 401,
    PAGES            : 1,
    PAGELENGTH       : 10,
    // LANGUAGE		 : {
    //     "sLengthMenu": '<select><option value="20">20</option><option value="50">50</option><option value="100">100</option></select> records per page'
    // }
}

/**------------------------------------------------------------
 * Request Type
 ------------------------------------------------------------*/
var REQUESTTYPE = {
    FORM: "form",
    AJAX: "ajax",
    JSON: "json"
}

/**------------------------------------------------------------
 * Edit Type
 ------------------------------------------------------------*/
var EDITTYPE = {
    REGIST: "regist",
    MODIFY: "modify"
}

/**------------------------------------------------------------
 * Auth Code
 ------------------------------------------------------------*/
var AUTHCODE = {
    ALL     : 1,
    READONLY: 2
}

/**------------------------------------------------------------
 * Menu Info
 ------------------------------------------------------------*/
var MENU = {
    Sample : { menuLink : "/sample/sample/sample" },
    Customer : { menuLink : "/customer/customer/customer" }
}

/**------------------------------------------------------------
 * chart color list
 ------------------------------------------------------------*/
var CHARTCOLORLIST = [
    am4core.color("#F18811"),
    am4core.color("#F2DBBB"),
    am4core.color("#C1330D"),
    am4core.color("#2782A0"),
    am4core.color("#0D4E51"),
    am4core.color("#8ABADD"),
    am4core.color("#F9D938"),
    am4core.color("#EE3831"),
    am4core.color("#53B0AE"),
    am4core.color("#D93D86"),
    am4core.color("#40B65F"),
    am4core.color("#6A6B9E"),
    am4core.color("#95A59A"),
    am4core.color("#F29993")
];

/**------------------------------------------------------------
 * COMMON.Ajax
 ------------------------------------------------------------*/
COMMON.Ajax = {
    /**------------------------------------------------------------
     * Function Name  : fnRequest
     * Description    : Ajax Process
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRequest: function(objReq) {
        var intErrCnt = 0;
        var obj = {
            requestType  : null == objReq.requestType   ? REQUESTTYPE.JSON : objReq.requestType,
            requestData  : objReq.requestData,
            strCallUrl   : objReq.strCallUrl,
            strCallBack  : objReq.strCallBack,
            isModalMsg   : null == objReq.isModalMsg    ? false : objReq.isModalMsg,
            isaSync      : null == objReq.isaSync       ? true : objReq.isaSync,
            strSuccessMsg: null == objReq.strSuccessMsg ? COMMON.SUCCESSMSG : objReq.strSuccessMsg,
            strFailedMsg : null == objReq.strErrorMsg   ? COMMON.FAILEDMSG : objReq.strErrorMsg,
            objJson: {
                intRetCode: 9999,
                strRetMsg : COMMON.EXCEPTIONMSG
            }
        }

        $.ajax({
            cache: false,
            async: obj.isaSync,
            type: "POST",
            data: (obj.requestType == REQUESTTYPE.JSON ? JSON.stringify(obj.requestData) : obj.requestData),
            url: obj.strCallUrl,
            dataType: "JSON",
            contentType: (obj.requestType == REQUESTTYPE.JSON ? "application/json; charset=utf-8" : "application/x-www-form-urlencoded; charset=UTF-8"),
            headers: { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
            beforeSend: function() {
                if(objReq.noLoading == undefined) {
                    COMMON.Ajax.fnAjaxBlock();
                }
            },
            complete: function() {
                if(objReq.noLoading == undefined) {
                    $.unblockUI();
                }
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                intErrCnt++;
                if(XMLHttpRequest.status == COMMON.SESSIONEXPIRECODE
                    || XMLHttpRequest.status == COMMON.INVALIDAUTHCODE) {
                    location.href = COMMON.LOGINURL;
                    return;
                }

                if(intErrCnt == 3 && XMLHttpRequest.readyState == 4 && textStatus == "parsererror") {
                    COMMON.Ajax.fnRequestResult(obj);
                } else if(intErrCnt == 3 && XMLHttpRequest.readyState == 0 && textStatus == "error") {
                    COMMON.Ajax.fnRequestResult(obj);
                }

                if(intErrCnt == 3 && 9999 === XMLHttpRequest.responseJSON.intRetCode) {
                    COMMON.Ajax.fnRequestResult(obj);
                }
            }
        }).retry({ times: 3, timeout: 1000 }).then(function(objJson) {
            try {
                if(typeof (objJson) === "object") {
                    obj.objJson = objJson;
                } else if(typeof (objJson) === "string") {
                    obj.objJson = jQuery.parseJSON(objJson);
                } else {
                    if(intErrCnt == 3) {
                        COMMON.Ajax.fnRequestResult(obj);
                    }

                    return;
                }

                if(obj.isModalMsg) {
                    COMMON.Ajax.fnRequestResult(obj);
                }else {
                    eval(obj.strCallBack)(objJson, obj.requestData);
                }
            } catch(ex) {}
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnAjaxBlock
     * Description    : Ajax Block
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnAjaxBlock: function() {
        try {
            $.blockUI({
                message: $("#" + COMMON.BLOCKDIVID),
                css: {
                    top: '50%',
                    left: '50%',
                    padding: 0,
                    margin: 0,
                    width: '24px',
                    height: '24px',
                    border: 'none',
                    backgroundColor: 'transparent',
                    '-webkit-border-radius': '10px',
                    '-moz-border-radius': '10px',
                    opacity: 1,
                    color: '#000',
                    'z-index': 9999
                }, overlayCSS: {
                    backgroundColor: '#FFFFFF',
                    opacity: 0.0,
                    cursor: 'wait'
                }
            });
        } catch(ex) {}
    },

    /**------------------------------------------------------------
     * Function Name  : CreateDataTable
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    CreateDataTable: function(strLocation, isPartial, opts) {
        // 테이블아이디, 부분조회여부, DataTables옵션
        // 기본옵션 상속받기 (옵션 우선순위 : 고정옵션 > 호출옵션 > 기본옵션)
        var conf = $.extend({
            pages: COMMON.PAGES,
            pageLength: COMMON.PAGELENGTH,
            method: 'POST',
            ordering: true,
            // dom: '<"top"r>frtip',
            autoWidth: true,
            lengthChange: true,
            pagingType: "simple_numbers"
        }, opts);

        var fnOrgDrawCallback = conf.fnDrawCallback;
        var fnOrgRowCallback = conf.fnRowCallback;

        // Callback 구현
        conf.fnDrawCallback = function(settings) {
            if(typeof (fnOrgDrawCallback) == "function") {
                fnOrgDrawCallback(settings);
            }
        }

        // fnRowCallback 구현
        conf.fnRowCallback = function(nRow, aData, iDisplayIndex) {
            if(typeof (fnOrgRowCallback) == "function") {
                fnOrgRowCallback(nRow, aData, iDisplayIndex);
            }
        }

        if(isPartial == true) {
            //부분조회시 고정 옵션 - 수정금지
            conf.processing = false;
            conf.serverSide = true;
            conf.sort = true;
            conf.searching = false;
            conf.ajax = COMMON.Ajax.Pipeline({
                url: conf.url,
                data: conf.data,
                pages: conf.pages
            });
        } else {
            //전체조회시 고정 옵션- 수정금지
            conf.processing = false;
            conf.serverSide = false;
            conf.sort = true;
            conf.ajax = function(data, callback, settings) {
                $.ajax({
                    type: conf.method,
                    url: conf.url,
                    dataType: "JSON",
                    data: JSON.stringify(conf.data()),
                    contentType: "application/json; charset=utf-8",
                    headers: { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
                    beforeSend: function() {
                        COMMON.Ajax.fnAjaxBlock();
                    },
                    complete: function() {
                        $.unblockUI();
                    },
                    success: function(json) {
                        if(json.subdata != null) {
                            eval(json.subdata.strCallBack)(json.subdata);
                        }

                        if(json.intRetCode != 0) {
                            COMMON.Msg.fnAlert(json.strRetMsg);
                        } else {
                            settings.json = json;
                            callback(json);
                        }
                    },
                    error: function(xhr, error, thrown) {
                        var log = settings.oApi._fnLog;

                        if(xhr.status == COMMON.SESSIONEXPIRECODE
                            || xhr.status == COMMON.INVALIDAUTHCODE) {
                            location.href = COMMON.LOGINURL;
                            return;
                        }

                        if(9999 === xhr.responseJSON.intRetCode) {
                            COMMON.Msg.fnAlert(xhr.responseJSON.strRetMsg);
                            return;
                        }

                        if(error == "parsererror") {
                            log(settings, 0, 'Invalid JSON response', 1);
                        } else if(xhr.readyState === 4) {
                            log(settings, 0, 'Ajax error', 7);
                        }
                    }
                });
            };
        }

        return $(strLocation).DataTable(conf);
    },

    /**------------------------------------------------------------
     * Function Name  : Pipeline
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    Pipeline: function(opts) {
        var conf = $.extend({
            method: 'POST'
        }, opts);

        var cacheLower       = -1;
        var cacheUpper       = null;
        var cacheLastRequest = null;
        var cacheLastJson    = null;

        return function(request, drawCallback, settings) {
            var ajax = false;
            var requestStart  = request.start;
            var drawStart     = request.start;
            var requestLength = request.length;
            var requestEnd    = requestStart + requestLength;

            if(settings.clearCache) {
                ajax = true;
                settings.clearCache = false;
            } else if(cacheLower < 0 || requestStart < cacheLower || requestEnd > cacheUpper) {
                ajax = true;
            } else if(JSON.stringify(request.order) !== JSON.stringify(cacheLastRequest.order) ||
                JSON.stringify(request.columns) !== JSON.stringify(cacheLastRequest.columns) ||
                JSON.stringify(request.search) !== JSON.stringify(cacheLastRequest.search)) {
                ajax = true;
            }

            cacheLastRequest = $.extend(true, {}, request);

            if(ajax) {
                if(requestStart < cacheLower) {
                    requestStart = requestStart - (requestLength * (conf.pages - 1));

                    if(requestStart < 0) {
                        requestStart = 0;
                    }
                }

                cacheLower = requestStart;
                cacheUpper = requestStart + (requestLength * conf.pages);

                request.start = requestStart;
                request.length = requestLength * conf.pages;

                if($.isFunction(conf.data)) {
                    var d = conf.data(request);

                    if(d) {
                        $.extend(request, d);
                    }
                } else if($.isPlainObject(conf.data)) {
                    $.extend(request, conf.data);
                }

                delete request.columns;
                delete request.search;
                delete request.order;

                settings.jqXHR = $.ajax({
                    "type": conf.method,
                    "url": conf.url,
                    "data": JSON.stringify(request),
                    "dataType": "json",
                    "contentType" : "application/json; charset=utf-8",
                    "headers": { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
                    "cache": false,
                    "success": function(json) {
                        cacheLastJson = $.extend(true, {}, json);

                        if(cacheLower != drawStart) {
                            json.data.splice(0, drawStart - cacheLower);
                        }

                        json.data.splice(requestLength, json.data.length);

                        settings.json = json;

                        drawCallback(json);
                    },
                    beforeSend: function() {
                        COMMON.Ajax.fnAjaxBlock();
                    },
                    complete: function() {
                        $.unblockUI();
                    },
                    error: function(xhr, error, thrown) {
                        var log = settings.oApi._fnLog;

                        if(xhr.status == COMMON.SESSIONEXPIRECODE
                            || xhr.status == COMMON.INVALIDAUTHCODE) {
                            location.href = COMMON.LOGINURL;
                            return;
                        }

                        if(9999 === xhr.responseJSON.intRetCode) {
                            COMMON.Msg.fnAlert(xhr.responseJSON.strRetMsg);
                            return;
                        }

                        if(error == "parsererror") {
                            log(settings, 0, 'Invalid JSON response', 1);
                        } else if(xhr.readyState === 4) {
                            log(settings, 0, 'Ajax error', 7);
                        }
                    }
                });
            } else {
                var json = $.extend(true, {}, cacheLastJson);

                json.draw = request.draw;
                json.data.splice(0, requestStart - cacheLower);
                json.data.splice(requestLength, json.data.length);

                drawCallback(json);
            }
        }
    },

    fnRequestResult: function(obj) {
        if("0" == obj.objJson.intRetCode) {
            COMMON.Msg.fnAlert(obj.strSuccessMsg, obj.strCallBack, obj);
        } else {
            COMMON.Msg.fnAlert(obj.strFailedMsg + "(" + obj.objJson.strRetMsg + ")", null, obj);
        }
    }
}

/**------------------------------------------------------------
 * COMMON.Utils
 ------------------------------------------------------------*/
COMMON.Utils = {
    /**------------------------------------------------------------
     * Function Name  : fnSetValidate
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnSetValidate: function(arrValidate) {
        $('#' + arrValidate["FORMID"]).validate({
            messages: 'ko',
            focusInvalid: false,
            ignore: "",
            rules: arrValidate["VARIABLE"],
            invalidHandler: function(event, validator) {
            },
            errorPlacement: function(label, element) { // render error placement for each input type
                var isDatePicker = $(element).hasClass("pl-datepicker");
                var $objParent = isDatePicker ? $(element).parent().parent() : $(element).parent();
                var $objTarget = isDatePicker ? $(element).parent() : $(element);
                var $objSpan = $objParent.children('span.help-block');

                $objSpan.remove();
                $('<span class="help-block"><label class="control-label" for="inputError"><i class="fa fa-times-circle-o"></i>&nbsp;</label>' + label[0].textContent + '</span>').insertAfter($objTarget);
                $objParent.removeClass('has-success').addClass('has-error');
                if(isDatePicker) {
                    $objTarget.removeClass('has-success').addClass('has-error');
                }
            },
            highlight: function(element) {
                var parent = $(element).parent();
                parent.removeClass('has-success').addClass('has-error');
            },
            unhighlight: function(element) {
            },
            success: function(label, element) {
                var isDatePicker = $(element).hasClass("pl-datepicker");
                var $objParent = isDatePicker ? $(element).parent().parent() : $(element).parent();
                var $objTarget = isDatePicker ? $(element).parent() : $(element);
                var $objSpan = $objParent.children('span.help-block');

                $objSpan.remove();
                $objParent.removeClass('has-error').addClass('has-success');
                if(isDatePicker) {
                    $objTarget.removeClass('has-error').addClass('has-success');
                }
            },
            submitHandler: function(form) {
            }
        });

        $('.select2', "#" + arrValidate["FORMID"]).change(function() {
            $('#' + arrValidate["FORMID"]).validate().element($(this));
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnChangeValidate
     * Description    : -
     * Author         : payletter, 2020. 02. 18.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnChangeValidate: function(arrValidate) {
        var obj = $('#' + arrValidate["FORMID"]).validate();

        if (obj) {
            obj.settings.rules = arrValidate["VARIABLE"];
        }
    },

    /**------------------------------------------------------------
     * Function Name  : fnInitSingleDateTimePicker
     * Description    : Set Date Picker
     * Modify History : Just Created.
     *                : payletter, strFormat 추가
     ------------------------------------------------------------*/
    fnInitSingleDatePicker: function(txtSelectYMD, strFormat, opts) {
        var $objSelectInput = $(txtSelectYMD);

        if(strFormat == null || strFormat == "undefined" || strFormat == "") {
            strFormat = "yyyy-mm-dd";
        }

        // 기본옵션 상속 (우선순위: 호출옵션 > 기본옵션)
        var objSelectConfig = $.extend({
            format: strFormat,
            dateFormat: 'yy-mm',
            autoclose: true,
            autoApply: true,
            showDropdowns: true
        }, opts);

        // Date Picker 생성
        $objSelectInput.datepicker(objSelectConfig);
    },

    /**------------------------------------------------------------
     * Function Name  : fnInitSearchDateRangePicker
     * Description    : Initialize Start and End DateRangePicker for Search
     * Modify History : Just Created.
     *                : payletter, strFormat 추가
     ------------------------------------------------------------*/
    fnInitSearchDateRangePicker: function(objForm, hidStart, hidEnd, txtRange, strFormat, opts) {
        var $objStartInput = $(hidStart);
        var $objEndInput   = $(hidEnd);
        var $objRangeInput = $(txtRange);

        if(strFormat == null || strFormat == "undefined" || strFormat == "") {
            strFormat = "YYYY-MM-DD";
        }

        // 기본옵션 상속 (우선순위: 호출옵션 > 기본옵션)
        var objRangeConfig = $.extend({
            locale: {
                format: strFormat,
                separator: " ~ "
            },
            autoApply: true,
            showDropdowns: true,
            startDate: $objStartInput.val(),
            endDate: $objEndInput.val()
        }, opts);

        // Date Range Picker 생성
        $objRangeInput.daterangepicker(objRangeConfig, function(startDate, endDate) {
            $("{0} {1}".format(objForm, hidStart)).prop("value", startDate.format(strFormat));
            $("{0} {1}".format(objForm, hidEnd)).prop("value", endDate.format(strFormat));
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnFileDownload
     * Description    : 파일 다운로드
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnFileDownload: function(strHttpMethod, requestData, strCallUrl) {
        var arrParams;

        if(strHttpMethod.toUpperCase() == "POST") {
            arrParams = { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() };
            $.extend(arrParams, requestData);
        } else {
            arrParams = requestData;
        }

        $.fileDownload(strCallUrl, {
            httpMethod: strHttpMethod,
            data: arrParams,
            prepareCallback: function(url) {
                //COMMON.Ajax.fnAjaxBlock();
            },
            successCallback: function(url) {
                //$.unblockUI();
            },
            failCallback: function(html, url) {
                //$.unblockUI();
                COMMON.Msg.fnAlert(COMMON.COMMONERRORMSG);
            }
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnRequestFile
     * Description    : Ajax Process For File Upload
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnRequestFile: function($objFormData, strCallUrl, strCallBack) {
        var obj = {
            intRetCode: 9998,
            strRetMsg : COMMON.EXCEPTIONMSG
        };

        $.ajax({
            cache: false,
            type: 'POST',
            data: $objFormData,
            url: strCallUrl,
            contentType: false,
            processData: false,
            headers: { "RequestVerificationToken": COMMON.AntiCSRF.getVerificationToken() },
            beforeSend: function() {
                COMMON.Ajax.fnAjaxBlock();
            },
            complete: function() {
                $.unblockUI();
            },
            success: function(objJson) {
                eval(strCallBack)(objJson);
            },
            error: function(XMLHttpRequest, textStatus, errorThrown) {
                if(XMLHttpRequest.status == COMMON.SESSIONEXPIRECODE
                    || XMLHttpRequest.status == COMMON.INVALIDAUTHCODE) {
                    location.href = COMMON.LOGINURL;
                    return;
                }

                eval(strCallBack)(obj);
            }
        });
    },

    /**------------------------------------------------------------
     * Function Name  : fnFrmReset
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnFrmReset: function(frmId) {
        $("form").each(function() {
            if(this.id == frmId) {
                this.reset();
                $(this).find("select").each(function() {
                    var defaultValue = $(this).find("option:eq(0)").val();
                    $(this).select2("val", defaultValue);
                });
            }
        });

        try {
            var parent = $('.form-control').parent();
            parent.removeClass('has-success').removeClass('has-error');
            parent.children('span:not(".glyphicon")').remove();
        } catch(ex) { }
    },

    fnRenderFlag: function(data) {
        return data == true ? "Y" : "N";
    },

    /**------------------------------------------------------------
     * Function Name  : fnSortSettings
     * Description    : 정렬기능 세팅 (페이징 처리 limit 사용)
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnSortSettings : function(obj, formObj) {
        var columnId = $(obj).attr("id");
        var araLabel = $(obj).attr("aria-label");
        var sortType = "";

        if(araLabel.match("ascending")) {
            sortType = "ASC";
        } else if(araLabel.match("descending")) {
            sortType = "DESC";
        }

        formObj.find("[name=strSortColumn]").val(columnId);
        formObj.find("[name=strSortType]").val(sortType);

    },

    /**------------------------------------------------------------
     * Function Name  : fnIsEmpty
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnIsEmpty: function(str) {
        if(str == null || str == undefined) {
            return true;
        } else if(typeof (str) == "string" && str.trim() == "") {
            return true;
        } else {
            return false;
        }
    },

    /**------------------------------------------------------------
     * Function Name  : fnFrmSettings
     * Description    : input 항목 값 자동 Mapping
     * Author         : payletter, 2019. 01. 28.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnFrmSettings: function(frmId, obj) {
        for (var key in obj) {
            var value  = obj[key];
            var target = "#{0} input[name={1}]".format(frmId, key);

            // 해당 태그에 value 셋팅
            if($(target).length == 1) {
                switch (key) {
                    default:
                        COMMON.Utils.fnIsEmpty(value) ? $(target).val("") : $(target).val(value);
                        break;
                }
            }
        }
    },

    /**------------------------------------------------------------
     * Function Name  : fnInitDivTbl
     * Description    : td 항목 값 초기화
     * Author         : payletter, 2019. 02. 21.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnInitDivTbl: function(divId) {
        $("#{0} table tr td:not('.td-item')".format(divId)).html("");
    },

    /**------------------------------------------------------------
     * Function Name  : fnDivTblSettings
     * Description    : td 항목 값 자동 Mapping
     * Author         : payletter, 2019. 02. 21.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnDivTblSettings: function(divId, obj) {
        for (var key in obj) {
            var value  = obj[key];
            value = COMMON.Utils.fnIsEmpty(value) ? "-" : ($.isNumeric(value)) ? COMMON.Utils.fnAddComma(value) : value;
            var target = "#{0} table tr td[td-id='td_{1}']".format(divId, key);

            // 해당 태그에 html 셋팅
            if($(target).length == 1) {
                switch (key) {
                    default:
                        COMMON.Utils.fnIsEmpty(value) ? $(target).html("") : $(target).html(value);
                        break;
                }
            }
        }
    },

    /**------------------------------------------------------------
     * Function Name  : fnAddComma
     * Description    : -
     * Author         : jaden, 2018. 04. 29.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnAddComma: function(strValue) {
        var strRetValue = "";
        var strDecimalNumber = "";
        strRetValue += strValue;

        if(strRetValue.indexOf(".") != -1) {
            strDecimalNumber = "." + strRetValue.split(".")[1];
            strRetValue = strRetValue.split(".")[0];
        }

        var pattern = /(-?[0-9]+)([0-9]{3})/;

        while (pattern.test(strRetValue)) {
            strRetValue = strRetValue.replace(pattern, "$1,$2");
        }

        return strRetValue + strDecimalNumber;
    },

    /**------------------------------------------------------------
     * Function Name  : fnEnterEvent
     * Description    : 엔터키 입력 이벤트
     * Author         : payletter, 2019. 04. 23.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnEnterEvent: function(formObj, callBackFn) {
        formObj.find(".form-control").on("keydown", function(event) {if(13 == event.which) eval(callBackFn)();});
    },

    /**------------------------------------------------------------
     * Function Name  : fnCreateSelectOptions - select code
     * Description    : Create option in select box
     * Author         : payletter, 2020. 02. 11
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnCreateSelectOptions: function(selectBoxName, optionData, selectedValue) {
        var objSelectBox = document.getElementById(selectBoxName);

        for (var i=0; i < optionData.length; i++) {
            var eachOption = optionData[i];
            var objOption = document.createElement("option");

            objOption.text = eachOption.codeName;
            objOption.value = eachOption.codeID;

            objSelectBox.options.add(objOption);
        }

        if(!COMMON.Utils.fnIsEmpty(selectedValue)) {
            $("#" + selectBoxName + "").select2("val", selectedValue);
        } else {
            $("#" + selectBoxName + "").select2("val", "");
        }
    },

    /**------------------------------------------------------------
     * Function Name  : fnCreateBarChart
     * Description    : Create Bar chart
     * Author         : payletter, 2019. 10. 01.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnCreateBarChart: function(divName, chartData, yArray) {
        // 차트 생성
        var chart = am4core.create(divName, am4charts.XYChart);

        chart.data = chartData;

        // x축 관련 셋팅
        var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
        categoryAxis.dataFields.category = "xLabel";
        categoryAxis.renderer.grid.template.location = 0;
        categoryAxis.renderer.minGridDistance = 30;
        categoryAxis.renderer.labels.template.rotation = 300;
        categoryAxis.renderer.labels.template.horizontalCenter = "middle";
        categoryAxis.renderer.labels.template.verticalCenter = "middle";

        // y축 관련 셋팅
        var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
        valueAxis.calculateTotals = true;
        valueAxis.min = 0;

        // 데이터 갯수에 따른 막대 그래프 설정
        for(var i = 0; i < yArray.length; i++) {
            var barSeries = chart.series.push(new am4charts.ColumnSeries());
            barSeries.dataFields.valueY =  yArray[i];
            barSeries.dataFields.categoryX = "xLabel";
            barSeries.name = yArray[i];
            barSeries.columns.template.tooltipText = "{categoryX}: [bold]{valueY}[/]";
            barSeries.columns.template.fillOpacity = .8;
            barSeries.columns.template.strokeWidth = 2;
            barSeries.columns.template.strokeOpacity = 1;
        }

        chart.legend = new am4charts.Legend();

        return chart;
    },

    /**------------------------------------------------------------
     * Function Name  : fnCreateBarAndLineChart
     * Description    : Create Bar & Line chart
     * Author         : payletter, 2019. 10. 01.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnCreateBarAndLineChart: function(divName, chartData, barYArray, lineYArray) {
        // 막대 차트 생성
        var chart = COMMON.Utils.fnCreateBarChart(divName, chartData, barYArray);

        var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
        valueAxis.renderer.opposite = true;

        // 데이터 갯수에 따른 선 그래프 설정
        for(var i = 0; i < lineYArray.length; i++) {
            var lineSeries = chart.series.push(new am4charts.LineSeries());
            lineSeries.dataFields.valueY = lineYArray[i];
            lineSeries.dataFields.categoryX = "xLabel";
            lineSeries.name = lineYArray[i];
            lineSeries.yAxis = valueAxis;
            lineSeries.strokeWidth = 3;
            lineSeries.stroke = am4core.color("#fdd400");
        }

        var bullet = lineSeries.bullets.push(new am4charts.Bullet());
        bullet.fill = am4core.color("#fdd400");
        bullet.tooltipText = "{categoryX}:\n[/][bold]{valueY}[/] {additional}[/]";
        var circle = bullet.createChild(am4core.Circle);
        circle.radius = 4;
        circle.strokeWidth = 3;
    },

    /**------------------------------------------------------------
     * Function Name  : fnCreateLineChart
     * Description    : Create Line chart
     * Author         : payletter, 2019. 10. 01.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnCreateLineChart: function(divName, chartData, yArray, lineColor) {
        // 차트 생성
        var chart = am4core.create(divName, am4charts.XYChart);

        chart.data = chartData;

        // x축 관련 셋팅
        var categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
        categoryAxis.dataFields.category = "xLabel";
        categoryAxis.renderer.grid.template.location = 0;
        categoryAxis.renderer.minGridDistance = 30;
        categoryAxis.renderer.labels.template.rotation = 300;
        categoryAxis.renderer.labels.template.horizontalCenter = "middle";
        categoryAxis.renderer.labels.template.verticalCenter = "middle";

        // y축 관련 셋팅
        var valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
        valueAxis.renderer.minLabelPosition = 0.01;

        // 데이터 갯수에 따른 선 그래프 설정
        for(var i = 0; i < yArray.length; i++) {
            var lineSeries = chart.series.push(new am4charts.LineSeries());
            lineSeries.dataFields.valueY = yArray[i];
            lineSeries.name = yArray[i];
            lineSeries.dataFields.categoryX = "xLabel";
            lineSeries.strokeWidth = 3;
            lineSeries.bullets.push(new am4charts.CircleBullet());
            lineSeries.legendSettings.valueText = "{valueY}";

            if(!COMMON.Utils.fnIsEmpty(lineColor)) {
                lineSeries.stroke = lineColor;
                lineSeries.fill = lineColor;
            }
        }

        chart.cursor = new am4charts.XYCursor();
        chart.legend = new am4charts.Legend();
    },

    /**------------------------------------------------------------
     * Function Name  : fnCreatePieChart
     * Description    : Create Line chart
     * Author         : payletter, 2019. 10. 01.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnCreatePieChart: function(divName, chartData) {
        var chart = am4core.create(divName, am4charts.PieChart);

        chart.data = chartData;

        var pieSeries = chart.series.push(new am4charts.PieSeries());
        pieSeries.colors.list = CHARTCOLORLIST;
        pieSeries.dataFields.category = "xlabel";
        pieSeries.dataFields.value = "yvalue";

        chart.legend = new am4charts.Legend();
    }
}

/**------------------------------------------------------------
 * COMMON.Msg
 ------------------------------------------------------------*/
COMMON.Msg = {
    /**------------------------------------------------------------
     * Function Name  : fnAlert
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnAlert: function(strMsg, strCallBack, objCallBackParam) {
        strMsg = strMsg.replace(/[\r\n]/g, '<br>');
        $("#modalMsg").text(strMsg);

        $("#alertModal #btnAlertModalBottom").unbind("click");

        if(typeof objCallBackParam != 'undefined' && null != objCallBackParam && "" != objCallBackParam) {
            if(null != objCallBackParam.objJson.intRetCode) {
                var iconClass = (objCallBackParam.objJson.intRetCode == "0") ? "fa fa-check-circle-o fa-2x" : "fa fa-times-circle-o fa-2x";
                $("#alertModal #alertModalIcon").prop("class", iconClass);
            }
        }

        if(null != strCallBack && "" != strCallBack) {
            $("#alertModal #btnAlertModalBottom").bind("click", function() {
                if(null != objCallBackParam && "" != objCallBackParam) {
                    eval(strCallBack)(objCallBackParam.objJson, objCallBackParam.requestData);
                } else {
                    eval(strCallBack)();
                }
            });
        }

        $("#btnModalMsg").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnConfirm
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnConfirm: function(strMsg, callBackFN) {
        strAlertCallBack = callBackFN;
        $("#modalConfirm").html(strMsg);
        $("#btnModalConfirm").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnLoginConfirm
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnLoginConfirm: function($objData, callBackFN) {
        strAlertCallBack = callBackFN;
        $("#spFinalAccessTime").text($objData.data["lastLoginDate"]);
        $("#spFinalAccessIp").html($objData.data["lastLoginIP"]);
        $("#btnModalLoginConfirm").click();
        $("#btnLoginConfirmModal").focus();
    },

    /**------------------------------------------------------------
     * Function Name  : fnAlertWithModal
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnAlertWithModal: function(strMsg, preModalId, preModalOpen, callBackFN) {
        strAlertCallBack = typeof callBackFN == "undefined" ? function() {} : callBackFN;
        strPreModalId  = preModalId;
        isPreModalOpen = preModalOpen;
        $("#" + preModalId).modal("hide");

        $("#modalMsg").text(strMsg);
        $("#btnModalMsg").click();
    },

    /**------------------------------------------------------------
     * Function Name  : fnConfirmWithModal
     * Description    : -
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    fnConfirmWithModal: function(strMsg, preModalId, preModalOpen, callBackFN) {
        strAlertCallBack = typeof callBackFN == "undefined" ? function() {} : callBackFN;
        strPreModalId  = preModalId;
        isPreModalOpen = preModalOpen;
        $("#" + preModalId).modal("hide");

        $("#modalConfirm").html(strMsg);
        $("#btnModalConfirm").click();
    }
}

/**------------------------------------------------------------
 * COMMON.AntiCSRF
 ------------------------------------------------------------*/
COMMON.AntiCSRF = {
    /**------------------------------------------------------------
     * Function Name  : getVerificationToken
     * Description    : Get CSRF Token
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    getVerificationToken: function() {
        var strCSRFValue = "";

        if(!gisCSRFToken) {
            $.ajax({
                cache : false,
                type  : 'POST',
                url   : "/login/getCSRFToken",
                async : false,
                success: function(objJson) {
                    strCSRFValue = $.trim(objJson.CSRFToken);
                    $("#" + COMMON.CSRFID).val(strCSRFValue);
                }
            });

            gisCSRFToken = true;
        } else {
            strCSRFValue = $("#" + COMMON.CSRFID).val();
        }

        return strCSRFValue;
    }
}

/**------------------------------------------------------------
 * COMMON.Auth
 ------------------------------------------------------------*/
COMMON.Auth = {
    AuthCode: null,
    AuthDLCode: null,

    /**------------------------------------------------------------
     * Function Name  : Init
     * Description    : Auth 초기화
     * Author         : jaden, 2018. 04. 20.
     * Modify History : Just Created.
     ------------------------------------------------------------*/
    Init: function(pageAuthCode, pageAuthDLCode) {
        this.AuthCode = pageAuthCode;       // 현재 접근한 메뉴에 대한 사용자의 권한
        this.AuthDLCode = pageAuthDLCode;   // 현재 접근한 메뉴에 대한 사용자의 다운로드 권한
        this.fnShowAndHide();
    },

    // 초기화 재수행 - 동적 html 처리시 필요한 경우 직접 호출..
    Refresh: function() {
        this.fnShowAndHide();
    },

    // 버튼/Layer 등 Show/Hide 수행
    fnShowAndHide: function() {
        // ALL 권한일 경우 auth-readonly hide
        if(this.AuthCode === AUTHCODE.ALL) {
            $(".auth-all").show();
            $(".auth-readonly").hide();

            // READONLY 권한일 경우 auth-all hide
        } else if(this.AuthCode === AUTHCODE.READONLY) {
            $(".auth-readonly").show();
            $(".auth-all").hide();

            // 그 외 지정되지 않은 값은 모두 hide
        } else {
            $(".auth-readonly").hide();
            $(".auth-all").hide();
        }

        // 엑셀 다운로드 권한에 따른 버튼 show/hide
        if(this.AuthDLCode) {
            $(".auth-dl").show();
        } else{
            $(".auth-dl").hide();
        }
    },

    // 접근한 사용자 권한이 지정한 권한을 포함 하는지 여부 확인(AuthCode는 낮은 숫자가 높은 권한)
    fnCheckHasAuth: function(intAuthCode, intAvailAuthCode) {
        return intAuthCode <= intAvailAuthCode;
    }
}

/**------------------------------------------------------------
 * COMMON.Navi
 ------------------------------------------------------------*/
COMMON.Navi = {
    /**------------------------------------------------------------
     * Function Name  : fnGoMain
     * Description    : Main 화면 이동
     ------------------------------------------------------------*/
    fnGoMain : function() {
        document.location.href = "/";
        return false;
    },

    /**------------------------------------------------------------
     * Function Name  : fnGoLogin
     * Description    : Login 화면 이동
     ------------------------------------------------------------*/
    fnGoLogin : function() {
        document.location.href = "/login/login";
        return false;
    },

    /**------------------------------------------------------------
     * Function Name  : fnGoLogout
     * Description    : Logout 화면 이동
     ------------------------------------------------------------*/
    fnGoLogout : function() {
        document.location.href = "/login/logout";
        return false;
    },

    /**------------------------------------------------------------
     * Function Name  : fnGoMenu
     * Description    : 메뉴 이동
     ------------------------------------------------------------*/
    fnGoMenu : function(objMenuInfo) {
        document.location.href = objMenuInfo.menuLink;
        return false;
    }
}

/**------------------------------------------------------------
 * Function Name  : serializeObject
 * Description    : Get Serialize Object
 * Modify History : Just Created.
 ------------------------------------------------------------*/
$.fn.serializeObject = function() {
    var o = {};
    var a = this.serializeArray();

    $.each(a, function() {
        if(o[this.name] !== undefined) {
            if(!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push($.trim(this.value) || '');
        } else {
            o[this.name] = $.trim(this.value) || '';
        }
    });

    return o;
};

/**------------------------------------------------------------
 * validation
 ------------------------------------------------------------*/
$(function() {
    $.validator.addMethod("notEqualTo", function(value, element, param) {
        return this.optional(element) || value != $(param).val();
    }, "Please specify a different value.");

    $.validator.addMethod("dupId", function(value, element, param) {
        return this.optional(element) || "N" != $(param).val();
    }, "Please specify a different Id or No.");

    $.validator.addMethod("alphaNumeric", function(value, element, param) {
        return this.optional(element) || /^[a-zA-Z0-9]+$/.test(value);
    }, "Please input alphanumeric characters only for Id.");

    $.validator.addMethod("passwordCheck", function(value, element, param) {
        return this.optional(element) || /^.*(?=.*\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&+=]).*$/.test(value);
    }, "Please enter a valid password value.(character + number + special character)");

    $.validator.addMethod("pwCheckConsecChars", function(value, element, param) {
        return this.optional(element) || !/(.)\1{2,}/.test(value);
    }, "The password must not contain 3 consecutive identical characters.");

    $.validator.addMethod("rate", function(value, element, param) {
        return this.optional(element) || /^(?=.)(?:[1-9]\d{0,2})?(?:\.\d{1,2})?$/.test(value);
    }, "Please enter a valid rate value.(decimal point is allowed up to 2)");

    $.validator.addMethod("money", function(value, element, param) {
        return this.optional(element) || /^(?=.)(?:[1-9]\d{0,18})?(?:\.\d{1,2})?$/.test(value) || value <= 0;
    }, "Please enter a valid rate value.(decimal point is allowed up to 2)");

    $.validator.addMethod("noKorean", function(value, element, param) {
        return this.optional(element) || !/[\u3131-\u314e|\u314f-\u3163|\uac00-\ud7a3]/.test(value);
    }, "Korean is not allowed in this field.");

    $.validator.addMethod("yyyymmdd", function(value, element, param) {
        return this.optional(element) || !/^[12][0-9]{3}[01][0-9][0-3][0-9]$/.test(value);
    }, "Invalid date format.");

    $.validator.addMethod("celNo", function(value, element, param) {
        return this.optional(element) || /^[0-9-]{2,20}$/.test(value);
    }, "Please input numeric and '-' characters only for cell phone number.");
});

/**------------------------------------------------------------
 * Function Name  : dataTable.Api.register
 * Description    : Data Table Pipeline
 * Modify History : Just Created.
 ------------------------------------------------------------*/
$.fn.dataTable.Api.register('clearPipeline()', function() {
    return this.iterator('table', function(settings) {
        settings.clearCache = true;
    });
});

/**------------------------------------------------------------
 * String 확장 메소드
 ------------------------------------------------------------*/
String.prototype.format = function() {
    var strString = this;

    for(var arg in arguments){
        var re = new RegExp("\\{" + arg + "\\}", "gm");
        strString = strString.replace(re, arguments[arg]);
    }

    return strString;
}

/**------------------------------------------------------------
 * Array 확장 메소드
 ------------------------------------------------------------*/
Array.prototype.contains = function(obj) {
    var i = this.length;
    while (i--) {
        if(this[i] === obj) {
            return true;
        }
    }
    return false;
}