//加载下拉数据源
function getConditon() {
	return "{\"SearchTable\":[{\"TYPE\":\"=COMMON\"}]}";
}
//grid宽高自适应
var GridAutoSize = function(){    	    	 
	 setGridSize("#jg",168);
}
$(document).ready(function () {
	window.onresize = function () {
		setGridSize("#jg",168);
	}
	var defaultWhere="{\"SearchTable\":[{\"IS_ENABLED\":\"1\"}]}"
	$("#jg").jqGrid({
		datatype: "json",
		url: "GetAccessPassList", 
		postData: {
			'CommandData':defaultWhere
		}, //发送数据 
		colNames: ['Schema','INDX','接入方名称','通道类型','TOKEN','消息编号标准','消息有效期至','消息类型','发送方代码','接收方代码','是否有效 ','创建时间','版本号'],
		colModel: [
				        { name: 'SCHEMA', index: 'SCHEMA', width: 100 },
				        { name: 'INDX', index: 'INDX', width: 50 },
				        { name: 'ACCESS_NAME', index: 'ACCESS_NAME', width: 100 },
				        { name: 'ACCESS_TYPE', index: 'ACCESS_TYPE', width: 100 },
				        { name: 'ACCESS_TOKEN', index: 'ACCESS_TOKEN', width: 100 },
				        { name: 'MESSAGE_ID', index: 'MESSAGE_ID', width: 100 },
				        { name: 'MESSAGE_TIME', index: 'MESSAGE_TIME', width: 100 },
				        { name: 'MESSAGE_TYPE', index: 'MESSAGE_TYPE', width: 100 },
				        { name: 'MESSAGE_SOURCE', index: 'MESSAGE_SOURCE', width: 100 },
				        { name: 'MESSAGE_DEST', index: 'MESSAGE_DEST', width: 100 },
						{ name: 'IS_ENABLED', index: 'IS_ENABLED', width: 100 },
						{ name: 'CREATE_TIME', index: 'CREATE_TIME', width: 150 , formatter: "date", formatoptions: { srcformat: 'Y-m-d H:i:s', newformat: 'Y-m-d H:i:s'} },
						{ name: 'REC_VER', index: 'REC_VER', width: 50 }
        	        ],
					viewrecords: true, 
		height: 450,
		rowNum: 15, //每页显示记录数
		//datatype: 'local',
		rowList: [15, 50, 100], //可调整每页显示的记录数
		rownumbers: true,
		rownumWidth: 25,
		multiselect: true, //是否支持多选 
		multiselectWidth: 35, //是否支持多选 
		viewrecords: true, //是否显示行数 
		//shrinkToFit: false, //
		pager: "#jqGridPager", //分页工具栏
		altRows: true,
		altclass: 'someClass',
		// loadonce : true,
		//caption: "**Data",
		sortable: true,
		//水平滚动条
		loadui: "none",
		shrinkToFit: false,
		autoScroll: true,
		//排序开始
		onSortCol: function (colName, Id, sortorder) {
			//colName   排序字段
			//Id   第几列
			//sortorder  排序  desc 倒序  asc 正序
			$("#jg").jqGrid('setGridParam', {
				url: "GetAccessPassList" ,
				postData: {
					'OrderField': colName,
					'Order': sortorder
				}, //发送数据 
				page: 1
			}).trigger("reloadGrid"); //重新载入 
		},
		loadComplete: function () {
			var rowNum = $("#jg").jqGrid('getGridParam', 'records');
			if (rowNum <= 0) {
				if ($("#norecords").html() == null)
					$("#jg").parent().append("<div id=\"norecords\" class=\"norecords\">暂无数据！</div>");
				$("#norecords").show();
			}
			else {//如果存在记录，则隐藏提示信息。
				$("#norecords").hide();
			}
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
		}
	});
		
	setGridSize("#jg",168);
	//显示某一列
	//$("#jg").setGridParam().showCol("colname").trigger("reloadGrid");
	//隐藏某一列
	$("#jg").setGridParam().hideCol("INDX").trigger("reloadGrid");
	//查询
	$("#btnSearch").click(function () {
		$("#jg").jqGrid('setGridParam', {
			url: "GetAccessPassList",
			postData: {
				'CommandData': FormToData('SearchTable')
			}, //发送数据 
			page: 1
		}).trigger("reloadGrid"); //重新载入 

	})
});
function urlCode(cellvalue, options, rowObject) {
        return "<a href=\"GetHTML?AccessPassEdit.html?INDX=" + rowObject.INDX + "&CELLVALUE=" +cellvalue+ "\"><span style=\"color:#00acec\">" + cellvalue + "</a>"
    
}
function openUpdate(){
	 var ids=$('#jg').jqGrid('getGridParam','selarrrow');
	 if (ids != null && ids.length != 0) {
		 if(ids.length==1){
	            for(var i=0;i<ids.length;i++){
	                var id = ids[i];
	                var row = $("#jg").jqGrid("getRowData",id);
	                var INDX = $.trim(row.INDX);
	                location.href='GetHTML?AccessPassEdit.html?INDX=' + INDX;
	            }
		 }else{
			 alert("只能修改一条数据");
		 }
	 }else{
		 alert("请先勾选一条需要修改的单号");
	 }
}
function openChange(type){
	var ids=$('#jg').jqGrid('getGridParam','selarrrow');
	var action = '';
    var text = '';
    if (type == 'btnDle') {
        action = 'btnDle';
        text = '失效';
    } else if (type == "btnRecovery") {
        action = 'btnRecovery';
        text = '恢复';
    }
	var jsonArr = new Array();
	 if (ids != null && ids.length != 0) {
		 var r=confirm("确认"+text+"吗？");
			if (r!=true){
				return ;
			}
			//var indxs = [];
            for(var i=0;i<ids.length;i++){
                //var id = ids[i];
                //var decl = $("#jg").jqGrid('getCell',id,"INDX");
                var rowData = $("#jg").jqGrid("getRowData", ids[i]); //根据上面的id获得本行的所有数据
                jsonArr[i] = rowData.INDX;
                //indxs.push(decl);
            }
            var Indxs = jsonArr.join(',');
		    $.ajax({
		        type: "post",
		        async: false, //控制同步
		        url: "ChangeAccessPassList",
		        data: {
		        	Indxs: Indxs,
		            action:action
		        },
		        //dataType: "json",
		        cache: false,
		        success: function (data) {
		        	var v = eval("(" + data + ")");
		        	ShowMessages(v.ErrMessage, "友情提示", "msgok", null, 0);
		        	callbackfunction();
		        },
		        Error: function (err) {
		            ShowMessages(err, "友情提示", "msgwaring", null, 0);
		        }
		    });
		    
	 }else{
		 ShowMessages("对不起,您没有选中任何记录", "友情提示", "msgwaring",null, 0);
	        return;
	 }
}
function callbackfunction() {
    $("#jg").trigger("reloadGrid");
}