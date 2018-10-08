package org.jknetl.tiddlers;

import com.google.common.base.CaseFormat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Utils {

    private Utils(){}

    public static void createDir(Path dir) {
        if (!Files.exists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String camelCaseToHyphens(String input) {

        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);

    }
}

