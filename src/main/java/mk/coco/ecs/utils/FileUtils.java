package mk.coco.ecs.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtils {

    private FileUtils() {
        // Avoid class instantiation
    }

    /**
     * Reads a file and return its content with the selected encoding.
     *
     * @param path     path of the file to read.
     * @param encoding encoding to transform the file content.
     * @return file content.
     * @throws IOException on file read failure.
     */
    public static String readFile(String path, Charset encoding) throws IOException {
        if (path == null)
            throw new IllegalArgumentException("path can't be null");
        byte[] content = Files.readAllBytes(Paths.get(path));
        return new String(content, encoding);
    }

    public static String getFilePathFromResource(String resourcePath) {
        ClassLoader classLoader = FileUtils.class.getClassLoader();
        URL resource = classLoader.getResource(resourcePath);

        if (resource != null) { return resource.getPath(); }

        throw new IllegalArgumentException(String.format("Resource '%s' does not exist", resourcePath));
    }

    public static String readFromInputStream(InputStream inputStream) throws IOException {
        //creating an InputStreamReader object
        InputStreamReader isReader = new InputStreamReader(inputStream);
        //Creating a BufferedReader object
        BufferedReader reader = new BufferedReader(isReader);
        StringBuilder sb = new StringBuilder();
        String str;
        while((str = reader.readLine())!= null){
            sb.append(str);
        }
        return sb.toString();
    }

}