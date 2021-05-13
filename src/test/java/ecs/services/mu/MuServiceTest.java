package ecs.services.mu;

import mk.coco.ecs.services.common.SupportedServices;
import mk.coco.ecs.services.mu.MuService;
import mk.coco.ecs.utils.URLs;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class MuServiceTest {
    @Test
    public void shouldBeCreatedAndUpdated() throws Exception {
        MuService ss = mock(MuService.class);
        doReturn(new URLs(SupportedServices.DATAPROC, "")).when(ss).getUrls();

        assertNull(ss.getCred());
        assertNull(ss.getUrls().getOverrideServiceFullUrl());

        ss.getUrls().setOverrideServiceFullUrl("default url");
        assertNotNull(ss.getUrls().getOverrideServiceFullUrl());
    }
}
