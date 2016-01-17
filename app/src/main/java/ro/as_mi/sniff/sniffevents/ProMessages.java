package ro.as_mi.sniff.sniffevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProMessages extends ActionBarActivity {

    ProgressBar pb;
    SharedPreferences someData;
    public List<Program> e_list=new ArrayList<Program>();
    List<String> myItems= new ArrayList<>();
    final List<Messages> mesList=new ArrayList<>();
    public static String filename="MySharedString";
    Integer id;
    public Integer index;
    public String SharedUserID;
    public static String get_mess_url="http://sniff.as-mi.ro/services/getProjectMessages.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_messages);
        someData = getSharedPreferences(filename,0);
        SharedUserID=someData.getString("id","nu");
        Integer ev_id=someData.getInt("event_id",0);
        index=someData.getInt("index",0);
        pb=(ProgressBar)findViewById(R.id.pb);
        pb.setVisibility(View.INVISIBLE);

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

        if(isOnline()){
            requestData(get_mess_url,ev_id.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pro_messages, menu);
        return true;
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

                    m.setMessage_text(obj.getString("message"));
                    m.setMessage_id(obj.getInt("idmessage"));
                    m.setProject_name(obj.getString("project_name"));
                    mesList.add(m);
                }

                populateListView();

                ArrayAdapter<Messages> adapter = new projectListAdapter(mesList);
                ListView listView=(ListView) findViewById(R.id.messagesList);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {



                        Intent intent=new Intent(view.getContext(),EventInfo.class);
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
            super(ProMessages.this,R.layout.pro_message,pro_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View viewItem=convertView;
            if(viewItem==null){
                viewItem = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pro_message, parent, false);
            }
            Messages currentM=ProMessages.this.mesList.get(position);

            TextView pro_name=(TextView) viewItem.findViewById(R.id.proNameText);
            pro_name.setText(currentM.getProject_name());

            TextView messageText=(TextView) viewItem.findViewById(R.id.messageText);
            messageText.setText(currentM.getMessage_text());

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
