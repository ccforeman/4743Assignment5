package security;

import java.util.HashMap;
import java.util.Map;

import model.User;

public class ABACPolicy {

		public static HashMap<String, HashMap<Credentials, Boolean>> accessControlList = new HashMap<String, HashMap<Credentials, Boolean>>();
	
	public ABACPolicy() {
		
	}
	
	public static void addACLEntry(String login, boolean ... perms) {
		HashMap<Credentials, Boolean> permissions = new HashMap<Credentials, Boolean>();
		permissions.put(Credentials.ADD_PART, perms[0]);
		permissions.put(Credentials.EDIT_PART, perms[1]);
		permissions.put(Credentials.DELETE_PART, perms[2]);
		
		accessControlList.put(login, permissions);
	}
	
	public boolean userIsAllowed(User u, Credentials function) {
		try {
			if(accessControlList.containsKey(u.getLogin())) {
				HashMap<Credentials, Boolean> userPerm = accessControlList.get(u.getLogin());
				
				return userPerm.get(function);
			}
		} catch (NullPointerException e) {
			return false;
		}
		
		
		return false;
	}
	
	public static HashMap<String, HashMap<Credentials, Boolean>> getList() {
		return accessControlList;
	}
}
