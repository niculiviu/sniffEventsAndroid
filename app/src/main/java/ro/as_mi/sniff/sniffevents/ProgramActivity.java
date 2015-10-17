package ro.as_mi.sniff.sniffevents;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ProgramActivity extends ActionBarActivity {

    ProgressBar pb;
    SharedPreferences someData;
    public List<Program> e_list=new ArrayList<Program>();
    List<String> myItems= new ArrayList<>();
    final List<Program> proList=new ArrayList<>();
    public static String filename="MySharedString";
    Integer id;
    public String SharedUserID;
    public static String get_pro_url="http://sniff.as-mi.ro/services/mobileGetSchandule.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program);

        someData = getSharedPreferences(filename,0);
        SharedUserID=someData.getString("id","nu");
        id=someData.getInt("event_id",0);
        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);

        if(isOnline()){
            requestData(get_pro_url,id.toString());
        }
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
        getMenuInflater().inflate(R.menu.menu_program, menu);
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
                    Program pro=new Program();
                    pro.setStart_hour(obj.getString("start_hour"));
                    pro.setEnd_hour(obj.getString("end_hour"));
                    pro.setPro_date(obj.getString("s_date"));
                    pro.setPro_desc(obj.getString("s_desc"));
                    pro.setPro_id(obj.getInt("id"));
                    proList.add(pro);
                }

                populateListView();

                ArrayAdapter<Program> adapter = new projectListAdapter(proList);
                ListView listView=(ListView) findViewById(R.id.programList);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /*String project=proList.get(position).getTitlu();
                        SharedPreferences.Editor editor = someData.edit();
                        editor.putString("event_category", proList.get(position).getCategorie());
                        editor.putString("event_name", proList.get(position).getTitlu());
                        editor.putString("event_address",proList.get(position).getAdresa());
                        editor.putString("event_location",proList.get(position).getLocatie());
                        editor.putString("event_org",proList.get(position).getOrg());
                        editor.putString("start",proList.get(position).getStart());
                        editor.putString("end",proList.get(position).getEnd());
                        editor.putInt("event_id", proList.get(position).getId());
                        editor.putString("start_hour",proList.get(position).getStart_hour());
                        editor.putString("end_hour",proList.get(position).getEnd_hour());
                        editor.putString("ifProg",proList.get(position).getIfProg());
                        editor.putString("description",proList.get(position).getDesc());
                        editor.commit();

                        Intent intent=new Intent(view.getContext(),EventInfo.class);
                        startActivityForResult(intent,0);

                        Toast.makeText(getApplicationContext(),
                                "Detalii despre: " + project, Toast.LENGTH_LONG)
                                .show();*/

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

    public class projectListAdapter extends ArrayAdapter<Program>{
        public projectListAdapter(List<Program> pro_list){
            super(ProgramActivity.this,R.layout.program,pro_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View viewItem=convertView;
            if(viewItem==null){
                viewItem = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.program, parent, false);
            }
            Program currentE=ProgramActivity.this.proList.get(position);


            TextView pro_desc=(TextView) viewItem.findViewById(R.id.progDesc);
            pro_desc.setText(currentE.getPro_desc());

            TextView hour=(TextView) viewItem.findViewById(R.id.ora);
            hour.setText(currentE.getStart_hour()+" - "+currentE.getEnd_hour());

            TextView pro_date_text=(TextView) viewItem.findViewById(R.id.data);
            pro_date_text.setText(currentE.getPro_date());

            /*TextView event_name=(TextView) viewItem.findViewById(R.id.titlu);
            event_name.setText(currentE.getTitlu());

            TextView organization_e=(TextView) viewItem.findViewById(R.id.org);
            if(currentE.getDiff().equals(0)) {
                organization_e.setText("Astazi");
            }
            if(currentE.getDiff().equals(1))
                organization_e.setText("Maine");
            else
                organization_e.setText("În "+currentE.getDiff()+" zile");

            TextView cat=(TextView) viewItem.findViewById(R.id.cat);
            cat.setText(currentE.getCategorie());*/

            return viewItem;
        }
    }
}
