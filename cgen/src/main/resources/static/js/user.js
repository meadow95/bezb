$(document).ready(function() {
	
	$.get({
		url: '/api/user/isLoggedUser',
		success: function(result) {
			
			if(result == 0 || result == 2){
				
				alert("Niste ulogovani kao User ili Admin, nemate pristup ovoj stranici");
				window.location.replace('index.html');
				
			}

		}
	});
	
	
});