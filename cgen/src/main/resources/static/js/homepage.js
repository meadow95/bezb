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

function addCertTr(cert , exp) {  //dodaje u tabelu sve sertifikate
	let tr = $('<tr></tr>');
	let tdSerial = $('<td hidden = "true">' + cert.serial + '</td>');
	let tdCommonName = $('<td>' + cert.commonName + '</td>');
	let tdGivenName = $('<td>' + cert.givenname + '</td>');
	let tdSurname = $('<td>' + cert.surname + '</td>');
	let tdOrganization = $('<td>' + cert.organization + '</td>');
	let tdOrganizationUnit = $('<td>' + cert.organizationUnit + '</td>');
	let tdCountry = $('<td>' + cert.country + '</td>');
	let tdEmail = $('<td>' + cert.email + '</td>');
	let tdExp = $('<td>' + exp + '</td>');
	tr.append(tdSerial).append(tdCommonName).append(tdGivenName).append(tdSurname).append(tdCountry).append(tdEmail).append(tdExp);
	tr.click(clickClosure(cert));
	$('#tabela tbody').append(tr);
}


$(document).ready(function() {
	
	$.get({
		url: '/api/user/isLoggedAdmin',
		success: function(result) {
			
			if(result == 0 || result == 2){
				
				alert("Niste ulogovani kao Admin, nemate pristup ovoj stranici");
				window.location.replace('index.html');
				
			}
			else{
					
					$.get({
						url: '/api/certificate',
						success: function(certs) {
							
							if(certs == null){
								
								//window.location.replace('webshopuser.html');
								
								alert("Nema sertifikata!");
								
							}
							else{
								
							for (let cert of certs) {
								
								
								let id = cert.serial;
								let exp;
								
								$.get({
									url: '/api/certificate/checkExpiration/' + id,
									success: function(bool) {
										
										if(bool == true){
											
											exp = "Valid";
											addCertTr(cert, exp);
													
										}
										else{
											
											exp = "Expired";
											addCertTr(cert, exp);
											
										}
									}
								});
								
								
								}
							}
						}
					})
			
			
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

					alert("Sertifikat nije povucen!");
					return;
				}
				else{
					alert("Sertifikat je povucen!");
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
	
	$('#download').on('click', function(e){               		//pravi pdf sertifikata
		var id = ($("#tabela tr.selected td:first").text());
		if(id==="")
			{

			alert("Niste oznacili sertifikat");
			
			return;
			}

		$.get({
			url: 'api/certificate/download/' + id,
			dataType: "text",
			success: function(check) {
				
				if(check){

					alert("Uspesno kreiran PDF sertifikata!");
					return;
				}
				else{
					alert("Nije uspelo kreiranje PDF-a sertifikata!");
					return;
					
				}
			}
		});
		
	});
	


	
	
});

