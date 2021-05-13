package mk.coco.ecs.services.psi;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.services.common.SemaasService;
import mk.coco.ecs.services.common.SupportedServices;

import static mk.coco.ecs.restcontent.RestContent.RequestType;
import static mk.coco.ecs.restcontent.RestContent.request;

/**
 * Should implement all functionality from https://platform.bbva.com/en-us/developers/pushsub/documentation/05-api-clientsdk/api-http-v1
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 */

@Slf4j
public class PsiService extends SemaasService {

    @Getter private String messeges;
    @Getter private String stores;
    @Getter private String subscriptions;
    @Getter private String topics;

    // Constructor

    /**
     * Returns a new PsiService
     *
     * @param secretName            Secret name
     */
    public PsiService(String secretName) throws Exception {
        serviceType = SupportedServices.PSI;
        log.debug("Creating PsiService with secretName {}", secretName);
        init(secretName);
    }

    /**
     * Returns a new PsiService using default secretName
     */
    public PsiService() throws Exception {
        serviceType = SupportedServices.PSI;
        log.debug("Creating PsiService with secretName {}", serviceType.toString());
        init(serviceType.toString().toLowerCase());
    }

    // Setters & Getters

    protected void readAllDefaultValues(MySecrets sec, String secretName, String serv) {
        log.debug("Initializing data in readAllDefaultValues method with secretName {} and service {}", secretName, serv);
        String dot = ".";

        String url = "url";
        messeges = sec.readValueFromSecretWithKey(secretName,serv + dot + url + dot + "messeges");
        stores = sec.readValueFromSecretWithKey(secretName,serv + dot + url + dot + "stores");
        subscriptions = sec.readValueFromSecretWithKey(secretName,serv + dot + url + dot + "subscriptions");
        topics = sec.readValueFromSecretWithKey(secretName,serv + dot + url + dot + "topics");
    }

    // GET

    /**
     *
     * @param messageId Id del mensaje
     * @return Un mensaje
     * @throws Exception If request fail
     */
    public String getMessage(String messageId) throws Exception {
        return request(
                urls.buildNsServiceURL(messeges + urls.getSlash() + messageId),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @return Lista todos los mensaje
     * @throws Exception If request fail
     */
    public String listMessage() throws Exception {
        return request(
                urls.buildNsServiceURL(messeges),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param storeId Id del store
     * @return Un store
     * @throws Exception If request fail
     */
    public String getStore(String storeId) throws Exception {
        return request(
                urls.buildNsServiceURL(stores + urls.getSlash() + storeId),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @return Lista todos los store
     * @throws Exception If request fail
     */
    public String listStore() throws Exception {
        return request(
                urls.buildNsServiceURL(stores),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param topicId Id del topic
     * @param subscriptionId Id de la suscripción
     * @return Una suscripción
     * @throws Exception If request fail
     */
    public String getSubscription(String topicId,String subscriptionId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId + subscriptions + urls.getSlash() + subscriptionId),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param topicId Id del topic
     * @return Lista todas las suscripciones
     * @throws Exception If request fail
     */
    public String listSubscription(String topicId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId + subscriptions),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @param topicId Id del topic
     * @return Un tópic
     * @throws Exception If request fail
     */
    public String getTopic(String topicId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId),
                "", cred, RequestType.GET);
    }

    /**
     *
     * @return Lista todos los tópic
     * @throws Exception If request fail
     */
    public String listTopic() throws Exception {
        return request(
                urls.buildNsServiceURL(topics),
                "", cred, RequestType.GET);
    }

    // POST

    /** Crea un mensaje
     *
     * @param messageId Id del mensaje
     * @param description Descripción
     * @param protoSpec Define los tipos del mensaje
     * @return El mensaje creado
     * @throws Exception If request fail
     */
    public String createMessage(String messageId, String description, String protoSpec) throws Exception {
        String body = String.format("{ \"_id\": \"%s\"," +
                        "\"description\": \"%s\"," +
                        "\"protoSpec\":{%s}" +
                        "}",
                messageId,
                description,
                protoSpec);
        return createMessage(body);
    }

    /** Crea un mensaje con un body
     *
     * @param body Body de la llamada API REST
     * @return El mensaje creado
     * @throws Exception If request fail
     */
    public String createMessage(String body) throws Exception {
        return request(
                urls.buildNsServiceURL(messeges),
                body, cred, RequestType.POST);
    }

    /** Crea un store
     *
     * @param storeId Id del store
     * @param description Descripción
     * @param kind Uno de los tipos permitidos
     * @param properties Propiedades del Kind
     * @return El store creado
     * @throws Exception If request fail
     */
    public String createStore(String storeId, String description, String kind, String properties) throws Exception {
        String body = String.format("{ \"_id\": \"%s\"," +
                        "\"description\": \"%s\"," +
                        " \"kind\": \"%s\"," +
                        " \"properties\":{%s}" +
                        "}",
                storeId,
                description,
                kind,
                properties);
        return createStore(body);
    }

    /** Crea un store con un body
     *
     * @param body Body de la llamada API REST
     * @return El store creado
     * @throws Exception If request fail
     */
    public String createStore(String body) throws Exception {
        return request(
                urls.buildNsServiceURL(stores),
                body, cred, RequestType.POST);
    }

    /** Crea una suscripción
     *
     * @param topicId Id del topic
     * @param subscriptionId Id de la suscripción
     * @param description Descripción
     * @param kind Uno de los tipos permitidos
     * @param properties Propiedades del Kind
     * @return La suscripción creada
     * @throws Exception If request fail
     */
    public String createSubscription(String topicId, String subscriptionId, String description, String kind, String properties) throws Exception {
        String body = String.format("{ \"_id\": \"%s\"," +
                        "\"description\": \"%s\"," +
                        " \"kind\": \"%s\"," +
                        " \"properties\":{%s}" +
                        "}",
                subscriptionId,
                description,
                kind,
                properties);
        return createSubscription(body, topicId);
    }

    /** Crea una suscripción con un body
     *
     * @param topicId Id del topic
     * @param body Body de la llamada API REST
     * @return La suscripción creada
     * @throws Exception If request fail
     */
    public String createSubscription(String body, String topicId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId + subscriptions),
                body, cred, RequestType.POST);
    }

    /** Manage de una suscripción
     *
     * @param topicId Id del topic
     * @param subscriptionId Id de la suscripción
     * @param append start, stop o test
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception Excepción
     */
    private String manageSubscription(String topicId, String subscriptionId, String append) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId + subscriptions + urls.getSlash() + subscriptionId + urls.getColon() + append),
                "", cred, RequestType.POST);
    }

    /** Start de una suscripción
     *
     * @param topicId Id del topic
     * @param subscriptionId Id de la suscripción
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception Excepción
     */
    public String startSubscription(String topicId, String subscriptionId) throws Exception {
        return manageSubscription(topicId, subscriptionId, "start");
    }

    /** Stop de una suscripción
     *
     * @param topicId Id del topic
     * @param subscriptionId Id de la suscripción
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception Excepción
     */
    public String stopSubscription(String topicId, String subscriptionId) throws Exception {
        return manageSubscription(topicId, subscriptionId, "stop");
    }

    /** Test de una suscripción
     *
     * @param topicId Id del topic
     * @param subscriptionId Id de la suscripción
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception Excepción
     */
    public String testSubscription(String topicId, String subscriptionId) throws Exception {
        return manageSubscription(topicId, subscriptionId, "test");
    }

    /** Crea un tópic
     *
     * @param topicId Id del topic
     * @param description Descripción
     * @return El topic creado
     * @throws Exception If request fail
     */
    public String createTopic(String topicId, String description) throws Exception {
        String body = String.format("{ \"_id\": \"%s\"," +
                        "\"description\": \"%s\"}",
                topicId,
                description);
        return createTopic(body);
    }

    /** Crea un tópic con un body
     *
     * @param body Body de la llamada API REST
     * @return El topic creado
     * @throws Exception If request fail
     */
    public String createTopic(String body) throws Exception {
        return request(
                urls.buildNsServiceURL(topics),
                body, cred, RequestType.POST);
    }

    /** Escribe en un tópic
     *
     * @param body Body de la llamada API REST
     * @param topicId Id del topic
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String publishMessageInTopic(String body, String topicId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId + urls.getColon() + "publishMessage"),
                body, cred, RequestType.POST);
    }

    /** Escribe en batch en un tópic
     *
     * @param body Body de la llamada API REST
     * @param topicId Id del topic
     * @return Numero de mensajes escritos y número de mensajes fallados
     * @throws Exception If request fail
     */
    public String publishMessageBatchInTopic(String body, String topicId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId + urls.getColon() + "publishMessageBatch"),
                body, cred, RequestType.POST);
    }

    // DELETE

    /** Elimina un mensaje
     *
     * @param messageId Id del mensaje
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String deleteMessage(String messageId) throws Exception {
        return request(
                urls.buildNsServiceURL(messeges + urls.getSlash() + messageId),
                "", cred, RequestType.DELETE);
    }

    /** Elimina un store
     *
     * @param storeId Id del store
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String deleteStore(String storeId) throws Exception {
        return request(
                urls.buildNsServiceURL(stores + urls.getSlash() + storeId),
                "", cred, RequestType.DELETE);
    }

    /** Elimina una suscripción
     *
     * @param topicId Id del topic
     * @param subscriptionId Id de la suscripción
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String deleteSubscription(String topicId, String subscriptionId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId + subscriptions + urls.getSlash() + subscriptionId),
                "", cred, RequestType.DELETE);
    }

    /** Elimina un tópic
     *
     * @param topicId Id del topic
     * @return String "Http code 204: successful request but no response from server"
     * @throws Exception If request fail
     */
    public String deleteTopic(String topicId) throws Exception {
        return request(
                urls.buildNsServiceURL(topics + urls.getSlash() + topicId),
                "", cred, RequestType.DELETE);
    }

}
