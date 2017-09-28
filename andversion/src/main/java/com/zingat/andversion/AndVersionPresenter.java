package com.zingat.andversion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by ismailgungor on 28.09.2017.
 */

public class AndVersionPresenter implements AndVersionContract.Presenter {

    private AndVersionContract.View mView;
    private OkHttpClient mClient;
    private JsonParseHelper mJsonParseHelper;

    @Inject
    AndVersionPresenter() {


    }

    @Inject
    void setClient( OkHttpClient client ) {
        this.mClient = client;
    }

    @Inject
    void setJsonParseHelper( JsonParseHelper jsonParseHelper ) {
        this.mJsonParseHelper = jsonParseHelper;
    }


    @Override
    public void setView( AndVersionContract.View view ) {

        this.mView = view;

    }

    @Override
    public void getJsonFromUrl( String url ) throws IOException {

        Request request = new Request.Builder()
                .url( url )
                .build();

        final String[] stResponse = new String[1];

        mClient.newCall( request ).enqueue( new Callback() {
            @Override
            public void onFailure( Call call, IOException e ) {
                stResponse[0] = "Hata";
                mView.makeToast( stResponse[0] );
            }

            @Override
            public void onResponse( Call call, Response response ) throws IOException {

                ResponseBody responseBody = response.body();

                try {
                    JSONObject jsonObject = new JSONObject( responseBody.string() );
                    JSONObject andVersionObject = jsonObject.getJSONObject( "AndVersion" );
                    stResponse[0] = String.valueOf( andVersionObject.getInt( "MinVersion" ) );
                    mView.makeToast( stResponse[0] );
                } catch ( JSONException e ) {
                    e.printStackTrace();
                }


            }
        } );


    }
}
