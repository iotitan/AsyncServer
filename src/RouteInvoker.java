import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.github.iotitan.annotations.GET;


public class RouteInvoker {
	
	/**
	 * Grabs all methods with a specific kind of annotation with a specific value
	 * @param route
	 * @param annotationClass
	 * @param value
	 * @TODO CACHE METHODS and OPTIMIZE!
	 */
	public void callMethodByAnnotation(Object route, String annotationClass, String value){
		Class routeHandler = route.getClass();
		Method[] methods = routeHandler.getMethods();
		for(Method method : methods){
			Annotation[] annotations = method.getDeclaredAnnotations();
			for(Annotation annotation: annotations){
				try {
					method.invoke(route, ASockSession.request, ASockSession.output);
				} catch (IllegalAccessException
						| IllegalArgumentException
						| InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
