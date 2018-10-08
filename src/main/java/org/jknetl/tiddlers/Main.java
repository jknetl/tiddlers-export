package org.jknetl.tiddlers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static Path tiddlersFile;
    private static Path outputDir;
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Please specify an input file with tiddlers and output folder");
            System.exit(1);
        }

        tiddlersFile = Paths.get(args[0]);
        outputDir = Paths.get(args[1]);

        Path markdownDir = outputDir.resolve("markdown");
        Path wikitextDir = outputDir.resolve("wikitext");
        Utils.createDir(markdownDir);
        Utils.createDir(wikitextDir);
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream inputStream = Files.newInputStream(tiddlersFile)) {
            List<Tiddler> tiddlers = mapper.readValue(inputStream, new TypeReference<List<Tiddler>>() {});
            tiddlers = filterTiddlers(tiddlers);


            if (!Files.exists(outputDir)) {
                Files.createDirectory(outputDir);
            }
            assert Files.isDirectory(outputDir);

            for (Tiddler t : tiddlers) {
               Path dir = t.isMarkdown() ? markdownDir : wikitextDir;
                processTiddler(t, dir);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    // removes all system tiddlers (e.g. how to create a tiddler and examples)
    private static List<Tiddler> filterTiddlers(List<Tiddler> tiddlers) {
        return tiddlers.stream()
                .filter(tiddler -> tiddler.getAuthor() != null)
                .collect(Collectors.toList());
    }

    private static void hasSystemTag(Tiddler tiddler) {
        List<String> tags = tiddler.getTags();
        List<String> systemTags = new ArrayList<String>(Arrays.asList(new String[]{"command", "concepts",
                "definitions", "demo", "deserializers", "dev", "done", "features", "howto", "introduction",
                "macros", "releasenote", "task", "tips", "video", "widgets"
}));
    }

    private static void processTiddler(Tiddler t, Path dir) {

        String fileName = Utils.camelCaseToHyphens(t.getTitle());
        fileName = t.isMarkdown() ? fileName.concat(".md") : fileName.concat(".wt");

        try {
            Path filePath = dir.resolve(fileName);
            if (Files.exists(filePath)) {
                System.out.printf("Error file '%s' already exists. Skipping tiddler%n", filePath);
                return;
            }
            filePath = Files.createFile(filePath);
            Files.write(filePath, t.getContent().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
