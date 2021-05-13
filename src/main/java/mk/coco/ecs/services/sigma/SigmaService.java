package mk.coco.ecs.services.sigma;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import static mk.coco.ecs.restcontent.RestContent.RequestType;
import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/sigma/documentation/api/v0
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 */
@Slf4j
public class SigmaService extends SemaasService {

    @Getter private String alarmReceivers;
    @Getter private String alarmTypes;
    @Getter private String alarms;
    @Getter private String alarmStatuses;
    @Getter private String setStatus;

    public enum DefaultStatus {
        OK,
        WARNING,
        CRITICAL,
        STALLED
    }

    public enum AlarmReceiverKinds {
        MAIL,
        SLACK,
        SUPPORT_LEVEL1,
        WEBHOOK
    }

    // Constructor

    /**
     * Returns a new SigmaService
     *
     * @param secretName            Secret name
     */
    public SigmaService(String secretName) throws Exception {
        serviceType = SupportedServices.SIGMA;
        log.debug("Creating SigmaService with secretName {} ", secretName);
        init(secretName);
        urls.setServiceOption(alarms);
    }

    /**
     * Returns a new SigmaService
     */
    public SigmaService() throws Exception {
        serviceType = SupportedServices.SIGMA;
        log.debug("Creating SigmaService with secretName {} and overrideAlarmId sigma", serviceType.toString());
        init(serviceType.toString().toLowerCase());
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("Initializing data in readAllDefaultValues method with secretName {} and service {}", secretName, serv);

        alarmReceivers = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "alarm-receivers");
        alarmTypes = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "alarm-types");
        alarms = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "alarms");
        alarmStatuses = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "alarm-statuses");
        setStatus = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "setStatus");
    }

    public void setAlarmId(String alarmId) {
        urls.setOptionName(alarmId);
    }

    public String getAlarmReceiverPath(String sigmaAlarmReceiverID) {
        return urls.getSlash() + urls.getSlash() + urls.getServiceUrlName() + urls.getDot() + urls.getZone() + urls.getNs() + urls.getSlash() + urls.getNamespace() + urls.getSlash() + alarmReceivers + urls.getSlash() + sigmaAlarmReceiverID;
    }

    public String getAlarmTypePath(String sigmaAlarmTypeID) {
        return urls.getSlash() + urls.getSlash() + urls.getServiceUrlName() + urls.getDot() + urls.getZone() + urls.getNs() + urls.getSlash() + urls.getNamespace() + urls.getSlash() + alarmTypes + urls.getSlash() + sigmaAlarmTypeID;
    }

    // Body composition

    private String createAlertBody(String status, String message){return "{\"status\":\"" + status + "\",\"reason\":\"" + message + "\"}";}
    private String createAlarmReceiverBody(String id, AlarmReceiverKinds kind, String receiver){
        String preparedReceiver = receiver.trim().toLowerCase();
        String rcvr;

        if (kind == AlarmReceiverKinds.MAIL && preparedReceiver.endsWith("@bbva.com")) {
            rcvr = "\"mail\": \"" + preparedReceiver + "\"";
        } else if (kind == AlarmReceiverKinds.SLACK && preparedReceiver.startsWith("https://")) {
            rcvr = "\"webhook\":" + "\"" + preparedReceiver + "\"";
        } else if (kind == AlarmReceiverKinds.WEBHOOK && preparedReceiver.startsWith("https://")) {
            rcvr = "\"url\":" + "\"" + preparedReceiver + "\"";
        } else {
            rcvr = "";
        }

        return "{\"_id\": \"" + id + "\", \"kind\": \"" + kind + "\", \"config\": {" + rcvr + "}}";
    }
    private String createSimpleAlarmTypeBody(String id, int statusFrequency, boolean stateChangesOnly, List<String> alarmReceiverPathList){
        List<String> notificationPoliciesList = alarmReceiverPathList.stream()
                .map( path -> "{\"stateChangesOnly\": " + stateChangesOnly + ", \"alarmReceiver\": \"" + path + "\"}")
                .collect(Collectors.toList());

        String notificationPolicies = String.join(", ", notificationPoliciesList);

        return "{\"_id\": \""+ id + "\", \"statusFrequency\": " + statusFrequency +
                ", \"notificationPolicies\": [ " + notificationPolicies + "]}";
    }
    private String createSimpleAlarmBody(String id, String alarmType, boolean enabled, String monitoredResource, String desc, String group){
        return "{\"_id\": \"" + id + "\", \"alarmType\": \"" + alarmType + "\", \"enabled\": "
                + enabled + ", \"monitoredResource\": \"" + monitoredResource + "\", \"properties\": {\"description\": \""
                + desc + "\", \"group\": \"" + group + "\"}}";
    }

    // Usage

    /**
     * Send email (alert) of type status with "message" content, to the alarm with id "overrideAlarmId"
     *
     * @param message           Message to be sent
     * @param status            Alarm status
     * @param overrideAlarmId   Overrides default alarmId
     * @return                  Request response
     * @throws Exception        If request fails
     */
    public String sendEmail(String message, DefaultStatus status, String overrideAlarmId) throws Exception {
        urls.setServiceOption(alarms);
        urls.setAction(setStatus);
        urls.setOptionName(overrideAlarmId);
        String escapedMessage = StringEscapeUtils.escapeJava(message);
        String body = createAlertBody(status.toString(), escapedMessage);
        String buildUrl = urls.buildActionDefaultURL();
        log.debug("Sending email to " + buildUrl + " with body " + body);

        return request(buildUrl, body, cred, RequestType.POST);
    }

    /**
     * gets info about sigma service
     * @return              Info in a JSON format
     * @throws Exception    If request fails
     */
    public String getInfo() throws Exception {
        return request(urls.buildServiceBaseAddress() + "_service/info", "", cred, RequestType.GET);
    }

    // alarm receiver

    /**
     * Lists all alarm receivers
     * @return              All alarm receivers in a JSON format
     * @throws Exception    If request fails
     */
    public String getAlarmsReceivers() throws Exception {
        urls.setServiceOption(alarmReceivers);
        return request(urls.buildCommonServiceURL(), "", cred, RequestType.GET);
    }

    /**
     * Get information about an alarm receiver
     * @param id            Id of the requested alarm receiver
     * @return              Information in a JSON format
     * @throws Exception    If request fails
     */
    public String getAlarmReceiver(String id) throws Exception {
        urls.setServiceOption(alarmReceivers);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RequestType.GET);
    }

    /**
     * Creates an alarm receiver
     * @param id                Id of the new alarm receiver
     * @param kind              Alarm receiver kind
     * @param receiver          Emails, or web addresses
     * @return                  Info of the new alarm receiver in a JSON format
     * @throws Exception        If request fails
     */
    public String createAlarmReceiver(String id, AlarmReceiverKinds kind, String receiver) throws Exception {
        String body = createAlarmReceiverBody(id, kind, receiver);
        urls.setServiceOption(alarmReceivers);
        return request(urls.buildCommonServiceURL(), body, cred, RequestType.POST);
    }

    /**
     * Deletes an alarm receiver
     * @param id            Id of the alarm receiver to be deleted
     * @return              Generally an empty string
     * @throws Exception    If request fails
     */
    public String deleteAlarmReceiver(String id) throws Exception {
        urls.setServiceOption(alarmReceivers);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RequestType.DELETE);
    }

    /**
     * Enables all alarm receiver by type
     * @param kind          Type to be enabled
     * @return              Info in a JSON format
     * @throws Exception    If request fails
     */
    public String enableNsAlarmReceiver(AlarmReceiverKinds kind) throws Exception {
        String body = "{\"kind\": \"" + kind.toString() + "\"}";
        urls.setServiceOption(alarmReceivers);
        urls.setAction("enable");
        return request(urls.buildCommonServiceURL(urls.getColon() + urls.getAction()), body, cred, RequestType.POST);
    }

    /**
     * Disables all alarm receivers by type
     * @param kind          type to be disabled
     * @return              Info in a JSOn format
     * @throws Exception    If request fails
     */
    public String disableNsAlarmReceiver(AlarmReceiverKinds kind) throws Exception {
        String body = "{\"kind\": \"" + kind.toString() + "\"}";
        urls.setServiceOption(alarmReceivers);
        urls.setAction("disable");
        return request(urls.buildCommonServiceURL(urls.getColon() + urls.getAction()), body, cred, RequestType.POST);
    }

    // alarm type

    /**
     * Get all alarm types
     * @return              Info in a JSON format
     * @throws Exception    If request fails
     */
    public String getAlarmTypes() throws Exception {
        urls.setServiceOption(alarmTypes);
        return request(urls.buildCommonServiceURL(), "", cred, RequestType.POST);
    }

    /**
     * Get info of an alarm type
     * @param id            Id of the requested alarm type
     * @return              Info in a JOSN format
     * @throws Exception    If request fails
     */
    public String getAlarmType(String id) throws Exception {
        urls.setServiceOption(alarmTypes);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RequestType.GET);
    }

    /**
     * Delete an alarm type
     * @param id            Id of the alarm type to be deleted
     * @return              Generally an empty string
     * @throws Exception    If request fails
     */
    public String deleteAlarmType(String id) throws Exception {
        urls.setServiceOption(alarmTypes);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RequestType.DELETE);
    }

    /**
     * Creates an alarm type
     * @param id                            Id of the new alarm type
     * @param statusFrequency               See docs
     * @param stateChangesOnly              Send alarts only if state changes
     * @param alarmReceiverPathList         Urls of the alarm receiver
     * @return                              Info of the new alarm type in a JSON format
     * @throws Exception                    If request fails
     */
    public String createAlarmType(String id, int statusFrequency, boolean stateChangesOnly, List<String> alarmReceiverPathList) throws Exception {
        String body = createSimpleAlarmTypeBody(id, statusFrequency, stateChangesOnly, alarmReceiverPathList);
        return createAlarmTypeWithBody(id, body);
    }

    /**
     * Creates an alarm type
     * @param id            Id of the new alarm type
     * @param body          Body in a JSON format
     * @return              Info of the new alarm type in a JSON format
     * @throws Exception    If request fails
     */
    public String createAlarmTypeWithBody(String id, String body) throws Exception {
        urls.setServiceOption(alarmTypes);
        return request(urls.buildCommonServiceURL(), body, cred, RequestType.POST);
    }

    // alarms

    /**
     * Get all alarms
     * @return              All alarms in a JSON format
     * @throws Exception    If request fails
     */
    public String getAlarms() throws Exception {
        urls.setServiceOption(alarms);
        return request(urls.buildCommonServiceURL(), "", cred, RequestType.GET);
    }

    /**
     * Get an alarm
     * @param id            Id of the requested alarm
     * @return              Info in a JSOn format
     * @throws Exception    If request fails
     */
    public String getAlarm(String id) throws Exception {
        urls.setServiceOption(alarms);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RequestType.GET);
    }

    /**
     * Create a new alarm
     * @param id                    Id of the new alarm
     * @param alarmType             Alarm type
     * @param enabled               Enables / Disabled
     * @param monitoredResource     Path to monitored resource
     * @param description           Description of the new alarm
     * @param group                 Processing group
     * @return                      Info in a JSON format
     * @throws Exception            If request fails
     */
    public String createAlarm(String id, String alarmType, boolean enabled, String monitoredResource, String description, String group) throws Exception {
        urls.setServiceOption(alarms);
        String body = createSimpleAlarmBody(id, alarmType, enabled, monitoredResource, description, group);
        return request(urls.buildCommonServiceURL(), body, cred, RequestType.POST);
    }

    /**
     * Deletes an alarm
     * @param id            Id to be deleted
     * @return              Generally empty string
     * @throws Exception    If request fails
     */
    public String deleteAlarm(String id) throws Exception {
        urls.setServiceOption(alarms);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RequestType.DELETE);
    }

    /**
     * Sets enabled status to enabled / disabled
     * @param id            Alarm's id to change state
     * @param setTo         Set to enabled / disabled
     * @return              Info in a JSOn format
     * @throws Exception    If request fails
     */
    public String setEnabledAlarmTo(String id, boolean setTo) throws Exception {
        urls.setServiceOption(alarms);
        urls.setOptionName(id);
        urls.setAction("setEnabled");
        String body = "{\"enabled\": " + setTo + "}";
        return request(urls.buildActionDefaultURL(), body, cred, RequestType.POST);
    }

    /**
     * Reset elapsed time
     * @param id            Id of the alarm to reset
     * @return              Info in a JSON format
     * @throws Exception    If request fails
     */
    public String resetAlarmStatusElapsedTime(String id) throws Exception {
        urls.setServiceOption(alarms);
        urls.setOptionName(id);
        urls.setAction("resetStatusElapsedTime");
        String body = "{\"statuses\": [\"" +
                Stream.of(DefaultStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining("\", \""))
                + "\"]}";
        return request(urls.buildActionDefaultURL(), body, cred, RequestType.POST);
    }

    /**
     * Get a count of alarms by status
     * @return              Info in a JSON format
     * @throws Exception    If request fails
     */
    public String getAlarmCount() throws Exception {
        urls.setServiceOption(alarms);
        String body = "{\"statuses\": [\"" +
                Stream.of(DefaultStatus.values())
                .map(Enum::name)
                .collect(Collectors.joining("\", \""))
                + "\"]}";
        return request(urls.buildCommonServiceURL(urls.getSlash() + "count"), body, cred, RequestType.GET);
    }

    /**
     * Get alarm status history
     * @return              Info in a JSO nformat
     * @throws Exception    If request fails
     */
    public String getAlarmStatusHistory() throws Exception {
        urls.setServiceOption(alarmStatuses);
        return request(urls.buildCommonServiceURL(), "", cred, RequestType.GET);
    }

    // Unification

    public String createAlarmFullProcess(
            String sigmaAlarmId,
            String sigmaAlarmReceiverIDRoot,
            String sigmaAlarmTypeID,
            List<String> receiversList,
            int statusFrequency,
            boolean stateChangesOnly,
            boolean enableAlarm,
            String alarmDescription,
            String alarmGroup,
            SigmaService.AlarmReceiverKinds alarmKind,
            boolean sendConfirmationEmail,
            String mrTypePath
    ) throws Exception {
        StringBuilder responses = new StringBuilder();

        // 1 - Create Alarm Receiver
        responses.append("\n## Alarm receiver response: ");
        List<String> idList = new ArrayList<String>();
        for (int ii = 0; ii < receiversList.size() ; ii++) {
            String id_iteration_name = sigmaAlarmReceiverIDRoot + "_" + ii;
            responses.append(createAlarmReceiver(id_iteration_name, alarmKind, receiversList.get(ii)));
            idList.add(getAlarmReceiverPath(id_iteration_name));
        }

        // 2 - Create Alarm Type
        responses.append("\n## Alarm receiver response: ");
        responses.append(createAlarmType(sigmaAlarmTypeID, statusFrequency, stateChangesOnly, idList));

        // 3 - Create Alarm
        String alarmTypePath = getAlarmTypePath(sigmaAlarmTypeID);
        responses.append("\n## Alarm response: ");
        responses.append(createAlarm(sigmaAlarmId, alarmTypePath, enableAlarm, mrTypePath, alarmDescription, alarmGroup));

        // 4 - Send test alarm (MAIL)
        if (sendConfirmationEmail && alarmKind.equals(SigmaService.AlarmReceiverKinds.MAIL)){
            responses.append("\n## Email response: ");
            responses.append(sendEmail("New mail alarm created successfully, test email", DefaultStatus.OK, sigmaAlarmId));
        }

        return responses.toString();
    }

}











