var domain = 'nabr';
//上传附件
$(function () {

    $('#attachments').datagrid({
        method: "GET",
        dataType: "json",
        toolbar: "#attachmentsbar",
        pageSize: 15,
        pageList: [15,30,100],
        columns: [
            [{
                field: 'id',
                checkbox: true
            }, {
                field: 'businessTypeName',
                title: '附件类型',
                width: 100
            }, {
                field: 'fileOriginName',
                title: '附件名称',
                width: 100
            },
                    {
                    field: 'fileSize',
                    title: '附件大小',
                    width: 100,
                    formatter: function (value, row, index) {
                        return bytesToSize(value);
                    }
                }, {
                    field: 'fileType',
                    title: '附件格式',
                    width: 100,
                    // formatter: function (value, row, index) {
                    //     var vlen = value.substring(0, 5);
                    //     if (vlen === 'image') {
                    //         if (value.indexOf("/") > 0) {
                    //             var valuetype = value.split("/");
                    //             return valuetype[1];
                    //         } else {
                    //             return value;
                    //         }
                    //     } else if (value.substring(value.length - 3, value.length) === 'pdf') {
                    //         return 'pdf';
                    //     } else if (value === 'doc' || value === 'docx' || value === 'xls' || value === 'xlsx' || value === 'png' || value === 'jpg' || value === 'jpeg') {
                    //         return value;
                    //     }
                    //     else {
                    //         if (value === 'application/vnd.openxmlformats-officedocument.wordprocessingml.document') {
                    //             return 'docx';
                    //         } else if (value === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet') {
                    //             return 'xlsx';
                    //         }
                    //         else if (value === 'application/msword') {
                    //             return 'doc';
                    //         }
                    //         else if (value === 'application/vnd.ms-excel' || value === 'application/octet-stream') {
                    //             return 'xls';
                    //         }
                    //     }
                    //
                    // }
                }, {
                    field: 'status',
                    title: '状态',
                    width: 100,
                    // formatter: function (value, row, index) {
                    //     if (isEmpty(value)) {
                    //         return '已上传';
                    //     }
                    //     else {
                    //         return value;
                    //     }
                    //
                    // }
                },
                {
                    field: 'createDate',
                    title: '上传时间',
                    width: 100
                }, {
                field: 'itemid',
                title: '操作',
                width: 100,
                align: 'right',
                formatter: function (value, row, index) {
                    if (isEmpty(value)) {
                        //console.log(row.fileType);
                        if (row.fileType != 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' && row.fileType != 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' && row.fileType != 'application/vnd.ms-excel' && row.fileType != 'application/msword' && row.fileType != 'application/octet-stream') {
                            var str = '<a data-magnify="gallery" data-caption="' + row.fileOriginName + '" href="' + domain + 'file/download/' + row.id + '">查看</a> <a href="' + domain + 'file/download/' + row.id + '?down=de">下载</a>';
                            return str;
                        }
                        else {
                            var str = '<a href="' + domain + 'file/download/' + row.id + '?down=de">下载</a>';
                            return str;
                        }
                    } else {
                        if (row.fileType === 'jpg' || row.fileType === 'png' || row.fileType === 'jpeg') {//row.fileType === 'pdf'
                            var stre = '<a data-magnify="gallery" data-caption="' + row.fileOriginName + '" href="' + value + '">查看</a>';
                            return stre;
                        } else {
                            return
                        }
                    }

                }
            }]
        ]
    });


});

var flieList = []; //数据，为一个复合数组
var sizeObj = []; //存放每个文件大小的数组，用来比较去重
$(function () {
    //元素
    //var oFileBtn = $("#fileUpload"); //上传按钮
    //点击选择文件按钮选文件
    $("#optionalAccessories").on("change", function () {
        parent.LoadingShow();
        analysisList(this.files);
        $('#optionalAccessories').val('');
        parent.LoadingHide();
    });

    //解析列表
    function analysisList(obj) {
        // var attachmentsType = GetSelect2Data("#attachmentsType");
        // console.log(attachmentsType);
        // if (isEmpty(attachmentsType.id)) {
        //     inputFocus('attachmentsType', '提示', '附件类型不能为空', 'warning');
        //     return false;
        // }
        //如果没有文件
        if (obj.length < 1) {
            $.messager.alert('提示', '未选中任何文件', 'warning');
            return false;
        }
        var i;
        for (i = 0; i < obj.length; i++) {
            var fileObj = obj[i]; //单个文件
            var name = fileObj.name; //文件名
            var size = fileObj.size; //文件大小
            var type = fileType(name); //报文类型，获取的是文件的后缀
            var imghref = window.URL.createObjectURL(fileObj);
            //文件大于3M，就不上传

            if (size > 1024 * 1024 * 6 || size === 0) {
                $.messager.alert('警告', '“' + name + '”超过了3M，不能上传', 'warning');
                continue;
            }
            //文件类型不为这种，就不上传
            if (("pdf/jpg/png/jpeg/doc/docx/xlsx/xls").indexOf(type) === -1) {
                $.messager.alert('警告', '“' + name + '”文件类型错误！', 'warning');
                continue;
            }
            //把文件大小放到一个数组中，然后再去比较，如果有比较上的，就认为重复了，不能上传
            //console.log(sizeObj.indexOf(size));
            if (sizeObj.indexOf(size) !== -1) {
                $.messager.alert('警告', '“' + name + '”已存在，不能重复上传！', 'warning');
                // console.log(sizeObj);
                continue;
            }
            else {
                sizeObj.push(size);
            }

            //console.log(imghref);
            //给json对象添加内容，得到选择的文件的数据
            var itemArr = [fileObj, name, size, type, imghref]; //文件，文件名，文件大小，报文类型
            flieList.push(itemArr);

//			console.log(flieList);

            $('#attachments').datagrid('insertRow', {
                index: 0,
                // 索引从0开始
                row: {
                    businessTypeName: '图片',
                    fileOriginName: name,
                    // fileSize: size,
                    // fileType: type,
                    // status: '未上传',
                    itemid: imghref
                }
            });
        }
    };

});

function fileUploadCX() {
    var tr = $('#attachments').datagrid('getChanges');
    if (tr.length === 0) {

        $.messager.alert('提示', '未选中任何文件！', 'warning');
    } else {
        var formData = uploadFn(tr); //参数为当前项，下标

        uploadingData(formData);
    }
}

var _fileUploadModule;
var _fileUploadPkSn;

function uploadFn(tr) {
    var formData = new FormData();
    var newFileInfo = [];
    var i;
    for (i = 0; i < tr.length; i++) {
        var sourcefile = flieList[i][0];
        var type = getBillTypeByFileSize(sourcefile.size);
        // var typeId = GetSelect2Data("#attachmentsType");
        //var Intercept = sourcefile.name.split('.');
        //var fileSuffix = Intercept.pop(Intercept.length - 1);
        var newFileName = sourcefile.name + '@#@' + sourcefile.size + '@#@' + '图片' + '##' + type.typeName;
        newFileInfo.push(newFileName);
        //console.log(newFileInfo);
        formData.append("files", sourcefile);

        /* formData.append("files", new File([f], newFileName, {
             type: f.type
         }));*/

    }

//	alert(_fileUploadModule);
    formData.append("info", newFileInfo);
    formData.append("module", _fileUploadModule);
    // formData.append("pkId", $('#id').val());
    formData.append("pkSn", _fileUploadPkSn);
    return formData;
}

function getBillTypeByFileSize(size) {
    var data = $('#attachments').datagrid('getChanges');
    var i;
    for (i = 0; i < data.length; i++) {
        if (data[i].fileSize === size) {
            return {
                typeName: data[i].businessTypeName
            };
        }

    }

}

function uploadingData(formData) {
    parent.LoadingShow();
    $.ajax({
        type: "POST",
        url: domain + "file/upload",
        data: formData,
        dataType: "json",
        //这里上传的数据使用了formData 对象
        processData: false,
        //必须false才会自动加上正确的Content-Type
        contentType: false,
        cache: false,
        //回调
        success: function (data) {
            parent.LoadingHide();
            if (data.status === '1') {
                $.messager.alert('提示', '上传成功！', 'info');
                fileUploadDatagridReload();
                flieList = [];
                newFileInfo = [];
            } else {
                $.messager.alert('提示', '上传失败！', 'warning');
            }
        },
        error: function () {
            $.messager.alert('提示', '操作失败,请稍后重试！', 'warning');
            parent.LoadingHide();
        }

    });
}

//字节大小转换，参数为b
function bytesToSize(bytes) {
    var sizes = ['Bytes', 'KB', 'MB'];
    if (bytes == 0) return 'n/a';
    var i = parseInt(Math.floor(Math.log(bytes) / Math.log(1024)));
    return (bytes / Math.pow(1024, i)).toFixed(1) + ' ' + sizes[i];
}

//通过文件名，返回文件的后缀名
function fileType(name) {
    var nameArr = name.split(".");
    return nameArr[nameArr.length - 1].toLowerCase();
}

//删除附件
function DeleteJudgeItem() {
    var selectRows = $("#attachments").datagrid("getSelections");
    if (isEmpty(selectRows)) {
        $.messager.alert('提示', '请至少选择一条记录进行操作！', 'warning');
        throw 'Operator exception';
    }
    var arrId = [];
    for (var i = 0; i < selectRows.length; i++) {
        //console.log(sizeObj);
        if (isEmpty(selectRows[i].id)) {
            var identical = selectRows[i].fileSize;
            $("#attachments").datagrid("deleteRow", $("#attachments").datagrid("getRowIndex", selectRows[i]));
            var index = sizeObj.indexOf(identical);
            if (index > -1) {
                sizeObj.splice(index, 1);
            }
        } else {
            arrId.push(selectRows[i].id);
        }
    }
    if (arrId.length > 0) {
        $.messager.confirm("操作确认", "确定要删除这些数据么？", function (r) {
            if (r) {
                $.postJSON(domain + "file/delete/batch", arrId, function (data) {
                    if (data.status === "1") {
                        $.messager.alert("提示", "批量删除成功！", "info");
                        $('#attachments').datagrid('reload');
                        flieList = [];
                        sizeObj = [];
                    }
                });
            }
        })
    } else {
        $.messager.alert("提示", "批量删除成功！", "info");
    }
}


function fileUploadDatagridReload() {
    // if (isEmpty(_fileUploadPkSn) || isEmpty(_fileUploadModule)) {
    //     $('#attachmentsbar .easyui-linkbutton').linkbutton('disable');
    //     $("#optionalAccessories").attr("disabled", "disabled");
    //     return;
    // } else {
    //     $('#attachmentsbar .easyui-linkbutton').linkbutton('enable');
    //     $("#optionalAccessories").attr("disabled", false);
    // }
    $("#attachments").datagrid({
        method: "POST",
        dataType: "json",
        url: domain + 'file/list?BG_BUG_ID=' + BG_BUG_ID
    });
}


//dataURL to blob
function dataURLtoBlob(dataurl) {
    var arr = dataurl.split(','),
        mime = arr[0].match(/:(.*?);/)[1],
        bstr = atob(arr[1]),
        n = bstr.length,
        u8arr = new Uint8Array(n);
    while (n--) {
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new Blob([u8arr], {
        type: mime
    });
}

//查看附件插件
$('[data-magnify]').magnify({
    headToolbar: ['close'],
    footToolbar: ['zoomIn', 'zoomOut', 'prev', 'fullscreen', 'next', 'actualSize', 'rotateRight'],
    title: false
});