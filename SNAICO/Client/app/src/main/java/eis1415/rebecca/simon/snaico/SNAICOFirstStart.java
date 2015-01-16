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

/*
* SNAICOFirstStart erkennt ob der Benutzer zum ersten Mal die Applikation startet, oder ein wiederkehrender Benutzer ist.
* Ist der Benutzer bekannt wird geprüft ob es sich um einen Firmenleiter oder Mitarbeiter handelt und wird demensprechend auf
* die nächste Screen umgeleitet.
 */
public class SNAICOFirstStart extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snaicofirst_start);

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
