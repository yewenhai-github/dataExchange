var INDX;

$(document).ready(function () { 
	 INDX = GetRequestName("INDX");
	 if(INDX==''){
		 $("#btnDelete").hide();
	 }else{
		 DoCommand("GetMessFileLogByIndx?INDX=" + INDX); 
	 }
 });
 	


function saveSuccess(s) {
    var v = eval("(" + s + ")");
    if (v.IsOk == "1") {
        ShowMessages(v.ErrMessage, "系统提示", "msgok", function okFunction() {
            window.location.href = "MessFileLog.html";
        }, 0);
    } else{
    	 ShowMessages(v.ErrMessage, "系统提示", "msgwaring", function okFunction() {
             window.location.href = "MessFileLog.html";
         }, 0);
    }
}

function DelSuccess(s) {
	 var v = eval("(" + s + ")");
	    if (v.IsOk == "1") {
	        ShowMessages(v.ErrMessage, "系统提示", "msgok", null, 0);
	    }else{
	    	ShowMessages(v.ErrMessage, "系统提示", "msgwaring", null, 0);
	    }
}
      