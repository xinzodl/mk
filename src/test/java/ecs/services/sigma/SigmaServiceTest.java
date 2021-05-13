package ecs.services.sigma;

import mk.coco.ecs.services.common.SupportedServices;
import mk.coco.ecs.services.sigma.SigmaService;
import mk.coco.ecs.utils.URLs;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class SigmaServiceTest {

    @Test
    public void shouldBeCreatedAndUpdated() throws Exception {
        SigmaService ss = mock(SigmaService.class);
        doReturn(new URLs(SupportedServices.DATAPROC, "")).when(ss).getUrls();

        assertNull(ss.getCred());
        assertNull(ss.getUrls().getOverrideServiceFullUrl());

//        assertEquals(ss.getUrls().from, ss.getUrls().serverFromOutside);
//        ss.getUrls().from = "platform.bbva.com";
//        assertEquals(ss.getUrls().from, "platform.bbva.com");

        ss.getUrls().setOverrideServiceFullUrl("default url");
        assertNotNull(ss.getUrls().getOverrideServiceFullUrl());

        assertEquals(Arrays.toString(SigmaService.DefaultStatus.values()), "[OK, WARNING, CRITICAL, STALLED]");
    }

}
