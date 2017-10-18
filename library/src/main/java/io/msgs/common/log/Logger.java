package io.msgs.common.log;

import android.util.Log;

/**
 * Simple logger which logs to logcat.
 * Created by Daniel Zolnai on 2017-10-17.
 */
public class Logger {
    private boolean _enabled = true;
    private Level _level = Level.VERBOSE;
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

    public void d(String message) {
        if (!_enabled || (_level == Level.INFO || _level == Level.WARN || _level == Level.ERROR)) {
            return;
        }
        Log.d(_tag, message);
    }

    public void i(String message) {
        if (!_enabled || (_level == Level.WARN || _level == Level.ERROR)) {
            return;
        }
        Log.i(_tag, message);
    }

    public void w(String message) {
        if (!_enabled || (_level == Level.ERROR)) {
            return;
        }
        Log.w(_tag, message);
    }

    public void w(String message, Throwable throwable) {
        if (!_enabled || (_level == Level.ERROR)) {
            return;
        }
        Log.w(_tag, message, throwable);
    }

    public void e(String message, Throwable throwable) {
        if (!_enabled) {
            return;
        }
        Log.e(_tag, message, throwable);
    }

}
