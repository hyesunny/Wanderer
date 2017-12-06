package com.example.user.findroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class building extends AppCompatActivity {

    public Handler handler;
    public String cnt[] = {"cnt-1119","cnt-1120","cnt-401","cnt-403"};
    public String cin[]={"8","8","8","8"};
    public String aName[]={"adn-ae-Gwang","adn-ae-Yul"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_building);
        startRetrieve();
    }

    public void DialClick(View view){
        switch(view.getId()) {
            case R.id.gwang:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent next;
                next=new Intent(building.this,popups.class);
                next.putExtra("cin1119",cin[0]);
                next.putExtra("cin1120",cin[1]);
                startActivity(next);}
        },600);
                break;
            case R.id.yulchung:
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent next;
                        next=new Intent(building.this,yulpopups.class);
                        next.putExtra("cin401",cin[2]);
                        next.putExtra("cin403",cin[3]);
                        startActivity(next);}
                },600);

                break;

        }
    }

    public building(){
        handler=new Handler();
    }

    public void startRetrieve() {
        int i,j,k=0;
        for(j=0;j<2;j++) {
            for(i=0;i<2;i++){
                RetrieveRequest req = new RetrieveRequest();
                req.RetrieveAE(aName[j]);
                req.Numbering(i+j+k);
                req.RetrieveRequest(cnt[i+j+k]);
                req.start();
            }
            k++;
        }

    }
    public interface IReceived {
        void getResponseBody(String msg);
    }
    class RetrieveRequest extends Thread {
        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());
        private IReceived receiver;
        private String ContainerName = "";
        private String AEName="";
        private int Number;

        public void Numbering(int number){this.Number=number;}
        public void RetrieveRequest(String containerName) {
            this.ContainerName = containerName;
        }
        public void RetrieveAE(String aename){this.AEName=aename;}
        public RetrieveRequest() {}
        public void setReceiver(IReceived hanlder) { this.receiver = hanlder; }

        @Override
        public void run() {
            try {
                String sb = "http://58.233.226.102:7579/mobius-yt/"+AEName+"/"
                        +ContainerName+"/latest";

                URL mUrl = new URL(sb);

                HttpURLConnection conn = (HttpURLConnection) mUrl.openConnection();
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setDoOutput(false);

                conn.setRequestProperty("Accept", "application/xml");
                conn.setRequestProperty("X-M2M-RI", "4891");
                conn.setRequestProperty("X-M2M-Origin", "SOrigin" );
                conn.setRequestProperty("nmtype", "short");
                conn.connect();

                String strResp = "";
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String strLine= "";
                while ((strLine = in.readLine()) != null) {
                    strResp += strLine;
                }

                ParseElementXml cxml= new ParseElementXml();
                cin[Number]= cxml.GetElementXml(strResp,"con");

                if ( strResp != "" ) {
                    receiver.getResponseBody(strResp);
                }
                conn.disconnect();

            } catch (Exception exp) {
                LOG.log(Level.WARNING, exp.getMessage());
            }
        }
    }
}
