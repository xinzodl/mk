package ecs.restcontent;

import mk.coco.ecs.restcontent.RestContent;

import java.util.Arrays;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RestContentTest {

    @Test
    public void shouldHaveGetAndSet() {
        assertEquals("[DELETE, GET, PATCH, POST, PUT]", Arrays.toString(RestContent.RequestType.values()));
    }

}
