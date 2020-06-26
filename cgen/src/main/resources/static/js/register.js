
/*
document.getElementById('registerf').addEventListener('submit', e => {
    e.preventDefault();
    
    const username =  document.getElementById('username').value;
    const pass =  document.getElementById('password').value;
    const repass = document.getElementById('password-confirm').value;
    
    var n = pass.includes("'");
    var u = username.includes("'");

    if(n || u){
    	
    	alert("Neki od unosa poseduje apostrof!");
    }
    
    else{
    
    if(pass == repass){
	    const userData = {
	        username : document.getElementById('username').value,
	        password : document.getElementById('password').value
	        
	    }
	   
	    	return fetch(`/api/user/register`, {
	    		method: 'post', 
	            headers: {
	                'content-type' : 'application/json'
	            },
	            body: JSON.stringify(userData),
	            credentials: 'include'
	    	}).then(user=>{
	        alert('Korisnik je registrovan');
	        console.log(user);

	    }).catch(err => {
	        alert('Greska pri registraciji!');
	        console.log(err);
	    }); 
	    
    }else{
    	alert("Lozinke se razlikuju!");
    }
    }
});
*/

$(document).ready(function() {
	
	function CheckPassword(inputtxt) 
	{ 
	var passw = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}$/;
	if(inputtxt.match(passw)) 
	{ 
	return true;
	}
	else
	{ 
	return false;
	}
	}

	$('#registerf').on('submit', function(event) {
		// Ne radi nista!!!.
		event.preventDefault();
		
		let username = $('input[id="username"]').val();
		let password = $('input[id="password"]').val();
		let repass = $('input[id="password-confirm"]').val();
		
		/* Napravi validaciju password-a
		if(password.length<1){
			
			alert("Niste uneli password");
			return false;
			
		}
		*/
	    var n = password.includes("'");
	    var u = username.includes("'");

	    if(n || u){
	    	
	    	alert("Neki od unosa poseduje apostrof!");
	    }
	    
	    else{
	    
	    if(password == repass){
	    	
	    	let username = $('input[id="username"]').val();
			let password = $('input[id="password"]').val();
			
			
			
			if(CheckPassword(password)){
				
				$.post({
					url: '/api/user/register', 
					data: JSON.stringify({username: username, password: password}), 
					contentType: 'application/json',
					success: function(response) {
						console.log("Uspesna registracija");
						alert("Uspesna registracija");
				
					},
					error: function(response) {
					
						alert("Vec postoji ovaj username!");
						
					}
				});
				
			}
			
			else{
				alert("Unesite sifru od 6 do 20 karaktera koja ima barem jedno veliko, jedno malo slovo i jedan broj!");
				
			}
 
	    }
	    
	    }
	});
});
 
