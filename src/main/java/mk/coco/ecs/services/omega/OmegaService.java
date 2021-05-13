package mk.coco.ecs.services.omega;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.restcontent.RestContent;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/omega/documentation/api/logs-api-http-v1
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 *
 *
 */
@Slf4j
public class OmegaService extends SemaasService {

    @Getter private String mrs;
    @Getter private String logs;

    public enum OmegaLogLevel {
        WARN,
        WARNING,
        ERROR,
        FATAL,
        TRACE,
        DEBUG,
        INFO
    }

    // Constructor

    /**
     * Returns a new OmegaService
     *
     * @param secretName            Certificate secret name
     */
    public OmegaService(String secretName) throws Exception {
        serviceType = SupportedServices.OMEGA;
        log.debug("Creating OmegaService with secretName {}", secretName);
        init(secretName);
    }

    /**
     * Returns a new OmegaService
     */
    public OmegaService() throws Exception {
        serviceType = SupportedServices.OMEGA;
        log.debug("Creating OmegaService with secretName {}", serviceType.toString().toLowerCase());
        init(serviceType.toString().toLowerCase());
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("Initializing data in readAllDefaultValues method with secretName {} and service {}", secretName, serv);

        mrs = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "mrs");
        logs = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "logs");
    }

    // Body composition

    private String getAppendBody(String mrId, String jobName, OmegaLogLevel level, String message, long creationDateInNano) {
        return "[{\"mrId\": \"" + mrId + "\", \"properties\": {\"jobName\": \"" + jobName + "\"}, \"level\": \"" + level
                + "\", \"message\" : \"" + message + "\", \"creationDate\": " + creationDateInNano + " }]";
    }
    private String getAppendBody(String mrId, OmegaLogLevel level, String message, long creationDateInNano) {
        return "[{\"mrId\": \"" + mrId + "\", \"level\": \"" + level
                + "\", \"message\" : \"" + message + "\", \"creationDate\": " + creationDateInNano + " }]";
    }

    // Usage
    /**
     *  Get logs of a monitored resource
     * @param mrID              Monitored Resource ID to get logs from
     * @param dynamicParams     String started by "?" and followed by any of the params bellow, with appropriate value (and with "&" between them):
     *                                                          {SEE Docs for details}
     *                          q=[query]
     *                          pageSize=[size]
     *                          sort=[sort]
     *                          paginationKey=[key]
     *         Use "" if no dynamicParams is needed
     * @throws Exception        If connection is not possible
     * @return                  Response in a JSON format
     */
    public String readLogs(String mrID, String dynamicParams) throws Exception {
        urls.setServiceOption(mrs);
        String append = urls.getSlash() + mrID + urls.getSlash() + logs + dynamicParams;
        return request(urls.buildCommonServiceURL(append), "", cred, RestContent.RequestType.GET);
    }

    /**
     *  Get logs of a monitored resource
     * @param mrID              Monitored Resource ID to get logs from
     * @throws Exception        If connection is not possible
     * @return                  Response in a JSON format
     */
    public String readLogs(String mrID) throws Exception {
        return readLogs(mrID, "");
    }

    /**
     *  Get logs of a monitored resource and submits a specific body
     * @param mrID              Monitored Resource ID to get logs from
     * @param body              Body of the request
     * @return                  Info in a JSON format
     * @throws Exception        If connection is not possible
     */
    public String readLogsWithBody(String mrID, String body) throws Exception {
        urls.setServiceOption(mrs);
        String append = urls.getSlash() + mrID + urls.getSlash() + logs;
        return request(urls.buildCommonServiceURL(append), body, cred, RestContent.RequestType.POST);
    }

    /**
     * Append logs (stack trace of a Throwable) to a monitored resource
     * @param mrId              Monitored Resource ID to append logs to
     * @param level             Log level
     * @param message           Will work as a header for the log message
     * @param e                 Throwable (stack trace will be obtained and logged)
     * @param creationDate      Log creation date in milliseconds
     * @return                  Info in a JSON format
     * @throws Exception        If connection is not possible
     */
    public String appendLogs(String mrId, OmegaLogLevel level, String message, Throwable e, long creationDate) throws Exception {
        String msg= message + "\n";
        for (StackTraceElement ste : e.getStackTrace()) {msg += ste.toString() +"\n";}
        return appendLogs(mrId, null, level, msg, creationDate);
    }

    /**
     * Append logs to a monitored resource with a basic body
     * @param mrId              Monitored Resource ID to append logs to
     * @param level             Log level
     * @param message           My log message
     * @param creationDate      Log creation date in milliseconds
     * @return                  Info in a JSON format
     * @throws Exception        If connection is not possible
     */
    public String appendLogs(String mrId, OmegaLogLevel level, String message, long creationDate) throws Exception {
        return appendLogs(mrId, null, level, message, creationDate);
    }

    /**
     * Append logs to a monitored resource with a basic body
     * @param mrId                      Monitored Resource ID to append logs to
     * @param jobName                   Job name
     * @param level                     Log level
     * @param message                   My log message
     * @param creationDateInMillis      Log creation date in milliseconds
     * @return                          Info in a JSON format
     * @throws Exception                If connection is not possible
     */
    public String appendLogs(String mrId, String jobName, OmegaLogLevel level, String message, long creationDateInMillis) throws Exception {
        String escapedMessage = StringEscapeUtils.escapeJava(message);
        String body;
        if (jobName == null) {
            body = getAppendBody(mrId, level, escapedMessage, creationDateInMillis * 1000000);
        } else {
            body = getAppendBody(mrId, jobName, level, escapedMessage, creationDateInMillis * 1000000);
        }
        return appendLogsWithBody(mrId, body);
    }

    /**
     *  Append logs to a monitored resource with a given body
     * @param mrId              Monitored Resource ID to append logs to
     * @param body              Body of the request
     * @return                  Info in a JSON format
     * @throws Exception        If connection is not possible
     */
    public String appendLogsWithBody(String mrId, String body) throws Exception {
        urls.setServiceOption(mrs);
        String append = urls.getSlash() + mrId + urls.getSlash() + logs;
        return request(urls.buildCommonServiceURL(append), body, cred, RestContent.RequestType.POST);
    }

}
