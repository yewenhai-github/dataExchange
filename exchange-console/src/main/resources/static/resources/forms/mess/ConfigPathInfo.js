var INDX;
var btnTestLoad;

$(document).ready(function () {
	if(GetRequestName("FUNCTIONTYPE")){
		$("#FUNCTIONTYPE").attr("disabled", true);
		if(GetRequestName("FUNCTIONTYPE") =='1'){
			$("#FUNCTIONTYPE").val("转换");
		}
		if(GetRequestName("FUNCTIONTYPE") =='2'){
			$("#FUNCTIONTYPE").val("发送");
		}
		if(GetRequestName("FUNCTIONTYPE") =='3'){
			$("#FUNCTIONTYPE").val("转换并发送");
		}

	}
	INDX = GetRequestName("INDX");
	if(INDX!=''){
		DoCommand("GetConfigPathByIndx?INDX=" + INDX);
	}

	$('#btnTest').on('click', function(){
		btnTestLoad = layer.msg('连接中.....', {
			icon: 16,
			shade: 0.5,
			time: 200000//事件
		});
	});
});

function SaveConfigPath(){
	if(Exis()){
		dispathServerAjaxPost("SaveConfigPath",FormToData("MESSDATA"),showConfigSuccess);
	}

}

function Exis(){
	var CONFIGNAME = $("#CONFIGNAME").val();
	var FUNCTIONTYPE = $("#FUNCTIONTYPE").val();
	if(CONFIGNAME == ""){
		ShowMessages($("#CONFIGNAME").attr("errormsg"), "系统提示", "msgwaring", null, 0);
		return false;
	}else if(FUNCTIONTYPE == ""){
		ShowMessages($("#FUNCTIONTYPE").attr("errormsg"), "系统提示", "msgwaring", null, 0);
		return false;
	}
	return true;
}

function showConfigSuccess(s) {
	var v = eval("(" + s + ")");
	if (v.IsOk == "1") {
		ShowMessages(v.ErrMessage, "系统提示", "msgok", function okFunction() {
			window.location.href = "GetHTML?html/ConfigPaths.html?refresh";
		}, 0);
	} else{
		ShowMessages(v.ErrMessage, "系统提示", "msgwaring", function okFunction() {
			window.location.href = "GetHTML?html/ConfigPaths.html?refresh";
		}, 0);
	}
}

function showDelConfigSuccess(s) {
	if(btnTestLoad!=null && btnTestLoad!= ""){
		layer.closeAll();
	}
	var v = eval("(" + s + ")");
	if (v.IsOk == "1") {
		ShowMessages(v.ErrMessage, "系统提示", "msgok", null, 0);
	}else{
		ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
	}
}


function checkFunction(){
	var FUNCTIONTYPE = $("#FUNCTIONTYPE").next().val();
	if(FUNCTIONTYPE == 1){
		$("#RECEIVETYPE").removeAttr("ismust");
	}else if(FUNCTIONTYPE == 2){
		$("#RECEIVETYPE").attr("ismust","1");
	}else if(FUNCTIONTYPE == 3){
		$("#RECEIVETYPE").attr("ismust","1");
	}
}

/*function checkReceiveType(){
	var RECEIVETYPE = $("#RECEIVETYPE").next().val();
	if(RECEIVETYPE != 3){
		var tr1 = document.getElementById("TRANSACTIONDIV");
		tr1.style.display = 'none';
	}else{
		var tr2 = document.getElementById("TRANSACTIONDIV");
		tr2.style.display = '';
	}
}*/

function chekcOnRadio(e){
	var obj = document.getElementsByName("checkbox1");
	for (i=0; i<obj.length; i++){
		if (obj[i]!=e) obj[i].checked = false;
		else obj[i].checked = true;
	}
}



function ondblocal(v){
	if(v == '0'){
		$("#LOCALFILEPATH").hide();
		$("#TLOCALFILEPATH").show();
		$(".googledelInput").remove();
	}else if(v == '1'){
		$("#TLOCALFILEPATH").hide();
		$("#LOCALFILEPATH").show();
	}
}


function onlocal(){
	try {
		var Message = "请选择文件夹"; //选择框提示信息
		var Shell = new ActiveXObject("Shell.Application");
		var Folder = Shell.BrowseForFolder(0, Message, 0x0040, 0x11);//起始目录为我的电脑
		//var Folder = Shell.BrowseForFolder(0,Message,0); //起始目录为桌面
		if (Folder != null) {
			Folder = Folder.items(); // 返回 FolderItems 对象
			Folder = Folder.item(); // 返回 Folderitem 对象
			Folder = Folder.Path;  // 返回路径
			if (Folder.charAt(Folder.length - 1) != "\\") {
				Folder = Folder + "\\";
			}
			$("#TLOCALFILEPATH").hide();
			$("#LOCALFILEPATH").show();
			$("#LOCALFILEPATH").val(Folder);
			return Folder;
		} else {
			$("#TLOCALFILEPATH").hide();
			$("#LOCALFILEPATH").show();
			return Folder;
		}
	}  catch (e) {
		console.log(e.message);
		$("#TLOCALFILEPATH").hide();
		$("#LOCALFILEPATH").show();
	}
}


      