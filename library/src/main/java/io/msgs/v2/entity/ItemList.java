package io.msgs.v2.entity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;

/**
 * ItemList enitiy.
 * 
 */
public class ItemList<T extends AbstractEntity> extends AbstractEntity {
    private Class<T> _clazz;

    public ItemList(Class<T> clazz) {
        super();
        _clazz = clazz;
    }

    public ItemList(Class<T> clazz, JSONObject data) {
        super(data);
        _clazz = clazz;
    }

    /**
     * Get total.
     */
    public Integer getTotal() {
        return _getInteger("total");
    }

    /**
     * Set total.
     */
    public ItemList<T> setTotal(Integer total) {
        _putInteger("total", total);
        return this;
    }

    /**
     * Get count.
     */
    public Integer getCount() {
        return _getInteger("count");
    }

    /**
     * Set count.
     */
    public ItemList<T> setCount(Integer count) {
        _putInteger("count", count);
        return this;
    }

    /**
     * Get item for index.
     */
    public T get(int index) {
        JSONArray rawItems = _getArray("items");
        if (rawItems == null || index >= rawItems.length()) {
            return null;
        } else {
            try {
                return _clazz.getConstructor(JSONObject.class).newInstance(rawItems.getJSONObject(index));
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Get items.
     */
    public T[] getItems() {
        try {
            JSONArray rawItems = _getArray("items");
            if (rawItems == null) {
                return null;
            }

            @SuppressWarnings("unchecked")
            T[] result = (T[])Array.newInstance(_clazz, rawItems.length());
            for (int i = 0; i < rawItems.length(); i++) {
                result[i] = _clazz.getConstructor(JSONObject.class).newInstance(rawItems.getJSONObject(i));
            }

            return result;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Set items.
     */
    public ItemList<T> setItems(T[] items) {
        JSONArray rawItems = new JSONArray();
        for (int i = 0; i < items.length; i++) {
            rawItems.put(items[i]._data);
        }

        _putArray("items", rawItems);
        return this;
    }
}