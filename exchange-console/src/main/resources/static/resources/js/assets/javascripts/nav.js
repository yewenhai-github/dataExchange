		function setMenuList()
		{
			var body, content, nav, nav_closed_width, nav_open, nav_toggler;
	
			nav_toggler = $("header .toggle-nav");
			nav = $("#main-nav");
			content = $("#content");
			body = $("body");
			nav_closed_width = 50;
			nav_open = body.hasClass("main-nav-opened") || nav.width() > nav_closed_width;
			$("#main-nav .dropdown-collapse").on("click", function (e) {
			    var link, list, url;
			    if (e.srcElement.parentElement.href != undefined) {
			        url = e.srcElement.parentElement.href.replace('#', '');
			        if (url != "" && url != "#" && url != window.location.href) {

			        	$('.selLeftText').removeClass("selLeftText");
		                e.srcElement.parentElement.getElementsByTagName("span")[0].className = "selLeftText";
		                var title=e.srcElement.innerText;
		                if(e.currentTarget.Node.Parent.Text!=null){
		                	title=e.currentTarget.Node.Parent.Text+"-"+e.srcElement.innerText;
		                }
			        	addTab(title,url);

			        }
			    }
			    else {
			        if (e.srcElement.parentElement.getElementsByTagName('a').length > 0) {
			            url = e.srcElement.parentElement.getElementsByTagName('a')[0].href;
			            if (url != "" && url != "#" && url != window.location.href) {
			                $('.selLeftText').removeClass("selLeftText");
			                e.srcElement.parentElement.getElementsByTagName("span")[0].className = "selLeftText";

			              //  $("#" + e.srcElement.parentElement.getElementsByTagName('a')[0]).attr("src", url);
			            }
			        }
			    }
			    e.preventDefault();
			    link = $(this);
			    list = link.parent().find("> ul");
			    
			    var all_menu = list.parent().parent().find("> li").find("> ul");
			    
			    
			    if (list.is(":visible")) {
			        if (body.hasClass("main-nav-closed") && link.parents("li").length === 1) {
			            false;
			        } else {
			            link.removeClass("in");
			            list.slideUp(300, function () {
			                return $(this).removeClass("in");
			            });
			        }
			    } else {
			        link.addClass("in");
			        
			        all_menu.each(function(){
			        	$(this).slideUp(function(){
			        		return $(this).removeClass("in");
			        	});
			        });
			        
			        list.slideDown(300, function () {
			            return $(this).addClass("in");
			        });
			    }
			    return false;
			});
			//
			nav.swiperight(function(event, touch) {
				return $(document).trigger("nav-open");
			});
			nav.swipeleft(function(event, touch) {
				return $(document).trigger("nav-close");
			});
			nav_toggler.on("click", function() {
				if (nav_open) {
					$(document).trigger("nav-close");
				} else {
					$(document).trigger("nav-open");
				}
				return false;
			});
			$(document).bind("nav-close", function(event, params) {
				body.removeClass("main-nav-opened").addClass("main-nav-closed");
				return nav_open = false;
			});
			return $(document).bind("nav-open", function(event, params) {
				body.addClass("main-nav-opened").removeClass("main-nav-closed");
				return nav_open = true;
			});
		}