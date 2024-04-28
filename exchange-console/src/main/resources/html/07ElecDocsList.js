String.prototype.replaceAll = function (AFindText,ARepText){
 raRegExp = new RegExp(AFindText,"g");
 return this.replace(raRegExp,ARepText);
}
var pid="0";
function InitPage()
{
  	if(GetRequestName("pid")!="")
	{
		pid=GetRequestName("pid");
		$("#POINT_ID").val(pid);
		$("#txtTableId").val(pid);
		selectOrgNode(pid);
	}	
}
function getRightType() {
	return "{\"SearchTable\":[{\"TYPE\":\"RightType\"}]}";
}

$(document).ready(function(){
	$('#actimediv').hide();
	var c = $(this);
	var setting = {
		view: {
			dblClickExpand: false,
			showLine: true,
			selectedMulti: false
		},
		data: {
			simpleData: {
				enable:true,
				idKey: "id",
				pIdKey: "pid",
				rootPId: ""
			}
		},
		callback: {
			onRightClick : OnRightClick,
			beforeClick : zTreeOnClick,
			onAsyncSuccess : zTreeOnAsyncSuccess,
			onDrop : zTreeOnDrop,
			onDrag : zTreeOnDrag
		},
		edit:{
			drag:{
				isCopy:false
			},
			enable:true,
			showRemoveBtn: false,
			showRenameBtn: false
		}
	};
	var zTree = null;
	//树的单击
	function zTreeOnClick(event, treeNode, clickFlag) {
		if (!zTree.isSelectedNode(treeNode)) {
			selectOrgNode(treeNode.id,treeNode);
			if(parseInt(treeNode.id)>1)
			{
				var inputs =document.getElementById("RuleData").getElementsByTagName("input")
				for(var i=0;i<inputs.length;i++)
					inputs[i].disabled = '';
				var buttons =document.getElementById("RuleData").getElementsByTagName("button")
				for(var i=0;i<buttons.length;i++)
					buttons[i].disabled = '';
			}
			else
			{
				var inputs =document.getElementById("RuleData").getElementsByTagName("input")
				for(var i=0;i<inputs.length;i++)
					inputs[i].disabled = 'disabled';
				var buttons =document.getElementById("RuleData").getElementsByTagName("button")
				for(var i=0;i<buttons.length;i++)
					buttons[i].disabled = 'disabled';
			}
		}
	}
	//选中树当前节点
	function selectOrgNode(id,treeNode){
		$("#POINT_ID").val(id);
		$("#txtTableId").val(id);
		zTree.selectNode(treeNode);
		$.ajax({
		  type:'POST',
		  url:'GetRulePointByIndx',
		  async: false, //控制同步
		  data:{
			  Indx:id
		  },
		  success:function(data){
			  var showData=eval('('+data+')');
			  var showRsultData=eval('('+showData.ReturnData+')');
			  var POINT_CODE=showRsultData.rows[0].SHOW_MAPPING_CON;
			  $("#txtPointCode").val(POINT_CODE);
			  $("#SHOW_HIT_EX_CON").val(showRsultData.rows[0].SHOW_HIT_EX_CON);
			  window["COULMN_NAME"].Query=getCoulmnName(""+POINT_CODE+"");
			  window["RULE_HIT_EX"].Query=getRuleConfig(""+showRsultData.rows[0].SHOW_HIT_EX_CON+"");
		  }
		})
		if(treeNode.id == '_tempid'){
			var tn = $.extend({},treeNode);
			tn.id = null;
		}else{
			$('#actimediv').hide();
			$("#jqGrid").jqGrid('setGridParam', {
            	url: "GetRuleCheck",
                postData: {
                    'pid': id
                },
                page: 1
            }).trigger("reloadGrid"); //重新载入 
		}
	}
	//树的右键
	function OnRightClick(event, treeId, treeNode) {
		var rMenu = $('#fcMenuCnt', c);
		var obj = rMenu.offsetParent();
		var curleft = event.clientX;
		var curtop = event.clientY;
		while (obj.prop('tagName').toLowerCase() != 'body') {
			if (obj.prop('tagName').toLowerCase() == 'div') {
				curleft = curleft - obj.position().left + obj.scrollLeft();
				curtop = curtop - obj.position().top + obj.scrollTop();
			}
			obj = obj.offsetParent();
		}
		//对于临时节点和叶子节点不显示刷新子功能节点
		if(!(treeNode.children!=undefined && treeNode.children !=null && treeNode.children.length > 0 ) || treeNode.id == '_tempid'){
			$('#fcMenu > li[menuid=refresh]',c).css('display','none');
		}else{
			$('#fcMenu > li[menuid=refresh]',c).css('display','');
		}
		//存在子节点不能删除
		if(treeNode.isParent==true){
			$('#fcMenu > li[menuid=del]',c).css('display','none');
		}else{
			$('#fcMenu > li[menuid=del]',c).css('display','');
		}
		rMenu.css({
			"top" : curtop + "px",
			"left" : curleft + "px",
			"visibility" : "visible"
		});
		//选中右键所指向的节点
		if (!zTree.isSelectedNode(treeNode)) {
			selectOrgNode(treeNode.id,treeNode);
		}
	}
	
	//树，拖拽-点击
	function zTreeOnDrag(event, treeId, treeNodes) {
		if(treeNodes.length == 1){
			var pn = treeNodes[0].getParentNode();
			if(pn.children != null && pn.children.length > 1){
				
			}else{
				pn.isParent = false;
				pn.nocheck = false;
				zTree.updateNode(pn);
			}
		}
	}
	//树，拖拽-放下
	function zTreeOnDrop(event, treeId, treeNodes, targetNode, moveType) {
		if(moveType == null){
			var pn = treeNodes[0].getParentNode();
			pn.isParent = true;
			pn.nocheck = true;
			zTree.updateNode(pn);
		}
		if(moveType == 'inner'){
			if(treeNodes.length == 1){
				var movedNode = treeNodes[0];
				var nid = movedNode.id;
				var tid = targetNode.id;
				SmFunctionManager.updateParent(nid,tid,function(){
					targetNode.isParent = true;
					targetNode.nocheck = true;
					zTree.updateNode(targetNode);
				});
			}
		}
	}
	//异步加载成功
	function zTreeOnAsyncSuccess(event, treeId, treeNode, msg) {
		if(treeNode == undefined){
			var root =  zTree.getNodesByParam("id", "1", null)[0];
			zTree.expandNode(root, true, false, true);

						
		}else{
			if(treeNode.children!=undefined && treeNode.children !=null && treeNode.children.length > 0 ){
				
			}else{
				treeNode.isParent = false;
				treeNode.nocheck = false;
				zTree.updateNode(treeNode);
			}
		}
	}
	
	//加载树的数据
	$.ajax({
        type: "post",
        async: false, //控制同步
        url: "GetRulePoint",
        data: {
        },
        cache: false,
        success: function (data) {
            var v = eval("(" + data + ")");
            var t = $("#ruleTree");
			var inputs =document.getElementById("RuleData").getElementsByTagName("input")
			for(var i=0;i<inputs.length;i++)
				inputs[i].disabled = 'disabled';
				var buttons =document.getElementById("RuleData").getElementsByTagName("button")
				for(var i=0;i<buttons.length;i++)
					buttons[i].disabled = 'disabled';
//			 var returnData = eval("(" + v.ReturnData.replace(/"([^"]+)":/g, function (v) { return v.toLowerCase(); }) + ")");
//        	t = $.fn.zTree.init(t, setting, returnData.rows);
        	var returnData = eval("(" + data.replace(/"([^"]+)":/g, function (v) { return v.toLowerCase(); }) + ")");
        	t = $.fn.zTree.init(t, setting, returnData);
    		zTree = $.fn.zTree.getZTreeObj("ruleTree");
    		zTree.selectNode(zTree.getNodeByParam("id", 1));
        },Error: function (err) {
            ShowMessages(err, "友情提示", "msgwaring", null, 0);
        }
    });
	//加载表格数据
	$("#jqGrid").jqGrid({
        datatype: "json",
        url: "GetRuleCheck?pid=1",
      colNames: ['ID','是否可用','规则编号', 
                   '规则名称','规则命中条件','提示内容',
                   '规则执行命中率','开始有效时间','截至有效时间','规则类型','来源','创建人',
                   '创建时间','修改人','修改时间','版本号','更改历史','POINT_ID','RULE_HIT_EX'],
        colModel: [
					  { name: 'INDX', index: 'INDX', width: 100},
					  { name: 'IS_VALIDATE', index: 'IS_VALIDATE', width: 60,align: "center", formatter: stateshow},
					  { name: 'RULE_NO', index: 'RULE_NO', width: 80},
		              { name: 'RULE_NAME', index: 'RULE_NAME', width: 100},
		              { name: 'RULE_HIT_CON', index: 'RULE_HIT_CON', width: 150},
		              { name: 'WARNING_INFO', index: 'WARNING_INFO', width: 200},
		              { name: 'CHK_HIT_PERCENT', index: 'CHK_HIT_PERCENT',align: "center", width: 120},
		              { name: 'ACTIVE_TIME_BEGIN', index: 'ACTIVE_TIME_BEGIN', width: 140},
		              { name: 'ACTIVE_TIME_END', index: 'ACTIVE_TIME_END', width: 140},
		              { name: 'RULE_TYPE', index: 'RULE_TYPE', width: 80, formatter: showTypeState},
		              { name: 'SOURCE', index: 'SOURCE',align: "center", width: 100},
		              { name: 'CREATOR', index: 'CREATOR', width: 100},
		              { name: 'CREATE_TIME', index: 'CREATE_TIME', width: 100},
		              { name: 'MODIFYOR', index: 'MODIFYOR', width: 100},
		              { name: 'MODIFY_DATE', index: 'MODIFY_DATE', width: 100},
		              { name: 'REC_VER', index: 'REC_VER', width: 100},
		              { name: 'HISTORY', index: 'HISTORY', width: 100},
		              //CHK_HIT_PERCENT
		              { name: 'POINT_ID', index: 'POINT_ID', hidden:true},
		              { name: 'RULE_HIT_EX', index: 'RULE_HIT_EX', hidden:true}
       			 ],
        viewrecords: true, // show the current page, data rang and total records on the toolbar
        width: 200,
        //autowidth:auto,
        height: 180,
        rowNum: 15, //每页显示记录数
        //datatype: 'local',
        rowList: [100], //可调整每页显示的记录数
        rownumbers: true,
        rownumWidth: 35,
        multiselect: true, //是否支持多选 
        multiselectWidth: 35,
        viewrecords: true, //是否显示行数 
        //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
        pager: "#jqGridPager", //分页工具栏
        altRows: true,
        // loadonce : true,
        //caption: "**Data",
        sortable: false,
        shrinkToFit: false,
        autoScroll: true,
        onSelectRow: function(rowid, selected) {
			if(rowid != null) {
				var rowData = $("#jqGrid").jqGrid("getRowData",rowid);//根据上面的id获得本行的所有数据
				$("#INDX").val(rowData.INDX);
				$("#POINT_ID").val(rowData.POINT_ID);
				$("#REC_VER").val(rowData.REC_VER);
				$("#RULE_NO").val(rowData.RULE_NO);//规则编号
				$("#RULE_NAME").val(rowData.RULE_NAME);//规则名称
				$("#CHK_HIT_PERCENT").val(rowData.CHK_HIT_PERCENT);//查验命中率
				$("#WARNING_TYPE").val(rowData.WARNING_TYPE);//提示类型
				$("#WARNING_INFO").val(rowData.WARNING_INFO);//提示内容 
				$("#RULE_HIT_PRE").val(rowData.RULE_HIT_PRE); //规则命中前提
				$("#RULE_HIT_CON").val(rowData.RULE_HIT_CON); //规则命中条件
				
				$("#RULE_HIT_EX").val(rowData.RULE_HIT_EX); //规则命中条件
				$("#CHK_HIT_PERCENT").val(rowData.CHK_HIT_PERCENT); //规则命中条件
	              
			}					
		}
    });
    $("#jqGrid").setGridWidth($(window).width() -185);
	$("#jqGrid").setGridParam().hideCol("INDX").trigger("reloadGrid");
    $("#jqGrid").setGridParam().hideCol("POINT_ID").trigger("reloadGrid");
    $("#jqGrid").setGridParam().hideCol("REC_VER").trigger("reloadGrid");
});



function pre(type) {
	var rule_hit_pre = $("#RULE_HIT_CON").val();
    if(type == 'Gen'){
    	//var name = $("#COULMN_NAME").val();
        var name = document.querySelectorAll("[datafield='RULE_CHECK.COULMN_NAME']").item(0).value;
    	//var op = $("#OP_CHAR").val();
		var op = document.querySelectorAll("[datafield='RULE_CHECK.OP_CHAR_VALUE']").item(0).value;
    	var value = "";
		if(document.getElementById("OP_VALUE").style.display=="none")
		   value=$("#txtOP_VALUE").val();
		else
		   value=$("#OP_VALUE").val();
    	if(name != '' && op != '' && value != ''){
			if(parseInt(document.querySelectorAll("[datafield='RULE_CHECK.COULMN_VALUE']").item(0).value)<3)
    		 $("#RULE_HIT_CON").val(rule_hit_pre + " ["+name + "] "+op + " '"+value+"'");
			else
			 $("#RULE_HIT_CON").val(rule_hit_pre + " ["+name + "] "+op + " ["+value+"]");
    	}else{
    		
    	}
    }else if(type == 'And'){
    	$("#RULE_HIT_CON").val(rule_hit_pre + ' AND');
    }else if(type == 'Or'){
    	$("#RULE_HIT_CON").val(rule_hit_pre + ' OR');
    }
}

function pos(type) {
	var rule_hit_con = $("#RULE_HIT_CON").val();
    if(type == 'Gen'){
    	var name = document.querySelectorAll("[datafield='RULE_CHECK.COULMN_VALUE']").item(0).value;
		//$("#COULMN_NAME").val()
    	//var op = $("#OP_CHAR").val();
		var op = document.querySelectorAll("[datafield='RULE_CHECK.OP_CHAR_VALUE']").item(0).value;
    	var value = "";
		if(document.getElementById("OP_VALUE").style.display=="none")
		   value=$("#txtOP_VALUE").val();
		else
		   value=$("#OP_VALUE").val();
    	if(name != '' && op != '' && value != ''){
    		$("#RULE_HIT_CON").val(rule_hit_con + " ["+name + "] "+op + " '"+value+"'");
    	}else{
    		
    	}
    	
    }else if(type == 'And'){
    	$("#RULE_HIT_CON").val(rule_hit_con + ' AND');
    }else if(type == 'Or'){
    	$("#RULE_HIT_CON").val(rule_hit_con + ' OR');
    }
}

function Clear() {
	$("#INDX").val('');
	$("#REC_VER").val('');
	$("#RULE_NO").val('');//规则编号
	$("#RULE_NAME").val('');//规则名称
	$("#CHK_HIT_PERCENT").val('');//查验命中率
	$("#WARNING_TYPE").val('');//提示类型
	$("#WARNING_INFO").val('');//提示内容 
	$("#RULE_HIT_PRE").val(''); //规则命中前提
	$("#RULE_HIT_CON").val(''); //规则命中条件
	$("#COULMN_NAME").val(''); //字段名称
	$("#OP_CHAR").val(''); //运算符
	$("#OP_VALUE").val(''); //运算符
}

function setActiveTime() {
	$('#actimediv').show();
}
function comfirmAcTime() {
	var beginTime = $("#ACTIVE_TIME_BEGIN").val();
	var endTime = $("#ACTIVE_TIME_END").val();
	if(beginTime == "" && endTime == ""){
		ShowMessages("请您输入规则的开始有效日期、截至有效日期", "友情提示", "msgwaring", null, 0);
		return;
	}
	updateRows("UpdateRuleCheck?doMethod=4&beginTime="+beginTime+"&endTime="+endTime,"upAcTime");
}
function cancelAcTime() {
	$("#ACTIVE_TIME_BEGIN").val('');
	$("#ACTIVE_TIME_END").val('');
	$('#actimediv').hide();
}
function publishRows() {
	updateRows("UpdateRuleCheck?doMethod=3","publish");
}
function recoverRows() {
	updateRows("UpdateRuleCheck?doMethod=0","recover");
}
function cancelRows() {
	updateRows("UpdateRuleCheck?doMethod=1","cancel");
}
function updateRows(commandName,checkFlag) {
    var grid = $("#jqGrid");
    var rowKey = grid.getGridParam("selrow");
    if (!rowKey){
    	alert("请先选中数据！");
    } else {
        var rowids = grid.getGridParam("selarrrow");
        var indxs = "";
        for (var i = 0; i < rowids.length; i++) {
        	var rowData = grid.jqGrid("getRowData",rowids[i]);
        	if("cancel" == checkFlag && rowData.IS_VALIDATE == "1"){
        		continue;
        	}else if("recover" == checkFlag && rowData.IS_VALIDATE == "0"){
        		continue;
        	}else if("publish" == checkFlag && rowData.INS_SYN == "已同步"){
        		continue;
        	}
        	if("publish" == checkFlag && rowData.INS_SYN == "已发布"){
        		ShowMessages("选中规则包含已发布，请等待同步至电子审单系统。", "友情提示", "msgwaring", null, 0);
        		return;
        	}
        	if(indxs == ""){
        		indxs += rowData.INDX;
        	}else{
        		indxs += "," + rowData.INDX;
        	}
        }
        if(indxs == ""){
        	var msg = "";
        	if("cancel" == checkFlag && rowData.IS_VALIDATE == "1"){
        		msg = "您选中已作废的数据，无需作废。";
        	}else if("recover" == checkFlag && rowData.IS_VALIDATE == "0"){
        		msg = "您选中未作废的数据，无需恢复。";
        	}else if("publish" == checkFlag && rowData.INS_SYN == "已同步"){
        		msg = "您选中已发布的数据，无需重复发布。";
        	}
        	ShowMessages(msg, "友情提示", "msgwaring", null, 0);
        	$("#jqGrid").jqGrid('setGridParam', {
    	        url: "GetRuleCheck",
    	        postData: {
    	            'pid': $("#txtTableId").val()
    	        },
    	        page: 1
    	    }).trigger("reloadGrid"); 
    		return;
        }
        
        $.ajax({
            type: "post",
            async: false, //控制同步
            url: commandName,
            data: {
            	Indx:indxs
            },
            cache: false,
            success: function (data) {
            	var v = eval("(" + data + ")");
            	if (v.IsOk == "1") {
            		ShowMessages("操作成功！", "友情提示", "msgok", null, 0);
            		$("#jqGrid").jqGrid('setGridParam', {
            	        url: "GetRuleCheck",
            	        postData: {
            	            'pid': $("#txtTableId").val()
            	        },
            	        page: 1
            	    }).trigger("reloadGrid"); 
            	}else{
            		ShowMessages("操作失败！", "友情提示", "msgwaring", null, 0);
            	}
            },Error: function (err) {
                ShowMessages(err, "友情提示", "msgwaring", null, 0);
                return "-1";
            }
        });
    }                
}

function stateshow(cellvalue, options, rowObject) {
    var rt = "";
    switch (cellvalue) {
        case '0':
            rt += "无效";
            break;
        case '1':
            rt += "有效";
            break;
    }
    return rt;
}
function showTypeState(cellvalue, options, rowObject)
{
	var rt="";
    switch (cellvalue) {
        case 'S':
            rt= "系统";
            break;
        case 'U':
            rt= "自定义";
            break;
    }
	return rt;	
}
function insSyn(cellvalue, options, rowObject) {
    var rt = "";
    switch (cellvalue) {
        case '0':
            rt += "未发布";
            break;
        case '1':
            rt += "已发布";
            break;
        case '2':
            rt += "已同步";
            break;
    }
    return rt;
}

function getConditon() {
	return "{\"SearchTable\":[{\"TYPE\":\"tips\"}]}";
}

function getOpChar() {
	return "{\"SearchTable\":[{\"TYPE\":\"OpChar\"}]}";
}

function getRightType() {
	return "{\"SearchTable\":[{\"TYPE\":\"RightType\"}]}";
}

function getCoulmnName(pointCode){
	return "{\"SearchTable\":[{\"POINT_CODE\":\""+pointCode+"\"}]}";
}

function getRuleConfig(pointCode){
	return "{\"SearchTable\":[{\"type\":\""+pointCode+"\"}]}";
}

function getSelectKeys(whereValue){
	return "{\"SearchTable\":[{"+whereValue+"}]}";
}

function savaRule(){
	DoCommand('/SavaRuleCheck','editTable');
}

//表头处理返回值
function showSuccess(s) {
    var v = eval("(" + s + ")");
    if (parseInt(v.IsOk) ==1) {
    	ShowMessages(v.ErrMessage, "系统提示", "msgok", function okFunction(){
			$("#jqGrid").jqGrid('setGridParam', {
            	url: "GetRuleCheck",
                postData: {
                    'pid': $("#POINT_ID").val()
                },
                page: 1
            }).trigger("reloadGrid"); 
			  ClearData("RuleData");
			});
    }else {
        DataReturns(s);
    }
}
function getSelectValue()
{ 												 
	switch(parseInt(document.querySelectorAll("[datafield='RULE_CHECK.RIGHT_TYPE_VALUE']").item(0).value))
	{
		case 1:
			$.ajax({
			  type:'POST',
			  url:'getMapping',
			  async: false, //控制同步
			  data:{
				  Indx:document.querySelectorAll("[datafield='RULE_CHECK.COULMN_VALUE']").item(0).value
			  },
			  success:function(data)
			  {
				  var showData=eval('('+data+')');
				  var showRsultData=eval('('+showData.ReturnData+')');
					  if(showRsultData.mapData[0].CSELECT_FLAG=="Y")
						{
							$("#rightTd").removeClass("tdSelectCss").addClass("tdCss");
							if(document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.className=="combobox")
							    document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.style.display="block";
						  $("#OP_VALUE").show();
						  $("#txtOP_VALUE").hide();
						  if(document.getElementById("OP_VALUE").getAttribute("type")=="combobox")
						  {
							window["OP_VALUE"].DataSourceName=showRsultData.mapData[0].CSELECT_TABLE_NAME;
							window["OP_VALUE"].datafield="RULE_CHECK."+showRsultData.mapData[0].FIELD_CODE;
							window["OP_VALUE"].valuefield="RULE_CHECK."+showRsultData.mapData[0].FIELD_CODE;

							window["OP_VALUE"].ColumnHeaders=showRsultData.mapData[0].CSELECT_TITLE_NAME.split(',');
							window["OP_VALUE"].ColumnsValue=showRsultData.mapData[0].FIELD_CODE;
							window["OP_VALUE"].Columns=showRsultData.mapData[0].CSELECT_FIELD_CODE.split(',');
							window["OP_VALUE"].CheckField=showRsultData.mapData[0].FIELD_CODE;
							window["OP_VALUE"].ShowFields=showRsultData.mapData[0].FIELD_CODE;
							window["OP_VALUE"].Query="{\"SearchTable\":[{"+showRsultData.mapData[0].CSELECT_CONDITION.replaceAll("'","\\\"")+"}]}";
						  }
						  else
						  {
							
							document.getElementById("OP_VALUE").setAttribute("datafield","RULE_CHECK."+showRsultData.mapData[0].FIELD_CODE);
							document.getElementById("OP_VALUE").setAttribute("valuefield","RULE_CHECK."+showRsultData.mapData[0].FIELD_CODE);
							document.getElementById("OP_VALUE").setAttribute("datasourcename",showRsultData.mapData[0].CSELECT_TABLE_NAME);
							document.getElementById("OP_VALUE").setAttribute("columnheader",showRsultData.mapData[0].CSELECT_TITLE_NAME);
							document.getElementById("OP_VALUE").setAttribute("columns",showRsultData.mapData[0].CSELECT_FIELD_CODE);
							document.getElementById("OP_VALUE").setAttribute("checkfield",showRsultData.mapData[0].CSELECT_CHECK_FIELD);
							document.getElementById("OP_VALUE").setAttribute("showfields",showRsultData.mapData[0].FIELD_CODE);
						    document.getElementById("OP_VALUE").setAttribute("GetQueryData","getSelectKeys('"+showRsultData.mapData[0].CSELECT_CONDITION.replaceAll("'","\\\"")+"')");
							document.getElementById("OP_VALUE").setAttribute("type","combobox");
							var selectTd=new ComboBox(document.getElementById("OP_VALUE"));
							window["OP_VALUE"]=selectTd;
							
						  }	
						}
						else
						{
							$("#rightTd").removeClass("tdSelectCss").addClass("tdCss");
							$("#txtOP_VALUE").show();
							if(document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.className=="combobox")
							    document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.style.display="none";
							$("#OP_VALUE").hidden();
							
						}
				  
				  
			  }
			})
		  break;
		case 2:
		$("#rightTd").removeClass("tdSelectCss").addClass("tdCss");
		$("#txtOP_VALUE").hide();
		$("#OP_VALUE").val("");
		  if(document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.className=="combobox")
			  document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.style.display="block";
			  if(document.getElementById("OP_VALUE").getAttribute("type")=="combobox")
			  {
				window["OP_VALUE"].DataSourceName="RULE_T_ALIAS";
				window["OP_VALUE"].ColumnHeaders="别名".split(',');
				window["OP_VALUE"].ColumnsValue="PROCESS_NAME";
				window["OP_VALUE"].Columns="PROCESS_NAME".split(',');
				window["OP_VALUE"].CheckField="PROCESS_NAME";
				window["OP_VALUE"].ShowFields="PROCESS_NAME";
				window["OP_VALUE"].datafield="RULE_CHECK.PROCESS_NAME";

				window["OP_VALUE"].ValueField="RULE_CHECK.PROCESS_CODE";
				
				window["OP_VALUE"].ValueField="INDX";
				window["OP_VALUE"].SaveField="INDX";
				window["OP_VALUE"].Query="";
				$("#OP_VALUE").show();
			  }
			  else
			  {
				document.getElementById("OP_VALUE").setAttribute("datasourcename","RULE_T_ALIAS");
				document.getElementById("OP_VALUE").setAttribute("columnheader","别名");
				document.getElementById("OP_VALUE").setAttribute("datafield","RULE_CHECK.PROCESS_NAME");	
				document.getElementById("OP_VALUE").setAttribute("ValueField","RULE_CHECK.PROCESS_CODE");
				document.getElementById("OP_VALUE").setAttribute("SaveField","INDX");
				document.getElementById("OP_VALUE").setAttribute("columns","PROCESS_NAME");
				document.getElementById("OP_VALUE").setAttribute("checkfield","PROCESS_NAME");
				document.getElementById("OP_VALUE").setAttribute("showfields","PROCESS_NAME");
			    document.getElementById("OP_VALUE").setAttribute("GetQueryData","");
				document.getElementById("OP_VALUE").setAttribute("type","combobox");
				window["OP_VALUE"]=new ComboBox(document.getElementById("OP_VALUE"));
				$("#OP_VALUE").show();
			  }	   
		break;
		case 3:
		$("#rightTd").removeClass("tdSelectCss").addClass("tdCss");
		$("#txtOP_VALUE").hide();
		$("#OP_VALUE").val("");	
		  if(document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.className=="combobox")
			  document.getElementById("OP_VALUE").parentElement.parentElement.parentElement.style.display="block";											
			  if(document.getElementById("OP_VALUE").getAttribute("type")=="combobox")
			  {
				window["OP_VALUE"].DataSourceName="RULE_B_MAPPING";
				window["OP_VALUE"].ColumnHeaders="编号,名称".split(',');
				window["OP_VALUE"].ColumnsValue="FIELD_CODE,FIELD_NAME";
				window["OP_VALUE"].Columns="FIELD_CODE,FIELD_NAME".split(',');
				window["OP_VALUE"].CheckField="FIELD_CODE,FIELD_NAME";
				window["OP_VALUE"].ShowFields="FIELD_NAME";
				window["OP_VALUE"].datafield="RULE_CHECK.PROCESS_NAME";

				window["OP_VALUE"].ValueField="RULE_CHECK.PROCESS_CODE";
				window["OP_VALUE"].SaveField="INDX";
				window["OP_VALUE"].Query="";
				$("#OP_VALUE").show();
			  }
			  else
			  {
				document.getElementById("OP_VALUE").setAttribute("datasourcename","RULE_B_MAPPING");
				document.getElementById("OP_VALUE").setAttribute("columnheader","编号,名称");
				document.getElementById("OP_VALUE").setAttribute("datafield","RULE_CHECK.PROCESS_NAME");	
				document.getElementById("OP_VALUE").setAttribute("ValueField","RULE_CHECK.PROCESS_CODE");
				document.getElementById("OP_VALUE").setAttribute("SaveField","INDX");
				document.getElementById("OP_VALUE").setAttribute("columns","FIELD_CODE,FIELD_NAME");
				document.getElementById("OP_VALUE").setAttribute("checkfield","FIELD_CODE,FIELD_NAME");
				document.getElementById("OP_VALUE").setAttribute("showfields","FIELD_NAME");
			    document.getElementById("OP_VALUE").setAttribute("GetQueryData","getCoulmnName('"+$("#txtPointCode").val()+"')");
				document.getElementById("OP_VALUE").setAttribute("type","combobox");
				window["OP_VALUE"]=new ComboBox(document.getElementById("OP_VALUE"));
				$("#OP_VALUE").show();
			  }	
		break;
	}		
}