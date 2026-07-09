package com.ondrecreates.notificationcenter.channel;

import com.ondrecreates.notificationcenter.notification.Notification;
import com.ondrecreates.notificationcenter.notification.NotificationChannel;

/**
 * Přidání nového kanálu = nová implementace tohoto interface registrovaná
 * jako Spring bean. NotificationService si všechny implementace posbírá
 * automaticky (List<NotificationChannelHandler>) – žádný switch/if na
 * NotificationChannel nikde v existujícím kódu.
 */
public interface NotificationChannelHandler {

    NotificationChannel channel();

    void dispatch(Notification notification);
}
