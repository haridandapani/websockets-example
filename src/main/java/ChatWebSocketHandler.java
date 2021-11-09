import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

@WebSocket
public class ChatWebSocketHandler {

    private String sender, msg;

    @OnWebSocketConnect
    public void onConnect(Session user) throws Exception {
    	System.out.println(user.getLocalAddress());
    	System.out.println("aaaaaaaaaaah");
    	System.out.println(user.getRemoteAddress());
    	String urlString = user.getUpgradeRequest().getRequestURI().toString();
    	System.out.println(urlString);
    	int room = Integer.valueOf(urlString.split("room=")[1]);

    	System.out.println(room);
        String username = "User" + Chat.nextUserNumber++;
    	// Chat.rooms.put(username, room);
        // Chat.userUsernameMap.put(user, username);
        
        // Chat.sessionMap.put(username, user);
        UserInfo thisUser = new UserInfo(user, room, username);
        Chat.users.put(user, thisUser);
        
        Chat.broadcastMessage(sender = "Server", msg = (username + " joined the chat"), room);
    }

    @OnWebSocketClose
    public void onClose(Session user, int statusCode, String reason) {
        String username = Chat.users.get(user).getUserName();
        int room = Chat.users.get(user).getRoom();
        // Chat.userUsernameMap.remove(user);
        Chat.users.remove(user);
        Chat.broadcastMessage(sender = "Server", msg = (username + " left the chat"), room);
    }

    @OnWebSocketMessage
    public void onMessage(Session user, String message) {
    	String username = Chat.users.get(user).getUserName();
    	int room = Chat.users.get(user).getRoom();
        Chat.broadcastMessage(sender = username, msg = message, room);
    }

}
