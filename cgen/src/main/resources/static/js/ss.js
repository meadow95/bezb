function json(res) {
    return res.json();
}


document.getElementById('certf').addEventListener('submit', e => {
    e.preventDefault();

    const certData = {
        commonName: document.getElementById('cn').value,
        givenname: document.getElementById('surname').value,
        surname: document.getElementById('givenName').value,
        organization: document.getElementById('o').value,
        organizationUnit: document.getElementById('ou').value,
        country: document.getElementById('c').value,
        email: document.getElementById('e').value,
        endDate: document.getElementById('date').value
    };
    return fetch('/api/certificate', {
            method: 'post',
            body: JSON.stringify(certData),
            headers: {
                'content-type' : 'application/json' 
            },
            credentials: 'include'
        }).then(json).then(res => { 
        alert('Sertifikat je uspesno kreiran')
        console.log(res);
    }).catch(err => {
        alert('Sertifikat nije kreiran');
        console.log(err); 
    });
});
