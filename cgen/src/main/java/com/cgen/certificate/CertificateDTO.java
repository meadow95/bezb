package com.cgen.certificate;

import java.math.BigInteger;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.bouncycastle.asn1.x500.AttributeTypeAndValue;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;

public class CertificateDTO {

	private BigInteger serial;
	private String commonName; 
	private String organization; 	
	private String organizationUnit;
	private String country; 
	private String surname; 
	private String givenname;
	private String email;
	private CerType status;
	
	public CertificateDTO(X509Certificate certificate) {
		
		X500Name naziv;
		try {
			serial = certificate.getSerialNumber();
			
			naziv = new JcaX509CertificateHolder(certificate).getSubject();
			RDN[] rdns = naziv.getRDNs();
			
			for (RDN rdn: rdns) {
				AttributeTypeAndValue[] values = rdn.getTypesAndValues();
				for (AttributeTypeAndValue val : values) {
					if (val.getType().equals(BCStyle.CN)) {
						commonName = val.getValue().toString();
					
					} else if (val.getType().equals(BCStyle.O)) {
						organization = val.getValue().toString();
					} else if (val.getType().equals(BCStyle.OU)) {
						organizationUnit = val.getValue().toString();
					} else if (val.getType().equals(BCStyle.GIVENNAME)) {
						givenname = val.getValue().toString();
					} else if (val.getType().equals(BCStyle.SURNAME)) {
						surname = val.getValue().toString();
					} else if (val.getType().equals(BCStyle.C)) {
						country = val.getValue().toString();
					} else if (val.getType().equals(BCStyle.E)) {
						email = val.getValue().toString();
					}
				}
			}
	
		} catch (CertificateEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	
	public BigInteger getSerial() {
		return serial;
	}
	public void setSerial(BigInteger serial) {
		this.serial = serial;
	}
	public String getCommonName() {
		return commonName;
	}
	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}
	public String getOrganization() {
		return organization;
	}
	public void setOrganization(String organization) {
		this.organization = organization;
	}
	public String getOrganizationUnit() {
		return organizationUnit;
	}
	public void setOrganizationUnit(String organizationUnit) {
		this.organizationUnit = organizationUnit;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getGivenname() {
		return givenname;
	}
	public void setGivenname(String givenname) {
		this.givenname = givenname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public CerType getStatus() {
		return status;
	}
	public void setStatus(CerType status) {
		this.status = status;
	}
	
	
	
	
	
	
	
}
