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
import android.widget.EditText;


public class SNAICONewCompanyCode extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaiconew_company_code);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar_nosymbol);

        String code = ((DataStore)getApplication()).getGlobalString();
        EditText companyCode = (EditText) findViewById(R.id.firmaGruendenCodeEditText);
        companyCode.setText(code);

        Button gotoOverview = (Button) findViewById(R.id.firmaGruendenUebersichtBtn);
        gotoOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(SNAICONewCompanyCode.this, SNAICOOverview.class);
                SNAICONewCompanyCode.this.startActivity(mainIntent);
                SNAICONewCompanyCode.this.finish();
            }
        });
    }
}
