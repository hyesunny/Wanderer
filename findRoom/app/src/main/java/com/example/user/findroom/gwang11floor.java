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
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;

public class gwang11floor extends AppCompatActivity {

    public ImageView gwangLectureRooom;
    public Handler handler;
    public TextView pTimeText,pTime1120,pTime1119;
	public String cnt[] = {"cnt-1119","cnt-1120"};
	public String pTime[]={"1234","5678"};
	public String cin[]={"8","8"};
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gwang11floor);
        gwangLectureRooom=(ImageView)findViewById(R.id.gwangLectureRoom);
        pTimeText=(TextView)findViewById(R.id.pTimeText);
        pTime1119=(TextView)findViewById(R.id.pTIme1119);
        pTime1120=(TextView)findViewById(R.id.pTime1120);
        startRetrieve();
    }
    public gwang11floor(){
        handler=new Handler();
    }

    public void startRetrieve() {
                int i;
                for(i=0;i<2;i++) {
                    RetrieveRequest req = new RetrieveRequest();
                    req.Numbering(i);
                    req.RetrieveRequest(cnt[i]);
                    req.start();
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                showLectureRoom();
                    }
                },1000);

    }
    public void showLectureRoom() {
            if(Integer.parseInt(cin[0])==8 || Integer.parseInt(cin[1])==8)
                gwangLectureRooom.setImageResource(R.drawable.servererror);
            else{
                pTimeText.setText("**** Updated Today ****");
                pTime1119.setText("Gwang1119 : " +pTime[0].substring(9,11)
                        +":"+pTime[0].substring(11,13)+":"+pTime[0].substring(13,15));
                pTime1120.setText("Gwang1120 : "+pTime[1].substring(9,11)
                        +":"+pTime[1].substring(11,13)+":"+pTime[1].substring(13,15));
                if (Integer.parseInt(cin[0]) == 0 && Integer.parseInt(cin[1]) == 0)
                    gwangLectureRooom.setImageResource(R.drawable.gwang00);
                else if (Integer.parseInt(cin[0]) == 1 && Integer.parseInt(cin[1]) == 0)
                    gwangLectureRooom.setImageResource(R.drawable.gwang10);
                else if (Integer.parseInt(cin[0]) == 0 && Integer.parseInt(cin[1]) == 1)
                    gwangLectureRooom.setImageResource(R.drawable.gwang01);
                else
                    gwangLectureRooom.setImageResource(R.drawable.gwang11);
        }
    }


    public interface IReceived {
        void getResponseBody(String msg);
    }
    class RetrieveRequest extends Thread {
        private final Logger LOG = Logger.getLogger(RetrieveRequest.class.getName());
        private IReceived receiver;
        private String ContainerName = "";
        private int Number;

        public void Numbering(int number){this.Number=number;}
        public void RetrieveRequest(String containerName) {
            this.ContainerName = containerName;
        }
        public RetrieveRequest() {}
        public void setReceiver(IReceived hanlder) { this.receiver = hanlder; }

        @Override
        public void run() {
            try {
                String sb = "http://58.233.226.102:7579/mobius-yt/adn-ae-Gwang/"
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
                pTime[Number]=cxml.GetElementXml(strResp,"ct");
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
