package com.chen.library.application;

import android.support.multidex.MultiDexApplication;

import com.blankj.utilcode.util.Utils;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Administrator on 2017/3/13.
 */

public class BaseApplication extends MultiDexApplication {
    private static BaseApplication instance;
    private static EventBus eventBus;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Utils.init(this);
//        CrashHandler.getInstance().init(this);
    }

    public static BaseApplication getInstance() {
        return instance;
    }


    /**
     * Convenience singleton for apps using a process-wide EventBus instance.
     */
    public static EventBus getEventBus() {
        if (eventBus == null) {
            synchronized (EventBus.class) {
                if (eventBus == null) {
                    eventBus = EventBus.builder().sendNoSubscriberEvent(false).build();
                }
            }
        }
        return eventBus;
    }

    public void clearAll() {
//        BaseUserManager.getInstance().setUser(null);
//        BasePreferenceManager.clearAll(getContext());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }


    public void reLoginByException() {
    }
}
