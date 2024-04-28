function selectOrgNode(id){
	$("#POINT_CODE").val(id);
	window["TABLE_NAME"].Query=getRuleConfig2(""+id+"");
	
}
function getRuleConfig(pointCode){
	return "{\"SearchTable\":[{\"type\":\""+pointCode+"\"}]}";
}
function getOpChar() {
	return "{\"SearchTable\":[{\"TYPE\":\"OpChar\"}]}";
}
function getFieldType() {
	return "{\"SearchTable\":[{\"TYPE\":\"FieldType\"}]}";
}
function getIsChinese() {
	return "{\"SearchTable\":[{\"TYPE\":\"IsChinese\"}]}";
}
function getRuleConfig2(pointCode){
	if(parent.$("#webEditMode").val()){
		return "{\"SearchTable\":[{\"type\":\""+pointCode+"\",\"APP_REC_VER\":\""+parent.$("#webEditMode").val()+"\"}]}";
	}else{
		return "{\"SearchTable\":[{\"type\":\""+pointCode+"\"}]}";
	}
}

function InitPage()
{
  	if(GetRequestName("point_Code")!="")
	{
  		pid=GetRequestName("point_Code");
		selectOrgNode(pid);
	}	
}

$(document).ready(function(){
	//加载表格数据
	$("#jqGrid").jqGrid({
	    datatype: "json",
	    url: "GetField?point_Code="+GetRequestName("point_Code"),
	    postData: {
	        'CommandData': FormToData("SearchTable")
	    }, //发送数据 
	    colNames: ['操作','业务数据表','规则编号','字段名称','字段描述','字段类型','是否必填','长度限制','长度起始值','长度上限值','行列限制','行*列','字段类型限制',
	               '运算符','操作值代码','操作值名称','创建人','创建时间','INDX','ROW_TOTAL','COL_TOTAL','POINT_CODE'],
	    colModel: [
					  { name: '', index: '',align: "center", width: 60, formatter: managefun},
					  { name: 'TABLE_NAME', index: 'TABLE_NAME', width: 120},
		              { name: 'RULE_NO', index: 'RULE_NO', width: 100},
		              { name: 'FIELD_CODE', index: 'FIELD_CODE',align: "center", width: 150},
		              { name: 'FIELD_NAME', index: 'FIELD_NAME', width: 150},
		              { name: 'FIELD_DATA_TYPE', index: 'FIELD_DATA_TYPE', width: 100},
		              { name: 'IS_MUSTINPUT', index: 'IS_MUSTINPUT', width: 100},
		              { name: 'IS_CONTROL_TOTAL', index: 'IS_CONTROL_TOTAL', width: 100},
		              { name: 'CHAR_TOTAL_START', index: 'CHAR_TOTAL_START', width: 100},
		              { name: 'CHAR_TOTAL', index: 'CHAR_TOTAL', width: 100},
		              { name: 'IS_CONTROL_ROW', index: 'IS_CONTROL_ROW', width: 100},
		              { name: 'ROWCOL', index: 'ROWCOL', width: 100},
		              { name: 'IS_CHINESE_NAME', index: 'IS_CHINESE_NAME', width: 100},
		              { name: 'OPERATOR', index: 'OPERATOR', width: 100},
		              { name: 'FIELD_VALUE', index: 'FIELD_VALUE', width: 100},
		              { name: 'FIELD_VALUE_CN', index: 'FIELD_VALUE_CN', width: 100},
		              { name: 'CREATOR', index: 'CREATOR', width: 100},
		              { name: 'CREATE_TIME', index: 'CREATE_TIME', width: 100},
		              { name: 'INDX', index: 'INDX', hidden:true},
		              { name: 'ROW_TOTAL', index: 'ROW_TOTAL', hidden:true},
		              { name: 'COL_TOTAL', index: 'COL_TOTAL', hidden:true},
		              { name: 'POINT_CODE', index: 'POINT_CODE', hidden:true}
	   			 ],

	   	        viewrecords: true, // 是否显示行数
	   	        width: 'auto',
	   		    height: 300,
	   		    rowNum: 10, //每页显示记录数
	   		    rowList: [10,20,50], //可调整每页显示的记录数
	   		    autowidth:true,
	   	        shrinkToFit: false,
	   	        autoScroll: false,
	   	        rowNum: 15, //每页显示记录数
	   	        //datatype: 'local',
	   	        rowList: [15, 50, 100], //可调整每页显示的记录数
	   	        rownumbers: true,
	   	        rownumWidth: 35,
	   		    multiselect: false, //是否支持多选 
	   		    multiselectWidth: 35,
	   		    pager: "#jqGridPager", //分页工具栏
	   	        loadui: "none", //隐藏默认的loading  
	   	        altRows: true,
	   	        altclass: 'someClass',
	   	        sortable: true,
	   	        //排序开始
	   	        onSortCol: function (colName, Id, sortorder) {
	   	            $("#jqGrid").jqGrid('setGridParam', {
	   	                url: "GetDeclList"+window.location.search,
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
	   	        },
			    onSelectRow: function(rowid, selected) {
					if(rowid != null) {
						$("#txtRowId").val(rowid);
						var rowData = $("#jqGrid").jqGrid("getRowData",rowid);//根据上面的id获得本行的所有数据
						$("#INDX").val(rowData.INDX);
						$("#POINT_CODE").val(rowData.POINT_CODE);
						$("#TABLE_NAME").val(rowData.TABLE_NAME);//业务数据表
						$("#FIELD_CODE").val(rowData.FIELD_CODE);//字段名称
						$("#FIELD_NAME").val(rowData.FIELD_NAME);//字段描述
						$("#FIELD_DATA_TYPE").val(rowData.FIELD_DATA_TYPE);//字段类型
						$("#RULE_NO").val(rowData.RULE_NO);//规则编号
						$("#IS_MUSTINPUT").val(rowData.IS_MUSTINPUT);//字段格式
						if(rowData.IS_MUSTINPUT=="Y"){
							$("#IS_MUSTINPUT").prop('checked', true);
							$("#IS_MUSTINPUT").attr('checked', true);
							document.getElementById("IS_MUSTINPUT").parentNode.parentNode.className = "check-box checkedBox";
						}else{
							$("#IS_MUSTINPUT").prop('checked', false);
							$("#IS_MUSTINPUT").attr('checked', false);
							document.getElementById("IS_MUSTINPUT").parentNode.parentNode.className = "check-box";
						}
						$("#IS_CONTROL_ROW").val(rowData.IS_CONTROL_ROW);//行*列
						if(rowData.IS_CONTROL_ROW=="Y"){
							$("#IS_CONTROL_ROW").prop('checked', true);
							$("#IS_CONTROL_ROW").attr('checked', true);
							document.getElementById("IS_CONTROL_ROW").parentNode.parentNode.className = "check-box checkedBox";
						}else{
							$("#IS_CONTROL_ROW").prop('checked', false);
							$("#IS_CONTROL_ROW").attr('checked', false);
							document.getElementById("IS_CONTROL_ROW").parentNode.parentNode.className = "check-box";
						}
						$("#ROW_TOTAL").val(rowData.ROW_TOTAL);//行*列
						$("#COL_TOTAL").val(rowData.COL_TOTAL);//行*列
						$("#IS_CONTROL_TOTAL").val(rowData.IS_CONTROL_TOTAL);//总长度
						if(rowData.IS_CONTROL_TOTAL=="Y"){
							$("#IS_CONTROL_TOTAL").prop('checked', true);
							$("#IS_CONTROL_TOTAL").attr('checked', true);
							document.getElementById("IS_CONTROL_TOTAL").parentNode.parentNode.className = "check-box checkedBox";
						}else{
							$("#IS_CONTROL_TOTAL").prop('checked', false);
							$("#IS_CONTROL_TOTAL").attr('checked', false);
							document.getElementById("IS_CONTROL_TOTAL").parentNode.parentNode.className = "check-box";
						}
						$("#CHAR_TOTAL_START").val(rowData.CHAR_TOTAL_START);//总长度
						$("#CHAR_TOTAL").val(rowData.CHAR_TOTAL);//总长度
						$("#IS_CHINESE_NAME").val(rowData.IS_CHINESE_NAME);//字段类型限制
						$("#OPERATOR").val(rowData.OPERATOR);//运算符
						$("#FIELD_VALUE").val(rowData.FIELD_VALUE);//操作值代码
						$("#FIELD_VALUE_CN").val(rowData.FIELD_VALUE_CN);//操作值名称
						
					}					
				}
	});
	$("#jqGrid").setGridWidth($(window).width());
	
    $("#btnSearch").click(function () {
        $("#jqGrid").jqGrid('setGridParam', {
            url: "GetField"+window.location.search,
            postData: {
                'CommandData': FormToData('SearchTable')
            }, //发送数据 
            page: 1
        }).trigger("reloadGrid"); //重新载入 
    });
});


function managefun(cellvalue, options, rowObject) {
	return "<a class=\"btn btn-danger btn-mini\" href=\"#\" onclick=\"DelField(" + rowObject.INDX + ")\" style=\"color:#fff;\"><i class=\"icon-remove\"></i>删除</a>";
}
function DelField(index) {
    ShowMessages("是否确定删除?", "系统提示", "msgquestion", function okFunction() {
        DoCommand("DeleteField?Indx=" + index);
        $("#jqGrid").trigger("reloadGrid");
        ClearData("RULE_CHECK");
        $("#btnAdd").click();
    }, 1);
}

//表头处理返回值
function showSuccess(s) {
  var v = eval("(" + s + ")");
  if (parseInt(v.IsOk) ==1) {
  	ShowMessages(v.ErrMessage, "系统提示", "msgok", function okFunction(){
			$("#jqGrid").jqGrid('setGridParam', {
          	url: "GetField"+window.location.search,
            page: 1
          }).trigger("reloadGrid"); 
			//ClearData("RULE_CHECK");
	});
  }else {
      DataReturns(s);
  }
}