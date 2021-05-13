package mk.coco.ecs.utils;

import mk.coco.ecs.credentials.MySecrets;
import mk.coco.ecs.services.common.SupportedServices;

import lombok.Getter;
import lombok.Setter;

/**
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 */
public class URLs {

    private MySecrets sec = new MySecrets();
    private String serv;

    // Constant parameters - shall not be changed in code
    @Getter @Setter private final String crt;
    @Getter @Setter private final String pk;
    @Getter @Setter private final String secretName;

    // Parameters that once set, shall not be modified
    @Getter private final String dot = ".";
    @Getter private final String url = "url";
    @Getter private final String ecs = "ecs";
    @Getter private final String colon = ":";
    @Getter private final String slash = "/";
    @Getter private final String https = "https://";
    @Getter private final String dotUrlDot = dot + url + dot ;
    @Getter @Setter final String serviceUrlName;
    @Getter @Setter final String ns;

    // Parameters which MUST be modified in code
    @Getter @Setter private String serviceOption = "";
    @Getter @Setter private String optionName = "";
    @Getter @Setter private String action = "";

    // Parameters which can be modified in code
    @Getter @Setter private String from;
    @Getter @Setter private String zone;
    @Getter @Setter private String version;
    @Getter @Setter private String namespace;
    @Getter @Setter private String mrs;

    // Urls used to override "dynamic" construction
    @Getter @Setter private String overrideServiceBaseAddressUrl;   // https://sigma.work-01.platform.bbva.com/
    @Getter @Setter private String overrideServiceVersionYNsURL;
    @Getter @Setter private String overrideNsServiceUrl;            // https://sigma.work-01.platform.bbva.com/v0/ns/user.xe81235/
    @Getter @Setter private String overrideCommonServiceUrl;        // https://sigma.work-01.platform.bbva.com/v0/ns/user.xe81235/alarms/
    @Getter @Setter private String overrideActionServiceUrl;        // https://sigma.work-01.platform.bbva.com/v0/ns/user.xe81235/alarms/id_alarm:setStatus
    @Getter @Setter private String overrideServiceFullUrl;          // https://any-url Will override all, if this is set, it will be used over any other

    public URLs(SupportedServices service, String secretName) {
        this.serv = service.toString().toLowerCase();
        this.secretName = secretName;

        crt = ecs + dot + "crt";         // cert
        pk = ecs + dot + "pk";           // key

        ns = sec.readValueFromSecretWithKey(secretName,ecs + dot + url + dot + "ns");                           // "/ns"
        from = sec.readValueFromSecretWithKey(secretName,ecs + dot + url + dot + "typeofaccess");               // serverFromOutside
        zone = sec.readValueFromSecretWithKey(secretName,ecs + dot + url + dot + "zone");                       // "work-01"
        namespace = sec.readValueFromSecretWithKey(secretName,ecs + dot + url + dot + "namespace");             // "user.xe81235"
        mrs = sec.readValueFromSecretWithKey(secretName,ecs + dot + url + dot + "mrs");                         // "/mrs"
        serviceUrlName = sec.readValueFromSecretWithKey(secretName,serv + dot + url + dot + "serviceUrlName");  // "sigma"
        version = sec.readValueFromSecretWithKey(secretName,serv + dot + url + dot + "version");                // "v0"
    }

    public void setDefaultUrlsToNull() {
        overrideServiceBaseAddressUrl = null;
        overrideServiceVersionYNsURL = null;
        overrideCommonServiceUrl = null;
        overrideNsServiceUrl = null;
        overrideActionServiceUrl = null;
        overrideServiceFullUrl = null;
    }

    // Set default urls from secrets

    public void setOverrideServiceAddressUrlFromSecrets(String overrideServiceAddressUrl) {
        this.overrideServiceBaseAddressUrl = sec.readValueFromSecretWithKey(secretName,overrideServiceAddressUrl);
    }

    public void setOverrideActionServiceUrlSecrets(String overrideActionServiceUrl) {
        this.overrideActionServiceUrl = sec.readValueFromSecretWithKey(secretName,overrideActionServiceUrl);
    }

    public void setOverrideNsServiceUrlSecrets(String overrideNsServiceUrl) {
        this.overrideNsServiceUrl = sec.readValueFromSecretWithKey(secretName,overrideNsServiceUrl);
    }

    public void setOverrideServiceFullUrlSecrets(String overrideServiceFullUrl) {
        this.overrideServiceFullUrl = sec.readValueFromSecretWithKey(secretName,overrideServiceFullUrl);
    }

    public void setDefaultCommonUrlFromSecrets(String defaultCommonAlarmUrl) {
        this.overrideCommonServiceUrl = sec.readValueFromSecretWithKey(secretName,defaultCommonAlarmUrl);
    }

    // Get Urls "dynamically"

    public String buildServiceBaseAddress(){return buildServiceBaseAddress("");}            // https://sigma.work-01.platform.bbva.com/ + append
    public String buildServiceBaseAddress(String append) {
        if (overrideServiceFullUrl != null) {return overrideServiceFullUrl;}
        return (overrideServiceBaseAddressUrl == null)
        ? https + serviceUrlName + dot + zone + dot + from + slash + append
        : overrideServiceBaseAddressUrl;
    }
    public String buildVersionYNsServiceURL(){return buildVersionYNsServiceURL("");}        // https://sigma.work-01.platform.bbva.com/v1/ns/ + append
    public String buildVersionYNsServiceURL(String append) {
        if (overrideServiceFullUrl != null) {return overrideServiceFullUrl;}
        return (overrideServiceVersionYNsURL == null)
                ? buildServiceBaseAddress() + version + ns + slash + append
                : overrideServiceVersionYNsURL;
    }
    public String buildNsServiceURL(){return buildNsServiceURL("");}                        // https://sigma.work-01.platform.bbva.com/v0/ns/user.xe81235/ + append
    public String buildNsServiceURL(String append) {
        if (overrideServiceFullUrl != null) {return overrideServiceFullUrl;}
        return (overrideNsServiceUrl == null)
            ? buildServiceBaseAddress() + version + ns + slash + namespace + append
            : overrideNsServiceUrl;
    }
    public String buildCommonServiceURL(){return buildCommonServiceURL("");}                // https://sigma.work-01.platform.bbva.com/v0/ns/user.xe81235/alarms + append
    public String buildCommonServiceURL(String append) {
        if (overrideServiceFullUrl != null) {return overrideServiceFullUrl;}
        return (overrideCommonServiceUrl == null)
            ? buildNsServiceURL() + slash + serviceOption + append
            : overrideCommonServiceUrl;
    }
    public String buildActionDefaultURL(){return buildActionDefaultURL("");}                // https://sigma.work-01.platform.bbva.com/v0/ns/user.xe81235/alarms/id_alarm:setStatus + append
    public String buildActionDefaultURL(String append) {
        if (overrideServiceFullUrl != null) {return overrideServiceFullUrl;}
        return (overrideActionServiceUrl == null)
        ? buildCommonServiceURL() + slash + optionName + colon + action + append
        : overrideActionServiceUrl;
    }

}
