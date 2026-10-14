package jurusanator;
import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
/**
 * Talks to the phone's calendar. Needs the READ/WRITE_CALENDAR permission (asked at runtime,
 * only when the user taps a calendar button). Everything fails soft: no permission or no
 * calendar app just returns false / empty, and the "add one by one" intent fallback still works.
 */
final class CalendarHelper {
    private CalendarHelper() {}
    static final int REQUEST = 4411;
    static boolean hasPermission(Context c) {
        return c.checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && c.checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED;
    }
    static void request(Activity a) {
        a.requestPermissions(new String[]{Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR}, REQUEST);
    }
    /** Busy (timed, non all-day) ranges {start,end} between from and to. Empty on any problem. */
    static long[][] busy(Context c, long from, long to) {
        List<long[]> list = new ArrayList<>();
        Cursor cur = null;
        try {
            Uri.Builder b = CalendarContract.Instances.CONTENT_URI.buildUpon();
            ContentUris.appendId(b, from);
            ContentUris.appendId(b, to);
            cur = c.getContentResolver().query(b.build(), new String[]{
                CalendarContract.Instances.BEGIN,
                        CalendarContract.Instances.END,
                        CalendarContract.Instances.ALL_DAY}, null, null, null);
            while (cur != null && cur.moveToNext()) {
                if (cur.getInt(2) == 0) list.add(new long[]{cur.getLong(0), cur.getLong(1)});
            }
        } catch (RuntimeException e) {
            // no permission / no provider: treat as "no information"
        } finally {
            if (cur != null) cur.close();
        }
        return list.toArray(new long[0][]);
    }
    /** First visible calendar we may write to (the primary one if possible), or -1. */
    static long writableCalendar(Context c) {
        Cursor cur = null;
        long best = -1;
        try {
            cur = c.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI,new String[]{
                CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                        CalendarContract.Calendars.IS_PRIMARY},
                    CalendarContract.Calendars.VISIBLE + "=1", null, null);
            while (cur != null && cur.moveToNext()) {
                if (cur.getInt(1) >= CalendarContract.Calendars.CAL_ACCESS_CONTRIBUTOR) {
                    if (best < 0 || cur.getInt(2) == 1) best = cur.getLong(0);
                }
            }
        } catch (RuntimeException e) {
            return -1;
        } finally {
            if (cur != null) cur.close();
        }
        return best;
    }
    /** All-day event on a yyyymmdd date, with alerts (minutes before). */
    static boolean insertAllDay(Context c, long calendarId, String title, String desc, int ymd, int[] alerts) {
        long start = Plan.utcMillis(ymd);
        return insert(c, calendarId, title, desc, start, start + 86400000L, true, alerts);
    }
    static boolean insertTimed(Context c, long calendarId, String title, String desc,
            long start, long end, int[] alerts) {
        return insert(c, calendarId, title, desc, start, end, false, alerts);
    }
    private static boolean insert(Context c, long calendarId, String title, String desc,
            long start, long end, boolean allDay, int[] alerts) {
        if (calendarId < 0) return false;
        try {
            ContentResolver cr = c.getContentResolver();
            ContentValues v = new ContentValues();
            v.put(CalendarContract.Events.CALENDAR_ID, calendarId);
            v.put(CalendarContract.Events.TITLE, title);
            v.put(CalendarContract.Events.DESCRIPTION, desc);
            v.put(CalendarContract.Events.DTSTART, start);
            v.put(CalendarContract.Events.DTEND, end);
            v.put(CalendarContract.Events.EVENT_TIMEZONE, allDay ? "UTC" : TimeZone.getDefault().getID());
            if (allDay) v.put(CalendarContract.Events.ALL_DAY, 1);
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI,v);
            if (uri == null) return false;
            long id = ContentUris.parseId(uri);
            for (int minutes : alerts) {
                ContentValues r = new ContentValues();
                r.put(CalendarContract.Reminders.EVENT_ID, id);
                r.put(CalendarContract.Reminders.MINUTES, minutes);
                r.put(CalendarContract.Reminders.METHOD,CalendarContract.Reminders.METHOD_ALERT);
                cr.insert(CalendarContract.Reminders.CONTENT_URI,r);
            }
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
    /** Fallback without permission: opens the calendar app with the event pre-filled. */
    static Intent insertIntent(String title, String desc, long start, long end, boolean allDay) {
        Intent i = new Intent(Intent.ACTION_INSERT);
        i.setData(CalendarContract.Events.CONTENT_URI);
        i.putExtra(CalendarContract.Events.TITLE, title);
        i.putExtra(CalendarContract.Events.DESCRIPTION, desc);
        i.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
        i.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        i.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allDay);
        return i;
    }
}


