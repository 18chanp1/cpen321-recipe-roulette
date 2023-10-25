package com.beaker.recipeRoulette;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class ChatRoomWebSocket extends WebSocketListener {
    private ChatRoomLiveView c;
    private WebSocket ws;
    private final static String TAG = "Websocket";
    public ChatRoomWebSocket(ChatRoomLiveView c) {
        super();
        this.c = c;

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request req = new Request.Builder()
                .url("wss://cpen321-reciperoulette.westus.cloudapp.azure.com")
                .build();

        ws = client.newWebSocket(req, this);
        client.dispatcher().executorService().shutdown();
    }

    @Override
    public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
        super.onOpen(webSocket, response);
        String msg = serializeMsg("HELLO", "HELLO");
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
        //TODO handle incoming msgs here

        ChatRoomLiveEntry item = new Gson().fromJson(text, ChatRoomLiveEntry.class);

        if(item.type.equals("SHOPREQ"))
        {
            c.runOnUiThread(() -> c.addItemToList(item));

            Log.d(TAG, "Added to list: " + item.name);
        }

        if(item.type.equals("DEL"))
        {
            c.runOnUiThread(() -> c.removeItemFromList(item));

            Log.d(TAG, "Added to list: " + item.name);
        }






    }

    @Override
    public void onMessage(@NonNull WebSocket webSocket, @NonNull ByteString bytes) {
        super.onMessage(webSocket, bytes);

        Log.d(TAG, "Incoming msg: " + bytes.hex());
        //TODO handle incoming msgs here

        //first deserialize


    }

    private String serializeMsg(String type, String msg)
    {
        //get token
        SharedPreferences sharedPref =
                c.getSharedPreferences(c.getString(R.string.shared_pref_filename), Context.MODE_PRIVATE);
        String tok = sharedPref.getString("TOKEN", "NOTOKEN");
        Log.d("TAG", tok);

        ChatRoomWebSocketMessage socketMessage = new ChatRoomWebSocketMessage(tok, type, msg);

        return new Gson().toJson(socketMessage);
    }

    public void sendAString(String type, String msg)
    {
        String serialized = serializeMsg(type, msg);
        Log.d(TAG, serialized);
        ws.send(serialized);
    }

}
