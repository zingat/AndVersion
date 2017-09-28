package com.zingat.andversion;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by ismailgungor on 28.09.2017.
 */
@Singleton
@Component( modules = { AndVersionModule.class } )
public interface AndVersionComponent {

    void inject( Ismail ısmail );

}
