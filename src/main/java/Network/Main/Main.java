package Network.Main;

import Network.ModelGateway;
import core.controller.GameController;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

public class Main {
    public static final int PORT = 5002;
    public static final String URI = "http://0.0.0.0/";

    public static final String[] RESOURCES_PACKAGES = {"Network.Resources"};
    public static GameController controller;

    public static void main(String [] args){
        startServer();
    }


    public static HttpServer startServer() {
        controller = new GameController();
        ModelGateway.setController(controller);

        ModelGateway.getController().registerNewPlayer("Sam");
        ModelGateway.getController().registerNewPlayer("Nick");
        ModelGateway.getController().registerNewPlayer("Walker");
        ModelGateway.getController().registerNewPlayer("Michael");
        ModelGateway.getController().newPublicGame("Michael", "Nick");
        ModelGateway.getController().newPublicGame("Sam", "Nick");
        ModelGateway.getController().newPublicGame("Walker", "Sam");
        ModelGateway.getController().newPublicGame("Sam", "Nick");
        ModelGateway.getController().newPublicGame("Walker", "Sam");
        ModelGateway.getController().newPublicGame("Nick", "Sam");
        ModelGateway.getController().newPublicGame("Sam", "Nick");
        ModelGateway.getController().newPublicGame("Sam", "Walker");
        ModelGateway.getController().makeMove(1,0,0, "Michael");
        ModelGateway.getController().makeMove(1,1,1, "Nick");
        ModelGateway.getController().makeMove(1,2,2, "Nick");
        ModelGateway.getController().makeMove(1,0,1, "Michael");
        ModelGateway.getController().makeMove(1,0,2, "Michael");
        ModelGateway.getController().makeMove(1,3,3, "Nick");
        ModelGateway.getController().makeMove(1,4,4, "Nick");
        ModelGateway.getController().makeMove(1,0,3, "Michael");
        ModelGateway.getController().makeMove(1,0,4, "Michael");
        ModelGateway.getController().makeMove(1,5,5, "Nick");
        ModelGateway.getController().makeMove(1,6,6,"Nick");

        URI baseUri = UriBuilder.fromUri(URI).port(PORT).build();
        final ResourceConfig config = new ResourceConfig().packages(RESOURCES_PACKAGES);
        return GrizzlyHttpServerFactory.createHttpServer(baseUri, config);
    }

}
