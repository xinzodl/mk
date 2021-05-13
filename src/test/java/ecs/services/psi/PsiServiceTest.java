package ecs.services.psi;

import mk.coco.ecs.services.common.SupportedServices;
import mk.coco.ecs.services.psi.PsiService;
import mk.coco.ecs.utils.URLs;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class PsiServiceTest {
    @Test
    public void shouldBeCreatedAndUpdated() throws Exception {

        PsiService ps = mock(PsiService.class);
        doReturn(new URLs(SupportedServices.PSI, "")).when(ps).getUrls();

        assertNull(ps.getCred());
        assertNull(ps.getUrls().getOverrideServiceFullUrl());

//        assertEquals(ps.getUrls().from, ps.getUrls().serverFromOutside);
//        ps.getUrls().from = "platform.bbva.com";
//        assertEquals(ps.getUrls().from, "platform.bbva.com");

        ps.getUrls().setOverrideServiceFullUrl("default url");
        assertNotNull(ps.getUrls().getOverrideServiceFullUrl());
    }
}
