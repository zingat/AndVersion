package com.zingat.andversion;

import com.zingat.andversion.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ismailgungor on 28.09.2017.
 */

public class JsonParseHelper {


    private JSONObject responseObjcet;
    private JSONObject andVersionObject;

    public void setResponseObjcet( JSONObject responseObjcet ) {
        this.responseObjcet = responseObjcet;
        setAndVerisonObject( responseObjcet );
    }

    public void setAndVerisonObject( JSONObject responseObjcet ) {

        try {
            this.andVersionObject = responseObjcet.getJSONObject( Constants.ANDVERSION_OBJECT );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

    }

    public int getMinSuppertVersion() {

        try {
            return this.andVersionObject.getInt( Constants.MIN_VERSION_OBJECT );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getCurrentVersion() {

        try {
            return this.andVersionObject.getInt( Constants.CURRENT_VERSION_OBJECT );
        } catch ( JSONException e ) {
            e.printStackTrace();
        }
        return -1;
    }

    public ArrayList< String > getTrWhatsNew() {

        ArrayList< String > trWhatsNewArray = new ArrayList<>();

        try {
            JSONObject whatsNewObject = andVersionObject.getJSONObject( Constants.WHATSNEW_OBJECT );
            JSONArray trWhatsNewJsonArray = whatsNewObject.getJSONArray( Constants.WHATSNEW_TR_ARRAY );

            for ( int i = 0; i < trWhatsNewJsonArray.length(); i++ ) {
                trWhatsNewArray.add( trWhatsNewJsonArray.get( i ).toString() );
            }
        } catch ( JSONException e ) {
            e.printStackTrace();
        }

        return trWhatsNewArray;
    }

}
