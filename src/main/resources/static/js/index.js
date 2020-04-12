function json(res) {
    return res.json();
}

function clickClosure(cert){
	return function() {
		// Parametar cert prosleđen u gornju funkciju će biti vidljiv u ovoj
		// Ovo znači da je funkcija "zapamtila" za koji je proizvod vezana
		$('tr.selected').removeClass('selected');
		$(this).addClass('selected');
	};
}

function addCertTr(cert) {  //dodaje u tabelu sve sertifikate
	let tr = $('<tr></tr>');
	let tdSerial = $('<td>' + cert.serial + '</td>');
	let tdCommonName = $('<td>' + cert.commonName + '</td>');
	let tdGivenName = $('<td>' + cert.givenname + '</td>');
	let tdSurname = $('<td>' + cert.surname + '</td>');
	let tdOrganization = $('<td>' + cert.organization + '</td>');
	let tdOrganizationUnit = $('<td>' + cert.organizationUnit + '</td>');
	let tdCountry = $('<td>' + cert.country + '</td>');
	let tdEmail = $('<td>' + cert.email + '</td>');
	tr.append(tdSerial).append(tdCommonName).append(tdGivenName).append(tdSurname).append(tdOrganization).append(tdOrganizationUnit).append(tdCountry).append(tdEmail);
	tr.click(clickClosure(cert));
	$('#tabela tbody').append(tr);
}


$(document).ready(function() {
	
	
	$.get({
		url: '/api/certificate',
		success: function(certs) {
			
			if(certs == null){
				
				//window.location.replace('webshopuser.html');
				
				alert("Nema sertifikata!");
				
			}
			else{
				
			for (let cert of certs) {
				
				addCertTr(cert);
				
				}
			}
		}
	});
	
	
	$('#check').on('click', function(e){               		//proverava da li je aktivan sertifikat
		var id = ($("#tabela tr.selected td:first").text());
		if(id==="")
			{

			alert("Niste oznacili sertifikat");
			
			return;
			}

		$.get({
			url: 'api/certificate/check/' + id,
			success: function(check) {
				
				if(check){

					alert("Validan sertifikat!");
					return;
				}
				else{
					alert("Nevalidan sertifikat!");
					return;
					
				}
			}
		});
		
	});
	
	
	$('#revoke').on('click', function(e){               		//povlaci sertifikat
	    //alert($("#tabela tr.selected td:first").html());	

		var id = ($("#tabela tr.selected td:first").text());
		if(id==="")
			{

			alert("Niste oznacili sertifikat");
			
			return;
			}

		$.ajax({
			url: 'api/certificate/revoke/' + id,
			type: 'PUT',
			success: function(check) {
				
				if(check){

					alert("Sertifikat je uspesno povucen!");
					return;
				}
				else{
					alert("Sertifikat je vec povucen!");
					return;
					
				}
			}
		});
		
	});

	
	
});

