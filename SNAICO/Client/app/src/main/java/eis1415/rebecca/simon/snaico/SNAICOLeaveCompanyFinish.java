package eis1415.rebecca.simon.snaico;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class SNAICOLeaveCompanyFinish extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicoleave_company_finish);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar_nosymbol);

        String companyName = ((DataStore)getApplication()).getGlobalString();

        TextView finishText = (TextView)findViewById(R.id.firmaVerlassenFinishText);
        finishText.setText(getString(R.string.firmaVerlassenFinishTextFragment1)+companyName+getString(R.string.firmaVerlassenFinishTextFragment2));

        Button gotoFirstStart = (Button) findViewById(R.id.firmaVerlassenFinishBtn);
        gotoFirstStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SNAICOLeaveCompanyFinish.this, SNAICOFirstStart.class);
                SNAICOLeaveCompanyFinish.this.startActivity(mainIntent);
                SNAICOLeaveCompanyFinish.this.finish();
            }
        });
    }
}
