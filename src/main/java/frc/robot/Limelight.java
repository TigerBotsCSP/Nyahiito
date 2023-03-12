package frc.robot;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketFactory;

import com.google.gson.*;

import java.util.ArrayList;

public class Limelight {
    WebSocketFactory m_factory;
    WebSocket m_webSocket;
    ArrayList<Integer> m_detectedTags = new ArrayList<Integer>();

    public void startLimelight() {
        m_factory = new WebSocketFactory();

        while (true) {
            try {
                m_webSocket = m_factory.createSocket("ws://limelight.local:5806");
                m_webSocket.connect();
                break; // Exit loop once successful
            } catch (Exception e) {
                System.out.println("Failed to connect to WebSocket: " + e.getMessage() + " | Retrying...");
            }
        }
                    
        m_webSocket.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                // New message, clear 'em out
                m_detectedTags.clear();
                
                JsonObject rootObject = JsonParser.parseString(message).getAsJsonObject();

                JsonArray tagsArray = rootObject.getAsJsonObject("Results").getAsJsonArray("Fiducial");

                if (tagsArray.isJsonNull() || tagsArray.isEmpty())
                    return;

                for (JsonElement tag : tagsArray) {
                    m_detectedTags.add(tag.getAsJsonObject().get("fID").getAsInt());
                }
            }
        });
    }

    public boolean tagDetected(int tag) {
        return m_detectedTags.contains(tag);
    }

    public ArrayList<Integer> getDetectedTags() {
        return m_detectedTags;
    }
}
