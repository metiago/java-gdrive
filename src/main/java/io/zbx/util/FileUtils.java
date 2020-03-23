package io.zbx.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileUtils {

    public static void copyFileTo(String dest, byte[] bytes) throws IOException {
        OutputStream outStream = new FileOutputStream(dest);
        outStream.write(bytes);
    }

}
