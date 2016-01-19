import play.*;
import play.mvc.Action;
import play.mvc.Http.Request;
import java.lang.reflect.Method;

public class Global extends GlobalSettings {

    private static final Logger.ALogger logger = Logger.of(Global.class);

    public Action onRequest(Request request, Method actionMethod) {
        logger.info("request=" + request);
        return super.onRequest(request, actionMethod);
    }

}
