/*
document.getElementById('loginf').addEventListener('submit', e => {

    e.preventDefault();
    const userData = {
        username : document.getElementById('username').value,
        password : document.getElementById('password').value
    }
      
    var n = userData.password.includes("'");
    var u = userData.username.includes("'");

    if(n || u){
    	
    	alert("Neki od unosa poseduje apostrof!");
    }
    
    else
    {
    	return fetch(`/api/user/login`, {
    		method: 'post',
            headers: {
                'content-type' : 'application/json'
            },
            body: JSON.stringify(userData),
            credentials: 'include'
    	}).then(user=>{
    		if(user){
    			console.log(user);
        alert('Korisnik uspesno ulogovan');
 //      sessionStorage.setItem("loggedUser", user);
       location.href="homepage.html"; 
    		}		
    		else
    			alert('Pogresan username ili lozinka!');
    }).catch(err => {
        alert('Greska pri logovanju!');
        console.log(err);
    });
}
});
*/
$(document).ready(function() {
	


	$('#loginf').on('submit', function(event) {
		// Ne radi nista!!!.
		event.preventDefault();
		
		let name = $('input[id="username"]').val();
		let password = $('input[id="password"]').val();
		let ADMIN = "ADMIN";
		let USER = "USER";
		
		
		
		$.post({
			url: '/api/user/login', //rest/login???
			data: JSON.stringify({username: name, password: password}), 
			contentType: 'application/json',
//			dataType:'text',
			success: function(response) {
				console.log(response);

		
		if(response == null){
			
		
			alert('Pogresan username ili lozinka!');
		
		}		
		else
			{
 
			for (let resp of response) {
				
				console.log(resp);
				if(resp === ADMIN)
					{
					alert('Korisnik uspesno ulogovan');
					location.href="homepage.html";
					return;
					}
				
			}
			
			for (let resp of response) {
				
				console.log(resp);
				if(resp === USER){
					
					alert('Korisnik uspesno ulogovan');
					location.href="user.html";
					return;
				}
				
			}

					alert('Nepoznat user');
					location.href="index.html";
					return;
				}
				
	    //   sessionStorage.setItem("loggedUser", user);
			
			},
			error: function(response) {
				console.log("Desila se greska pri logovanju!");
			}
		});
	});
});