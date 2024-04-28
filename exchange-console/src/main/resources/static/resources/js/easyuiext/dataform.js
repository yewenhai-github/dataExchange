if (document.addEventListener) {
    document.addEventListener("readystatechange", isReady);
}
else if (document.attachEvent) {
    document.attachEvent("onreadystatechange", isReady);
}

function isReady() {
    if (document.readyState === "complete") {
        if (window.InitPage) {
            window.InitPage();
        }
    }
}

function doCommand(cmd, commanddata) {
    var parms = '';
    if (commanddata != '' && commanddata != undefined) {
        postData(cmd, commanddata, true, dataReturns, onerror);
    } else {
        postData(cmd, "", true, dataReturns, onerror);
    }
}

function formBind(dataset, container) {
    var inputs;
    var p;
    if (container == undefined) {
        inputs = $("[DataField]");
    }
    else {
        p = $("#" + container);
        if (p.length > 0) {
            inputs = p.find("[DataField]");
        }
    }
    for (var e = 0; e < inputs.length; e++) {
        var vf = $(inputs[e]).attr("DataValueField");
        var df = $(inputs[e]).attr("DataField");
        var v = undefined;
        if (df != "") {
            var fs = df.split(".");
            if (dataset[fs[0]] != null && dataset[fs[0]][0]) {
                if (dataset[fs[0]][0][fs[1]] != null) {
                    v = dataset[fs[0]][0][fs[1]];
                }
            }
        }
        if (vf != undefined && vf != "") {
            var vfs = vf.split(".");
            var vd = undefined;
            if (dataset[vfs[0]] != null && dataset[vfs[0]][0]) {
                if (dataset[vfs[0]][0][vfs[1]] != null) {
                    vd = dataset[vfs[0]][0][vfs[1]];
                }
            }
            if (v != undefined) {
                $(inputs[e]).combo("setText", v);
            }
            if (vd != undefined) {
                $(inputs[e]).combo("setValue", vd);
            }
        }
        else if (df != "") {
            if (v != undefined) {
                if ($(inputs[e]).attr("type") == "hidden") {
                    $(inputs[e]).val(untransferString(v));
                }
                else {
                    $(inputs[e]).textbox("setText", untransferString(v));
                }
            }
        }
    }
}

function postData(url, parms, usepost, callback, error) {
    loadingShow();
    var ptype = "GET";
    if (usepost) {
        ptype = "POST";
    }
    $.ajax({url: url, type: ptype, dataType: "json", data: parms, success: callback, error: error});
}

function dataReturns(data, proc) {
    try {
        if (data.ReturnData) {
            var rtdata = eval("(" + data.ReturnData + ")");
            formBind(rtdata);
        }
        if (data.ErrMessage) {
            $.messager.alert('消息', data.ErrMessage);
        }
        if (data.Clear) {
            var fs = data.Clear.split(",");
            for (var i = 0; i < fs.length; i++) {
                clearData(fs[i]);
            }
        }
        if (data.Exec) {
            eval(data.Exec);
        }
    }
    catch (e) {
        $.messager.alert('消息', e);
    }
    loadingHide();
}

function formToData(container) {
    var datas = new Array();
    var inputs;
    var comboxs;
    var p;
    if (container == undefined) {
        inputs = $("[DataField]");
        comboxs = $("[DataValueField]");
    }
    else {
        p = $("#" + container);
        if (p.length > 0) {
            inputs = p.find("[DataField]");
            comboxs = p.find("[DataValueField]");
        }
    }
    for (var e = 0; e < inputs.length; e++) {
        var vf = $(inputs[e]).attr("DataValueField");
        var df = $(inputs[e]).attr("DataField");
        var td = undefined;
        var vd = undefined;
        if (vf != undefined && vf != "") {
            var vfs = vf.split(".");
            td = $(inputs[e]).combo("getText");
        }
        else if (df != "") {
            if ($(inputs[e]).attr("type") == "hidden") {
                td = $(inputs[e]).val();
            }
            else {
                td = $(inputs[e]).textbox("getText");
            }
            td = transferString(td);
        }
        if (td != undefined) {
            var fs = df.split(".");
            if (datas[fs[0]] == null) {
                datas[fs[0]] = new Array();
                datas[fs[0]].push([]);
            }
            var row = datas[fs[0]][0];
            row[fs[1]] = "\"" + fs[1] + "\":\"" + td + "\"";
        }
    }
    for (var e = 0; e < comboxs.length; e++) {
        var vf = $(comboxs[e]).attr("DataValueField");
        var vd = undefined;
        if (vf != undefined && vf != "") {
            var vfs = vf.split(".");
            vd = $(comboxs[e]).combo("getValue");
        }
        if (vd != undefined) {
            var fs = vf.split(".");
            if (datas[fs[0]] == null) {
                datas[fs[0]] = new Array();
                datas[fs[0]].push([]);
            }
            var row = datas[fs[0]][0];
            row[fs[1]] = "\"" + fs[1] + "\":\"" + vd + "\"";
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
    var rt = rtdatas.join(",");
    if (rt != "") {
        return "{" + rt + "}";
    }
    else {
        return "";
    }
}

function clearData(dataname) {
    var inputs;
    var p;
    if (container == undefined) {
        inputs = $("[DataField*=" + dataname + ".]");
    }
    else {
        p = $("#" + container);
        if (p.length > 0) {
            inputs = p.find("[DataField*=" + dataname + ".]");
        }
    }
    for (var e = 0; e < inputs.length; e++) {
        var vf = $(inputs[e]).attr("DataValueField");
        var df = $(inputs[e]).attr("DataField");
        if (vf != undefined && vf != "") {
            $(inputs[e]).combo("setText", "");
            $(inputs[e]).combo("setValue", "");
        }
        else if (df != "") {
            if ($(inputs[e]).attr("type") == "hidden") {
                $(inputs[e]).val("");
            }
            else {
                $(inputs[e]).textbox("setText", "");
            }
        }
    }
}

function q(urlPara, requestName) {
    var parm = (requestName) ? requestName : urlPara;
    var url = (requestName) ? urlPara : location;
    var reg = new RegExp("[&|?]" + parm + "=([^&$]*)", "gi");
    var a = reg.test(url);
    return a ? RegExp.$1 : "";
}

function qd(field) {
    var inputs = $("[DataField='" + field + "']");
    var comboxs = $("[DataValueField='" + field + "']");
    var vs = new Array();
    if (inputs.length > 0) {
        for (var e = 0; e < inputs.length; e++) {
            var vf = $(inputs[e]).attr("DataValueField");
            if (vf != undefined && vf != "") {
                vs.push($(inputs[e]).combo("getText"));
            }
            else {
                vs.push($(inputs[e]).textbox("getText"));
            }
        }
    }
    if (comboxs.length > 0) {
        for (var c = 0; c < comboxs.length; c++) {
            vs.push($(comboxs[c]).combo("getValue"));
        }
    }
    return vs.join(",");
}

function sd(field, value) {
    if (field) {
        var inputs = $("[DataField='" + field + "']");
        var comboxs = $("[DataValueField='" + field + "']");
        if (inputs.length > 0) {
            for (var e = 0; e < inputs.length; e++) {
                var vf = $(inputs[e]).attr("DataValueField");
                if (vf != undefined && vf != "") {
                    $(inputs[e]).combo("setText", value);
                }
                else {
                    $(inputs[e]).textbox("setText", value);
                }
            }
        }
        if (comboxs.length > 0) {
            for (var c = 0; c < comboxs.length; c++) {
                $(comboxs[c]).combo("setValue", value);
            }
        }
    }
}

function onerror(s) {
    if (s != "") {
        $.messager.alert('消息', s);
    }
    else {
        $.messager.alert('消息', "数据为空!");
    }
    loadingHide();
}

//替换所有的回车换行  
function transferString(content) {
    var string = content;
    try {
        string = string.replace(/\r\n/g, "<BR>");
        string = string.replace(/\n/g, "<BR>");
        string = string.replace(/<BR>/g, "\\r\\n");
    } catch (e) {
        $.messager.alert('错误', e.message);
    }
    return string;
}

//替换所有的回车换行
function untransferString(content) {
    var string = content;
    try {
        string = string.replace(/\\n/g, "\n");
        string = string.replace(/\\r/g, "\r");
    } catch (e) {
    	$.messager.alert('错误', e.message);
    }
    return string;
}

/**********************原：commons-tools.js begin**************************/
(function (window) {
    var document = window.document,
        location = window.location,
        navigator = window.navigator,
        // Map over jQuery in case of overwrite
        _jQuery = window.jQuery,
        // Map over the $ in case of overwrite
        _$ = window.$;
    var toolsFn = {
        hereDoc: function (doc, ref) {
            /*
             * alert(hodc.toString()) alert(hodc.toString().split("/*"))
             * alert(hodc.toString().split("/*")[1])
             */
            return doc.toString().split("/*")[1].split("*/")[0].replace(
                /\$\{([^}]+)\}/g, function (outer, inner, pos) {
                    return dynAttrValue(ref, inner);
                })
        },
        dynAttrValue: function (variable, key) {
            var attrs = key.split("\.");
            for (var i = 0; i < attrs.length; i++) {
                if (i == 0) {
                    value = variable[attrs[0]]
                } else {
                    value = value[attrs[i]]
                }
            }
            return value;
        },
        arrayContains: function (array, obj) {
            var i = a.length;
            while (i--) {
                if (array[i] === obj) {
                    return true;
                }
            }
            return false;
        },
        /*
         * 模拟 form post 做 js post 提交
         */
        formPost: function (URL, PARAMS, form_target) {
            var form = document.createElement("form");
            form.action = URL;
            form.method = "post";
            form.style.display = "none";
            if (form_target) {
                form.target = form_target;
            }
            for (var x in PARAMS) {
                var opt = document.createElement("input");
                opt.name = x;
                opt.value = PARAMS[x];
                // alert(opt.name)
                form.appendChild(opt);
            }
            document.body.appendChild(form);
            form.submit();
            return temp;
        },

        /*
         * 获得 url 上的参数 封装成 json对象
         */
        getUrlParams: function () {
            var locationurl = location.href;
            var tem0 = locationurl.split("#");
            var tem = tem0[0].split('?');
            var paramsdata = {};
            if (tem.length > 1) {
                var parms = tem[1].split('&');
                for (var i = 0; i < parms.length; i++) {
                    var param = parms[i].split('=');
                    paramsdata[param[0]] = param[1];
                }
            }

            return paramsdata;

        },

        /*
         * 值转换
         */
        valueMapping: function (value, mapping) {
            var res = "";
            if (value) {
                res = mapping[value];
            }
            return res;
        },

        /*
         * 去除 undfine null
         */
        fitValue: function (value) {
            if (value) {

            } else {
                value = "";
            }
            return value;
        },

        getformElementParams: function (container) {
            var param = {};
            var inputs = container.find('input');
            var selects = container.find('select');
            var textareas = container.find('textarea');
            for (var i in inputs) {
                if (!isNaN(i)) {
                    var input = inputs[i];
                    if (input.value && input.value !== "") {
                        param[input.name] = input.value;
                    }
                }
            }
            for (var i in textareas) {
                if (!isNaN(i)) {
                    var textarea = textareas[i];
                    if (textarea.value && textarea.value !== "") {
                        param[textarea.name] = textarea.value;
                    }
                }
            }
            for (var i in selects) {
                if (!isNaN(i)) {
                    var select = selects[i];
                    var value = $(select).val();
                    if (value && value !== "") {
                        param[select.name] = value;
                    }
                }
            }
            return param;
        },
        setformElementParams: function test(container, param) {
            var inputs = container.find('input');
            var selects = container.find('select');
            var textareas = container.find('textarea');
            for (var i in inputs) {
                if (!isNaN(i)) {
                    var input = inputs[i];
                    var name = input.name;
                    if (name && name !== "" && param[name] != undefined) {
                        input.value = param[name];
                    }
                }
            }
            for (var i in textareas) {
                if (!isNaN(i)) {
                    var textarea = textareas[i];
                    var name = textarea.name;
                    if (name && name !== "") {
                        textarea.value = param[name];
                    }
                }
            }
            for (var i in selects) {
                if (!isNaN(i)) {
                    var select = selects[i];
                    var name = select.name
                    var value = $(select).val();
                    if (name && name !== "") {
                        $(select).val(param[name]);
                    }
                }
            }
        },
        pagedownReady: function (aLink, fileName) {
            var content = document.getElementsByTagName("html")[0].outerHTML;
            if (navigator.userAgent.indexOf('Firefox') >= 0) {
                var blob = new Blob([content]);
                var evt = document.createEvent("HTMLEvents");
                evt.initEvent("click", false, false);

                aLink.download = fileName;
                aLink.href = URL.createObjectURL(blob);
                aLink.dispatchEvent(evt);
                document.getElementById("a").click();
            }
            else if ((navigator.userAgent.indexOf('MSIE') >= 0)
                && (navigator.userAgent.indexOf('Opera') < 0)
                || (!!window.ActiveXObject || "ActiveXObject" in window)
                || (navigator.userAgent.indexOf('Edge') >= 0)) {

            } else {
                var blob = new Blob([content]);
                var evt = document.createEvent("HTMLEvents");
                evt.initEvent("click", false, false);
                aLink.download = fileName;
                aLink.href = URL.createObjectURL(blob);
                aLink.dispatchEvent(evt);
                // document.getElementById("a").click();
            }
        }
        ,
        pagedownIE: function () {
            if ((navigator.userAgent.indexOf('MSIE') >= 0)
                && (navigator.userAgent.indexOf('Opera') < 0)
                || (!!window.ActiveXObject || "ActiveXObject" in window)
                || (navigator.userAgent.indexOf('Edge') >= 0)) {
                document.execCommand('Saveas', false, 'c:\\' + fileName);
            }
        },
        maskerOut: function () {
            document.getElementById('maskerloading').style.display = 'none';
        }
        ,
        maskerLoading: function () {
            document.getElementById('maskerloading').style.display = 'block';
        },
        masker: function () {
            var masker = hereDoc(function () {
                /*
                 *
                 * <div id='maskerloading' class='Loading'
                 * style='display: none'> <img
                 * src='resources/images/loading.gif'> </div>
                 *
                 * <style>
                 * .Loading{width:100%;height:100%;overflow:hidden;position:fixed;left:0;
                 * top:0;right: 0;bottom:0;z-index:99999;filter:
                 * alpha(opacity=60);background-color:rgba(0,0,0,0.6)}
                 * .Loading img{width: 50px; height: 50px; position:
                 * fixed;left:50%;top:50%;margin: -25px 0 0 -25px}
                 * </style>
                 *
                 */
            });
            var jqmasker = $(masker);
            $("body").append(jqmasker);
        }
    };
    /*
	 * !important 构造方法传字符串 中国式身份证处理对象
	 */
    window.Idcard = function (idNo, local) {
        this.idNo = idNo;
        if (local == "zh_CN" || local == "CN" || local == "zh" || local == undefined) {
            this.getBirthday = function () {
                var birthdayno, birthdaytemp;
                if (this.idNo.length == 18) {
                    birthdayno = this.idNo.substring(6, 14);
                } else if (this.idNo.length == 15) {
                    birthdaytemp = psidno.substring(6, 12);
                    birthdayno = "19" + birthdaytemp;
                } else {
                    return false;
                }


                var birthday = new Date();

                birthday.setFullYear(birthdayno.substring(0, 4), birthdayno.substring(4, 6) - 1, birthdayno.substring(6, 8));

                return birthday;
            };
            this.getSex = function () {
                var sexno, sex;
                if (this.idNo.length == 18) {
                    sexno = this.idNo.substring(16, 17);
                } else if (this.idNo.length == 15) {
                    sexno = this.idNo.substring(14, 15);
                } else {
                    alert("错误的身份证号码，请核对！");
                    return false;
                }
                var tempid = sexno % 2;
                if (tempid == 0) {
                    sex = 'F';
                } else {
                    sex = 'M';
                }
                return sex;
            };
            this.getProvinceNameByIdNo = function () {
                var area = {
                    11: "北京", 12: "天津", 13: "河北", 14: "山西", 15: "内蒙古", 21: "辽宁",
                    22: "吉林", 23: "黑龙江", 31: "上海", 32: "江苏", 33: "浙江", 34: "安徽",
                    35: "福建", 36: "江西", 37: "山东", 41: "河南", 42: "湖北", 43: "湖南",
                    44: "广东", 45: "广西", 46: "海南", 50: "重庆", 51: "四川", 52: "贵州",
                    53: "云南", 54: "西藏", 61: "陕西", 62: "甘肃", 63: "青海",
                    64: "宁夏", 65: "新疆", 71: "台湾", 81: "香港", 82: "澳门", 91: "国外"
                };

                var provinceName = "";
                var provinceNo = this.idNo.substr(0, 2);
                if (area[parseInt(provinceNo)] != null) {
                    provinceName = area[parseInt(provinceNo)];
                }
                return provinceName;
            };
            this.idCheck = function () {
                var idPat = new RegExp(/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/);

                if (!idPat.test(this.idNo)) {
                    alert("请输入正确的身份证号");
                    return false;
                }
                return true;
            };
        }
        ;
    };

    Date.prototype.format = function (fmt) {
        var o = {
            "M+": this.getMonth() + 1, // 月份
            "d+": this.getDate(), // 日
            "h+": this.getHours(), // 小时
            "m+": this.getMinutes(), // 分
            "s+": this.getSeconds(), // 秒
            "q+": Math.floor((this.getMonth() + 3) / 3), // 季度
            "S": this.getMilliseconds() // 毫秒
        };
        if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
        for (var k in o)
            if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
        return fmt;
    };

    if (_jQuery != undefined && _$ != undefined && _jQuery == _$) {
        $.extend(toolsFn);
    } else {
        window.tF = toolsFn;
    }
}(window));


function hereDoc(doc, ref) {
    return doc.toString().split("/*")[1].split("*/")[0].replace(
        /\$\{([^}]+)\}/g, function (outer, inner, pos) {
            return dynAttrValue(ref, inner);
        })
}

function dynAttrValue(variable, key) {
    var attrs = key.split("\.");
    for (var i = 0; i < attrs.length; i++) {
        if (i == 0) {
            value = variable[attrs[0]]
        } else {
            value = value[attrs[i]]
        }
    }
    return value;
}

var indexSet = {
    indexcode: [],
    toString: function () {
        return this.indexcode.toString();
    },
    put: function (index) {
        this.indexcode.push(index);
    },
    del: function (index) {
        for (var i = 0; i < this.indexcode.length; i++) {
            if (this.indexcode[i] == index) {
                if (i == this.indexcode.length - 1) {
                    this.indexcode.pop();
                } else {
                    var tem = this.indexcode[i];
                    this.indexcode[i] = this.indexcode[this.indexcode.length - 1];
                    this.indexcode[this.indexcode.length - 1] = tem;
                    this.indexcode.pop();
                }
            }
        }
    },
    contains: function (index) {
        for (var i = 0; i < this.indexcode.length; i++) {
            if (this.indexcode[i] == index) {
                return true;
            }
        }
        return false;
    }
};

/**********************原：commons-tools.js end**************************/

/*********原public.js Begin************/
function getRequestName(urlPara, requestName) {
    var parm = (requestName) ? requestName : urlPara;
    var url = (requestName) ? urlPara : location;
    var reg = new RegExp("[&|?]" + parm + "=([^&$]*)", "gi");
    var a = reg.test(url);
    return a ? RegExp.$1 : "";
}

function createFrame(Url) {
    var s = '<iframe name="ifrmname2" scrolling="auto" frameborder="0"  src="' + Url + '" style="width:100%;height:99%;"></iframe>';
    return s;
}

function height(ID, type) {
    var h = document.documentElement.clientHeight;
    var h1 = $(ID).height();
    if (type == "tab") {
        var h3 = h - h1 - 60;
    }
    else {
        var h3 = h - h1 - 10;
    }
    return h3;
}

function select2SetValue(ID, URL, TEXT) {
    $(ID).select2({
        ajax: {
            url: URL,
            dataType: 'json',
            delay: 250,
            data: function (params) {
                return {
                    q: encodeURIComponent(params.term == undefined ? "" : params.term, "utf-8"), // search term
                    page: params.page
                };
            },
            processResults: function (data, params) {
                params.page = params.page || 1;
                return {
                    results: data.rows,
                    pagination: {
                        more: (params.page * 10) < data.total
                    }
                };
            },
            cache: true
        },
        placeholder: TEXT,
        allowClear: true,
        language: "zh-CN",
        escapeMarkup: function (markup) {
            return markup;
        }, // let our custom formatter work
        minimumInputLength: 0,
        templateResult: formatRepo, // omitted for brevity, see the source of this page
        templateSelection: formatRepoSelection // omitted for brevity, see the source of this page
    });

    function formatRepoSelection(repo) {
        var value = repo.text.split("-");
        if (value.length > 1) {
            return repo.text.split("-")[1];
        }
        return repo.text;
    }

    function formatRepo(repo) {
        return repo.text;
    }
}

function getSelect2Indx(ID) {
    var INDX = $(ID).val();
    if ($(ID).select2('data').length <= 0) {
        INDX = "";
    }
    return INDX;
}

function getSelect2Text(ID) {
    var Text = "";
    if ($(ID).select2('data').length > 0) {
        Text = $(ID).select2('data')[0].text;
        var value = Text.split("-");
        if (value.length > 1) {
            Text = Text.split("-")[1];
        }
    }
    return Text;
}

function popShip(Url, Title, Width, Height, obj) {
    Url = obj.location.href.substr(0, obj.location.href.lastIndexOf('/') + 1) + Url;
    $('#Pop_Ship').removeData()
    $('#Pop_Ship').window({
        title: Title,
        width: Width,
        modal: true,
        shadow: true,
        closed: true,
        height: Height,
        content: createFrame(Url),
        draggable: true,
        resizable: false,
        minimizable: false,
        maximizable: false,
        collapsible: false
    });
    $('#Pop_Ship').window('open');
    $('#Pop_Ship').show();
}

function requestTab(Id, Str) {
    $('#tab_main').tabs('getSelected');
    var refresh_tab = $('#tab_main').tabs('getSelected');
    if (refresh_tab && refresh_tab.find('iframe').length > 0) {
        refresh_tab.find('iframe')[0].contentWindow.$('#' + Id).textbox('setValue', Str);
    }
}

function requestTab(Id, Str, Indx) {
    $('#tab_main').tabs('getSelected');
    var refresh_tab = $('#tab_main').tabs('getSelected');
    if (refresh_tab && refresh_tab.find('iframe').length > 0) {
        refresh_tab.find('iframe')[0].contentWindow.$('#' + Id).textbox('setValue', Str);
        refresh_tab.find('iframe')[0].contentWindow.$('#' + Id).attr("data-indx", Indx);
    }
}

function requesttree(Id) {
    var refresh_tab = $('#tab_main').tabs('getSelected');
    if (refresh_tab && refresh_tab.find('iframe').length > 0) {
        refresh_tab.find('iframe')[0].contentWindow.$('#' + Id).treegrid('reload');
    }
}

function requestTabrow(rowIndx, FieldName, Id, Str) {
    $('#tab_main').tabs('getSelected');
    var refresh_tab = $('#tab_main').tabs('getSelected');
    if (refresh_tab && refresh_tab.find('iframe').length > 0) {
        refresh_tab.find('iframe')[0].contentWindow.$('#' + Id).datagrid('getEditor', {
            index: rowIndx,
            field: FieldName
        }).target.textbox('setValue', Str);
        //refresh_tab.find('iframe')[0].contentWindow.$('#'+Id).datagrid('updateRow',{index:rowIndx,row:{FieldName:Str}});
    }
}

function close() {
    $('#Pop_Ship').window('close');
}

function replacestr(Value) {
    if ($.trim(Value) == "" || Value == null) {
        return Value;
    } else {
        var reg = new RegExp("'", "g");
        return Value.toString().replace(reg, "‘");
    }

}

//生成保存时需要的数组 arr数组 TableField关键字 Value参数
function push(arr, TableField, Value) {
    if ($.trim(Value) == "" || Value == null) {
        arr.push(TableField + ":" + "''");
    }
    else {
        var value = replacestr(Value);
        arr.push(TableField + ":'" + value + "'");
    }
}

function dispathServerAjaxPost(url, serviceJson, afterPost, async) {
    //默认为异步
    if (!async)
        async = false;
    else
        async = true;
    $.ajax(
        {
            type: "POST",
            url: url,
            data: serviceJson,
            contentType: "application/x-www-form-urlencoded;charset=utf-8",
            cache: false,
            dataType: 'text',
            async: async,
            success: function (data) {
                if (afterPost instanceof Function)
                    afterPost(data);
            },
            error: function (xhr, data) {
                ShowMessages(data, "友情提示", "msgwaring", null, 0);
                return false;
            }
        });
}

//POP框里的POP框
function popShipById(Url, Title, Width, Height, obj, Id) {
    Url = obj.location.href.substr(0, obj.location.href.lastIndexOf('/') + 1) + Url;
    $('#' + Id).removeData()
    $('#' + Id).window({
        title: Title,
        width: Width,
        modal: true,
        shadow: true,
        closed: true,
        height: Height,
        content: createFrame(Url),
        draggable: true,
        resizable: false,
        minimizable: false,
        maximizable: false,
        collapsible: false
    });
    $('#' + Id).window('open');
    $('#' + Id).show();
}

function closeByID(id) {
    $('#' + id).window('close');
}

function requestTabByWindows(Id, Str) {
    $('#Pop_Ship').find('iframe')[0].contentWindow.$('#' + Id).textbox('setValue', Str);
}

function requestTabByIndx(Id, Str, Indx) {
    $('#Pop_Ship').find('iframe')[0].contentWindow.$('#' + Id).textbox('setValue', Str);
    $('#Pop_Ship').find('iframe')[0].contentWindow.$('#' + Id).attr("data-indx", Indx);
}

function dgReload(TabName, DgName) {
    window.parent.$('#tab_main').tabs('getTab', TabName).find('iframe')[0].contentWindow.$('#' + DgName).datagrid('reload');
}

function replaceAll() {
    String.prototype.replaceAll = function (reallyDo, replaceWith, ignoreCase) {
        if (!RegExp.prototype.isPrototypeOf(reallyDo)) {
            return this.replace(new RegExp(reallyDo, (ignoreCase ? "gi" : "g")), replaceWith);
        } else {
            return this.replace(reallyDo, replaceWith);
        }
    }
}

//分页
function pagination(id, size) {
    var p = $("#" + id).datagrid('getPager');
    $(p).pagination({
        pageSize: size,
        pageList: [5, 10, 15],
        beforePageText: '第',
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示ʾ {from} - {to} 条记录   共 {total} 条记录'
    });
}

//隐藏查询
//hideclass为隐藏div的id，btnid为点击按钮的id，datagrid为重新自适应的id
function hideByClass(hideclass, btnid, datagrid) {
    $("." + hideclass).hide();
    $("#" + btnid).click(function () {
        if ($("." + hideclass).is(':hidden')) {
            $("." + hideclass).show();
            $("#" + btnid).linkbutton({iconCls: 'icon-arrow_in_longer', text: '收起'});

        }
        else {
            $("." + hideclass).hide();
            $("#" + btnid).linkbutton({iconCls: 'icon-arrow_out_longer', text: '更多...'});
        }
        $("#" + datagrid).resize();
    });
}

//合并重复单元格
//参数 tableID 要合并table的id
//参数 colList 要合并的列,用逗号分隔(例如："name,department,office");
function mergeCellsByField(tableID, colList) {
    var ColArray = colList.split(",");
    var tTable = $("#" + tableID);
    var TableRowCnts = tTable.datagrid("getRows").length;
    var tmpA;
    var tmpB;
    var PerTxt = "";
    var CurTxt = "";
    var alertStr = "";
    for (j = ColArray.length - 1; j >= 0; j--) {
        PerTxt = "";
        tmpA = 1;
        tmpB = 0;

        for (i = 0; i <= TableRowCnts; i++) {
            if (i == TableRowCnts) {
                CurTxt = "";
            }
            else {
                CurTxt = tTable.datagrid("getRows")[i][ColArray[j]];
            }
            if (PerTxt == CurTxt) {
                tmpA += 1;
            }
            else {
                tmpB += tmpA;

                tTable.datagrid("mergeCells", {
                    index: i - tmpA,
                    field: ColArray[j],//合并字段
                    rowspan: tmpA,
                    colspan: null
                });
                tTable.datagrid("mergeCells", { //根据ColArray[j]进行合并
                    index: i - tmpA,
                    field: colList,
                    rowspan: tmpA,
                    colspan: null
                });

                tmpA = 1;
            }
            PerTxt = CurTxt;
        }
    }
}

function enterToSearch(btnID) {
    $(document).keydown(function (e) {
        if (!e)
            e = window.event;
        if (document.getElementById(btnID)) {
            if ((e.keyCode || e.which) == 13) {

                $("#" + btnID).click();
            }
        } else {
            return
        }

    });

}

function accMul(arg1, arg2) {
    if (arg1 != null && arg2 != null) {
        var m = 0, s1 = arg1.toString(), s2 = arg2.toString();
        try {
            m += s1.split(".")[1].length
        } catch (e) {
        }
        try {
            m += s2.split(".")[1].length
        } catch (e) {
        }
        return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
    }
    return 0;
}

function forDight(Dight, How) {
    //必须是数字或浮点数。如3.56 、 789
    //1：先将小数向右移动How位。
    //2：将移动后结果四舍五入。
    //3：先将小数向左移动How位。
    Dight = Math.round(accMul(Dight, Math.pow(10, How))) / Math.pow(10, How);
    return Dight;
}

/*********原public.js End************/
//判断为为空
function isEmpty(str) {
    return isNotEmpty(str);
}

//判断为不为空
function isNotEmpty(str) {
    if (str != "" && str != null && str != "undefined" && str != undefined) {
        return true;
    }
    return false;
}

//列表数据绑定
function gridDataBind(container, commandName) {
    var p = $("#" + container);
    p.datagrid({
        type: "GET",
        url: commandName,
        dataType: "json",
        fit: true,
        striped: true,
        fitColumns: false,
        rownumbers: true,
        remoteSort: true,
        pagination: true,
        toolbar: "#toolbar",
        collapsible: true,
        pageSize: 10,
        pageList: [10, 50, 100],
        columns: getColumn,
        loadMsg: 'LOADING...',
        showHeader: true,
        showFooter: true
    });
    var gp = p.datagrid('getPager');
    $(gp).pagination({
        beforePageText: '第',
        afterPageText: '页    共 {pages} 页',
        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录'
    });
}

//表单数据绑定
function formDataBind(container, commandName, param) {
    var tempValue = getRequestName(param);
    if (isNotEmpty(tempValue)) {
        $.ajax({
            url: commandName + '?' + param + '=' + tempValue,
            dataType: "json",
            success: function (data) {
                formBind(data);
            }
        });
    }
}

//下拉数据绑定
function comboboxBind(container, commandName, valueField, textField) {
    $("#" + container).combobox({
        method: 'get',
        url: commandName,
        valueField: valueField,
        textField: textField
    });
}

//编辑页面数据绑定
function editDataBind(container, parentPoint, SelectID, param) {
	var tempValue = getRequestName(param);
	var p_url = parentPoint+SelectID+'?'+param+'='+tempValue;
	
    if(isNotEmpty(tempValue)) {
    	$.ajax({
			 url:p_url,
			 dataType:"json",
			 success:function(data){
				 formBind(data, container);
			 }
		  });
    }
}

//保存区域内数据数据
function saveFormData(container, commandName) {
    loadingShow();
    var param = getformElementParams($("#" + container));
    $.ajax({
        url: commandName,
        type: "POST",
        dataType: "json",
        data: param,
        success: function (result) {
            if (result.isOk) {
                $.messager.show({ // show error message
                    title: '提示',
                    msg: result.ErrMessage
                });
                $('#dg').datagrid('reload');
            } else {
                $.messager.alert('消息', result.ErrMessage);
            }
            loadingHide();
        }
    });
}

//删除列表页面数据
function deleteGridData(CommandName) {
    var row = $('#dg').datagrid('getSelections');
    if (row.length <= 0) {
        $.messager.alert("提示信息", "请您选择需删除的记录！", 'warning');
    } else {
        var indxs = "";
        for (var i = 0; i < row.length; i++) {
            if (indxs == null || indxs.length == 0) {
                indxs = row[i].indx;
            } else {
                indxs = indxs + "," + row[i].indx;
            }
        }

        $.messager.confirm('提示', '确定要删除选中记录吗?', function (r) {
            if (r) {
                $.post(CommandName, {indxs: indxs}, function (result) {
                    if (result.IsOk == '1' || result.IsOk == 'true') {
                        $.messager.show({title: '提示', msg: result.ErrMessage});
                    } else {
                        $.messager.alert({title: '提示', msg: result.ErrMessage});
                    }
                    $('#dg').datagrid('reload');
                }, 'json');
            }
        });
    }
}

//弹出页面Url, Title, Width, Height, obj
function popWindow(container, Url, Title, Width, Height) {
    Url = this.location.href.substr(0, this.location.href.lastIndexOf('/') + 1) + Url;
    $('#' + container + '').removeData()
    $('#' + container + '').window({
        title: Title,
        width: Width,
        modal: true,
        shadow: true,
        closed: true,
        height: Height,
        content: createFrame(Url),
        draggable: true,
        resizable: false,
        minimizable: false,
        maximizable: false,
        collapsible: false
    });
    $('#' + container + '').window('open');
    $('#' + container + '').show();
}

//获取查询区域参数，返回数组
function getSearchParams(container) {
    var param = {};
    var inputs = $('#' + container + '').find('input');
    var selects = $('#' + container + '').find('select');
    var textareas = $('#' + container + '').find('textarea');
    var i;
    for (i in inputs) {
        if (!isNaN(i)) {
            var input = inputs[i];
            var inputId = input.id;
            if (inputId !== "" && inputId.substr(0, 4) == "txt_") {
                inputId = input.id.substring(4);
            }
            if (input.className !== "" && input.className.substr(0, 15) == "easyui-combobox") {
                var inputText = inputs[++i];
                var inputValue = inputs[++i];
                if (inputText.value !== "") {
                    param[inputId + "_N"] = inputText.value;
                }
                if (inputValue.value !== "") {
                    param[inputId + "_V"] = inputValue.value;
                }
            } else if (input.className !== "" && input.className.substr(0, 14) == "easyui-textbox") {
                if (input.value && input.value !== "") {
                    if (input.name == "") {
                        param[inputId] = inputs[++i].value;
                    } else {
                        param[input.name] = input.value;
                    }
                }
            } else if (input.className !== "" && input.className.substr(0, 14) == "easyui-datebox") {
                var inputText = inputs[++i];
                if (inputText.value !== "") {
                    param[inputId] = inputText.value;
                }
            }
        }
    }
    for (var i in textareas) {
        if (!isNaN(i)) {
            var textarea = textareas[i];
            if (textarea.value && textarea.value !== "") {
                param[textarea.name] = textarea.value;
            }
        }
    }
    for (var i in selects) {
        if (!isNaN(i)) {
            var select = selects[i];
            var value = $(select).val();
            if (value && value !== "") {
                param[select.name] = value;
            }
        }
    }
    return param;
}

//获取容器区域参数，返回数组
function getformElementParams(container) {
    var param = {};
    var inputs = container.find('input');
    var selects = container.find('select');
    var textareas = container.find('textarea');
    for (var i in inputs) {
        if (!isNaN(i)) {
            var input = inputs[i];
            if (input.value && input.value !== "") {
                param[input.name] = input.value;
            }
        }
    }
    for (var i in textareas) {
        if (!isNaN(i)) {
            var textarea = textareas[i];
            if (textarea.value && textarea.value !== "") {
                param[textarea.name] = textarea.value;
            }
        }
    }
    for (var i in selects) {
        if (!isNaN(i)) {
            var select = selects[i];
            var value = $(select).val();
            if (value && value !== "") {
                param[select.name] = value;
            }
        }
    }
    return param;
}

function geteditElementParams (container) {
    var param = {};
    var inputs = container.find('input');
    var selects = container.find('select');
    var textareas = container.find('textarea');
    for (var i in inputs) {
        if (!isNaN(i)) {
            var input = inputs[i];
            param[input.name] = input.value;
        }
    }
    for (var i in textareas) {
        if (!isNaN(i)) {
            var textarea = textareas[i];
            if (textarea.value && textarea.value !== "") {
                param[textarea.name] = textarea.value;
            }
        }
    }
    for (var i in selects) {
        if (!isNaN(i)) {
            var select = selects[i];
            var value = $(select).val();
            if (value && value !== "") {
                param[select.name] = value;
            }
        }
    }
    return param;
}

// 选中行和取消选中行样式方法
function checkOrUnCheck(index, row) {
    var opt = $(this).datagrid("options");
    var rows1 = opt.finder.getTr(this, "", "allbody", 2);
    if (rows1.length > 0) {
        $(rows1).each(function () {
            var tempIndex = parseInt($(this).attr("datagrid-row-index"));
            if (index == tempIndex) {
                if ($(this).css("background-color") == 'rgb(248, 166, 37)') {
                    //当前点击的行已经是黄色的了再次点击恢复颜色
                    if (tempIndex % 2 == 0) {
                        $(this).css('background-color', '#CDCDCD');
                        num = null;
                    } else {
                        $(this).css('background-color', '#FFFFFF');
                        num = null;
                    }
                } else {
                    $(this).css('background-color', '#f8a625');
                }
            }
        })
    }
}


/**
 * 必须在此js之前引入 jquery.serializejson.min.js
 * @param formSelector
 * @returns {*|jQuery}
 */
jQuery["serialize2JSON"] = function (formSelector) {
    return $(formSelector).serializeJSON({
        checkboxUncheckedValue: 0,
        parseAll: true,
        parseWithFunction: function (val, inputName) {
            if (val === "") return null;
            // if (val === 0) return null;
            return val;
        }
    });
};


/*****************************************
 * var map = new Map();  
	map.put("a",5);  
	map.put("b",10);  
	map.put("c",15);  
	map.put("d",20);  
	var sum = 0;  
	if(map.size()>0){  
	  map.each(function(e_key, e_value, e_i){  
	  sum = sum + e_value;  
	 });  
	}  
 * 
 *****************************************/
function Map() {       
    /** 存放键的数组(遍历用到) */      
    this.keys = new Array();       
    /** 存放数据 */      
    this.data = new Object();       
           
    /**     
     * 放入一个键值对     
     * @param {String} key     
     * @param {Object} value     
     */      
    this.put = function(key, value) {       
        if(this.data[key] == null){       
            this.keys.push(key);       
        }       
        this.data[key] = value;       
    };       
           
    /**     
     * 获取某键对应的值     
     * @param {String} key     
     * @return {Object} value     
     */      
    this.get = function(key) {       
        return this.data[key];       
    };       
           
    /**     
     * 删除一个键值对     
     * @param {String} key     
     */      
    this.remove = function(key) {       
        this.keys.remove(key);       
        this.data[key] = null;       
    };       
           
    /**     
     * 遍历Map,执行处理函数     
     *      
     * @param {Function} 回调函数 function(key,value,index){..}     
     */      
    this.each = function(fn){       
        if(typeof fn != 'function'){       
            return;       
        }       
        var len = this.keys.length;       
        for(var i=0;i<len;i++){       
            var k = this.keys[i];       
            fn(k,this.data[k],i);       
        }       
    };       
           
    /**     
     * 获取键值数组(类似Java的entrySet())     
     * @return 键值对象{key,value}的数组     
     */      
    this.entrys = function() {       
        var len = this.keys.length;       
        var entrys = new Array(len);       
        for (var i = 0; i < len; i++) {       
            entrys[i] = {       
                key : this.keys[i],       
                value : this.data[i]       
            };       
        }       
        return entrys;       
    };       
           
    /**     
     * 判断Map是否为空     
     */      
    this.isEmpty = function() {       
        return this.keys.length == 0;       
    };       
           
    /**     
     * 获取键值对数量     
     */      
    this.size = function(){       
        return this.keys.length;       
    };       
           
    /**     
     * 重写toString      
     */      
    this.toString = function(){       
        var s = "{";       
        for(var i=0;i<this.keys.length;i++,s+=','){       
            var k = this.keys[i];       
            s += k+"="+this.data[k];       
        }       
        s+="}";       
        return s;       
    };       
}
function loadEasyuiLang() {
    var locale = getCookie("locale");
    if ("zh" == locale) {
        jQuery.getScript("../../resources/js/easyui1.5.5.4/locale/locale/easyui-lang-zh_CN.js", function (data, status, jqxhr) {
            loadGrid();
        });
    } else {
        jQuery.getScript("../../resources/js/easyui1.5.5.4/locale/easyui-lang-ru.js", function (data, status, jqxhr) {
            loadGrid();
        });
    }
}

/*******************************base-loading.js*****************************************/
function loadingShow() {
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;

    var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
        _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;

    var Loadimagerul = "../../resources/js/easyui1.5.5.4/themes/default/images/loading.gif";

    var _Loadingdiv = document.createElement('div');

    _Loadingdiv.innerHTML = '<div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width:100px;; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #E1EED2 url(' + Loadimagerul + ') no-repeat scroll 30px 20px; border: 2px solid #123555; color: #D1484D; font-weight: bold; font-family:\'Microsoft YaHei\';">LOADING...</div>';
    _Loadingdiv.setAttribute("style", "position:absolute;left:0;width:100%;height:" + _PageHeight + "px;top:0;background:#DCD0A8;opacity:0.8;filter:alpha(opacity=80);z-index:10000;");
    _Loadingdiv.setAttribute("id", "loadingDataDiv");

    document.body.appendChild(_Loadingdiv);
}

function loadingHide() {
    var loadingMask = document.getElementById('loadingDataDiv');
    if(null != loadingMask && null != loadingMask.parentNode){
    	loadingMask.parentNode.removeChild(loadingMask);
    }
}

function initPageLoading() {
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;

    var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
        _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;

    var Loadimagerul = "../../resources/js/easyui1.5.5.4/themes/default/images/loading.gif";

    var _LoadingHtml = '<div id="loadingDiv" style="position:absolute;left:0;width:100%;height:' + _PageHeight + 'px;top:0;background:#DCD0A8;opacity:0.8;filter:alpha(opacity=80);z-index:10000;"><div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width:100px;; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #E1EED2 url(' + Loadimagerul + ') no-repeat scroll 30px 20px; border: 2px solid #123555; color: #D1484D; font-weight: bold; font-family:\'Microsoft YaHei\';">LOADING...</div></div>';

    document.write(_LoadingHtml);

    document.onreadystatechange = completeLoading;
}
function completeLoading() {
    if (document.readyState == "complete") {
        var loadingMask = document.getElementById('loadingDiv');
        loadingMask.parentNode.removeChild(loadingMask);
    }
}

//隐藏查询
//hideclass为隐藏div的id，btnid为点击按钮的id，datagrid为重新自适应的id
function hideFormElementParams(hideclass, btnid, datagrid) {
  $("." + hideclass).hide();
  $("#" + btnid).click(function () {
      if ($("." + hideclass).is(':hidden')) {
          $("." + hideclass).show();
          $("#" + btnid).linkbutton({iconCls: 'icon-arrow_in_longer', text: '收起'});

      }
      else {
          $("." + hideclass).hide();
          $("#" + btnid).linkbutton({iconCls: 'icon-arrow_out_longer', text: '更多...'});
      }
      $("#" + datagrid).resize();
  });
}
