package mk.coco.ecs.services.common;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.credentials.PemEtherCredentials;
import mk.coco.ecs.utils.URLs;

public abstract class SemaasService {
    protected PemEtherCredentials cred;
    protected URLs urls;
    protected SupportedServices serviceType;

    public SupportedServices getServiceType() {
        return serviceType;
    }

    public URLs getUrls() {
        return urls;
    }

    public void setCred(PemEtherCredentials cred) {
        this.cred = cred;
    }

    public PemEtherCredentials getCred() {
        return cred;
    }

    protected abstract void readAllDefaultValues(MySecrets sec, String secretName, String serv);

    protected void init(String secretName) throws Exception {
        String serviceTypeStr = serviceType.toString().toLowerCase();
        urls = new URLs(serviceType, secretName);
        MySecrets sec = new MySecrets();
        cred = PemEtherCredentials.fromString(sec.readValueFromSecretWithKey(secretName, urls.getCrt()), sec.readValueFromSecretWithKey(secretName, urls.getPk()));
        readAllDefaultValues(sec, secretName, serviceTypeStr);
    }
}
