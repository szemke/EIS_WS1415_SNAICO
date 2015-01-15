package eis1415.rebecca.simon.snaico;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SNAICOJoinCompanyFinish extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicojoin_company_finish);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String companyName = prefs.getString("CompanyName", "");

        TextView finishText = (TextView)findViewById(R.id.firmaBeitretenFinishText);
        finishText.setText(getString(R.string.firmaBeitretenFinishTextFragment1)+companyName+getString(R.string.firmaBeitretenFinishTextFragment2));

        Button gotoOverview = (Button) findViewById(R.id.firmaBeitretenFinishBtn);
        gotoOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SNAICOJoinCompanyFinish.this, SNAICOOverviewStaff.class);
                SNAICOJoinCompanyFinish.this.startActivity(mainIntent);
                SNAICOJoinCompanyFinish.this.finish();
            }
        });
    }
}
