package jurusanator;
import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.Calendar;
import java.util.List;
/**
 * Optional daily reminder (default 07:30): today's top headline about the dream university
 * plus a nudge to study. It only runs if the user turned it on. Alarms are inexact (battery
 * friendly) and are re-armed whenever the app is opened.
 */
public final class DailyReceiver extends BroadcastReceiver {
    private static final String CHANNEL = "daily";
    private static final int ALARM_ID = 7301;
    @Override
    public void onReceive(final Context context, Intent intent) {
        final PendingResult pending = goAsync();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notifyNow(context.getApplicationContext());
                } catch (RuntimeException e) {
                    // never crash from a background alarm
                } finally {
                    pending.finish();
                }
            }
        }, "daily").start();
    }
    private static void notifyNow(Context context) {
        Store store = new Store(context);
        Profile profile = store.profile();
        Univ univ = Univ.byId(profile.dreamUniv);
        if (!profile.dailyNews || univ == null || !canNotify(context)) return;
        boolean id = store.langId(true);
        L.id = id;
        String headline = null;
        try {
            List<Rss.Item> items = NewsClient.fetchBlocking(univ, id);
            if (!items.isEmpty()) headline = items.get(0).title;
        } catch (java.io.IOException | RuntimeException e) {
            // offline: send the reminder without a headline
        }
        String title = L.t("Today for " + univ.shortName, "Hari ini untuk " + univ.shortName);
        String text = headline != null ? headline
                : L.t("Open your plan: a small study session today keeps you on track.",
                "Buka rencanamu: sesi belajar singkat hari ini menjaga progresmu.");
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm == null) return;
        Notification.Builder b;
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel ch = new NotificationChannel(CHANNEL,
                    L.t("Daily news & study", "Berita & belajar harian"), NotificationManager.IMPORTANCE_DEFAULT);
            nm.createNotificationChannel(ch);
            b = new Notification.Builder(context, CHANNEL);
        } else {
            b = new Notification.Builder(context);
        }
        Intent open = new Intent(context, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pi = PendingIntent.getActivity(context, 0, open,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        b.setSmallIcon(R.drawable.ic_stat).setContentTitle(title).setContentText(text)
                .setStyle(new Notification.BigTextStyle().bigText(text))
                .setContentIntent(pi).setAutoCancel(true);
        nm.notify(1, b.build());
    }
    /** Android 13+ needs the notification permission; older versions always allow. */
    static boolean canNotify(Context c) {
        if (Build.VERSION.SDK_INT < 33) return true;
        return c.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }
    /** Turn the daily alarm on or off. Safe to call often. */
    static void schedule(Context context, boolean on) {
        try {
            AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (am == null) return;
            Intent intent = new Intent(context, DailyReceiver.class);
            PendingIntent pi = PendingIntent.getBroadcast(context, ALARM_ID, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pi);
            if (!on) return;
            Calendar first = Calendar.getInstance();
            first.set(Calendar.HOUR_OF_DAY, 7);
            first.set(Calendar.MINUTE, 30);
            first.set(Calendar.SECOND, 0);
            if (first.getTimeInMillis() < System.currentTimeMillis()) first.add(Calendar.DAY_OF_MONTH, 1);
            am.setInexactRepeating(AlarmManager.RTC_WAKEUP,first.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pi);
        } catch (RuntimeException e) {
            // alarms are a bonus feature, never fatal
        }
    }
}


