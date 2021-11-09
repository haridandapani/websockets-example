import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;

import freemarker.template.Configuration;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import static j2html.TagCreator.*;

public class Chat {

    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    
	static int nextUserNumber = 1; //Assign to username for next connecting user
    
    static Map<Session, UserInfo> users = new ConcurrentHashMap<>();

    public static void main(String[] args) {    
        System.out.println("hari2");
        runSparkServer(4567);
    }
    
    private static void runSparkServer(int port) {
        Spark.port(port);
        Spark.externalStaticFileLocation("src/main/resources/public");
        
        FreeMarkerEngine engine = createEngine();
        Spark.webSocket("/chat", ChatWebSocketHandler.class);
        Spark.get("/ws", new Chat.HomePage(), engine);
        Spark.init();
      }
    
	private static FreeMarkerEngine createEngine() {
		Configuration config = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
		File templates = new File("src/main/resources/public");
		try {
			config.setDirectoryForTemplateLoading(templates);
		} catch (IOException ioe) {
			System.out.printf("ERROR: Unable use %s for template loading.%n", templates);
			System.exit(1);
		}
		return new FreeMarkerEngine(config);
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
    	
    }

    //Builds a HTML element with a sender-name, a message, and a timestamp,
    private static String createHtmlMessageFromSender(String sender, String message) {
        return article(
            b(sender + " says:"),
            span(attrs(".timestamp"), new SimpleDateFormat("HH:mm:ss").format(new Date())),
            p(message)
        ).render();
    }
    
    public static class HomePage implements TemplateViewRoute {
		@Override
		public ModelAndView handle(Request request, Response response) throws Exception {
			// TODO Auto-generated method stub
			return new ModelAndView(null, "websocket.ftl");
		}	
    }

}
