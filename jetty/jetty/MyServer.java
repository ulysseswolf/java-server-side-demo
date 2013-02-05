package jetty;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyServer {
    private static Logger logger = LoggerFactory.getLogger(MyServer.class);

    static Server server;

    static {
        try {
            Resource resource = Resource.newSystemResource("jetty.xml");
            XmlConfiguration configuration = new XmlConfiguration(resource.getInputStream());
            server = (Server) configuration.configure();
        } catch (Exception e) {
            logger.error("Server initail error ", e);
            System.exit(0);
        }
    }

    public static void main(String[] args) throws Exception {
        server.start();
        server.join();
    }
    
    public static void start() throws Exception {
    	server.start();
    }
    
    public static void stop() throws Exception {
    	server.stop();
    }
}
