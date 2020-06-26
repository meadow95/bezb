function json(res) {
    return res.json();
}

$.get({
	url: '/api/user/isLoggedAdmin',
	success: function(result) {
		
		if(result == 0 || result == 2){
			
			alert("Niste ulogovani kao Admin, nemate pristup ovoj stranici");
			window.location.replace('index.html');
			
		}

	}
}); 

fetch('/api/certificate?type=ca', {
    credentials: 'include'
		}).then(json).then(certificates => {
    const dropdown = document.getElementById('signerSerialNumber');
    
    for (let cert of certificates) {
        dropdown.innerHTML += `<option value="${cert.serial}">${cert.commonName}</option>`
    }
});   

document.getElementById('certf').addEventListener('submit', e => {
    e.preventDefault();
    
    const serial =  document.getElementById('signerSerialNumber').value; 

    const certData = {
        commonName : document.getElementById('cn').value,
        givenname : document.getElementById('surname').value,
        surname : document.getElementById('givenName').value,
        organization : document.getElementById('o').value,
        organizationUnit : document.getElementById('ou').value,
        country : document.getElementById('c').value,
        email : document.getElementById('e').value,
        endDate : document.getElementById('date').value
    }

    fetch(`/api/certificate/${serial}/signable`, {
        method: 'POST', 
        headers: {
            'content-type' : 'application/json'
        },
        body: JSON.stringify(certData),
        credentials: 'include'
    }).then(json).then(certificate => { 
        alert('Uspesno izdat CA sertifikat');
        console.log(certificate);
    }).catch(err => { 
        console.log(err);
    });
});
