document.getElementById('loginf').addEventListener('submit', e => {
    e.preventDefault();
    const userData = {
        username : document.getElementById('username').value,
        password : document.getElementById('password').value
    }
    
    	return fetch(`/api/user/login`, {
    		method: 'post',
            headers: {
                'content-type' : 'application/json'
            },
            body: JSON.stringify(userData),
            credentials: 'include'
    	}).then(user=>{
        alert('Korisnik uspesno ulogovan');
        sessionStorage.setItem("loggedUser", user);
        location.href="index.html"; 
    }).catch(err => {
        alert('Greska pri logovanju!');
        console.log(err);
    });
    
});