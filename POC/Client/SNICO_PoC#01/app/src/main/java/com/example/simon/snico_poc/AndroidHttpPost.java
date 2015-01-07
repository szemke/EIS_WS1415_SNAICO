package com.example.simon.snico_poc;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AndroidHttpPost extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Button sendButton = (Button) findViewById(R.id.send);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PostTask(AndroidHttpPost.this).execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_poc1) {
            startActivity(new Intent(getApplicationContext(), AndroidHttpPost.class));
        }
        if (id == R.id.action_poc2) {
            startActivity(new Intent(getApplicationContext(), AndroidLocation.class));
        }
        if(id == R.id.action_poc3){
            startActivity(new Intent(getApplicationContext(), AndroidGCM.class));
        }
        return super.onOptionsItemSelected(item);
    }
    private class PostTask extends AsyncTask<String, Void, Boolean>{
        private ProgressDialog dialog;
        private Context context;

        public PostTask(Activity activity){
            context = activity;
            dialog = new ProgressDialog(context);
        }

        protected void onPreExecute(){
            this.dialog.setMessage("Nachricht wird gesendet...");
            this.dialog.show();
        }

        protected void onPostExecute(final Boolean success){
            if(dialog.isShowing()){
                this.dialog.setMessage("Nachricht gesendet.");
                EditText editText = (EditText) findViewById(R.id.editText);
                editText.setText("");
                dialog.dismiss();
            }
        }

        protected Boolean doInBackground(final String... args){
            EditText editText = (EditText) findViewById(R.id.editText);
            String message = editText.getText().toString();

            Date date = new Date();
            List params = new ArrayList();
            params.add(new BasicNameValuePair("date", date.toString()));
            params.add(new BasicNameValuePair("message", message));

            JSONParser jParser = new JSONParser();
            String url = "http://188.40.158.58:3000/post/";
            JSONArray jPost = jParser.makeHttpRequest(url, "POST", params);

            return null;
        }
    }
}
