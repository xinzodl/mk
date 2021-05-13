package ecs.services.dataproc;

import mk.coco.ecs.services.common.SupportedServices;
import mk.coco.ecs.services.dataproc.DataprocService;
import mk.coco.ecs.utils.URLs;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class DataprocServiceTest {
    @Test
    public void shouldBeCreatedAndUpdated() throws Exception {

        DataprocService ss = mock(DataprocService.class);
        doReturn(new URLs(SupportedServices.DATAPROC, "")).when(ss).getUrls();

        assertNull(ss.getCred());
        assertNull(ss.getUrls().getOverrideServiceFullUrl());

//        assertEquals(ss.getUrls().from, ss.getUrls().serverFromOutside);
//        ss.getUrls().from = "platform.bbva.com";
//        assertEquals(ss.getUrls().from, "platform.bbva.com");

        ss.getUrls().setOverrideServiceFullUrl("default url");
        assertNotNull(ss.getUrls().getOverrideServiceFullUrl());

    }
}
