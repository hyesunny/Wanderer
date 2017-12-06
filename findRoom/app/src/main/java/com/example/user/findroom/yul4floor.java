package com.example.user.findroom;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class yul4floor extends AppCompatActivity {
    public ImageView yulLectureRooom;
    public Handler handler;
    public TextView pTimeText,pTime401,pTime404;
    public String cnt[] = {"cnt-401","cnt-404"};
    public String pTime[]={"1234","5678"};
    public String cin[]={"8","8"};
    public String up="0";
    public int count=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yul4floor);
        yulLectureRooom=(ImageView)findViewById(R.id.yulLectureRoom);
        pTimeText=(TextView)findViewById(R.id.pTimeText_y);
        pTime401=(TextView)findViewById(R.id.pTime401);
        pTime404=(TextView)findViewById(R.id.pTime404);
        CheckTypesTask task = new CheckTypesTask();
        startRetrieve();
        task.execute();
    }

    public yul4floor(){
        handler=new Handler();
    }
    public void startRetrieve() {
        int i;
        for(i=0;i<2;i++) {
            RetrieveRequest req = new RetrieveRequest();
            req.Numbering(i);
            if(up.equals("1"))
                req.IsUpdated("updatelatest");
            else
                req.IsUpdated("latest");
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

    public void showLectureRoom(){
        if(Integer.parseInt(cin[0])==8 || Integer.parseInt(cin[1])==8) {
            yulLectureRooom.setImageResource(R.drawable.servererror);
        }
        else {
            pTimeText.setText("**** Updated Today ****");
            pTime401.setText("Yul401 : " + pTime[0].substring(9, 11)
                    + ":" + pTime[0].substring(11, 13) + ":" + pTime[0].substring(13, 15));
            pTime404.setText("Yul404 : " + pTime[1].substring(9, 11)
                    + ":" + pTime[1].substring(11, 13) + ":" + pTime[1].substring(13, 15));
            if (Integer.parseInt(cin[0]) == 0 && Integer.parseInt(cin[1]) == 0)
                yulLectureRooom.setImageResource(R.drawable.yul00);
            else if (Integer.parseInt(cin[0]) == 1 && Integer.parseInt(cin[1]) == 0)
                yulLectureRooom.setImageResource(R.drawable.yul10);
            else if (Integer.parseInt(cin[0]) == 0 && Integer.parseInt(cin[1]) == 1)
                yulLectureRooom.setImageResource(R.drawable.yul01);
            else
                yulLectureRooom.setImageResource(R.drawable.yul11);
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
        private String Update="";

        public void Numbering(int number){this.Number=number;}
        public void IsUpdated(String update){this.Update=update;}
        public void RetrieveRequest(String containerName) {
            this.ContainerName = containerName;
        }
        public RetrieveRequest() {}
        public void setReceiver(IReceived hanlder) { this.receiver = hanlder; }

        @Override
        public void run() {
            try {
                String sb = "http://58.233.226.102:7579/mobius-yt/adn-ae-Yul/"
                        +ContainerName+"/"+Update;
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

    public void udClick(View v){
        CheckTypesTask update = new CheckTypesTask();
        up="1"; count=8;
        startRetrieve();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startRetrieve();
            }
        },3000);
        update.execute();
    }

    private class CheckTypesTask extends AsyncTask<Void,Void,Void> {
        ProgressDialog asyncDialog = new ProgressDialog(yul4floor.this);

        protected void onPreExecute(){
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage("Loading...");

            asyncDialog.show();
            super.onPreExecute();
        }
        protected Void doInBackground(Void... arg0){
            try{
                for(int i=0;i<count;i++){
                    Thread.sleep(500);
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Void result){
            asyncDialog.dismiss();
            super.onPostExecute(result);
        }
    }

}
