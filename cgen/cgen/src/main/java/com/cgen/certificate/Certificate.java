package com.cgen.certificate;

import java.util.Date;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

public class Certificate {

	
	@NotNull
	private String commonName; 
	@NotNull
	private String organization; 
	@NotNull
	private String organizationUnit;
	@NotNull
	private String country; 
	@NotNull
	private String surname; 
	@NotNull
	private String givenname;
	@NotNull
	@Email
	private String email;
	@NotNull
	private Date endDate;
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
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}  
	
	


	
}
