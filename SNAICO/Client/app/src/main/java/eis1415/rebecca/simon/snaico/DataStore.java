package eis1415.rebecca.simon.snaico;

import android.app.Application;

/**
 * Created by Simon on 08.01.2015.
 */
public class DataStore extends Application {
    private int globalInteger=1;
    private String globalString;

    public String getGlobalString(){
        return globalString;
    }
    public int getGlobalInteger() {
        return globalInteger;
    }

    public void setGlobalString(String globalString){
        this.globalString = globalString;
    }
    public void setGlobalInteger(int globalInteger) {
        this.globalInteger = globalInteger;
    }
    @Override
    public void onCreate() {
        //reinitialize variable
    }
}
