var t1;
var t2 = true;
$(document).ready(function () {
        window.onresize = function () {
        	setGridSize("#corptable",180);
        }
        $("#corptable").jqGrid({
            datatype: "json",
            url: "GetConfigPath",
            postData: {
                'CommandData': setTableByUrl("SearchTable")
            }, //发送数据 
            colNames: ['INDX','配置名','配置目的','','源文件类型','','目标文件类型','','接收类型','接收地址','接收用户名','接收密码','接收文件存放目录','','发送类型','发送地址','发送用户名','发送密码','发送存放目录','邮箱','本地目录','扫描开关','描述'],
            colModel: [
						{ name: 'INDX', index: 'INDX', width: 10,hidden:true,key:true},//indx
                        { name: 'CONFIGNAME', index: 'CONFIGNAME', width: 100,formatter: urlConfig,align : 'center'},//配置名                        
                        { name: 'FUNCTIONTYPENAME', index: 'FUNCTIONTYPENAME', width: 100,align : 'center'},//功能类型
                        { name: 'SOURCEFILETYPE', index: 'SOURCEFILETYPE', width: 100,hidden:true},//源文件类型
                        { name: 'SOURCEFILETYPENAME', index: 'SOURCEFILETYPE', width: 100,formatter: urlCode,align : 'center'},//源文件类型名称
                        { name: 'TARGETFILETYPE', index: 'TARGETFILETYPE', width: 100,hidden:true},//目标文件类型                       
                        { name: 'TARGETFILETYPENAME', index: 'TARGETFILETYPE', width: 100,formatter: urlCode,align : 'center'},//目标文件类型名称                      
                        { name: 'RECEIVETYPE', index: 'RECEIVETYPE', width: 100, hidden:true,align : 'center'},//接收类型
                        { name: 'RECEIVETYPENAME', index: 'RECEIVETYPE', width: 100, formatter: urlCode,align : 'center'},//接收类型
                        { name: 'RECEIVEADRESS', index: 'RECEIVEADRESS', width: 100,align : 'center'},//接收地址
                        { name: 'RECEIVEUSERNAME', index: 'RECEIVEUSERNAME', width: 100,align : 'center'},//接收用户名
                        { name: 'RECEIVEUSERPWD', index: 'RECEIVEUSERPWD', width: 100,align : 'center'},//接收密码
                        { name: 'RECEIVEPATH', index: 'RECEIVEPATH', width: 100,hidden:true},//接收文件存放目录
                        { name: 'SENDTYPE', index: 'SENDTYPE', width: 100, hidden:true,align : 'center'},//发送类型                        
                        { name: 'SENDTYPENAME', index: 'SENDTYPENAME', width: 100, formatter: urlCode,align : 'center'},//发送类型                        
                        { name: 'SENDADRESS', index: 'SENDADRESS', width: 100,align : 'center'},//发送地址
                        { name: 'SENDUSERNAME', index: 'SENDUSERNAME', width: 100,align : 'center'},//发送用户名
                        { name: 'SENDUSERPWD', index: 'SENDUSERPWD', width: 100,align : 'center'},//发送密码
                        { name: 'SENDPATH', index: 'SENDPATH', width: 100,hidden:true},//发送存放目录                        
                        { name: 'USEREMAIL', index: 'USEREMAIL', width: 100,hidden:true},//邮箱                 
                        { name: 'LOCALFILEPATH', index: 'LOCALFILEPATH', width: 100,hidden:true},//本地目录                 
                        { name: 'LOCALFILESWITCH', index: 'LOCALFILESWITCH', width: 100,hidden:true},//开关          
                        { name: 'REMARK', index: 'REMARK', width: 100,align : 'center'}//描述
        	        ],
        	ondblClickRow :function (rowid , iRow, iCol, e){
        		var config = $("#corptable").getRowData(rowid).CONFIGNAME;
        		var configname = $(config).attr("name");
				parent.openHtml("GetHTML?html/ConfigPathInfo.html?INDX=" +$("#corptable").getRowData(rowid).INDX,configname,'配置信息')
        	},
            viewrecords: true, // show the current page, data rang and total records on the toolbar
            width: 500,
            //autowidth:auto,
            height: window.screen.height-440,
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

        setGridSize("#corptable",180);

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
        return "<a name='"+cellvalue+"' href=\"javascript:parent.openHtml('GetHTML?html/ConfigPathInfo.html?INDX=" + rowObject.INDX + "','" + cellvalue + "','配置信息')\"><span style=\"color:#00acec\">" + cellvalue + "</a>";
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
    	this.parent.openHtml("GetHTML?html/ConfigName.html?indx="+indx,"转换映射");
    }
    
    function newBtn(){
    	this.parent.openHtml("GetHTML?html/ConfigPathInfo.html","新建");
    }
    
    function ConfigMapper(){
    	var grid = $("#corptable");
    	var rowKey = grid.getGridParam("selrow");
    	if (!rowKey){
    		top.ShowMessages("对不起,您没有选中任何记录", "系统提示", "msgwaring", null, 0);
    	}else {
    		var selectedIDs = grid.getGridParam("selarrrow");
    		if(selectedIDs.length > 1){
    			top.ShowMessages("对不起,只能选择一条记录", "系统提示", "msgwaring", null, 0);
    	    	return ;
    	    }
    		var rowData = $("#corptable").jqGrid('getRowData',rowKey);
    		var config = rowData.CONFIGNAME;
    		var configname = $(config).attr("name");
    		this.parent.openHtml("GetHTML?html/ConfigName.html?indx="+rowData.INDX,configname,"映射配置");
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
    			  content: 'GetHTML?html/DbFind.html?indx='+rowData.INDX
    			});
    	}
    	
    }
    function doFileChange(){
    	var grid = $("#corptable");
    	var rowKey = grid.getGridParam("selrow");
    	if(rowKey == null){
    		layer.alert("请选择一条数据！");
    		return;
    	}
	   var load = layer.msg('上传中,请稍等', {
		   icon: 16,
		   shade: 0.5,
		   time: 200000//事件
		});
    	var rowData = $("#corptable").jqGrid('getRowData',rowKey);
		var formData = new FormData();
		var obj = document.getElementById("file").files.length;
		for(var i=0;i<obj;i++){
			 formData.append("myfile", document.getElementById("file").files[i]); 
		}
	     
	    $.ajax({
            url: "uploadXZX?INDX="+rowData.INDX,
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
            	layer.close(load);
            	successXmlData(data);
            },
            error: function () {
            	layer.close(load);
            	layer.alert("上传失败");
            }
        });
    };


 
    
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
	
	
	function autoDeal(){
		document.getElementById("QuarantineUl").style.display="block";
	}
	
	function onoff(){
		if(t1){
			t2 = true;
			clearInterval(t1);
			t1 = null;
			layer.alert("扫描已关闭！");
		}else{
			layer.alert("无扫描正在执行！");
		}
	}
	function scanning(){
		if(t2==false){
			return;
		}
		t2 = false;
		var DataTotal = $("#corptable").jqGrid('getRowData');   //获取总数 （包含未显示的数据）
		if(DataTotal){
			for(var i=0; i<DataTotal.length;i++){
				var LOCALFILEPATH = DataTotal[i].LOCALFILEPATH;
				var LOCALFILESWITCH = DataTotal[i].LOCALFILESWITCH;
				if(LOCALFILEPATH && LOCALFILESWITCH=='1'){
					var fso;
					 try {
					    fso  = new ActiveXObject("Scripting.FileSystemObject");   //加载控件
					 }
					 catch(err){
						 clearInterval(t1);
						 t1 = null;
						 layer.alert('游览器不支持该控件或控件被禁用 扫描自动关闭！'); 
						 t2 = true;
						 return;
					 }
					 if(!fso){
						 t2 = true;
						 return;
					 }
					 var f ;
					 try {
						 f = fso.GetFolder(LOCALFILEPATH);
					 } catch (e) {
						 continue;
					 }
					 var underFiles = new Enumerator(f.files); //文件夹下文件
					 for (;!underFiles.atEnd();underFiles.moveNext()){   
				          var fn = "" + underFiles.item();   
				          var f1 = fso.GetFile(fn);//所有文件  //TODO  目前IFile对象需要处理
				          var fileName = f1.name;
				          var index1=fileName.lastIndexOf(".");
				          var index2=fileName.length;
				          var suffix=fileName.substring(index1+1,index2);//后缀名
				          if(suffix.toUpperCase()!= "XML"){
				        	  continue;
				          }
				          var fh = fso.OpenTextFile(f1, 1);
				          var content = '';
			               while ( !fh.AtEndOfStream ) {
			                      content += fh.ReadLine();
			               }
			               fh.close()
			               if(content	==''){
			            	   continue;
			               }
			               var data = {
			            		   "filename":fileName,
			            		   "content":content
			               }
			               /*  var formData = new FormData();
				  		  formData.append("filename", f1.name); 
				  		  formData.append("content", content); */
					  	$.ajax({
				            url: "upactivexxml?INDX="+DataTotal[i].INDX,
				            type: "POST",
				            data: data,
				            dataType:"text",
				            success: function (data) {
				            	f1.Delete();
				            },
				            error: function () {
				            	layer.alert("上传失败");
				            }
				        });
				          
				    } 
					 
				}
			}
		}
		t2 = true;
	}
	