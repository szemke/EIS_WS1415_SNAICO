package eis1415.rebecca.simon.snaico;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class SNAICOSplash extends Activity {


    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String known = "known";
    private static final String unknown = "unknown";
    private static final String newUserSuccess = "success";
    private String httpResponseStr = "foo";
    private boolean companyLeader = false;
    private String companyCode = "CompanyCode";
    private String gcmRegId = "gcmRegId";

    String SENDER_ID = "539001017043";
    static final String TAG = "GCM Demo";
    GoogleCloudMessaging gcm;
    Context context;

    String regid;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_snicosplash);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        boolean isServerSet = prefs.getBoolean("ServerAddressIsSet", false);

        if(!isServerSet) {
            Intent mainIntent = new Intent(SNAICOSplash.this, SNAICOServerIp.class);
            SNAICOSplash.this.startActivity(mainIntent);
            SNAICOSplash.this.finish();
        }else {

            // Check GooglePlayServices
            context = getApplicationContext();

            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                regid = getRegistrationId(context);

                if (regid.isEmpty()) {
                    registerInBackground();
                }

                new ChkUser(SNAICOSplash.this).execute();


            } else {
                Log.i(TAG, "No valid Google Play Services APK found.");
                Toast.makeText(SNAICOSplash.this, "No valid Google Play Services APK found.",
                        Toast.LENGTH_SHORT).show();
            }
        }
   }



    private class ChkUser extends AsyncTask<String, Void, Boolean> {

        public ChkUser(Activity activity) {
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
            if (!companyLeader) {
                if (!gcmRegId.equals("gcmRegId")) {
                    if (!companyCode.equals("CompanyCode")) {

                        Intent mainIntent = new Intent(SNAICOSplash.this, SNAICOOverviewStaff.class);
                        SNAICOSplash.this.startActivity(mainIntent);
                        SNAICOSplash.this.finish();
                    }else {
                        Intent mainIntent = new Intent(SNAICOSplash.this, SNAICOFirstStart.class);
                        SNAICOSplash.this.startActivity(mainIntent);
                        SNAICOSplash.this.finish();
                    }
                } else {
                    new newUser(SNAICOSplash.this).execute();
                }
            } else {
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("companyLeader",companyLeader);
                editor.commit();

                Intent mainIntent = new Intent(SNAICOSplash.this, SNAICOOverview.class);
                SNAICOSplash.this.startActivity(mainIntent);
                SNAICOSplash.this.finish();
            }
       }


        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/user/"+regid;
            JSONObject jPost = jParser.makeHttpRequest(url, "GET", params);

            try {
                JSONArray response = jPost.getJSONArray("response");
                for (int i = 0; i < response.length(); i++){
                    JSONObject r = (JSONObject)response.get(i);

                    if(r.has("companyLeader")) {
                        companyLeader = r.getBoolean("companyLeader");
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putBoolean("companyLeader",companyLeader);
                        editor.commit();
                    }
                    if(r.has("companyCode")) {
                        companyCode = r.getString("companyCode");
                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("companyCode",companyCode);
                        editor.commit();
                    }
                    if(r.has("gcmRegId")) {
                        gcmRegId = r.getString("gcmRegId");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                httpResponseStr = jPost.getString("response");
                Log.d("response", httpResponseStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    private class newUser extends AsyncTask<String, Void, Boolean> {

        public newUser(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {

        }

        protected void onPostExecute(final Boolean success) {
            if (httpResponseStr.equals(newUserSuccess)) {
                Log.d("response", "newUser successful");
                Intent mainIntent = new Intent(SNAICOSplash.this, SNAICOFirstStart.class);
                SNAICOSplash.this.startActivity(mainIntent);
                SNAICOSplash.this.finish();
            }
        }


        protected Boolean doInBackground(final String... args) {

            Date date = new Date();
            List params = new ArrayList();
            params.add(new BasicNameValuePair("gcmRegId", regid));
            params.add(new BasicNameValuePair("date", date.toString()));

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/user";
            JSONObject jPost = jParser.makeHttpRequest(url, "POST", params);

            try {
                httpResponseStr = jPost.getString("response");
                Log.d("response", httpResponseStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.i(TAG, msg);
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private SharedPreferences getGcmPreferences(Context context) {
        return getSharedPreferences(SNAICOSplash.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }
}


