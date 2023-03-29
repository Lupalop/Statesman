package statesman.commands;

public abstract class Command {

    public abstract Command createInstance(String[] arguments);

    public abstract void execute();

}
