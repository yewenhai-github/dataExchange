 $(document).ready(function () {
        window.onresize = function () {
        	setGridSize("#corptable",180);
        }
        $("#corptable").jqGrid({
            datatype: "json",
            url: "GetMessFileLog",
            postData: {
                'CommandData': setTableByUrl("SearchTable")
            }, //发送数据 
            
            colModel: [			
			{ label: 'INDX', name: 'INDX', index: 'INDX', width: 50, key: true,hidden:true },
			{ label: '源文件名',   name: 'SOURCE_FILE_NAME', index: 'SOURCE_FILE_NAME', width: 120 ,align : 'center'},
			{ label: '目标文件名', name: 'TARGET_FILE_NAME', index: 'TARGET_FILE_NAME',align : 'center', width: 120 },
			{ label: '转换状态', name: 'TRANSFORMATION_NAME', index: 'TRANSFORMATION_NAME',align : 'center', width: 60 },
			{ label: '发送状态', name: 'SEND_NAME', index: 'SEND_NAME', align : 'center',width: 60 },
			{ label: '转换时间', name: 'TRANSFORMATION_TIME', index: 'TRANSFORMATION_TIME', align : 'center',width: 60 },
			{ label: '发送时间', name: 'SEND_TIME', index: 'SEND_TIME',align : 'center', width: 60 },
			{ label: '结果描述', name: 'PROCESS_MSG', index: 'PROCESS_MSG', align : 'center',width: 60 },
			{ label: '源文件', name: 'SOURCE_BACK_PATH', index: 'SOURCE_BACK_PATH', align : 'center',width: 70, formatter :function(cellvalue, options, rowObject){
				if((rowObject.DATA_SOURCE != 'DB') && (rowObject.SEN_CODE == 2  || rowObject.SEND_NAME =='成功' || rowObject.TRANSFORMATION_NAME  =='无需' || rowObject.TRANSFORMATION_NAME  =='成功' || rowObject.TRANSFORMATION_NAME  =='失败')){
					return "<a class=\"btn btn-success btn-mini\" onclick='Sorcedown("+ rowObject.INDX+ ")' style=\"color:#fff;\"><i class=\"icon-ok\"></i>下载</a>";
				}else{
					return "<a class=\"btn btn-success btn-mini\" href=\"javascript:return false;\" style=\"opacity: 0.2\")' style=\"color:#fff;\"><i class=\"icon-ok\"></i>下载</a>";
				}
			}},
			{ label: '目标文件', name: 'SUCCESS_BACK_PATH', index: 'SUCCESS_BACK_PATH', width: 80,align : 'center', formatter :function(cellvalue, options, rowObject){
				if( rowObject.TRANSFORMATION_CODE == 2 ||  rowObject.TRANSFORMATION_NAME =='成功'){
					return "<a class=\"btn btn-success btn-mini\" onclick='down("+ rowObject.INDX+ ")' style=\"color:#fff;\"><i class=\"icon-ok\"></i>下载</a>";
				}else{
					return "<a class=\"btn btn-success btn-mini\" href=\"javascript:return false;\" style=\"opacity: 0.2\")' style=\"color:#fff;\"><i class=\"icon-ok\"></i>下载</a>";
				}
			} },
			{ label: '对比显示', name: '', index: '', width: 60,align : 'center', formatter :function(cellvalue, options, rowObject){
				if(rowObject.TRANSFORMATION_CODE == 2 || rowObject.TRANSFORMATION_NAME == '成功'){
					return "<a class=\"btn btn-success btn-mini\" onclick='select("+rowObject.INDX+")' style=\"color:#fff;\"><i class=\"icon-ok\"></i>查看</a>";
					
				}else{
					return "<a class=\"btn btn-suc btn-mini\" href=\"javascript:return false;\" style=\"opacity: 0.2\"><i class=\"icon-ok\"></i>查看</a>";
				}
			} },
			{ label: '回执结果', name: 'RECEIPT_RESULT', index: 'RECEIPT_RESULT', align : 'center',width: 80 },
			{ label: '回执时间', name: 'RECEIPT_TIME', index: 'RECEIPT_TIME', align : 'center',width: 60 },
			{ label: '描述', name: 'REMARKS', index: 'REMARKS', width: 60 },
			{ label: '注册号', name: 'SERIAL_NO', index: 'SERIAL_NO', width: 80 ,align : 'center',hidden:true}
			
            ],
            viewrecords: true, // show the current page, data rang and total records on the toolbar
            width: 500,
            //autowidth:auto,
            shrinkToFit: false,
            autoScroll: false,
            rowNum: 10, //每页显示记录数
            //datatype: 'local',
            rowList: [10, 20, 100], //可调整每页显示的记录数
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
                    url: "GetMessFileLog",
                    postData: {
                        'OrderField': colName,
                        'Order': sortorder,
                        'CommandData': FormToData('SearchTable')
                    }, //发送数据 
                    page: 1
                }).trigger("reloadGrid"); //重新载入 
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
            ,
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

        setGridSize("#corptable",180);

        //显示某一列
        //$("#grid_id").setGridParam().showCol("colname").trigger("reloadGrid");
        //隐藏某一列
        //$("#grid_id").setGridParam().hideCol("colname").trigger("reloadGrid");
        $("#corptable").setGridParam().hideCol("INDX").trigger("reloadGrid");
        //查询
        $("#btnSoEnt").click(function () {
            $("#corptable").jqGrid('setGridParam', {
                url: "GetMessFileLog",
                postData: {
                    'CommandData': FormToData('SearchTable')
                }, //发送数据 
                page: 1
            }).trigger("reloadGrid"); //重新载入 
        });
    });
 	function urlCode(cellvalue, options, rowObject){
 		var rt ="";
 		switch (cellvalue) {
 		case '1':
 			rt += "<span class=\"label label-warning\">XML</span>";
 			break;
 		case '2':
 			rt += "<span class=\"label label-warning\">Excel</span>";
 			break;
 		}
 		return rt;
 	}
 	function urlTypeCode(cellvalue, options, rowObject){
 		var rt ="";
 		switch (cellvalue) {
 		case '1':
 			rt += "<span class=\"label label-warning\"> </span>";
 			break;
 		return rt;
 		}
 	}
    function urlCodes(cellvalue, options, rowObject) {
        return "<a name='"+cellvalue+"' href=\"javascript:parent.openHtml('GetHTML?html/MessFileLogInfo.html?INDX=" + rowObject.INDX + "','" + cellvalue + "','详细信息')\"><span style=\"color:#00acec\">" + cellvalue + "</a>";

    }
    
    
    function DelAll(){
    	var ids=$('#corptable').jqGrid('getGridParam','selarrrow');
    	if (ids != null && ids.length != 0) {
    		var r=confirm("确认删除"+ids.length+"条数据吗？");
    		if (r!=true){
				return ;
			}
    		dispathServerAjaxPost("DeleteMessFileLog?ids="+ids,"",DelAllSuccess);
    	}else{
    		ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring",null, 0);
 	        return;
    	}
    }
    
    function DelAllSuccess(r){
    	var v = eval("(" + r + ")");
 	    if (v.IsOk == "1") {
 	        ShowMessages(v.ErrMessage, "系统提示", "msgok", null, 0);
 	        $("#corptable").jqGrid().trigger("reloadGrid"); //重新载入 
 	    }else{
 	    	ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
 	    	$("#corptable").jqGrid().trigger("reloadGrid"); //重新载入 
 	    }
    }
    
    function select(indx){
    	this.parent.openHtml("GetHTML?html/MessAnyXmlTest.html?INDX="+indx,'文件查看');
    }
    
    function down(indx){
    	if(indx!=""){
    		window.location.href = "DownXml?INDX="+indx;
    	}
    }
    
    function Sorcedown(indx){
    	if(indx!=""){
    		window.location.href = "DownXml?TYPE=SOURCE&INDX="+indx;
    	}
    }
    
   /* function  newBtn(){
    		this.parent.NavaddTab('ConfigPath',
    			"mess/MessFileLogInfo.html","新建");
    		
    }
    
    function edit(indx){
    	this.parent.NavaddTab('ConfigPath',
    			"mess/MessFileLogInfo.html?INDX="+indx,"编辑");
    }
    */
   