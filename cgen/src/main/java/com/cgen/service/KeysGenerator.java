package com.cgen.service;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import java.util.UUID;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.springframework.stereotype.Component;

import com.cgen.certificate.Certificate;

@Component
public class KeysGenerator {

	private static final Random randomSec = new SecureRandom();
	
	
    public static X500Name generateX500Name(Certificate cert) {
        return new X500NameBuilder(BCStyle.INSTANCE)
        	.addRDN(BCStyle.UID, UUID.randomUUID().toString())
			.addRDN(BCStyle.CN, cert.getCommonName())
			.addRDN(BCStyle.O, cert.getOrganization())
			.addRDN(BCStyle.OU, cert.getOrganizationUnit())
			.addRDN(BCStyle.SURNAME, cert.getSurname())
			.addRDN(BCStyle.GIVENNAME, cert.getGivenname())
			.addRDN(BCStyle.C, cert.getCountry())
			.addRDN(BCStyle.E, cert.getEmail())
			.build();
    }
    
    public static byte[] generateSalt() {
		byte[] salt = new byte[16];
		randomSec.nextBytes(salt);
	    return salt;
	}
    
    public static byte[] hashPassword(String password, byte[] salt) {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
		
	    try {
	      SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
	      return skf.generateSecret(spec).getEncoded();
	    
	    } catch (NoSuchAlgorithmException | InvalidKeySpecException exc) {
	      throw new AssertionError("Greska pri hesiranju lozinke: " + exc.getMessage(), exc);
	    } finally { 
	    	//obrisi
	      spec.clearPassword();
	    }
	}
	
	
	
	public static KeyPair generateKeyPair() {
        try {
        	KeyPairGenerator  keyGen = KeyPairGenerator .getInstance("RSA"); 
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
			keyGen.initialize(2048, random);
			return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		}
        return null;

	}
	
}
