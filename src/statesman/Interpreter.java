package statesman;
import java.util.*;

import statesman.actions.*;

public class Interpreter {
	
	private static Scanner _scanner;
	private static Scene _currentScene;
	private static boolean[] _switches;
	private static boolean _isPlaying = true;
	private static boolean _shownIntro = false;
	private static HashMap<String, Command> _commands;

	static {
		_commands = new HashMap<String, Command>();
		// XXX: manually input IDs of new commands here!
		_commands.put(PrintCommand.Id, new PrintCommand());
		_commands.put(PrintRandomCommand.Id, new PrintRandomCommand());
		_commands.put(PrintCombineCommand.Id, new PrintCombineCommand());
		_commands.put(SceneCommand.Id, new SceneCommand());
		_commands.put(GotoCommand.Id, new GotoCommand());
		_commands.put(JumpCommand.Id, new JumpCommand());
		_commands.put(BreakCommand.Id, new BreakCommand());
		_commands.put(ConditionalJumpCommand.Id, new ConditionalJumpCommand());
		_commands.put(SetSwitchCommand.Id, new SetSwitchCommand());

		// XXX: Fixed switch size of 2000
		_switches = new boolean[2000];
		Arrays.fill(_switches, false);
	}
	
	public static Command findCommand(Scene parent, String[] arguments) {
		String commandId = arguments[0];

		Iterator<String> iterator = getCommands().keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (key.equalsIgnoreCase(commandId)) {
				return getCommands().get(key).createInstance(parent, arguments);
			}
		}
		
		return null;
	}

	public static HashMap<String, Command> getCommands() {
		return _commands;
	}

	public static boolean[] getSwitches() {
		return _switches;
	}

	public static Scene getCurrentScene() {
		return _currentScene;
	}
	
	public static Scanner getScanner() {
		return _scanner;
	}

	public static void setScanner(Scanner _scanner) {
		Interpreter._scanner = _scanner;
	}

	public static void setCurrentScene(Scene scene) {
		_currentScene = scene;
	}

	public static void startParser() {
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
			currentKeywords = getScanner().nextLine();
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
