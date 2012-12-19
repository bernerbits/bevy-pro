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
	$('#credit-span').text(m.credit);
	if(m.credit == 0) {
		$('#refund-button a').addClass('inactive');
	} else {
		$('#refund-button a').removeClass('inactive');
	}
	$('#message-div').text(m.message);
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
		setTimeout(7000, "poll()");
	});
};
$(function() {
	poll();
});