package statesman.commands;

public interface Command {

    public static final String ID = "";

    public abstract Command createInstance(String[] arguments);

    public abstract void execute();

}
