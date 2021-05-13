package mk.coco.ecs.restcontent;

import mk.coco.ecs.credentials.PemEtherCredentials;
import mk.coco.ecs.utils.FileUtils;

import javax.net.ssl.SSLContext;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;

@Slf4j
public class RestContent {

    /**
     * Supported request types
     */
    public enum RequestType {
        DELETE,
        GET,
        PATCH,
        POST,
        PUT
    }

    /**
     * Gets a CloseableHttpClient given a valid PemEtherCredentials
     *
     * @param cred          Valid PemEtherCredentials
     * @return              A CloseableHttpClient
     * @throws Exception    If credentials are not valid or connection / ssl context fails
     */
    private static CloseableHttpClient getHttpClient(PemEtherCredentials cred) throws Exception {
        SSLContext sslContext = SSLContexts.custom().loadKeyMaterial(cred.getKeyStore(), cred.getKeyStorePassword()).build();
        return HttpClients.custom().setSSLContext(sslContext).build();
    }

    /**
     * Returns a Get/post request, depending on "rt"
     *
     * @param rt            Request type: Get / Post
     * @param url           Url of the request
     * @param body          Body of the request (not used on GET, only in POST)
     * @return              A HttpUriRequest being Get or Post
     * @throws Exception    If Unexpected values are found
     */
    private static HttpUriRequest getRequest(RequestType rt, String url, String body) throws Exception {
        StringEntity bodyEntity = new StringEntity(body);
        switch(rt) {
            case DELETE:
                return new HttpDelete(url);
            case GET:
                return new HttpGet(url);
            case PATCH:
                HttpPatch requestPatch = new HttpPatch(url);
                requestPatch.setEntity(bodyEntity);
                return requestPatch;
            case POST:
                HttpPost requestPost = new HttpPost(url);
                requestPost.setEntity(bodyEntity);
                return requestPost;
            case PUT:
                HttpPut requestPut = new HttpPut(url);
                requestPut.setEntity(bodyEntity);
                return requestPut;
            default:
                throw new IllegalStateException("Unexpected value: " + rt);
        }
    }

    /**
     * Returns response from the request
     *
     * @param entity        A valid HttpEntity (Get / post)
     * @param httpResponse  A valid CloseableHttpResponse
     * @param url           The url used (for debug purposes)
     * @return              The content of the response
     * @throws Exception    If didn't get valid status codes or if no response was received
     */
    private static String getContent(HttpEntity entity, CloseableHttpResponse httpResponse, String url) throws Exception {
        String content;
        if (entity != null) {
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            List<Integer> codigosAceptados = Arrays.asList(200,201,202,204);
            InputStream inputStream = entity.getContent();
            String getString = FileUtils.readFromInputStream(inputStream);
            inputStream.close();
            content = getString;
            if (!codigosAceptados.contains(statusCode)) {
                throw new Exception("Error getStatusCode " + statusCode + " in httpResponse with url: " + url + " with response: " + content);
            }
        } else {
            if (httpResponse.getStatusLine().getStatusCode() != 204) {
                throw new Exception("No response in HttpRequest to url: " + url);
            } else {
                content = "Http code 204: successful request but no response from server";
            }
        }
        return content;
    }

    /**
     * Submits the request
     *
     * @param url           Url of the request
     * @param body          Body of the request (not used on GET, only in POST)
     * @param cred          Necessary credential to submit the request
     * @param rt            Request type: Get / Post
     * @return              Message returned by the request (response)
     * @throws Exception    If Connection is not successful
     */
    public static String request(String url, String body, PemEtherCredentials cred, RequestType rt) throws Exception {
        if (cred == null) throw new Exception("PemEtherCredentials can't be null, please provide valid credentials");

        CloseableHttpClient httpClient = getHttpClient(cred);
        log.debug("Creating request to {} with {}", url, body);
        HttpUriRequest request = getRequest(rt, url, body);
        CloseableHttpResponse httpResponse = httpClient.execute(request);
        HttpEntity entity = httpResponse.getEntity();

        String content = getContent(entity, httpResponse, url);
        httpClient.close();

        return content;
    }

}
