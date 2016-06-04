package com.ijidou.accessory.tirepressure;

import java.util.ArrayList;
import java.util.List;

import com.ijidou.accessory.tirepress.common.ThemeListener;

import android.app.Application;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

public class TpmsApplication extends Application{
    public static final String TAG = "Tpms:TpmsApplication";
    
    public static Context gContext = null;

    public static boolean isDayTheme = false;
    public static Uri ThemeModeUri = Uri.parse("content://com.ijidou.voice.main.server/appearance_info");
    public static List<ThemeListener> lstThemeListener = new ArrayList<ThemeListener>();
    
    @Override
    public void onCreate() {
        super.onCreate();

        gContext = this.getApplicationContext();

        notifyThemeChanged();

        getContentResolver().registerContentObserver(TpmsApplication.ThemeModeUri, true, ThemeChangeObserver);
    }

    public static void addThemeListener(ThemeListener tl) {
        lstThemeListener.add(tl);
    }

    public static void removeThemeListener(ThemeListener tl) {
        lstThemeListener.remove(tl);
    }

    private ContentObserver ThemeChangeObserver = new ContentObserver(null) {
        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            notifyThemeChanged();

            for (ThemeListener call : lstThemeListener) {
                call.doChangeTheme();
            }
        }
    };

    public void notifyThemeChanged() {
        Cursor cursor = getContentResolver().query(TpmsApplication.ThemeModeUri, null, null, null, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();

            //0表示自动切换模式，1表是始终白天，2表示始终黑夜
            if (cursor.getInt(1) == 1) {
                isDayTheme = true;
            } else if (cursor.getInt(1) == 2) {
                isDayTheme = false;
            } else {
                //0表示白天，1表示黑夜
                if (cursor.getInt(2) == 0) {
                    isDayTheme = true;
                } else {
                    isDayTheme = false;
                }
            }

            cursor.close();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        getContentResolver().unregisterContentObserver(ThemeChangeObserver);

    }
}
