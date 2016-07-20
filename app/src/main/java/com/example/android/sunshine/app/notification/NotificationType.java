package com.example.android.sunshine.app.notification;

import android.content.BroadcastReceiver;


public enum NotificationType {

    MORNING(NBC.MorningNotificationReceiver.class),
    EVENING(NBC.EveningNotificationReceiver.class);

    private Class<? extends BroadcastReceiver> receiverClazz;

    NotificationType(Class<? extends BroadcastReceiver> receiverClass) {
        this.receiverClazz = receiverClass;
    }

    public Class<? extends BroadcastReceiver> getReceiverClass() {
        return this.receiverClazz;
    }
}
