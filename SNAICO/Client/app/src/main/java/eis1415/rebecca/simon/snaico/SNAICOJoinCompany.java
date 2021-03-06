package eis1415.rebecca.simon.snaico;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
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

/*
* SNAICOJoinCompany der Benutzer kann einer Firma mit dem entsprechendem Firmencode beitreten
 */
public class SNAICOJoinCompany extends Activity {

    private String companyCodeStr;
    private String nameStr;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private String httpResponseStr = "foo";
    private String gcmRegId;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicojoin_company);

        context = getApplicationContext();
        gcmRegId = getRegistrationId(context);

        Button confirmJoinBtn = (Button) findViewById(R.id.firmaBeitretenBtn);
        confirmJoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText companyCode = (EditText) findViewById(R.id.firmaBeitretenEditText);
                EditText name = (EditText) findViewById(R.id.firmaBeitretenEditTextName);
                nameStr = name.getText().toString();
                companyCodeStr = companyCode.getText().toString();
                Log.d("companyCodeStr", companyCodeStr);
                new joinCompany(SNAICOJoinCompany.this).execute();
            }
        });
    }

    private class joinCompany extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public joinCompany(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
                if(httpResponseStr != null){
                    SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("CompanyName",httpResponseStr);
                    editor.putString("CompanyCode",companyCodeStr);
                    editor.commit();

                    Intent mainIntent = new Intent(SNAICOJoinCompany.this, SNAICOJoinCompanyFinish.class);
                    SNAICOJoinCompany.this.startActivity(mainIntent);
                    SNAICOJoinCompany.this.finish();
                }
        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String ServerAddress = prefs.getString("ServerAddress", "");

            JSONParser jParser = new JSONParser();
            params.add(new BasicNameValuePair("name", nameStr));

            String url = ServerAddress+"company/"+companyCodeStr+"/staff/"+gcmRegId;
            JSONObject jPost = jParser.makeHttpRequest(url, "PUT", params);

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
