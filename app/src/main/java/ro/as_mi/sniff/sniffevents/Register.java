package ro.as_mi.sniff.sniffevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.OkUrlFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

public class Register extends ActionBarActivity {
    EditText editText_user_name;
    EditText editText_email;
    Button button_login;
    public EditText nume;
    public EditText prenume;
    public EditText email;
    public EditText pass;

    Button register;
    public static String filename="MySharedString";
    public static String register_url="http://sniff.as-mi.ro/services/MobileRegisterAndroid.php";
    SharedPreferences someData;


    static final String TAG = "pavan";

    TextView mDisplay;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    SharedPreferences prefs;
    Context context;
    String regid;
    String msg;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        context = getApplicationContext();


        if(isUserRegistered(context)){

            startActivity(new Intent(this,DashboardActivity.class));
            finish();

        }else {

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


            // Check device for Play Services APK. If check succeeds, proceed with
            //  GCM registration.

            if (checkPlayServices()) {
                gcm = GoogleCloudMessaging.getInstance(this);
                regid = getRegistrationId(context);

                if (regid.isEmpty()) {
                    registerInBackground();
                }


            } else {
                Log.i("pavan", "No valid Google Play Services APK found.");
            }
        }
    }

    private void requestData(String uri,String email,String pass,String prenume,String nume) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("email", email);
        p.SetParam("pass", pass);
        p.SetParam("nume",nume);
        p.SetParam("prenume", prenume);
        p.SetParam("gcm_id",regid);

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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Util.PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String registrationId = prefs.getString(Util.PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        // Check if app was updated; if so, it must clear the registration ID
        // since the existing registration ID is not guaranteed to work with
        // the new app version.
        int registeredVersion = prefs.getInt(Util.PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion) {
            Log.i(TAG, "App version changed.");
            return "";
        }
        return registrationId;
    }


    private boolean isUserRegistered(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        String User_name = prefs.getString(Util.USER_NAME, "");
        if (User_name.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return false;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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




    /**
     * Substitute you own sender ID here. This is the project number you got
     * from the API Console, as described in "Getting Started."
     */


    private void registerInBackground() {
        new AsyncTask() {



            @Override
            protected String doInBackground(Object[] params) {


                try {

                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(Register.this);
                    }
                    regid = gcm.register(Util.SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;



                    // You should send the registration ID to your server over HTTP,
                    //GoogleCloudMessaging gcm;/ so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    // sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the registration ID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;



            }
        }.execute();

    }


    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Util.PROPERTY_REG_ID, regId);
        editor.putInt(Util.PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }

    private void storeUserDetails(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Util.EMAIL, editText_email.getText().toString());
        editor.putString(Util.USER_NAME, editText_user_name.getText().toString());
        editor.commit();
    }

    private SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the registration ID in your app is up to you.
        return getSharedPreferences(Register.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }


    //  private RequestQueue mRequestQueue;
    private void sendRegistrationIdToBackend() {
        // Your implementation here.


        new SendGcmToServer().execute();

// Access the RequestQueue through your singleton class.
        // AppController.getInstance().addToRequestQueue(jsObjRequest, "jsonRequest");

    }


    private class SendGcmToServer extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();


        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub

            String url = Util.register_url+"?name="+editText_user_name.getText().toString()+"&email="+editText_email.getText().toString()+"&regId="+regid;
            Log.i("pavan", "url" + url);

            OkHttpClient client_for_getMyFriends = new OkHttpClient();

            String response = null;
            // String response=Utility.callhttpRequest(url);

            try {
                url = url.replace(" ", "%20");
                response = callOkHttpRequest(new URL(url),
                        client_for_getMyFriends);
                for (String subString : response.split("<script", 2)) {
                    response = subString;
                    break;
                }
            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            //Toast.makeText(context,"response "+result,Toast.LENGTH_LONG).show();

            if (result != null) {
                if (result.equals("success")) {

                    storeUserDetails(context);
                    startActivity(new Intent(Register.this, DashboardActivity.class));
                    finish();

                } else {

                    Toast.makeText(context, "Try Again" + result, Toast.LENGTH_LONG).show();
                }


            }else{

                Toast.makeText(context, "Check net connection ", Toast.LENGTH_LONG).show();
            }

        }


    }



    // Http request using OkHttpClient
    String callOkHttpRequest(URL url, OkHttpClient tempClient)
            throws IOException {

        HttpURLConnection connection = new OkUrlFactory(tempClient).open(url);

        connection.setConnectTimeout(40000);
        InputStream in = null;
        try {
            // Read the response.
            in = connection.getInputStream();
            byte[] response = readFully(in);
            return new String(response, "UTF-8");
        } finally {
            if (in != null)
                in.close();
        }
    }

    byte[] readFully(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        for (int count; (count = in.read(buffer)) != -1;) {
            out.write(buffer, 0, count);
        }
        return out.toByteArray();
    }
  /* public EditText nume;
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

        Intent intent=new Intent(this,Register.class);
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


    }*/
}
