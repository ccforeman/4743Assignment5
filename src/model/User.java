package model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import security.Hasher;

public class User {
	private long id;
	private String login;
	private String pwHash;
	private String fullName;
	
	private static long idCounter = 0L;
	
	public User() {
		this.id = idCounter++;
	}
	
	public User(String login, String pw, String fullName) {
//		this();
		this.login = login;
		setPwHash(pw);
		this.fullName = fullName;
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getLogin() {
		return login;
	}
	
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getPwHash() {
		return pwHash;
	}
	
	public void setPwHash(String password){
		
		this.pwHash = Hasher.getPasswordHash(password); 
	}
	
	public String getFullName() {
		return fullName;
	}
	
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
}
