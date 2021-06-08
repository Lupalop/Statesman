package statesman.commands;

public interface Command {

    public abstract Command createInstance(String[] arguments);

    public abstract void execute();

}
