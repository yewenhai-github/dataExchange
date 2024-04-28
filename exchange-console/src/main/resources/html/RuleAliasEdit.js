var setting = {
            check: {
                enable: false
            },
            data: {
                simpleData: {
                    enable: true
                }
            },
            callback: {
                beforeClick: zTreeOnClick
            }
        };
        var zNodes;
        function BindTree(indx) {
            $.ajax({
                type: "post",
            async: false, //控制同步
            url: "GetAliasConfig?indx=" + indx,
            data: {
        },
        cache: false,
        success: function (data) {
            var v = eval("(" + data + ")");
            var t = $("#ruleAliastTree");
            var returnData = eval("(" + data.replace(/"([^"]+)":/g, function (v) { return v.toLowerCase(); }) + ")");
            t = $.fn.zTree.init(t, setting, returnData);
            zTree = $.fn.zTree.getZTreeObj("ruleAliastTree");
            zTree.selectNode(zTree.getNodeByParam("id", 1));
        }, Error: function (err) {
            ShowMessages(err, "友情提示", "msgwaring", null, 0);
        }
    });




    //            $.getJSON("GetAliasConfig?indx=" + indx, { id: Math.random() }, function (data, textStatus) {
    //                if (data != null) {
    //                    var menuTable = eval('(' + data.ReturnData + ')');
    //                    zNodes = menuTable.menuData;
    //                    $.fn.zTree.init($("#ruleAliastTree"), setting, zNodes);
    //                }
    //            });
}
var zTree = null;
//树的单击
function zTreeOnClick(event, node, clickFlag) {
    $("input[name='txtRule']").each(function () {
        $(this).removeAttr("disabled");
    });
    $("#txtId").val(node.id);
    if(GetRequestName("INDX")!= "" || $('#INDX').val() !=""){
    $("#btnNew").removeAttr("disabled");
    }
    var modelList = "";
    if(GetRequestName("type")!= ""){
		var type=GetRequestName("type");
		if(type="zbq"){
			modelList = "[{name:'ITEM_CODE',index:'ITEM_CODE',width:80},{name:'ITEM_NAME',index:'ITEM_NAME',width:220},{name:'INDX',index:'INDX',width:10,hidden:true}]";
		}
	}else{
	    if (parseInt(node.id) <= 1){
	        modelList = "[{name:'HS_CODE',index:'HS_CODE',width:80},{name:'HS_CNAME',index:'HS_CNAME',width:220},{name:'INDX',index:'INDX',width:10,hidden:true}]";
	    }else{
	        modelList = "[{name:'ITEMCODE',index:'ITEMCODE',width:80},{name:'ITEMNAME',index:'ITEMNAME',width:220},{name:'INDX',index:'INDX',width:10,hidden:true}]";
	    }
    }
    $('#tableRowList').GridUnload();
    // $('#tableSessionList').clearGridData();
    $("#tableRowList").jqGrid({
        datatype: "json",
        url: "RuleAliasRowList?id=" + node.id,
        colNames: ['代码', '描述', 'ID'],
        colModel: eval('(' + modelList + ')'),
        width: 300,
        autowidth: true,
        height: 200,
        shrinkToFit: true,
        autoScroll: false,
        rowNum: 16, //每页显示记录数
        //datatype: 'local',
        rowList: [16, 50, 100], //可调整每页显示的记录数
        rownumbers: true,
        rownumWidth: 35,
        multiselect: true, //是否支持多选 
        viewrecords: true, //是否显示行数
        //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
        pager: "#rowPager", //分页工具栏
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
    // }
}


function rbtLike() {
    $("#txtKeyWord").removeAttr("disabled");
    $("#txtStartNum").attr("disabled", "disabled");
    $("#txtEndNum").attr("disabled", "disabled");
}
function rbtFaiWei() {
    $("#txtKeyWord").attr("disabled", "disabled");
    $("#txtStartNum").removeAttr("disabled");
    $("#txtEndNum").removeAttr("disabled");
}
function rbtbtnNew() {
    var selectedIDs = $("#tableRowList").getGridParam("selarrrow");
    var strJson = "";
    var strJsons = "";
    if(GetRequestName("type")!= ""){
		var type=GetRequestName("type");
		if(type="zbq"){
			for (var i = 0; i < selectedIDs.length; i++) {
		        var rowData = $("#tableRowList").jqGrid("getRowData", selectedIDs[i]); //根据上面的id获得本行的所有数据
		        if (i == 0)
		            strJson += "{\"BIZ_CODE\":\"" + rowData.ITEM_CODE + "\",\"BIZ_NAME\":\"" + rowData.ITEM_NAME + "\",\"BIZ_TYPE\":\"1\"}";
		        else
		            strJson += ",{\"BIZ_CODE\":\"" + rowData.ITEM_CODE + "\",\"BIZ_NAME\":\"" + rowData.ITEM_NAME + "\",\"BIZ_TYPE\":\"1\"}";
		    }
		}
	}else{
		if (parseInt($("#txtId").val()) <= 1) {
	        for (var i = 0; i < selectedIDs.length; i++) {
	            var rowData = $("#tableRowList").jqGrid("getRowData", selectedIDs[i]); //根据上面的id获得本行的所有数据
	            if (i == 0)
	                strJson += "{\"BIZ_CODE\":\"" + rowData.HS_CODE + "\",\"BIZ_NAME\":\"" + rowData.HS_CNAME + "\",\"BIZ_TYPE\":\"1\"}";
	            else
	                strJson += ",{\"BIZ_CODE\":\"" + rowData.HS_CODE + "\",\"BIZ_NAME\":\"" + rowData.HS_CNAME + "\",\"BIZ_TYPE\":\"1\"}";
		    }
	    }
	    else {
	        for (var i = 0; i < selectedIDs.length; i++) {
	            var rowData = $("#tableRowList").jqGrid("getRowData", selectedIDs[i]); //根据上面的id获得本行的所有数据
	            if (i == 0)
	                strJson += "{\"BIZ_CODE\":\"" + rowData.ITEMCODE + "\",\"BIZ_NAME\":\"" + rowData.ITEMNAME + "\",\"BIZ_TYPE\":\"1\"}";
	            else
	                strJson += ",{\"BIZ_CODE\":\"" + rowData.ITEMCODE + "\",\"BIZ_NAME\":\"" + rowData.ITEMNAME + "\",\"BIZ_TYPE\":\"1\"}";
	        }
	    }
    }
    
    
    

    var ruleList = "{";
    $("input[name='txtRule']").each(function () {
        if ($(this).is(':checked') == true)
            ruleList += "\"txtRuleId\":\"" + $(this).attr("id") + "\"";
    });
    ruleList += ",\"txtKeyWord\":\"" + $("#txtKeyWord").val() + "\"";
    ruleList += ",\"txtStartNum\":\"" + $("#txtStartNum").val() + "\"";
    ruleList += ",\"txtEndNum\":\"" + $("#txtEndNum").val() + "\"";
    ruleList += ",\"txtMenuId\":\"" + $("#txtId").val() + "\"}";
    	 $("#txtAllJson").val(strJson);
         $('#tableSessionList').GridUnload();
         $("#tableSessionList").jqGrid({
             datatype: "json",
             url: "SessionRuleAliasRowTable",
             colNames: ['ID', '代码', '描述', 'BIZ_TYPE'],
             colModel: [
 					 { name: 'INDX', index: 'INDX', width: 90, hidden: true },
         	        { name: 'BIZ_CODE', index: 'BIZ_CODE', width: 80 },
         	        { name: 'BIZ_NAME', index: 'BIZ_NAME', width: 220 },
 					{ name: 'BIZ_TYPE', index: 'BIZ_TYPE', width: 90, hidden: true },
 				],
             postData: {
                 'CommandData': "{\"searchTable\":[" + strJson + "]}",
                 'other': "{\"addQuery\":[" + ruleList + "]}",
                 'txtid': $("#INDX").val()
             },
             viewrecords: true, // show the current page, data rang and total records on the toolbar
             mtype: 'POST',
             width: 300,
             autowidth: true,
             toolbar:[true,"top"],
             height: 200,
             shrinkToFit: true,
             autoScroll: true,
             rowNum: 200, //每页显示记录数
             //datatype: 'local',
             rownumbers: false,
             rownumWidth: 35,
             multiselect: true, //是否支持多选 
             //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
             pager: "#rowSessionPager", //分页工具栏
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
                	 okUrl();
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
         })		
         $("#t_tableSessionList").height(30);
         var btnhtml="<table width='100%' border='0' cellspacing='0' cellpadding='0' style='float:left;table-layout:auto;margin-top:2px' class='ui-pg-table topnavtable'>  <tr> <td align='right' style='padding-top: 2px;padding-bottom: 5px;padding-right: 5px;'> <button name='btnSave' type='button' class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left; margin-left: 5px; margin-right: 5px;' id='btnSaveUserRole' commandname='SaveAliaNews' commandok='showSuccess' commanddatas=\"FormToData('DataSource');\" onclick='Save(this)'> <i class='icon-save'></i>保 存</button> <button class='btn btn-primary btn-small' id='delRow' style='margin-bottom: 5px;float: left;margin-right: 5px;' onclick='delRow()'> <i class='icon-remove'></i>删 除  </button><button class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left;margin-right: 5px;' id='delAllRow'  onclick='delAllRow()'><i class='icon-remove'></i>全 删 </button> ";
      	 btnhtml+="<iframe class='hidden' name='uploadiframe' onload='dataloaded();'></iframe>";
      	btnhtml+="<form id='files' target='uploadiframe' enctype='multipart/form-data' method='post' <span class='input-uploadfile' style='float: left;' > <input type='file' name='uploadfile' id='uploadfile' class='file' /></span></form>";
      	btnhtml+="<button class='btn btn-primary btn-small' id='toExcel'  type='button' style='margin-bottom: 5px; margin-right: 5px; float: left;margin-left:5px;'>	<i class='icon-edit'></i>导 出</button>";
      	btnhtml+="<button id='btnDown' class='btn btn-primary btn-small' style='margin-bottom: 5px; margin-right: 5px; float: left;' type='button'><i class='icon-download icon-white'></i>下载模板</button>";
         $("#t_tableSessionList").append(btnhtml);
         $("#uploadfile").change(function () {
         	uploadfile();
         })
         $("#btnDown").click(function () {
        	 window.location.href = "resources/ImportAliaTemp.xlsx";
         })
         $("#toExcel").click(function () {
        	 var jgNum=$("#tableSessionList").jqGrid("getRowData").length;
        	 if(jgNum<=0){
        		 ShowMessages("无数据!", "友情提示", "msgwaring", null, 0);
        		 return ;
        	 }
        	 var form = document.getElementById("files");
     	    form.action = "EmportAliaNews?indx="+GetRequestName("INDX");
     	    form.submit();
         })
         //"<table width='100%' border='0' cellspacing='0' cellpadding='0' style='float:left;table-layout:auto;margin-top:2px' class='ui-pg-table topnavtable'>                    <tr>                    <td align='right' style='padding-top: 2px;padding-bottom: 5px;padding-right: 5px;'>                        <button name='btnSave' type='button' class='btn btn-primary btn-small' id='btnSaveUserRole'                            commandname='SaveAliaNews' commandok='showSuccess' commanddatas=\"FormToData('DataSource');\" onclick='Save(this)'>                            <i class='icon-save'></i>保 存</button>                        <button class='btn btn-primary btn-small' id='delRow' onclick='delRow()'>                            <i class='icon-remove'></i>删 除                        </button>                        <button class='btn btn-primary btn-small' id='delAllRow' onclick='delAllRow()'>                            <i class='icon-remove'></i>全 删                        </button>                        </td></tr>            </table>"
        
}
function delRow() {
    ShowMessages("您确定要删除选中的吗？", "系统提示", "msgquestion", delSelRows, 1);
}
function showSuccess(s) {
    if ($.parseJSON(s).IsOk == "1") {
        ShowMessages($.parseJSON(s).ErrMessage, "系统提示", "msgok", 0);
        var data = eval('(' + $.parseJSON(s).ReturnData + ')');
        $("#INDX").val(data.Role[0].INDX);
        $("#delRow").removeAttr("disabled");
        $("#delAllRow").removeAttr("disabled");
        $("#btnAddNew").removeAttr("disabled");
        return;
    }
}
function okUrl() {
	$("#tableRowList").jqGrid('setGridParam', {
        url: "RuleAliasRowList?id=" + $("#txtId").val(),
        mtype: 'POST', //发送数据 
        page: 1
    }).trigger("reloadGrid"); //重新载入
}
function rbtbtnAddNew() {
	var msg="";
    if($("#txtValue").val() ==""){
		msg += "请填写代码选项值！";
	}
    if($("#txtText").val() ==""){
		msg += "请填写描述参考值！";
	}
	if (msg != "") 
		ShowMessages(msg, "系统提示", "msgwaring", null, 0);
	else{
    $.ajax({
        type: 'POST',
        url: 'SaveRowRuleAias',
        data: {
            txtValue: $("#txtValue").val(),
            txtText: $("#txtText").val()
        },
        success: function (data) {
            $("#tableSessionList").jqGrid('setGridParam', {
                url: "SessionRowRuleList?txtId=" + $("#INDX").val(),
                mtype: 'POST', //发送数据 
                page: 1
            }).trigger("reloadGrid"); //重新载入 
        }
    })
	}
}
function delSelRows() {
    var selectedIDs = $("#tableSessionList").getGridParam("selarrrow");
    var allId = "";
    for (var i = 0; i < selectedIDs.length; i++) {
        var rowData = $("#tableSessionList").jqGrid("getRowData", selectedIDs[i]); //根据上面的id获得本行的所有数据
        allId += "'" + rowData.BIZ_CODE + "',";
    }
    $("#tableSessionList").jqGrid('setGridParam', {
        url: "SessionRuleAliasRowTable",
        mtype: 'POST',
        postData: {
            'allid': allId,
            'txtId': $("#INDX").val()
        }, //发送数据 
        page: 1
    }).trigger("reloadGrid"); //重新载入 
}
function delAllRow() {
    ShowMessages("您确定要清空所有的吗？", "系统提示", "msgquestion", delAllRows, 1);
}
function delAllRows() {
    $("#tableSessionList").jqGrid('setGridParam', {
        url: "SessionRowRuleList?isclear=1",
        mtype: 'POST', //发送数据 
        page: 1
    }).trigger("reloadGrid"); //重新载入 
}
function initRuleList() {
    var modelList = "[{name:'ITEMCODE',index:'ITEMCODE',width:80},{name:'ITEMNAME',index:'ITEMNAME',width:220},{name:'INDX',index:'INDX',width:10,hidden:true}]";
    $("#tableRowList").jqGrid({
        datatype: "json",
        url: "RuleAliasRowList?id=" + $("#txtId").val(),
        colNames: ['代码', '描述', 'ID'],
        colModel: eval('(' + modelList + ')'),
        width: 300,
        autowidth: true,
        height: 200,
        shrinkToFit: true,
        autoScroll: true,
        rowNum: 16, //每页显示记录数
        //datatype: 'local',
        rowList: [16, 50, 100], //可调整每页显示的记录数
        rownumbers: false,
        rownumWidth: 35,
        multiselect: true, //是否支持多选 
        //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
        pager: "#rowPager", //分页工具栏
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
    $("#tableSessionList").jqGrid({
        datatype: "json",
        url: "SessionRowRuleList?isclear=1",
        colNames: ['ID', '代码', '描述', 'BIZ_TYPE'],
        colModel: [
				 { name: 'INDX', index: 'INDX', width: 90, hidden: true },
    	        { name: 'BIZ_CODE', index: 'BIZ_CODE', width: 80 },
    	        { name: 'BIZ_NAME', index: 'BIZ_NAME', width: 220 },
				{ name: 'BIZ_TYPE', index: 'BIZ_TYPE', width: 90, hidden: true },
			],
        mtype: 'POST',
        width: 300,
        toolbar:[true,"top"],
        autowidth: true,
        height: 200,
        shrinkToFit: true,
        autoScroll: true,
        rowNum: 200, //每页显示记录数
        //datatype: 'local',
        rownumbers: false,
        rownumWidth: 35,
        multiselect: true, //是否支持多选 
        //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
        pager: "#rowSessionPager", //分页工具栏
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
    })
    $("#t_tableSessionList").height(30);
    var btnhtml="<table width='100%' border='0' cellspacing='0' cellpadding='0' style='float:left;table-layout:auto;margin-top:2px' class='ui-pg-table topnavtable'>  <tr> <td align='right' style='padding-top: 2px;padding-bottom: 5px;padding-right: 5px;'> <button name='btnSave' type='button' class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left; margin-left: 5px; margin-right: 5px;' id='btnSaveUserRole' commandname='SaveAliaNews' commandok='showSuccess' commanddatas=\"FormToData('DataSource');\" onclick='Save(this)'> <i class='icon-save'></i>保 存</button> <button class='btn btn-primary btn-small' id='delRow' style='margin-bottom: 5px;float: left;margin-right: 5px;' onclick='delRow()'> <i class='icon-remove'></i>删 除  </button><button class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left;margin-right: 5px;' id='delAllRow'  onclick='delAllRow()'><i class='icon-remove'></i>全 删 </button> ";
 	 btnhtml+="<iframe class='hidden' name='uploadiframe' onload='dataloaded();'></iframe>";
 	btnhtml+="<form id='files' target='uploadiframe' enctype='multipart/form-data' method='post' <span class='input-uploadfile' style='float: left;' > <input type='file' name='uploadfile' id='uploadfile' class='file' /></span></form>";
 	btnhtml+="<button class='btn btn-primary btn-small' id='toExcel'  type='button' style='margin-bottom: 5px; margin-right: 5px; float: left;margin-left:5px;'>	<i class='icon-edit'></i>导 出</button>";
 	btnhtml+="<button id='btnDown' class='btn btn-primary btn-small' style='margin-bottom: 5px; margin-right: 5px; float: left;' type='button'><i class='icon-download icon-white'></i>下载模板</button>";
    $("#t_tableSessionList").append(btnhtml);
    $("#uploadfile").change(function () {
    	uploadfile();
    })
    $("#btnDown").click(function () {
    	window.location.href = "resources/ImportAliaTemp.xlsx";
    })
     $("#toExcel").click(function () {
    	 var jgNum=$("#tableSessionList").jqGrid("getRowData").length;
    	 if(jgNum<=0){
    		 ShowMessages("无数据!", "友情提示", "msgwaring", null, 0);
    		 return ;
    	 }
    	 var form = document.getElementById("files");
 	    form.action = "EmportAliaNews?indx="+GetRequestName("INDX");
 	    form.submit();
     })
    //"<table width='100%' border='0' cellspacing='0' cellpadding='0' style='float:left;table-layout:auto;margin-top:2px' class='ui-pg-table topnavtable'>                    <tr>                    <td align='right' style='padding-top: 2px;padding-bottom: 5px;padding-right: 5px;'>                        <button name='btnSave' type='button' class='btn btn-primary btn-small' id='btnSaveUserRole'                            commandname='SaveAliaNews' commandok='showSuccess' commanddatas=\"FormToData('DataSource');\" onclick='Save(this)'>                            <i class='icon-save'></i>保 存</button>                        <button class='btn btn-primary btn-small' id='delRow' onclick='delRow()'>                            <i class='icon-remove'></i>删 除                        </button>                        <button class='btn btn-primary btn-small' id='delAllRow' onclick='delAllRow()'>                            <i class='icon-remove'></i>全 删                        </button>                        </td></tr>            </table>"
    
}
$(function () {
    if (GetRequestName("INDX") != "") {
        DoCommand("GetAliasByIndx?indx=" + GetRequestName("INDX"));
        BindTree(GetRequestName("INDX"));
        var modelList="";
        if(GetRequestName("type")!= ""){
    		var type=GetRequestName("type");
    		if(type="zbq"){
    			modelList = "[{name:'ITEM_CODE',index:'ITEM_CODE',width:80},{name:'ITEM_NAME',index:'ITEM_NAME',width:220},{name:'INDX',index:'INDX',width:10,hidden:true}]";
    		}
    	}else{
    	    modelList = "[{name:'ITEMCODE',index:'ITEMCODE',width:80},{name:'ITEMNAME',index:'ITEMNAME',width:220},{name:'INDX',index:'INDX',width:10,hidden:true}]";
        }
        $("#tableRowList").jqGrid({
            datatype: "json",
            url: "RuleAliasRowList?id=" + $("#txtId").val(),
            colNames: ['代码', '描述', 'ID'],
            colModel: eval('(' + modelList + ')'),
            width: 300,
            autowidth: true,
            height: 200,
            shrinkToFit: true,
            autoScroll: true,
            rowNum: 16, //每页显示记录数
            //datatype: 'local',
            rowList: [16, 50, 100], //可调整每页显示的记录数
            rownumbers: true,
            rownumWidth: 35,
            multiselect: true, //是否支持多选 
            //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
            pager: "#rowPager", //分页工具栏
            loadui: "none", //隐藏默认的loading  
            altRows: true,
            altclass: 'someClass',
            viewrecords: true, //是否显示行数 
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



        $("#tableSessionList").jqGrid({
            datatype: "json",
            url: "RowRuleListByIndx?indx=" + GetRequestName("INDX"),
            colNames: ['ID', '代码', '描述', 'BIZ_TYPE'],
            colModel: [
				 { name: 'INDX', index: 'INDX', width: 90, hidden: true },
    	        { name: 'BIZ_CODE', index: 'BIZ_CODE', width: 80 },
    	        { name: 'BIZ_NAME', index: 'BIZ_NAME', width: 220 },
				{ name: 'BIZ_TYPE', index: 'BIZ_TYPE', width: 90, hidden: true },
			],
            mtype: 'POST',
            width: 300,
            toolbar:[true,"top"],
            autowidth: true,
            height: 200,
            shrinkToFit: true,
            autoScroll: true,
            rowNum: 200, //每页显示记录数
            //datatype: 'local',
            rownumbers: true,
            rownumWidth: 35,
            multiselect: true, //是否支持多选 
            //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
            pager: "#rowSessionPager", //分页工具栏
            loadui: "none", //隐藏默认的loading  
            altRows: true,
            altclass: 'someClass',
            viewrecords: true, //是否显示行数 
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
        })
        $("#t_tableSessionList").height(30);
        var btnhtml="<table width='100%' border='0' cellspacing='0' cellpadding='0' style='float:left;table-layout:auto;margin-top:2px' class='ui-pg-table topnavtable'>  <tr> <td align='right' style='padding-top: 2px;padding-bottom: 5px;padding-right: 5px;'> <button name='btnSave' type='button' class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left; margin-left: 5px; margin-right: 5px;' id='btnSaveUserRole' commandname='SaveAliaNews' commandok='showSuccess' commanddatas=\"FormToData('DataSource');\" onclick='Save(this)'> <i class='icon-save'></i>保 存</button> <button class='btn btn-primary btn-small' id='delRow' style='margin-bottom: 5px;float: left;margin-right: 5px;' onclick='delRow()'> <i class='icon-remove'></i>删 除  </button><button class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left;margin-right: 5px;' id='delAllRow'  onclick='delAllRow()'><i class='icon-remove'></i>全 删 </button> ";
      	 btnhtml+="<iframe class='hidden' name='uploadiframe' onload='dataloaded();'></iframe>";
      	btnhtml+="<form id='files' target='uploadiframe' enctype='multipart/form-data' method='post' <span class='input-uploadfile' style='float: left;' > <input type='file' name='uploadfile' id='uploadfile' class='file' /></span></form>";
      	btnhtml+="<button class='btn btn-primary btn-small' id='toExcel'  type='button' style='margin-bottom: 5px; margin-right: 5px; float: left;margin-left:5px;'>	<i class='icon-edit'></i>导 出</button>";
      	btnhtml+="<button id='btnDown' class='btn btn-primary btn-small' style='margin-bottom: 5px; margin-right: 5px; float: left;' type='button'><i class='icon-download icon-white'></i>下载模板</button>";
       	 
        $("#t_tableSessionList").append(btnhtml);
        $("#uploadfile").change(function () {
        	uploadfile();
        })
        $("#btnDown").click(function () {
        	window.location.href = "resources/ImportAliaTemp.xlsx";
        })
         $("#toExcel").click(function () {
        	 var jgNum=$("#tableSessionList").jqGrid("getRowData").length;
        	 if(jgNum<=0){
        		 ShowMessages("无数据!", "友情提示", "msgwaring", null, 0);
        		 return ;
        	 }
        	 var form = document.getElementById("files");
     	    form.action = "EmportAliaNews?indx="+GetRequestName("INDX");
     	    form.submit();
         })
        //"<table width='100%' border='0' cellspacing='0' cellpadding='0' style='float:left;table-layout:auto;margin-top:2px' class='ui-pg-table topnavtable'>                    <tr>                    <td align='right' style='padding-top: 2px;padding-bottom: 5px;padding-right: 5px;'>                        <button name='btnSave' type='button' class='btn btn-primary btn-small' id='btnSaveUserRole'                            commandname='SaveAliaNews' commandok='showSuccess' commanddatas=\"FormToData('DataSource');\" onclick='Save(this)'>                            <i class='icon-save'></i>保 存</button>                        <button class='btn btn-primary btn-small' id='delRow' onclick='delRow()'>                            <i class='icon-remove'></i>删 除                        </button>                        <button class='btn btn-primary btn-small' id='delAllRow' onclick='delAllRow()'>                            <i class='icon-remove'></i>全 删                        </button>                        </td></tr>            </table>"
        /* $("#tableSessionList").jqGrid('setGridParam', {
        		url: "RowRuleListByIndx?indx="+GetRequestName("INDX"),
        		page: 1
        	}).trigger("reloadGrid"); //重新载入   */
    }
    else {
        DoCommand("RowRuleListByIndx?indx=0");
        BindTree(0);
        initRuleList();
        $('#delRow').attr('disabled',"true");
        $('#delAllRow').attr('disabled',"true");
        $('#btnNew').attr('disabled',"true");
        $('#btnAddNew').attr('disabled',"true");
    }
})

 


function Save(button){
    if (top.showloading) {
        top.showloading();
    }
    if (button.attributes.CommandName.nodeValue.substring(0, 1) == "#") {
    	var msg="";
        if($("#PROCESS_NAME").val() ==""){
    		msg += "请填写别名名称！";
    	}
        if($("#REMARK").val() ==""){
    		msg += "请填写描述！";
    	}
    	if (msg != "") 
    		{
    		ShowMessages(msg, "系统提示", "msgwaring", null, 0);
    		 if (top.hideloading) {
                 top.hideloading();
             }}
    	else{
        eval(button.attributes.CommandName.nodeValue.substring(1));
        if (top.hideloading) {
            top.hideloading();
        }
    	}
    }
    else {
    	var msg="";
        if($("#PROCESS_NAME").val() ==""){
    		msg += "请填写别名名称！";
    	}
        if($("#REMARK").val() ==""){
    		msg += "请填写描述！";
    	}
    	if (msg != "") {
    		ShowMessages(msg, "系统提示", "msgwaring", null, 0);
    	 if (top.hideloading) {
             top.hideloading();
         }
    	}
    	else{
        var parms = "";
        if (button.attributes.CommandDatas.nodeValue && button.attributes.CommandDatas.nodeValue != "") {
            parms = eval(button.attributes.CommandDatas.nodeValue);
        }

        var xmlhttp;
        if (window.XMLHttpRequest) {
            // code for IE7+, Firefox, Chrome, Opera, Safari
            xmlhttp = new XMLHttpRequest();
        }
        else {
            // code for IE6, IE5
            xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
        }
        xmlhttp.getResponseText = eval(button.attributes.CommandOK.nodeValue);

        xmlhttp.error = function (s) {
            if (ShowMessages) {
                ShowMessages(s);
            }
            else {
                alert(s);
            }
            if (top.hideloading) {
                top.hideloading();
            }
        };

        xmlhttp.onreadystatechange = function (e) {
            if (this.readyState == 4) {
                if (this.status == 200) {
                    if (this.getResponseText != null) {
                        this.getResponseText(this.responseText);
					  if (top.hideloading) {
						  top.hideloading();
					  }
                    }
                }
                else {
                    if (this.error != null) {
                        this.error(this.responseText);
					  if (top.hideloading) {
						  top.hideloading();
					  }
                    }
                }
            }
        };
        var l = location.search;
        if (location.search != "") {
            if (button.attributes.CommandName.nodeValue.indexOf("?") > 0) {
                l = l.replace("?", "&");
            }
        }
        xmlhttp.open("POST", button.attributes.CommandName.nodeValue + l, true);
        xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
        try {
            var flag = true;
			if(button.isValiDate=="1")
			{
			  if(button.DataContainer!=null)
				 flag=formValidatorAllInput(button.DataContainer);
			  else
				 flag=formValidatorAllInput(null);
			}
			else
			  flag=true;
            if(flag)
            xmlhttp.send("CommandData=" + encodeURIComponent(parms));
			else
			  {
				  if(button.DataContainer!=null)
				  {
					 if(document.getElementById(button.DataContainer+"-formText").value!="")
					 {
						 ShowMessages(document.getElementById(button.DataContainer+"-formText").value,"系统提示","msgwaring",function foucsFunction()
						 {
							 document.getElementById(document.getElementById(button.DataContainer+"-focusInput").value).focus();
						 },null);
					 }
				  }
				  else
				  {
					 if(document.getElementById("formText").value!="")
					 {
						 ShowMessages(document.getElementById("formText").value,"系统提示","msgwaring",function foucsFunction()
						 {
							document.getElementById(document.getElementById("focusInput").value).focus(); 
						 },0);
					 }
				  }
				if (top.hideloading) {
					top.hideloading();
				}
           }
        } catch (errs) {
            showmsg(errs);
        }
    }
    }
}
function dispathServerAjaxPost(url,serviceJson , afterPost ,async){
 //默认为异步
   if (!async)
       async = false;
 else
     async = true; 
 $.ajax(
 {
     type: "POST",
     url: url,
     data: {"serviceJson":serviceJson},
     contentType: "application/x-www-form-urlencoded;charset=utf-8",
     cache: false,
     dataType: 'text',
     async: async,
     success: function (data) {		         
         if(afterPost instanceof Function)
         	afterPost(data);
     },
     error: function (xhr, data) { 
    	 ShowMessages(data, "友情提示", "msgwaring", null, 0);		         
   	         return false;
   	     }
   	 });
   }
function roaldAliasTempJqGrid(){
	$('#tableSessionList').GridUnload();
    $("#tableSessionList").jqGrid({
        datatype: "json",
        url: "SessionRuleAliasRowTable",
        colNames: ['ID', '代码', '描述', 'BIZ_TYPE'],
        colModel: [
				 { name: 'INDX', index: 'INDX', width: 90, hidden: true },
    	        { name: 'BIZ_CODE', index: 'BIZ_CODE', width: 80 },
    	        { name: 'BIZ_NAME', index: 'BIZ_NAME', width: 220 },
				{ name: 'BIZ_TYPE', index: 'BIZ_TYPE', width: 90, hidden: true },
			],
        postData: {
            'txtId': $("#INDX").val(),
            'typename':"afterImport"
        },
        viewrecords: true, // show the current page, data rang and total records on the toolbar
        mtype: 'POST',
        width: 300,
        autowidth: true,
        toolbar:[true,"top"],
        height: 200,
        shrinkToFit: true,
        autoScroll: true,
        rowNum: 200, //每页显示记录数
        //datatype: 'local',
        rownumbers: false,
        rownumWidth: 35,
        multiselect: true, //是否支持多选 
        //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
        pager: "#rowSessionPager", //分页工具栏
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
           	 okUrl();
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
    })	;	
    $("#t_tableSessionList").height(30);
    var btnhtml="<table width='100%' border='0' cellspacing='0' cellpadding='0' style='float:left;table-layout:auto;margin-top:2px' class='ui-pg-table topnavtable'>  <tr> <td align='right' style='padding-top: 2px;padding-bottom: 5px;padding-right: 5px;'> <button name='btnSave' type='button' class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left; margin-left: 5px; margin-right: 5px;' id='btnSaveUserRole' commandname='SaveAliaNews' commandok='showSuccess' commanddatas=\"FormToData('DataSource');\" onclick='Save(this)'> <i class='icon-save'></i>保 存</button> <button class='btn btn-primary btn-small' id='delRow' style='margin-bottom: 5px;float: left;margin-right: 5px;' onclick='delRow()'> <i class='icon-remove'></i>删 除  </button><button class='btn btn-primary btn-small' style='margin-bottom: 5px;float: left;margin-right: 5px;' id='delAllRow'  onclick='delAllRow()'><i class='icon-remove'></i>全 删 </button> ";
 	 btnhtml+="<iframe class='hidden' name='uploadiframe' onload='dataloaded();'></iframe>";
 	btnhtml+="<form id='files' target='uploadiframe' enctype='multipart/form-data' method='post' <span class='input-uploadfile' style='float: left;' > <input type='file' name='uploadfile' id='uploadfile' class='file' /></span></form>";
 	btnhtml+="<button class='btn btn-primary btn-small' id='toExcel'  type='button' style='margin-bottom: 5px; margin-right: 5px; float: left;margin-left:5px;'>	<i class='icon-edit'></i>导 出</button>";
 	btnhtml+="<button id='btnDown' class='btn btn-primary btn-small' style='margin-bottom: 5px; margin-right: 5px; float: left;' type='button'><i class='icon-download icon-white'></i>下载模板</button>";
    $("#t_tableSessionList").append(btnhtml);
    $("#uploadfile").change(function () {
    	uploadfile();
    })
    $("#btnDown").click(function () {
        window.location.href = "resources/ImportAliaTemp.xlsx";
    })
     $("#toExcel").click(function () {
    	 var jgNum=$("#tableSessionList").jqGrid("getRowData").length;
    	 if(jgNum<=0){
    		 ShowMessages("无数据!", "友情提示", "msgwaring", null, 0);
    		 return ;
    	 }
    	 var form = document.getElementById("files");
 	    form.action = "EmportAliaNews?indx="+GetRequestName("INDX");
 	    form.submit();
     });
  	
}