# Tiddlers-export

Set of tools which allowed me to export my notes out of tiddlywiki and convert them to the markdown

## Usage

1. export your tiddlywiki into json
2. run the java app with two arguments in order to extract tiddlers from json to individual files.
    ```
    mvn package exec:java -Dexec.args="INPUT_FILE OUTPUT_DIR"
    ```
3. convert all tiddlers from tiddlywiki markup to markdown 
    ```
    ./scripts/convert-dir OUTPUT_DIR
    ```

