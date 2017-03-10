package dong.lan.mapeye.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by 梁桂栋 on 2016/7/11.
 * Email: 760625325@qq.com
 * Github: github.com/donlan
 */
public class SPHelper {

    public static final String SP_NAME ="map_eye";
    public static SharedPreferences sharedPreferences;
    public static void init(Context context){
        if(sharedPreferences==null)
          sharedPreferences =  context.getApplicationContext().getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
    }

    public static void put(String key, String value){
        sharedPreferences.edit().putString(key,value).apply();
    }

    public static String get(String key){
        return sharedPreferences.getString(key,"");
    }

    public static void putBoolean(String key,boolean value){
        sharedPreferences.edit().putBoolean(key,value).apply();
    }

    public static boolean getBoolean(String key){
        return sharedPreferences.getBoolean(key,false);
    }
}
