package com.example.android.sunshine.app.notification;

import android.content.BroadcastReceiver;


public enum NotificationType {

    MORNING(NBC.MorningNotificationReceiver.class, NBC.MorningPreNotificationReceiver.class),
    EVENING(NBC.EveningNotificationReceiver.class, NBC.EveningPreNotificationReceiver.class);

    private final Class<? extends BroadcastReceiver> notificationReceiverClazz;
    private final Class<? extends BroadcastReceiver> updateReceiverClazz;

    NotificationType(Class<? extends BroadcastReceiver> notificationReceiverClass,
                     Class<? extends BroadcastReceiver> updateReceiverClass) {
        this.notificationReceiverClazz = notificationReceiverClass;
        this.updateReceiverClazz = updateReceiverClass;
    }

    public Class<? extends BroadcastReceiver> getNotificationReceiverClass() {
        return this.notificationReceiverClazz;
    }

    public Class<? extends BroadcastReceiver> getUpdateReceiverClass() {
        return this.updateReceiverClazz;
    }
}
