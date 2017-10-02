package com.zingat.andversion;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component( modules = { AndVersionModule.class } )
public interface AndVersionComponent {

    void inject( AndVersion andVersion );

}
