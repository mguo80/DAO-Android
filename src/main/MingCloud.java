import java.io.Serializable;

/**
 * Created by mguo80 on 11/1/14.
 */
public abstract class MingCloud {
    public abstract Object getObjectByKey(String key);

    public abstract boolean setObjectByKey(String key, Object obj);

    public abstract boolean removeObjectByKey(String key);
}
