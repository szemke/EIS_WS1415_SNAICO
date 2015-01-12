package eis1415.rebecca.simon.snaico;

import android.app.Application;

import java.util.HashMap;

/**
 * Created by Simon on 08.01.2015.
 */
public class DataStore extends Application {
    private int globalInteger=1;
    private String globalString;

    private HashMap<String, String> companyStaff = new HashMap<String, String>();

    public HashMap getCompanyStaff(){ return companyStaff; }
    public String getGlobalString(){
        return globalString;
    }
    public int getGlobalInteger() {
        return globalInteger;
    }

    public void setCompanyStaff(HashMap<String, String> companyStaff) { this.companyStaff = companyStaff; }
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
