function dispathServerAjaxPost(url,serviceJson , afterPost ,async){
	 //默认为异步
    if (!async)
        async = false;
	 else
	     async = true; 
	 $.ajax(
	 {
	     type: "POST",
	     url: url,
	     data: {"serviceJson":serviceJson},
	     contentType: "application/x-www-form-urlencoded;charset=utf-8",
	     cache: false,
	     dataType: 'text',
	     async: async,
	     success: function (data) {		         
	         if(afterPost instanceof Function)
	         	afterPost(data);
	     },
	     error: function (xhr, data) { 
	    	 ShowMessages(data, "友情提示", "msgwaring", null, 0);		         
	         return false;
	     }
	 });
}
