import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by mguo80 on 11/1/14.
 */
public class MingStoreWithPreference extends MingStore {
    protected SharedPreferences sp;
    protected SharedPreferences.Editor spEdit;

    /**
     * Constructor
     * @param appContext application context
     * @param name  preference name
     * @param cloud cloud store
     */
    public MingStoreWithPreference(Context appContext, String name, MingCloud cloud) {
        super(appContext, name, cloud);
        this.sp = appContext.getSharedPreferences(name, appContext.MODE_PRIVATE);
        this.spEdit = this.sp.edit();
    }

    public boolean setIntByKey(String key, int value) {
        this.spEdit.putInt(key, value);
        if (this.spEdit.commit()) {
            if (this.cloud != null) {
                return this.cloud.setObjectByKey(key, value);
            }
        }
        return false;
    }

    public int getIntByKey(String key, int defaultValue) {
        int value = defaultValue;
        if (!this.sp.contains(key)) {
            Integer intObj = (Integer)this.cloud.getObjectByKey(key);
            if (intObj != null) {
                value = intObj.intValue();
                this.spEdit.putInt(key, value);
                this.spEdit.commit();
            }
        }
        return this.sp.getInt(key, value);
    }

    public boolean setLongByKey(String key, long value) {
        this.spEdit.putLong(key, value);
        if (this.spEdit.commit()) {
            if (this.cloud != null) {
                return this.cloud.setObjectByKey(key, value);
            }
        }
        return false;
    }

    public long getLongByKey(String key, long defaultValue) {
        long value = defaultValue;
        if (!this.sp.contains(key)) {
            Long longObj = (Long)this.cloud.getObjectByKey(key);
            if (longObj != null) {
                value = longObj.longValue();
                this.spEdit.putLong(key, value);
                this.spEdit.commit();
            }
        }
        return this.sp.getLong(key, value);
    }

    public boolean setFloatByKey(String key, float value) {
        this.spEdit.putFloat(key, value);
        if (this.spEdit.commit()) {
            if (this.cloud != null) {
                return this.cloud.setObjectByKey(key, value);
            }
        }
        return false;
    }

    public float getFloatByKey(String key, float defaultValue) {
        float value = defaultValue;
        if (!this.sp.contains(key)) {
            Float floatObj = (Float)this.cloud.getObjectByKey(key);
            if (floatObj != null) {
                value = floatObj.floatValue();
                this.spEdit.putFloat(key, value);
                this.spEdit.commit();
            }
        }
        return this.sp.getFloat(key, value);
    }

    public boolean setBooleanByKey(String key, boolean value) {
        this.spEdit.putBoolean(key, value);
        if (this.spEdit.commit()) {
            if (this.cloud != null) {
                return this.cloud.setObjectByKey(key, value);
            }
        }
        return false;
    }

    public boolean getBooleanByKey(String key, boolean defaultValue) {
        boolean value = defaultValue;
        if (!this.sp.contains(key)) {
            value = (Boolean)this.cloud.getObjectByKey(key);
            this.spEdit.putBoolean(key, value);
            this.spEdit.commit();
        }
        return this.sp.getBoolean(key, value);
    }

    public boolean setStringByKey(String key, String value) {
        this.spEdit.putString(key, value);
        if (this.spEdit.commit()) {
            if (this.cloud != null) {
                return this.cloud.setObjectByKey(key, value);
            }
        }
        return false;
    }

    public String getStringByKey(String key) {
        String value = null;
        if (!this.sp.contains(key)) {
            String strObj = (String)this.cloud.getObjectByKey(key);
            if (strObj != null) {
                value = strObj;
                this.spEdit.putString(key, value);
                this.spEdit.commit();
            }
        }
        return this.sp.getString(key, value);
    }

    public boolean setBytesByKey(String key, byte[] value) {
        String base64Str = Base64.encodeToString(value, Base64.DEFAULT);
        return setStringByKey(key, base64Str);
    }

    public byte[] getBytesByKey(String key) {
        String str = getStringByKey(key);
        if (str != null) {
            return Base64.decode(str, Base64.DEFAULT);
        }
        return null;
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
        if (this.sp.contains(key)) {
            this.spEdit.remove(key);
            if (this.spEdit.commit()) {
                return this.cloud.removeObjectByKey(key);
            }
        }
        return false;
    }
}
