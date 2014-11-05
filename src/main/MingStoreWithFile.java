import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;

import android.content.Context;
import android.util.Base64;

/**
 * Created by mguo80 on 11/1/14.
 */
public class MingStoreWithFile extends MingStore {
    private DiskCache cache;    //can only store byte[]

    /**
     * Constructor
     * @param appContext application context
     * @param name store/file name
     * @param cloud cloud instance
     */
    public MingStoreWithFile(Context appContext, String name, MingCloud cloud) {
        super(appContext, name, cloud);
        cache = new DiskCache(new File(appContext.getCacheDir(), name));
        if (cache != null) {
            cache.initialize();
        }
    }

    public boolean setIntByKey(String key, int value) {
        byte[] data = ByteBuffer.allocate(Integer.SIZE/8).putInt(value).array();
        return setBytesByKey(key, data);
    }

    public int getIntByKey(String key, int defaultValue) {
        byte[] data = getBytesByKey(key);
        if (data != null) {
            return ByteBuffer.wrap(data).getInt();
        }
        return defaultValue;
    }

    public boolean setLongByKey(String key, long value) {
        byte[] data = ByteBuffer.allocate(Long.SIZE/8).putLong(value).array();
        return setBytesByKey(key, data);
    }

    public long getLongByKey(String key, long defaultValue) {
        byte[] data = getBytesByKey(key);
        if (data != null) {
            return ByteBuffer.wrap(data).getLong();
        }
        return defaultValue;
    }

    public boolean setFloatByKey(String key, float value) {
        byte[] data = ByteBuffer.allocate(Float.SIZE/8).putFloat(value).array();
        return setBytesByKey(key, data);
    }

    public float getFloatByKey(String key, float defaultValue) {
        byte[] data = getBytesByKey(key);
        if (data != null) {
            return ByteBuffer.wrap(data).getFloat();
        }
        return defaultValue;
    }

    public boolean setBooleanByKey(String key, boolean value) {
        char val = value ? '1' : '0';
        byte[] data = ByteBuffer.allocate(1).putChar(val).array();
        return setBytesByKey(key, data);
    }

    public boolean getBooleanByKey(String key, boolean defaultValue) {
        byte[] data = getBytesByKey(key);
        if (data != null) {
            char value = ByteBuffer.wrap(data).getChar();
            return value == '1';
        }
        return defaultValue;
    }

    public boolean setStringByKey(String key, String value) {
        if (value != null) {
            return setBytesByKey(key, value.getBytes());
        }
        return false;
    }

    public String getStringByKey(String key) {
        byte[] data = getBytesByKey(key);
        if (data != null) {
            return new String(data);
        }
        return null;
    }

    public boolean setBytesByKey(String key, byte[] value) {
        if (value != null) {
            Cache.Entry entry = new Cache.Entry();
            if (entry != null) {
                entry.data = value;
                this.cache.put(key, entry);

                if (this.cloud != null) {
                    String base64Str = Base64.encodeToString(value, Base64.DEFAULT);
                    return this.cloud.setObjectByKey(key, base64Str);
                }
                return true;
            }
        }
        return false;
    }

    public byte[] getBytesByKey(String key) {
        byte[] data = null;
        Cache.Entry entry = this.cache.get(key);
        if (entry == null) {
            String base64Str = (String)this.cloud.getObjectByKey(key);
            if (base64Str != null) {
                data = Base64.decode(base64Str, Base64.DEFAULT);
                entry = new Cache.Entry();
                entry.data = data;
                this.cache.put(key, entry);
            }
        } else {
            data = entry.data;
        }
        return data;
    }

    public boolean setObjectByKey(String key, Serializable obj) {
        ByteArrayOutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            return setBytesByKey(key, os.toByteArray());
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public Object getObjectByKey(String key) {
        byte[] byteArray = getBytesByKey(key);
        if (byteArray != null) {
            ByteArrayInputStream is = null;
            ObjectInputStream ois = null;
            try {
                is = new ByteArrayInputStream(byteArray);
                ois = new ObjectInputStream(is);
                return ois.readObject();
            } catch (Exception ioe) {
                ioe.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (ois != null) {
                    try {
                        ois.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return null;
    }

    public boolean removeObjectByKey(String key) {
        this.cache.remove(key);
        return this.cloud.removeObjectByKey(key);
    }
}
