package com.zingat.andversion;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by mustafaolkun on 18/12/2017.
 *
 * Used to get response comes from server.
 * Triggered if the response that comes from server is not null
 *
 * @see Callback#onResponse(Call, Response)
 */
interface IServerResponseListener {

    void onParsedData( ParsedContentModel content );

}
