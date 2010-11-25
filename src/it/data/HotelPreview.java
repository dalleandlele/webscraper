package it.data;

import java.net.URL;
import java.util.ArrayList;

import com.google.appengine.repackaged.org.json.JSONArray;
import com.google.appengine.repackaged.org.json.JSONObject;

public class HotelPreview {
	private String imageLinkSmall;
	private String imageLinkBig;
	private String linkHotel;
	private int ID;
	private String name;
	private String address;
	//if -1 --> no stars
	
	private int stars;
	private String description;
	private double preference;
	private ArrayList<RoomPreview> listRoomPreview;
	
	
	
	public HotelPreview(String imageLinkSmall, String imageLinkBig, String linkHotel,
			int iD, String name, String address, int stars, String description,
			double preference, ArrayList<RoomPreview> listRoomPreview) {
		this.imageLinkSmall = imageLinkSmall;
		this.imageLinkBig = imageLinkBig;
		this.linkHotel = linkHotel;
		ID = iD;
		this.name = name;
		this.address = address;
		this.stars = stars;
		this.description = description;
		this.preference = preference;
		this.listRoomPreview = listRoomPreview;
	}
	//Lo faccio in JSON
	@Override 
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{").append(concatenateFieldValue("ID", ID)).append(",")
			.append(concatenateFieldValue("address", address)).append(",")
			.append(concatenateFieldValue("description", description)).append(",")
			.append(concatenateFieldValue("imageBigLink", imageLinkBig.toString())).append(",")
			.append(concatenateFieldValue("imageLinkSmall", imageLinkSmall.toString())).append(",")
			.append(concatenateFieldValue("linkHotel", linkHotel)).append(",")
			.append("\"listRoomPreview\":").append(getListRoomPreviewInJSON()).append(",")
			.append(concatenateFieldValue("name", name)).append(",")
			.append(concatenateFieldValue("preference", preference)).append(",")
			.append(concatenateFieldValue("stars", stars)).append("}");
		
		return sb.toString();
		
		//JSONObject obj = new JSONObject(this);
		//return obj.toString();
		
		
		/*sb.append("ID: ").append(ID).append("\nname: ").append(name).append("\nlinkHotel: ").append(linkHotel).append("\nsmallImage: ").append(imageLinkSmall.toString())
		.append("\nbigImage: ").append(imageLinkBig.toString()).append("\naddress: ").append(address)
		.append("\nstars: ").append(stars).append("\npreference: ").append(preference).append("\ndescription: ").append(description);
		
		for(RoomPreview r : listRoomPreview) {
			sb.append("\n").append(r.toString());
		}
		return sb.toString();*/
	}
	
	private String getListRoomPreviewInJSON() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(RoomPreview r : listRoomPreview) {
			sb.append(r.toString()).append(",");
		}
		//rimuovo l'ultima ,
		sb.delete(sb.length()-1, sb.length());
		sb.append("]");
		return sb.toString();
	}
	
	private String concatenateFieldValue(String field, double value) {
		StringBuilder sb = new StringBuilder();
		sb.append("\"").append(field).append("\":").append(value);
		return sb.toString();
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

	public double getPreference() {
		return preference;
	}

	public void setPreference(int preference) {
		this.preference = preference;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStars() {
		return stars;
	}
	
	public ArrayList<RoomPreview> getListRoomPreview() {
		return listRoomPreview;
	}

	public void setListRoomPreview(ArrayList<RoomPreview> listRoomPreview) {
		this.listRoomPreview = listRoomPreview;
	}

	public void setPreference(double preference) {
		this.preference = preference;
	}

	public void setStars(int stars) {
		this.stars = stars;
	}

	public HotelPreview() {
		
	}

	public String getImageLinkSmall() {
		return imageLinkSmall;
	}

	public void setImageLinkSmall(String imageLinkSmall) {
		this.imageLinkSmall = imageLinkSmall;
	}

	public String getImageLinkBig() {
		return imageLinkBig;
	}

	public void setImageLinkBig(String imageLinkBig) {
		this.imageLinkBig = imageLinkBig;
	}

	public String getLinkHotel() {
		return linkHotel;
	}

	public void setLinkHotel(String linkHotel) {
		this.linkHotel = linkHotel;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getAddress() {
		return address;
	}
	
}
