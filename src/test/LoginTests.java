package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import controller.LoginException;
import model.Session;
import model.User;
import security.Authenticator;

public class LoginTests {
	
	@BeforeClass
	public static void init() {
		@SuppressWarnings("unused")
		Authenticator auth = new Authenticator();
	}
	
	@Test // Test valid login
	public void test1() {
		User user = new User("Bob", "123", "Bob Roberts");
		Session s = null;
		
		try {
			s = Authenticator.login(user.getLogin(), user.getPwHash());
		} catch (LoginException e) {
			fail("test1");
		}
		Authenticator.logout(s);
		assertNotNull(s);
	}
	
	@Test // Test invalid login
	public void test2() {
		User user = new User("invalid", "123", "Bob Roberts");
		Session s = null;
		
		try {
			s = Authenticator.login(user.getLogin(), user.getPwHash());
		} catch (LoginException e) {
			assertEquals("Invalid Login!", e.getMessage());
		}
		
		assertNull(s);
		
	}
	
	@Test // Test invalid password
	public void test3() {
		User user = new User("Bob", "1234", "Bob Roberts");
		Session s = null;
		
		try {
			s = Authenticator.login(user.getLogin(), user.getPwHash());
		} catch (LoginException e) {
			assertEquals("Invalid Login!", e.getMessage());
		}
		
		assertNull(s);
		
	}

	@Test // Test invalid login and password
	public void test4() {
		User user = new User("invalid", "1234", "Bob Roberts");
		Session s = null;
//		MDIParent m = new MDIParent("Test", new WarehouseList(), new PartList());
		
		try {
			s = Authenticator.login(user.getLogin(), user.getPwHash());
		} catch (LoginException e) {
			assertEquals("Invalid Login!", e.getMessage());
		}
		
		assertNull(s);
		
	}
	
}
