package ro.as_mi.sniff.sniffevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.security.PublicKey;


public class MainActivity extends ActionBarActivity {
    TextView output;
    ProgressBar pb;
    EditText email;
    EditText pass;

    Button register_button;
    Button login;
    Button skip;

    public String SharedEmail;
    public String SharedPass;
    public String SharedUserID;

    public static String filename="MySharedString";
    public static String login_url="http://sniff.as-mi.ro/services/mobileLogin.php";
    SharedPreferences someData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        someData = getSharedPreferences(filename,0);

       /* output = (TextView) findViewById(R.id.textView);
        output.setMovementMethod(new ScrollingMovementMethod());*/

        pb=(ProgressBar)findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);

        email=(EditText) findViewById(R.id.email);
        pass=(EditText) findViewById(R.id.pass);
        SharedEmail=someData.getString("email","nu");
        SharedPass=someData.getString("pass","nu");
        SharedUserID=someData.getString("id","nu");

        if(!SharedUserID.equals("nu")){
            goToLogin();
        }

        if(!SharedEmail.equals("nu")){
            email.setText(SharedEmail);

        }
        if(!SharedPass.equals("nu")){
            pass.setText(SharedPass);

        }

        login=(Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isOnline()){
                    System.out.println(email.toString() + " " + pass.toString());
                    boolean ok=true;
                    if(email.getText().toString().equals("")||pass.getText().toString().equals("")){
                        ok=false;
                        Toast.makeText(getApplicationContext(),"Nu ati introdus email-ul sau parola",Toast.LENGTH_LONG).show();
                    }
                    else
                        ok=true;

                    if(ok)
                    requestData(login_url, email.getText().toString(), pass.getText().toString());
                }else
                    Toast.makeText(getApplicationContext(),"Internetul este dezactivat",Toast.LENGTH_LONG).show();
            }
        });

        register_button = (Button) findViewById(R.id.register);
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),Register.class);
                startActivityForResult(intent,0);
            }
        });
        skip = (Button) findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),DashboardActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            this.finish();
            System.exit(0);


        }
        return super.onOptionsItemSelected(item);
    }
    protected void goToLogin(){

        Intent intent=new Intent(this,DashboardActivity.class);
        startActivityForResult(intent,0);

    }
    private void requestData(String uri,String email,String pass) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("email", email);
        p.SetParam("pass", pass);

        MyTask task= new MyTask();
        task.execute(p);
    }

    protected void updateDisplay(String message){

        output.append(message+'\n');
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
            //updateDisplay("Starting task");
            pb.setVisibility(View.VISIBLE);
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
                    if(s.toString().equals("no_user\n")){
                        Toast.makeText(getApplicationContext(),"Emailul nu exista in baza de date",Toast.LENGTH_LONG).show();
                    }
                    else if(s.toString().equals("no_pass\n")){
                        Toast.makeText(getApplicationContext(),"Parola nu este corecta",Toast.LENGTH_LONG).show();
                    }
                    else{
                        JSONObject obj= new JSONObject(s);
                        SharedPreferences.Editor editor= someData.edit();
                        editor.putString("id",obj.getString("id"));
                        editor.putString("first_name",obj.getString("first_name"));
                        editor.putString("last_name",obj.getString("last_name"));
                        editor.putString("email",obj.getString("email"));
                        editor.putString("pass",pass.getText().toString());
                        editor.commit();
                        goToLogin();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();

                }finally {

                }


            }
            pb.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            updateDisplay(values[0]);
        }
    }
}
