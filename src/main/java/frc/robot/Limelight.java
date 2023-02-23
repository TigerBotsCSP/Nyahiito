package frc.robot;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import com.google.gson.*;

import java.io.IOException;
import java.util.ArrayList;

public class Limelight {
    WebSocketFactory m_factory;
    WebSocket m_webSocket;
    ArrayList<Integer> m_detectedTags = new ArrayList<Integer>();

    private void Limelightx() {
        m_factory = new WebSocketFactory();
        try {
            m_webSocket = m_factory.createSocket("ws://10.90.72.50:5806", 5000);
            
            m_webSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    // New message, clear 'em out
                    m_detectedTags.clear();

                    JsonParser parser = new JsonParser();
                    JsonObject rootObject = parser.parse(message).getAsJsonObject();

                    JsonArray tagsArray = rootObject.getAsJsonObject("Results").getAsJsonArray("Fiducial");

                    if (tagsArray.isJsonNull() || tagsArray.isEmpty())
                        return;

                    for (JsonElement tag : tagsArray) {
                        m_detectedTags.add(tag.getAsJsonObject().get("fID").getAsInt());
                    }
                }
            });

            System.out.print("h");
            m_webSocket.connect();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    public void startLimelight() {
        Limelightx();
    }

    public boolean tagDetected(int tag) {
        return m_detectedTags.contains(tag);
    }
}
