package com.cgen.controller;

import com.cgen.certificate.Certificate;
import com.cgen.certificate.CertificateDTO;
import com.cgen.certificate.SubjectData;
import com.cgen.config.Config;
import com.cgen.model.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
import org.xml.sax.SAXException;

import com.cgen.service.CertificateService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.itextpdf.text.pdf.parser.Path;
import com.itextpdf.text.pdf.parser.clipper.Paths;


@RestController
@RequestMapping("api/certificate")
public class CertificateController {

	@Autowired 
	private CertificateService certificateService; 
	private HttpSession httpSession;
	private Config config;
	private SAXParserFactory factory;
	
	
	@Autowired
	public CertificateController(CertificateService certificateService, HttpSession httpSession) {
		this.certificateService = certificateService;
		this.httpSession = httpSession;	
	}
	
	public boolean isLoggedIn() {
		
		User user = (User) httpSession.getAttribute("loggedUser");
		if(user != null) {
			Collection<Role> roles = new HashSet<>();
			roles=user.getRoles();
			String name = "ADMIN";
 
		    for (Role user_list : roles) {
		    	
		    	if(name.equals(user_list.getName())) {
		    			    		
		    		return true;
		    		
		    	}
				
	        }
			
			return false;
		}else {
			return false; 	 
		} 
		
		
		
	}
	
	@GetMapping("{serial}")
	public CertificateDTO getCertificate(@PathVariable String serial) {
		return new CertificateDTO(certificateService.getOne(serial));
	}
	
	@GetMapping("/checkExpiration/{serial}")
	public boolean checkCertificateExpiration(@PathVariable String serial) {
		
		boolean ret = certificateService.checkExpiration(serial);
		
		return ret;
	}
	
	@GetMapping("ispis/{serial}")
	public ResponseEntity<?> ispisCertificate(@PathVariable String serial) {

		
		String certFile = certificateService.ispis(serial);

		return ResponseEntity.ok().contentType(MediaType.parseMediaType("application/pkix-cert")).contentLength(certFile.length()).body(certFile);

	}
	@GetMapping
	public List<CertificateDTO> getCertificates(@RequestParam(value = "type", required = false, defaultValue = "all") String f) throws SAXException, ParserConfigurationException {
		
	
	//	CertificateService.setFeaturesBySystemProperty(factory);
		
		if (f.equals("all")) {
			if(isLoggedIn()) {return certificateService.getAll();}
			return null;
			
		} else if (f.equals("ca")) {
			if(isLoggedIn()) {return certificateService.getAllCa();}
			return null;
		} else {
			throw new RuntimeException();
		}
	}
	@PostMapping
	public CertificateDTO postCertificate(@RequestBody @Valid Certificate cert) {  //izdaje selfsigned
		/*
		User user = (User) httpSession.getAttribute("loggedUser");
		
		if (user == null || user.getType() == UserType.USER) {
			throw new RuntimeException("Nedozvoljen pristup!");
		}
		*/
		if(isLoggedIn()) {
			
			if (cert.getEndDate().before(new Date())) {
				throw new RuntimeException();
			}
			
			X509Certificate certificate = certificateService.add(cert);

			return new CertificateDTO(certificate);
			
		}
		
		else {return null;} 

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
		if(isLoggedIn()) 
		{
			return certificateService.revokeCertificate(serial);
		}
		else
			return false;
		
		
	}
	@PostMapping("{serial}/signable") //izdaje CA
	public CertificateDTO postCertificateSignable(@PathVariable String serial, @RequestBody Certificate cert) {
		
		if(isLoggedIn()) 
		{
			
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
		else
			return null;
		

	}
	
	@GetMapping("/check/{serial}") 
	public boolean checkCertificate(@PathVariable String serial) {
		
		if(isLoggedIn()) {
			boolean check = certificateService.IsValid(serial);

			return check;

		}
		else
			return false;

		
	}
	@PostMapping("{serial}/end") //izdaje obican sertifikat
	public CertificateDTO postCertificateEnd(@PathVariable String serial, @RequestBody Certificate cert) {

		
		if(isLoggedIn()) 
		{
			if (cert.getEndDate().before(new Date())) {
				throw new RuntimeException();
			}
			
			X509Certificate certificate = certificateService.issueCertificate(serial, cert, false);
			
			if (certificate == null) {
				throw new RuntimeException();
			}
			
			return new CertificateDTO(certificate);

		}
		else
			return null;
		

	}
	
	
	@GetMapping("/download/{serial}")
	@JsonIgnore
	public boolean downloadCertificate(@PathVariable String serial, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		if(isLoggedIn()) {
			
			final int ARBITARY_SIZE = 1048;
			boolean pdf = certificateService.PDFGenerator(serial);
			
		  //    response.setContentType("text/plain");
		  //    response.setHeader("Content-disposition", "attachment; filename=sample.txt");
		      
			InputStream in = CertificateController.class.getResourceAsStream("/sample.txt");
			
		    	  InputStreamReader inputReader = new InputStreamReader(in);
		    	  BufferedReader reader = new BufferedReader(inputReader);
		    	  String line = null;
		            byte[] buffer = new byte[ARBITARY_SIZE];
		         
		            int numBytesRead;
		            
		          //  OutputStream out = response.getOutputStream();
		            /*
		            while ((numBytesRead = in.read(buffer)) >= 0) {
		                out.write(buffer, 0, numBytesRead);
		            }
		            */
		            
		            while ((line = reader.readLine()) != null) {
		               System.out.println(line);
		            }
		            
		            
		            /*
		            PrintWriter out = response.getWriter();
		            PrintWriter p = new PrintWriter(out);
		            	            
		            while ((line = reader.readLine()) != null) {
			               p.println(line);  
			            }
		            */
		            in.close();
//		            out.flush();
//		            out.close();
		            return pdf;
			
			
		}
		else
			return false;
		

	}
	
	/*
	@GetMapping("/download/{serial}")
	@JsonIgnore
	public boolean downloadCertificate(@PathVariable String serial, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		final int ARBITARY_SIZE = 1048;
		boolean pdf = certificateService.PDFGenerator(serial);
		
		
		try {
			
			String filepath = "C:/Users/Jovana/Desktop/sample.zip";
			String filename = "sample.zip";
			FileInputStream filetodownload = new FileInputStream(filepath);
			ServletOutputStream out = response.getOutputStream();
			response.setContentType("application/zip");
			response.setHeader("Content-disposition", "attachment; filename = " +filename);
			response.setContentLength(filetodownload.available());
			
			int c;
			
			while((c=filetodownload.read()) != -1) {
				
				out.write(c);
				
			}
			
			out.flush();
			out.close();
			filetodownload.close();
			
		}catch(Exception e) {
			
			
		}finally {
			

			
		}
		
		return pdf;
     
		
	}
*/
	
	
}
