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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;


public class SNAICONewJob extends Activity {

    //private final EditText editActivity = (EditText) findViewById(R.id.neuerAuftragTaetitkeitEditText);
    //private final EditText editComment = (EditText) findViewById(R.id.neuerAuftragKommentarEditText);
    private String address="foo";
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private String gcmRegId;
    private Context context;
    private ArrayAdapter<String> stringArrayAdapter;
    private ArrayList<String> staffNames = new ArrayList<String>();
    private HashMap<String, String> companyStaff = new HashMap<String, String>();
    private Spinner jobStaffMember;
    private String jobAddressStr;
    private String jobActivityStr;
    private String jobCommentStr;
    private String jobStaffMemberStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaiconew_job);

        context = getApplicationContext();
        gcmRegId = getRegistrationId(context);
        Button getGPS = (Button) findViewById(R.id.neuerAuftragPositionGps);
        Button confirmJobBtn = (Button) findViewById(R.id.neuerAuftragBestaetigen);

        final GPSService mGPSService = new GPSService(SNAICONewJob.this);
        mGPSService.getLocation();

        new getStaffMember(SNAICONewJob.this).execute();
        stringArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, staffNames);



        getGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editAddress = (EditText) findViewById(R.id.neuerAuftragAdresseEditText);

                if (!mGPSService.isLocationAvailable) {

                    Toast.makeText(getApplicationContext(), "Your location is not available, please try again.", Toast.LENGTH_SHORT).show();

                } else {
                    address = mGPSService.getLocationAddress();
                    editAddress.setText(address);
                }
            }
        });

        confirmJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText jobAddress = (EditText)findViewById(R.id.neuerAuftragAdresseEditText);
                EditText jobActivity = (EditText)findViewById(R.id.neuerAuftragTaetitkeitEditText);
                EditText jobComment = (EditText)findViewById(R.id.neuerAuftragKommentarEditText);

                jobAddressStr = jobAddress.getText().toString();
                jobActivityStr = jobActivity.getText().toString();
                jobStaffMemberStr = jobStaffMember.getSelectedItem().toString();
                jobCommentStr = jobComment.getText().toString();

                new newJob(SNAICONewJob.this).execute();

                Log.d("confirmBtnClicked", companyStaff.get(jobStaffMemberStr));
            }
        });
    }



    private class getStaffMember extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public getStaffMember(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
            jobStaffMember = (Spinner) findViewById(R.id.neuerAuftragMitarbeiterSpinner);
            jobStaffMember.setAdapter(stringArrayAdapter);

        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String companyCode = prefs.getString("companyCode", "");

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/company/"+companyCode+"/staff/";
            JSONObject jPost = jParser.makeHttpRequest(url, "GET", params);
            JSONArray httpResponseArr = null;
            try {
                httpResponseArr = jPost.getJSONArray("staff");
                for(int i = 0; i < httpResponseArr.length();i++){
                    companyStaff.put(httpResponseArr.getJSONObject(i).getString("name"),httpResponseArr.getJSONObject(i).getString("gcmRegId"));
                    staffNames.add(httpResponseArr.getJSONObject(i).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class newJob extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public newJob(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
            Intent mainIntent = new Intent(SNAICONewJob.this, SNAICONewJobFinish.class);
            SNAICONewJob.this.startActivity(mainIntent);
            SNAICONewJob.this.finish();
        }

        protected Boolean doInBackground(final String... args) {



            List params = new ArrayList();
            params.add(new BasicNameValuePair("gcmRegId", gcmRegId));
            params.add(new BasicNameValuePair("jobAddress", jobAddressStr));
            params.add(new BasicNameValuePair("jobActivity", jobActivityStr));
            params.add(new BasicNameValuePair("jobStaffMember", jobStaffMemberStr));
            params.add(new BasicNameValuePair("jobComment", jobCommentStr));
            params.add(new BasicNameValuePair("jobStaffMemberGcmRegId", companyStaff.get(jobStaffMemberStr)));

            final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String companyCode = prefs.getString("companyCode", "");

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/company/"+companyCode+"/job";
            JSONObject jPost = jParser.makeHttpRequest(url, "POST", params);


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
