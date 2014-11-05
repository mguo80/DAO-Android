import android.support.v4.util.LruCache;
import java.io.Serializable;

/**
 * Created by mguo80 on 11/1/14.
 */
public class MingDao {
    protected LruCache<String, Object> cache;
    protected MingStore store;

    /**
     * Constructor
     * @param store a store instance, can be file, preference or sqlite
     */
    public MingDao(MingStore store) {
        this.cache = new LruCache<String, Object>(500);
        this.store = store;
    }

    /**
     * Add key-value pair into DAO
     * @param key
     * @param value
     * @return true if succeed, fail if fail
     */
    public boolean setIntByKey(String key, int value) {
        if (key != null) {
            if (store.setIntByKey(key, value)) {
                cache.put(key, value);
                return true;
            }
        }
        return false;
    }

    public boolean setLongByKey(String key, long value) {
        if (key != null) {
            if (store.setLongByKey(key, value)) {
                cache.put(key, value);
                return true;
            }
        }
        return false;
    }

    public boolean setFloatByKey(String key, float value) {
        if (key != null) {
            if (store.setFloatByKey(key, value)) {
                cache.put(key, value);
                return true;
            }
        }
        return false;
    }

    protected boolean setBooleanByKey(String key, boolean value) {
        if (key != null) {
            if (store.setBooleanByKey(key, value)) {
                cache.put(key, value);
                return true;
            }
        }
        return false;
    }

    public boolean setStringByKey(String key, String value) {
        if (key != null) {
            if (store.setStringByKey(key, value)) {
                cache.put(key, value);
                return true;
            }
        }
        return false;
    }

    public boolean setBytesByKey(String key, byte[] value) {
        if (key != null) {
            if (store.setBytesByKey(key, value)) {
                cache.put(key, value);
                return true;
            }
        }
        return false;
    }

    public boolean setObjectByKey(String key, Serializable obj) {
        if (key != null) {
            if (store.setObjectByKey(key, obj)) {
                cache.put(key, obj);
                return true;
            }
        }
        return false;
    }

    /**
     * Get value associated with key in DAO
     * @param key
     * @param defaultValue
     * @return actual value if exist, or default value if not exist
     */
    public int getIntByKey(String key, int defaultValue) {
        int value = defaultValue;
        if (key != null) {
            Integer obj = (Integer)cache.get(key);
            if (obj == null) {
                value = store.getIntByKey(key, defaultValue);
                cache.put(key, value);
            }
            else {
                value = obj.intValue();
            }
        }
        return value;
    }

    public long getLongByKey(String key, long defaultValue) {
        long value = defaultValue;
        if (key != null) {
            Long obj = (Long)cache.get(key);
            if (obj == null) {
                value = store.getLongByKey(key, defaultValue);
                cache.put(key, value);
            }
            else {
                value = obj.longValue();
            }
        }
        return value;
    }

    public float getFloatByKey(String key, float defaultValue) {
        float value = defaultValue;
        if (key != null) {
            Float obj = (Float)cache.get(key);
            if (obj == null) {
                value = store.getFloatByKey(key, defaultValue);
                cache.put(key, value);
            }
            else {
                value = obj.floatValue();
            }
        }
        return value;
    }

    public boolean getBooleanByKey(String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (key != null) {
            Boolean obj = (Boolean)cache.get(key);
            if (obj == null) {
                value = store.getBooleanByKey(key, defaultValue);
                cache.put(key, value);
            }
            else {
                value = obj.booleanValue();
            }
        }
        return value;
    }

    public String getStringByKey(String key) {
        String obj = null;
        if (key != null) {
            obj = (String)cache.get(key);
            if (obj == null) {
                obj = store.getStringByKey(key);
                cache.put(key, obj);
            }
        }
        return obj;
    }

    public byte[] getBytesByKey(String key) {
        byte[] obj = null;
        if (key != null) {
            obj = (byte[])cache.get(key);
            if (obj == null) {
                obj = store.getBytesByKey(key);
                cache.put(key, obj);
            }
        }
        return obj;
    }

    public Object getObjectByKey(String key) {
        Object obj = null;
        if (key != null) {
            obj = cache.get(key);
            if (obj == null) {
                obj = store.getObjectByKey(key);
                cache.put(key, obj);
            }
        }
        return obj;
    }

    /**
     * Remove key-value pair from DAO
     * @param key
     * @return true if succeed, false if fail
     */
    public boolean removeObjectByKey(String key) {
        if (key != null) {
            if (store.removeObjectByKey(key)) {
                cache.remove(key);
                return true;
            }
        }
        return false;
    }
}
