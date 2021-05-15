package statesman.actions;

import java.util.Random;

import statesman.Content;
import statesman.Scene;

public class PrintRandomCommand extends Command {

	public static final String Id = "printr";

	private String[] _messages;
	private Random _random;

	public PrintRandomCommand() {
		_messages = null;
		_random = new Random();
	}
	
	public PrintRandomCommand(String[] messages) {
		this();
		_messages = new String[messages.length];
		for (int i = 0; i < messages.length; i++) {
			_messages[i] = Content.getMessages().getOrDefault(messages[i], messages[i]);				
		}
	}

	@Override
	public Command createInstance(Scene parent, String[] arguments) {
		if (arguments.length > 2) {
			String[] messages = new String[arguments.length - 1];
			System.arraycopy(arguments, 1, messages, 0, arguments.length - 1);
			return new PrintRandomCommand(messages);
		}
		return null;
	}

	@Override
	public Command createInstance(String[] arguments) {
		return createInstance(null, arguments);
	}

	@Override
	public void execute() {
		int i = _random.nextInt(_messages.length);
		System.out.printf(_messages[i] + "%n");
	}

}
