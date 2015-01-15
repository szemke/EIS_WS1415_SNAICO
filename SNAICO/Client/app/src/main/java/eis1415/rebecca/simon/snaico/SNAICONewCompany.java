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
import java.util.Date;
import java.util.List;


public class SNAICONewCompany extends Activity {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private String httpResponseStr = "foo";
    private String companyName;
    private String name;
    private String gcmRegId;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaiconew_company);

        context = getApplicationContext();
        gcmRegId = getRegistrationId(context);

        Button confirmBtn = (Button) findViewById(R.id.firmaGruendenBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText firmaGruendenEditText = (EditText) findViewById(R.id.firmaGruendenEditText);
                EditText firmaGruendenEditTextName = (EditText) findViewById(R.id.firmaGruendenEditTextName);

                companyName = firmaGruendenEditText.getText().toString();
                name = firmaGruendenEditTextName.getText().toString();

                Log.d("getText: ", companyName);
                if(companyName != null){
                    new newCompany(SNAICONewCompany.this).execute();
                }
            }
        });
    }

    private class newCompany extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog dialog;
        private Context context;

        public newCompany(Activity activity) {
            context = activity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute() {
            this.dialog.setMessage("Neue Firma wird angelegt..");
            this.dialog.show();
        }

        protected void onPostExecute(final Boolean success) {
            if(httpResponseStr != null) {
                SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("CompanyName", name);
                editor.putString("CompanyCode", httpResponseStr);
                editor.commit();

                Intent mainIntent = new Intent(SNAICONewCompany.this, SNAICONewCompanyCode.class);
                SNAICONewCompany.this.startActivity(mainIntent);
                SNAICONewCompany.this.finish();
            }
        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();
            params.add(new BasicNameValuePair("companyName", companyName));
            params.add(new BasicNameValuePair("gcmRegId", gcmRegId));
            params.add(new BasicNameValuePair("name", name));

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/company";
            JSONObject jPost = jParser.makeHttpRequest(url, "POST", params);

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
