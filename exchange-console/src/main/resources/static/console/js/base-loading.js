/**
 * Created by tiding on 2016/8/11.
 */
function LoadingShow() {
    //鑾峰彇娴忚鍣ㄩ〉闈㈠彲瑙侀珮搴﹀拰瀹藉害
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;

    //璁＄畻loading妗嗚窛绂婚《閮ㄥ拰宸﹂儴鐨勮窛绂伙紙loading妗嗙殑瀹藉害涓�15px锛岄珮搴︿负61px锛�
    var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
        _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;

    //鍔犺浇gif鍦板潃
    var Loadimagerul = "resources/img/loading.gif";

    var _Loadingdiv = document.createElement('div');

    //鍦ㄩ〉闈㈡湭鍔犺浇瀹屾瘯涔嬪墠鏄剧ず鐨刲oading Html鑷畾涔夊唴瀹�
    _Loadingdiv.innerHTML = '<div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width:100px;; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #E1EED2 url(' + Loadimagerul + ') no-repeat scroll 30px 20px; border: 2px solid #123555; color: #D1484D; font-weight: bold; font-family:\'Microsoft YaHei\';">LOADING...</div>';
    _Loadingdiv.setAttribute("style", "position:absolute;left:0;width:100%;height:" + _PageHeight + "px;top:0;background:#DCD0A8;opacity:0.8;filter:alpha(opacity=80);z-index:10000;");
    _Loadingdiv.setAttribute("id", "loadingDataDiv");

    //鍛堢幇loading鏁堟灉
    document.body.appendChild(_Loadingdiv);
}

function LoadingHide() {
    var loadingMask = document.getElementById('loadingDataDiv');
    loadingMask.parentNode.removeChild(loadingMask);
}

function InitPageLoading() {
    //鑾峰彇娴忚鍣ㄩ〉闈㈠彲瑙侀珮搴﹀拰瀹藉害
    var _PageHeight = document.documentElement.clientHeight,
        _PageWidth = document.documentElement.clientWidth;

    //璁＄畻loading妗嗚窛绂婚《閮ㄥ拰宸﹂儴鐨勮窛绂伙紙loading妗嗙殑瀹藉害涓�15px锛岄珮搴︿负61px锛�
    var _LoadingTop = _PageHeight > 61 ? (_PageHeight - 61) / 2 : 0,
        _LoadingLeft = _PageWidth > 215 ? (_PageWidth - 215) / 2 : 0;

    //鍔犺浇gif鍦板潃
    var Loadimagerul = "resources/img/loading.gif";

    //鍦ㄩ〉闈㈡湭鍔犺浇瀹屾瘯涔嬪墠鏄剧ず鐨刲oading Html鑷畾涔夊唴瀹�
    var _LoadingHtml = '<div id="loadingDiv" style="position:absolute;left:0;width:100%;height:' + _PageHeight + 'px;top:0;background:#DCD0A8;opacity:0.8;filter:alpha(opacity=80);z-index:10000;"><div style="position: absolute; cursor1: wait; left: ' + _LoadingLeft + 'px; top:' + _LoadingTop + 'px; width:100px;; height: 57px; line-height: 57px; padding-left: 50px; padding-right: 5px; background: #E1EED2 url(' + Loadimagerul + ') no-repeat scroll 30px 20px; border: 2px solid #123555; color: #D1484D; font-weight: bold; font-family:\'Microsoft YaHei\';">LOADING...</div></div>';

    //鍛堢幇loading鏁堟灉
    document.write(_LoadingHtml);

    document.onreadystatechange = completeLoading;
}
//鍔犺浇鐘舵�涓篶omplete鏃剁Щ闄oading鏁堟灉
function completeLoading() {
    if (document.readyState == "complete") {
        var loadingMask = document.getElementById('loadingDiv');
        loadingMask.parentNode.removeChild(loadingMask);
    }
}