package mk.coco.ecs;


import mk.coco.ecs.credentials.PemEtherCredentials;
import mk.coco.ecs.services.dataproc.DataprocService;
import mk.coco.ecs.services.monitoredresource.MonitoredResourceService;
import mk.coco.ecs.services.mu.MuService;
import mk.coco.ecs.services.omega.OmegaService;
import mk.coco.ecs.services.psi.PsiService;
import mk.coco.ecs.services.rho.RhoService;
import mk.coco.ecs.services.sigma.SigmaService;
import mk.coco.ecs.services.unification.UnificationService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UsageExamples {
//    public static void main(String[] args) throws Exception {
//
//        // MR
//        MonitoredResourceService mrs = new MonitoredResourceService();
//
//        System.out.println(mrs.listMonitorResourcesTypes());
//        System.out.println("");
//        System.out.println(mrs.listMonitorResources());
//
//        mrs.getUrls().setNamespace("fake");
//        System.out.println(mrs.listMonitorResources());
//
//        String mrstypeID = "new_mrt_id3";
//        String responseMR1 = mrs.createDefaultMonitorResourceType(mrstypeID);
//        System.out.println("responseMR1 " + responseMR1);
//
//        String mrsID = "new_mr_id3";
//        String mrsType_path = mrs.getMonitoredResourceTypePath(mrstypeID);
//        String responseMR2 = mrs.createMonitorResourceFromTypePath(mrsID, mrsType_path);
//        System.out.println("responseMR2 " + responseMR2);
//
//
//        // RHO
//        RhoService rs = new RhoService();
//
//        System.out.println(rs.findSpans());
//        System.out.println(rs.findTraces());
//        System.out.println(rs.createSpans("spName", "mr", "sp", "tr", "1578474347845000000", "1578474347886730600"));
//        System.out.println(rs.loadSpan("mr", "sp"));
//        System.out.println(rs.loadSpanTrace("mr", "sp"));
//        System.out.println(rs.loadTrace("mr", "tr"));
//
//
//        // MU
//        MuService ms = new MuService();
//
//        String responseMU1 = ms.listMetrics();
//        System.out.println(responseMU1);
//
//        String responseMU2 = ms.listMetricSets();
//        System.out.println(responseMU2);
//
//        String responseMU3 = ms.listMetricSetType();
//        System.out.println(responseMU3);
//
//        String responseMU4 = ms.getMetric("qqq");
//        System.out.println(responseMU4);
//
//        MonitoredResourceService mrs = new MonitoredResourceService();
//
//        String mrstypeID = "mrt_5";
//        String responseMR1 = mrs.createDefaultMonitorResourceType(mrstypeID);
//        System.out.println("responseMR1 " + responseMR1);
//
//        String mrsID = "mr_5";
//        String mrsType_path = mrs.getMonitoredResourceTypePath(mrstypeID);
//        String responseMR2 = mrs.createMonitorResourceFromTypePath(mrsID, mrsType_path);
//        System.out.println("responseMR2 " + responseMR2);
//
//        String mu_m_id = "m_5";
//        String responseMU5 = ms.createMetric(mu_m_id, "integer", "COUNT", "description");
//        System.out.println(responseMU5);
//
//        String mu_mst_id = "mst_5";
//        String metricsSpecName = "name_5";
//        String metricsSpecPath = "//mu.work-01/ns/user.xe81235/metrics/" + mu_m_id;
//        String responseMU6 = ms.createMetricSetType(mu_mst_id, metricsSpecName, metricsSpecPath);
//        System.out.println(responseMU6);
//
//        String mu_ms_id = "set_metrica10";
//        String metricsSetTypePath = "//mu.work-01/ns/user.xe81235/metric-set-types/" + mu_mst_id;
//        String monitoredResource = "//mr.work-01/ns/user.xe81235/mrs/" + mrsID;
//        String responseMU7 = ms.createMetricSet(mu_ms_id, metricsSetTypePath, monitoredResource);
//        System.out.println(responseMU7);
//
//        long ts = 1579910400000000000L;
//        System.out.println(ms.addMetric(mu_ms_id, metricsSpecName, ts, "10"));
//
//
//        // OMEGA
//        OmegaService os = new OmegaService();
//
//        try {
//            PemEtherCredentials rs = PemEtherCredentials.fromString("iuhiuhiu", "ihgj");
//        } catch (Exception e) {
//            String mrID = "mr_omni_analytics_logs";
//            os.appendLogs(mrID, OmegaService.OmegaLogLevel.ERROR, "CABECERA", e, System.currentTimeMillis());
//        }
//
//        String mrID = "mr_omni_analytics_logs";
//        String msg = "aupa\ncelta";
//        String xx = os.appendLogs(mrID, OmegaService.OmegaLogLevel.DEBUG, msg, System.currentTimeMillis());
//        System.out.println(xx);
//
//
//        String mrID = "id222";
//        String responseOS1 = os.readLogs(mrID, "");
//        System.out.println("responseOS1 " + responseOS1);
//
//        String jobName = "jobName";
//        String level = "INFO";
//        String message = "my message";
//        long creationDate = 157856650000000000L;
//        String responseOS2 = os.appendLogs(mrID, jobName, OmegaService.OmegaLogLevel.ERROR, message, creationDate);
//        System.out.println("responseOS2 " + responseOS2);
//
//        String dynamicParams = "?level=INFO";
//        String responseOS3 = os.readLogs(mrID, dynamicParams);
//        System.out.println("responseOS3 " + responseOS3);
//
//
//        // DATAPROC
//        DataprocService ds = new DataprocService();
//
//        HashMap<String, String> map = new HashMap<>();;
//        map.put("epsilon.committer","spark.sql.sources.outputCommitterClass=org.apache.hadoop.fs.epsilon.output.committer.MapreduceDirectOutputCommitter;mapred.output.committer.class=org.apache.hadoop.fs.epsilon.output.committer.MapredDirectOutputCommitter;spark.sql.parquet.output.committer.class=org.apache.hadoop.fs.epsilon.output.committer.ParquetDirectOutputCommitter");
//
//        System.out.println(ds.setUUAA("ebgc"));
//        System.out.println(ds.createGroup("processing", "Descripción"));
//        System.out.println(ds.createGroup("sandbox", "Descripción", "pruebaupsilon@processing.ecs"));
//        System.out.println(ds.disableGroup("processing"));
//        System.out.println(ds.enableGroup("processing"));
//        System.out.println(ds.patchGroupBotName("pruebaupsilon@processing.ecs", "processing"));
//        System.out.println(ds.patchGroup("{\"description\": \"Descripción cambiada\"}", "sandbox"));
//
//        System.out.println(ds.registerJob("processing",
//        "ebgc-gl-spk-ejm-kirby-00",
//        "https://artifactory-estigia.live.es.ether.igrupobbva/artifactory/spark-global-libs-release-local/com/datio/kirby/kirby-ingestion/3.0.0/kirby-ingestion-3.0.0-jar-with-dependencies.jar",
//        "https://epsilon.work-01.ether.igrupobbva/v2/ns/user.xe81235/buckets/pruebaepsilon/files/kirby_conf:download",
//        "com.datio.kirby.Launcher"));
//        System.out.println(ds.registerJob("processing",
//        "ebgc-gl-spk-ejm-kirby-01",
//        "https://artifactory-estigia.live.es.ether.igrupobbva/artifactory/spark-global-libs-release-local/com/datio/kirby/kirby-ingestion/3.0.0/kirby-ingestion-3.0.0-jar-with-dependencies.jar",
//        "https://epsilon.work-01.ether.igrupobbva/v2/ns/user.xe81235/buckets/pruebaepsilon/files/kirby_conf:download",
//        "com.datio.kirby.Launcher",
//        map));
//        System.out.println(ds.runJob("processing", "ebgc-gl-spk-ejm-kirby-00", "[{}]"));
//
//        System.out.println(ds.getAllGroups());
//        System.out.println(ds.getAllJobs("processing"));
//        System.out.println(ds.describeGroup("processing"));
//        System.out.println(ds.describeGroup("sandbox"));
//        System.out.println(ds.describeJob("processing", "ebgc-gl-spk-ejm-kirby-00"));
//        System.out.println(ds.getAllRuns("processing", "ebgc-gl-spk-ejm-kirby-00"));
//
//        ds.setDescription("Prueba kirby");
//        System.out.println(ds.updateJob("processing",
//        "ebgc-gl-spk-ejm-kirby-00",
//        "https://artifactory-estigia.live.es.ether.igrupobbva/artifactory/spark-global-libs-release-local/com/datio/kirby/kirby-ingestion/3.0.0/kirby-ingestion-3.0.0-jar-with-dependencies.jar",
//        "https://epsilon.work-01.ether.igrupobbva/v2/ns/user.xe81235/buckets/pruebaepsilon/files/kirby_conf:download",
//        "com.datio.kirby.Launcher"));
//        System.out.println(ds.updateJob("processing",
//        "ebgc-gl-spk-ejm-kirby-01",
//        "https://artifactory-estigia.live.es.ether.igrupobbva/artifactory/spark-global-libs-release-local/com/datio/kirby/kirby-ingestion/3.0.0/kirby-ingestion-3.0.0-jar-with-dependencies.jar",
//        "https://epsilon.work-01.ether.igrupobbva/v2/ns/user.xe81235/buckets/pruebaepsilon/files/kirby_conf:download",
//        "com.datio.kirby.Launcher",
//        map));
//
//        System.out.println(ds.deleteJob("processing", "ebgc-gl-spk-ejm-kirby-00"));
//        System.out.println(ds.deleteJob("processing", "ebgc-gl-spk-ejm-kirby-01"));
//        System.out.println(ds.deleteGroup("processing"));
//        System.out.println(ds.deleteGroup("sandbox"));
//
//
//        // SIGMA
//        UnificationService us = new UnificationService();
//
//        String monitoredResourceSecretName = "monitoredresource" ;
//        String sigmaSecretName = "sigma" ;
//        String sigmaAlarmId = "test_alarm_id18" ;
//        String monitorResourceTypeID =  "test_mr_type_id18";
//        String monitorResourceID =  "test_mr_id18";
//        String sigmaAlarmReceiverID =  "test_alarm_receiver_id18";
//        String sigmaAlarmTypeID =  "test_alarm_type_id18";
//        List<String> reveiversList = Arrays.asList("alvaro.gomez.ramos.contractor@bbva.com", "pablo.lopez.gallego.contractor@bbva.com") ;
//        int statusFrequency =  0;
//        boolean stateChangesOnly =  false;
//        boolean enableAlarm =  true;
//        String alarmDescription =  "desc";
//        String alarmGroup =  "processing";
//
//        String response = us.createEmailAlarmAndAllRequirementsForAGivenNamespace(
//                monitoredResourceSecretName,
//                sigmaSecretName,
//                sigmaAlarmId,
//                monitorResourceTypeID,
//                sigmaAlarmReceiverID,
//                sigmaAlarmTypeID,
//                reveiversList,
//                statusFrequency,
//                stateChangesOnly,
//                enableAlarm,
//                alarmDescription,
//                alarmGroup
//        );
//
//        System.out.println(response);
//
//        SigmaService ss = new SigmaService();
//        ss.createAlarmReceiver("miId1", SigmaService.AlarmReceiverKinds.MAIL, "alvaro.gomez.ramos.contractor@bbva.com");
//        System.out.println(ss.getAlarm(sigmaAlarmId));
//        ss.setAlarmId(sigmaAlarmId);
//        ss.sendEmail("a ver si este llega", SigmaService.DefaultStatus.WARNING, sigmaAlarmId);
//
//
//        // PSI
//        PsiService ps = new PsiService();
//
//        System.out.println(ps.listMessage());
//        System.out.println(ps.listStore());
//        System.out.println(ps.listTopic());
//
//        System.out.println(ps.createMessage("messageId", "Creamos el message",
//        "\"Mensaje\": \"string\""));
//        System.out.println(ps.createStore("storeId", "Creamos el store",
//        "upsilon", "\"rootLocator\": \"//upsilon.work-01/ns/user.xe82462/streams/rankings\""));
//        System.out.println(ps.createTopic("topicId", "Creamos el topic"));
//
//        System.out.println(ps.listSubscription("topicId"));
//
//        System.out.println(ps.createSubscription("topicId", "subscriptionId",
//        "Creamos la subscripción", "webex",
//        "\"webHook\":\"https://api.ciscospark.com/v1/webhooks/incoming/Y2lzY29zcGFyazovL3VzL1dFQkhPT0svMjhkNmYzOGUtZjY0OS00ZmUyLTkwMGItYmExNDA2YmYzOWM1\""));
//
//        System.out.println(ps.getMessage("messageId"));
//        System.out.println(ps.getStore("storeId"));
//        System.out.println(ps.getTopic("topicId"));
//        System.out.println(ps.getSubscription("topicId", "subscriptionId"));
//
//        System.out.println(ps.publishMessageInTopic("{\"Mensaje\": \"Prueba mensaje NO Batch\"}", "topicId"));
//        System.out.println(ps.publishMessageBatchInTopic("{\"messages\": [{\"Mensaje\": \"Prueba mensaje Batch\"}]}", "topicId"));
//
//        System.out.println(ps.startSubscription("topicId", "subscriptionId"));
//        System.out.println(ps.stopSubscription("topicId", "subscriptionId"));
//        System.out.println(ps.testSubscription("topicId", "subscriptionId"));
//
//        System.out.println(ps.deleteMessage("messageId"));
//        System.out.println(ps.deleteStore("storeId"));
//        System.out.println(ps.deleteSubscription("topicId", "subscriptionId"));
//        System.out.println(ps.deleteTopic("topicId"));
//
//
//        System.out.println("----------------------- DONE -----------------------");
//
//
//    }
}
