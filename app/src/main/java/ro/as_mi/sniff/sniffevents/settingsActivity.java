package ro.as_mi.sniff.sniffevents;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class settingsActivity extends ActionBarActivity {
   Button SchimbaParola;
    Button EditProfile;
    Button Logout;
    Button Fav;
    public static String filename="MySharedString";
    SharedPreferences someData;
    ImageView eventListP;
    ImageView account;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        eventListP=(ImageView) findViewById(R.id.eventListP);
        account=(ImageView) findViewById(R.id.userP);
        account.setImageResource(R.drawable.user42active);
        ImageView heartP;
        heartP=(ImageView) findViewById(R.id.heartP);
        heartP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent=new Intent(getApplicationContext(),FavoritesActivity.class);
                    startActivityForResult(intent, 0);


            }
        });

        eventListP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DashboardActivity.class);
                startActivityForResult(intent,1);
            }
        });
        ImageView messagesImg;
        messagesImg=(ImageView) findViewById(R.id.chatP);
        messagesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                    Intent intent=new Intent(getApplicationContext(),MessagesActivity.class);
                    startActivityForResult(intent, 0);


            }
        });
        someData = getSharedPreferences(filename,0);
        SchimbaParola=(Button) findViewById(R.id.SchimbaParola);
        SchimbaParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),changePasswordActivity.class);
                startActivityForResult(intent,0);
            }
        });

        EditProfile=(Button) findViewById(R.id.editProfileButton);
        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),EditProfileActivity.class);
                startActivityForResult(intent,0);
            }
        });

        Logout=(Button) findViewById(R.id.LogoutButton);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                SharedPreferences.Editor editor= someData.edit();
                editor.remove("id");
                editor.remove("first_name");
                editor.remove("last_name");
                editor.remove("email");
                editor.remove("pass");
                editor.commit();
                Intent intent=new Intent(v.getContext(),MainActivity.class);
                startActivityForResult(intent,0);
            }
        });
        Fav=(Button) findViewById(R.id.FavoritesButton);
        Fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),FavoritesActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }
    @Override
    public  void onBackPressed() {
        Intent intent=new Intent(this,DashboardActivity.class);
        startActivityForResult(intent,0);
        overridePendingTransition  (R.anim.abc_fade_in, R.anim.abc_fade_out);
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
}
