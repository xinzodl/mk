package mk.coco.ecs.services.monitoredresource;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.restcontent.RestContent;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/atenea/documentation/monitored-resources-atenea
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 */
@Slf4j
public class MonitoredResourceService extends SemaasService {

    @Getter private String mrTypes;
    @Getter private String mrs;

    // Constructor

    /**
     * Returns a new MonitoredResource using default secretName
     */
    public MonitoredResourceService() throws Exception {
        serviceType = SupportedServices.MR;
        log.debug("Creating MonitoredResourceService with secretName {}", serviceType.toString());
        init(serviceType.toString().toLowerCase());
    }

    /**
     * Returns a new MonitoredResource
     *
     * @param secretName            Secret name
     */
    public MonitoredResourceService(String secretName) throws Exception {
        serviceType = SupportedServices.MR;
        log.debug("Creating MonitoredResourceService with secretName {}", secretName);
        init(secretName);
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("Initializing data in readAllDefaultValues method with secretName {} and service {}", secretName, serv);

        mrTypes = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "mr-types");
        mrs = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "mrs");
    }

    // Body composition

    private String createMonitorResourceTypeBody(String id){
        return "{\"_id\":\"" + id + "\", \"sourceOf\": [\"TRACES\",\"LOGS\",\"METRICS\",\"ALARMS\"]}";
    }

    private String createMonitorResourceBody(String id, String mrType){
        return "{\"_id\":\"" + id + "\", \"mrType\": \""+ mrType +"\"}";
    }

    public String getMonitoredResourceTypePath(String monitoredResourceTypeID) {
        return urls.getSlash() + urls.getSlash() + urls.getServiceUrlName() + urls.getDot() + urls.getZone() + urls.getNs()
                +  urls.getSlash() + urls.getNamespace() + urls.getSlash() + mrTypes + urls.getSlash() + monitoredResourceTypeID;
    }

    public String getMonitoredResourcePath(String monitoredResourceID) {
        return urls.getSlash() + urls.getSlash() + urls.getServiceUrlName() + urls.getDot() + urls.getZone() + urls.getNs()
                +  urls.getSlash() + urls.getNamespace() + urls.getSlash() + mrs + urls.getSlash() + monitoredResourceID;
    }

    // Usage

    // MR types

    /**
     * List all monitored resource types
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String listMonitorResourcesTypes() throws Exception {
        urls.setServiceOption(mrTypes);
        return request(urls.buildCommonServiceURL(), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Get information of a monitored resource type
     * @param id            Requested id
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String getMonitorResourcesType(String id) throws Exception {
        urls.setServiceOption(mrTypes);
        return request(urls.buildCommonServiceURL( urls.getSlash() + id), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Deletes monitor resource type
     * @param id            Id to be deleted
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String deleteMonitorResourcesType(String id) throws Exception {
        urls.setServiceOption(mrTypes);
        return request(urls.buildCommonServiceURL( urls.getSlash() + id), "", cred, RestContent.RequestType.DELETE);
    }

    /**
     * Create monitor resource type
     * @param id            Id to be created
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String createDefaultMonitorResourceType(String id) throws Exception {
        String body = createMonitorResourceTypeBody(id);
        return createMonitorResourceType(body);
    }

    /**
     * Create monitor resource type
     * @param body          Body of the request (must contain ID and SOURCE_OF)
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String createMonitorResourceType(String body) throws Exception {
        urls.setServiceOption(mrTypes);
        return request(urls.buildCommonServiceURL(), body, cred, RestContent.RequestType.POST);
    }

    // MR

    /**
     *list monitor resources
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String listMonitorResources() throws Exception {
        urls.setServiceOption(mrs);
        return request(urls.buildCommonServiceURL(), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Get on monitor resource
     * @param id
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String getMonitorResources(String id) throws Exception {
        urls.setServiceOption(mrs);
        return request(urls.buildCommonServiceURL( urls.getSlash() + id), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Delete a monitor resource
     * @param id            Id to be deleted
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String deleteMonitorResources(String id) throws Exception {
        urls.setServiceOption(mrs);
        return request(urls.buildCommonServiceURL( urls.getSlash() + id), "", cred, RestContent.RequestType.DELETE);
    }

    /**
     * Create monitor resource
     * @param id            Id to be created
     * @param mrTypeID      Monitor resource type id
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String createMonitorResourceFromTypeId(String id, String mrTypeID) throws Exception {
        String mrTypePath = getMonitoredResourceTypePath(mrTypeID);
        return createMonitorResourceFromTypePath(id, mrTypePath);
    }

    /**
     * Create monitor resource
     * @param id            Id to be created
     * @param mrTypePath    Monitor resource type locator
     * @return              Info in JSON format
     * @throws Exception    If connection is not possible
     */
    public String createMonitorResourceFromTypePath(String id, String mrTypePath) throws Exception {
        urls.setServiceOption(mrs);
        String body = createMonitorResourceBody(id, mrTypePath);
        return request(urls.buildCommonServiceURL(), body, cred, RestContent.RequestType.POST);
    }

}
