package com.example.user.findroom;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class gwang11floor extends AppCompatActivity {
    public TextView ttext;
    public TextView ctext;
    public ImageView gwang10;
    public Handler handler;
	public String cin="";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gwang11floor);
        ttext=(TextView)findViewById(R.id.ttext);
        ctext=(TextView)findViewById(R.id.ctext);
        gwang10=(ImageView)findViewById(R.id.gwang10);
    }
    public gwang11floor(){
        handler=new Handler();
    }
    public void tclick(View v) {
        switch (v.getId()) {
            case R.id.test: {
                RetrieveRequest req = new RetrieveRequest();
                ttext.setText("..");
                req.setReceiver(new IReceived() {
                    public void getResponseBody(final String msg) {
                        handler.post(new Runnable() {
                            public void run() {
                                if(Integer.parseInt(cin)==1)
                                {
                                    gwang10.setVisibility(View.VISIBLE);
                                }
                                else if(Integer.parseInt(cin)==0){
                                    gwang10.setVisibility(View.INVISIBLE);
                                }

                            }
                        });
                    }
                });
                req.start();
                break;
            }
        }
    }
    public interface IReceived {
        void getResponseBody(String msg);
    }
    class RetrieveRequest extends Thread {
        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());
        private IReceived receiver;
        private String ContainerName = "cnt-1119";

        public RetrieveRequest(String containerName) {
            this.ContainerName = containerName;
        }
        public RetrieveRequest() {}
        public void setReceiver(IReceived hanlder) { this.receiver = hanlder; }

        @Override
        public void run() {
            try {
                String sb = "http://58.233.226.102:7579/mobius-yt/adn-ae-Gwang/cnt-1119/latest";

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
				cin= cxml.GetElementXml(strResp,"con");

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
