function update(json) {
	var m = json.m;
	for(var i in m.bevs) {
		var bev = m.bevs[i];
		var bevElement = $('#bev-' + bev.id);
		if(bevElement.length) { // Element exists
			if(bev.soldOut) {
				bevElement.addClass('sold-out');
			} else {
				bevElement.removeClass('sold-out');
			}
		}
	}
	if($('#credit-span').text() !== m.creditString) {
		$('#credit-span').fadeOut('fast',function(){
			$('#credit-span').text(m.creditString);
			$('#credit-span').fadeIn('fast');
		});
	}
	if(m.credit == 0) {
		$('#refund-button a').addClass('inactive');
	} else {
		$('#refund-button a').removeClass('inactive');
	}
	if(m.message !== $('#message-div').text()) {
		$('#message-div').fadeOut('fast',function(){
			$('#message-div').text(m.message);
			$('#message-div').fadeIn('fast');
		});
	}
}
function poll() {
	$.getJSON('modelUpdate.json')
	.success(function(json) {
		update(json);
		poll();
	})
	.error(function(xhr,options,error) {
		if(error != '') {
			$('#message-div').text('Communication Error');
		}
		setTimeout(poll, 7000);
	});
};
$(function() {
	poll();
});