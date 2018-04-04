package com.example.andreas.iridiumflares;

import android.app.Activity;
import android.content.Intent;
//import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] myStringArray = {"test"};
        ListView FlareList = findViewById(R.id.list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, myStringArray);

        FlareList.setAdapter(adapter);

        Log.i("i", "Created menu item");
        FlareList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
                String value = (String)adapter.getItemAtPosition(position);
                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
                Log.i("i", "menu item selected: " + value);
                Switch(value);
                /*
                Intent intent = new Intent(this, VÅRAN-EGEN-KLASS.class);
                ****KOORDINATER****
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
                 */

            }
        });

    }

    public void Switch(String angle){
        Intent intent = new Intent(this, CompassActivity.class);
        intent.putExtra("Angle", angle );
        startActivity(intent);
    }

}
