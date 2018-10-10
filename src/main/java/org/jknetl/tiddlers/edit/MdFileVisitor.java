package org.jknetl.tiddlers.edit;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MdFileVisitor extends SimpleFileVisitor<Path> {

    private Path imgDir;
    private Path notesDir;

    public MdFileVisitor(Path notesDir, Path imgDir) {
        this.imgDir = imgDir;
        this.notesDir = notesDir;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        String fileName = file.getFileName().toString();

        if (".md".equals(fileName.substring(fileName.lastIndexOf(".")).toLowerCase())) {
            convertHtmlImagesToMarkdownImages(file);
            moveImages(file);
        }

        return super.visitFile(file, attrs);
    }

    private void moveImages(Path file) throws IOException {
        Pattern imagePathPattern = Pattern.compile("\\!\\[(.*?)\\]\\((.*?)\\)");
        String content = new String(Files.readAllBytes(file));
        Matcher matcher = imagePathPattern.matcher(content);

        boolean fileChanged = false;

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            Path imageElementPath = Paths.get(matcher.group(2));
            Path imageFile = imgDir.getParent().resolve(imageElementPath).normalize();

            Path localImgDir = file.resolveSibling("img");
            Path newFileLocation = localImgDir.resolve(imageFile.getFileName());

            // skip images from the internet
            if (imageElementPath.toString().startsWith("http:")) {
                continue;
            }
            // skip images which are located close to file
            if (Files.exists(file.resolveSibling(imageElementPath))) {
                continue;
            }

            if (!imageElementPath.normalize().equals(newFileLocation)) {

                String imageDescription = matcher.group(1);
                String replacement = String.format("![%s](%s)", imageDescription, file.getParent().relativize(newFileLocation).toString());
                matcher.appendReplacement(sb, replacement);
                fileChanged = true;
            }

            if (Files.exists(imageFile)) {
                // move file
                Files.createDirectories(localImgDir);
                Files.move(imageFile, newFileLocation);

                // make sure that link from the element point to the proper location
                assert Files.exists(file.resolveSibling(imageElementPath));
            } else {
                System.out.printf("Warning: File %s doesn't exists%n", imageFile.toString());
            }

        }
        matcher.appendTail(sb);
        if (fileChanged) {
            Files.write(file, sb.toString().getBytes());
        }
    }

    private void convertHtmlImagesToMarkdownImages(Path file) throws IOException {
        String content = new String(Files.readAllBytes(file));

        Pattern htmlImagePattern = Pattern.compile("\\<img.*?src=\"(.*?)\".*?\\>");

        Matcher matcher = htmlImagePattern.matcher(content);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {

            String replacement = String.format("![image](%s)", matcher.group(1));
            matcher.appendReplacement(sb, replacement);
        }

        matcher.appendTail(sb);
        String output = sb.toString();
        Files.write(file, output.getBytes());
    }
}
