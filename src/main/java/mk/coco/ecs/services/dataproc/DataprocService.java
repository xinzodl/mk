package mk.coco.ecs.services.dataproc;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static mk.coco.ecs.restcontent.RestContent.RequestType;
import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/dataproc/documentation/dataproc-api/api/01-http-api-v2
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 *
 */

@Slf4j
public class DataprocService extends SemaasService {

    @Getter private String groups;
    @Getter private String jobs;
    @Getter private String runs;
    @Getter private String disable;
    @Getter private String enable;
    @Getter private String setUUAA;
    @Getter private String stop;

    @Getter @Setter private String jobVersion = "01";
    @Getter @Setter private String id = "spark";
    @Getter @Setter private String sparkVersion = "2.2.1";
    @Getter @Setter private String processing = "batch";
    @Getter @Setter private String description = "Se ejecuta un job de Spark";
    @Getter @Setter private String size = "S";

    // Constructor

    /**
     * Returns a new DataprocService
     *
     * @param secretName            Secret name
     */
    public DataprocService(String secretName) throws Exception {
        serviceType = SupportedServices.DATAPROC;
        log.debug("Creating DataprocService with secretName {}", secretName);
        init(secretName);
    }

    /**
     * Returns a new DataprocService using default secretName
     */
    public DataprocService() throws Exception {
        serviceType = SupportedServices.DATAPROC;
        log.debug("Creating DataprocService with secretName {}", serviceType.toString());
        init(serviceType.toString().toLowerCase());
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("Initializing data in readAllDefaultValues method with secretName {} and service {}", secretName, serv);

        groups = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "groups");
        jobs = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "jobs");
        runs = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "runs");
        disable = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "disable");
        enable = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "enable");
        setUUAA = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "setUUAA");
        stop = sec.readValueFromSecretWithKey(secretName,serv + urls.getDotUrlDot() + "stop");
    }

    /**
     *
     * @param jobName Nombre del job
     * @param artifactUrl Path desde donde nos descargamos el jar
     * @param configUrl Path desde donde nos descargamos la configuración
     * @param mainClass Main class que ejecutamos en el Job
     * @param map Map con el resto de parámetros del job
     * @return String con un body para la construcción de un Job
     */
    public String bodyCreator(String jobName, String artifactUrl, String configUrl, String mainClass, HashMap<String, String> map) {
        StringBuilder add = new StringBuilder();
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = it.next();
            add.append(",\"").append(pair.getKey()).append("\": \"").append(pair.getValue()).append("\"");
            it.remove();
        }

        return String.format("{\"jobName\": \"%s\"," +
                        "\"jobVersion\": \"%s\"," + "\"runtime\" : {" +
                        "\"id\":\"%s\"," +
                        "\"version\": \"%s\"," +
                        "\"processing\": \"%s\"}," +
                        "\"description\": \"%s\",\"configuration\": {" +
                        "\"size\": \"%s\"," + "\"params\": " +
                        "{\"artifactUrl\": \"%s\"," +
                        "\"configUrl\": \"%s\"," +
                        "\"mainClass\": \"%s\"" +
                        add +
                        "}}}",
                jobName,
                jobVersion,
                id,
                sparkVersion,
                processing,
                description,
                size,
                artifactUrl,
                configUrl,
                mainClass,
                add.toString());
    }

    // GETS

    /**
     *
     * @param groupName Nombre del grupo
     * @return La descripción de un grupo
     * @throws Exception If request fail
     */
    public String describeGroup(String groupName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @return La descripción de un job
     * @throws Exception If request fail
     */
    public String describeJob(String groupName, String jobName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash() + jobName),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @return La descripción de todos los grupos que existen en un namespace
     * @throws Exception If request fail
     */
    public String getAllGroups() throws Exception {
        return request(
                urls.buildNsServiceURL(groups),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param groupName Nombre del grupo
     * @return La descripción de todos los jobs que existen en un grupo
     * @throws Exception If request fail
     */
    public String getAllJobs(String groupName) throws Exception {
        return request(
          urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash()),
            "", cred, RequestType.GET);
    }

    /**
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @return El estado de todas las ejecuciones de un job
     * @throws Exception If request fail
     */
    public String getAllRuns(String groupName, String jobName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash() + jobName + runs),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @param runName Id de la ejecución de un Job
     * @return El estado de una ejecución específica
     * @throws Exception If request fail
     */
    public String getRunStatus(String groupName, String jobName, String runName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash() + jobName + runs + urls.getSlash() + runName),
                "", cred, RequestType.GET);
    }

    //POST

    /** Crea un grupo
     *
     * @param groupName Nombre del grupo
     * @param description Descripción
     * @return El grupo creado
     * @throws Exception If request fail
     */
    public String createGroup(String groupName, String description) throws Exception {
        String body = String.format("{ \"name\": \"%s\", " +
                            "\"description\": \"%s\"}"
                    , groupName, description);

        return request(
          urls.buildNsServiceURL(groups),
            body, cred, RequestType.POST);
    }

    /** Crea un grupo
     *
     * @param groupName Nombre del grupo
     * @param description Descripción
     * @param botName Nombre del Bot
     * @return El grupo creado
     * @throws Exception If request fail
     */
    public String createGroup(String groupName, String description, String botName) throws Exception {
        String body = String.format("{ \"name\": \"%s\", " +
                            "\"description\": \"%s\"," +
                            "\"botName\": \"%s\"}"
                    , groupName, description, botName);

        return request(
                urls.buildNsServiceURL(groups),
                body, cred, RequestType.POST);
    }

    /** Deshabilita un grupo
     *
     * @param groupName Nombre del grupo
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String disableGroup(String groupName) throws Exception {
        return request(
          urls.buildNsServiceURL(groups + urls.getSlash() + groupName + urls.getColon() + disable),
            "", cred, RequestType.POST);
    }

    /** Habilita un grupo
     *
     * @param groupName Nombre del grupo
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String enableGroup(String groupName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + urls.getColon() + enable),
                "", cred, RequestType.POST);
    }

    /** Crea un job
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @param artifactUrl Path desde donde nos descargamos el jar
     * @param configUrl Path desde donde nos descargamos la configuración
     * @param mainClass Main class que ejecutamos en el Job
     * @return El grupo creado
     * @throws Exception If request fail
     */
    public String registerJob(String groupName, String jobName, String artifactUrl, String configUrl, String mainClass) throws Exception {
        String body = bodyCreator(jobName, artifactUrl, configUrl, mainClass, new HashMap<>());
        return registerJob(body, groupName);
    }

    /** Crea un job
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @param artifactUrl Path desde donde nos descargamos el jar
     * @param configUrl Path desde donde nos descargamos la configuración
     * @param mainClass Main class que ejecutamos en el Job
     * @param map Map con el resto de parámetros del job
     * @return El grupo creado
     * @throws Exception If request fail
     */
    public String registerJob(String groupName, String jobName, String artifactUrl, String configUrl, String mainClass, HashMap<String, String>  map) throws Exception {
        String body = bodyCreator(jobName, artifactUrl, configUrl, mainClass, map);
        return registerJob(body, groupName);
    }

    /** Crea un Job con un body no predefinido
     *
     * @param body body del Job creado
     * @param groupName Nombre del grupo
     * @return El job creado
     * @throws Exception If request fail
     */
    public String registerJob(String body, String groupName) throws Exception {
        return request(
          urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs),
            body, cred, RequestType.POST);
    }

    /** Ejecuta un job
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @param params Parámetros para el job
     * @return La ejecución del Job
     * @throws Exception If request fail
     */
    public String runJob(String groupName, String jobName, String params) throws Exception {
        String body = String.format("{\"params\": %s}",params);
        return request(
          urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash() + jobName + runs),
            body, cred, RequestType.POST);
    }

    /** Setea una UUAA en el Namespace
     *
     * @param uuaa UUAA del Namespace
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String setUUAA(String uuaa) throws Exception {
        String body = String.format("{\"uuaa\": \"%s\"}",uuaa);
        return request(
          urls.buildNsServiceURL(urls.getColon() + setUUAA),
            body, cred, RequestType.POST);
    }

    /** Para una ejecución de un job
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @param runName Id de la ejecución de un Job
     * @return El Job parado
     * @throws Exception If request fail
     */
    public String stopRun(String groupName, String jobName, String runName) throws Exception {
        return request(
          urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash() + jobName + runs + urls.getSlash() + runName + urls.getColon() + stop),
            "", cred, RequestType.POST);
    }

    //DELETE

    /** Elimina un grupo, el grupo ha de estar vacío.
     *
     * @param groupName Nombre del grupo
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String deleteGroup(String groupName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName),
                "", cred, RequestType.DELETE);
    }

    /** Elimina un job, no debe estar ejecutando el job.
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String deleteJob(String groupName, String jobName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash() + jobName),
                "", cred, RequestType.DELETE);
    }

    //PATCH

    /** Actualiza un grupo
     *
     * @param botName Nombre del Bot
     * @param groupName Nombre del grupo
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String patchGroupBotName(String botName, String groupName) throws Exception {
        String body = String.format("{\"botName\": \"%s\"}", botName);
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + urls.getSlash()),
                body, cred, RequestType.PATCH);
    }

    /** Actualiza un grupo con un body no predefinido
     *
     * @param body Body de la llamada API REST
     * @param groupName Nombre del grupo
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String patchGroup(String body, String groupName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + urls.getSlash()),
                body, cred, RequestType.PATCH);
    }

    //PUT

    /** Sobreescribe un job
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @param artifactUrl Path desde donde nos descargamos el jar
     * @param configUrl Path desde donde nos descargamos la configuración
     * @param mainClass Main class que ejecutamos en el Job
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String updateJob(String groupName, String jobName, String artifactUrl, String configUrl, String mainClass) throws Exception {
        String body = bodyCreator(jobName, artifactUrl, configUrl, mainClass, new HashMap<>());
        return updateJob(body, groupName, jobName);
    }

    /** Sobreescribe un job
     *
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @param artifactUrl Path desde donde nos descargamos el jar
     * @param configUrl Path desde donde nos descargamos la configuración
     * @param mainClass Main class que ejecutamos en el Job
     * @param map Map con el resto de parámetros del job
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String updateJob(String groupName, String jobName, String artifactUrl, String configUrl, String mainClass, HashMap<String, String> map) throws Exception {
        String body = bodyCreator(jobName, artifactUrl, configUrl, mainClass, map);
        return updateJob(body, groupName, jobName);
    }

    /** Sobreescribe un job con un body no predefinido
     * 
     * @param body Body de la llamada API REST
     * @param groupName Nombre del grupo
     * @param jobName Nombre del job
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String updateJob(String body, String groupName, String jobName) throws Exception {
        return request(
                urls.buildNsServiceURL(groups + urls.getSlash() + groupName + jobs + urls.getSlash() + jobName),
                body, cred, RequestType.PUT);
    }
}
