 $(document).ready(function () {
        window.onresize = function () {
            $("#corptable").setGridWidth($(window).width() - 80);
        }
        $("#corptable").jqGrid({
            datatype: "json",
            url: "GetTemplet",
            postData: {
                'CommandData': setTableByUrl("SearchTable")
            }, //发送数据 
            colNames: ['INDX','模板名称','模板描述', '创建时间', '模板类型', '模板','模板路径','操作'],
            colModel: [			
            			{   name: 'INDX', index: 'INDX', width: 50, key: true },
						{   name: 'TEMPLET_NAME', index: 'TEMPLET_NAME', width: 180 },
						{   name: 'TEMPLET_MARK', index: 'TEMPLET_MARK', width: 300 },
						{   name: 'TEMPLET_CREATE_TIME', index: 'TEMPLET_CREATE_TIME', width: 80 ,hidden:true},
						{   name: 'TEMPLET_TYPE', index: 'TEMPLET_TYPE', width: 80 ,hidden:true},
						{   name: 'TEMPLET_ORG_ID', index: 'TEMPLET_ORG_ID', width: 80 ,hidden:true},
						{   name: 'TEMPLET_PATH', index: 'TEMPLET_PATH', width: 80 ,hidden:true},
						{   name:'', width:100,align : 'center',formatter :function(cellvalue, options, rowObject) {
								return "<a class=\"btn btn-success btn-mini\" onclick='find("+ rowObject.INDX+")' style=\"color:#fff;\"><i class=\"icon-edit\"></i>下载</a>";
							}}
					  ],
        	ondblClickRow :function (rowid , iRow, iCol, e){
        	},
            viewrecords: true, // show the current page, data rang and total records on the toolbar
            width: window.screen.width - 300,
            //autowidth:auto,
            height: 300,
            shrinkToFit: false,
            autoScroll: false,
            rowNum: 15, //每页显示记录数
            //datatype: 'local',
            rowList: [5, 10, 15], //可调整每页显示的记录数
            rownumbers: true,
            rownumWidth: 35,
            multiselect: false, //是否支持多选 
            viewrecords: true, //是否显示行数 
            //shrinkToFit: false, // must be set with frozen columns, otherwise columns will be shrank to fit the grid width
            pager: "#jqGridPager", //分页工具栏
            altRows: true,
            altclass: 'someClass',
            loadui: "none", //隐藏默认的loading    
            //排序开始
            onSortCol: function (colName, Id, sortorder) {
                //colName   排序字段
                //Id   第几列
                //sortorder  排序  desc 倒序  asc 正序
                $("#corptable").jqGrid('setGridParam', {
                    url: "GetConfigPath",
                    postData: {
                        'OrderField': colName,
                        'Order': sortorder,
                        'CommandData': FormToData('SearchTable')
                    }, //发送数据 
                    page: 1
                }).trigger("reloadGrid"); //重新载入 
            },
            //排序结束
            //loadonce : true,
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
            ,
            beforeRequest: function () {
                if (top.showloading) {
                    top.showloading();
                }
            },
            gridComplete: hideSelectAll,
            beforeSelectRow: beforeSelectRow
        });

        $("#corptable").setGridWidth($(window).width() - 20);

        //显示某一列
        //$("#grid_id").setGridParam().showCol("colname").trigger("reloadGrid");
        //隐藏某一列
        //$("#grid_id").setGridParam().hideCol("colname").trigger("reloadGrid");
        $("#corptable").setGridParam().hideCol("INDX").trigger("reloadGrid");
        //查询
        $("#btnSoEnt").click(function () {
            $("#corptable").jqGrid('setGridParam', {
                url: "GetConfigPath",
                postData: {
                    'CommandData': FormToData('SearchTable')
                }, //发送数据 
                page: 1
            }).trigger("reloadGrid"); //重新载入 
        });
    });
 
 
 	function hideSelectAll() {  
	    $("#cb_table_mycars").hide();  
	    return(true);  
	} 
 	
 	function beforeSelectRow() {  
 	    $("#corptable").jqGrid('resetSelection');  
 	    return(true);  
 	}
 	
 	function urlCode(cellvalue, options, rowObject){
 		var rt =cellvalue;
 		switch (cellvalue) {
 		case 'XML(zip)':
 			rt = "<span class=\"label label-success\">XML(zip)</span>";
 			break;
 		case 'Excel(zip)':
 			rt = "<span class=\"label label-success\">Excel(zip)</span>";
 			break;
 		case 'DB数据库':
 			rt = "<span class=\"label label-success\">DB数据库</span>";
 			break;
 		case 'XML':
 			rt = "<span class=\"label label-success\">XML</span>";
 			break;
 		case 'FTP':
 			rt = "<span class=\"label label-success\">FTP</span>";
 			break;
 		case 'Web Service':
 			rt = "<span class=\"label label-success\">Web Service</span>";
 			break;
 		case 'MS MQ':
 			rt = "<span class=\"label label-success\">MS MQ</span>";
 			break;
 		case 'Active MQ':
 			rt = "<span class=\"label label-success\">Active MQ</span>";
 			break;
 		case 'IBM MQ':
 			rt = "<span class=\"label label-success\">IBM MQ</span>";
 			break;
 		case '其他':
 			rt = "<span class=\"label label-success\">其他</span>";
 			break;
 		case 'sqlserver':
 			rt = "<span class=\"label label-success\">sqlserver</span>";
 			break;
 		case 'oracle':
 			rt = "<span class=\"label label-success\">oracle</span>";
 			break;
 		case 'mysql':
 			rt = "<span class=\"label label-success\">mysql</span>";
 			break;
 		}
 		return rt;
 	}
 	
 	function urlTypeCode(cellvalue, options, rowObject){
 		var rt =cellvalue;
 		switch (cellvalue) {
 		case '1':
 			rt = "<span class=\"label label-warning\">FTP</span>";
 			break;
 		case '2':
 			rt = "<span class=\"label label-warning\">Web Service</span>";
 			break;
 		case '3':
 			rt = "<span class=\"label label-warning\">MS MQ</span>";
 			break;
 		case '4':
 			rt = "<span class=\"label label-warning\">Active MQ</span>";
 			break;
 		case '5':
 			rt = "<span class=\"label label-warning\">Ibm MQ</span>";
 			break;
 		case '6':
 			rt = "<span class=\"label label-warning\">其他</span>";
 			break;
 		}
 		return rt;
 	}
    function urlConfig(cellvalue, options, rowObject) {
        return "<a name='"+cellvalue+"' href=\"javascript:parent.NavaddTab('配置信息','mess/ConfigPathInfo.html?INDX=" + rowObject.INDX + "','" + cellvalue + "')\"><span style=\"color:#00acec\">" + cellvalue + "</a>";
    }
    
    
    function DelAll(){
    	//var ids=$('#corptable').jqGrid('getGridParam','selrow');
    	var grid = $("#corptable");
    	var ids = grid.getGridParam("selrow");
    	if (ids != null && ids.length != 0) {
    		var r=confirm("确认删除当前行记录");
    		if (r!=true){
				return ;
			}
    		dispathServerAjaxPost("DeleteConfigPath?ids="+ids,"",DelAllSuccess);
    	}else{
    		ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring",null, 0);
 	        return;
    	}
    }
    
    function DelAllSuccess(r){
    	var v = eval("(" + r + ")");
 	    if (v.IsOk == "1") {
 	        ShowMessages(v.ErrMessage, "系统提示", "msgok", null, 0);
 	       $("#corptable").jqGrid().trigger("reloadGrid");
 	    }else{
 	    	ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
 	    }
    }
    
    
    function mapping(indx){
    	
    	this.parent.NavaddTab('ConfigPath',
    			"mess/ConfigName.html?indx="+indx,"转换映射");
    }
    
    function newBtn(){
    	this.parent.NavaddTab('ConfigPath',
    			"mess/ConfigPathInfo.html","新建");
    }
    
    function ConfigMapper(){
    	var grid = $("#corptable");
    	var rowKey = grid.getGridParam("selrow");
    	if (!rowKey){
    		ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring", null, 0);
    	}else {
    		var selectedIDs = grid.getGridParam("selarrrow");
    		if(selectedIDs.length > 1){
    			ShowMessages("对不起,只能选择一条记录", "系统提示", "msgwaring", null, 0);
    	    	return ;
    	    }
    		var rowData = $("#corptable").jqGrid('getRowData',rowKey);
    		var config = rowData.CONFIGNAME;
    		var configname = $(config).attr("name");
    		this.parent.NavaddTab(configname,
    	    			"mess/ConfigName.html?indx="+rowData.INDX,"映射配置");
    	}
    }
    
    function upSource(){
    	var grid = $("#corptable");
    	var rowKey = grid.getGridParam("selrow");
    	if(rowKey == null){
    		layer.alert("请选择一条数据！");
    		return;
    	}
    	var rowData = $("#corptable").jqGrid('getRowData',rowKey);
    	if(rowData.SOURCEFILETYPE ==1){//XML
    		file.click();
    	}else if(rowData.SOURCEFILETYPE ==2){//Excel
    		file.click();
    	}else if(rowData.SOURCEFILETYPE ==3){//DB
    		layer.open({
    			  type: 2,
    			  area: ['500px', '500px'],
    			  fixed: true, //不固定
    			  maxmin: false,
    			  content: 'DbFind.html?indx='+rowData.INDX
    			});
    	}
    	
    }
  
 
    
	function successXmlData(data){
		var obj = eval("("+data+")");
		if(obj.IsOk && obj.IsOk==1){
			ShowMessages(obj.ErrMessage, "系统提示", "msgok", function okFunction() {
				 window.location.reload();
			}, 0);	
		}else{
			ShowMessages(obj.ErrMessage, "系统提示", "msgwaring", function okFunction() {
				 window.location.reload();
			}, 0);	
		}
	}
	   function find(indx){
			if(indx == ""){
				ShowMessages("请重新打开该页面重试！", "系统提示", "msgok", null, 0);	
				return;
			}
//			dispathServerAjaxPost("GetTempletByIndx?INDX=" + indx, "",
//					functionFindok);
			window.location.href = "GetTempletByIndx?INDX=" + indx;
	    }
	    
	    
	    function functionFindok(r){
	    	var obj = $.parseJSON(r);
	    	if(obj.IsOk && obj.IsOk==1){
	    		var ReturnData =  $.parseJSON(obj.ReturnData);
	    		var TempletDATA = ReturnData.TempletDATA;
	    		var XMLDATA = TempletDATA[0].XMLDATA;
	    		XMLDATA = XMLDATA.replace(/"([^"]+)":/g, function (v) { return v.toUpperCase(); });
	    		$("#txtXMLDATA").val(XMLDATA);
	    	}else{
	    		ShowMessages(obj.ErrMessage, "系统提示", "msgwaring", null, 0);	
	    	}
	    }
	   
		function functionBtndok(r){
			var obj = $.parseJSON(r);
	    	if(obj.IsOk && obj.IsOk==1){
	    		ShowMessages(obj.ErrMessage, "系统提示", "msgok", function okFunction(){
	    			parent.location.reload();
	    	    	window.close();
	    		}, 0);	
	    	}else{
	    		ShowMessages(obj.ErrMessage, "系统提示", "msgwaring", null, 0);	
	    	}
		}
	 function btnOk(){
	    	var grid = $("#corptable");
	    	var rowKey = grid.getGridParam("selrow");
	    	if (rowKey.length ==0){
	    		ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring", null, 0);
	    		return;
	    	}else{
	    		var INDX = GetRequestName("indx");
	    		dispathServerAjaxPost("SavePathTemplet?PATH_INDX=" + INDX+"&INDX="+rowKey, "",
	    				functionBtndok);
	    	}
	    	
	    }
	 
	function Clearok(){
    	parent.location.reload();
    	window.close();
    }
	