package com.beaker.reciperoulette.ChatRoom;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.beaker.reciperoulette.R;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatRoomWebSocket extends WebSocketListener {
    private final ChatRoomLiveView context;
    private final WebSocket ws;
    private final static String TAG = "ChatRoomWebSocket";
    private final boolean isCookingRequest;
    private String name;
    private String details;
    private String contact;

    public ChatRoomWebSocket(ChatRoomLiveView context, boolean isCookingRequest, String name,
                             String details, String contact) {
        super();
        this.context = context;
        this.isCookingRequest = isCookingRequest;
        this.name = name;
        this.details = details;
        this.contact = contact;

        if(this.context == null) throw new IllegalArgumentException();
        if(this.name == null) this.name = "";
        if(this.details == null) this.details = "";
        if(this.contact == null) this.contact = "";

        OkHttpClient client = new OkHttpClient.Builder()
                //0 is default values, don't know what it does
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request req = new Request.Builder()
                .url(context.getString(R.string.server_wss_url))
                .build();

        ws = client.newWebSocket(req, this);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);

        //get token
        ChatRoomLiveEntry c;

        if(isCookingRequest) {
            c = new ChatRoomLiveEntry("", name, details,
                    contact, "", context.getString(R.string.cht_type_newcookreq));
        }
        else {
            c = new ChatRoomLiveEntry("", name, details,
                    contact, "", context.getString(R.string.cht_type_newshopreq));
        }

        String msg = new Gson().toJson(c);

        webSocket.send(msg);
    }
    @Override
    public void onClosed(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosed(webSocket, code, reason);
        Log.d(TAG, "Socket closed");
    }

    @Override
    public void onClosing(@NonNull WebSocket webSocket, int code, @NonNull String reason) {
        super.onClosing(webSocket, code, reason);
        webSocket.close(1000, null);
        Log.d(TAG, "Closing: " + reason);
    }

    @Override
    public void onFailure(@NonNull WebSocket webSocket, @NonNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        t.printStackTrace();
    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
        super.onMessage(webSocket, text);
        Log.d(TAG, "Incoming msg: " + text);

        ChatRoomLiveEntry item = new Gson().fromJson(text, ChatRoomLiveEntry.class);

        //do nothing if illegal type.
        if(item.type == null) return;

        if(item.type.equals(context.getString(R.string.cht_type_shopreq)))
        {
            context.runOnUiThread(() -> context.addItemToList(item));
            Log.d(TAG, "Added to list: " + item.name);
        }
        else if(item.type.equals(context.getString(R.string.cht_type_cookreq)))
        {
            context.runOnUiThread(() -> context.addItemToList(item));
            Log.d(TAG, "Added to list: " + item.name);
        }


        if(item.type.equals(context.getString(R.string.cht_type_del)))
        {
            context.runOnUiThread(() -> context.removeItemFromList(item));

            Log.d(TAG, "Removed from list: " + item.name);
        }
    }
    public void close()
    {
        ws.close(1000, "Socket is closed by client");
    }

}
