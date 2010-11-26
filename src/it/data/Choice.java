package it.data;

public class Choice {
	private String link;
	private String name;
	private String country;
	private String number;
	
	public Choice(String link, String name, String country, String number) {
		this.link = link;
		this.name = name;
		this.country = country;
		this.number = number;
	}
	
	public Choice() {
		
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
	
	
	
}
