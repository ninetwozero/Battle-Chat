package com.ninetwozero.battlechat.services;

import org.apache.http.cookie.Cookie;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.ninetwozero.battlechat.BattleChat;
import com.ninetwozero.battlechat.R;
import com.ninetwozero.battlechat.activities.LoginActivity;
import com.ninetwozero.battlechat.datatypes.User;
import com.ninetwozero.battlechat.http.BattleChatClient;
import com.ninetwozero.battlechat.http.CookieFactory;
import com.ninetwozero.battlechat.http.HttpUris;
import com.ninetwozero.battlechat.http.LoginHtmlParser;
import com.ninetwozero.battlechat.misc.Keys;

public class BattlelogService extends Service {
		private static final int NOTIFICATION = R.string.service_name;
		private static final String TAG = "BattlelogSessionService";

	    private final IBinder mBinder = new LocalBinder();
	    
	    private NotificationManager mNotificationManager;
	    private SessionReloadTask mSessionReloadTask;
		private SharedPreferences mSharedPreferences;

	    @Override
	    public void onCreate() {
	        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
	    	mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    }

	    @Override
	    public int onStartCommand(Intent intent, int flags, int startId) {
    		setupBattleChatClient();
	    	load();
		    return Service.START_NOT_STICKY;	
	    }
	    
		@Override
	    public IBinder onBind(Intent intent) {
	        return mBinder;
	    }
	    
		private void load() {
			if( mSessionReloadTask == null ) {
				mSessionReloadTask = new SessionReloadTask();
				mSessionReloadTask.execute();
			}
		}
		
		private void showNotification() {
	    	 Notification notification = new NotificationCompat.Builder(BattlelogService.this)
	         .setContentTitle("You've been logged out from Battlelog")
	         .setContentText("Tap here to login")
	         .setSmallIcon(R.drawable.ic_launcher)
	         .setWhen(System.currentTimeMillis())
	         .setAutoCancel(true)
	         .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class), 0))
	         .getNotification();
	    	 
	        mNotificationManager.notify(NOTIFICATION, notification);
	    }
	    
	    public static final PendingIntent getPendingIntent(Context c, Intent data) {
	    	return PendingIntent.getService(c, 0, data, PendingIntent.FLAG_CANCEL_CURRENT);
	    }
	    
	    public static final Intent getIntent(Context c) {
	    	return new Intent(c, BattlelogService.class);
	    }
	    
	    public class SessionReloadTask extends AsyncTask<Void, Void, Boolean> {
	    	private User mUser;
	    	private Cookie mCookie;
	    	private String mChecksum;
	    	
	    	@Override
	    	protected Boolean doInBackground(Void... params) {
	    		try {
	    			Connection.Response response = Jsoup.connect(HttpUris.MAIN).execute();
	    			LoginHtmlParser parser = new LoginHtmlParser(response.parse());
	    			if( parser.isLoggedIn() ) {
		    			mUser = new User(parser.getUserId(), parser.getUsername(), User.ONLINE);
		    			mCookie = CookieFactory.build(BattleChat.COOKIE_NAME, response.cookie(BattleChat.COOKIE_NAME));
		    			mChecksum = parser.getChecksum();
		    			return true;
	    			}
	    		} catch( Exception ex ) {
	    			ex.printStackTrace();
	    		}	    			
	    		return false;
	    	}  	
	    	
	    	@Override
	    	protected void onPostExecute(Boolean hasActiveSession) {
	    		if(hasActiveSession) {
	    			BattleChat.reloadSession(mUser, mCookie, mChecksum);
	    		} else {
	    			if( mSharedPreferences.getBoolean(Keys.Settings.NOTIFY_ON_LOGOUT, true) ) {
	    				showNotification();
	    			}
	    		}
	    		stopSelf();
	    	}
	    }

	    public class LocalBinder extends Binder {
	        BattlelogService getService() {
	            return BattlelogService.this;
	        }
	    }
	    
	    private void setupBattleChatClient() {
			if( BattleChat.hasSession() ) {
				BattleChat.reloadSession(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
			}
			BattleChatClient.setCookie(BattleChat.getSession().getCookie());
		}
	}