package model;

public class Session {
	private long id;
	
	private static long idCounter = 0L;
	
	public Session() {
		this.id = idCounter++;
	}
	
//	public Session(User user) {
//		this();
//	}

	public long getId() {
		return id;
	}

}
