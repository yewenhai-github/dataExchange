$.extend($.fn.datagrid.defaults.editors, {
		select2: {
			init: function(container, options){
				var input = $('<select style="width:100%">').appendTo(container);
				return input.select2(options);
			},
			destroy: function(target){
				$(target).select2('destroy');
			},
			getValue: function(target){
				return $(target).val();//select2('getValue');
			},
			setValue: function(target, value){
				$(target).val(value).trigger("change");//.select2('setValue',value);
			},
			resize: function(target, width){
				//$(target).select2({'width':width});//.select2('resize',width);
			}
		} 
	});