package com.zingat.andversionexample;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.zingat.andversion.AndVersion;
import com.zingat.andversion.OnCompletedListener;

public class MainActivity extends AppCompatActivity {

    public static final String ANDVERSION_URL = "http://andversion.com/sample/demoAndroid.json";

    private TextView operationCompleted;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        Toolbar toolbar = ( Toolbar ) findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );

        this.operationCompleted = ( TextView ) findViewById( R.id.operation_completed );
    }

    @Override
    protected void onResume() {
        super.onResume();

        AndVersion.getInstance()
                .setActivity( this )
                .setUri( ANDVERSION_URL )
                .addHeader( "Accept", "application/json" )
                .checkForceUpdate( new OnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        operationCompleted.setText( "Operation Completed!" );
                    }
                } );

    }

    @Override
    protected void onPause() {
        super.onPause();

        AndVersion.getInstance()
                .closeDialog();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if ( id == R.id.action_settings ) {
            return true;
        }

        return super.onOptionsItemSelected( item );
    }
}
