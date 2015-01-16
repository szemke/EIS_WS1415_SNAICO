package eis1415.rebecca.simon.snaico;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SNAICODetailJob extends Activity {

    private String  jobActivity;
    private String jobStaffMember;
    private String jobComment;
    private String jobAddress;
    private HashMap<String, String> companyStaff = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicodetail_job);


        new getStaffMember(SNAICODetailJob.this).execute();
    }

    private class getAllJobs extends AsyncTask<String, Void, Boolean> {
        private Context context;

        public getAllJobs(Activity activity) {
            context = activity;
        }

        protected void onPreExecute() {
        }

        protected void onPostExecute(final Boolean success) {
            TextView jobAddressView = (TextView)findViewById(R.id.detailAuftragAdresseTextView);
            TextView jobActivityView = (TextView)findViewById(R.id.detailAuftragTaetitkeitEditText);
            TextView jobStaffView = (TextView)findViewById(R.id.detailAuftragMitarbeiter);
            TextView jobCommentView = (TextView)findViewById(R.id.detailAuftragKommentarTextView);

            String jobStaffMemberName = companyStaff.get(jobStaffMember);


            jobAddressView.setText(jobAddress);
            jobActivityView.setText(jobActivity);
            jobStaffView.setText(jobStaffMemberName);
            jobCommentView.setText(jobComment);

            Button jobAccept = (Button) findViewById(R.id.detailAuftragBestaetigen);
            jobAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //AcceptJobMethode
                    final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                    if(!prefs.getBoolean("CompanyLeader", false)){
                        Intent mainIntent = new Intent(SNAICODetailJob.this, SNAICOOverview.class);
                        SNAICODetailJob.this.startActivity(mainIntent);
                        SNAICODetailJob.this.finish();
                    }else {
                        Intent mainIntent = new Intent(SNAICODetailJob.this, SNAICOOverviewStaff.class);
                        SNAICODetailJob.this.startActivity(mainIntent);
                        SNAICODetailJob.this.finish();
                    }
                }
            });
        }

        protected Boolean doInBackground(final String... args) {

            List params = new ArrayList();

            final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            String companyCode = prefs.getString("companyCode", "");
            String jobCode = prefs.getString("selectedJob", "");

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/company/"+companyCode+"/job/";
            JSONObject jPost = jParser.makeHttpRequest(url, "GET", params);
            JSONArray httpResponseArr = null;

            try {
                httpResponseArr = jPost.getJSONArray("response");
                for(int i = 0; i < httpResponseArr.length();i++){
                    Log.d("jobtest", httpResponseArr.getJSONObject(i).getString("jobCode") + "and Code" + jobCode);
                    if(httpResponseArr.getJSONObject(i).getString("jobCode").equals(jobCode)){
                        jobActivity = httpResponseArr.getJSONObject(i).getString("jobActivity");
                        jobStaffMember = httpResponseArr.getJSONObject(i).getString("jobStaffMemberGcmRegId");
                        jobComment = httpResponseArr.getJSONObject(i).getString("jobComment");
                        jobAddress = httpResponseArr.getJSONObject(i).getString("jobAddress");
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
            new getAllJobs(SNAICODetailJob.this).execute();
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
                    companyStaff.put(httpResponseArr.getJSONObject(i).getString("gcmRegId"),httpResponseArr.getJSONObject(i).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
