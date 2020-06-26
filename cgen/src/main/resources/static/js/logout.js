$(document).ready(function() {

	$('#logout').on('click', function(e) {

		
		$.get({
			url: 'api/user/logout',
			success: function(check) {
				
				window.location.replace('index.html');
				
			}
		});
		
		
		
		
		
	});
});