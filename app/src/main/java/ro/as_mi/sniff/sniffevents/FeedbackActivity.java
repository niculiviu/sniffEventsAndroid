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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class FeedbackActivity extends ActionBarActivity {

    SharedPreferences someData;
    public static String filename="MySharedString";
    String titlu;
    Integer id;
    public static String send_feedback_url="http://sniff.as-mi.ro/services/mobileTrimiteFeedback.php";
    ProgressBar pb;
    Button Feedback_btn;
    public TextView name;
    public TextView message;
    public String SharedUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        someData = getSharedPreferences(filename,0);
        pb=(ProgressBar)findViewById(R.id.loading);
        SharedUserID=someData.getString("id","nu");
        titlu=someData.getString("event_name","");
        id=someData.getInt("event_id",0);
        message=(TextView) findViewById(R.id.feedback);
        name=(TextView) findViewById(R.id.nume);
        name.setText("");
        message.setText("");

        setTitle("Feedback");
        pb.setVisibility(View.INVISIBLE);
        Feedback_btn = (Button) findViewById(R.id.sendFeedback);
        Feedback_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb.setVisibility(View.VISIBLE);
                if(isOnline()){
                    requestData(send_feedback_url,id.toString(),name.getText().toString(),message.getText().toString());
                }
            }
        });


    }

    private void requestData(String uri,String id,String name,String message) {

        RequestPackage p = new RequestPackage();

        p.setMethod("POST");
        p.setUri(uri);

        p.SetParam("event", id);
        p.SetParam("msg", message);
        p.SetParam("user", name);


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
    public void goToProject(){
        Intent intent=new Intent(this,EventInfo.class);
        startActivityForResult(intent,0);
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


                if(s.equals("success\n")){
                    name.setText("");
                    message.setText("");
                    Toast.makeText(getApplicationContext(),"Feedback trimis cu succes!",Toast.LENGTH_LONG).show();
                    goToProject();
                }else{
                    Toast.makeText(getApplicationContext(),"Eroare! Feedback-ul nu a fost trimis",Toast.LENGTH_LONG).show();
                }


            }
            catch (Exception e){
                e.printStackTrace();
            }

            pb.setVisibility(View.INVISIBLE);
        }





    }

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem userIcon = menu.findItem(R.id.action_settings);
        if(SharedUserID.equals("nu"))
        {
            userIcon.setVisible(false);
        }
        else
        {
            userIcon.setVisible(true);
        }

        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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
            Intent intent=new Intent(this,settingsActivity.class);
            startActivityForResult(intent,0);
        }

        return super.onOptionsItemSelected(item);
    }
}
