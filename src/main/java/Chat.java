import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import static j2html.TagCreator.*;
import static spark.Spark.*;

public class Chat {

    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    // static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    
    // static Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    
    static int nextUserNumber = 1; //Assign to username for next connecting user
    // static Map<String, Integer> rooms = new ConcurrentHashMap<>();
    
    static Map<Session, UserInfo> users = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);
        webSocket("/chat", ChatWebSocketHandler.class);
        System.out.println("hari2");
        init();
    }

    //Sends a message from one user to all users, along with a list of current usernames
    public static void broadcastMessage(String sender, String message, int room) {
    	List<Session> sessionsToSend = new ArrayList<>();
    	List<String> usersList = new ArrayList<>();
    	
    	for (UserInfo user : users.values()) {
    		if (user.getRoom() == room) {
    			sessionsToSend.add(user.getSession());
    			usersList.add(user.getUserName());
    		}
    	}
    	
    	sessionsToSend.stream().filter(Session::isOpen).forEach(session ->{
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("userlist", usersList)
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
    	});
    	
    	/*
        userUsernameMap.keySet().stream().filter(Session::isOpen).forEach(session -> {
            try {
                session.getRemote().sendString(String.valueOf(new JSONObject()
                    .put("userMessage", createHtmlMessageFromSender(sender, message))
                    .put("userlist", userUsernameMap.values())
                ));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        */
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article(
            b(sender + " says:"),
            span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
            p(message)
        ).render();
    }
    

}
