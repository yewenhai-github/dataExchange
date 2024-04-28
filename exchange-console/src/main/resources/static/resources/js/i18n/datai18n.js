function getI18nText(relpath,prop,callback){
	var locale=getCookie("locale");
    $.i18n.properties({
        name: "msg",
        path:relpath+"i18n",
        mode: "map",//模式：变量或 Map
        language: locale,
        cache: true,
        encoding: "UTF-8",
        callback: function() {
        	callback;
        }
    });
    return $.i18n.map[prop];
}

/************************************************************************
|    函数名称： setCookie                                                |
|    函数功能： 设置cookie函数                                            |
|    入口参数： name：cookie名称；value：cookie值                        |
*************************************************************************/
function setCookie(name, value) { 
    var argv = setCookie.arguments; 
    var argc = setCookie.arguments.length; 
    var expires = (argc > 2) ? argv[2] : null; 
    if(expires!=null) { 
        var LargeExpDate = new Date (); 
        LargeExpDate.setTime(LargeExpDate.getTime() + (expires*1000*3600*24));         
    } 
    document.cookie = name + "=" + escape (value)+((expires == null) ? "" : ("; expires=" +LargeExpDate.toGMTString())); 
}
/************************************************************************
|    函数名称： getCookie                                                |
|    函数功能： 读取cookie函数                                            |
|    入口参数： Name：cookie名称                                            |
*************************************************************************/
function getCookie(Name) { 
    var search = Name + "=" 
    if(document.cookie.length > 0) { 
        offset = document.cookie.indexOf(search) 
        if(offset != -1) { 
            offset += search.length 
            end = document.cookie.indexOf(";", offset) 
            if(end == -1) end = document.cookie.length 
            return unescape(document.cookie.substring(offset, end)) 
        } else 
        	return "" 
    } 
} 

/************************************************************************
|    函数名称： deleteCookie                                            |
|    函数功能： 删除cookie函数                                            |
|    入口参数： Name：cookie名称                                        |
*************************************************************************/    
function deleteCookie(name) { 
    var expdate = new Date(); 
    expdate.setTime(expdate.getTime() - (86400 * 1000 * 1)); 
    setCookie(name, "", expdate); 
} 
