package bf.memoire;
import android.app.*;
import android.content.*;
import android.os.*;
import org.json.*;
import java.io.*;

public class ReminderReceiver extends BroadcastReceiver {
    static PendingIntent pending(Context c,String id,String title) {
        Intent i=new Intent(c,ReminderReceiver.class).setAction("bf.memoire.REMIND").setData(android.net.Uri.parse("memoire://reminder/"+id)).putExtra("id",id).putExtra("title",title);
        return PendingIntent.getBroadcast(c,0,i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
    }
    static void schedule(Context c,String id,String title,long at){
        AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        if(at>System.currentTimeMillis())a.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,pending(c,id,title));
        else a.cancel(pending(c,id,title));
    }
    @Override public void onReceive(Context c,Intent intent){
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            try{JSONObject db=new LocalStore(c).load();JSONArray ns=db.getJSONArray("notes");for(int i=0;i<ns.length();i++){JSONObject n=ns.getJSONObject(i);if(!n.optBoolean("deleted"))schedule(c,n.optString("id"),n.optString("title"),n.optLong("reminder"));}}catch(Exception ignored){}return;
        }
        try {
            String id=intent.getStringExtra("id");JSONObject db=new LocalStore(c).load();JSONArray ns=db.getJSONArray("notes");JSONObject found=null;for(int j=0;j<ns.length();j++)if(ns.getJSONObject(j).optString("id").equals(id))found=ns.getJSONObject(j);
            if(found==null||found.optBoolean("deleted")||found.optLong("reminder")==0)return;
            NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel("reminders","Rappels de fiches",NotificationManager.IMPORTANCE_DEFAULT));
            Intent open=new Intent(c,MainActivity.class).putExtra("openNote",id);
            PendingIntent tap=PendingIntent.getActivity(c,id.hashCode(),open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);
            Notification n=new Notification.Builder(c,"reminders").setSmallIcon(android.R.drawable.ic_popup_reminder).setContentTitle("Mémoire — rappel").setContentText(found.optString("title")).setContentIntent(tap).setAutoCancel(true).build();nm.notify(id.hashCode(),n);
        }catch(Exception ignored){}
    }
}
