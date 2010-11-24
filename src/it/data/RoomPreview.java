package it.data;

import com.google.appengine.repackaged.org.json.JSONObject;

public class RoomPreview {
	private String name;
	private int persons;
	private String availability;
	private String price;
	
	
	
	public RoomPreview(String name, int persons, String availability,
			String price) {
		super();
		this.name = name;
		this.persons = persons;
		this.availability = availability;
		this.price = price;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("{").append(concatenateFieldValue("availability", availability)).append(",")
			.append(concatenateFieldValue("name", name)).append(",")
			.append(concatenateFieldValue("persons", persons)).append(",")
			.append(concatenateFieldValue("price", price)).append("}");
		
		//sb.append("name: ").append(name).append(" - max persons: ").append(persons).append(" - availability: ").append(availability).append(" - price: ").append(price);
		return sb.toString();
		
		//JSONObject obj = new JSONObject(this);
		//return obj.toString();
	}
	
	private String concatenateFieldValue(String field, String value) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(field).append("\":\"").append(value).append("\"");
		return sb.toString();
	}
	
	private String concatenateFieldValue(String field, int value) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(field).append("\":").append(value);
		return sb.toString();
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPersons() {
		return persons;
	}
	public void setPersons(int persons) {
		this.persons = persons;
	}
	public String getAvailability() {
		return availability;
	}
	public void setAvailability(String availability) {
		this.availability = availability;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
}
