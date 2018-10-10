package org.jknetl.tiddlers.edit;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class FileNameSearchVisitor extends SimpleFileVisitor<Path> {

    private String filename;

    private Path result = null;

    public FileNameSearchVisitor(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (filename.equals(file.getFileName().toString())) {
            result =  file;
            return FileVisitResult.TERMINATE;
        }
        return super.visitFile(file, attrs);
    }

    public Path getResult() {
        return result;
    }
}
