package security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import controller.LoginException;
import model.Session;
import model.User;

public class Authenticator {
	private User user;
	private static ABACPolicy aPolicy = new ABACPolicy();
	
	private static Map<Session, User> userSessions = new HashMap<Session, User>();
//	private static Map<Session, Long> userSessions = new HashMap<Session, Long>();
//	private static Map<String, String> usersAndPasswords = new HashMap<String, String>();
	private static ArrayList<User> userCreds = new ArrayList<User>();
	

	public Authenticator() {
		insert();
	}

	public static Session login(String login, String password) throws LoginException {
		Session s = null;
		
		if(!userSessions.isEmpty()) {
			throw new LoginException("You must logout first");
		}
		
		for(User u : userCreds) {
			if(u.getLogin().equals(login) && u.getPwHash().equals(password)) {
				s = new Session();
				userSessions.put(s, u);
				return s;
			}
		}
		throw new LoginException("Invalid Login!");
	}
	
	public static void logout(Session s) {
		userSessions.remove(s);
	}
	
	public static boolean serverHasAccess(Session id, Credentials func) throws SecurityException {
		if(userSessions.containsKey(id)) {
			return aPolicy.userIsAllowed(getUserBySession(id), func);
		} else {
			throw new SecurityException("Invalid session!");
		}
	}
	
	public boolean userHasAccess(Session s, Credentials func) {
		User u = userSessions.get(s);
		return aPolicy.userIsAllowed(u, func);
	}
	
	public static ArrayList<User> getUsers() {
		return userCreds;
	}
	
//	public static Map<Session, User> getUserSessions() {
//		return userSessions;
//	}
	
	public static Map<Session, User> getUserSessions() {
		return userSessions;
	}
	
	public static User getUserBySession(Session s) {
		if(userSessions.containsKey(s))
			return userSessions.get(s);
		else
			return null;
	}
	
	public static ABACPolicy getPolicy() {
		return aPolicy;
	}
	
	
	/* Auto-add the first default entries on start up */
	
	public static void insert() {
		
		for( int i = 0; i < 3; i++) {
			User u;
			switch(i) {
			case 0:
				u = new User("Bob", "123", "Bob Roberts");
				userCreds.add(u);
				ABACPolicy.addACLEntry("Bob", true, true, false);
				break;
			case 1:
				u = new User("Sue","123", "Sue Williams");
				userCreds.add(u);
				ABACPolicy.addACLEntry("Sue", false, false, false);
				break;
			case 2:
				u = new User("Ragnar", "123", "Ragnar Jones");
				userCreds.add(u);
				ABACPolicy.addACLEntry("Ragnar", true, true, true);
				break;
			}
		}
	}
	
}