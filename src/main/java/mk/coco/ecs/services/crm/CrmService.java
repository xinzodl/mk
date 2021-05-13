package mk.coco.ecs.services.crm;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import lombok.extern.slf4j.Slf4j;

import static mk.coco.ecs.restcontent.RestContent.RequestType;
import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/crm/documentation/api/01-crm-api
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 */
@Slf4j
public class CrmService extends SemaasService {

    // Constructor

    /**
     * Returns a new CrmService
     *
     * @param secretName            Secret name
     */
    public CrmService(String secretName) throws Exception {
        serviceType = SupportedServices.CRM;
        log.debug("Creating CrmService with secretName {}", secretName);
        init(secretName);
    }

    /**
     * Returns a new CrmService
     */
    public CrmService() throws Exception {
        serviceType = SupportedServices.CRM;
        log.debug("Creating CrmService with secretName {}", serviceType.toString());
        init(serviceType.toString().toLowerCase());
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("No data was initialized in readAllDefaultValues method");
    }
    //POST

    /**
     * Creates a nominal namespace "user.xy00000"
     * @param ns            Name of the new namespace
     * @return              Response of the request
     * @throws Exception    If request fails
     */
    public String createNominalNamespace(String ns) throws Exception {
        String body = String.format("{ \"_id\": \"%s\"}"
                , ns);
        return request(
                urls.buildVersionYNsServiceURL(),
                body, cred, RequestType.POST);
    }

    /**
     * Creates a nominal namespace, getting the name from service.getUrls().gatNamespace()
     * @return              Response of the request
     * @throws Exception    If request fails
     */
    public String createNominalNamespace() throws Exception {
        return createNominalNamespace(urls.getNamespace());
    }
}
