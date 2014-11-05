import java.io.Serializable;
import android.content.Context;

/**
 * Created by mguo80 on 11/1/14.
 */
public abstract class MingStore {
    protected Context appContext;
    protected String name;      //name of the store/file
    protected MingCloud cloud;

    /**
     * Construct MingStore instance
     * @param name  name of the store, must not be null
     * @param cloud cloud object, can be null if not cloud
     */
    protected MingStore(Context ctx, String name, MingCloud cloud) {
        this.appContext = ctx;
        this.name = name;
        this.cloud = cloud;
    }

    public abstract boolean setIntByKey(String key, int value);

    public abstract boolean setLongByKey(String key, long value);

    public abstract boolean setFloatByKey(String key, float value);

    public abstract boolean setBooleanByKey(String key, boolean value);

    public abstract boolean setStringByKey(String key, String value);

    public abstract boolean setBytesByKey(String key, byte[] value);

    public abstract boolean setObjectByKey(String key, Serializable obj);

    public abstract int getIntByKey(String key, int defaultValue);

    public abstract long getLongByKey(String key, long defaultValue);

    public abstract float getFloatByKey(String key, float defaultValue);

    public abstract boolean getBooleanByKey(String key, boolean defaultValue);

    public abstract String getStringByKey(String key);

    public abstract byte[] getBytesByKey(String key);

    public abstract Object getObjectByKey(String key);

    public abstract boolean removeObjectByKey(String key);
}
