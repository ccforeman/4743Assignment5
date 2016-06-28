package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import controller.LoginException;
import model.Session;
import model.User;
import security.Authenticator;
import security.Credentials;
import security.SecurityException;

public class AccessTests {
	
	private final Credentials EDIT = Credentials.EDIT_PART;
	private final Credentials ADD = Credentials.ADD_PART;
	private final Credentials DELETE = Credentials.DELETE_PART;
	
	
	private static User bob;
	private static User sue;
	private static User ragnar;
	
	@BeforeClass
	public static void init() {
		Authenticator auth = new Authenticator();
		bob = new User("Bob", "123", "Bob Roberts");
		sue = new User("Sue", "123", "Sue Williams");
		ragnar = new User("Ragnar", "123", "Ragnar Jones");
	}
	
	
	@Test // Test allowed to edit part with full permissions
	public void test1() {
		Session s = null;
		
		try {
			s = Authenticator.login(ragnar.getLogin(), ragnar.getPwHash());
		} catch (LoginException e) {
			fail("test1");
		}
		
		try {
			assertTrue(Authenticator.serverHasAccess(s, EDIT));
		} catch (SecurityException e) {
			fail("test1");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test allowed to add part with full permissions
	public void test2() {
		Session s = null;
		
		try {
			s = Authenticator.login(ragnar.getLogin(), ragnar.getPwHash());
		} catch (LoginException e) {
			fail("test2");
		}
		
		try {
			assertTrue(Authenticator.serverHasAccess(s, ADD));
		} catch (SecurityException e) {
			fail("test3");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test allowed to delete part with full permissions
	public void test3() {
		Session s = null;
		
		try {
			s = Authenticator.login(ragnar.getLogin(), ragnar.getPwHash());
		} catch (LoginException e) {
			fail("test3");
		}
		
		try {
			assertTrue(Authenticator.serverHasAccess(s, DELETE));
		} catch (SecurityException e) {
			fail("test3");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test allowed to edit part with permissions limited to no delete
	public void test4() {
		Session s = null;
		
		try {
			s = Authenticator.login(bob.getLogin(), bob.getPwHash());
		} catch (LoginException e) {
			fail("test4");
		}
		
		try {
			assertTrue(Authenticator.serverHasAccess(s, EDIT));
		} catch (SecurityException e) {
			fail("test4");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test allowed to add part with permissions limited to no delete
	public void test5() {
		Session s = null;
		
		try {
			s = Authenticator.login(bob.getLogin(), bob.getPwHash());
		} catch (LoginException e) {
			fail("test5");
		}
		
		try {
			assertTrue(Authenticator.serverHasAccess(s, ADD));
		} catch (SecurityException e) {
			fail("test5");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test not allowed to delete part with permissions limited to no delete
	public void test6() {
		Session s = null;
		
		try {
			s = Authenticator.login(bob.getLogin(), bob.getPwHash());
		} catch (LoginException e) {
			fail("test6");
		}
		
		try {
			assertFalse(Authenticator.serverHasAccess(s, DELETE));
		} catch (SecurityException e) {
			fail("test6");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test not allowed to edit part with no permission to do anything
	public void test7() {
		Session s = null;
		
		try {
			s = Authenticator.login(sue.getLogin(), sue.getPwHash());
		} catch (LoginException e) {
			fail("test7");
		}
		
		try {
			assertFalse(Authenticator.serverHasAccess(s, EDIT));
		} catch (SecurityException e) {
			fail("test7");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test not allowed to add part with no permission to do anything
	public void test8() {
		Session s = null;
		
		try {
			s = Authenticator.login(sue.getLogin(), sue.getPwHash());
		} catch (LoginException e) {
			fail("test8");
		}
		
		try {
			assertFalse(Authenticator.serverHasAccess(s, ADD));
		} catch (SecurityException e) {
			fail("test8");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test not allowed to delete part with no permission to do anything
	public void test9() {
		Session s = null;
		
		try {
			s = Authenticator.login(sue.getLogin(), sue.getPwHash());
		} catch (LoginException e) {
			fail("test9");
		}
		
		try {
			assertFalse(Authenticator.serverHasAccess(s, DELETE));
		} catch (SecurityException e) {
			fail("test9");
		}
		Authenticator.logout(s);
	}
	
	@Test // Test invalid session id throws errors
	public void test10() {
		Session s = null;
		
		try {
			s = Authenticator.login(ragnar.getLogin(), ragnar.getPwHash());
		} catch (LoginException e) {
			fail("test10");
		}
		Authenticator.logout(s);
		s = new Session();
		
		try {
			assertFalse(Authenticator.serverHasAccess(s, EDIT));
		} catch (SecurityException e) {
			assertEquals("Invalid session!", e.getMessage());
		}
	}
	
	@Test // Test must logout first before logging in again
	public void test11() {
		Session s = null;
		try {
			s = Authenticator.login(ragnar.getLogin(), ragnar.getPwHash());
			Authenticator.login(sue.getLogin(), sue.getPwHash());
			fail("test11");
		} catch (LoginException le) {
			assertEquals("You must logout first", le.getMessage());
		}
		
		Authenticator.logout(s);
	}
	
}
