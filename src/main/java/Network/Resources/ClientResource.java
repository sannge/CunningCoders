package Network.Resources;

import javax.ws.rs.*;
import java.io.InputStream;

/**
 * This is the resource that serves the client application to a web browser.
 */
@Path("client")
public class ClientResource {


    /**
     * Returns the HTML for the client application.
     *
     * @return HTML document that contains the client user interface.
     */
    @GET
    public InputStream getClient() {
        return this.getClass().getResourceAsStream("/client.html");
    }

    /**
     * Returns the Javascript file(s) requested
     * @param fileName The name of the javascript file
     */
    @GET
    @Path("{file}")
    @Produces("application/javascript")
    public InputStream getClientJavascript(@PathParam("file") String fileName) {
        InputStream stream = this.getClass().getResourceAsStream("/" + fileName);
        if(stream == null) {
            throw new WebApplicationException(404);
        }
        return stream;
    }

}
