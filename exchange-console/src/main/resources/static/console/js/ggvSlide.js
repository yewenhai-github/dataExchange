/* @version 1.0 | 20171019
 * @license: none (public login)
 */
/**
	*网页幻灯片插件
	*作者：郭徵
	*开发日期：2014-07-03
	*备注：需要html4版本以上html、jquery 1.3版本以上js框架
**/

$(function(){
$.fn.slide = function(o) {
	o = $.extend({}, o || {});
			
	return this.each(function(){
		var me = $(this),
			compents = new SlideCompents(),
			counter = new Counter(compents, me);
			
		//载入参数
		$.each(o, function(i, n){
			//当参数为counter，则判断为计数器		   
			if (i.toString() === "counter") {
				counter.getParam(n);
			} else {
			//当参数不为counter，则判断为具体组件，需声明并添加该组件
				var compent = new SlideCompent(counter, me.find(i.toString()), n, o.counter.begin);
				compents.add(compent);
			}		   
		});
		//计数器开始工作
		counter.active();
	});
};
}); 

//========
	//计数器模块
	//用于接受具体组件节点指令,加以处理后控制组件总节点动作
//========
function Counter(compents, dom) {
	var oldNum = -1,             //上一操作节点序号
		newNum = 0,              //当前操作节点序号
		auto,                    //幻灯片是否需要循环播放
		len,                     //幻灯片片数
		duration,                //延时播放秒数（毫秒）
		compents = compents,
		timer = null,            //延时播放
		arr = 0,                  //完成动作后的节点数
		that;
	function getNum(num) {
		if (num < 0) {
			num = len - 1;
		}
		if (num > len - 1) {
			num = 0;
		}
		return num;
	}
	//总节点动作开始执行
	function publish() {
		compents.active(oldNum, newNum);
	}
	that = {
		//获得参数
		getParam: function(param) {
			auto = param.auto;
			//len = param.len;
			len = dom.find(param.len).size();
			duration = param.duration;
			newNum = getNum(param.begin);
			oldNum = getNum(param.begin - 1);
		},
		//计数器开始工作
		active: function() {
			timer = auto ? setTimeout(publish, duration) : null;
		},
		//计数器回复播放
		play: function() {
			timer = setTimeout(function(){ 
				publish(); 						
			}, duration);
			arr = 0;
		},
		//计数器停止播放
		stop: function() {
			clearTimeout(timer);
			arr = 0;
		},
		//计数器从某一位置开始播放
		choose: function(num) {
			clearTimeout(timer);
			oldNum = newNum;
			newNum = num;
			arr = 0;
			publish(); 
		},
		//计数器从下一位置开始播放
		next: function() {
			clearTimeout(timer);
			oldNum = newNum;
			newNum = getNum(newNum + 1);
			arr = 0;
			publish(); 
		},
		//计数器从上一位置开始播放
		preview: function() {
			clearTimeout(timer);
			oldNum = newNum;
			newNum = getNum(newNum - 1);
			arr = 0;
			publish(); 
		},
		//计数器判断是否所有节点都完成了工作
		finish: function(index) {
			//console.log(arr);
			arr += index;
			if (arr === compents.length() && auto) {
				timer = setTimeout(function(){ 
					oldNum = newNum;
					newNum = getNum(++newNum);
					publish(); 
				}, duration);
				arr = 0;
			}
		}
	};
	return that;	
}
//========
	//总节点模块
	//用于向下对具体节点传送指令
//========
function SlideCompents() {
	var arr = [],
		i,
		len,
		that;
	that = {
		//每个节点都执行命令
		active: function(oldNum, newNum) {
			for (i = 0, len = arr.length; i < len; i++) {
				arr[i].active(oldNum, newNum);
			} 
		},
		//添加节点
		add: function(compent) {
			arr.push(compent);
		},
		length: function() {
			return len;	
		}
	};
	return that;
} 
//========
	//具体节点模块
	//用于向计数器传送指令
//========
function SlideCompent(counter, dom, param, begin) {
	var counter = counter,
		dom = dom,
		begin = begin,
		animateFun = null,
		that = {
			//模块已完成动作，并通知计数器
			active : function(oldNum, newNum) {
				counter.finish(animateFun.active(oldNum, newNum));
			},
			play: function(event) {
				counter.play();
			},
			stop: function(event) {
				counter.stop();
			},
			choose: function(event) {
				var node = dom.children()[0].nodeName.toLowerCase(), num;
				if ($(event.target)[0].nodeName.toLowerCase() === node) {
					num = $(event.target).index();
				} else {
					num = $(event.target).parents(node).index();
				}
				counter.choose(num);
			},
			preview: function(event) {
				counter.preview();
			},
			next: function(event) {
				counter.next();
			}
		};
	//通过参数判断加载具体动画动作
	$.each(param, function(i, n){
		if (i.toString() === "active") {
			animateFun = new n(dom);
		} else if (i.toString() === "param") {
			animateFun.getParam(n);
		} else {
			if (typeof(n) === "string") {
				dom.bind(i.toString(), that[n]);
			} else {
				/*var n = new n();
				dom.bind(i.toString(), n);*/
			}
		}		  
	});
	//具体动画动作初始化
	animateFun.init(begin);
	return that;
}
//========
	//初始化动画方法
//========
//普通切换
function initShow(dom, param, begin, loop) {
	var l = 0;
	if (loop != undefined) {
		l = dom.children().size() / 3;
	}
	dom.children().hide().eq(l + begin).show().addClass(param.css);
}
//普通添加样式
function initAddClass(dom, param, begin, loop) {
	var l = 0;
	if (loop != undefined) {
		l = dom.children().size() / 3;
	}
	dom.children().eq(l + begin).addClass(param.css);
}
//显示第一个
function initFirstShow(dom, param, begin) {
	dom.children().hide();
	dom.children().eq(begin).show();
	return begin;
}

function initClassFun(dom, style, begin, loop) {
	var html = dom.html(),
		css = "",
		first = dom.children().eq(0),
		l = dom.children().size(),
		s = 0,
		r = 0,
		bs = 0;
	if (style == "width") {
		s = first.outerWidth(true);
		r = l - Math.ceil(dom.parent().outerWidth() / s);
		css = "left";
	}
	if (style == "height") {
		s = first.outerHeight(true);
		r = l - Math.ceil(dom.parent().outerHeight() / s);
		css = "top";
	}
	if (loop) {
		dom.html(html + html + html);
		dom.css(style, s * l * 3);
		bs = l;
		
	} else {
		dom.css(style, s * l);		
	}
	if (begin < r) {
		dom.css(css,(begin + bs) * -s);
	} else {
		dom.css(css,(r + bs) * -s);
	}
	return r;
}

//横向排列
function initHorizontalArray(dom, param, begin) {	
	return initClassFun(dom, "width", begin, false);
}
//纵向排列
function initDirectionArray(dom, param, begin) {
	return initClassFun(dom, "height", begin, false);
}
//横向重复排列
function initLoopHorizontalArray(dom, param, begin) {
	initClassFun(dom, "width", begin, true);
	return begin;
}
//纵向重复排列
function initLoopDirectionArray(dom, param, begin) {
	initClassFun(dom, "height", begin, true);
	return begin;
}
//========
	//具体执行动画方法
//========
//加载图片
function loadImage(dom, callback) {
	var src,
		$image = dom.find("img");
	$image.error(function() {
		dom.attr("name", "loaded");
	})
	$image.bind("load", function(evt){
		$image.unbind("load");
		callback();
	}).each(function(){
		if ($image[0].complete) {
			$image.trigger("load");
		}
		src = $(this).attr("src");
		if (/webkit/.test(navigator.userAgent.toLowerCase())) {
			$(this).attr("src", "");
		}
		$(this).attr("src", src);
	});
	return 1;	
}
//加载图片后处理
function afterLoadImg(dom, newNum, callback) {
	if (dom.attr("name") == undefined) {
		var img = dom.find("img");
		if (img.size() > 0) {
			return loadImage(dom, function(){
				callback();
				dom.attr("name", "loaded");
			})
		} else {
			callback();
			dom.attr("name", "loaded");
		}
	} else {
		callback();
	}
	return 1;	
}
//普通切换
function activeShow(dom, oldNum, newNum, param, loop) {
	var l = 0;
	if (loop != undefined) {
		l = dom.children().size() / 3;
	}
	return afterLoadImg(dom.children().eq(newNum), newNum, function(){
		dom.children().eq(oldNum + l).hide().removeClass(param.css).end().eq(newNum + l).show().addClass(param.css);
	});
}
//普通添加样式
function activeAddClass(dom, oldNum, newNum, param, loop) {
	var l = 0;
	if (loop != undefined) {
		l = dom.children().size() / 3;
	}
	return afterLoadImg(dom.children().eq(newNum), newNum, function(){
		dom.children().eq(oldNum + l).removeClass(param.css).end().eq(newNum + l).addClass(param.css);
	});
}
//渐隐渐显
function activeFadeIn(dom, oldNum, newNum, param) {
	return afterLoadImg(dom.children().eq(newNum), newNum, function(){
		dom.children().eq(oldNum).fadeOut(param.speed).end().eq(newNum).fadeIn(param.speed);
	});
}
function activeScrollFun(dom, newNum, limit, style, s, speed) {
	if (style == "left") {
		if (newNum < limit) {				 
			dom.animate({left:-newNum * s}, speed);	
		} else {
			dom.animate({left:-limit * s}, speed);	
		}
	}
	if (style == "top") {
		if (newNum < limit) {				 
			dom.animate({top:-newNum * s}, speed);	
		} else {
			dom.animate({top:-limit * s}, speed);	
		}
	}
}
function activeScrollLoopFun(dom, oldNum, newNum, l, style, s, speed) {
	if (newNum == 0 && oldNum == l - 1) {
		dom.css(style, -s * (l - 1));
	}
	if (newNum == l - 1 && oldNum == 0) {
		dom.css(style, -s * (l * 2));
	}
	if (style == "left") {
		dom.animate({left:-(newNum + l) * s}, speed);
	}
	if (style == "top") {
		dom.animate({top:-(newNum + l) * s}, speed);
	}
}
//向左滚动
function activeScrollLeft(dom, oldNum, newNum, param, limit) {
	var s = dom.children().eq(0).outerWidth(true);
	return afterLoadImg(dom.children().eq(newNum), newNum, function(){
		activeScrollFun(dom, newNum, limit, "left", s, param.speed);
	});
}
//向上滚动
function activeScrollTop(dom, oldNum, newNum, param, limit) {
	var s = dom.children().eq(0).outerHeight(true);
	return afterLoadImg(dom.children().eq(newNum), newNum, function(){
		activeScrollFun(dom, newNum, limit, "top", s, param.speed);
	});
}
//向左循环滚动
function activeScrollLoopLeft(dom, oldNum, newNum, param) {
	var s = dom.children().eq(0).outerWidth(true),
		l = dom.children().size() / 3;
	return afterLoadImg(dom.children().eq(newNum), newNum, function(){
		activeScrollLoopFun(dom, oldNum, newNum, l, "left", s, param.speed);
	});
}
//向上循环滚动
function activeScrollLoopTop(dom, oldNum, newNum, param) {
	var s = dom.children().eq(0).outerHeight(true),
		l = dom.children().size() / 3;
	return afterLoadImg(dom.children().eq(newNum), newNum, function(){
		activeScrollLoopFun(dom, oldNum, newNum, l, "top", s, param.speed);
	});
}
//默认动画方法
function DefaultSlide(dom) {
	var that = {
		dom : dom,
		param : null,
		getParam : function(param) { that.param = param; },
		init : function(begin) {},
		active : function(oldNum, newNum){ return 1; },
		begin: null
	};
	return that;
}
//普通添加样式动画
function AddClassSlide(dom) {
	var that = DefaultSlide(dom);
	that.init = function(begin) {
		initAddClass(that.dom, that.param, begin);
	};
	that.active = function(oldNum, newNum) {
		return activeAddClass(that.dom, oldNum, newNum, that.param);
	};
	return that;
}
//普通切换动画
function ShowSlide(dom) {
	var that = DefaultSlide(dom);
	that.init = function(begin) {
		initShow(that.dom, that.param, begin);
	};
	that.active = function(oldNum, newNum) {
		return activeShow(that.dom, oldNum, newNum, that.param);
	};
	return that;
}
//渐隐渐现动画
function FadeInSlide(dom) {
	var that = DefaultSlide(dom);
	that.init = function(begin) {
		that.begin = initFirstShow(that.dom, that.param, begin);
	};
	that.active = function(oldNum, newNum) {
		if (that.begin == newNum) {
			return 1;	
		} else {
			that.begin = null;
			return activeFadeIn(that.dom, oldNum, newNum, that.param);
		}
	};
	return that;
}
//普通向左平移动画
function LeftMoveSlide(dom) {
	var that = DefaultSlide(dom),
		limit;
	that.init = function(begin) {
		limit = initHorizontalArray(that.dom, that.param, begin);
	};
	that.active = function(oldNum, newNum) {
		return activeScrollLeft(that.dom, oldNum, newNum, that.param, limit);
	};
	return that;
}
//普通向上平移动画
function TopMoveSlide(dom) {
	var that = DefaultSlide(dom),
		limit;
	that.init = function(begin) {
		limit = initDirectionArray(that.dom, that.param, begin);
	};
	that.active = function(oldNum, newNum) {
		return activeScrollTop(that.dom, oldNum, newNum, that.param, limit);
	};
	return that;
}
//向左循环平移动画
function LeftLoopMoveSlide(dom) {
	var that = DefaultSlide(dom);
	that.init = function(begin) {
		that.begin = initLoopHorizontalArray(that.dom, that.param, begin);
	};
	that.active = function(oldNum, newNum) {
		if (that.begin == newNum) {
			return 1;	
		} else {
			that.begin = null;
			return activeScrollLoopLeft(that.dom, oldNum, newNum, that.param);
		}
	};
	return that;
}
//向上循环平移动画
function TopLoopMoveSlide(dom) {
	var that = DefaultSlide(dom);
	that.init = function(begin) {
		that.begin = initLoopDirectionArray(that.dom, that.param, begin);
	};
	that.active = function(oldNum, newNum) {
		if (that.begin == newNum) {
			return 1;	
		} else {
			that.begin = null;
			return activeScrollLoopTop(that.dom, oldNum, newNum, that.param);
		}
	};
	return that;
}