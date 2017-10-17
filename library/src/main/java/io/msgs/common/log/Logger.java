package io.msgs.common.log;

import android.util.Log;

/**
 * Simple logger which logs to logcat.
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class Logger {
    private boolean _enabled = true;
    private Level _level = Level.INFO;
    private String _tag = Logger.class.getName();

    public enum Level {
        VERBOSE, DEBUG, INFO, WARN, ERROR
    }

    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }

    public void setLevel(Level level) {
        _level = level;
    }

    public void setTag(String tag) {
        _tag = tag;
    }

    public String getTag() {
        return _tag;
    }

}
