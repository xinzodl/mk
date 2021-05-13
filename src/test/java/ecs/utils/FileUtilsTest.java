package ecs.utils;

import mk.coco.ecs.utils.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.NoSuchFileException;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileUtilsTest {

    String doesNotExistMsg = "does not exist";
    String pathCantBeNull = "path can't be null";

    String nonExistingPath = "/this/path/does/not/exist";
    String existingPath = "this/path/does/exist/file.txt";

    String expectedExceptionWasNotThrown = "Expected exception was not thrown";

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowPathCantBeNullException() throws Exception {
        try{
            FileUtils.readFile(null, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(), pathCantBeNull);
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test(expected = NoSuchFileException.class)
    public void shouldThrowNoSuchFileException() throws Exception {
        try{
            FileUtils.readFile(nonExistingPath, StandardCharsets.UTF_8);
        } catch (NoSuchFileException e) {
            assertEquals(e.getMessage(), nonExistingPath);
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowResourceDoesNotExistException() throws Exception {
        try{
            FileUtils.getFilePathFromResource(nonExistingPath);
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains(doesNotExistMsg));
            throw e;
        }
        fail(expectedExceptionWasNotThrown);
    }

    @Test
    public void readsSuccessfully1() throws Exception {
        String path = FileUtilsTest.class.getClassLoader().getResource(existingPath).getPath();
        String content = FileUtils.readFile(path, StandardCharsets.UTF_8);
        assertEquals("some text", content);
    }

    @Test
    public void readsSuccessfully2() throws Exception {
        String str = "this is a sample string as an input stream";
        InputStream is = new ByteArrayInputStream( str.getBytes() );
        String content = FileUtils.readFromInputStream(is);
        assertEquals(str, content);
    }

    @Test
    public void readsPathSuccessfully() throws Exception {
        String content = FileUtils.getFilePathFromResource(existingPath);
        assertTrue(content.endsWith(existingPath));
    }

}