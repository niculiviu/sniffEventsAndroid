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
import android.widget.EditText;
import android.widget.Toast;
import org.json.JSONObject;


public class Register extends ActionBarActivity {
   public EditText nume;
   public EditText prenume;
   public EditText email;
   public EditText pass;

    Button register;
    public static String filename="MySharedString";
    public static String register_url="http://sniff.as-mi.ro/services/MobileRegister.php";
    SharedPreferences someData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        someData = getSharedPreferences(filename,0);
        nume=(EditText) findViewById(R.id.nume);
        prenume=(EditText) findViewById(R.id.prenume);
        email=(EditText) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.pass);

       register=(Button) findViewById(R.id.register);
       register.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(isOnline()){
                   boolean ok=true;
                   if(email.getText().toString().equals("") || nume.getText().toString().equals("") || prenume.getText().toString().equals("") || pass.getText().toString().equals("")){
                       ok=false;
                       Toast.makeText(getApplicationContext(),"Toate campurile sunt obligatorii",Toast.LENGTH_LONG).show();
                   }else{
                       ok=true;
                   }


                   if(ok)
                    requestData(register_url, email.getText().toString(), pass.getText().toString(),prenume.getText().toString(),nume.getText().toString());
               }
           }
       });
    }

    private void requestData(String uri,String email,String pass,String prenume,String nume) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("email", email);
        p.SetParam("pass", pass);
        p.SetParam("nume",nume);
        p.SetParam("prenume", prenume);

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    protected void goToLogin(){

        Intent intent=new Intent(this,MainActivity.class);
        startActivityForResult(intent,0);

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
                    if(s.toString().equals("mobile_user_success\n")) {
                        String emailResponse = email.getText().toString();
                        String passResponse = pass.getText().toString();
                        SharedPreferences.Editor editor = someData.edit();
                        editor.putString("email", emailResponse);
                        editor.putString("pass", passResponse);
                        editor.commit();
                        goToLogin();
                    }else{
                        Toast.makeText(getApplicationContext(),"Eroare!",Toast.LENGTH_LONG).show();
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
