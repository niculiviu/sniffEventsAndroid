package ro.as_mi.sniff.sniffevents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class EventDescActivity extends ActionBarActivity {
    SharedPreferences someData;
    public static String filename="MySharedString";
    TextView ev_desc;
    public String SharedUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_desc);
        ev_desc=(TextView) findViewById(R.id.desc);
        someData = getSharedPreferences(filename,0);

        SharedUserID=someData.getString("id","nu");

        ev_desc.setText(someData.getString("description","No Description"));
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
        getMenuInflater().inflate(R.menu.menu_event_desc, menu);
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
