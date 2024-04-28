function checkRadio()
{
	$('input[class="radio-btn"]').wrap('<div class="radio-btn"><i></i></div>');
    $(".radio-btn").on('click', function () {
        var _this = $(this),
        block = _this.parent().parent();
        block.find('input:radio').attr('checked', false);
        block.find(".radio-btn").removeClass('checkedRadio');
        _this.addClass('checkedRadio');
        _this.find('input:radio').attr('checked', true);
    });
    $('input[class="check-box"]').wrap('<div class="check-box"><i></i></div>');
    $.fn.toggleCheckbox = function () {
    	if ($(this).is(':checked') == true) {
            $(this).prop('checked', false);
            $(this).attr('checked', false);
        }
        else {
            $(this).prop('checked', true);
            $(this).attr('checked', true);
        }
//        this.attr('checked', !this.attr('checked'));
    };
    $('.check-box').on('click', function () {
//		if($(this).find(':checkbox').attr("disabled")!="disabled")
//		{
		  $(this).find(':checkbox').toggleCheckbox();
		  $(this).toggleClass('checkedBox');
//		}
    });
}
$(document).ready(function () {
	checkRadio();
});
