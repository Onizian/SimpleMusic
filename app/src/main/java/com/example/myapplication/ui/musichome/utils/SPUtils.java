package com.example.myapplication.ui.musichome.utils;

import android.content.Context;
import android.content.SharedPreferences;

/*
*
* sharepref工具类
*
*
* SharedPreferences是Android平台上一个轻量级的存储辅助类，
* 用来保存应用的一些常用配置，它提供了String，set，int，long，
* float，boolean六种数据类型。SharedPreferences的数据以键值对
* 的进行保存在以xml形式的文件中。在应用中通常做一些简单数据的持久化缓存。
* */
public class SPUtils {

    private static final String NAME = "config";

    public static void putBoolean(String key, boolean value, Context context) {
        /*增加boolean数据*/
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    public static boolean getBoolean(String key, boolean defValue, Context context) {
        /*查询boolean数据*/
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    public static void putString(String key, String value, Context context) {
        /*增加String数据*/
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }

    public static String getString(String key, String defValue, Context context) {
        /*增加String数据*/
        if (context != null) {
            SharedPreferences sp = context.getSharedPreferences(NAME,
                    Context.MODE_PRIVATE);
            return sp.getString(key, defValue);
        }
        return "";

    }

    public static void putInt(String key, int value, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().putInt(key, value).commit();
    }


    public static int getInt(String key, int defValue, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    public static void remove(String key, Context context) {
        SharedPreferences sp = context.getSharedPreferences(NAME,
                Context.MODE_PRIVATE);
        sp.edit().remove(key).commit();
    }

}
