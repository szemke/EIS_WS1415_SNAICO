package eis1415.rebecca.simon.snaico;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String companyCodeStr = prefs.getString("CompanyCode", "");

        EditText companyCode = (EditText) findViewById(R.id.firmaGruendenCodeEditText);
        companyCode.setText(companyCodeStr);

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
