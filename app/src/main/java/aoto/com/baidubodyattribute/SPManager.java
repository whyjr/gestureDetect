package aoto.com.baidubodyattribute;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * author:why
 * created on: 2019/7/2 9:27
 * description:
 */
public class SPManager {

    private SharedPreferences mSp;
    private static volatile SPManager manager;

    private SPManager(Context context) {
        mSp = context.getSharedPreferences(SPValueKey.SP_NAME, Context.MODE_PRIVATE);
    }

    /**
     * DCL
     *
     * @param context
     * @return
     */
    public static SPManager getInstance(Context context) {
        if (manager == null) {
            synchronized (SPManager.class) {
                if (manager == null) {
                    manager = new SPManager(context);
                }
            }
        }
        return manager;
    }


    public String getString(String key, String defaultValue) {
        return mSp.getString(key, defaultValue);
    }

    public int getInt(String key, int defaultValue) {
        return mSp.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return mSp.getBoolean(key, defaultValue);
    }

    public float getFloat(String key, float defaultValue) {
        return mSp.getFloat(key, defaultValue);
    }


    public void putString(String key, String value){
        mSp.edit().putString(key,value).apply();
    }

    public void putBoolean(String key, boolean value){
        mSp.edit().putBoolean(key,value).apply();
    }


    class SPValueKey {
        public static final String SP_NAME = "SP_NAME";
        public static final String COUNT_KEY = "COUNT_KEY";
        public static final String DATE_KEY = "DATE_KEY";
        public static final String APP_ID = "APP_ID";
        public static final String API_KEY = "API_KEY";
        public static final String SECRET_KEY = "SECRET_KEY";
        public static final String CHECK_RESULT = "CHECK_RESULT";
    }
}
