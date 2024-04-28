//tableName  表名
//coulmnName 列名
//coulmnValue 列值
//divId div的Id
//Location 当前流程是第几个流程状态
//状态流程图
function getViewState(tableName, coulmnName, coulmnValue, divId) {
    $.ajax({
        type: "post",
        async: false, //控制同步
        url: "getState",
        data: {
            tableName: tableName,
            coulmname: coulmnName,
            coulmvalue: coulmnValue
        },
        //dataType: "json",
        cache: false,
        success: function (data) {
            var v = eval("(" + data + ")");

            if (v.IsOk == "1") {
                ;
                var returnData = eval("(" + v.ReturnData.replace(/"([^"]+)":/g, function (v) { return v.toLowerCase(); }) + ")");
                $("#" + divId + "").loadStep({
                    size: "large",
                    color: "green",
                    steps: returnData.statadata
                });
                $("#" + divId + "").setStep(returnData.statadata.length);
            }
            else
                ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
        },
        Error: function (err) {
            ShowMessages(err, "系统提示", "msgwaring", null, 0);
        }
    });
}
//UserName  用户名input的Id
//UserId    用户Id 的input的Id
//dateInput  当前日期input
function getCurrentUserName(UserName,UserId,dateInput) {
	$.ajax({
		type: "post",
		async: false, //控制同步
		url: "../../auth.GetCurrentUser",// GetCurrentUser
		//dataType: "json",
		cache: false,
		success: function (data) {
			var v = eval("(" + data + ")");

			if (v.IsOk == "1") {
				var userData = eval("(" + v.ReturnData + ")");
				///var CurrentUser=eval("(" + userData.CurrentUser + ")");
				//CurrentUser
				if(UserName!="")
				  $("#"+UserName+"").val(userData.CurrentUser[0].USERNAME);
				if(UserId!="")
				  $("#"+UserId+"").val(userData.CurrentUser[0].INDX);				 
				if(dateInput!="")
				{
				  var myDate = new Date();
				  $("#"+dateInput+"").val(myDate.getFullYear() + "-" + (myDate.getMonth() + 1) + "-" + myDate.getDate() + " " + myDate.getHours() + ":" + myDate.getMinutes() + ":" + myDate.getSeconds());
				}
			}
			else
				ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
		},
		Error: function (err) {
			ShowMessages(err, "系统提示", "msgwaring", null, 0);
		}
	});
}
//关闭右侧按钮列
//allCount  总数
//currentId  当前Id
//callback 执行完成之后要回调的function
function openCloserightBut(allCount,currentId,showCallBack,hiddenCallBack)
{
		if($("#rightTable0"+currentId).is(":hidden"))
		{
		   $("#formMid").show();
		   $("#rightTable0"+currentId).show();
		   $("#But0"+currentId).removeClass("tdiconCss").addClass("tdiSelconCss");
		   for(var i=1;i<=allCount;i++)
		   {
			   if(i!=currentId)
			   {
				 $("#rightTable0"+i).hide();
				 $("#But0"+i).removeClass("tdiSelconCss").addClass("tdiconCss");
			   }
		   }
		}
		else
		{
		   $("#But0"+currentId).removeClass("tdiSelconCss").addClass("tdiconCss");
		   $("#formMid").hide();
		   $("#rightTable0"+currentId).show();
		}
		showCallBack();
		hiddenCallBack();
}


/*将url参数回填绑定给对应table 比如 SearcheTable*/
function setTableByUrl(table) {
    var url = location.search;
    var theRequest = new Object();
    if (url.indexOf("?") != -1) {
        var str = url.substr(1);
        var strArr = new Array();
        strs = str.split("&");
        for (var i = 0; i < strs.length; i++) {
            var name = strs[i].split("=")[0];
            var val = strs[i].split("=")[1];
            strArr[i] = "\"" + name + "\":\"" + UrlDecode(val) + "\"";
        }
        var strJson = strArr.join(",");
        strJson = "{\"" + table.toUpperCase() + "\":[{" + strJson + "}]}";
        var datasource = eval("(" + strJson + ")");
        FormBind(datasource);
        return strJson;
    }
}

/*---UrlDecode-------*/
function UrlDecode(zipStr) {
    var uzipStr = "";
    for (var i = 0; i < zipStr.length; i++) {
        var chr = zipStr.charAt(i);
        if (chr == "+") {
            uzipStr += " ";
        } else if (chr == "%") {
            var asc = zipStr.substring(i + 1, i + 3);
            if (parseInt("0x" + asc) > 0x7f) {
                uzipStr += decodeURI("%" + asc.toString() + zipStr.substring(i + 3, i + 9).toString());
                i += 8;
            } else {
                uzipStr += AsciiToString(parseInt("0x" + asc));
                i += 2;
            }
        } else {
            uzipStr += chr;
        }
    }

    return uzipStr;
}

function StringToAscii(str) {
    return str.charCodeAt(0).toString(16);
}
function AsciiToString(asccode) {
    return String.fromCharCode(asccode);
}
/*---UrlDecode-------*/
//ClassId  附件类型
//Id  外键Id
//divId  图片列表要显示在什么地方
function getPicList(ClassId,Id,divId)
{
	var htmlList="<div class=\"picList\"><ul>";
	$.ajax({
		type: "post",
		async: false, //控制同步
		url: "PicList",
        data: {
            classid: ClassId,
            PIndx: Id
        },
		cache: false,
		success: function (data) {
			var v = eval("(" + data + ")");

			if (v.IsOk == "1") {
				var userData = eval("(" + v.ReturnData + ")");
				for(var i=0;i<userData.records;i++)
				{
				    htmlList += "<li><img src=\"showPic?Indx=" + userData.pic[i].INDX + "\" border=\"0\" width=\"100\" height=\"100\"/><div id=\""+userData.pic[i].INDX+"\"><input type=\"hidden\" datafield=\"picData.INDX\" value=\""+userData.pic[i].INDX+"\"/></div><br/>";
					
					htmlList +="<button class=\'btn btn-primary btn-small\' style=\'margin-bottom: 5px\' id=\"picDel\" commandname=\"delPic\"";
					htmlList +="commanddatas=\"FormToData('"+userData.pic[i].INDX+"')\" commandok=\"showDelSuccess\" ConfirmMessage=\"确定要删除图片吗？\">";
					htmlList +="<i class=\"icon-remove\"></i>删　除</button></li>";
				}
				$("#"+divId).html(htmlList+"</ul></div>");
			}
			else
				ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
		},
		Error: function (err) {
			ShowMessages(err, "系统提示", "msgwaring", null, 0);
		}
	});
}
//检查文件类型和大小  filepath  文件路径  uf 文件名  size 大小
function CheckFile(filepath, uf, Size) {
    var msg = "";
    var flag = true;
    var isnext = false;
    var filemaxsize = 1024 * Size; //M 
    var filetypes = [".jpg", ".png", 'bmp']; //报文类型
    var fileend = filepath.substring(filepath.indexOf("."));
    //判断文件是否可上传
    for (var i = 0; i < filetypes.length; i++) {
        if (filetypes[i] == fileend) {
            isnext = true;
            break;
        }
    }
    if (!isnext) {
        msg = "不接受此文件类型！";
        uf.value = "";
        flag = false;
    } else {
        var fileSize = uf.files.item(0).size; //文件名称
        var SizeTrue = fileSize / 1024;
        if (SizeTrue > filemaxsize) {
            msg = "附件大小不能大于" + filemaxsize / 1024 + "M！";
            uf.value = "";
            flag = false;
        }
    }
    if (msg != "") {
        alert(msg);
    }
    return flag;
}


//管理选项 editFunc 修改请求的类名， delFunc删除请求的类名 field根据哪个字段更新

function managelist(cellvalue, options, rowObject, field, gridId, editFunc, delFunc) {
    //加载修改 删除
    if (editFunc != null && delFunc != null) {
        return "<button class=\"btn btn-primary btn-small\" name=\"button\" type=\"button\" onClick=\"editFunction('" + options.rowId + "','" + rowObject.field + "','" + field + "','" + gridId + "','" + editFunc + "')\" id=\"save" + options.rowId + "\"><i class='icon-edit'></i>修 改</button>&nbsp;<button class=\"btn btn-danger\" name=\"button\" type=\"button\" onClick=\"delNotice('" + options.rowId + "','" + delFunc + "','" + gridId + "')\"><i class='icon-remove'></i>删 除</button>";
    }
    else if (editFunc != null && delFunc == null) {
        return "<button class=\"btn btn-primary btn-small\" name=\"button\" type=\"button\" onClick=\"editFunction('" + options.rowId + "','" + rowObject.field + "','" + field + "','" + gridId + "','" + editFunc + "')\" id=\"save" + options.rowId + "\"><i class='icon-edit'></i>修 改</button>";
    }
    else if (editFunc == null && delFunc != null) {
		if(parseInt(rowObject.ANNEX_DECL_STATUS)==1 || parseInt(rowObject.ANNEX_DECL_STATUS)==2)
          return "<button class=\"btn btn-danger\" name=\"button\" type=\"button\" onClick=\"delFiles('" + options.rowId + "','" + delFunc + "','" + gridId + "')\" disabled=\"disabled\"><i class='icon-remove'></i>删 除</button>";
		else
		  return "<button class=\"btn btn-danger\" name=\"button\" type=\"button\" onClick=\"delFiles('" + options.rowId + "','" + delFunc + "','" + gridId + "')\"><i class='icon-remove'></i>删 除</button>";    }
    else {
        return "";
    }
    if (options.rowId == "jqg1") {
        return "<button class=\"btn btn-success\" name=\"button\" type=\"button\" onClick=\"saveNotice('" + options.rowId + "','" + rowObject.INDX + "')\"><i class='icon-save'></i>保 存</button>&nbsp;<button class=\"btn btn-danger\" name=\"button\" type=\"button\" onClick=\"delNotice('" + options.rowId + "')\"><i class='icon-remove'></i>删 除</button>";
    }
}


//点了修改按钮之后的操作
function editFunction(Id, INDX, field, gridId, ClassName) {
    $("#" + gridId + "").editRow(Id, true);
    $("#save" + Id).removeClass("btn btn-primary btn-small").addClass("btn btn-success");
    $("#save" + Id).removeAttr("onclick");
    $("#save" + Id).bind("click", function () {
        saveNotice(Id, INDX, field, gridId, ClassName);
    });
    document.getElementById("save" + Id).innerHTML = "<i class='icon-save'></i>保 存";
}
//点了保存按钮之后的操作
function saveNotice(Id, INDX, field, gridId, ClassName) {
    var strJson = "";
    var retJson = "";
    var rowData = $("#" + gridId + "").jqGrid("getRowData", Id);
    var colModel = $("#" + gridId + "").jqGrid('getGridParam', 'colModel');
    for (var i = 2; i < colModel.length; i++) {
        var rowfield = colModel[i].index;
        if (rowfield == "editFunc" || rowfield == "delFunc") {

            break;
        }
        if (rowfield == "INDX")
            strJson += "{\"INDX\":\"" + $("#" + gridId + "").getCell(Id, "INDX") + "\"},";
        else
        //var s = $("#" + gridId + "").getCell(Id, rowfield);
        {
            var val = $('#' + Id + '_' + rowfield + '').val();
            if (val == undefined) {
                val = $("#" + gridId + "").getCell(Id, rowfield);
            }
            strJson += "{\"" + colModel[i].name + "\":\"" + val + "\"},";
        }
    }
    //	if (Id == "jqg1")
    //		retJson = "{\"noticeData\":[{\"CREATE_TIME\":\"" + $('#' + Id + '_CREATE_TIME').val() + "\"}]}";
    //	else
    //		retJson = "{\"noticeData\":[{\"INDX\":\"" + rowData.INDX + "\",\"Title\":\"" + $('#' + Id + '_TITLE').val() + "\",\"CREATE_TIME\":\"" + $('#' + Id + '_CREATE_TIME').val() + "\"}]}";

    retJson = "{\"noticeData\":[" + strJson + "]}";
    $.ajax({
        type: "post",
        async: false, //控制同步
        url: "" + ClassName + "",
        data: {
            CommandData: retJson
        },
        cache: false,
        success: function (data) {
            var v = eval("(" + data + ")");

            if (v.IsOk == "1") {
                var gridID = v.Grid;
                if (Id == "jqg1")
                    $("#" + gridID + "").trigger("reloadGrid");
                else
                { $("#" + gridID + "").jqGrid('saveRow', Id, $("#" + gridID + "").get(0).p.editParams); document.getElementById("save" + Id).innerHTML = "<i class='icon-save'></i>修 改"; }
            }
            else
                ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
        },
        Error: function (err) {
            ShowMessages(err, "系统提示", "msgwaring", null, 0);
        }
    });

}


//function getRowData(cellvalue, options, rowObject, gridId) {
//	var strJson = "";
//	var retJson = "";
//	var colModel = $("#" + gridId + "").jqGrid('getGridParam', 'colModel');
//	for (var i = 2; i < colModel.length; i++) {
//		strJson += "{\"" + colModel[i].name + "\":\"" + colModel[i].index + "\"},";
//	}
//	retJson = "{\"noticeData\":[" + strJson + "]}";
//	return retJson;
//	//managelist(cellvalue, options, rowObject,rowObject.TITLE, "saveNotice", "delNotice", "INotice");
//}


//点了删除按钮之后的删除
function delFiles(Id, ClassName, gridId) {
    ShowMessages("确定要删除当前记录吗?", "系统提示", "msgquestion", function okFunction() {
        var rowData = $("#" + gridId + "").jqGrid("getRowData", Id);
        var strJson = "{\"noticeData\":[{\"INDX\":\"" + rowData.INDX + "\"}]}"
        $.ajax({
            type: "post",
            async: false, //控制同步
            url: "" + ClassName + "",
            data: {
                CommandData: strJson
            },
            cache: false,
            success: function (data) {
                var v = eval("(" + data + ")");
                var gridId = v.Grid;
                if (v.IsOk == "1") {
                    $("#" + gridId + "").trigger("reloadGrid");
                }
                else
                    ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
            },
            Error: function (err) {
                ShowMessages(err, "系统提示", "msgwaring", null, 0);
            }
        });
    }, 1);


}

/*grid宽高自适应函数
 * gridId:   jqgrid控件ID 例如"#jg"
 * topHeight:自动计算高度时需要减去的高度，包括（banner高度+header高度+列表查询框高度），请根据页面实际情况自行调整。
 */
function setGridSize(gridId,topHeight){
	 $(gridId).setGridWidth($(window).width() -10);
	 var gridh=$(window).height()-topHeight;	//获得当前分辨率下grid高度
	 $(gridId).setGridHeight(gridh);	
}

function changeshow(cellvalue){
	 return cellvalue.replace(/\'/g,"＇") 	
}

