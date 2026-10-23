package com.blackfalcon.appkiller;

import android.content.Context;
import android.content.SharedPreferences;

final class Store {
    private Store() {}

    private static SharedPreferences p(Context c) {
        return c.getSharedPreferences("appkiller", Context.MODE_PRIVATE);
    }

    static String lang(Context c) { return p(c).getString("lang", "en"); }
    static void setLang(Context c, String v) { p(c).edit().putString("lang", v).apply(); }

    static boolean dark(Context c) { return p(c).getBoolean("dark", true); }
    static void setDark(Context c, boolean v) { p(c).edit().putBoolean("dark", v).apply(); }

    static boolean rootOk(Context c) { return p(c).getBoolean("root_ok", false); }
    static void setRootOk(Context c, boolean v) { p(c).edit().putBoolean("root_ok", v).apply(); }
}
