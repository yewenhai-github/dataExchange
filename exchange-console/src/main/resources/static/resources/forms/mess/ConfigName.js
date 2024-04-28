var Configname = "";
$(document).ready(function() {
	initDataGrid();
})
var lastsel;
function initDataGrid() {
	$("#corptable")
			.jqGrid(
					{
						datatype : "json",
						url : "ConfigNameByIndx?INDX=" + GetRequestName("indx"),// ,'操作'
						colNames : [ '外键', '序列', '源层', '源节点名', '目标节点名','层级','节点描述', '节点属性', '是否必填', '固定默认值', '目标节点属性 ', '配置默认值', '生成默认值', '备注 ', '操作' ],
							/*
							  [ '外键', '序列', '源层', '源节点名', '目标节点层',
								'目标节点名', '固定默认值', '目标节点子表', '目标节点属性 ', '目标节点必填',
								'节点描述', '配置默认值', '生成默认值', '备注 ', '操作' ],
							  
							 */
								
								
								
						colModel : [ {
							name : 'P_INDX',
							index : 'P_INDX',
							width : 220,
							align : 'center',
							hidden : true
							//外键
						}, {
							name : 'SEQ',
							index : 'SEQ',
							width : 45,
							align : 'center'
								//序列
						}, {
							name : 'SOURCEFILEFLOOR',
							index : 'SOURCEFILEFLOOR',
							width : 65,
							align : 'center',
							hidden : true
							//源层
						}, {
							name : 'SOURCENOTENAME',
							index : 'SOURCENOTENAME',
							width : 190,
							align : 'center',
							editable: true,
							edittype: 'text'
							//源节点名
						}, {
							name : 'TARGETCOLNAME',
							index : 'TARGETCOLNAME',
							width : 190,
							align : 'center'
								//目标节点名
						}, {
							name : 'TARGETFILEFLOOR',
							index : 'TARGETFILEFLOOR',
							width : 40,
							align : 'center'
								//层级
						}, {
							name : 'CN_REMAKE',
							index : 'CN_REMAKE',
							width : 100,
							align : 'center'
								//节点描述
						}, {
							name : 'ISSUBLISTNAME',
							index : 'ISSUBLISTNAME',
							width : 65,
							align : 'center'
								//节点属性
						}, {
							name : 'TARGETNOTEISINPUT',
							index : 'TARGETNOTEISINPUT',
							width : 65,
							align : 'center'
								//目标节点必填
						}, {
							name : 'DEFVALUE',
							index : 'DEFVALUE',
							width : 75,
							align : 'center'
								//固定默认值
						}, {
							name : 'TARGETNOTEATTRIBUTE',
							index : 'TARGETNOTEATTRIBUTE',
							width : 90,
							align : 'center',
							hidden : true
							//目标节点属性
						}, {
							name : 'MAPPING',
							index : 'MAPPING',
							width : 90,
							align : 'center'
								//配置默认值
						}, {
							name : 'FUNCTION',
							index : 'FUNCTION',
							width : 90,
							align : 'center'
								//生成默认值
						}, {
							name : 'REMARK',
							index : 'REMARK',
							width : 50,
							align : 'center'
								//备注
						}, {
							name : 'INDX',
							index : 'INDX',
							width : 100,
							align : 'center',
							hidden : true
							//操作

						} ],
						
						viewrecords : true, // window.screen.height - 405
						height : 500,
						autowidth : true,
						rowNum : 50, // 每页显示记录数
						rowList : [ 50, 100, 150 ], // 可调整每页显示的记录数
						rownumbers : true,
						rownumWidth : 25,
						multiselect : true, // 是否支持多选
						multiselectWidth : 35, // 是否支持多选
						viewrecords : true, // 是否显示行数
						pager : "#jqGridPager", // 分页工具栏
						altRows : true,
						altclass : 'someClass',
						sortable : true,
						ondblClickRow :function (rowid){
							if(rowid && rowid!==lastsel){
								$("#corptable").jqGrid("restoreRow",lastsel);
								$("#corptable").editRow(rowid,{
									 keys : true,        
									 url: "EditConfigName",  
									 mtype : "POST",  
						             restoreAfterError: true,  
						             extraparam: { 
						            	 "INDX":$("#corptable").getRowData(rowid).INDX,
						            	 //"SOURCEFILEFLOOR":$("#"+rowid+"_SOURCEFILEFLOOR").val(),
						            	 "SOURCENOTENAME":$("#"+rowid+"_SOURCENOTENAME").val()
						             }, 
							         oneditfunc: function(rowid){  
						                console.log(rowid);  
							         },
							         succesfunc: function(response){  
						                console.log("save success");  
						                return true;  
					            	},  
						            errorfunc: function(rowid, res){  
						                console.log(rowid);  
						                console.log(res);  
						            } 
								});
								lastsel=rowid;
							}
							
						},
						onSelectRow : function(rowid, status, e) {
							DoCommand("EditConfigName?INDX="
									+ $("#corptable").getRowData(rowid).INDX);
							$("#SEQ").attr("readonly","readonly");
						}, // 选中当前行事件
						// 水平滚动条
						loadui : "none",
						shrinkToFit : false,
						autoScroll : true,
						loadComplete : function() {
							var rowNum = $("#corptable").jqGrid('getGridParam',
									'records');
							if (rowNum <= 0) {
								if ($("#norecords").html() == null)
									$("#corptable")
											.parent()
											.append(
													"<div id=\"norecords\" class=\"norecords\">暂无数据！</div>");
								$("#norecords").show();
							} else {// 如果存在记录，则隐藏提示信息。
								$("#norecords").hide();
							}
						},
						beforeRequest : function() {
							if (top.showloading) {
								top.showloading();
							}
						},
						gridComplete : function() {
							if (top.hideloading) {
								top.hideloading();
							}
						}
					});
	

}
// 刷新
function refreshGrid() { 
	$("#SEQ").val(""); 
	$("#SOURCEFILEFLOOR").val(""); 
	$("#SOURCENOTE").val(""); 
	$("#TARGETFILEFLOOR").val(""); 
	$("#TARGETCOL").val(""); 
	$("#DEFVALUE").val(""); 
	$("#ISSUBLIST").val(""); 
	$("#TARGETNOTEATTRIBUTE").val(""); 
	$("#ERRORPATH").val(""); 
	$("#TARGETNOTEISINPUT").val(""); 
	$("#MAPPING").val(""); 
	$("#TARGETFILEPATH").val("");
	$("#SENDUSERNAME").val("");
	$("#SENDADRESS").val("");
	$("#SENDPATH").val("");
	$("#SENDUSERPWD").val("");
	$("#RECEIVEADRESS").val("");
	$("#FUNCTION").val("");
	$(".jlu").val("");
	$("#BACKPATH").val("");
	$(".yuanxmls").val("");
	$("#CONFIGNAME").val("");
	// $("#P_INDX").val("");
	$(".INDX").val("");
	$("#corptable").jqGrid().trigger("reloadGrid");
}
// 保存
function showConfigSuccess(parameters) {
	var data = $.parseJSON(parameters);
	if (data.IsOk == "1") { 
		ShowMessages("添加成功", "系统提示", "msgok", null, 0); 
		refreshGrid(); 
	} else {
		ShowMessages(data.ErrMessage, "系统提示", "msgwaring", null, 0);
	}

}


function doFileChange(){
	var formData = new FormData();
	//注意顺序  调整顺序导致后台数据异常
	formData.append(GetRequestName("indx"), GetRequestName("indx"));   
    formData.append("myfile", document.getElementById("file").files[0]);   
    $.ajax({
        url: "uploadXml",
        type: "POST",
        data: formData,
        contentType: false,
        cache:false,
        processData: false,
        success: function (data) {
        	successXmlData(data);
        },
        error: function () {
            alert("上传失败！");
        }
    });
    return;
};

function successXmlData(data){
	var obj = eval("("+data+")");
	if(obj.IsOk && obj.IsOk==1){
		ShowMessages(obj.ErrMessage, "系统提示", "msgok", function okFunction(){
			location.reload();	
		}, 0);	
	}else{
		ShowMessages(obj.ErrMessage, "系统提示", "msgwaring", function okFunction(){
			location.reload();
		}, 0);	
		
	}
}

// 保存外键
$(function() {
	$("#INDX").val("");
	$("#P_INDX").val(GetRequestName("indx"));
})

// 批量删除
function idsDel() {
	var array = new Array();
	var grid = $("#corptable");
	var rowKey = grid.getGridParam("selrow");
	var IsAll = 0;
	if ($("#txtSelectAll").is(":checked") == true)
		IsAll = 1;
	if (!rowKey && IsAll == 0)
		ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring", null, 0);
	else {
		var selectedIDs = grid.getGridParam("selarrrow");
		var result = "";
		for (var i = 0; i < selectedIDs.length; i++) {
			var rowData = grid.jqGrid("getRowData", selectedIDs[i]);
			array.push(rowData.INDX);
			 
		}

		// 跳转到批量删除路径
		dispathServerAjaxPost("DeletesConfigName?ids=" + array.toString(), "",
				dels);
	}
}
// 批量删除
function dels(parameters) {
	var data = $.parseJSON(parameters);
	if (data.IsOk == "1") {
		ShowMessages("删除成功", "系统提示", "msgok", null, 0);
		refreshGrid();
	} else {
		ShowMessages("删除失败", "系统提示", "msgwaring", null, 0);
		refreshGrid();
	}
}

// 删除
function del() {   
	dispathServerAjaxPost("DeleteConfigName?id=" + $("#INDX").val(), "", dele)
}

function dele(parameters) {
	var data = $.parseJSON(parameters);
	if (data.IsOk == "1") {
		ShowMessages("删除成功", "系统提示", "msgok", null, 0);
		$("#SEQ").val(""); 
		$("#SOURCEFILEFLOOR").val(""); 
		$("#SOURCENOTE").val(""); 
		$("#TARGETFILEFLOOR").val(""); 
		$("#TARGETCOL").val(""); 
		$("#DEFVALUE").val(""); 
		$("#ISSUBLIST").val(""); 
		$("#TARGETNOTEATTRIBUTE").val(""); 
		$("#ERRORPATH").val(""); 
		$("#TARGETNOTEISINPUT").val(""); 
		$("#MAPPING").val(""); 
		$("#TARGETFILEPATH").val("");
		$("#SENDUSERNAME").val("");
		$("#SENDADRESS").val("");
		$("#SENDPATH").val("");
		$("#SENDUSERPWD").val("");
		$("#RECEIVEADRESS").val("");
		$("#FUNCTION").val("");
		$(".jlu").val("");
		$("#BACKPATH").val("");
		$(".yuanxmls").val("");
		$("#CONFIGNAME").val("");
		// $("#P_INDX").val("");
		$(".INDX").val("");
		refreshGrid();
	} else {
		ShowMessages("删除失败", "系统提示", "msgwaring", null, 0);
	}
}

function news() {
	$("#SEQ").val(""); 
	$("#SOURCEFILEFLOOR").val(""); 
	$("#SOURCENOTENAME").val(""); 
	$("#TARGETFILEFLOOR").val(""); 
	$("#TARGETCOLNAME").val(""); 
	$("#TARGETCOL").val(""); 
	$("#DEFVALUE").val(""); 
	$("#CN_REMAKE").val(""); 
	$("#ISSUBLIST").val(""); 
	$("#TARGETNOTEATTRIBUTE").val(""); 
	$("#ERRORPATH").val(""); 
	$("#TARGETNOTEISINPUT").val(""); 
	$("#MAPPING").val(""); 
	$("#TARGETFILEPATH").val("");
	$("#SENDUSERNAME").val("");
	$("#SENDADRESS").val("");
	$("#SENDPATH").val("");
	$("#SENDUSERPWD").val("");
	$("#RECEIVEADRESS").val("");
	$("#FUNCTION").val("");
	$(".jlu").val("");
	$("#BACKPATH").val("");
	$(".yuanxmls").val("");
	$("#CONFIGNAME").val("");
	$("#INDX").val("");
	$("#SEQ").attr("readonly",false);

}



function Templetclick(){
	var INDX = GetRequestName("indx");
	if(INDX == ""){
		ShowMessages("数据为空", "系统提示", "msgwaring", null, 0);
		return;
	}
	layer.open({
		  type: 2,
		  area: ['700px', '550px'],
		  fixed: true, //不固定
		  maxmin: false,
		  content: 'Templet.html?indx='+INDX
	});
}



