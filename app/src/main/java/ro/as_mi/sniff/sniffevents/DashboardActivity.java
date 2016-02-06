package ro.as_mi.sniff.sniffevents;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class DashboardActivity extends ActionBarActivity {
    Bitmap bitmap;
    ProgressDialog pDialog;

    ProgressBar pb;
    SharedPreferences someData;
    public List<Events> e_list=new ArrayList<Events>();
    List<String> myItems= new ArrayList<>();
    final List<Events> eventsList=new ArrayList<>();
    public static String filename="MySharedString";
    public ListView listView;
    public boolean ok;
    ImageView img;
    public String SharedUserID;
    public TextView no_Event;
    public SwipeActionAdapter mAdapter;

    ImageView ed;
    ImageView ca;
    ImageView so;
    ImageView di;
    ImageView co;
    ImageView tr;
    ImageView eventListP;
    ImageView account;
public TextView searchText;
    public RelativeLayout searchContainer;


    private SwipeRefreshLayout swipeRefreshLayout;

    private List<Movie> movieList;

    ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
    /*public static String get_projects_url="http://sniff.as-mi.ro/services/getPublicEvents.php";*/
    public static String get_projects_url="http://sniff.as-mi.ro/services/getPublicEvents.php";
    public static String getGet_projects_cat="http://sniff.as-mi.ro/services/mobileGetPublishedEventsByCategory.php";
    public static String getGet_projects_date="http://sniff.as-mi.ro/services/mobileGetPublishEventsByDate.php";
    public static String getGet_projects_org="http://sniff.as-mi.ro/services/mobileGetPublishedEventsByOrg.php";
    public static String search_projects_org="http://sniff.as-mi.ro/services/search.php";
    public void sw() {
        listView.setOnTouchListener(new View.OnTouchListener() {
            private static final int DEFAULT_THRESHOLD = 128;
            int initialX = 0;
            final float slop = ViewConfiguration.get(getApplicationContext()).getScaledTouchSlop();

            public boolean onTouch(final View view, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    initialX = (int) event.getX();
                    view.setPadding(0, 0, 0, 0);
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    int currentX = (int) event.getX();
                    int offset = currentX - initialX;
                    if (Math.abs(offset) > slop) {
                        view.setPadding(offset, 0, 0, 0);

                        if (offset > DEFAULT_THRESHOLD) {
                            // TODO :: Do Right to Left action! And do nothing on action_up.
                        } else if (offset < -DEFAULT_THRESHOLD) {
                            // TODO :: Do Left to Right action! And do nothing on action_up.
                        }
                    }
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    // Animate back if no action was performed.
                    ValueAnimator animator = ValueAnimator.ofInt(view.getPaddingLeft(), 0);
                    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator valueAnimator) {
                            view.setPadding((Integer) valueAnimator.getAnimatedValue(), 0, 0, 0);
                        }
                    });
                    animator.setDuration(150);
                    animator.start();
                }

                return false;
            };
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        String page="Dashboard";
        searchText=(TextView) findViewById(R.id.searchText);
        searchContainer=(RelativeLayout) findViewById(R.id.linearLayout_focus);
        eventListP=(ImageView) findViewById(R.id.eventListP);
        account=(ImageView) findViewById(R.id.userP);

        ImageView searchImg=(ImageView) findViewById(R.id.searchImg);
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ed.setAlpha((float) 0.4);
                ca.setAlpha((float) 0.4);
                so.setAlpha((float) 0.4);
                di.setAlpha((float) 0.4);
                co.setAlpha((float) 0.4);
                tr.setAlpha((float) 0.4);
                String textForSearch= searchText.getText().toString();
                if(!textForSearch.equals(""))
                    requestDataSearch(search_projects_org, textForSearch);
                else
                    requestData(get_projects_url);
            }
        });

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
        if(page.equals("Dashboard")){
            eventListP.setImageResource(R.drawable.list30active);
        }



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






        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .diskCacheExtraOptions(480, 800, null)
                .build();

        ImageLoader.getInstance().init(config);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(isOnline()){
                    requestData(get_projects_url);
                    ed.setAlpha((float) 0.4);
                    ca.setAlpha((float) 0.4);
                    so.setAlpha((float) 0.4);
                    di.setAlpha((float) 0.4);
                    co.setAlpha((float) 0.4);
                    tr.setAlpha((float) 0.4);
                }else{
                    Toast.makeText(getApplicationContext(),"Internetul este dezactivat",Toast.LENGTH_LONG).show();
                }
            }
        });

        someData = getSharedPreferences(filename,0);
        pb=(ProgressBar)findViewById(R.id.loader);
        pb.setVisibility(View.INVISIBLE);
        img = (ImageView) findViewById(R.id.image);
        no_Event=(TextView) findViewById(R.id.noEvent);
        no_Event.setVisibility(View.INVISIBLE);
        listView=(ListView) findViewById(R.id.eventList);
        listView.setLongClickable(true);
        SharedUserID=someData.getString("id","nu");
        registerForContextMenu(listView);
        if(isOnline()){
            requestData(get_projects_url);
        }else{
            Toast.makeText(getApplicationContext(),"Internetul este dezactivat",Toast.LENGTH_LONG).show();
        }


        ed=(ImageView) findViewById(R.id.ed);

        ed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat_id="1";
                requestDataEvCat(getGet_projects_cat, cat_id);
                Toast.makeText(getApplicationContext(),"Educational",Toast.LENGTH_LONG).show();
                ed.setAlpha((float) 1);
                ca.setAlpha((float) 0.4);
                so.setAlpha((float) 0.4);
                di.setAlpha((float) 0.4);
                co.setAlpha((float) 0.4);
                tr.setAlpha((float) 0.4);

            }
        });


        ca=(ImageView) findViewById(R.id.ca);

        ca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat_id="2";
                requestDataEvCat(getGet_projects_cat, cat_id);
                Toast.makeText(getApplicationContext(),"Cariera",Toast.LENGTH_LONG).show();
                ed.setAlpha((float) 0.4);
                ca.setAlpha((float) 1);
                so.setAlpha((float) 0.4);
                di.setAlpha((float) 0.4);
                co.setAlpha((float) 0.4);
                tr.setAlpha((float) 0.4);
            }
        });

        so=(ImageView) findViewById(R.id.so);

        so.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat_id="3";
                requestDataEvCat(getGet_projects_cat, cat_id);
                Toast.makeText(getApplicationContext(),"Social",Toast.LENGTH_LONG).show();
                ed.setAlpha((float) 0.4);
                ca.setAlpha((float) 0.4);
                so.setAlpha((float) 1);
                di.setAlpha((float) 0.4);
                co.setAlpha((float) 0.4);
                tr.setAlpha((float) 0.4);
            }
        });

        di=(ImageView) findViewById(R.id.di);

        di.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat_id="4";
                requestDataEvCat(getGet_projects_cat, cat_id);
                Toast.makeText(getApplicationContext(),"Distractie",Toast.LENGTH_LONG).show();
                ed.setAlpha((float) 0.4);
                ca.setAlpha((float) 0.4);
                so.setAlpha((float) 0.4);
                di.setAlpha((float) 1);
                co.setAlpha((float) 0.4);
                tr.setAlpha((float) 0.4);
            }
        });

        co=(ImageView) findViewById(R.id.co);

        co.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat_id="5";
                requestDataEvCat(getGet_projects_cat, cat_id);
                Toast.makeText(getApplicationContext(),"Concurs",Toast.LENGTH_LONG).show();
                ed.setAlpha((float) 0.4);
                ca.setAlpha((float) 0.4);
                so.setAlpha((float) 0.4);
                di.setAlpha((float) 0.4);
                co.setAlpha((float) 1);
                tr.setAlpha((float) 0.4);
            }
        });

        tr=(ImageView) findViewById(R.id.tr);

        tr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cat_id="6";
                requestDataEvCat(getGet_projects_cat, cat_id);
                Toast.makeText(getApplicationContext(),"Training",Toast.LENGTH_LONG).show();
                ed.setAlpha((float) 0.4);
                ca.setAlpha((float) 0.4);
                so.setAlpha((float) 0.4);
                di.setAlpha((float) 0.4);
                co.setAlpha((float) 0.4);
                tr.setAlpha((float) 1);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add("Feedback");
        menu.add("Locatie");
        menu.add("Evenimentele Organizatiei");
        menu.add("Detalii");
        Log.e("err:",v.toString());
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Integer position=info.position;

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
        editor.putString("long",eventsList.get(position).getLong());
        editor.putString("lat",eventsList.get(position).getLat());
        editor.commit();

        if(item.getTitle()=="Feedback"){
            Intent intent=new Intent(this,FeedbackActivity.class);
            startActivityForResult(intent,0);
        }

        if(item.getTitle()=="Detalii"){
            Intent intent=new Intent(this,EventInfo.class);
            startActivityForResult(intent,0);
        }

        if(item.getTitle()=="Locatie"){
            Intent intent=new Intent(this,LocationActivity.class);
            startActivityForResult(intent,0);
        }

        if(item.getTitle()=="Evenimentele Organizatiei"){
            requestDataEvOrg(getGet_projects_org,eventsList.get(position).getOrg());
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //do not give the editbox focus automatically when activity starts
        //searchText.clearFocus();
       searchContainer.requestFocus();
    }

    private void requestData(String uri) {

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);


        MyTask task= new MyTask();
        task.execute(p);
    }

    private void requestDataEvCat(String uri,String id){

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("id", id);


        MyTask task= new MyTask();
        task.execute(p);
    }

    private void requestDataSearch(String uri,String text){

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("event_name", text);


        MyTask task= new MyTask();
        task.execute(p);
    }

    private void requestDataEvOrg(String uri,String org){

        RequestPackage p = new RequestPackage();
        p.setMethod("POST");
        p.setUri(uri);
        p.SetParam("org", org);


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
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);

        return true;
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
        MenuItem refresh=menu.findItem(R.id.action_refresh);
        refresh.setVisible(false);

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
        if (id == R.id.action_refresh) {
            if(isOnline()){
                requestData(get_projects_url);
                ed.setAlpha((float) 0.4);
                ca.setAlpha((float) 0.4);
                so.setAlpha((float) 0.4);
                di.setAlpha((float) 0.4);
                co.setAlpha((float) 0.4);
                tr.setAlpha((float) 0.4);
            }else{
                Toast.makeText(getApplicationContext(),"Internetul este dezactivat",Toast.LENGTH_LONG).show();
            }
        }

        if(id==R.id.educational){

            String cat_id="1";
            requestDataEvCat(getGet_projects_cat, cat_id);
        }

        if(id==R.id.cariera){

            String cat_id="2";
            requestDataEvCat(getGet_projects_cat,cat_id);
        }

        if(id==R.id.social){

            String cat_id="3";
            requestDataEvCat(getGet_projects_cat,cat_id);
        }

        if(id==R.id.distractie){

            String cat_id="4";
            requestDataEvCat(getGet_projects_cat,cat_id);
        }
        if(id==R.id.concurs){

            String cat_id="5";
            requestDataEvCat(getGet_projects_cat,cat_id);
        }
        if(id==R.id.training){

            String cat_id="6";
            requestDataEvCat(getGet_projects_cat,cat_id);
        }

        if (id==R.id.azi){
            String cat_id="1";
            requestDataEvCat(getGet_projects_date,cat_id);
        }

        if (id==R.id.saptamana){
            String cat_id="2";
            requestDataEvCat(getGet_projects_date,cat_id);
        }

        if (id==R.id.luna){
            String cat_id="3";
            requestDataEvCat(getGet_projects_date,cat_id);
        }

        return super.onOptionsItemSelected(item);
    }

    protected class MyTask extends AsyncTask<RequestPackage,String,String> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
            pb.setVisibility(View.INVISIBLE);
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
                if(arr.length()==0){
                    swipeRefreshLayout.setRefreshing(false);
                    no_Event.setVisibility(View.VISIBLE);
                }else {
                    no_Event.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
                eventsList.clear();
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
                    event.setLong(obj.getString("location_x"));
                    event.setLat(obj.getString("location_y"));
                    eventsList.add(event);
                }

                populateListView();

                ArrayAdapter<Events> adapter = new projectListAdapter(eventsList);

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
                        editor.putString("long",eventsList.get(position).getLong());
                        editor.putString("lat",eventsList.get(position).getLat());
                        editor.putString("daysDiff",eventsList.get(position).getDiff());
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
            super(DashboardActivity.this,R.layout.event,pro_list);
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View viewItem=convertView;
            if(viewItem==null){
                viewItem = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.event, parent, false);
            }
            Events currentE=DashboardActivity.this.eventsList.get(position);

            TextView event_name=(TextView) viewItem.findViewById(R.id.titlu);
            event_name.setText(currentE.getTitlu());
            Integer d=Integer.parseInt(currentE.getDiff());

            /*ImageView im=(ImageView) viewItem.findViewById(R.id.imageView2);
            if(currentE.getCategorie().equals("Educational")){
                im.setImageResource(R.drawable.educational);
            }
            if(currentE.getCategorie().equals("Cariera")){
                im.setImageResource(R.drawable.cariera);
            }
            if(currentE.getCategorie().equals("Social")){
                im.setImageResource(R.drawable.social);
            }
            if(currentE.getCategorie().equals("Distractie")){
                im.setImageResource(R.drawable.distractie);
            }
            if(currentE.getCategorie().equals("Concurs")){
                im.setImageResource(R.drawable.concurs);
            }
            if(currentE.getCategorie().equals("Training")){
                im.setImageResource(R.drawable.training);
            }*/
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
            cat.setText(currentE.getOrg());


            boolean memCache = false;
            boolean fileCache = true;

           // aq.id(R.id.image).image("http://sniff.as-mi.ro/services/images/"+currentE.getId()+"_r.png", memCache, fileCache);

            imageLoader.displayImage("http://sniff.as-mi.ro/services/images/"+currentE.getId()+"_r.png", (ImageView) viewItem.findViewById(R.id.image));

            return viewItem;
        }
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(DashboardActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){


                pDialog.dismiss();

            }else{

                pDialog.dismiss();
                Toast.makeText(DashboardActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
