package com.example.user.findroom;

/**
 * Created by user on 2017-11-15.
 */

public class CSEBase {
    public String CSEHostAddress = "";
    public String CSEPort = "";
    public String CSEName = "";

    public void setInfo(String param1, String param2, String param3){
        this.CSEHostAddress=param1;
        this.CSEPort=param2;
        this.CSEName=param3;
    }
    public String getHost(){
        return this.CSEHostAddress;
    }
    public String getPort(){
        return this.CSEPort;
    }
    public String getCSEName(){
        return this.CSEName;
    }
    public String getServiceUrl(){
        return "http://"+this.CSEHostAddress+":"+this.CSEPort+"/"+this.CSEName;
    }
}
