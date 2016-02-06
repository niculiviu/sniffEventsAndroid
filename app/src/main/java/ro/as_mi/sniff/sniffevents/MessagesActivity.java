package ro.as_mi.sniff.sniffevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MessagesActivity extends ActionBarActivity {

    ProgressBar pb;
    SharedPreferences someData;
    public List<Program> e_list=new ArrayList<Program>();
    List<String> myItems= new ArrayList<>();
    final List<Messages> mList=new ArrayList<>();
    public static String filename="MySharedString";
    Integer id;
    public String SharedUserID;
    public static String get_mess_url="http://sniff.as-mi.ro/services/getAllMessagesForUser.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        someData = getSharedPreferences(filename,0);
        SharedUserID=someData.getString("id","nu");

        pb=(ProgressBar)findViewById(R.id.pb);
        pb.setVisibility(View.INVISIBLE);

        if(isOnline()){
            requestData(get_mess_url,SharedUserID);
        }

        String page="Messages";


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
                    startActivityForResult(intent, 0);


            }
        });



        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedUserID.equals("nu"))
                {
                    Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                    startActivityForResult(intent, 0);
                }
                else
                {
                    Intent intent=new Intent(getApplicationContext(),settingsActivity.class);
                    startActivityForResult(intent, 0);
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

        if(page.equals("Messages")){
            messagesImg.setImageResource(R.drawable.chat81active);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_messages, menu);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestData(String uri,String id) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("id", id);


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

    protected class MyTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {

            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(RequestPackage... params) {
            String content=HttpManager.getData(params[0]);
            return content;
        }

        @Override
        protected void onPostExecute(String s) {

            try{

                JSONArray arr = new JSONArray(s);


                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    Messages m=new Messages();
                    m.setMes_nr(obj.getInt("msgNr"));
                    m.setProject_name(obj.getString("project_name"));
                    m.setProject_id(obj.getInt("event_id"));
                    m.setIndex(i);
                    mList.add(m);
                }

                populateListView();

                ArrayAdapter<Messages> adapter = new projectListAdapter(mList);
                ListView listView=(ListView) findViewById(R.id.messagesList);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        SharedPreferences.Editor editor = someData.edit();
                        editor.putInt("event_id", mList.get(position).getProject_id());
                        editor.putInt("index",position);
                        editor.commit();
                        Intent intent=new Intent(view.getContext(),ProMessages.class);
                        startActivityForResult(intent,0);


                    }
                });


            }
            catch (Exception e){
                e.printStackTrace();
            }

            pb.setVisibility(View.INVISIBLE);
        }





    }

    private void populateListView() {


    }

    public class projectListAdapter extends ArrayAdapter<Messages>{
        public projectListAdapter(List<Messages> pro_list){
            super(MessagesActivity.this,R.layout.message,pro_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View viewItem=convertView;
            if(viewItem==null){
                viewItem = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.message, parent, false);
            }
            Messages currentM=MessagesActivity.this.mList.get(position);

            TextView mNr=(TextView) viewItem.findViewById(R.id.msgNr);
            mNr.setText(currentM.getMes_nr().toString());

            Integer messageNr=currentM.getMes_nr();

            FrameLayout bg=(FrameLayout) viewItem.findViewById(R.id.NrBg);
            Integer r=37;
            Integer g=142;
            Integer b=37;
            bg.setBackgroundColor(Color.rgb(r, g, b));

            TextView p_name=(TextView) viewItem.findViewById(R.id.project_name);
            p_name.setText(currentM.getProject_name());


           /* TextView pro_desc=(TextView) viewItem.findViewById(R.id.progDesc);
            pro_desc.setText(currentE.getPro_desc());

            TextView hour=(TextView) viewItem.findViewById(R.id.ora);
            hour.setText(currentE.getStart_hour()+" - "+currentE.getEnd_hour());

            TextView pro_date_text=(TextView) viewItem.findViewById(R.id.data);
            pro_date_text.setText(currentE.getPro_date());*/

            return viewItem;
        }
    }
}
