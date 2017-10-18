package io.msgs.v2.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * JSON entity base class.
 */
public class AbstractEntity {
    protected JSONObject _data;

    /**
     * Constructor.
     */
    public AbstractEntity() {
        _data = new JSONObject();
    }

    /**
     * Constructor.
     * 
     * @param data
     */
    public AbstractEntity(JSONObject data) {
        _data = data;
    }

    /**
     * Returns the boolean value for the given key.
     * 
     * @param key Key.
     * 
     * @return String value.
     */
    protected Boolean _getBoolean(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                return _data.getBoolean(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store boolean value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putBoolean(String key, Boolean value) {
        try {
            _data.put(key, value);
        } catch (JSONException e) {
        }
    }

    /**
     * Returns the integer value for the given key.
     * 
     * @param key Key.
     * 
     * @return String value.
     */
    protected Integer _getInteger(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                return _data.getInt(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store integer value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putInteger(String key, Integer value) {
        try {
            _data.put(key, value);
        } catch (JSONException e) {
        }
    }

    /**
     * Returns the long value for the given key.
     * 
     * @param key Key.
     * 
     * @return String value.
     */
    protected Long _getLong(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                return _data.getLong(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store long value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putLong(String key, Long value) {
        try {
            _data.put(key, value);
        } catch (JSONException e) {
        }
    }

    /**
     * Returns the double value for the given key.
     * 
     * @param key Key.
     * 
     * @return String value.
     */
    protected Double _getDouble(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                return _data.getDouble(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store double value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putDouble(String key, Double value) {
        try {
            _data.put(key, value);
        } catch (JSONException e) {
        }
    }

    /**
     * Returns the string value for the given key.
     * 
     * @param key Key.
     * 
     * @return String value.
     */
    protected String _getString(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                return _data.getString(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store string value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putString(String key, String value) {
        try {
            _data.put(key, value);
        } catch (JSONException e) {
        }
    }

    /**
     * Returns the object value for the given key.
     * 
     * @param key Key.
     * 
     * @return String value.
     */
    protected JSONObject _getObject(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                return _data.getJSONObject(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store object value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putObject(String key, JSONObject value) {
        try {
            _data.put(key, value);
        } catch (JSONException e) {
        }
    }

    /**
     * Returns the object array value for the given key.
     * 
     * @param key Key.
     * 
     * @return String value.
     */
    protected JSONArray _getArray(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                return _data.getJSONArray(key);
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store object array value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putArray(String key, JSONArray value) {
        try {
            _data.put(key, value);
        } catch (JSONException e) {
        }
    }

    /**
     * Returns the string array value for the given key.
     * 
     * @param key Key.
     * 
     * @return String array value.
     */
    protected String[] _getStringArray(String key) {
        try {
            if (!_data.has(key) || _data.isNull(key)) {
                return null;
            } else {
                JSONArray items = _data.getJSONArray(key);

                String[] result = new String[items.length()];
                for (int i = 0; i < items.length(); i++) {
                    result[i] = items.getString(i);
                }

                return result;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Store string array value for the given key.
     * 
     * @param key Key.
     * @param value Value.
     */
    protected void _putStringArray(String key, String[] value) {
        try {
            if (value == null) {
                _data.put(key, null);
            } else {
                _data.put(key, new JSONArray(Arrays.asList(value)));
            }
        } catch (JSONException e) {
        }
    }

    /**
     * Return a JSON object with the data for this entity.
     * 
     * @return Data.
     */
    public JSONObject toJSON() {
        ArrayList<String> keys = new ArrayList<String>();
        Iterator<?> iter = _data.keys();
        while (iter.hasNext()) {
            keys.add((String)iter.next());
        }

        return toJSON(keys.toArray(new String[0]));
    }

    /**
     * Return a JSON object with the data for this entity, limitted to the given keys.
     * 
     * @param keys
     * 
     * @return Data.
     */
    public JSONObject toJSON(String... keys) {
        try {
            JSONObject result = new JSONObject();
            for (String key : keys) {
                if (_data.has(key)) {
                    result.put(key, _data.get(key));
                }
            }

            return result;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * Convert to JSON string representation.
     * 
     * @return JSON string representation.
     */
    @Override
    public String toString() {
        return _data.toString();
    }
}
