package it.data.db;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Access {
	@PrimaryKey
	@Persistent(valueStrategy= IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String ipAddress;
	@Persistent
	private Date date;
	@Persistent
	private String urlRequest;
	
	public Access(String ipAddress, Date date, String urlRequest) {
		this.ipAddress = ipAddress;
		this.date = date;
		this.urlRequest = urlRequest;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public String getUrlRequest() {
		return urlRequest;
	}
	public void setUrlRequest(String urlRequest) {
		this.urlRequest = urlRequest;
	}
	
	public String toStringDate() {
		Calendar c = new GregorianCalendar(TimeZone.getTimeZone("Europe/Rome"));
		c.setTime(date);
		StringBuilder when = new StringBuilder();
		when.append(c.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + c.get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR_OF_DAY)).append(":").append(c.get(Calendar.MINUTE) < 10 ? "0" + c.get(Calendar.MINUTE) : c.get(Calendar.MINUTE)).append(":").append(c.get(Calendar.SECOND) < 10 ? "0" + c.get(Calendar.SECOND) : c.get(Calendar.SECOND)).append(".").append(c.get(Calendar.MILLISECOND)).append(" ").append(c.get(Calendar.DAY_OF_MONTH)).append("/").append(c.get(Calendar.MONTH)+1).append("/").append(c.get(Calendar.YEAR));
		return when.toString();
	}
	
	
}
