function LoadingShow() {
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;

    var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
        _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;

    var Loadimagerul = "resources/biz/img/loading.gif";

    var _Loadingdiv = document.createElement('div');

    _Loadingdiv.innerHTML = '<div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width:100px;; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #E1EED2 url(' + Loadimagerul + ') no-repeat scroll 30px 20px; border: 2px solid #123555; color: #D1484D; font-weight: bold; font-family:\'Microsoft YaHei\';">LOADING...</div>';
    _Loadingdiv.setAttribute("style", "position:absolute;left:0;width:100%;height:" + _PageHeight + "px;top:0;background:#DCD0A8;opacity:0.8;filter:alpha(opacity=80);z-index:10000;");
    _Loadingdiv.setAttribute("id", "loadingDataDiv");

    document.body.appendChild(_Loadingdiv);
}

function LoadingHide() {
    var loadingMask = document.getElementById('loadingDataDiv');
    if(null != loadingMask && null != loadingMask.parentNode){
    	loadingMask.parentNode.removeChild(loadingMask);
    }
}

function InitPageLoading() {
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;

    var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
        _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;

    var Loadimagerul = "resources/biz/img/loading.gif";

    var _LoadingHtml = '<div id="loadingDiv" style="position:absolute;left:0;width:100%;height:' + _PageHeight + 'px;top:0;background:#DCD0A8;opacity:0.8;filter:alpha(opacity=80);z-index:10000;"><div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width:100px;; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #E1EED2 url(' + Loadimagerul + ') no-repeat scroll 30px 20px; border: 2px solid #123555; color: #D1484D; font-weight: bold; font-family:\'Microsoft YaHei\';">LOADING...</div></div>';

    document.write(_LoadingHtml);

    document.onreadystatechange = completeLoading;
}
function completeLoading() {
    if (document.readyState == "complete") {
        var loadingMask = document.getElementById('loadingDiv');
        loadingMask.parentNode.removeChild(loadingMask);
    }
}