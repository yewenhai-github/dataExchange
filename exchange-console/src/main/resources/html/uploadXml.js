function doFileChange(){
		var formData = new FormData();
		//注意顺序  调整顺序导致后台数据异常
	    formData.append("myfile", document.getElementById("file").files[0]);   
	    $.ajax({
            url: "uploadXZX",
            type: "POST",
            data: formData,
            contentType: false,
            processData: false,
            success: function (data) {
            	successXmlData(data);
            },
            error: function () {
                alert("上传失败！");
            }
        });
};


function successXmlData(data){
	var obj = eval("("+data+")");
	if(obj.IsOk && obj.IsOk==1){
		ShowMessages(obj.ErrMessage, "系统提示", "msgok", null, 0);	
	}else{
		ShowMessages(obj.ErrMessage, "系统提示", "msgwaring", null, 0);	
	}
}