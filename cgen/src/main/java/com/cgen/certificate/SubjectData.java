package com.cgen.certificate;


import java.security.PublicKey;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;

public class SubjectData {
	private PublicKey publicKey;
	private X500Name x500name;
	private Date startDate; 
	private Date endDate;

	public SubjectData() {
 
	}

	public SubjectData(PublicKey publicKey, X500Name x500name, Date startDate, Date endDate) {
		
		this.publicKey = publicKey;
		this.x500name = x500name;
		this.startDate = startDate;
		this.endDate = endDate;

	}


	public X500Name getX500name() {
		return x500name;
	}

	public void setX500name(X500Name x500name) {
		this.x500name = x500name;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}


	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	

}
