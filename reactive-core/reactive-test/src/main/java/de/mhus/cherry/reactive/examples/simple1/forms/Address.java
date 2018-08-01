package de.mhus.cherry.reactive.examples.simple1.forms;

import java.util.Locale;

import de.mhus.cherry.reactive.model.annotations.PropertyDescription;
import de.mhus.lib.core.MCast;
import de.mhus.lib.core.MConstants;
import de.mhus.lib.core.MString;
import de.mhus.lib.core.MSystem;

public class Address {
	
	public static final String COUNTRY_CODE_DE = Address.getCountryCode(MConstants.LOCALE_DE_DE);
	
	public enum SALUTATION {OTHER,MR,MRS,COMPANY,AGENCY,COUPLE}
	
	@PropertyDescription
	private SALUTATION salutation;
	@PropertyDescription
	private String firstName;
	@PropertyDescription
	private String lastName;
	@PropertyDescription
	private String zip;
	@PropertyDescription
	private String town;
	@PropertyDescription
	private String street;
	@PropertyDescription
	private String streetNumber;
	@PropertyDescription
	private String email;
	@PropertyDescription
	private String tel;
	@PropertyDescription
	private String country = COUNTRY_CODE_DE;
	
	public Address() {
		
	}
	
	public Address(SALUTATION salutation, String firstName, String lastName, String zip, String town, String street, String streetNumber, String email) {
		this(salutation,firstName,lastName, COUNTRY_CODE_DE, zip,town,street,streetNumber,email, true);
	}
	
	public Address(SALUTATION salutation, String firstName, String lastName, String zip, String town, String street, String streetNumber, String email, boolean validate) {
		this(salutation,firstName,lastName, COUNTRY_CODE_DE, zip,town,street,streetNumber,email, validate);
	}

	public Address(SALUTATION salutation, String firstName, String lastName, String country, String zip, String town, String street, String streetNumber, String email) {
		this(salutation,firstName,lastName, country, zip,town,street,streetNumber,email, true);
	}
	
	public Address(SALUTATION salutation, String firstName, String lastName, String country, String zip, String town, String street, String streetNumber, String email, boolean validate) {
		this.salutation = salutation;
		this.firstName = firstName;
		this.lastName = lastName;
		this.country = country;
		this.zip = zip;
		this.town = town;
		this.street = street;
		this.streetNumber = streetNumber;
		this.email = email;
	}
	
	public static SALUTATION toSalutation(int salutation) {
		if (salutation >= SALUTATION.values().length || salutation < 0) salutation = 0;
		return SALUTATION.values()[salutation];
	}
	
	public static int toSalutationInt(String salStr) {
		int salutation = MCast.toint(salStr, -1);
		if (salStr != null && salutation == -1) {
			salStr = salStr.trim().toLowerCase();
			switch (salStr) {
			case "herr":  salutation = 1;break;
			case "frau":  salutation = 2;break;
			case "firma": salutation = 3;break;
			case "behörde": salutation = 4;break;
			case "eheleute": salutation = 5;break;
			case "mr": salutation = 1;break;
			case "ms": salutation = 2;break;
			case "mrs": salutation = 2;break;
			case "couple": salutation = 5;break;
			case "company": salutation = 3;break;
			case "agency": salutation = 4;break;
			default: salutation = 0;break;
			}
		}
		if (salutation >= SALUTATION.values().length || salutation < 0) salutation = 0;
		return salutation;
	}
	
	public static String toSalutationString(SALUTATION salutation, Locale l) {
		switch (salutation) {
		case AGENCY:
			return "Behörde";
		case COMPANY:
			return "Firma";
		case COUPLE:
			return "Eheleute";
		case MR:
			return "Herr";
		case MRS:
			return "Frau";
		case OTHER:
		default:
			return "";
		
		}
	}
	
	public static String toAddressText(SALUTATION salutation, Locale l) {
		switch (salutation) {
		case AGENCY:
		case COMPANY:
		case COUPLE:
		case OTHER:
		default:
			return "Sehr geehrte Damen und Herren";
		case MR:
			return "Sehr geehrter Herr";
		case MRS:
			return "Sehr geehrte Frau";
		
		}
	}
	
	public static SALUTATION toSalutation(String salStr) {
		int salutation = toSalutationInt(salStr);
		if (salutation < 0 || salutation >= SALUTATION.values().length)
			salutation = 0;
		return SALUTATION.values()[ salutation ];
	}

	public SALUTATION getSalutation() {
		return salutation;
	}

	public void setSalutation(SALUTATION salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getTown() {
		return town;
	}

	public void setTown(String town) {
		this.town = town;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreetNumber() {
		return streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

//	public String createGlue() {
//		return (
//				MXml.normalizeName(firstName) + ":" + 
//				MXml.normalizeName(lastName) + ":" + 
//				zip + ":" + 
//				MXml.normalizeName(street) + ":" + 
//				MXml.normalizeName(streetNumber)
//				).toLowerCase();
//	}

	public String getTelefon() {
		return tel;
	}

	public void setTelefon(String tel) {
		this.tel = tel;
	}
	
	public String toAddress() {
		return 	toSalutationString(salutation, MConstants.LOCALE_DE_DE) 
				+ (MString.isSet(getFirstName()) ? " " + getFirstName() : "" ) 
				+ (MString.isSet(getLastName()) ? " " + getLastName() : "" );
	}
	
	@Override
	public String toString() {
		return MSystem.toString(this, toAddress(), email);
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public static String getCountryCode(Locale locale) {
		return locale.getCountry();
	}

	public String toName(boolean salutation) {
		return getFirstName() + " " + getLastName();
	}

}
