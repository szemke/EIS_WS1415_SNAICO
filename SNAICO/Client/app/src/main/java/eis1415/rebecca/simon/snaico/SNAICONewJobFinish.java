package eis1415.rebecca.simon.snaico;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class SNAICONewJobFinish extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_job_finish);

        Button gotoOverview = (Button) findViewById(R.id.NeuerAuftragAngelegtBtn);
        gotoOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(SNAICONewJobFinish.this, SNAICOOverview.class);
                SNAICONewJobFinish.this.startActivity(mainIntent);
                SNAICONewJobFinish.this.finish();
            }
        });
    }
}
