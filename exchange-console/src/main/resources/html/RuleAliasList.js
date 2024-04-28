
$(document).ready(function () {
	document.width = $(window).width() - 10;
	$("#RuleAliasTable").jqGrid({
	    datatype: "json",
	    url: "RuleAliasList",
	    postData: {
	        'CommandData': FormToData('SearchTable')
	    },
	    colNames: ['别名', '描述', 'ID'],
	    colModel: [
					{ name: 'PROCESS_NAME', index: 'PROCESS_NAME', width: 200, formatter: urlCode },
		            { name: 'REMARK', index: 'REMARK', width: 400, formatter: unurlRemark},
	                { name: 'INDX', index: 'INDX', width: 10 }
		        ],
	    viewrecords: true,
	    width: 500,
	    autowidth: true,
	    height: 390,
	    shrinkToFit: true,
	    autoScroll: true,
	    rowNum: 16, //每页显示记录数
	    //datatype: 'local',
	    rowList: [16, 50, 100], //可调整每页显示的记录数
	    rownumbers: true,
	    rownumWidth: 35,
	    multiselect: true, //是否支持多选 
	    multiselectWidth: 35,
	    pagerpos: "center",
	    //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
	    pager: "#RuleAliasPager", //分页工具栏
	    loadui: "none", //隐藏默认的loading  
	    altRows: true,
	    altclass: 'someClass',
	    beforeRequest: function () {
	        if (top.showloading) {
	            top.showloading();
	        }
	    },
	    gridComplete: function () {
	        if (top.hideloading) {
	            top.hideloading();
	        }
	    },
	    //排序开始
	    onSortCol: function (colName, Id, sortorder) {
	        //colName   排序字段
	        //Id   第几列
	        //sortorder  排序  desc 倒序  asc 正序
	        $("#RuleAliasTable").jqGrid('setGridParam', {
	            url: "RuleAliasList",
	            postData: {
	                'OrderField': colName,
	                'Order': sortorder,
	                'CommandData': FormToData('SearchTable')
	            }, //发送数据 
	            page: 1
	        }).trigger("reloadGrid"); //重新载入 
	    },
	    beforeRequest: function () {
	        if (top.showloading) {
	            top.showloading();
	        }
	    },
	    gridComplete: function () {
	        if (top.hideloading) {
	            top.hideloading();
	        }
	    },
	    //排序结束
	    // loadonce : true,
	    //caption: "**Data",
	    sortable: true,
	    loadComplete: function () {
	        var rowNum = $(this).jqGrid('getGridParam', 'records');
	        if (rowNum <= 0) {
	            if ($(this).parent().children('.norecords').length == 0)
	                $(this).parent().append("<div class=\"norecords\">暂无数据！</div>");
	            $(this).parent().children('.norecords').show();
	        }
	        else {//如果存在记录，则隐藏提示信息。
	            $(this).parent().children('.norecords').hide();
	        }
	    }
	});
	$("#RuleAliasPager_right").width(500);
    window.onresize = function () {
        $("#RuleAliasTable").setGridWidth($(window).width()  - 10);
    }
    
    $("#RuleAliasTable").setGridParam().hideCol("INDX").trigger("reloadGrid");
    $("#RuleAliasTable").setGridWidth($(window).width()  - 10);
    $("#btnSoEnt").click(function () {
        $("#RuleAliasTable").jqGrid('setGridParam', {
            url: "RuleAliasList",
            postData: {
                'CommandData': FormToData('SearchTable')
            }, //发送数据 
            page: 1
        }).trigger("reloadGrid"); //重新载入 
    });
    
    if(GetRequestName("type")!= ""){
    	var type=GetRequestName("type");
    	var loca="javascript:location.href='GetHTML?rule/RuleAliasEdit.html?type="+type+"'";
    	$("#btnNew").attr("onclick",loca);
	}
});
function unurlRemark(cellvalue, options, rowObject)
{
	return decodeURIComponent(cellvalue);
}
function urlCode(cellvalue, options, rowObject) {
	var type="";
	if(GetRequestName("type")!= ""){
		type=GetRequestName("type");
	}
	if(parent.window.location.href.indexOf('/forms/')>-1){
		var detail_uri = "GetHTML?html/02AnyXml2DBEdit.html";
		return "<a href="+detail_uri+"?INDX="+rowObject.INDX + "&CELLVALUE=" + cellvalue+"'><span style='color:#00acec'>" + cellvalue + "</a>";
		return "<a href=\"javascript:parent.NavaddTab('别名组设置详情','../GetHTML?rule/RuleAliasEdit.html?INDX=" + rowObject.INDX + "&type="+type+"','" + cellvalue + "')\"><span style=\"color:#00acec\">" + cellvalue + "</a>";
	 
	} else{
		var detail_uri = "GetHTML?html/02AnyXml2DBEdit.html";
		return "<a href="+detail_uri+"?INDX="+rowObject.INDX + "&CELLVALUE=" + cellvalue+"'><span style='color:#00acec'>" + cellvalue + "</a>";
		return "<a href=\"javascript:parent.NavaddTab('别名组设置详情','RuleAliasEdit.html?INDX=" + rowObject.INDX + "&type="+type+"','" + cellvalue + "')\"><span style=\"color:#00acec\">" + cellvalue + "</a>";  
	}
		  }

//获取所有Id
function getId(gridId) {
    var IsAll = 0;
    if ($("#txtSelAll").attr("checked") == true)
        IsAll = 1;
    var rowKey = $("#" + gridId + "").getGridParam("selrow");
    if (IsAll == 0) {
        if (!rowKey)
            ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring", null, 0);
        else
            return returnEnd(gridId, IsAll);
    }
    else
        return returnEnd(gridId, IsAll);
}
function returnEnd(gridId, IsAll) {
    var selectedIDs = $("#" + gridId + "").getGridParam("selarrrow");
    var Indxs = "";
    var BillCodes = "";
    var BillDecl_NO = "";
    var strJson = "";
    var strJsons = "";
    for (var i = 0; i < selectedIDs.length; i++) {
        var rowData = $("#" + gridId + "").jqGrid("getRowData", selectedIDs[i]); //根据上面的id获得本行的所有数据
        strJson = "{\"INDX\":\"" + rowData.INDX + "\"},"
        strJsons += strJson;
    }
    return "{\"key\":[" + strJsons + "],\"IsAll\":[{\"isall\":\"" + IsAll + "\"}]," + FormToData('SearchTable') + "}";
}


function showSuccess(s) {
    var v = eval("(" + s + ")");
    if (v.IsOk == "1") {
        ShowMessages(v.ErrMessage, "系统提示", "msgok", function okFunction() {
            $("#RuleAliasTable").jqGrid('setGridParam', {
                url: "RuleAliasList",
                postData: {
                    'CommandData': FormToData('SearchTable')
                }, //发送数据 
                page: 1
            }).trigger("reloadGrid"); //重新载入  
        }, 0);
    }
    else
        ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
    }

  //批量操作
  function BitchToDo(ID) {
    var grid = $("#RuleAliasTable");
var rowKey = grid.getGridParam("selrow");
var meg="您确定要删除选中数据吗?";

if (!rowKey)
    ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring", null, 0);
else {
    ShowMessages(meg, "系统提示", "msgquestion", function okFunction() {
        var selectedIDs = grid.getGridParam("selarrrow");
        var result = "";
        for (var i = 0; i < selectedIDs.length; i++) {
            var rowData = grid.jqGrid("getRowData", selectedIDs[i]); //根据上面的id获得本行的所有数据
            var valIndx = rowData.INDX;
            result += valIndx + ",";
        }
        $.ajax({
            type: "post",
            async: false, //控制同步
            url: ID,
            data: {
                allId: result,
                CommandData: FormToData('SearchTable')
            },
            cache: false,
            success: function (data) {
                var v = eval("(" + data + ")");
                if (v.IsOk == "1") {
                    ShowMessages(v.ErrMessage, "系统提示", "msgok", function okFunction() {
                        $("#RuleAliasTable").trigger("reloadGrid");
                    }, 0);
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
  }