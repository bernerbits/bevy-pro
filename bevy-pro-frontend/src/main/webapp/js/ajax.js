$(function() {
	// Make links send AJAX requests instead of forcing a page reload. 
	// The updater will handle any model updates.
	$('#beverage-buttons a').attr('href','#');
	$('#beverage-buttons a').click(function(e) {
		var id = $(e.currentTarget).parent()[0].id.substr(4); // Chop off "bev-" from parent id to get beverage id. Hackish but it works fine.
		$.getJSON('./dispense/' + id + '.json')
		.error(function(xhr,options,error) {
			if(error != '') {
				$('#message-div').text('Communication Error');
			}
		});
		return false;
	});
	$('#refund-button a').attr('href','#');
	$('#refund-button a').click(function() {
		$.getJSON('./refund.json')
		.error(function(xhr,options,error) {
			if(error != '') {
				$('#message-div').text('Communication Error');
			}
		});
		return false;
	});
});