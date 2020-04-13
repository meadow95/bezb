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
	private final String keyStorepath = "./ks.jks";
	private final String keyStorePassword = ""; 
	
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
	} 
	
	public void saveCertificate(String alias, PrivateKey privateKey, Certificate certificate) throws Exception {
		keyStore.setKeyEntry(alias, privateKey, keyStorePassword.toCharArray(), new Certificate[] {certificate});
		keyStore.store(new FileOutputStream(keyStorepath), keyStorePassword.toCharArray()); 
	}
	
	public Optional<X509Certificate> getCertificate(String alias) {
		try {
			keyStore.load(new FileInputStream(keyStorepath), keyStorePassword.toCharArray());
			X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);
			return Optional.ofNullable(cert);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public List<X509Certificate> getCertificates() {	
		List<X509Certificate> certs = new ArrayList<>();
		
		try {
			keyStore.load(new FileInputStream(keyStorepath), keyStorePassword.toCharArray());
			
			Enumeration<String> aliases = keyStore.aliases();
			
			while (aliases.hasMoreElements()) {
				String alias = aliases.nextElement();
				
				if (keyStore.isKeyEntry(alias)) {		
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
			
			if (cert == null || privateKey == null) {
				return null;
			}
			
			X500Name issuerName = new JcaX509CertificateHolder(cert).getSubject();
			
			return new IssuerData(privateKey, issuerName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	
	
}
