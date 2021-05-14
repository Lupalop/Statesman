package statesman.actions;

public class Action {

	protected String[] _keywords;
	protected Command _command;
	
	public Action() {
		_keywords = null;
		_command = null;
	}
	
	public Action(String[] keywords, Command command) {
		_keywords = keywords;
		_command = command;
	}

	public Action(String keyword, Command command) {
		this(new String[] { keyword }, command);
	}

	public boolean hasKeyword(String keyword) {
		for (int i = 0; i < _keywords.length; i++) {
			if (keyword.toLowerCase().equals(_keywords[i])) {
				return true;
			}
		}
		return false;
	}

	public void execute() {
		_command.execute();
	}
}
