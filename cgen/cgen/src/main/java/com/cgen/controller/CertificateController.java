package com.cgen.controller;

import com.cgen.certificate.Certificate;
import com.cgen.certificate.CertificateDTO;
import com.cgen.certificate.SubjectData;
import com.cgen.model.*;

import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cgen.service.CertificateService;


@RestController
@RequestMapping("api/certificate")
public class CertificateController {

	@Autowired 
	private CertificateService certificateService; 
	private HttpSession httpSession;
	
	
	@Autowired
	public CertificateController(CertificateService certificateService, HttpSession httpSession) {
		this.certificateService = certificateService;
		this.httpSession = httpSession;	
	}
	
	@GetMapping("{serial}")
	public CertificateDTO getCertificate(@PathVariable String serial) {
		return new CertificateDTO(certificateService.getOne(serial));
	}
	
	@GetMapping("ispis/{serial}")
	public ResponseEntity<?> ispisCertificate(@PathVariable String serial) {
		String certFile = certificateService.ispis(serial);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pkix-cert")).contentLength(certFile.length()).body(certFile);
	}
	@GetMapping
	public List<CertificateDTO> getCertificates(@RequestParam(value = "type", required = false, defaultValue = "all") String f) {		
		if (f.equals("all")) {
			return certificateService.getAll();
		} else if (f.equals("ca")) {
			return certificateService.getAllCa();
		} else {
			throw new RuntimeException();
		}
	}
	@PostMapping
	public CertificateDTO postCertificate(@RequestBody @Valid Certificate cert) {
		/*
		User user = (User) httpSession.getAttribute("loggedUser");
		
		if (user == null || user.getType() == UserType.USER) {
			throw new RuntimeException("Nedozvoljen pristup!");
		}
		*/
		if (cert.getEndDate().before(new Date())) {
			throw new RuntimeException();
		}
		
		X509Certificate certificate = certificateService.add(cert);

		return new CertificateDTO(certificate);
	}
	
	@PutMapping("/revoke/{serial}")
	public boolean revokeCertificate(@PathVariable String serial) {
		/*
		User user = (User) httpSession.getAttribute("loggedUser");
		System.out.println(user);
		if (user == null || user.getType() == UserType.USER) {
			throw new RuntimeException("Nedozvoljen pristup!");
		}
		*/
		return certificateService.revokeCertificate(serial);
	}
	@PostMapping("{serial}/signable")
	public CertificateDTO postCertificateSignable(@PathVariable String serial, @RequestBody Certificate cert) {
		if (cert.getEndDate().before(new Date())) {
			throw new RuntimeException();
		} 
		/*
		User user = (User) httpSession.getAttribute("loggedUser");
		
		if (user == null || user.getType() ==  UserType.USER) {
			throw new RuntimeException("Nedozvoljen pristup!");
		}
		*/
		X509Certificate certificate = certificateService.issueCertificate(serial, cert, true);
		
		if (certificate == null) {
			throw new RuntimeException();
		}
		
		return new CertificateDTO(certificate);
	}
	@GetMapping("/check/{serial}") 
	public boolean checkCertificate(@PathVariable String serial) {
		boolean check = certificateService.IsValid(serial);
		
		
		return check;
		
	}
	@PostMapping("{serial}/end")
	public CertificateDTO postCertificateEnd(@PathVariable String serial, @RequestBody Certificate cert) {
		if (cert.getEndDate().before(new Date())) {
			throw new RuntimeException();
		}
		
		X509Certificate certificate = certificateService.issueCertificate(serial, cert, false);
		
		if (certificate == null) {
			throw new RuntimeException();
		}
		
		return new CertificateDTO(certificate);
	}

	
	
}
