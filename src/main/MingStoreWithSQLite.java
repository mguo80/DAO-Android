import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.HashMap;

/**
 * Created by mguo80 on 11/1/14.
 */
public class MingStoreWithSQLite extends MingStore {
    // mapping of dbName/tableName to MingSQLiteOpenHelper
    // each table has its own MingSQLiteOpenHelper
    private static HashMap<String, MingSQLiteOpenHelper> dbManager = new HashMap<String, MingSQLiteOpenHelper>();

    private static MingSQLiteOpenHelper getSQLiteOpenHelper(Context ctx, String dbName, String tableName) {
        String key = dbName + "/" + tableName;
        MingSQLiteOpenHelper helper = dbManager.get(key);
        if (helper == null) {
            helper = new MingSQLiteOpenHelper(ctx, dbName, tableName);
            dbManager.put(key, helper);
        }
        return helper;
    }

    private MingSQLiteOpenHelper openHelper;

    public MingStoreWithSQLite(Context ctx, String dbName, String storeName, MingCloud cloud) {
        super(ctx, storeName, cloud);
        openHelper = MingStoreWithSQLite.getSQLiteOpenHelper(ctx, dbName, storeName);
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
        if (openHelper != null && openHelper.setBytesByKey(key, value)) {
            if (this.cloud != null) {
                String base64Str = Base64.encodeToString(value, Base64.DEFAULT);
                return this.cloud.setObjectByKey(key, base64Str);
            }
        }
        return false;
    }

    public byte[] getBytesByKey(String key) {
        byte[] data = null;
        if (openHelper != null) {
            data = openHelper.getBytesByKey(key);
            if (data == null) {
                String base64Str = (String)this.cloud.getObjectByKey(key);
                if (base64Str != null) {
                    data = Base64.decode(base64Str, Base64.DEFAULT);
                    openHelper.setBytesByKey(key, data);
                }
            }
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
        if (openHelper != null && openHelper.removeByKey(key)) {
            if (this.cloud != null) {
                return this.cloud.removeObjectByKey(key);
            }
        }
        return false;
    }

    private static class MingSQLiteOpenHelper extends SQLiteOpenHelper {
        private final static String COLUMN_ID = "_id";       //TEXT
        private final static String COLUMN_DATA = "_data";   //BINARY

        private final static int DATABASE_VERSION = 1;

        private String tableName;

        public MingSQLiteOpenHelper(Context ctx, String dbName, String tableName) {
            super(ctx, dbName, null, DATABASE_VERSION);
            this.tableName = tableName;
        }

        public void onCreate(SQLiteDatabase database) {
            String query = "CREATE TABLE IF NOT EXISTS " + this.tableName + " (" +
                            COLUMN_ID + " TEXT PRIMARY KEY NOT NULL, " +
                            COLUMN_DATA + " byte[]";
            database.execSQL(query);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion != oldVersion) {
                db.execSQL("DROP TABLE IF EXISTS " + tableName);
                onCreate(db);
            }
        }

        public boolean setBytesByKey(String key, byte[] value) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, key);
            values.put(COLUMN_DATA, value);

            SQLiteDatabase db = getWritableDatabase();
            long rtn = db.replace(tableName, null, values);
            db.close();

            return rtn != -1;
        }

        public byte[] getBytesByKey(String key) {
            byte[] data = null;
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.query(tableName, new String[]{COLUMN_DATA}, COLUMN_ID + " = ?", new String[]{key}, null, null, null);
            if (c != null) {
                c.moveToFirst();
                data = c.getBlob(0);
                c.close();
            }
            db.close();
            return data;
        }

        public boolean removeByKey(String key) {
            SQLiteDatabase db = getWritableDatabase();
            int numRow = db.delete(tableName, COLUMN_ID + " = ?", new String[]{key});
            db.close();
            return numRow != 0;
        }
    }
}
