package eis1415.rebecca.simon.snaico;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
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
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar_nosymbol);

        String companyName = ((DataStore)getApplication()).getGlobalString();

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
