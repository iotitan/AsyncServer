import com.github.iotitan.annotations.GET;

public class RouteHandler {
	
	@GET("/")
	public void helloWorld(HTTPHeader input, String output){
		System.out.println(input);
	}
}
