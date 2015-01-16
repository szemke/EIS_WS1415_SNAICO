package eis1415.rebecca.simon.snaico;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SNAICOOverviewStaff extends Activity implements NavigationDrawerFragmentStaff.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentStaff mNavigationDrawerFragmentStaff;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private NavigationDrawerFragmentOverview mNavigationDrawerFragmentOverview;
    private String gcmRegId;
    private Context context;
    private HashMap<String, String> companyJobs = new HashMap<String, String>();
    private ArrayList<String> jobActivity = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicooverview_staff);

        context = getApplicationContext();
        gcmRegId = getRegistrationId(context);

        mNavigationDrawerFragmentStaff = (NavigationDrawerFragmentStaff)
                getFragmentManager().findFragmentById(R.id.navigation_drawer2);

        // Set up the drawer.
        mNavigationDrawerFragmentStaff.setUp(
                R.id.navigation_drawer2,
                (DrawerLayout) findViewById(R.id.drawer_layout2));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container2, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:

                break;
            case 2:

                break;
            case 3:
                Intent mainIntent = new Intent(SNAICOOverviewStaff.this, SNAICOLeaveCompany.class);
                SNAICOOverviewStaff.this.startActivity(mainIntent);
                SNAICOOverviewStaff.this.finish();
                break;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView jobListView;
        private HashMap<String, String> companyJobs = new HashMap<String, String>();
        private ArrayList<String> jobActivity = new ArrayList<String>();
        ArrayAdapter<String> adapter;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_snaicooverview_staff, container, false);

            new getAllJobs(getActivity()).execute();

            adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, jobActivity);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((SNAICOOverviewStaff) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
        private class getAllJobs extends AsyncTask<String, Void, Boolean> {
            private Context context;

            public getAllJobs(Activity activity) {
                context = activity;
            }

            protected void onPreExecute() {
            }

            protected void onPostExecute(final Boolean success) {
                jobListView = (ListView) getActivity().findViewById(R.id.laufendeAuftraegeListView);
                jobListView.setAdapter(adapter);

                jobListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String positionActivity = (String) jobListView.getItemAtPosition(position);

                        String jobCodeStr = companyJobs.get(positionActivity);

                        SharedPreferences sharedPref = context.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("selectedJob",jobCodeStr);
                        editor.commit();

                        Intent intent = new Intent(getActivity(),SNAICODetailJob.class);
                        startActivity(intent);


                    }
                });
            }

            protected Boolean doInBackground(final String... args) {

                List params = new ArrayList();

                final SharedPreferences prefs = getActivity().getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                String companyCode = prefs.getString("companyCode", "");
                String ServerAddress = prefs.getString("ServerAddress", "");

                JSONParser jParser = new JSONParser();
                String url = ServerAddress+"company/"+companyCode+"/job/";
                JSONObject jPost = jParser.makeHttpRequest(url, "GET", params);
                JSONArray httpResponseArr = null;

                try {
                    httpResponseArr = jPost.getJSONArray("response");
                    for(int i = 0; i < httpResponseArr.length();i++){
                        companyJobs.put(httpResponseArr.getJSONObject(i).getString("jobActivity"),httpResponseArr.getJSONObject(i).getString("jobCode"));
                        jobActivity.add(httpResponseArr.getJSONObject(i).getString("jobActivity"));

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }
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
