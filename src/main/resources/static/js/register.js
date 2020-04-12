document.getElementById('registerf').addEventListener('submit', e => {
    e.preventDefault();
    
    const pass =  document.getElementById('password').value;
    const repass = document.getElementById('password-confirm').value;

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
	        location.href="index.html";  
	    }).catch(err => {
	        alert('Greska pri registraciji!');
	        console.log(err);
	    }); 
	    
    }else{
    	alert("Lozinke se razlikuju!");
    }
});
 
