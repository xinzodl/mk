package mk.coco.ecs.services.mu;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.restcontent.RestContent;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;

import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/mu/documentation/api/v0
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 *
 *
 */
@Slf4j
public class MuService extends SemaasService {

    @Getter private String metrics;
    @Getter private String metricsSetTypes;
    @Getter private String metricsSets;
    @Getter private String addMetrics;
    @Getter private String addMeasurements;

    // Constructor

    /**
     * Returns a new MuService
     *
     * @param secretName                Secret name
     */
    public MuService(String secretName) throws Exception {
        serviceType = SupportedServices.MU;
        log.debug("Creating MuService with secretName {}", secretName);
        init(secretName);
    }

    /**
     * Returns a new MuService using default secretName
     */
    public MuService() throws Exception {
        serviceType = SupportedServices.MU;
        log.debug("Creating MuService with secretName {}", serviceType.toString().toLowerCase());
        init(serviceType.toString().toLowerCase());
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("Initializing data in readAllDefaultValues method with secretName {} and service {}", secretName, serv);

        addMetrics = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "addMetrics");
        addMeasurements = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "addMeasurements");
        metrics = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "metrics");
        metricsSetTypes = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "metric-set-types");
        metricsSets = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "metric-sets");
    }

    // Body composition
    private String createMetricBody(String id, String dataType, String dataUnit, String description) {
        return "{ \"_id\": \"" + id + "\", \"dataType\": \"" + dataType + "\", \"dataUnit\": \""
                + dataUnit + "\", \"description\": \"" + description + "\" }";
    }
    private String createMetricSetTypeBody(String id, String metricsSpecName, String metricsSpecPath) {
        return "{\"_id\": \"" + id + "\", \"metricsSpec\":{\"" + metricsSpecName + "\":\"" + metricsSpecPath + "\"}}";
    }
    private String createMetricSetBody(String id, String metricSetType, String monitoredResource) {
        return "{ \"_id\": \"" + id + "\", \"metricSetType\": \"" + metricSetType + "\", \"monitoredResource\": \"" + monitoredResource + "\" }";
    }
    private String createAddMetricBody(String id, long tsInMillis, String value) {
        return "{\"metrics\": [{\"timestamp\": " + tsInMillis + " ,\"values\": {\"" + id + "\": " + value + " }}]}";
    }

    public String getMetricsSpecPath(String metricID) {
        return urls.getSlash() + urls.getSlash() + urls.getServiceUrlName() + urls.getDot() + urls.getZone() + urls.getNs()
                +  urls.getSlash() + urls.getNamespace() + urls.getSlash() + metrics + urls.getSlash() + metricID;
    }

    public String getMetricsSetTypesPath(String metricSetID) {
        return urls.getSlash() + urls.getSlash() + urls.getServiceUrlName() + urls.getDot() + urls.getZone() + urls.getNs()
                +  urls.getSlash() + urls.getNamespace() + urls.getSlash() + metricsSetTypes + urls.getSlash() + metricSetID;
    }

    // Usage

    // Metrics

    /**
     * Lists all metrics
     * @return              List of all metrics in a JSON format
     * @throws Exception    If connection fails
     */
    public String listMetrics() throws Exception {
        urls.setServiceOption(metrics);
        return request(urls.buildCommonServiceURL( ), "", cred, RestContent.RequestType.GET);
    }

    /***
     * Returns info from a specific metric
     * @param id            Id of the requested metric
     * @return              Info from the requested metric in a JSON format
     * @throws Exception    If connection fails
     */
    public String getMetric(String id) throws Exception {
        urls.setServiceOption(metrics);
        return request(urls.buildCommonServiceURL( urls.getSlash() + id), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Created new metric
     * @param id            Id of the new metric
     * @param dataType      One of the supported data types
     * @param dataUnit      One of the supported data units
     * @param description   Description of the metric
     * @return              Info of the new metric in a JSON format
     * @throws Exception    If connection fails
     */
    public String createMetric(String id, String dataType, String dataUnit, String description) throws Exception {
        urls.setServiceOption(metrics);
        String escapedDescription = StringEscapeUtils.escapeJava(description);
        String body = createMetricBody(id, dataType.toLowerCase().trim(), dataUnit.toUpperCase().trim(), escapedDescription);
        return request(urls.buildCommonServiceURL(), body, cred, RestContent.RequestType.POST);
    }

    // Metric set type

    /**
     * Creates a new metric set type
     * This {id} is the name you will see in Atenea, under "CHOOSE METRICS"
     * This {metricsSpecName} is the metric under the {id} metric set
     * @param id                Id of the new metric set type
     * @param metricsSpecName   Metric spec name
     * @param metricsSpecPath   Metric spec path
     * @return                  Information of the new metric in a JSON format
     * @throws Exception        If connection fails
     */
    public String createMetricSetType(String id, String metricsSpecName, String metricsSpecPath) throws Exception {
        urls.setServiceOption(metricsSetTypes);
        String body = createMetricSetTypeBody(id, metricsSpecName, metricsSpecPath);
        return request(urls.buildCommonServiceURL(), body, cred, RestContent.RequestType.POST);
    }

    /**
     * Get a metric set type
     * @param id            Requested id
     * @return              Info of a metric set type in a JSON format
     * @throws Exception    If connection fails
     */
    public String getMetricSetType(String id) throws Exception {
        urls.setServiceOption(metricsSetTypes);
        return request(urls.buildCommonServiceURL(), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Deletes a metric set type
     * @param id            Id of the metric set type to be deleted
     * @return              Generally empty string
     * @throws Exception    If connection fails
     */
    public String deleteMetricSetType(String id) throws Exception {
        urls.setServiceOption(metricsSetTypes);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RestContent.RequestType.DELETE);
    }

    /**
     * Lists all metric set types
     * @return              JSON with information of all metric set types
     * @throws Exception    If connection fails
     */
    public String listMetricSetType() throws Exception {
        urls.setServiceOption(metricsSetTypes);
        return request(urls.buildCommonServiceURL(), "", cred, RestContent.RequestType.GET);
    }

    // Metric set

    /**
     * Lists all metric sets
     * @return              All metric sets in a JSON format
     * @throws Exception    If connection fails
     */
    public String listMetricSets() throws Exception {
        urls.setServiceOption(metricsSets);
        return request(urls.buildCommonServiceURL(), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Creates a metric set
     * This {id} is each one of the metric sets under each metric
     * This {metricSetType} is the "parent"
     * @param id                          Id of the new metric set
     * @param metricsSetTypePath          path to the metric set type
     * @param monitoredResource           path to the monitored resource
     * @return                            JSON of the new metric set created
     * @throws Exception                  If connection fails
     */
    public String createMetricSet(String id, String metricsSetTypePath, String monitoredResource) throws Exception {
        urls.setServiceOption(metricsSets);
        String body = createMetricSetBody(id, metricsSetTypePath, monitoredResource);
        return request(urls.buildCommonServiceURL(), body, cred, RestContent.RequestType.POST);
    }

    /**
     * Adds a new value {value} to the metric {metricId} under the metric set {metricSet} at a specific timestamp {ts}
     * @param metricSet         Metric set of choice
     * @param metricSetName     Id of the metric
     * @param tsInMillis        Timestamp in milliseconds
     * @param value             New value
     * @return                  Usually nothing
     * @throws Exception        If request fails
     */
    public String addMetric(String metricSet, String metricSetName, long tsInMillis, String value) throws Exception {
        urls.setServiceOption(metricsSets);
        urls.setOptionName(metricSet);
        urls.setAction(addMeasurements);
        String body = createAddMetricBody(metricSetName, tsInMillis * 1000000, value);
        return request(urls.buildActionDefaultURL(), body, cred, RestContent.RequestType.POST);
    }

    /**
     * Lists all metric set
     * @return              JSON with information of all metric set types
     * @throws Exception    If connection fails
     */
    public String listMetricSet() throws Exception {
        urls.setServiceOption(metricsSets);
        return request(urls.buildCommonServiceURL(), "", cred, RestContent.RequestType.GET);
    }
    /**
     * Get a specific metric set
     * @param id            Id of the requested metric set
     * @return              JSON with information about the requested id
     * @throws Exception    If connection fails
     */
    public String getMetricSet(String id) throws Exception {
        urls.setServiceOption(metricsSets);
        return request(urls.buildCommonServiceURL(), "", cred, RestContent.RequestType.GET);
    }

    /**
     * Deletes a metric set
     * @param id            Id of the metric set to be deleted
     * @return              Generally an empty string
     * @throws Exception    If connection fails
     */
    public String deleteMetricSet(String id) throws Exception {
        urls.setServiceOption(metricsSets);
        return request(urls.buildCommonServiceURL(urls.getSlash() + id), "", cred, RestContent.RequestType.DELETE);
    }

    // Unification

    public String createMetricFullProcess(
            String monitoredResourcePath,
            String metricID,
            String dataType,
            String dataUnit,
            String metricDescription,
            String metricSetTypeID,
            String metricsSpecName,
            String metricSetID
    ) throws Exception {
        StringBuilder responses = new StringBuilder();

        responses.append("\n## Create metric response: ");
        responses.append(createMetric(metricID, dataType, dataUnit, metricDescription));

        responses.append("\n## Create metric set type response: ");
        String metricsSpecPath = getMetricsSpecPath(metricID);
        responses.append(createMetricSetType(metricSetTypeID, metricsSpecName, metricsSpecPath));

        responses.append("\n## Create metric set response: ");
        String metricsSetTypePath = getMetricsSetTypesPath(metricSetTypeID);
        responses.append(createMetricSet(metricSetID, metricsSetTypePath, monitoredResourcePath));

        return responses.toString();
    }

}
