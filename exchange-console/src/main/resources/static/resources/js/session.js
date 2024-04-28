var i_isRoot = "";
var i_username = "";
var i_indx = "";

$(function () {
	$.ajax({
        url: '../../auth/GetUserInfo',
        type: "POST",
        dataType: "json",
        success: function (result) {
        	i_isRoot = result.isRoot;
        	i_username = result.username;
        	i_indx = result.indx;
        }
    });
});