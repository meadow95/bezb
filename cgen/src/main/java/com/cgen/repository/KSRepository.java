package com.cgen.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.springframework.data.jpa.repository.JpaRepository;

import com.cgen.certificate.IssuerData;


public class KSRepository{

	private KeyStore keyStore;
	private KeyStore keyStoreCA;
	private KeyStore keyStoreSS;
	private final String keyStorepath = "./ks.jks";
	private final String keyStorePassword = "";
	
	private final String keyStorepathCA = "./ksCA.jks";
	private final String keyStorePasswordCA = "";
	
	private final String keyStorepathSS = "./ksSS.jks";
	private final String keyStorePasswordSS = "";
	
	public KSRepository() { 
		try {  
			File f = new File(keyStorepath); 
			keyStore = KeyStore.getInstance("JKS", "SUN");
			if (f.exists()) {
				keyStore.load(new FileInputStream(f), keyStorePassword.toCharArray());
			}else {
				keyStore.load(null,  keyStorePassword.toCharArray());
				keyStore.store(new FileOutputStream(f), "".toCharArray());
			}
		} catch (Exception e) {  
			e.printStackTrace();
			
		} 
		
		try {  
			File f2 = new File(keyStorepathCA); 
			keyStoreCA = KeyStore.getInstance("JKS", "SUN");
			if (f2.exists()) {
				keyStoreCA.load(new FileInputStream(f2), keyStorePasswordCA.toCharArray());
			}else {
				keyStoreCA.load(null,  keyStorePasswordCA.toCharArray());
				keyStoreCA.store(new FileOutputStream(f2), "".toCharArray());
			}
		} catch (Exception e) {  
			e.printStackTrace();
			
		}
		
		try {  
			File f3 = new File(keyStorepathSS); 
			keyStoreSS = KeyStore.getInstance("JKS", "SUN");
			if (f3.exists()) {
				keyStoreSS.load(new FileInputStream(f3), keyStorePasswordSS.toCharArray());
			}else {
				keyStoreSS.load(null,  keyStorePasswordSS.toCharArray());
				keyStoreSS.store(new FileOutputStream(f3), "".toCharArray());
			}
		} catch (Exception e) {  
			e.printStackTrace();
			
		}
	} 
	
	public void saveCertificate(String alias, PrivateKey privateKey, Certificate certificate) throws Exception {
		keyStore.setKeyEntry(alias, privateKey, keyStorePassword.toCharArray(), new Certificate[] {certificate});
		keyStore.store(new FileOutputStream(keyStorepath), keyStorePassword.toCharArray()); 
	}
	
	public void saveCertificateCA(String alias, PrivateKey privateKey, Certificate certificate) throws Exception {
		keyStoreCA.setKeyEntry(alias, privateKey, keyStorePasswordCA.toCharArray(), new Certificate[] {certificate});
		keyStoreCA.store(new FileOutputStream(keyStorepathCA), keyStorePasswordCA.toCharArray()); 
	}
	
	public void saveCertificateSS(String alias, PrivateKey privateKey, Certificate certificate) throws Exception {
		keyStoreSS.setKeyEntry(alias, privateKey, keyStorePasswordSS.toCharArray(), new Certificate[] {certificate});
		keyStoreSS.store(new FileOutputStream(keyStorepathSS), keyStorePasswordSS.toCharArray()); 
	}
	
	public Optional<X509Certificate> getCertificate(String alias) {
		try {
			keyStore.load(new FileInputStream(keyStorepath), keyStorePassword.toCharArray());
			X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
			
			if(cert != null) {
				
				return Optional.ofNullable(cert);
			}
				
			keyStoreSS.load(new FileInputStream(keyStorepathSS), keyStorePasswordSS.toCharArray());
			X509Certificate certSS = (X509Certificate) keyStoreSS.getCertificate(alias);
			
			if(certSS != null) {
				
				return Optional.ofNullable(certSS);
			}
			
			keyStoreCA.load(new FileInputStream(keyStorepathCA), keyStorePasswordCA.toCharArray());
			X509Certificate certCA = (X509Certificate) keyStoreCA.getCertificate(alias);
			
			if(certCA != null) {
				
				return Optional.ofNullable(certCA);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<X509Certificate> getCertificates() {	
		List<X509Certificate> certs = new ArrayList<>();
		
		try {
			keyStore.load(new FileInputStream(keyStorepath), keyStorePassword.toCharArray());
			keyStoreSS.load(new FileInputStream(keyStorepathSS), keyStorePasswordSS.toCharArray());
			keyStoreCA.load(new FileInputStream(keyStorepathCA), keyStorePasswordCA.toCharArray());
			
			Enumeration<String> aliases = keyStore.aliases();
			Enumeration<String> aliasesSS = keyStoreSS.aliases();
			Enumeration<String> aliasesCA = keyStoreCA.aliases();

			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				
				if (keyStore.isKeyEntry(alias)) {		
					certs.add(getCertificate(alias).get());
				}
			}
			
			while (aliasesSS.hasMoreElements()) {
				String alias = aliasesSS.nextElement();
				
				if (keyStoreSS.isKeyEntry(alias)) {		
					certs.add(getCertificate(alias).get());
				}
			}
			
			while (aliasesCA.hasMoreElements()) {
				String alias = aliasesCA.nextElement();
				
				if (keyStoreCA.isKeyEntry(alias)) {		
					certs.add(getCertificate(alias).get());
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return certs;
	}
	public IssuerData getIssuerData(String issuerSerialNumber) {
		try { 
			keyStore.load(new FileInputStream(keyStorepath), keyStorePassword.toCharArray());
			
			X509Certificate cert = (X509Certificate)keyStore.getCertificate(issuerSerialNumber);
			PrivateKey privateKey = (PrivateKey) keyStore.getKey(issuerSerialNumber, keyStorePassword.toCharArray());
			
			if (cert != null && privateKey != null) {
				X500Name issuerName = new JcaX509CertificateHolder(cert).getSubject();
				
				return new IssuerData(privateKey, issuerName);
			}
			
			keyStoreCA.load(new FileInputStream(keyStorepathCA), keyStorePasswordCA.toCharArray());
			
			X509Certificate certCA = (X509Certificate)keyStoreCA.getCertificate(issuerSerialNumber);
			PrivateKey privateKeyCA = (PrivateKey) keyStoreCA.getKey(issuerSerialNumber, keyStorePasswordCA.toCharArray());
			
			if (certCA != null && privateKeyCA != null) {
				X500Name issuerName = new JcaX509CertificateHolder(certCA).getSubject();
				
				return new IssuerData(privateKeyCA, issuerName);
			}
			
			keyStoreSS.load(new FileInputStream(keyStorepathSS), keyStorePasswordSS.toCharArray());
			
			X509Certificate certSS = (X509Certificate)keyStoreSS.getCertificate(issuerSerialNumber);
			PrivateKey privateKeySS = (PrivateKey) keyStoreSS.getKey(issuerSerialNumber, keyStorePasswordSS.toCharArray());
			
			if (certSS != null && privateKeySS != null) {
				X500Name issuerName = new JcaX509CertificateHolder(certSS).getSubject();
				
				return new IssuerData(privateKeySS, issuerName);
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
}
