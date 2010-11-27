package it.web.scraping;

import it.data.Choice;
import it.data.db.ManagerDB;
import it.web.utility.Commons;
import it.web.utility.GAEConnectionManager;
import it.web.utility.UtilityParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;


public class LookingDestination extends HttpServlet {
	
	private final static String thisURL = "/lookingdestination";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		ManagerDB.writeAcces(req.getRemoteAddr(), new Date(), thisURL);
		
		String where = req.getParameter("where");
		where = URLEncoder.encode(where, "UTF-8");

		String checkin_monthday = req.getParameter("checkin_monthday");
		String checkin_year_month = req.getParameter("checkin_year_month");
		String checkout_monthday = req.getParameter("checkout_monthday");
		String checkout_year_month = req.getParameter("checkout_year_month");
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
		//System.out.println(url.toString());
		
		//System.out.println("Invoking request...");
		
		ArrayList<Choice> list = getResult(url);

		StringBuilder response = new StringBuilder("[");
		for(Choice c : list) {
			response.append(c.toString()).append(",");
		}
		response.deleteCharAt(response.length()-1);
		response.append("]");
		resp.getWriter().println(response.toString());
		
	}
	
	
	private static ArrayList<Choice> getResult(URL url) throws ClientProtocolException, IOException {
		//ArrayList<String> listLink = new ArrayList<String>();
		ArrayList<Choice> list = new ArrayList<Choice>();
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

		NodeList n = dom.getElementsByTagName("table");

		Node table = n.item(0);
		ArrayList<Node> trList = UtilityParser.getNodeChildWithName(table.getChildNodes(), "tr");
		
		for (int i = 0; i < trList.size(); i++) {
			Choice c = new Choice();
			/*recupero i <td>
			nel primo c'è l'immagine e la tralascio
			nel secondo il link e il nome della disamb
			nel terzo il paese
			nel quarto il numero di hotel*/
			ArrayList<Node> td = UtilityParser.getNodeChildWithName(trList.get(i).getChildNodes(), "td");
			//salto il primo
			for(int indexTD = 1; indexTD < td.size();indexTD++) {
				switch (indexTD) {
				case 1:
					Node a = UtilityParser.getNodeChildWithName(td.get(indexTD).getChildNodes(), "a").get(0);
					c.setLink(UtilityParser.getAttribute(a, "href"));
					assert a.getFirstChild().getNodeType() == Node.TEXT_NODE;
					c.setName(a.getFirstChild().getNodeValue());
					break;
				case 2:
					Node span = UtilityParser.getNodeChildWithName(td.get(indexTD).getChildNodes(), "span").get(0);
					assert span.getFirstChild().getNodeType() == Node.TEXT_NODE;
					c.setCountry(span.getFirstChild().getNodeValue());
					break;
				case 3:
					assert td.get(indexTD).getFirstChild().getNodeType() == Node.TEXT_NODE;
					c.setNumber(td.get(indexTD).getFirstChild().getNodeValue());
					break;
				}
			}
			
			list.add(c);

		}
		return list;
	}
	
}


