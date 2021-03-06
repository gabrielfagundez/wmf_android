/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whereismyfriend;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This {@code IntentService} does the actual handling of the GCM message.
 * {@code GcmBroadcastReceiver} (a {@code WakefulBroadcastReceiver}) holds a
 * partial wake lock for this service while the service does its work. When the
 * service is finished, it calls {@code completeWakefulIntent()} to release the
 * wake lock.
 */
public class GcmIntentService extends IntentService {
    //public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GcmIntentService() {
        super("GcmIntentService");
    }
    public static final String TAG = "GCM Demo";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
        	if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // Post notification of received message.
        		
        		//Si la app esta corriendo
        		boolean corriendo=false;
        		if (Amigos.activ!=null && Amigos.isfront==1)
        			corriendo=true;
        		if (Solicitudes.activ!=null && Solicitudes.isfront==1)
        			corriendo=true;
        		if (Mapa.activ!=null && Mapa.isfront==1)
        			corriendo=true;
        		if (extras.getString("alert")!=null){
        			SharedPreferences pref = getSharedPreferences("prefs",Context.MODE_PRIVATE);
        			boolean logueado = pref.getBoolean("log_in", false);
        			String type = extras.getString("type");
        			if (!corriendo || !logueado)
        				sendNotification(extras.getString("alert"), extras.getString("badge"), type);
        			else{
        				 synchronized (this) 
        				  {
        				    startActivity(new Intent(this,DialogPush.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra("alert", extras.getString("alert")).putExtra("badge", extras.getString("badge")).putExtra("type", type));
        				  }
        			}
        			
        		}
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg, String badge, String type) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        
        int notification_id;
        PendingIntent contentIntent;
        if (type.compareTo("s")==0){
        	contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, Solicitudes.class), 0);
        	notification_id=0;
        }	
        else{
        	contentIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, Mapa.class), 0);
        	notification_id=1;
        }	
        	
        if (Integer.parseInt(badge)>1){
        	if (type.compareTo("s")==0)
        		msg=getResources().getString(R.string.push_no_leidas_1)+ " "+ badge + " "+ getResources().getString(R.string.push_no_leidas_2);
        	else
        		msg=getResources().getString(R.string.push_no_leidas_1_acc)+ " "+ badge + " "+ getResources().getString(R.string.push_no_leidas_2_acc);
        }

        
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
        .setSmallIcon(R.drawable.ic_stat_gcm)
        .setContentTitle(getResources().getString(R.string.app_name))
        .setStyle(new NotificationCompat.BigTextStyle()
        .bigText(msg))
        .setContentText(msg).setAutoCancel(true);
        
        
        AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                mBuilder.setLights(Color.CYAN, 3000, 3000);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
                mBuilder.setLights(Color.CYAN, 3000, 3000);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                mBuilder.setLights(Color.CYAN, 3000, 3000);
                break;
        }
        
        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(notification_id, mBuilder.build());
    }
}
