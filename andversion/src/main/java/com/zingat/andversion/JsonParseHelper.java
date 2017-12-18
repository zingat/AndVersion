package com.zingat.andversion;

import com.zingat.andversion.constants.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;


class JsonParseHelper {


    private JSONObject andVersionObject;

    void setAndVersionObject( JSONObject responseObjcet ) {
        try {
            this.andVersionObject = responseObjcet.getJSONObject( Constants.ANDVERSION_OBJECT );

        } catch ( JSONException e ) {
            e.printStackTrace();
            this.andVersionObject = new JSONObject();

        }

    }

    int getMinSupportVersion() {

        try {
            if ( this.andVersionObject.has( Constants.MIN_VERSION_OBJECT ) ) {
                return this.andVersionObject.getInt( Constants.MIN_VERSION_OBJECT );
            }

        } catch ( JSONException e ) {
            e.printStackTrace();
        } catch ( NullPointerException e ) {
            e.printStackTrace();
        }

        return -1;
    }

    int getCurrentVersion() {

        try {
            if ( this.andVersionObject.has( Constants.CURRENT_VERSION_OBJECT ) ) {
                return this.andVersionObject.getInt( Constants.CURRENT_VERSION_OBJECT );
            }

        } catch ( JSONException e ) {
            e.printStackTrace();
        } catch ( NullPointerException e ) {
            e.printStackTrace();
        }

        return -1;
    }

    ArrayList< String > getWhatsNew() {

        ArrayList< String > whatsNewArray = new ArrayList<>();

        try {
            JSONObject whatsNewObject = andVersionObject.getJSONObject( Constants.WHATSNEW_OBJECT );
            Iterator< String > keys = whatsNewObject.keys();
            JSONArray whatsNewJsonArray = null;

            if ( whatsNewObject.has( Locale.getDefault().getLanguage() ) ) {
                whatsNewJsonArray = whatsNewObject.getJSONArray( Locale.getDefault().getLanguage() );
            } else if ( whatsNewObject.has( Constants.WHATSNEW_EN_ARRAY ) ) {
                whatsNewJsonArray = whatsNewObject.getJSONArray( Constants.WHATSNEW_EN_ARRAY );
            } else if ( whatsNewObject.length() > 0 ) {
                whatsNewJsonArray = whatsNewObject.getJSONArray( keys.next() );
            }

            if ( whatsNewJsonArray != null ) {
                for ( int i = 0; i < whatsNewJsonArray.length(); i++ ) {
                    whatsNewArray.add( whatsNewJsonArray.get( i ).toString() );
                }
            }

        } catch ( JSONException e ) {
            e.printStackTrace();

        }

        return whatsNewArray;
    }

}
