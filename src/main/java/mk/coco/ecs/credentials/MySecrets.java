package mk.coco.ecs.credentials;

import io.fabric8.kubernetes.api.model.DoneableSecret;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.client.AutoAdaptableKubernetesClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

@Slf4j
public class MySecrets {

    Config config = new ConfigBuilder().withDisableHostnameVerification(true).build();
    KubernetesClient client = new AutoAdaptableKubernetesClient(config);

    /**
     * Read value "key" from secret "secretName"
     * @param secretName    Name of the secret
     * @param key           Key you read
     * @return              Value fo the key in the secret
     */
    public String readValueFromSecretWithKey(String secretName, String key) {
        try {
            log.debug("Reading from secret {} with key {}.", secretName, key);
            Resource<Secret, DoneableSecret> secretResource = client.secrets().withName(secretName);
            byte[] valueDecoded = Base64.decodeBase64(secretResource.get().getData().get(key));
            return new String(valueDecoded);

        } catch (Exception e) {
            log.error("There was an error recovering secret key value.", e);
            return "";
        }
    }
}
