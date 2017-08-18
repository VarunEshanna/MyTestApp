package org.adobe.assist.VirtualAssistance;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.adobe.sfdc.pojo.TestData;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("myresource")
public class MyResource {

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public TestData getIt() {
    	TestData td = new TestData();
    	td.setIntentName("Intent Name");
    	td.setLuisCallRequired(false);
    	td.setFinalResponseText("Congrats");
        return td;
    }
    
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public TestData postDataFromCustomer(TestData testdata) {
    	MyController myController = new MyController();
    	return myController.getResponseData(testdata);
/*    	
    	TestData td = new TestData();
    	td.setFinalResponseType(testdata.getUserQuery());
    	td.setIntentName("Intent Name");
    	td.setLuisCallRequired(false);
    	td.setFinalResponseText("Congrats");
        return td;*/
    }
    
}
