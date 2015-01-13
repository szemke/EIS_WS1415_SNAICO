package eis1415.rebecca.simon.snaico;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class SNAICOServerIp extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicoserver_ip);

        Button confirmBtn = (Button) findViewById(R.id.ipEingebenBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText serverIpEditText = (EditText) findViewById(R.id.ipEingebenEditText);

                String serverIp = serverIpEditText.getText().toString();
                ((DataStore)getApplication()).setServerUrl(serverIp);
                Log.d("getText: ", serverIp);
                Intent mainIntent = new Intent(SNAICOServerIp.this, SNAICOSplash.class);
                SNAICOServerIp.this.startActivity(mainIntent);
                SNAICOServerIp.this.finish();

            }
        });
    }
}

