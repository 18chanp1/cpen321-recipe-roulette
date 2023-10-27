package com.beaker.recipeRoulette;

public class ChatRoomWebSocketMessage {
    String tok;
    String type;
    String msg;

    public ChatRoomWebSocketMessage(String tok, String type, String msg) {
        this.tok = tok;
        this.type = type;
        this.msg = msg;
    }
}
