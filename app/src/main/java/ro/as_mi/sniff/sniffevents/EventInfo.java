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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


import java.util.Timer;
import java.util.TimerTask;


public class EventInfo extends ActionBarActivity {

    SharedPreferences someData;
    public static String filename="MySharedString";
    public RelativeLayout joinContainer;
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
    String d;
    String start_hour;
    String end_hour;
    Button Feedback_btn;
    Button Map_button;
    Button ProgButton;
    Button Detalii;
    ImageButton mapImg;
    ImageView joinIcon;
    String ifProg;
    String event_id;
    String id_string;
    private Menu menu;
    public String action;
    ImageLoader imageLoader = ImageLoader.getInstance();
    public String SharedUserID;
    public RelativeLayout over;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);

        someData = getSharedPreferences(filename,0);
//        getSupportActionBar().setElevation(0);
        SharedUserID=someData.getString("id","nu");
        ImageView eventListP;
        ImageView account;
        eventListP=(ImageView) findViewById(R.id.eventListP);
        account=(ImageView) findViewById(R.id.userP);
        ImageView heartP;
        heartP=(ImageView) findViewById(R.id.heartP);
        heartP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedUserID.equals("nu"))
                {
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivityForResult(intent, 0);
                }
                else
                {
                    Intent intent=new Intent(getApplicationContext(),FavoritesActivity.class);
                    startActivityForResult(intent, 0);
                }

            }
        });


        eventListP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DashboardActivity.class);
                startActivityForResult(intent,1);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedUserID.equals("nu"))
                {
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivityForResult(intent,0);
                }
                else
                {
                    Intent intent=new Intent(getApplicationContext(),settingsActivity.class);
                    startActivityForResult(intent,0);
                }

            }
        });

        ImageView messagesImg;
        messagesImg=(ImageView) findViewById(R.id.chatP);
        messagesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedUserID.equals("nu"))
                {
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivityForResult(intent, 0);
                }
                else
                {
                    Intent intent=new Intent(getApplicationContext(),MessagesActivity.class);
                    startActivityForResult(intent, 0);
                }

            }
        });

        joinIcon=(ImageView) findViewById(R.id.joinIcon);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCacheExtraOptions(480, 800, null)
                .build();

        ImageLoader.getInstance().init(config);

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
        d=someData.getString("daysDiff","");
        setTitle("");

        TextView days=(TextView) findViewById(R.id.daysDiff);
        if(d.equals('0')) {
            days.setText("Astăzi");
        }
        if(d.equals('1'))
            days.setText("Mâine");
        else
            days.setText("Începe în "+d+" zile");
        if(Integer.parseInt(d)<0){
            days.setText("În desfasurare de "+Math.abs(Integer.parseInt(d))+" zile");
        }

        id_string=id.toString();

        ProgButton=(Button) findViewById(R.id.programBtn);
        //ProgButton.setVisibility(View.INVISIBLE);
        if(ifProg.equals("0")){
            ProgButton.setEnabled(false);
        }
        TextView organization_e=(TextView) findViewById(R.id.days);
        organization_e.setText(titlu);
        joinContainer=(RelativeLayout) findViewById(R.id.joinContainer);




        TextView ev_address=(TextView) findViewById(R.id.address);
        ev_address.setText(adresa);
        imageLoader.displayImage("http://sniff.as-mi.ro/services/images/" +id + "_r.png", (ImageView) findViewById(R.id.p));

        ImageView evCat=(ImageView) findViewById(R.id.evCat);
        if(categorie.equals("Educational"))
            evCat.setImageResource(R.drawable.educational);
        if(categorie.equals("Cariera"))
            evCat.setImageResource(R.drawable.cariera);
        if(categorie.equals("Social"))
            evCat.setImageResource(R.drawable.social);
        if(categorie.equals("Distractie"))
            evCat.setImageResource(R.drawable.distractie);
        if(categorie.equals("Concurs"))
            evCat.setImageResource(R.drawable.concurs);
        if(categorie.equals("Training"))
            evCat.setImageResource(R.drawable.training);
        evCat.bringToFront();

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

        Map_button=(Button) findViewById(R.id.map);
        Map_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),LocationActivity.class);
                startActivityForResult(intent,0);
            }
        });


        over=(RelativeLayout) findViewById(R.id.overlayBg);
        over.bringToFront();
        over.setVisibility(View.INVISIBLE);
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

       /* if(SharedUserID.equals("nu"))
        {
            joinIcon.setVisibility(View.INVISIBLE);
        }else{*/
            joinIcon.setVisibility(View.VISIBLE);
        joinIcon.bringToFront();
            joinIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline()){
                        requestData(join_event_url,someData.getString("id","0"),id_string,action);
                    }
                }
            });
        //}


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
            favIcon.setVisible(false);
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
    public  void onBackPressed() {
        Intent intent=new Intent(this,DashboardActivity.class);
        startActivityForResult(intent,0);
        overridePendingTransition  (R.anim.abc_fade_in, R.anim.abc_fade_out);
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

        private void refreshTimer() {
            Timer autoUpdate = new Timer();
            autoUpdate.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Animation out = AnimationUtils.makeOutAnimation(getApplicationContext(), true);
                            over.startAnimation(out);
                            over.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }, 2000);
        }

        @Override
        protected void onPostExecute(String s) {
            //updateDisplay(s);
            if(s!=null){
                try{
                  /*  pb.setVisibility(View.INVISIBLE);*/
                    if(s.toString().equals("inserted\n")) {
                        //menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j_true));
                        joinIcon.setImageResource(R.drawable.j_true);
                        action="1";
                        Animation in = AnimationUtils.loadAnimation(getApplicationContext(), android.R.anim.fade_in);
                        over.startAnimation(in);
                        over.setVisibility(View.VISIBLE);
                        refreshTimer();
                        joinContainer.setBackgroundResource(R.color.red);

                    }

                    if(s.toString().equals("deleted\n")) {
                        //menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j));
                        joinIcon.setImageResource(R.drawable.j);
                        joinContainer.setBackgroundResource(R.color.white);
                        action="0";
                    }

                    if(s.toString().equals("true\n")){
                        //menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j_true));
                        joinIcon.setImageResource(R.drawable.j_true);
                        joinContainer.setBackgroundResource(R.color.red);
                        action="1";
                    }

                    if(s.toString().equals("false\n")){
                        //menu.getItem(1).setIcon(getResources().getDrawable(R.drawable.j));
                        joinIcon.setImageResource(R.drawable.j);
                        joinContainer.setBackgroundResource(R.color.white);
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
