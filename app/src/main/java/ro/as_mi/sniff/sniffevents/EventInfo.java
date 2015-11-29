package ro.as_mi.sniff.sniffevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class EventInfo extends ActionBarActivity {

    SharedPreferences someData;
    public static String filename="MySharedString";

    public String join_event_url="http://sniff.as-mi.ro/services/joinEvent.php";
    public String if_join_url="http://sniff.as-mi.ro/services/ifJoin.php";
    String titlu;
    String desc;
    String adresa;
    String locatie;
    String categorie;
    Integer id;
    String start;
    String end;
    String org;
    String start_hour;
    String end_hour;
    Button Feedback_btn;
    Button ProgButton;
    Button Detalii;
    ImageButton mapImg;
    String ifProg;
    String event_id;
    String id_string;
    private Menu menu;
    public String action;
    public String SharedUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        someData = getSharedPreferences(filename,0);
        getSupportActionBar().setElevation(0);
        SharedUserID=someData.getString("id","nu");
       /*editor.putString("event_category", eventsList.get(position).getCategorie());
        editor.putString("event_name", eventsList.get(position).getTitlu());
        editor.putString("event_address",eventsList.get(position).getAdresa());
        editor.putString("event_location",eventsList.get(position).getLocatie());
        editor.putString("event_org",eventsList.get(position).getOrg());
        editor.putString("start",eventsList.get(position).getStart());
        editor.putString("end",eventsList.get(position).getEnd());
        editor.putInt("event_id",eventsList.get(position).getId());
        editor.putString("start_hour",eventsList.get(position).getStart_hour());
        editor.putString("end_hour",eventsList.get(position).getEnd_hour());*/

        titlu=someData.getString("event_name","");
        categorie=someData.getString("event_category","");
        locatie=someData.getString("event_location","");
        adresa=someData.getString("event_address","");
        start=someData.getString("start","");
        end=someData.getString("end","");
        org=someData.getString("event_org","");
        start_hour=someData.getString("start_hour","");
        end_hour=someData.getString("end_hour","");
        id=someData.getInt("event_id",0);
        ifProg=someData.getString("ifProg","");
        setTitle("");

        id_string=id.toString();

        ProgButton=(Button) findViewById(R.id.programBtn);
        ProgButton.setVisibility(View.INVISIBLE);
        if(!ifProg.equals("0")){
            ProgButton.setVisibility(View.VISIBLE);
        }
        TextView organization_e=(TextView) findViewById(R.id.org);
        organization_e.setText(titlu);

        TextView cat=(TextView) findViewById(R.id.cat);
        cat.setText(categorie);

        TextView start_date=(TextView) findViewById(R.id.startDate);
        start_date.setText(start);

        TextView end_date=(TextView) findViewById(R.id.endDate);
        end_date.setText(end);

        TextView s_hour=(TextView) findViewById(R.id.start_hour);
        s_hour.setText(start_hour);

        TextView e_hour=(TextView) findViewById(R.id.end_hour);
        e_hour.setText(end_hour);

        TextView ev_address=(TextView) findViewById(R.id.address);
        ev_address.setText(adresa);

        boolean memCache = false;
        boolean fileCache = true;



        TextView ev_location=(TextView) findViewById(R.id.location);
        ev_location.setText(locatie);

        Feedback_btn = (Button) findViewById(R.id.feedback);
        Feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),FeedbackActivity.class);
                startActivityForResult(intent,0);
            }
        });



        Detalii=(Button) findViewById(R.id.detalii);
        Detalii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),EventDescActivity.class);
                startActivityForResult(intent,0);
            }
        });

        ProgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),ProgramActivity.class);
                startActivityForResult(intent,0);
            }
        });

        if(isOnline()){
            requestData2(if_join_url,id_string,someData.getString("id","0"));

        }

    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem userIcon = menu.findItem(R.id.action_settings);
        MenuItem favIcon=menu.findItem(R.id.action_join);
        if(SharedUserID.equals("nu"))
        {
            userIcon.setVisible(false);
            favIcon.setVisible(false);
        }
        else
        {
            userIcon.setVisible(true);
            favIcon.setVisible(true);
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_info, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent=new Intent(this,settingsActivity.class);
            startActivityForResult(intent,0);
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_join) {
            if(isOnline()){
                    requestData(join_event_url,someData.getString("id","0"),id_string,action);
            }
        }

        if(id==R.id.action_location){
            Intent intent=new Intent(this,LocationActivity.class);
            startActivityForResult(intent,0);
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestData(String uri,String user_id,String event_id,String action) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("action", action);
        p.SetParam("event_id",event_id);
        p.SetParam("user_id",user_id);


        MyTask task= new MyTask();
        task.execute(p);
    }

    private void requestData2(String uri,String event_id,String user_id) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("event_id",event_id);
        p.SetParam("user_id",user_id);


        MyTask task= new MyTask();
        task.execute(p);
    }

    protected boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnectedOrConnecting())
        {
            return true;
        }else{
            return false;
        }
    }

    protected void goToLogin(){

        Intent intent=new Intent(this,settingsActivity.class);
        startActivityForResult(intent, 0);

    }

    protected class MyTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {
            //updateDisplay("Starting task");
            //pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content=HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String s) {
            //updateDisplay(s);
            if(s!=null){
                try{
                  /*  pb.setVisibility(View.INVISIBLE);*/
                    if(s.toString().equals("inserted\n")) {
                        menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j_true));
                        action="1";
                    }

                    if(s.toString().equals("deleted\n")) {
                        menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j));
                        action="0";
                    }

                    if(s.toString().equals("true\n")){
                        menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j_true));
                        action="1";
                    }

                    if(s.toString().equals("false\n")){
                        menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j));
                        action="0";
                    }
                }
                catch (Exception e){
                    e.printStackTrace();

                }finally {

                }


            }

        }


    }
}
