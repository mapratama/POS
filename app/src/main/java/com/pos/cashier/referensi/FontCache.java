package com.pos.cashier.referensi;

import android.content.Context;
import android.graphics.Typeface;
import java.util.Hashtable;

public class FontCache {

    private static Hashtable<String, Typeface> fontCache = new Hashtable<String, Typeface>();

    public static Typeface get(Context c, String name){
        synchronized(fontCache){
            if(!fontCache.containsKey(name)){
                try {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), String.format("%s.otf", name));
                    fontCache.put(name, t);
                } catch (Exception e) {
                    Typeface t = Typeface.createFromAsset(c.getAssets(), String.format("%s.ttf", name));
                    fontCache.put(name, t);
                }
            }
            return fontCache.get(name);
        }
    }
}