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
import android.widget.ProgressBar;
import android.widget.Toast;


public class changePasswordActivity extends ActionBarActivity {
    public EditText oldPass;
    public EditText newPass;
    public EditText newPass2;
    public String user_id;
    public ProgressBar pb;

    Button ChangePass;
    public static String filename="MySharedString";
    public static String change_pass_url="http://sniff.as-mi.ro/services/MobileSchimbaParola.php";
    SharedPreferences someData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        someData = getSharedPreferences(filename,0);

        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);

        user_id=someData.getString("id","0");
        oldPass= (EditText) findViewById(R.id.oldPass);
        newPass= (EditText) findViewById(R.id.newPass);
        newPass2=(EditText) findViewById(R.id.newPass2);
        ChangePass=(Button) findViewById(R.id.changePass);


            ChangePass.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isOnline()){
                            boolean ok=true;
                            if(newPass.getText().toString().equals(newPass2.getText().toString())){
                                ok=true;
                                if(oldPass.getText().toString().equals("") || oldPass.getText().toString().equals("") || newPass2.getText().toString().equals("")){
                                    ok=false;
                                    Toast.makeText(getApplicationContext(), "Toate campurile sunt obligatorii", Toast.LENGTH_LONG).show();
                                }else{
                                    ok=true;
                                }
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Cele doua parole nu se potrivesc", Toast.LENGTH_LONG).show();
                        }




                        if(ok){
                            requestData(change_pass_url, oldPass.getText().toString(), newPass.getText().toString(),user_id.toString());
                            pb.setVisibility(View.VISIBLE);
                            }
                    }
                }
            });

    }
    private void requestData(String uri,String oldPass,String newPass,String id) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("id", id);
        p.SetParam("old",oldPass);
        p.SetParam("new",newPass);


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
        getMenuInflater().inflate(R.menu.menu_change_password, menu);
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
                    pb.setVisibility(View.INVISIBLE);
                    if(s.toString().equals("success\n")) {
                        SharedPreferences.Editor editor= someData.edit();
                        editor.putString("pass",newPass.getText().toString());
                        editor.commit();
                        goToLogin();
                        Toast.makeText(getApplicationContext(),"Parola a fost schimbata cu succes!",Toast.LENGTH_LONG).show();
                    }else{
                        if(s.toString().equals("parola_veche\n"))
                            Toast.makeText(getApplicationContext(),"Parola veche nu se potriveste",Toast.LENGTH_LONG).show();
                        else{
                            Toast.makeText(getApplicationContext(),"Eroare, parola nu a fost schimbata",Toast.LENGTH_LONG).show();
                        }
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
