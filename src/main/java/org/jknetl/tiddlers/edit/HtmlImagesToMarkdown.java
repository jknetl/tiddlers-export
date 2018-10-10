package org.jknetl.tiddlers.edit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Organizes mess in my local images.
 */
public class HtmlImagesToMarkdown {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.out.println("Specify notes dir path");
            System.exit(1);
        }

        Path notesDir = Paths.get(args[0]);

        assert Files.isDirectory(notesDir);

        Path imgDir = notesDir.resolve("img");
        Files.walkFileTree(notesDir, new MdFileVisitor(notesDir, imgDir));



    }
}
