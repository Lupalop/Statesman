package statesman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import statesman.actions.*;

public class Content {

    private static boolean _dataParsed;
    private static Path _dataPath;
    private static List<String> _data;
    private static ContentTuple _source;

    static {
        _dataParsed = false;
        _dataPath = null;
        _data = null;
        _source = null;
    }

    public static void loadData() throws IOException {
        _data = Files.readAllLines(_dataPath);
    }
    
    public static boolean tryLoadData() {
        try {
            loadData();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private static void parseData() {
        _source = new ContentTuple();
        Scene currentScene = null;
        CommandGroup currentGroup = null;

        Iterator<String> dataIterator = _data.iterator();
        try {
            while (dataIterator.hasNext()) {
                String line = dataIterator.next();

                // Comments
                if (line.isBlank() || (line.length() >= 2 && line.substring(0, 2) == "//")) {
                    continue;
                }

                String[] lineParts = line.split("\\|");
                for (int i = 0; i < lineParts.length; i++) {
                    lineParts[i] = lineParts[i].trim();
                }

                switch (lineParts[0]) {
                // Scene start marker
                case "$sb":
                    if (lineParts.length != 2) {
                        throw new MalformedResourceException();
                    }
                    String sceneName = lineParts[1];
                    // Throw on invalid keys (empty/blank/conflicting)
                    if (sceneName.isBlank() || _source.getScenes().containsKey(sceneName)) {
                        throw new MalformedResourceException();
                    }
                    currentScene = new Scene(sceneName);
                    break;
                // Scene end marker
                case "$se":
                    if (currentScene == null) {
                        throw new MalformedResourceException();
                    }
                    _source.getScenes().put(currentScene.getName(), currentScene);
                    currentScene = null;
                    break;
                // Command group start marker
                case "$cgb":
                    if (lineParts.length != 2) {
                        throw new MalformedResourceException();
                    }
                    String groupName = lineParts[1];
                    // Throw on invalid keys (empty/blank/conflicting)
                    if (groupName.isBlank() || currentScene.getGroupCommands().containsKey(groupName)) {
                        throw new MalformedResourceException();
                    }
                    currentGroup = new CommandGroup(groupName);
                    break;
                // Command group end marker
                case "$cge":
                    if (currentScene == null || currentGroup == null) {
                        throw new MalformedResourceException();
                    }
                    currentScene.getGroupCommands().put(currentGroup.getName(), currentGroup);
                    currentGroup = null;
                    break;
                // Action marker (command with keyword)
                case "a":
                    if (currentScene == null || lineParts.length != 3) {
                        throw new MalformedResourceException();
                    }
                    String[] keywords = lineParts[1].split(",");
                    String[] cArguments = lineParts[2].split(",");

                    Command cCommand = Interpreter.findCommand(currentScene, cArguments);

                    if (cCommand == null) {
                        throw new MalformedResourceException();
                    }

                    for (int i = 0; i < keywords.length; i++) {
                        currentScene.getActions().put(keywords[i], cCommand);
                    }
                    break;
                // Command marker (command inside a group)
                case "c":
                    if (currentScene == null || currentGroup == null) {
                        throw new MalformedResourceException();
                    }

                    String[] gArguments;
                    // With line numbers
                    if (lineParts.length == 3) {
                        gArguments = lineParts[2].split(",");
                        // Without line numbers
                    } else if (lineParts.length == 2) {
                        gArguments = lineParts[1].split(",");
                        // Weird argument count
                    } else {
                        throw new MalformedResourceException();
                    }

                    Command gCommand = Interpreter.findCommand(currentScene, gArguments);

                    if (gCommand == null) {
                        throw new MalformedResourceException();
                    }

                    currentGroup.getCommands().add(gCommand);
                    break;
                // Message marker
                case "m":
                    if (lineParts.length != 3) {
                        throw new MalformedResourceException();
                    }

                    String key = lineParts[1];
                    String value = lineParts[2];

                    // Throw on invalid keys (empty/blank/conflicting)
                    if (key.isBlank() || _source.getMessages().containsKey(key)) {
                        throw new MalformedResourceException();
                    }

                    _source.getMessages().put(key, value);
                    break;
                // Unknown tag
                default:
                    break;
                }
            }
        } catch (Exception e) {
            if (App.debugMode) {
                e.printStackTrace();
            }
            _source = null;
        }
        _dataParsed = true;
    }
    
    public static ContentTuple getSource() {
        if (!_dataParsed) {
            parseData();
        }
        return _source;
    }

    public static Path getDataPath() {
        return _dataPath;
    }

    public static void setDataPath(String location) {
        _dataPath = Paths.get(location);
        _dataParsed = false;
        _data = null;
        _source = null;
    }

    public static List<String> getData() {
        return _data;
    }

}
