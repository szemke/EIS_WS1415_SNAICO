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


public class SNAICOFirstStart extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicofirst_start);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar_nosymbol);

        Button firmaGruenden = (Button) findViewById(R.id.firmaGruenden);
        firmaGruenden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SNAICOFirstStart.this, SNAICONewCompany.class);
                SNAICOFirstStart.this.startActivity(mainIntent);
                SNAICOFirstStart.this.finish();
            }
        });

        Button firmaBeitreten = (Button) findViewById(R.id.firmaBeitreten);
        firmaBeitreten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SNAICOFirstStart.this, SNAICOJoinCompany.class);
                SNAICOFirstStart.this.startActivity(mainIntent);
                SNAICOFirstStart.this.finish();
            }
        });
    }

}
