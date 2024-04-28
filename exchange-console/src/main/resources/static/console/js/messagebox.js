/* @version 1.0 | 20171019
 * @license: none (public login)
 */
var showWarningMsg = function (msg, title, okfc, clfc) {
    layer.msg(msg, {
        offset: '300px',
        //1成功标志  2失败标志 3 问号标志
        icon: 2,
        time: 3000
    }, function () {
        if (typeof(okfc) == "function") {
            okfc();
        }
    });
};
var showOKMessage = function (msg, title, okfc, clfc) {
    layer.msg(msg, {
        icon: 1,
        offset: '300px',
        time: 3000
    }, function () {
        if (typeof(okfc) == "function") {
            okfc();
        }
    });
};
function ShowMessages(msg, title, icon, okfc, clfc) {
    switch (icon) {
        case 'msgok' :
            showOKMessage(msg, title, okfc, clfc);
            break;
        case 'msgwaring':
            showWarningMsg(msg, title, okfc, clfc);
            break;
        default:
            showWarningMsg(msg, title, okfc, clfc);
            break;
    }
}
function showValidateTipsMsg(tipMsg, element) {
    element.focus();
    layer.tips(tipMsg, element, {
        //#0FA6D8
        tips: [2, '#FF0000']
    });
}

function showConfirm(msg, title, okfc, clfc) {
    layer.confirm(msg, {
            offset: '300px',
            icon: 3,
            title:title,
            btn: ['确定', '取消'], //按钮
        }, okfc, clfc
    )
}

//弹出iFrame
function showIframe() {
    layer.open({
        type: 2,
        offset: '100px',
        title: '模板导入',
        shadeClose: true,
        area: ['70%', '50%'],
        content: $basePath + "/uploadfile/templateUpload"
    });
}
function layerAlert(msg){
   layer.alert(msg, {icon: 2, title:'错误信息',area:'450px'});
}
function layerLoading(){
    return layer.load(0, {shade: [0.3, '#000']});
}
