package it.data.db;

import java.util.Date;
import java.util.List;

import javax.jdo.PersistenceManager;

public class ManagerDB {

	public static void writeAcces(String ipAddress, Date date, String urlRequest) {
		Access a = new Access(ipAddress, date, urlRequest);
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.makePersistent(a);
		} finally {
			pm.close();
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Access> getAccesses() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		String query = "select from " + Access.class.getName();
        List<Access> accesses = (List<Access>) pm.newQuery(query).execute();
        return accesses;
	}
	
	public static void deleteAllAccesses(List<Access> list) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			pm.deletePersistentAll(list);
		} finally {
			pm.close();
		}
	}
}
