package mk.coco.ecs.services.rho;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static mk.coco.ecs.restcontent.RestContent.RequestType;
import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/rho/documentation/api/traces-api-http-v1
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 */
@Slf4j
public class RhoService extends SemaasService {
    @Getter private String spans;
    @Getter private String traces;
    @Getter private String trace;

    // Constructor

    /**
     * Returns a new RhoService
     *
     * @param secretName            Secret name
     */
    public RhoService(String secretName) throws Exception {
        serviceType = SupportedServices.RHO;
        log.debug("Creating RhoService with secretName {}", secretName);
        init(secretName);
    }

    /**
     * Returns a new RhoService
     */
    public RhoService() throws Exception {
        serviceType = SupportedServices.RHO;
        log.debug("Creating RhoService with secretName {}", serviceType.toString().toLowerCase());
        init(serviceType.toString().toLowerCase());
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("Initializing data in readAllDefaultValues method with secretName {} and service {}", secretName, serv);

        spans = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "spans");
        traces = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "traces");
        trace = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "trace");
    }

    // GET

    /**
     *
     * @return All Spans in a namespace
     * @throws Exception If request fail
     */
    public String findSpans() throws Exception {
        return request(
            urls.buildNsServiceURL(spans),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @return All Traces in a namespace
     * @throws Exception If request fail
     */
    public String findTraces() throws Exception {
        return request(
            urls.buildNsServiceURL(traces),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param mrsName Monitored Resource Name
     * @param spanId Span Id
     * @return Spans in a Monitored Resource
     * @throws Exception If request fail
     */
    public String loadSpan(String mrsName, String spanId) throws Exception {
        return request(
                urls.buildNsServiceURL(urls.getMrs() + urls.getSlash() + mrsName + spans + urls.getSlash() + spanId),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param mrsName Monitored Resource Name
     * @param traceId Trace Id
     * @return Traces in a Monitored Resource
     * @throws Exception If request fail
     */
    public String loadTrace(String mrsName, String traceId) throws Exception {
        return request(
                urls.buildNsServiceURL(urls.getMrs() + urls.getSlash() + mrsName + traces + urls.getSlash() + traceId),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param mrsName Monitored Resource Name
     * @param spanId Span Id
     * @return Trace of a Span in a Monitored Resource
     * @throws Exception If request fail
     */
    public String loadSpanTrace(String mrsName, String spanId) throws Exception {
        return request(
                urls.buildNsServiceURL(urls.getMrs() + urls.getSlash() + mrsName + spans + urls.getSlash() + spanId + urls.getColon() + trace),
                "", cred, RequestType.GET);
    }

    // POST

    /** Create a Span from a Body
     *
     * @param body Body to create Span
     * @return Empty String
     * @throws Exception If request fail
     */
    public String createSpans(String body) throws Exception {
        return request(
                urls.buildNsServiceURL(spans),
                body, cred, RequestType.POST);
    }
    /** Create a Span from a predefined Body
     *
     * @param spanName Span Name
     * @param mrsName Monitored Resource Name
     * @param spanId Span Id
     * @param traceId Trace Id
     * @param startDate Start Date in nanoseconds
     * @param finishDate Finish Date in nanoseconds
     * @return Empty String
     * @throws Exception If request fail
     */
    public String createSpans(String spanName, String mrsName, String spanId, String traceId, String startDate, String finishDate) throws Exception {
        String body = String.format("[{ \"name\": \"%s\",  " +
                        "\"mrId\": \"%s\", " +
                        "\"spanId\": \"%s\", " +
                        "\"startDate\": %s, " +
                        "\"finishDate\": %s, " +
                        "\"traceId\": \"%s\"}]",
                spanName, mrsName, spanId, startDate, finishDate, traceId);
        return createSpans(body);
    }
}
