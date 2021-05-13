package mk.coco.ecs.services.unification;

import mk.coco.ecs.services.crm.CrmService;
import mk.coco.ecs.services.monitoredresource.MonitoredResourceService;
import mk.coco.ecs.services.mu.MuService;
import mk.coco.ecs.services.sigma.SigmaService;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * Uses a mix of two or more services
 *
 * If override URLs are defined, they are used instead of using parameters from methods to create urls
 *
 *
 * All Sigma keys & all Monitored Resources keys
 */
@Slf4j
public class UnificationService {

    // MONITORED RESOURCE & MU                 -----------------------------------------------------------------------------------------------

    /**
     * Creates a new metric, running all necessary commmands and creating monitored resources, etc.
     * All IDs must be unique, permissions are assumed
     *
     * @param mrsTypeID                 New Monitored resource type ID
     * @param monitoredResourceID       New Monitored resource ID
     * @param metricID                  New Metric ID
     * @param dataType                  Datatype of the new metric
     * @param dataUnit                  Dataunit of the new metric
     * @param metricDescription         Description of the new metric
     * @param metricSetTypeID           New metric set type ID
     * @param metricsSpecName           New metric spec name
     * @param metricSetID               New metric set ID
     * @return                          All responses from server
     * @throws Exception                If any connection is not possible
     */
    public String createMetricAndAllRequirements(
            String mrsTypeID,
            String monitoredResourceID,
            String metricID,
            String dataType,
            String dataUnit,
            String metricDescription,
            String metricSetTypeID,
            String metricsSpecName,
            String metricSetID
    ) throws Exception {
        StringBuilder responses = new StringBuilder();

        MuService ms = new MuService();
        MonitoredResourceService mrs = new MonitoredResourceService();

        responses.append("Metric creation responses:");

        // 1 - Create monitored resource type
        responses.append("Create MRType responses:");
        responses.append(mrs.createDefaultMonitorResourceType(mrsTypeID));

        // 2 - Create monitored resource
        responses.append("Create MR responses:");
        String mrsType_path = mrs.getMonitoredResourceTypePath(mrsTypeID);
        responses.append(mrs.createMonitorResourceFromTypePath(monitoredResourceID, mrsType_path));

        // 3 - Create metric
        String monitoredResourcePath = mrs.getMonitoredResourcePath(monitoredResourceID);
        responses.append(ms.createMetricFullProcess(monitoredResourcePath, metricID, dataType, dataUnit,
                metricDescription, metricSetTypeID, metricsSpecName, metricSetID));

        return  responses.toString();
    }

    // MONITORED RESOURCE & SIGMA               -----------------------------------------------------------------------------------------------

    /**
     * Creates a new alarm, running all necessary commands and creating monitored resources, alarm type, etc.
     * All IDs must be unique, permissions are assumed
     *
     * WARNING: Credentials for Sigma service and Monitored service should be the same, so you can use the monitor resource to create the alarm
     * @param monitoredResourceSecretName       Secret containing data for monitored resource service
     * @param sigmaSecretName                   Secret containing data for sigma service
     * @param sigmaAlarmId                      Sigma alarm ID
     * @param monitoredResourceTypeID           Monitored resource type ID
     * @param sigmaAlarmReceiverIDRoot          Sigma alarm receiver ID root (if several alarm receivers are created, "_x" is appended, wth "x" being from 0 to limit)
     * @param sigmaAlarmTypeID                  Sigma type ID
     * @param receiversList                     Receivers list (email@bbva.com, http)
     * @param statusFrequency                   See docs
     * @param stateChangesOnly                  Notify only status changes
     * @param enableAlarm                       Enable or disable alarm
     * @param alarmDescription                  Alarm description
     * @param alarmGroup                        Alarm's processing group
     * @return                                  All responses
     * @throws Exception                        If connection is not successful
     */
    public String createEmailAlarmAndAllRequirementsForAGivenNamespace(
            String monitoredResourceSecretName,
            String sigmaSecretName,
            String sigmaAlarmId,
            String monitoredResourceTypeID,
            String sigmaAlarmReceiverIDRoot,
            String sigmaAlarmTypeID,
            List<String> receiversList,
            int statusFrequency,
            boolean stateChangesOnly,
            boolean enableAlarm,
            String alarmDescription,
            String alarmGroup
        ) throws Exception {
            return createAlarmAndAllRequirementsForANominalNamespace(
                    monitoredResourceSecretName,
                    sigmaSecretName,
                    sigmaAlarmId,
                    monitoredResourceTypeID,
                    sigmaAlarmReceiverIDRoot,
                    sigmaAlarmTypeID,
                    receiversList,
                    statusFrequency,
                    stateChangesOnly,
                    enableAlarm,
                    alarmDescription,
                    alarmGroup,
                    SigmaService.AlarmReceiverKinds.MAIL,
                    null,
                    null,
                    true
            );
    }

    /**
     * Creates a new alarm in a new namespace, and run all necessary commands.
     * All IDs must be unique, permissions are assumed
     *
     * WARNING: Credentials for Sigma service and Monitored service should be the same, so you can use the monitor resource to create the alarm
     * @param monitoredResourceSecretName       Secret containing data for monitored resource service
     * @param sigmaSecretName                   Secret containing data for sigma service
     * @param sigmaAlarmId                      Sigma alarm ID
     * @param monitoredResourceTypeID           Monitored resource type ID
     * @param sigmaAlarmReceiverIDRoot          Sigma alarm receiver ID root (if several alarm receivers are created, "_x" is appended, wth "x" being from 0 to limit)
     * @param sigmaAlarmTypeID                  Sigma type ID
     * @param receiversList                     Receivers list (email@bbva.com, http)
     * @param statusFrequency                   See docs
     * @param stateChangesOnly                  Notify only status changes
     * @param enableAlarm                       Enable or disable alarm
     * @param alarmDescription                  Alarm description
     * @param alarmGroup                        Alarm's processing group
     * @param crmSecretName                     Secret containing data for crm service
     * @param newNamespace                      New nominal namespace (user.x000000)
     * @param sendConfirmationEmail             Send an email after creating an email alarm
     * @return                                  All responses
     * @throws Exception                        If connection is not successful
     */
    public String createAlarmAndAllRequirementsForANominalNamespace(
            String monitoredResourceSecretName,
            String sigmaSecretName,
            String sigmaAlarmId,
            String monitoredResourceTypeID,
            String sigmaAlarmReceiverIDRoot,
            String sigmaAlarmTypeID,
            List<String> receiversList,
            int statusFrequency,
            boolean stateChangesOnly,
            boolean enableAlarm,
            String alarmDescription,
            String alarmGroup,
            SigmaService.AlarmReceiverKinds alarmKind,
            String crmSecretName,
            String newNamespace,
            boolean sendConfirmationEmail
        ) throws Exception {

        MonitoredResourceService monitoredResourceService = new MonitoredResourceService(monitoredResourceSecretName);
        SigmaService sigmaService = new SigmaService(sigmaSecretName);

        StringBuilder responses = new StringBuilder();

        responses.append("Alarm creation responses:");

        // 0 - Force namespace & create new namespace
        if (newNamespace != null && crmSecretName != null) {
            monitoredResourceService.getUrls().setNamespace(newNamespace);
            sigmaService.getUrls().setNamespace(newNamespace);

            CrmService crmService = new CrmService();
            responses.append("\n## CRM response: ").append(crmService.createNominalNamespace(newNamespace));
        }

        // 1 - Create Monitored Resource Type
        responses.append("\n## Monitored Resource Type response: ");
        responses.append(monitoredResourceService.createDefaultMonitorResourceType(monitoredResourceTypeID));

        // 2 - Create alarm full process
        String mrTypePath = monitoredResourceService.getMonitoredResourceTypePath(monitoredResourceTypeID);
        responses.append(sigmaService.createAlarmFullProcess(sigmaAlarmId, sigmaAlarmReceiverIDRoot, sigmaAlarmTypeID,
                receiversList, statusFrequency, stateChangesOnly, enableAlarm, alarmDescription, alarmGroup, alarmKind,
                sendConfirmationEmail, mrTypePath));

        return responses.toString();
    }

}
