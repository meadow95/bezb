package com.cgen.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyPair;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64.InputStream;

import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.bouncycastle.asn1.x500.X500Name;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;

import com.cgen.certificate.Certificate;
import com.cgen.certificate.CertificateDTO;
import com.cgen.certificate.CertificateGenerator;
import com.cgen.certificate.IssuerData;
import com.cgen.certificate.SubjectData;
import com.cgen.repository.KSRepository;



@Service
public class CertificateService {
	 
	private final CertificateGenerator certificateGenerator; 
	@Autowired
	private final KSRepository repository; 
	
	 
	@Autowired
	public CertificateService(CertificateGenerator certificateGenerator, KSRepository repository) {
		this.certificateGenerator = certificateGenerator;
		this.repository = repository;
	}

	public X509Certificate getOne(String serial) {
		return repository.getCertificate(serial).orElseThrow(RuntimeException::new);
	} 
	
	public List<CertificateDTO> getAll() {

		return repository.getCertificates().stream().map(CertificateDTO::new).collect(Collectors.toList());

	}
	public List<CertificateDTO> getAllCa() {
		
		List<X509Certificate> certs = new ArrayList<>();
		List<X509Certificate> certsValid = new ArrayList<>();
		
		certs = repository.getCertificates();
		
		for (X509Certificate cert : certs) {

			if(IsValid(cert.getSerialNumber().toString()) == true) {
				
				certsValid.add(cert);
//				System.out.println(cert.getSerialNumber().toString());
			}
			
		}
		
		return certsValid.stream().filter(c -> c.getBasicConstraints() != -1).map(CertificateDTO::new).collect(Collectors.toList());
		
	}
	
	public X509Certificate add(Certificate cert) {		
		try {
			X500Name x500name = KeysGenerator.generateX500Name(cert);
			KeyPair keyPair = KeysGenerator.generateKeyPair();

			SubjectData subjectData = new SubjectData(keyPair.getPublic(), x500name, new Date(), cert.getEndDate());
			IssuerData issuerData = new IssuerData(keyPair.getPrivate(), x500name);

			X509Certificate certificate = certificateGenerator.generateCertificate(subjectData, issuerData, true);	

			repository.saveCertificateSS(certificate.getSerialNumber().toString(), keyPair.getPrivate(), certificate);
		
			return certificate;
		} catch(Exception e) {
			e.printStackTrace(); 
//			throw new RuntimeException();
		}
		
		return null;
		
	}
	public X509Certificate issueCertificate(String issuerSerial, Certificate cert, boolean isCa) {
		IssuerData issuerData = repository.getIssuerData(issuerSerial);
		
		if (issuerData == null || getOne(issuerSerial).getBasicConstraints() == -1) {
			throw new RuntimeException();
		}

		try {
			KeyPair keyPair = KeysGenerator.generateKeyPair();
			X500Name x500name = KeysGenerator.generateX500Name(cert);
			SubjectData subjectData = new SubjectData(keyPair.getPublic(), x500name, new Date(), cert.getEndDate());
			X509Certificate certificate = certificateGenerator.generateCertificate(subjectData, issuerData, isCa);
			
			if(isCa) {
				
				repository.saveCertificateCA(certificate.getSerialNumber().toString(), keyPair.getPrivate(), certificate);
				
			}
			else {
				
				repository.saveCertificate(certificate.getSerialNumber().toString(), keyPair.getPrivate(), certificate);
				
			}

			return certificate;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public boolean revokeCertificate(String serial) {
		X509Certificate certificate = getOne(serial);
		ArrayList<String> revokedSerials = new ArrayList<String>();
	//	List<X509Certificate> revokeList = new ArrayList<X509Certificate>();
		
		try {
			File file2 = new File("./revokeList.crl");

			if(!IsValid(serial)) {
				
				return false;
				
			}
			
			String issuer = certificate.getSubjectX500Principal().getName();
			
			List<X509Certificate> allCertificates = repository.getCertificates();
			
			List<X509Certificate> revokeList = allCertificates
				.stream()
				.filter(c -> c.getIssuerX500Principal().getName().equals(issuer))
				.collect(Collectors.toList());
			

			BufferedWriter writer = null;
			
			writer = new BufferedWriter(new FileWriter(file2, true));
			
			if(revokeList.isEmpty() == false) {
				/*
			for (X509Certificate cert : revokeList) {

				writer.append(cert.getSerialNumber().toString());
				writer.append('\n');
				
			}
			*/
				
				recursion(allCertificates, revokeList, allCertificates);
			}
			writer.append(serial);
			writer.append('\n');
			
			
			writer.close();

		
			
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return true;
	} 
	
	private void saveCRL(List<X509Certificate> certificates, File file) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(certificates);
		oos.flush();
		oos.close();
	}
	
	public void recursion(List<X509Certificate> certificates, List<X509Certificate> revokeList, List<X509Certificate> allCertificates) throws Exception {
		
		File file2 = new File("./revokeList.crl");
	
		
		revokeList.forEach(rc -> {
			List<X509Certificate> childRevokeList = allCertificates
				.stream()
				.filter(c -> c.getIssuerX500Principal().getName().equals(rc.getSubjectX500Principal().getName()))
				.collect(Collectors.toList());
			
		//	certificates.addAll(childRevokeList);
		//	allCertificates.removeAll(childRevokeList);
			
			BufferedWriter writer = null;
			
			try {
				writer = new BufferedWriter(new FileWriter(file2, true));
				
				writer.append(rc.getSerialNumber().toString());
				writer.append('\n');
				
				for (X509Certificate cert : childRevokeList) {

					writer.append(cert.getSerialNumber().toString());
					writer.append('\n');
					
				}
				
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}
					
			try {
				writer.close();
				
				if(revokeList.isEmpty() == false) {
					
					recursion(certificates, childRevokeList, allCertificates);
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
	}
	
	public boolean IsValid(String serial) {
		
		X509Certificate cert = getOne(serial);
		File file2 = new File("./revokeList.crl");
		ArrayList<String> revokedSerials = new ArrayList<String>();
		
		/*
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file2, false));
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		if (!file2.exists()) {
			return true;
		}
		/*
		try {
			cert.checkValidity();
		} catch (CertificateExpiredException | CertificateNotYetValidException  e) {
			 
			return false;		
		} 
		*/
		try {

//			@SuppressWarnings("deprecation")
			
			BufferedReader in = null;
			in = new BufferedReader(new FileReader(file2));
			String line;
			
			while ((line = in.readLine()) != null) {
				revokedSerials.add(line);
			}
			
			in.close();
						
			for(String revokedSerial : revokedSerials) {
				
				if(revokedSerial.equalsIgnoreCase(serial))
					return false;
				
			}
			/*
			String compare = ois.readLine();
			ois.close();
			
			if(compare.equalsIgnoreCase(serial)) {

				return false;
			}
			*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public boolean PDFGenerator(String serial) {
		
		X509Certificate certificate = getOne(serial);
		Document document = new Document();
		
		repository.getCertificates().stream().map(CertificateDTO::new).collect(Collectors.toList());
		
		new CertificateDTO(certificate);
		
	      try
	      {
	         PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("YourCertificate.pdf"));
	         document.open();
	         document.add(new Paragraph("Serial number: " + certificate.getSerialNumber()));
	         document.add(new Paragraph("Common name: " + new CertificateDTO(certificate).getCommonName()));
	         document.add(new Paragraph("Given name: " + new CertificateDTO(certificate).getGivenname()));
	         document.add(new Paragraph("Surname: " + new CertificateDTO(certificate).getSurname()));
	         document.add(new Paragraph("Organization: " + new CertificateDTO(certificate).getOrganization()));
	         document.add(new Paragraph("Organization unit: " + new CertificateDTO(certificate).getOrganizationUnit()));
	         document.add(new Paragraph("Country: " + new CertificateDTO(certificate).getCountry()));
	         document.add(new Paragraph("E-mail: " + new CertificateDTO(certificate).getEmail()));

	         
	         document.close();
	         writer.close();
	      } catch (DocumentException e)
	      {
	         e.printStackTrace();
	      } catch (FileNotFoundException e)
	      {
	         e.printStackTrace();
	      }

		return true;
		
	}
	
	
	public String ispis(String serial) {
		X509Certificate cert = getOne(serial);
		StringWriter streamWritter = new StringWriter();
		
		try {
			streamWritter.write("---------BEGIN----------\n");
			streamWritter.write(DatatypeConverter.printBase64Binary(cert.getEncoded()).replaceAll("(.{64})", "$1\n"));
			streamWritter.write("\n-----------END----------\n");
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
		}
	   
		return streamWritter.toString();
	}
	
	
	public boolean checkExpiration(String serial) {
		
		X509Certificate cert = getOne(serial);
		
		try {
			cert.checkValidity();
			
		} catch (CertificateExpiredException | CertificateNotYetValidException  e) {
			 
			return false;
			
		} 
		
		return true;
		
	}
	
	public static void setFeaturesBySystemProperty(SAXParserFactory factory) 
	         throws SAXException, ParserConfigurationException { 
	  
	     final boolean enableExternalDtdLoad = Boolean.parseBoolean( 
	         System.getProperty("ENABLE_EXTERNAL_DTD_LOAD", "false")); 
	  
	     factory.setFeature("LOAD_EXTERNAL_DTD", enableExternalDtdLoad); 
	     factory.setFeature("EXTERNAL_GENERAL_ENTITIES", enableExternalDtdLoad); 
	 } 
	
}
