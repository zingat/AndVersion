package com.zingat.andversion;

/**
 * Created by mustafaolkun on 18/12/2017.
 * <p>
 * Used to save parsed data on temporary data.
 */
class ParsedContentModel {

    private int minSupportVersion;
    private int currentUpdateVersion;
    private String features;

    int getMinSupportVersion() {
        return minSupportVersion;
    }

    void setMinSupportVersion( int minSupportVersion ) {
        this.minSupportVersion = minSupportVersion;
    }

    int getCurrentUpdateVersion() {
        return currentUpdateVersion;
    }

    void setCurrentUpdateVersion( int currentUpdateVersion ) {
        this.currentUpdateVersion = currentUpdateVersion;
    }

    String getFeatures() {
        return features;
    }

    void setFeatures( String features ) {
        this.features = features;
    }
}
