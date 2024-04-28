
//--------------------------------------------//



var setting = {
			edit: {
				enable: true,
				showRemoveBtn: true,
				showRenameBtn: true,
				enable: true
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeDrag: beforeDrag,//是否可以拖拽
				beforeDrop: beforeDrop
			},
			view: {
				//addHoverDom: addHoverDom//当鼠标移动到节点上时，显示用户自定义控件
			}
	};


	
		
	function removeHoverDom(treeId, treeNode) {  
	    $("#addBtn_"+treeNode.tId).unbind().remove();  
	};  

	var zNodes =[];
	console.log(zNodes+"!-----!");
	
	
	function beforeDrag(treeId, treeNodes) {
		for (var i=0,l=treeNodes.length; i<l; i++) {
			if (treeNodes[i].drag === false) {
				return false;
			}
		}
		return true;
	}
	
	function beforeDrop(treeId, treeNodes, targetNode, moveType) {
		console.log(treeId);
		console.log(treeNodes);
		console.log(targetNode);
		console.log(moveType);
		var treeObj=$.fn.zTree.getZTreeObj("treeDemo");
		var nodes=treeObj.getNodes();
		console.log(nodes);
		return targetNode ? targetNode.drop !== false : true;
	}
	
	
	
	$(document).ready(function(){
		$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		$.fn.zTree.init($("#treeDemo2"), setting);
		var box=document.getElementById('tl');
		box.ondragover=function (e){
		 e.preventDefault();
		}
		box.ondrop=function (e){
		 e.preventDefault();
		 var f=e.dataTransfer.files[0];//获取到第一个上传的文件对象
		 var formData = new FormData();
		 formData.append("myfile", f);   
		 $.ajax({
             url: "GetConfigYs",
             type: "POST",
             data: formData,
             /**
             *必须false才会自动加上正确的Content-Type
             */
             contentType: false,
             /**
             * 必须false才会避开jQuery对 formdata 的默认处理
             * XMLHttpRequest会对 formdata 进行正确的处理
             */
             processData: false,
             success: function (data) {
            	 uploadXmlSuccess(data);
             },
             error: function () {
                 alert("上传失败！");
                 
             }
         });
		 
		 }
		
	});
	
	
	
	
function uploadXmlSuccess(data){
	var obj = eval("("+data+")");
	if(obj.IsOk=="1"){
		$("#xmlfile").text(obj.ReturnData);
		zNodes = eval("("+obj.ReturnData+")");
		console.log(zNodes);
		$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		$.fn.zTree.init($("#treeDemo2"), setting, zNodes);
	}else{
		alert(obj.ErrMessage);
	}
}
	

