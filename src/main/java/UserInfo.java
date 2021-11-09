import org.eclipse.jetty.websocket.api.Session;

public class UserInfo {
	
	private Session userSession;
	private int room;
	private String userName;
	
	public UserInfo(Session userSession, int room, String userName) {
		this.userSession = userSession;
		this.room = room;
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public int getRoom() {
		return room;
	}
	
	public Session getSession() {
		return userSession;
	}

}
