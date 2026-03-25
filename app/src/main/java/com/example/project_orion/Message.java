package com.example.project_orion;

public class Message {
    public String text;
    public boolean isUser; // true = fui eu, false = foi o Orion

    public Message(String text, boolean isUser) {
        this.text = text;
        this.isUser = isUser;
    }
}