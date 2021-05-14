package statesman;
import java.util.*;

import statesman.actions.*;

public class GameManager {
	private Scanner _scanner;
	private Scene _currentScene;
	private boolean[] _switches;
	private boolean _isPlaying = true;
	private boolean _shownIntro = false;
	
	public GameManager(Scanner scanner) {
		_scanner = scanner;
		// XXX: Fixed switch size of 2000
		_switches = new boolean[2000];
		Arrays.fill(_switches, false);
	}

	public boolean[] getSwitches() {
		return _switches;
	}

	public Scene getCurrentScene() {
		return _currentScene;
	}
	
	public void setCurrentScene(Scene scene) {
		_currentScene = scene;
	}

	public void startParser() {
		// The first scene is the initial scene
		_currentScene = App.Content.getScenes().get("initial");
		
		String currentKeywords = "";
		
		while (_isPlaying) {
			// FIXME: static'd
			if (!_shownIntro) {
				System.out.printf(App.Content.getMessages().get("0"));
				_shownIntro = true;
			}
			
			System.out.println();
			System.out.print("> ");
			currentKeywords = _scanner.nextLine();
			Iterator<Action> iterator = _currentScene.getActions().iterator();
			while (iterator.hasNext()) {
				Action currentAction = iterator.next();
				boolean matches = currentAction.hasKeyword(currentKeywords);
				if (matches) {
					currentAction.execute();
				}
			}
		}
	}

}
