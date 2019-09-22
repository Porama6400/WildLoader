package net.otlg.wildloader;

import java.util.HashMap;

public class SessionStorage extends HashMap<String, Object> {

    public void set(String key, Object object) {
        remove(key);
        put(key, object);
    }

    public Object get(String key) {
        return remove(key);
    }


}
