package statesman;

import java.util.HashMap;

public class ContentTuple {

    private HashMap<String, String> _messages;
    private HashMap<String, Scene> _scenes;
    
    public ContentTuple() {
        _messages = new HashMap<String, String>();
        _scenes = new HashMap<String, Scene>();
    }

    public HashMap<String, String> getMessages() {
        return _messages;
    }

    public void setMessages(HashMap<String, String> _messages) {
        this._messages = _messages;
    }

    public HashMap<String, Scene> getScenes() {
        return _scenes;
    }

    public void setScenes(HashMap<String, Scene> _scenes) {
        this._scenes = _scenes;
    }

}
