package com.lafonapps.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by chenjie on 2017/8/21.
 */

public class NotificationCenter {
    //static reference for singleton
    private static NotificationCenter defaultCenter = new NotificationCenter();

    private Map<String, CustomObservable> registedObservables;

    //default c'tor for singleton
    private NotificationCenter() {
        registedObservables = new HashMap<String, CustomObservable>();
    }

    //returning the reference
    public static NotificationCenter defaultCenter() {
        return defaultCenter;
    }

    public synchronized void addObserver(String notificationName, Observer observer) {
        CustomObservable observable = registedObservables.get(notificationName);
        if (observable == null) {
            observable = new CustomObservable();
            registedObservables.put(notificationName, observable);
        }
        observable.deleteObserver(observer); //避免重复添加
        observable.addObserver(observer);
    }

    public synchronized void removeObserver(String notificationName, Observer observer) {
        CustomObservable observable = registedObservables.get(notificationName);
        if (observable != null) {
            observable.deleteObserver(observer);
        }
    }

    public synchronized void postNotification(String notificationName) {
        postNotification(notificationName, null);
    }

    public synchronized void postNotification(String notificationName, Object object) {
        CustomObservable observable = registedObservables.get(notificationName);
        if (observable != null) {
            observable.setChanged();
            observable.notifyObservers(object);
        }
    }

    private class CustomObservable extends Observable {
        @Override
        protected void setChanged() {
            super.setChanged();
        }
    }
}
