/* @version 1.0 | 20171019
 * @license: none (public login)
 */
if (document.addEventListener) {
    document.addEventListener("readystatechange", isReady);
} else if (document.attachEvent) {
    document.attachEvent("onreadystatechange", isReady);
}
var inputForm = "";

if (!document.querySelectorAll) {
    document.querySelectorAll = function (selector, container) {
        var s = selector.slice(0, 1);
        var c = [];
        var a = document.all;
        if (container) {
            a = container.all;
        }
        if (s == '#') {
            c = document.getElementById(selector.slice(1));
        } else if (s == ".") {
            for (i = a.length; i--;) {
                if (a[i].className == selector.slice(1)) {
                    c.push(a[i]);
                }
            }
        } else if (s == "[") {
            var attrs = selector.slice(1, selector.length - 1);
            var attr = attrs.split('=');
            if (attr.length > 1) {
                var att = attr[0].split('*');
                if (att.length > 1) {
                    for (i = a.length; i--;) {
                        if (a[i].getAttribute(att[0]) && a[i].getAttribute(att[0]).indexOf(attr[1]) >= 0) {
                            c.push(a[i]);
                        }
                    }

                } else {
                    for (i = a.length; i--;) {
                        if (a[i].getAttribute(att[0]) == attr[1]) {
                            c.push(a[i]);
                        }
                    }
                }
            } else {
                for (i = a.length; i--;) {
                    if (a[i].getAttribute(attr[0])) {
                        c.push(a[i]);
                    }
                }
            }
        }
        return c;
    }
}


function isReady() {
    if (document.readyState === "complete") {
        if (window.InitPage) {
            window.InitPage();
        }
        //InitInput();
        //InitRights();
        if (inputForm != "") {
            var inputSplist = inputForm.split(',');
            for (var i = 0; i < inputSplist.length; i++) {
                judgeAllInput(inputSplist[i]);
            }
        } else
            judgeAllInput();
    }
}
function InitInput() {
    var inputs = document.getElementsByTagName("input")
    for (var i = 0; i < inputs.length; i++) {
        var input = inputs[i];
        if (input.id != "") {
            var span = document.createElement("div");
            span.className = "delInput";
            span.name = input.id;
            //清空input
            span.onclick = function () {
                if (this.parentElement.getElementsByTagName("input").length == 1) {
                    this.parentElement.getElementsByTagName("input")[0].value = "";
                    this.parentElement.getElementsByTagName("input")[0].focus();
                } else {
                    this.parentElement.getElementsByTagName("input")[0].value = "";
                    this.parentElement.getElementsByTagName("input")[1].value = "";
                    this.parentElement.getElementsByTagName("input")[0].focus();
                }
                //input.parentElement.appendChild(span);
                //document.getElementById(this.name).value="";
                //document.getElementById(this.name).focus();
            }
            //input.parentElement.appendChild(span);
            //是否有删除小标
            //				if(input.getAttribute("isdel")!=null)
            //				{
            input.onmouseover = function () {
                if (this.type == "text")
                    this.parentElement.appendChild(span);
            }
            //					input.onmouseout=function()
            //					{ 
            //						this.parentElement.removeChild(span);
            //					}
            //}
            //是否有提示文字
            if (input.getAttribute("msgShow") != null) {
                input.onmouseover = function () {

                    document.getElementById("infoWin").style.display = "block";
                    var o = document.getElementById(this.id);
                    var oTop = o.offsetTop;
                    var oLeft = o.offsetLeft;
                    while (o.offsetParent != null) {
                        oParent = o.offsetParent;
                        oTop += oParent.offsetTop;
                        oLeft += oParent.offsetLeft;
                        o = oParent;
                    }
                    document.getElementById("msgTitle").innerHTML = document.getElementById(this.id).getAttribute("msgTitle");
                    document.getElementById("msgInfo").innerHTML = document.getElementById(this.id).getAttribute("msgShow");
                    document.getElementById("infoWin").style.top = oTop - parseInt(document.getElementById("infoWin").offsetHeight) + "px";
                    document.getElementById("infoWin").style.left = (oLeft - parseInt("10")) + "px";
                }
                input.onmouseout = function () {
                    document.getElementById("infoWin").style.display = "none";
                }
            }
        }
    }
}
//对input控件的实时判断
var dataName = "";
function judgeAllInput(formId, isAllText) {
    var flag = true;
    //must 必填 email 电子邮件 num 数字 date 日期 cn 中文 en 英文 url 合法网址 integer 整数
    //,email}]}
    var judgeValueList = "must,email,num,date,cn,en,url,Integer";
    var msgJsonInfo = "{\"msg\":[{\"must\":\"此项是必填项,请您输入!\",";
    msgJsonInfo += "\"email\":\"请输入正确的Email格式\",";
    msgJsonInfo += "\"num\":\"只能输入数字!\",";
    msgJsonInfo += "\"date\":\"只能输入日期!\",";
    msgJsonInfo += "\"cn\":\"只能输入中文!\",";
    msgJsonInfo += "\"en\":\"只能输入英文!\"";
    msgJsonInfo += "}]}";
    var judgeJsonInfo = "{\"judge\":[{\"must\":\"\",";
    judgeJsonInfo += "\"email\":/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/,";
    judgeJsonInfo += "\"num\":\/^[0-9]+.?[0-9]*$/,";
    judgeJsonInfo += "\"date\":/^(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})$/,";
    judgeJsonInfo += "\"cn\":\"/[\u4E00-\u9FA5\uF900-\uFA2D]/\",";
    judgeJsonInfo += "\"en\":\"new RegExp('^[a-z]*|[A-Z]*$')\"";
    judgeJsonInfo += "}]}";
    var judgedata = eval('(' + judgeJsonInfo + ')');
    var msgdata = eval('(' + msgJsonInfo + ')');
    var inputs = document.querySelectorAll("[judge],[ismust]");
    var formInput = document.createElement("input");
    formInput.type = "hidden";
    var formFoucsInput = document.createElement("input");
    formFoucsInput.type = "hidden";

    if (formId == null) {
        formInput.id = "formText";
        formFoucsInput.id = "focusInput";
        //inputs = document.querySelectorAll("[judge],[ismust]");
        inputs = document.getElementsByTagName("input")
        document.body.appendChild(formInput);
        document.body.appendChild(formFoucsInput);
    } else {
        formInput.id = formId + "-formText";
        formFoucsInput.id = formId + "-focusInput";
        inputs = document.getElementById(formId).getElementsByTagName("input");
        //.querySelectorAll("[judge],[ismust]")
        document.getElementById(formId).appendChild(formInput);
        document.getElementById(formId).appendChild(formFoucsInput);
    }
    for (var i = 0; i < inputs.length; i++) {
        var span = document.createElement("span");
        span.className = "bicss";
        span.innerHTML = "*";
        var td = document.createElement("td");
        //td.className="tdBiCss";
        var tdTwo = document.createElement("td");
        if (inputs[i].getAttribute("ismust") == "1")
            td.appendChild(span);
        if (isAllText != "1") {
            if (inputs[i].getAttribute("type") == "text") {
                //inputs[i].parentElement.parentElement.appendChild(tdTwo);
                span.className = "bicsspadding";
            } else {

            }
        }
        switch (inputs[i].getAttribute("judge")) {
            case "num":
                inputs[i].onkeyup = function () {
                    this.value = this.value.replace(/[^\d\.]/g, '');
                }
                break;
            case "date":
                inputs[i].onclick = function () {
                    WdatePicker();
                }
                break;
        }
        //if (inputs[i].getAttribute("type") == "text") {
        //    td.className = "inputTd";
        //    inputs[i].parentElement.parentElement.appendChild(td);
        //} else {
        //    //td.className = "selectTd";
        //    //inputs[i].parentElement.parentElement.appendChild(td);
        //}

    }
}
function formValidatorAllInput(formId) {
    var flag = true;
    var formFlag = true;

    //must 必填 email 电子邮件 num 数字 date 日期 cn 中文 en 英文 url 合法网址 integer 整数
    //,email}]}
    var judgeValueList = "must,email,num,date,cn,en,url,Integer";
    var msgJsonInfo = "{\"msg\":[{\"must\":\"此项是必填项,请您输入!\",";
    msgJsonInfo += "\"email\":\"请输入正确的Email格式\",";
    msgJsonInfo += "\"num\":\"只能输入数字!\",";
    msgJsonInfo += "\"date\":\"只能输入日期!\",";
    msgJsonInfo += "\"cn\":\"只能输入中文!\",";
    msgJsonInfo += "\"en\":\"只能输入英文!\"";
    msgJsonInfo += "}]}";
    var judgeJsonInfo = "{\"judge\":[{\"must\":\"\",";
    judgeJsonInfo += "\"email\":/^([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+[_|\_|\.]?)*[a-zA-Z0-9]+\.[a-zA-Z]{2,3}$/,";
    judgeJsonInfo += "\"num\":\/^[0-9]+.?[0-9]*$/,";
    judgeJsonInfo += "\"date\":/^(\\d{1,4})(-|\\/)(\\d{1,2})\\2(\\d{1,2})$/,";
    judgeJsonInfo += "\"cn\":\"/[\u4E00-\u9FA5\uF900-\uFA2D]/\",";
    judgeJsonInfo += "\"en\":\"new RegExp('^[a-z]*|[A-Z]*$')\"";
    judgeJsonInfo += "}]}";
    var judgedata = eval('(' + judgeJsonInfo + ')');
    var msgdata = eval('(' + msgJsonInfo + ')');
    var inputs = null;
    var mustflag = true;


    if (formId == null) {
        inputs = document.querySelectorAll("[judge],[ismust]");
        if (document.getElementById("formText") != null) {
            document.getElementById("formText").value = "";
            document.getElementById("focusInput").value = "";

        }
    } else {
        inputs = document.getElementById(formId).querySelectorAll("[judge],[ismust]");
        if (document.getElementById(formId + "-formText") != null) {
            document.getElementById(formId + "-formText").value = "";
            document.getElementById(formId + "-focusInput").value = "";
        }
    }


    for (var i = 0; i < inputs.length; i++) {
        if (inputs[i].getAttribute("ismust") == "1") {
            if (inputs[i].value == "") {
                inputs[i].focus();
                formFlag == true
                if (formId == null) {
                    if (document.getElementById("formText").value != "")
                        formFlag = false;
                    if (formFlag == true) {
                        document.getElementById("formText").value = inputs[i].getAttribute("errorMsg");
                        document.getElementById("focusInput").value = inputs[i].id;
                    }
                } else {
                    if (document.getElementById(formId + "-formText").value != "")
                        formFlag = false;
                    if (formFlag == true) {
                        document.getElementById(formId + "-formText").value = inputs[i].getAttribute("errorMsg");
                        document.getElementById(formId + "-focusInput").value = inputs[i].id;
                    }
                }
                flag = false;
                mustflag = false;
            } else {
                flag = true;
                mustflag = true;
            }
        }
        var reg = "";
        if (inputs[i].value != "") {
            switch (inputs[i].getAttribute("judge")) {
                case "cn":
                    reg = /^[\u4E00-\u9FA5]+$/;
                    if (!reg.test(inputs[i].value))
                        flag = false; else
                        flag = true;
                    break;
                case "en":
                    reg = /^[a-zA-Z]+$/;
                    if (!reg.test(inputs[i].value))
                        flag = false; else
                        flag = true;
                    break;
                case "num":
                    if (!judgedata.judge[0]["num"].test(inputs[i].value))
                        flag = false; else
                        flag = true;
                    break;
                //					  case "date":
                //					   if(!judgedata.judge[0]["date"].test(inputs[i].value))
                //						    flag=false;
                //						  else
                //							flag=true;
                //					  break;
                case "email":
                    if (!judgedata.judge[0]["email"].test(inputs[i].value))
                        flag = false; else
                        flag = true;
                    break;
                case "url":
                    var url = inputs[i].value.match(/http:\/\/.+/);
                    if (url == null)
                        flag = false; else
                        flag = true;
                    break;
            }
        }
        if (mustflag) {
            if (flag == false) {
                if (formId == null) {
                    document.getElementById("formText").value = inputs[i].getAttribute("ValidatorErrorMsg");
                    document.getElementById("focusInput").value = inputs[i].id;
                } else {
                    document.getElementById(formId + "-formText").value = inputs[i].getAttribute("ValidatorErrorMsg");
                    document.getElementById(formId + "-focusInput").value = inputs[i].id;
                }
                break;
            }
        } else {
            flag = mustflag;
            if (formId == null) {
                document.getElementById("formText").value = inputs[i].getAttribute("ErrorMsg");
                document.getElementById("focusInput").value = inputs[i].id;
            } else {
                document.getElementById(formId + "-formText").value = inputs[i].getAttribute("ErrorMsg");
                document.getElementById(formId + "-focusInput").value = inputs[i].id;
            }
            break;
        }

    }
    return flag;
}
function InitRights() {
    var s = location.href.split('/');
    var key = s[s.length - 1].split('.')[0]
    postData("GetUserRights?command=" + key, null, true, InitControlRights, onerror);
}

function InitControlRights(s) {
    DataReturns(s, function (dataset) {
        for (f in dataset.RIGHTS) {
            var field = dataset.RIGHTS[f]["DISABLED"];
            var inputs = document.querySelectorAll("#" + field);
            if (inputs.length > 0) {
                for (i in inputs) {
                    inputs[i].disabled = 'disabled';
                    if (document.getElementById(inputs[i].id) != null)
                        document.getElementById(inputs[i].id).style.display = "none";
                }
            }
            inputs = document.querySelectorAll("[CommandName*=" + field + "]");
            if (inputs.length > 0) {
                for (i in inputs) {
                    inputs[i].disabled = 'disabled';
                    if (document.getElementById(inputs[i].id) != null)
                        document.getElementById(inputs[i].id).style.display = "none";
                }
            }
        }
    });
}

//上传数据使用的方法
//url是上传所用的地址
//parms是上传数据的字符串
//callback是上传结束后得到结果的回调处理方法
//error是出现服务器错误时的数据回调处理方法
function postData(url, parms, usepost, callback, error) {
    try {
        var xmlhttp;
        if (window.XMLHttpRequest) {// code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
        } else {// code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.getResponseText = callback;
        xmlhttp.error = error;
        xmlhttp.onreadystatechange = procResponse;
        if (top.showloading) {
            top.showloading();
        }
        if (usepost) {
            xmlhttp.open("POST", url, false);
            xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            xmlhttp.send(parms);
        } else {
            xmlhttp.open("GET", url + "?" + parms, false);
            xmlhttp.send();
        }
    } catch (e) {
        if (top.hideloading) {
            top.hideloading();
        }
        showmsg(e);
    }
}
//处理上传数据状态更新的方法
function procResponse(e) {
    if (this.readyState == 4) {

        if (top.hideloading) {
            top.hideloading();
        }

        if (this.status == 200) {
            this.getResponseText(this.responseText);
        } else {
            //this.error(this.responseText);
        }
    }
}
function clearJqPost(id, selectId) {
    var postData = $("#" + id).jqGrid("getGridParam", "postData");
    $("#" + selectId + " option").each(function () {
        delete postData[$(this).val()];
    });
}
function BindFormByFrom(dataset) {
    var inputs = document.querySelectorAll("[FromTextField]");
    for (var i = 0; i < inputs.length; i++) {
        var field = inputs[i].getAttribute("FromTextField");
        if (field) {
            if (dataset[field]) {
                if (inputs[i].value != undefined) {
                    inputs[i].value = dataset[field];
                } else {
                    inputs[i].innerHTML = dataset[field];
                }
            }
        }
    }
}

//获取url地址里的参数
function GetRequestName(urlPara, requestName) {
    var parm = (requestName) ? requestName : urlPara;
    var url = (requestName) ? urlPara : location;
    var reg = new RegExp("[&|?]" + parm + "=([^&$]*)", "gi");
    var a = reg.test(url);
    return a ? RegExp.$1 : "";
}

function Q(urlPara, requestName) {
    var parm = (requestName) ? requestName : urlPara;
    var url = (requestName) ? urlPara : location;
    var reg = new RegExp("[&|?]" + parm + "=([^&$]*)", "gi");
    var a = reg.test(url);
    return a ? RegExp.$1 : "";
}

function FormBind(dataset, container) {
    var inputs;
    var eninputs;
    var visibleinputs;
    var checks;
    var p = document;
    if (container) {
        var con = document.getElementById(container);
        if (con) {
            p = con;
        }
    }
    inputs = [];
    if (p.querySelectorAll) {
        inputs = p.querySelectorAll("[DataField]");
    } else {
        inputs = document.querySelectorAll("[DataField]", p);
    }
    var inputType = "";
    var timeFormat = "";
    var inputValue = "";
    var isInfo = "0";
    for (var i = 0; i < inputs.length; i++) {
        var field = inputs[i].getAttribute("DataField");
        inputType = inputs[i].getAttribute("judge");
        timeFormat = inputs[i].getAttribute("timeFormat");
        if (inputs[i].getAttribute("isInfo") != null)
            isInfo = inputs[i].getAttribute("isInfo"); else
            isInfo = "0";
        if (field != null) {
            var fs = field.split(".");
            if (dataset[fs[0].toUpperCase()] != null && dataset[fs[0].toUpperCase()][0]) {
                if (dataset[fs[0].toUpperCase()][0][fs[1].toUpperCase()] != null) {
                    inputValue = dataset[fs[0].toUpperCase()][0][fs[1].toUpperCase()];
                    if (inputType == "date")
                        inputValue = getTimeFomart(inputValue, timeFormat);
                    if (inputs[i].value != undefined) {
                        if (inputs[i].type == "radio") {
                            var zt = document.getElementsByName(inputs[i].name);
                            for (var j = 0; j < zt.length; j++) {
                                if (zt[j].value == dataset[fs[0].toUpperCase()][0][fs[1]])
                                    zt[j].checked = true; else
                                    zt[j].checked = false;
                            }
                        }
                        if (inputs[i].type == "checkbox") {
                            var zt = document.getElementsByName(inputs[i].name);
                            for (var j = 0; j < zt.length; j++) {
                                if (dataset[fs[0].toUpperCase()][0][fs[1]].indexOf(zt[j].value) > -1)
                                    zt[j].checked = true; else
                                    zt[j].checked = false;
                            }
                        }
                        if (inputs[i].type != "radio" && inputs[i].type != "checkbox") {
                            if (isInfo == "1")
                                inputs[i].value = unescape(inputValue); else
                                inputs[i].value = inputValue;
                        }
                    } else {
                        if (isInfo == "1")
                            inputs[i].innerText = unescape(inputValue); else
                            inputs[i].innerText = inputValue;
                    }
                } else {
                    if (inputs[i].value != undefined) {
                        inputs[i].value = "";
                    } else {
                        inputs[i].innerHTML = "";
                    }
                }
            }
        }
    }

    eninputs = [];
    if (p.querySelectorAll) {
        eninputs = p.querySelectorAll("[EnableField]");
    } else {
        eninputs = document.querySelectorAll("[EnableField]", p);
    }
    for (var i = 0; i < eninputs.length; i++) {
        var enfield = eninputs[i].getAttribute("EnableField");
        var EnableValue = "1";
        if (eninputs[i].getAttribute("EnableValue") != null)
            EnableValue = eninputs[i].getAttribute("EnableValue");
        if (enfield != null) {
            var fs = enfield.split(".");
            if (dataset[fs[0].toUpperCase()] != null) {
                if (dataset[fs[0].toUpperCase()][0][fs[1].toUpperCase()] != null) {
                    if (dataset[fs[0].toUpperCase()][0][fs[1].toUpperCase()] == EnableValue) {
                        eninputs[i].disabled = "";
                    } else {
                        eninputs[i].disabled = "disabled";

                    }
                }
            }
        }
    }

    visibleinputs = [];
    if (p.querySelectorAll) {
        visibleinputs = p.querySelectorAll("[VisibleField]");
    } else {
        visibleinputs = document.querySelectorAll("[VisibleField]", p);
    }
    for (var i = 0; i < visibleinputs.length; i++) {
        var visiblefield = visibleinputs[i].getAttribute("VisibleField");
        var VisibleValue = "1";
        if (visibleinputs[i].getAttribute("VisibleValue") != null)
            VisibleValue = visibleinputs[i].getAttribute("VisibleValue");
        if (visiblefield != null) {
            var fs = visiblefield.split(".");
            if (dataset[fs[0].toUpperCase()] != null) {
                if (dataset[fs[0].toUpperCase()][0][fs[1].toUpperCase()] != null) {
                    if (dataset[fs[0].toUpperCase()][0][fs[1].toUpperCase()] != VisibleValue) {
                        visibleinputs[i].style.display = "none";
                    } else {
                        visibleinputs[i].style.display = "block";

                    }
                }
            }
        }
    }


    checks = [];
    if (p.querySelectorAll) {
        checks = p.querySelectorAll("[CheckedField]");
    } else {
        checks = document.querySelectorAll("[CheckedField]", p);
    }
    for (var i = 0; i < checks.length; i++) {
        var ckfield = checks[i].getAttribute("CheckedField");
        if (ckfield != null) {
            var fs = ckfield.split(".");
            if (dataset[fs[0]] != null) {
                if (dataset[fs[0]][0][fs[1]] != null) {

                    var ckv = checks[i].getAttribute("CheckedValue");
                    var unckv = checks[i].getAttribute("UnCheckedValue");
                    if (ckv && unckv) {
                        if (dataset[fs[0]][0][fs[1]] == ckv) {
                            checks[i].checked = true;
                        } else {
                            checks[i].checked = false;
                        }
                    } else {
                        if (dataset[fs[0]][0][fs[1]] == "0") {
                            checks[i].checked = false;
                        } else {
                            checks[i].checked = true;
                        }
                    }
                }
            }
        }
    }
    var urls = [];
    if (p.querySelectorAll) {
        urls = p.querySelectorAll("[UrlField]");
    } else {
        urls = document.querySelectorAll("[UrlField]", p);
    }
    for (var i = 0; i < urls.length; i++) {
        if ((urls[i].href != undefined && (urls[i].href == null || urls[i].href == "")) || (urls[i].src != undefined && (urls[i].src == null || urls[i].src == ""))) {
            var urlfield = urls[i].getAttribute("UrlField");
            var format = urls[i].getAttribute("UrlFormat");
            if (urlfield != null) {
                var url = "";
                if (format != null) {
                    url = format;
                    var ufs = urlfield.split(",");
                    for (var n = 0; n < ufs.length; n++) {
                        var fs = ufs[n].split(".");
                        var v = "";
                        if (dataset[fs[0]] != null) {
                            if (dataset[fs[0]][0][fs[1]] != null) {
                                v = dataset[fs[0]][0][fs[1]];
                            }
                        }
                        url = url.replace("{" + n + "}", v);
                    }
                } else {
                    var fs = urlfield.split(".");
                    if (dataset[fs[0]] != null) {
                        if (dataset[fs[0]][0][fs[1]] != null) {
                            url = dataset[fs[0]][0][fs[1]];
                        }
                    }
                }

                if (urls[i].href != undefined) {
                    urls[i].href = url;
                }
                if (urls[i].src != undefined) {
                    urls[i].src = url;
                }
            }
        }
    }
}


//窗体数据转换为json
function FormToData(container) {
    var inputs;
    var checked;
    var c = document.getElementById(container);
    var datas = new Array();
    if (c != null) {
        if (c.querySelectorAll) {
            inputs = c.querySelectorAll("[DataField]");
            checked = c.querySelectorAll("[CheckedField]");
        } else {
            inputs = document.querySelectorAll("[DataField]", c);
            checked = document.querySelectorAll("[CheckedField]", c);
        }
    } else {
        inputs = document.querySelectorAll("[DataField]");
        checked = document.querySelectorAll("[CheckedField]");
    }
    var tableName = null, dataObj = null, judge = null;
    var startVal = "";  //前端字符
    var endVal = "";  //后端字符
    var isInfo = "0";
    var radioName = "", checkBoxName = "", checkBoxValue = "";
    var inputValue = "";
    for (var i = 0; i < inputs.length; i++) {
        var field = inputs[i].getAttribute("DataField");
        if (inputs[i].getAttribute("dataNamefield") != null)
            tableName = inputs[i].getAttribute("dataNamefield");
        if (inputs[i].getAttribute("dataObjfield") != null)
            dataObj = inputs[i].getAttribute("dataObjfield");
        if (inputs[i].getAttribute("judge") != null)
            judge = inputs[i].getAttribute("judge"); else
            judge = null;
        //前端字符
        if (inputs[i].getAttribute("startVal") != null)
            startVal = inputs[i].getAttribute("startVal"); else
            startVal = "";
        //后端字符
        if (inputs[i].getAttribute("endVal") != null)
            endVal = inputs[i].getAttribute("endVal"); else
            endVal = "";
        if (inputs[i].getAttribute("isInfo") != null)
            isInfo = inputs[i].getAttribute("isInfo");       //是否是text多行文本
        else
            isInfo = "0";
        if (inputs[i].value != undefined) {
            if (field != null && field != "") {
                var fs = field.split(".");
                if (datas[fs[0]] == null) {
                    datas[fs[0]] = new Array();
                    datas[fs[0]].push([]);
                }
                var row = datas[fs[0]][0];
                //
                if (inputs[i].type == "radio") {
                    if (radioName.indexOf(inputs[i].name) < 0) {
                        var zt = document.getElementsByName(inputs[i].name);
                        for (var j = 0; j < zt.length; j++) {
                            if (zt[j].checked) {
                                if (zt[j].value.substring(zt[j].value.length - 1) == ",")
                                    row[fs[1]] = "\"" + fs[1] + "\":\"" + +startVal + zt[j].value.substring(0,
                                            zt[j].value.length - 1) + endVal + "\""; else
                                    row[fs[1]] = "\"" + fs[1] + "\":\"" + +startVal + zt[j].value + endVal + "\"";
                                radioName += inputs[j].name + ",";
                            }
                        }
                    }
                }
                if (inputs[i].type == "checkbox") {
                    checkBoxValue = "";
                    if (checkBoxName.indexOf(inputs[i].name) < 0) {
                        var zt = document.getElementsByName(inputs[i].name);
                        for (var j = 0; j < zt.length; j++) {
                            if (zt[j].checked) {
                                //row[fs[1]] = "\"" + fs[1] + "\":\"" + zt[j].value + "\"";
                                checkBoxValue += zt[j].value + ",";
                                checkBoxName += zt[j].name + ",";
                                checkFlag = true;
                            }
                        }

                    }
                    if (checkBoxValue != "") {
                        if (checkBoxValue.substring(checkBoxValue.length - 1) == ",")
                            checkBoxValue = checkBoxValue.substring(0, checkBoxValue.length - 1)
                        row[fs[1]] = "\"" + fs[1] + "\":\"" + startVal + checkBoxValue + endVal + "\"";
                    }
                }

                //                if (row[fs[1]]) {
                //                    var l = datas[fs[0]].push([]);
                //                    row = datas[fs[0]][l - 1];
                //                }
                if (inputs[i].type != "radio" && inputs[i].type != "checkbox") {
                    if (isInfo == "1")
                        inputValue = escape(inputs[i].value); else
                        inputValue = inputs[i].value;
                    if (inputs[i].value != "")
                        row[fs[1]] = "\"" + fs[1] + "\":\"" + startVal + inputValue + endVal + "\""; else
                        row[fs[1]] = "\"" + fs[1] + "\":\"" + inputValue + "\"";
                }

                var field = inputs[i].getAttribute("CheckedField");
                if (field) {
                    var cs = field.split(".");
                    row.push("\"" + cs[1] + "\":\"" + (inputs[i].checked ? "1" : "0") + "\"");
                }
            }
        }
    }
    for (var i = 0; i < checked.length; i++) {
        var field = checked[i].getAttribute("CheckedField");
        if (!checked[i].getAttribute("DataField")) {
            if (field != null && field != "") {
                var fs = field.split(".");
                if (datas[fs[0]] == null) {
                    datas[fs[0]] = new Array();
                    datas[fs[0]].push([]);
                }
                var row = datas[fs[0]][0];

                var ckv = checked[i].getAttribute("CheckedValue");
                var unckv = checked[i].getAttribute("UnCheckedValue");
                if (ckv && unckv) {
                    row.push("\"" + fs[1] + "\":\"" + (checked[i].checked ? ckv : unckv) + "\"");
                } else {
                    row.push("\"" + fs[1] + "\":\"" + (checked[i].checked ? "1" : "0") + "\"");
                }

            }
        }
    }

    var rtdatas = new Array();
    var d = 0;
    for (var table in datas) {
        var rows = new Array();
        for (datarow in datas[table]) {
            var cells = new Array();
            var r = 0;
            for (datacell in datas[table][datarow]) {
                cells[r] = datas[table][datarow][datacell];
                r++;
            }
            rows.push("{" + cells.join(',') + "}");
        }
        rtdatas[d] = "\"" + table + "\":[" + rows.join(",") + "]";
        d++;
    }
    var litableName = "";
    if (tableName != null) {
        litableName = "\"tableName\":\"" + tableName + "\"";
    }
    var rt = "";
    if (litableName != "")
        rt = rtdatas.join(",") + ",sqlData:[{" + litableName + "}]"; else
        rt = rtdatas.join(",");
    if (rt != "") {
        return "{" + rt + "}";
    } else {
        return "";
    }
}

//窗体数据转换为json
function customFormToData(container, objName) {
    var inputs;
    var checked;
    var c = document.getElementById(container);
    var datas = new Array();
    if (c != null) {
        inputs = document.querySelectorAll("[DataField]", c);
        checked = document.querySelectorAll("[CheckedField]", c);
    } else {
        return {};
    }


    var dataObj = null, judge = null;
    var isInfo = "0";
    var radioName = "", checkBoxName = "", checkBoxValue = "";
    var inputValue = "";
    for (var i = 0; i < inputs.length; i++) {
        var field = inputs[i].getAttribute("DataField");
        if (inputs[i].getAttribute("dataNamefield") != null) tableName = inputs[i].getAttribute("dataNamefield");
        if (inputs[i].getAttribute("dataObjfield") != null) dataObj = inputs[i].getAttribute("dataObjfield");
        if (inputs[i].getAttribute("judge") != null) judge = inputs[i].getAttribute("judge"); else judge = null;

        if (inputs[i].getAttribute("isInfo") != null) isInfo = inputs[i].getAttribute("isInfo"); //是否是text多行文本
        else isInfo = "0";
        if (inputs[i].value != undefined) {
            if (field != null && field != "") {
                var fs = field.split(".");
                if (datas[fs[0]] == null) {
                    datas[fs[0]] = new Array();
                    datas[fs[0]].push([]);
                }
                var row = datas[fs[0]][0];
                //
                if (inputs[i].type == "radio") {
                    if (radioName.indexOf(inputs[i].name) < 0) {
                        var zt = document.getElementsByName(inputs[i].name);
                        for (var j = 0; j < zt.length; j++) {
                            if (zt[j].checked) {
                                if (zt[j].value.substring(zt[j].value.length - 1) == ",") row[fs[1]] = "\"" + fs[1] + "\":\"" + zt[j].value.substring(0,
                                        zt[j].value.length - 1) + "\""; else row[fs[1]] = "\"" + fs[1] + "\":\"" + zt[j].value + "\"";
                                radioName += inputs[j].name + ",";
                            }
                        }
                    }
                }
                if (inputs[i].type == "checkbox") {
                    checkBoxValue = "";
                    if (checkBoxName.indexOf(inputs[i].name) < 0) {
                        var zt = document.getElementsByName(inputs[i].name);
                        for (var j = 0; j < zt.length; j++) {
                            checkBoxName += zt[j].name + ",";
                            if (zt[j].checked) {
                                //row[fs[1]] = "\"" + fs[1] + "\":\"" + zt[j].value + "\"";
                                checkBoxValue += zt[j].value + ",";
                                checkBoxName += zt[j].name + ",";
                                checkFlag = true;
                            }
                        }
                    }
                    //if (checkBoxValue != "") {
                        if (checkBoxValue.substring(checkBoxValue.length - 1) == ",") checkBoxValue = checkBoxValue.substring(0,
                            checkBoxValue.length - 1);
                        row[fs[1]] = "\"" + fs[1] + "\":\"" + checkBoxValue + "\"";
                    //}
                }

                if (inputs[i].type != "radio" && inputs[i].type != "checkbox") {
                    if (isInfo == "1") inputValue = escape(inputs[i].value); else inputValue = inputs[i].value;
                    inputValue = $.trim(inputValue);
                    if (inputs[i].value != "") row[fs[1]] = "\"" + fs[1] + "\":\"" + inputValue + "\""; else row[fs[1]] = "\"" + fs[1] + "\":\"" + inputValue + "\"";
                }

                var field = inputs[i].getAttribute("CheckedField");
                if (field) {
                    var cs = field.split(".");
                    row.push("\"" + cs[1] + "\":\"" + (inputs[i].checked ? "1" : "0") + "\"");
                }
            }
        }
    }
    for (var i = 0; i < checked.length; i++) {
        var field = checked[i].getAttribute("CheckedField");
        if (!checked[i].getAttribute("DataField")) {
            if (field != null && field != "") {
                var fs = field.split(".");
                if (datas[fs[0]] == null) {
                    datas[fs[0]] = new Array();
                    datas[fs[0]].push([]);
                }
                var row = datas[fs[0]][0];

                var ckv = checked[i].getAttribute("CheckedValue");
                var unckv = checked[i].getAttribute("UnCheckedValue");
                if (ckv && unckv) {
                    row.push("\"" + fs[1] + "\":\"" + (checked[i].checked ? ckv : unckv) + "\"");
                } else {
                    row.push("\"" + fs[1] + "\":\"" + (checked[i].checked ? "1" : "0") + "\"");
                }

            }
        }
    }

    var rtdatas = new Array();
    var d = 0;
    var prefix = "";
    if (null != objName && undefined != objName && "" != objName) {
        //prefix = objName+".";
    }
    for (var table in datas) {
        var rows = new Array();
        for (datarow in datas[table]) {
            var cells = new Array();
            var r = 0;
            for (datacell in datas[table][datarow]) {

                cells[r] = datas[table][datarow][datacell].substring(0,
                        1) + prefix + datas[table][datarow][datacell].substring(1);
                r++;
            }
            rows.push("{" + cells.join(',') + "}");
        }
        rtdatas[d] = rows.join(",");
        d++;
    }
    var litableName = "";
    rt = rtdatas.join(",");
    if (rt != "") {
        return rt;
    } else {
        return "";
    }
}

function GridSearchData(gridid, containerid) {
    var rtdata = "";
    if (gridid) {
        var querydata = "";
        var ct;
        if (containerid) {
            ct = document.getElementById(containerid);
        }
        querydata = FormToData(ct);
        var grid = window[gridid];
        rtdata = "{\"DataName\":\"" + grid.DataSourceName + "\",\"PageSize\":\"" + grid.Pager.PageSize + "\",\"Page\":\"1\",\"QueryData\":" + querydata + ",\"Grid\":\"" + gridid + "\"}"
    }
    return rtdata;
}

function ObjToJson(obj) {
    var datas = new Array();
    var i = 0;
    for (var name in obj) {
        if (obj[name] instanceof Array) {
            var ls = new Array();
            var n = 0;
            for (var subobj in obj[name]) {
                ls[n] = ObjToJson(obj[name][subobj]);
                n++;
            }
            datas[i] = "\"" + name + "\":[" + ls.join(',') + "]";
        } else {
            datas[i] = "\"" + name + "\":\"" + obj[name] + "\"";
        }
        i++;
    }
    return "{" + datas.join(',') + "}";
}

function showmsg(s, rt) {
    if (ShowMessages) {
        if (rt) {
            ShowMessages(s, null, null, function () {
                changelocation(rt);
            });
        } else {
            ShowMessages(s);
        }
    } else {
        if (rt) {
            changelocation(rt);
        }
    }
}

function ClearData(dataname) {
    var inputs = document.querySelectorAll("[DataField*='" + dataname + ".']");
    var isNoCrear = 0;
    for (var i = 0; i < inputs.length; i++) {
        if (inputs[i].getAttribute("isNoCrear") != null)
            isNoCrear = inputs[i].getAttribute("isNoCrear"); else
            isNoCrear = 0;
        if (inputs[i].value != undefined) {
            if (isNoCrear != 1)
                inputs[i].value = "";
        } else {
            inputs[i].innerHTML = "";
        }
    }
}

//清空某一个区域的所有input
function ClearAllInput(divName) {
    $("#status").html("");
    var inputs = document.getElementById(divName).getElementsByTagName("input");
    var isNoCrear = 0;
    for (var i = 0; i < inputs.length; i++) {
        if (inputs[i].getAttribute("isNoCrear") != null)
            isNoCrear = inputs[i].getAttribute("isNoCrear"); else
            isNoCrear = 0;
        if (inputs[i].value != undefined && inputs[i].getAttribute("required") == null) {
            if (isNoCrear != 1)
                inputs[i].value = "";
        } else if (inputs[i].getAttribute("required") == "required") {
            inputs[i].value = "";
        } else {
            inputs[i].innerHTML = "";
        }
    }
    var selects = document.getElementById(divName).getElementsByTagName("select");
    for (var i = 0; i < selects.length; i++) {
        selects[i].options[0].selected = true;
    }
    var textarea = document.getElementById(divName).getElementsByTagName("textarea");
    for (var i = 0; i < textarea.length; i++) {
        textarea[i].innerText = "";
    }
}

function QD(field) {
    var inputs = document.querySelectorAll("[DataField='" + field + "']");
    var vs = new Array();

    if (inputs.length > 0) {
        for (var i = 0; i < inputs.length; i++) {
            if (inputs[i].value != undefined) {
                vs.push(inputs[i].value);
            } else {
                vs.push(inputs[i].innerHTML);
            }
        }
    }
    return vs.join(",");
}
function SD(field, value) {
    if (field && value) {
        var inputs = document.querySelectorAll("[DataField='" + field + "']");
        if (inputs.length > 0) {
            for (var i = 0; i < inputs.length; i++) {
                if (inputs[i].value != undefined) {
                    inputs[i].value = value;
                } else {
                    inputs[i].innerHTML = value;
                }
            }
        }
    }
}

function DataReturns(s, proc) {
    if (s) {
        try {
            var data = eval("(" + s + ")");
            if (data.ReturnData) {
                var json = data.ReturnData;
                json = json.replace(/"([^"]+)":/g, function (v) {
                    return v.toUpperCase();
                });
                var rtdata = eval("(" + json + ")");
                if (proc) {
                    proc(rtdata);
                } else {
                    FormBind(rtdata);
                }

            }
            if (data.ErrMessage) {
                showmsg(data.ErrMessage, data.ReturnAddress);
            } else {
                if (data.ReturnAddress) {
                    changelocation(data.ReturnAddress);
                }
            }
            if (data.Grid) {
                var fs = data.Grid.split(",");
                for (var i = 0; i < fs.length; i++) {
                    if (window[fs[i]]) {
                        window[fs[i]].GetData(window[fs[i]].Pager.CurrentPage);
                    }
                }
            }
            if (data.Clear) {
                var fs = data.Clear.split(",");
                for (var i = 0; i < fs.length; i++) {
                    ClearData(fs[i]);
                }
            }
            if (data.Exec) {
                eval(data.Exec);
            }
        } catch (e) {
            showmsg(e);
        }
    }
    if (top.hideloading) {
        top.hideloading();
    }
}

function DoCommand(cmd, datacontainer) {
    var parms = '';
    if (datacontainer) {
        parms = FormToData(datacontainer);
    }
    if (parms != '') {
        postData(cmd, "CommandData=" + encodeURIComponent(parms), true, DataReturns, onerror);
    } else {
        postData(cmd, "", true, DataReturns, onerror);
    }
}

function onerror(s) {
    if (s != "") {
        var rt = eval("(" + s + ")")
        showmsg(rt.ErrMessage, rt.ReturnAddress);
    } else {
        showmsg("返回空值!");
    }
}

function focusnext(e) {
    var key = e.which;
    if (key == 13) {

        var $inp = document.querySelectorAll(":read-write");
        e.preventDefault();
        for (var i = 0; i < $inp.length; i++) {
            if ($inp[i] == e.srcElement) {
                if ($inp[i + 1]) {
                    $inp[i + 1].focus();
                } else {
                    $inp[0].focus();
                }
                break;
            }
        }
    }
}

function changelocation(url) {
    if (top.showloading) {
        top.showloading();
    }
    top.location = url;
}

function SerachGrid(gridid, container) {
    var grid = window[gridid];
    grid.Query = FormToData(container)
    grid.GetData(1);
}

function GetDataValue(field) {
    return QD(field);
}

function SetDataValue(field, value) {
    SD(field, value);
}
//DateValue 数据库里的时间值
//TimeFormat 时间格式
function getTimeFomart(DateValue, TimeFormat) {
    var returnValue = "";
    if (DateValue != "") {
        var myDate = new Date(DateValue);
        //myDate.setHours(myDate.getHours() - 14);

        var Month = myDate.getMonth() + 1;
        var Day = myDate.getDate();
        var H = myDate.getHours();
        var M = myDate.getMinutes();
        var S = myDate.getSeconds();
        if (Month < 10)
            Month = "0" + Month;
        if (Day < 10)
            Day = "0" + Day;
        if (H < 10)
            H = "0" + H;
        if (M < 10)
            M = "0" + M;
        if (S < 10)
            S = "0" + S;
        switch (TimeFormat) {
            case "YY-MM-DD":
                returnValue = myDate.getYear() + "-" + Month + "-" + Day;
                break;
            case "YYYY-MM-DD":
                returnValue = myDate.getFullYear() + "-" + Month + "-" + Day;
                break;
            case "YYYY-MM-DD hh:mm:ss":
                returnValue = myDate.getFullYear() + "-" + Month + "-" + Day + " " + H + ":" + M + ":" + S;
                break;
            default:
                returnValue = myDate.getYear() + "-" + Month + "-" + Day;
                break;
                break;
        }
    }
    return returnValue;
}
//字符串转换时间格式
//dateStr 时间字符串
function strToDateTime(dateStr) {
    var DateTime = dateStr.replace(/-/g, "/");
    return new Date(DateTime).toUTCString().replace("GMT", "CST");
}
function hasClass(obj, cls) {
    return obj.className.match(new RegExp('(\\s|^)' + cls + '(\\s|$)'));
}

function addClass(obj, cls) {
    if (!this.hasClass(obj, cls)) obj.className += " " + cls;
}

function removeClass(obj, cls) {
    if (hasClass(obj, cls)) {
        var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
        obj.className = obj.className.replace(reg, ' ');
    }
}
