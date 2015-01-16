package eis1415.rebecca.simon.snaico;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SNAICOAcceptJob extends Activity {

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private String jobActivity;
    private String jobStaffMember;
    private String jobComment;
    private String jobAddress;
    private HashMap<String, String> companyStaff = new HashMap<String, String>();
    private String companyCode = "CompanyCode";
    Context context;
    String gcmRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicoaccept_job);

        context = getApplicationContext();
        gcmRegId = getRegistrationId(context);

        new ChkUser(SNAICOAcceptJob.this).execute();

    }

    private class getAllJobs extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public getAllJobs(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
            TextView jobAddressView = (TextView) findViewById(R.id.uebersichtAuftragAdresseTextView);
            TextView jobActivityView = (TextView) findViewById(R.id.uebersichtAuftragTaetitkeitEditText);
            TextView jobStaffView = (TextView) findViewById(R.id.uebersichtAuftragMitarbeiter);
            TextView jobCommentView = (TextView) findViewById(R.id.uebersichtAuftragKommentarTextView);

            String jobStaffMemberName = companyStaff.get(jobStaffMember);

            jobAddressView.setText(jobAddress);
            jobActivityView.setText(jobActivity);
            jobStaffView.setText(jobStaffMemberName);
            jobCommentView.setText(jobComment);

            Button jobAccept = (Button) findViewById(R.id.uebersichtAuftragBestaetigen);
            jobAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //AcceptJobMethode

                    Intent mainIntent = new Intent(SNAICOAcceptJob.this, SNAICOOverviewStaff.class);
                    SNAICOAcceptJob.this.startActivity(mainIntent);
                    SNAICOAcceptJob.this.finish();
                }
            });
        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);

            String jobCode = prefs.getString("lastPush", "");

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/company/" + companyCode + "/job/";
            JSONObject jPost = jParser.makeHttpRequest(url, "GET", params);
            JSONArray httpResponseArr = null;

            try {
                httpResponseArr = jPost.getJSONArray("response");
                for (int i = 0; i < httpResponseArr.length(); i++) {
                    Log.d("jobtest", httpResponseArr.getJSONObject(i).getString("jobCode") + "and Code" + jobCode);
                    if (httpResponseArr.getJSONObject(i).getString("jobCode").equals(jobCode)) {
                        jobActivity = httpResponseArr.getJSONObject(i).getString("jobActivity");
                        jobStaffMember = httpResponseArr.getJSONObject(i).getString("jobStaffMemberGcmRegId");
                        jobComment = httpResponseArr.getJSONObject(i).getString("jobComment");
                        jobAddress = httpResponseArr.getJSONObject(i).getString("jobAddress");
                        Log.d("jobtest", jobAddress);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

    private class getStaffMember extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public getStaffMember(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {

        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);


            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/company/" + companyCode + "/staff/";
            JSONObject jPost = jParser.makeHttpRequest(url, "GET", params);
            JSONArray httpResponseArr = null;
            try {
                httpResponseArr = jPost.getJSONArray("staff");
                for (int i = 0; i < httpResponseArr.length(); i++) {
                    companyStaff.put(httpResponseArr.getJSONObject(i).getString("gcmRegId"), httpResponseArr.getJSONObject(i).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class ChkUser extends AsyncTask<String, Void, Boolean> {

        public ChkUser(Activity activity) {
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
            new getStaffMember(SNAICOAcceptJob.this).execute();
            new getAllJobs(SNAICOAcceptJob.this).execute();
        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/user/" + gcmRegId;
            JSONObject jPost = jParser.makeHttpRequest(url, "GET", params);

            try {
                JSONArray response = jPost.getJSONArray("response");
                for (int i = 0; i < response.length(); i++) {
                    JSONObject r = (JSONObject) response.get(i);

                    if (r.has("companyCode")) {
                        companyCode = r.getString("companyCode");
                    }
                }
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