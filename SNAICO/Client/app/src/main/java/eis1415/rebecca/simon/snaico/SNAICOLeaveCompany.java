package eis1415.rebecca.simon.snaico;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SNAICOLeaveCompany extends Activity {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private String httpResponseStr = "foo";
    private String gcmRegId;
    Context context;
    private String companyCodeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicoleave_company);

        context = getApplicationContext();
        gcmRegId = getRegistrationId(context);

        Button leaveCompanyBtn = (Button) findViewById(R.id.firmaVerlassenBtn);
        leaveCompanyBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                EditText companyCode = (EditText) findViewById(R.id.firmaVerlassenEditText);
                companyCodeStr = companyCode.getText().toString();

                new leaveCompany(SNAICOLeaveCompany.this).execute();

            }
        });
    }

    private class leaveCompany extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public leaveCompany(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
            if(httpResponseStr != null){
                Intent mainIntent = new Intent(SNAICOLeaveCompany.this, SNAICOLeaveCompanyFinish.class);
                SNAICOLeaveCompany.this.startActivity(mainIntent);
                SNAICOLeaveCompany.this.finish();
            }
        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String companyCode = prefs.getString("CompanyCode", "");
            String ServerAddress = prefs.getString("ServerAddress", "");

            JSONParser jParser = new JSONParser();
            String url = ServerAddress+"company/"+companyCode+"/staff/"+gcmRegId;
            JSONObject jPost = jParser.makeHttpRequest(url, "DELETE", params);

            try {
                httpResponseStr = jPost.getString("response");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;

        }
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i("GCM", "Registration not found.");
            return "";
        }

        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i("GCM", "App version changed.");
            return "";
        }
        return registrationId;
    }
    private SharedPreferences getGcmPreferences(Context context) {
        return getSharedPreferences(SNAICOSplash.class.getSimpleName(),
                Context.MODE_PRIVATE);
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
}
