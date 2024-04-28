var mainPlatform = {

	init: function(){

		this.bindEvent();
		// this.render(menu['home']);
	},

	bindEvent: function(){
		var self = this;
		InitTopMenu();
		// 顶部大菜单单击事件
		$(document).on('click', '.pf-nav-item', function() {
            $('.pf-nav-item').removeClass('current');
            $(this).addClass('current');
			
			 if($(".pf-nav-item").index(this)==0){
				 $('#index_main').show();
				  $('#pf-bd').hide();
				 }
				 else{
					  $('#index_main').hide();
					   $('#pf-bd').show();
					 }
			

            // 渲染对应侧边菜单
            var m = $(this).data('menu');


			var iconText = $(this).find(".iconfont").text();	//获取图标
			var title = $(this).find(".pf-nav-title").text();  //获取大栏目名称
            self.render(m,iconText,title);
        });

      /*  $(document).on('click', '.pf-logout', function() {
        	if(window.confirm('您确定要退出吗？')){
        		 $.ajax({
      		       url:'login/LoginOut',
      		       type:"POST",
      		       success:function(result){  
      		    if(result.IsOk){ 
      		    	 window.location.href = result.ReturnAddress;
      		    }}
      		   }); 
        	}
        });*/
        //左侧菜单收起
        $(document).on('click', '.toggle-icon', function() {
            $(this).closest("#pf-bd").toggleClass("toggle");
            setTimeout(function(){
            	$(window).resize();
            },300)
        });

        //$(document).on('click', '.pf-modify-pwd', function() {
        //    $('#pf-page').find('iframe').eq(0).attr('src', 'backend/modify_pwd.html')
        //});

        $(document).on('click', '.pf-notice-item', function() {
            $('#pf-page').find('iframe').eq(0).attr('src', 'backend/notice.html')
        });
	},

	render: function(parentMenuId,icon,title){
		var current,
			html = ['<h2 class="pf-model-name"><span class="iconfont">'+icon+'</span><span class="pf-name">'+ title +'</span><span class="toggle-icon"></span></h2>'];
		
		$.ajax({
			type: "GET",
			url: "auth/GetSysSubMenuList",
			data: {'ParentMenuId': parentMenuId},
			dataType: "json",
			cache: false,
			beforeSend:function(request){
				LoadingShow();				
			},
			complete: function(XMLHttpRequest, textStatus){
				LoadingHide();
	        },
			success: function (data) {
				var navData = eval(data);
				var ordernum = 0
				var FD = eval(navData.ReturnData);
				html.push('<ul class="sider-nav">');
				for (var i = 0; i < FD.length; i++) {
					if(i==0){
						html.push('<li class="current" title="'+FD[i].MENU_TEXT +'" data-src="" onclick="LeftMenuClick(this)">' +
							'<a href="javascript:;"><span class="iconfont sider-nav-icon">'+FD[i].ICON+'</span><span class="sider-nav-title">'+FD[i].MENU_TEXT +'</span><i class="iconfont"></i></a>');
					}else{
						html.push('<li  title="'+FD[i].MENU_TEXT +'" data-src="" onclick="LeftMenuClick(this)">' +
							'<a href="javascript:;"><span class="iconfont sider-nav-icon">'+FD[i].ICON+'</span><span class="sider-nav-title">'+FD[i].MENU_TEXT +'</span><i class="iconfont"></i></a>');
					}
					var subdata = FD[i].children;

					if(subdata.length>0) {
						var strContent = "<ul class='sider-nav-s'>";
						//遍历生成子菜单项
						$.each(subdata, function (j, subItem) {
							var attr_active = "";
							strContent += "<li "+attr_active+" ><a href=\"javascript:addTabPage('" + subItem.MENU_TEXT + "','"+subItem.URL+"')\">"+subItem.MENU_TEXT+"</a></li>";
						})
						strContent += "</ul></li>";
						html.push(strContent);
					}
				}
				html.push('</ul>');
				$('#pf-sider').empty();
				$('#pf-sider').html(html.join(''));
				$('.sider-nav-s li a').click(function () {
					LoadingShow();
					$('.sider-nav-s li').removeClass("selected");
					$(this).parent().addClass("selected");
				}).hover(function () {
					$(this).parent().addClass("hover");
				}, function () {
					$(this).parent().removeClass("hover");
				});
			},
			error: function (d) {
				alert("error");
			}
		});
	}

};

//初始化导航菜单
function InitTopMenu() {

	$.ajax({
		type: "GET",
		url: "auth/getSysMenuList",
		dataType: "json",
		cache: false,
		success: function (data) {
			//console.log(data);
			//console.log(data.ReturnData)

			var navData = eval("(" + data.ReturnData + ")");
			//console.log(navData);

			$.each(navData.rows, function (i, item) {

				var linkTemplete = "<li class=\"pf-nav-item home current\" data-menu=\""+item.INDX+"\">" +
					"<a href=\"javascript:InitLeftMenu('" + item.INDX + "');\"><span class=\"iconfont\">"+item.MENU_ICON+"</span><span class=\"pf-nav-title\">" + item.MENU_TEXT +" </span></a></li>";
				//console.log(linkTemplete);
				$("#navContainer ul").append(linkTemplete);
			});


			$("#navContainer ul li").on('click', function (e) {
				e = event || window.event;
				var target = e.target;
				//console.log($(target));
				//导航栏添加样式
				$("#navContainer ul li a").removeClass("mainMenuSelect");
				$(target).addClass("mainMenuSelect")
			});
			//默认选中第一个模块
		$("#navContainer ul li a")[0].click();
			pages = ($('.pf-nav').height() / 70) - 1;
			if(pages === 0){
//		        $('.pf-nav-prev,.pf-nav-next').hide();
		    }
		},
		error: function () {
			alert("error");
		}
	});
}

//初始化左侧菜单
//parentMenuId：一级栏目id
function InitLeftMenu(parentMenuId) {
	//var pnls = $('#nav').accordion('panels');


	//var arr = new Array();

	//$.each(pnls, function (i, n) {
	//	if (n) {
	//		var t = n.panel('options').title;
	//		arr.push([n.panel('options').title]);
	//		//$('#nav').accordion('remove', t);
	//		//alert(i);
	//	}
	//});
	//for (var i = 0; i < arr.length; i++) {
	//	//alert(arr[i]);
	//	$('#nav').accordion('remove', arr[i]);
	//}nb


//	$.ajax({
//		type: "GET",
//		url: "auth/GetSysSubMenuList",
//		data: {'ParentMenuId': parentMenuId},
//		dataType: "json",
//		cache: false,
//		success: function (data) {
//			//一次性读取所有数据再根据parentMenuId筛选json节点
//			//后台改写时注意可以只输出需要的json数据
//			var navData = eval(data);
//			var ordernum = 0
//			var FD = eval(navData.ReturnData);
//			for (var i = 0; i < FD.length; i++) {
//				var subdata = FD[i].children;
//				var strContent = "<ul class='submenu'>";
//				//遍历生成子菜单项
//				$.each(subdata, function (j, subItem) {
//					strContent += "<li><div><a href=\"javascript:addTabPage('" + subItem.MENU_TEXT + "','" + subItem.URL + "')\" class='" + subItem.ICON + " left-icon'>" + subItem.MENU_TEXT + "</a></div></li>";
//				})
//				strContent += "</ul>";
//				/*var pnl = $('#nav').accordion('add', {
//					id: FD[i].INDX,
//					title: FD[i].MENU_TEXT,
//					content: strContent,
//					selected: false,
//					iconCls: FD[i].ICON,
//					animate:true
//				});*/
//				ordernum++;
//			}
//			//console.log($('#nav li a'))
//			/*$('#nav li a').click(function () {
//				LoadingShow();
//				$('#nav li div').removeClass("selected");
//				$(this).parent().addClass("selected");
//			}).hover(function () {
//				$(this).parent().addClass("hover");
//			}, function () {
//				$(this).parent().removeClass("hover");
//			});*/
//		},
//		error: function (d) {
//			alert("error");
//		}
//	});
}

mainPlatform.init();



$(document).ready(function(){
	var Swidth = $(window).width();
	if( Swidth >= 1503 ){
		$('.pf-nav-prev,.pf-nav-next').hide();
//		console.log('hide');
	}else{
		$('#pf-hd .pf-nav-wrap .pf-nav-ww').width(560);
		$('.pf-nav-prev,.pf-nav-next').show();
//		console.log('show');
	}

	$(window).resize(function() {
		var Swidth = $(window).width();
//		console.log(Swidth);
		if( Swidth >= 1503 ){
			$('.pf-nav-prev,.pf-nav-next').hide();
			$('#pf-hd .pf-nav-wrap .pf-nav-ww').width(1080);
		}else{
			$('#pf-hd .pf-nav-wrap .pf-nav-ww').width(560);
			$('.pf-nav-prev,.pf-nav-next').show();
		}
	});
});