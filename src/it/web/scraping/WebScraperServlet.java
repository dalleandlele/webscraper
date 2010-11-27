package it.web.scraping;

import it.data.HotelPreview;
import it.data.RoomPreview;
import it.web.utility.Commons;
import it.web.utility.DOMPrinter;
import it.web.utility.GAEConnectionManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.*;

import net.sf.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.google.appengine.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class WebScraperServlet extends HttpServlet {

	ArrayList<String> listLink = new ArrayList<String>();
	ArrayList<String> listPlaces = new ArrayList<String>();

	ArrayList<HotelPreview> listHotel = new ArrayList<HotelPreview>();
	private String checkin_monthday, checkin_year_month, checkout_monthday, checkout_year_month;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		String where = req.getParameter("where");
		where = URLEncoder.encode(where, "UTF-8");

		checkin_monthday = req.getParameter("checkin_monthday");
		checkin_year_month = req.getParameter("checkin_year_month");
		checkout_monthday = req.getParameter("checkout_monthday");
		checkout_year_month = req.getParameter("checkout_year_month");
		URL url = new URL(
				//"http://www.booking.com/searchresults.html?error_url=http%3A%2F%2Fwww.booking.com%2Findex.html%3Fsid%3D10119a8c528f688bc9350270f17d322d%3B&sid=10119a8c528f688bc9350270f17d322d&si=ai%2Cco%2Cci%2Cre%2Cdi&ss="
				"http://www.booking.com/searchresults.html?error_url=http%3A%2F%2Fwww.booking.com%2Findex.html&si=ai%2Cco%2Cci%2Cre%2Cdi&ss="
						+ where
						+ "&checkin_monthday="
						+ checkin_monthday
						+ "&checkin_year_month="
						+ checkin_year_month
						+ "&checkout_monthday="
						+ checkout_monthday
						+ "&checkout_year_month="
						+ checkout_year_month
						+ "&group_adults=1&group_children=0&clear_group=1");
		System.out.println(url.toString());
		// Milano
		// URL url = new
		// URL("http://www.booking.com/searchresults.html?error_url=http%3A%2F%2Fwww.booking.com%2Findex.html%3Fsid%3D10119a8c528f688bc9350270f17d322d%3B&sid=10119a8c528f688bc9350270f17d322d&si=ai%2Cco%2Cci%2Cre%2Cdi&ss=Milano&checkin_monthday=25&checkin_year_month=2010-11&checkout_monthday=26&checkout_year_month=2010-11&group_adults=1&group_children=0&clear_group=1");

		// cardano al campo
		// URL url = new
		// URL("http://www.booking.com/searchresults.html?error_url=http%3A%2F%2Fwww.booking.com%2Findex.html%3Fsid%3D10119a8c528f688bc9350270f17d322d%3B&sid=10119a8c528f688bc9350270f17d322d&si=ai%2Cco%2Cci%2Cre%2Cdi&ss=cardano+al+campo&checkin_monthday=30&checkin_year_month=2010-11&checkout_monthday=1&checkout_year_month=2010-12&group_adults=1&group_children=0&clear_group=1");

		try {
			getDisambiguation(url);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		// prendo solo il primo link...
		doChoice(new URL("http://www.booking.com" + listLink.get(0)));
		

		/*for (HotelPreview h : listHotel) {
			resp.getWriter().println(h);
			System.out.println(h);
		}*/
		
		//JSONArray json2 = JSONArray.fromObject(listHotel);
		//System.out.println(json2);
		
		/*com.google.appengine.repackaged.org.json.JSONArray json = new com.google.appengine.repackaged.org.json.JSONArray(listHotel);
		System.out.println(json);
		
		resp.getWriter().println("JSON:");
		
		resp.getWriter().println(json2);*/
		String json = getJSONFromListHitel(listHotel);
		System.out.println(json);
		resp.getWriter().println("JSON:");
		resp.getWriter().println(json);
		
		
	}
	
	private static String getJSONFromListHitel(ArrayList<HotelPreview> list) {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(HotelPreview h : list) {
			sb.append(h.toString()).append(",");
		}
		sb.delete(sb.length()-1, sb.length());
		sb.append("]");
		return sb.toString();
	}

	private void doChoice(URL url) throws IOException {
		System.out.println("Request --> " + url.toString());
		HttpParams httpParams = new BasicHttpParams();
		ClientConnectionManager connectionManager = new GAEConnectionManager();
		
		HttpClient httpClient = new DefaultHttpClient(connectionManager, httpParams);
		HttpProtocolParams.setUserAgent(httpClient.getParams(), Commons.USER_AGENT);
		
		
		HttpGet httpGet = new HttpGet(url.toString());
		HttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		
		assert entity != null : "Problema nella richiesta http";
		
		InputStream instream = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));

		Tidy ty = new Tidy();
		ty.setShowWarnings(false);
		Document dom = ty.parseDOM(reader, null);

		// recupera il nodo table con class=hotellist_wrap
		Node table_class_hotellist_wrap = getNodeWithAttribute(dom, "table",
				"class", "hotellist");

		// in ogni tr c'è un hotel
		NodeList list_tr = table_class_hotellist_wrap.getChildNodes();

		assert list_tr.getLength() > 0 : "Non vi sono nodi tr contenenti hotel";

		manageTRNode(list_tr);

	}

	private void manageTRNode(NodeList listTr) throws MalformedURLException {
		// ogni tr contiene la descrizione dell'hotel
		ArrayList<Node> list = getNodeChildWithName(listTr, "tr");

		for(Node tr : list) {
			ArrayList<Node> listTD = getNodeChildWithName(tr.getChildNodes(), "td");
			assert listTD.size() == 2 : "Ci sono più di due figli td";
			Node firstTD = listTD.get(0);
			Node secondTD = listTD.get(1);
			
			int id = getIDHotel(firstTD);
			assert id > 0 : "id minore di 0";
			
			System.out.println("id --> " + id);
			
			//adesso recupero il link all'immmagine jpg
			Node link = getNodeChildWithName(firstTD.getChildNodes(), "a").get(0);
			String title = getAttribute(link, "title");	
			assert title != null : "l'attributo title non esiste";
			
			int begin = title.indexOf("'");
			int end = title.indexOf("'", begin + 1);
			String imageLinkBig = title.substring(begin + 1, end);
			System.out.println("link immagine jpg --> " + imageLinkBig);
			
			//recupero il link all'immagine gif
			Node smallLink = getNodeChildWithName(link.getChildNodes(), "img").get(0);
			String imageLinkSmall = getAttribute(smallLink, "src");
			assert imageLinkSmall != null : "il link all'immagine gif non esiste";
			System.out.println("link immagine gif --> " + imageLinkSmall);
			
			
			//finito col primo td, passo al secondo
			//recupero il link all'hotel e il nome
			Node h3 = getNodeChildWithName(secondTD.getChildNodes(), "h3").get(0);
			String linkHotel = getAttribute(getNodeChildWithName(h3.getChildNodes(), "a").get(0), "href");
			String nameHotel = getNodeChildWithName(h3.getChildNodes(),"a").get(0).getChildNodes().item(0).getNodeValue();
			System.out.println("link hotel --> " + linkHotel + "\nnome --> " +nameHotel);
			
			//adesso recupero le stars
			DOMPrinter.printNode(h3, "");
			String starsStr = getAttribute(getNodeChildWithName(getNodeChildWithName(h3.getChildNodes(), "span").get(0).getChildNodes(), "img").get(0), "title");
			int stars;
			if(starsStr.isEmpty())
				stars = -1;
			else
				stars = Character.getNumericValue(starsStr.charAt(0));
			System.out.println("stars --> " + stars);
	
			ArrayList<Node> divNode = getNodeChildWithName(secondTD.getChildNodes(), "div");
			
			//prelevo la preferenza
			/*
			 * <div class="reviewFloater">
  					<a id="ind_rev_total" href="/hotel/it/starhotels-rosa.html?tab=4" style="text-align: center;text-decoration: none; font-size: 1.5em">
    					Very good, 8.5
  					</a>
			 * 
			 * 
			 */
			Node pref = divNode.get(0);
			
			String prefString = "";
			Node aTmp = getNodeChildWithName(pref.getChildNodes(), "a").get(0);
			if(aTmp.getFirstChild().getNodeType() == Node.TEXT_NODE && !aTmp.getFirstChild().getNodeValue().isEmpty()) {
				prefString = aTmp.getFirstChild().getNodeValue();
			} else {
				prefString =  getNodeChildWithName(getNodeChildWithName(aTmp.getChildNodes(), "span").get(0).getChildNodes(), "span").get(0).getFirstChild().getNodeValue();
			}
			    
			System.out.println(prefString);
			
			
			
			double prefHotel = Double.parseDouble(prefString.substring(prefString.indexOf(",")+1).trim());
			System.out.println("preferenza --> " + prefHotel);
			
			//adesso cerco l'indirizzo
			Node address = divNode.get(1);	
			String addressHotel = getAddress(address);
			
			System.out.println("address --> " + addressHotel);
			
			//adesso viene la descrizione
			ArrayList<Node> listAddress =  getNodeChildWithName(secondTD.getChildNodes(), "p");
			assert listAddress.size() == 1 : "ci sono più nodi p nel secondo td";
			
			String descriptionHotel = listAddress.get(0).getChildNodes().item(0).getNodeValue();
			System.out.println("descrizione --> " + descriptionHotel);
			
			//rimangono le stanze...
			Node form = getNodeChildWithName(secondTD.getChildNodes(), "form").get(0);
			Node tbody = getNodeChildWithName(getNodeChildWithName(form.getChildNodes(), "table").get(0).getChildNodes(), "tbody").get(0);
			
			ArrayList<RoomPreview> rooms = getRooms(getNodeChildWithName(tbody.getChildNodes(), "tr"));
			
			
			listHotel.add(new HotelPreview(imageLinkSmall, imageLinkBig, linkHotel, id, nameHotel, addressHotel, stars, descriptionHotel, prefHotel, rooms));
		}
	}
	
	private ArrayList<RoomPreview> getRooms(ArrayList<Node> trNodes) {
		ArrayList<RoomPreview> toRet = new ArrayList<RoomPreview>();
		
		
		//per ogni tr ci sono 4 td
		//1 --> nome e link
		//2 --> max persons
		//3 --> availability
		//4 --> prices
		
		for(Node tr : trNodes) {
			ArrayList<Node> listTD = getNodeChildWithName(tr.getChildNodes(), "td");
			assert listTD.size() == 4 : "Ci sono un numero diverso di TD nella tabella delle stanze";
			Node td1 = listTD.get(0);
			Node td2 = listTD.get(1);
			Node td3 = listTD.get(2);
			Node td4 = listTD.get(3);
			
			//becco il nome e il link
			Node tmpA = getNodeChildWithName(getNodeChildWithName(td1.getChildNodes(), "div").get(0).getChildNodes(), "a").get(0);
			String link = getAttribute(tmpA, "href");
			String name = tmpA.getFirstChild().getNodeValue();
			System.out.println("linkroom --> " + link);
			System.out.println("name room --> " + name);
			//cerco il max persons
			Node spanMax = getNodeChildWithName(getNodeChildWithName(td2.getChildNodes(), "div").get(0).getChildNodes(), "span").get(0);
			String stringMax = spanMax.getFirstChild().getNodeValue();
			int maxPersons = Integer.parseInt(stringMax);
			
			System.out.println("max persons --> " + maxPersons);
			//cerco la disponibilità 
			
			String availability = findChildsTextNode(td3);
			
			//Node aAvailability = getNodeChildWithName(getNodeChildWithName(td3.getChildNodes(), "div").get(0).getChildNodes(), "a").get(0);
			//String availability = aAvailability.getFirstChild().getNodeValue();
			
			System.out.println("availability --> " + availability);
			
			Node price =  getNodeChildWithName(getNodeChildWithName(td4.getChildNodes(), "div").get(0).getChildNodes(), "strong").get(0);
			System.out.println("price --> " + price.getFirstChild().getNodeValue());
			String priceString = price.getFirstChild().getNodeValue();
			/*char[] priceArray = priceString.toCharArray();
			int index = -1;
			for(int i=0;i<priceArray.length;i++) {
				if(Character.isDigit(priceArray[i])) {
					index = i;
					break;
				}
			}
			double priceRoom = Double.parseDouble(priceString.substring(index));
			*/
			System.out.println("price --> " + priceString);
			
			RoomPreview room = new RoomPreview(name, maxPersons, availability,priceString);
			toRet.add(room);
		}
		return toRet;
	}

	private String findChildsTextNode(Node n) {

		boolean stop = false;
		
		while(!stop) {
			System.out.println("n --> " + n.getNodeName() + "  " + n.getNodeValue());
			if(n != null && n.getFirstChild() != null && n.getFirstChild().getNodeType() == Node.TEXT_NODE && !n.getFirstChild().getNodeValue().isEmpty()) {
				//inutile
				stop = true;
				return n.getFirstChild().getNodeValue();
				
			}
			if(n == null)
				System.out.println("problema...");
			if(n.getNodeType() == Node.COMMENT_NODE) {
				n = n.getNextSibling();
			} else
				n = n.getFirstChild();
		}
		
		
		return null;
	}

	private String getAddress(Node address) {
		StringBuilder sb = new StringBuilder();
		NodeList list = address.getChildNodes();
		for(int i=0;i<list.getLength();i++) {
			if(list.item(i).getNodeType() == Node.TEXT_NODE) {
				sb.append(list.item(i).getNodeValue());
			} else {
				assert list.item(i).getChildNodes().item(0).getNodeType() == Node.TEXT_NODE : "Il figlio di a dovrebbe essere un testo nodo";
				sb.append(list.item(i).getChildNodes().item(0).getNodeValue());
			}
		}
		
		int ch = sb.indexOf(" •");
		
		return sb.toString().substring(0, ch);
	}

	private String getAttribute(Node n, String nameAttribute) {
		NamedNodeMap map = n.getAttributes();
		for(int i=0;i<map.getLength();i++) {
			if(map.item(i).getNodeName().equals(nameAttribute)) {
				return map.item(i).getNodeValue();
			}
		}
		return null;
	}

	private ArrayList<Node> getNodeChildWithName(NodeList childs, String name) {
		ArrayList<Node> toReturn = new ArrayList<Node>();
		for(int i=0;i<childs.getLength();i++) {
			if(childs.item(i).getNodeName().equals(name)) {
				toReturn.add(childs.item(i));
			}
		}
		return toReturn;
	}

	private int getIDHotel(Node firstTD) {
		assert firstTD != null : "primo td == null";
		NamedNodeMap mapAttribute = firstTD.getAttributes();
		for (int i = 0; i < mapAttribute.getLength(); i++) {
			if (mapAttribute.item(i).getNodeName().equals("id")) {
				assert mapAttribute.item(i).getNodeValue().startsWith("hotel_") : "L'attributo ID del nodo td non inzia con 'hotel_'";
				String longId = mapAttribute.item(i).getNodeValue();
				longId = longId.replace("hotel_", "");
				return Integer.parseInt(longId);
			}
		}

		return -1;
	}

	private boolean isNodeWithAttribute(Node n, String nameAttribute,
			String valueAttribute) {
		NamedNodeMap mapAttribute = n.getAttributes();
		for (int i = 0; i < mapAttribute.getLength(); i++) {
			Node tmp = mapAttribute.item(i);
			if (tmp.getNodeName().equals(nameAttribute)
					&& tmp.getNodeValue().equals(valueAttribute))
				return true;
		}

		return false;
	}

	private Node getNodeWithAttribute(Document dom, String tagname,
			String nameAttribute, String valueAttribute) {
		NodeList list = dom.getElementsByTagName(tagname);
		for (int i = 0; i < list.getLength(); i++) {
			// cerca l'attributo passato come argomento
			if (isNodeWithAttribute(list.item(i), nameAttribute, valueAttribute))
				return list.item(i);
		}
		return null;
	}

	private void getDisambiguation(URL url) throws IOException, URISyntaxException {
		
		HttpParams httpParams = new BasicHttpParams();
		ClientConnectionManager connectionManager = new GAEConnectionManager();
		
		HttpClient httpClient = new DefaultHttpClient(connectionManager, httpParams);
		
		HttpProtocolParams.setUserAgent(httpClient.getParams(), "Mozilla/5.0 (X11; U; Linux i686; en-US) AppleWebKit/534.7 (KHTML, like Gecko) Ubuntu/10.04 Chromium/7.0.517.44 Chrome/7.0.517.44 Safari/534.7");
		
		
		HttpGet httpGet = new HttpGet(url.toString());
		HttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		
		assert entity != null : "Problema nella richiesta http";
		
		InputStream instream = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(instream));


		
		
		Tidy ty = new Tidy();
		ty.setShowWarnings(false);
		
		
		
		
		Document dom = ty.parseDOM(reader, null);

		NodeList n = dom.getElementsByTagName("table");

		Node table = n.item(0);
		NodeList trList = table.getChildNodes();

		for (int i = 0; i < trList.getLength(); i++) {
			System.out.println(trList.item(i).getNodeName());

			Node tdNode = trList.item(i).getFirstChild();
			
			Node link = tdNode.getFirstChild();
			
			NamedNodeMap map = link.getAttributes();
			for (int z = 0; z < map.getLength(); z++) {
				if (map.item(z).getNodeName().equals("href")) {
					StringBuilder tmpLink = new StringBuilder(map.item(z).getNodeValue());
					System.out.println(tmpLink.toString());
					
					listLink.add(tmpLink.toString());
				}
			}

			Node secondTdNode = tdNode.getNextSibling();
			System.out.println(secondTdNode.getFirstChild().getFirstChild()
					.getNodeValue());

		}

	}
}
