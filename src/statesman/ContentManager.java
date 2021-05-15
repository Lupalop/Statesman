package statesman;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import statesman.actions.*;

public class ContentManager {

	private Path _dataPath;
	private List<String> _data;
	private HashMap<String, Scene> _scenes;
	private HashMap<String, String> _messages;
	
	public ContentManager() {
		_dataPath = null;
		_data = null;
		_scenes = new HashMap<String, Scene>();
		_messages = new HashMap<String, String>();
	}

	public Path getDataPath() {
		return _dataPath;
	}

	public List<String> getData() {
		return _data;
	}
	
	public HashMap<String, Scene> getScenes() {
		return _scenes;
	}
	
	public HashMap<String, String> getMessages() {
		return _messages;
	}

	public void setDataPath(String location) {
		_dataPath = Paths.get(location);
	}

	public boolean tryLoadData() {
		if (Files.exists(_dataPath)) {
			try {
				_data = Files.readAllLines(_dataPath);
			} catch (IOException e) {
				return false;
			}
			return true;
		}
		
		return false;
	}

	public boolean parseData() {
		Scene currentScene = null;
		CommandGroup currentGroup = null;
		
		Iterator<String> dataIterator = _data.iterator();
		try {
			while (dataIterator.hasNext()) {
				String line = dataIterator.next();
				
				// Comments
				if (line.isBlank() ||
				    (line.length() >= 2 && line.substring(0, 2) == "//")) {
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
					if (sceneName.isBlank() || _scenes.containsKey(sceneName)) {
						throw new MalformedResourceException();
					}
					currentScene = new Scene(sceneName);
					break;
				// Scene end marker
				case "$se":
					if (currentScene == null) {
						throw new MalformedResourceException();
					}
					_scenes.put(currentScene.getName(), currentScene);
					currentScene = null;
					break;
				// Command group start marker
				case "$cgb":
					if (lineParts.length != 2) {
						throw new MalformedResourceException();
					}
					String groupName = lineParts[1];
					// Throw on invalid keys (empty/blank/conflicting)
					if (groupName.isBlank() ||
						currentScene.getGroupCommands().containsKey(groupName)) {
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
				// Action marker
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
					
					Action action = new Action(keywords, cCommand);
					currentScene.getActions().add(action);
					break;
				// Command (in a group) marker
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
					if (key.isBlank() || _messages.containsKey(key)) {
						throw new MalformedResourceException();
					}
					
					_messages.put(key, value);
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
			return false;
		}
		
		return true;
	}
	
}
