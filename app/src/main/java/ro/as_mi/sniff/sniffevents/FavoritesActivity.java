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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FavoritesActivity extends ActionBarActivity {
    ProgressBar pb;
    SharedPreferences someData;
    public List<Events> e_list=new ArrayList<Events>();
    List<String> myItems= new ArrayList<>();
    final List<Events> eventsList=new ArrayList<>();
    public static String filename="MySharedString";

    ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance

    /*public static String get_projects_url="http://sniff.as-mi.ro/services/getPublicEvents.php";*/
    public static String get_projects_url="http://sniff.as-mi.ro/services/getFavorites.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        someData = getSharedPreferences(filename,0);
        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);

        ImageView eventListP;
        ImageView account;
        eventListP=(ImageView) findViewById(R.id.eventListP);
        account=(ImageView) findViewById(R.id.userP);
        ImageView heartP;
        heartP=(ImageView) findViewById(R.id.heartP);
        heartP.setImageResource(R.drawable.heart30active);


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


                    Intent intent=new Intent(getApplicationContext(),settingsActivity.class);
                    startActivityForResult(intent,0);


            }
        });


        if(isOnline()){
            requestData(get_projects_url,someData.getString("id","0"));
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                    .diskCacheExtraOptions(480, 800, null)
                    .build();

            ImageLoader.getInstance().init(config);
        }else{
            Toast.makeText(getApplicationContext(), "Internetul este dezactivat", Toast.LENGTH_LONG).show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
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

                String pro_desc;
                for(int i=0;i<arr.length();i++){
                    JSONObject obj=arr.getJSONObject(i);
                    Events event=new Events();
                    event.setTitlu(obj.getString("project_name"));
                    event.setDesc(obj.getString("description"));
                    event.setCategorie(obj.getString("categoryName"));
                    event.setAdresa(obj.getString("address"));
                    event.setId((int) obj.getInt("event_id"));
                    event.setOrg(obj.getString("org_name"));
                    event.setLocatie(obj.getString("location_name"));
                    event.setStart(obj.getString("start_date"));
                    event.setEnd(obj.getString("end_date"));
                    event.setStart_hour(obj.getString("start_time"));
                    event.setEnd_hour(obj.getString("end_time"));
                    event.setDiff(obj.getString("DiffDate"));
                    event.setIfProg(obj.getString("ProgNR"));
                    eventsList.add(event);
                }

                populateListView();

                ArrayAdapter<Events> adapter = new projectListAdapter(eventsList);
                ListView listView=(ListView) findViewById(R.id.eventList);
                listView.setAdapter(adapter);


                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String project=eventsList.get(position).getTitlu();
                        SharedPreferences.Editor editor = someData.edit();
                        editor.putString("event_category", eventsList.get(position).getCategorie());
                        editor.putString("event_name", eventsList.get(position).getTitlu());
                        editor.putString("event_address",eventsList.get(position).getAdresa());
                        editor.putString("event_location",eventsList.get(position).getLocatie());
                        editor.putString("event_org",eventsList.get(position).getOrg());
                        editor.putString("start",eventsList.get(position).getStart());
                        editor.putString("end",eventsList.get(position).getEnd());
                        editor.putInt("event_id", eventsList.get(position).getId());
                        editor.putString("start_hour",eventsList.get(position).getStart_hour());
                        editor.putString("end_hour",eventsList.get(position).getEnd_hour());
                        editor.putString("ifProg",eventsList.get(position).getIfProg());
                        editor.putString("description",eventsList.get(position).getDesc());
                        editor.commit();

                        Intent intent=new Intent(view.getContext(),EventInfo.class);
                        startActivityForResult(intent,0);

                        Toast.makeText(getApplicationContext(),
                                "Detalii despre: " + project, Toast.LENGTH_LONG)
                                .show();

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

    public class projectListAdapter extends ArrayAdapter<Events>{
        public projectListAdapter(List<Events> pro_list){
            super(FavoritesActivity.this,R.layout.event,pro_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View viewItem=convertView;
            if(viewItem==null){
                viewItem = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.event, parent, false);
            }
            Events currentE=FavoritesActivity.this.eventsList.get(position);

            TextView event_name=(TextView) viewItem.findViewById(R.id.titlu);
            event_name.setText(currentE.getTitlu());
            Integer d=Integer.parseInt(currentE.getDiff()) ;
            TextView organization_e=(TextView) viewItem.findViewById(R.id.days);
            if(currentE.getDiff().equals('0')) {
                organization_e.setText("Astazi");
            }
            if(currentE.getDiff().equals('1'))
                organization_e.setText("Maine");
            else
                organization_e.setText("În "+currentE.getDiff()+" zile");
            if(d<0){
                organization_e.setText("Au trecut "+Math.abs(d)+" zile");
            }

            TextView cat=(TextView) viewItem.findViewById(R.id.cat);
            cat.setText(currentE.getCategorie());

            imageLoader.displayImage("http://sniff.as-mi.ro/services/images/"+currentE.getId()+"_r.png", (ImageView) viewItem.findViewById(R.id.image));

            return viewItem;
        }
    }
}
