package frc.robot;

import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.*;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

public class Ncp {
    // * Variables
    String ncpServerURL = "ws://localhost:9072/v1/client";
    WebSocketFactory ncpFactory;
    WebSocket ncpWebSocket;
    ArrayList<String> ncpLogs = new ArrayList<>();

    Ncp() {
        // ? NCP uses websockets for fast communication between the client, the server, and the robot.
        // ? The client and server should be connected to the radio network for successful routine.
        try {
            ncpFactory = new WebSocketFactory();
            ncpWebSocket = ncpFactory.createSocket(ncpServerURL, 5000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ! Nyahiito Control Panel: Core function, handles initial connection and messages
    public void core() {
        try {
            ncpWebSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    JsonObject rootObject = JsonParser.parseString(message).getAsJsonObject();

                    // ? NCP Protocol: Websocket messages get sent from the control panel, to the Node server, and finally to this code.
                    // ? Messages are in the JSON format where all data is in the core object.

                    // Obtain variables and push changes
                    JsonObject varsObject = rootObject.getAsJsonObject("Variables");
                    Constants.armSpeed = varsObject.get("Arm Speed").getAsDouble();
                    Constants.armLengthOutPOV = varsObject.get("Arm Out POV").getAsInt();
                    Constants.armLengthInPOV = varsObject.get("Arm In POV").getAsInt();
                    Constants.joystickDriftSafety = varsObject.get("Joystick Drift").getAsDouble();
                }
            });

            ncpWebSocket.connect();

            log("Connection: Nyahiito es aquí.");
            publish();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    // ! Nyahiito Control Panel: Publish function, handles updating robot data
    public void publish() {
        // Create the main JSON Object
        JsonObject json = new JsonObject();

        // Create the logs array with all messages
        JsonArray logsArray = new JsonArray();
        for (String log : ncpLogs) {
            logsArray.add(log);
        }

        // Create the variables object with its values
        JsonObject varsObject = new JsonObject();
        varsObject.addProperty("Arm Speed", Constants.armSpeed);
        varsObject.addProperty("Arm In POV", Constants.armLengthInPOV);
        varsObject.addProperty("Arm Out POV", Constants.armLengthOutPOV);
        varsObject.addProperty("Joystick Drift", Constants.joystickDriftSafety);

        // Add all child objects
        json.add("Logs", logsArray);
        json.add("Variables", varsObject);

        // Send the JSON as a String
        ncpWebSocket.sendText(json.toString());

        // Flush pending console logs
        ncpLogs.clear();
    }

    public void log(String message) {
        ncpLogs.add(message);
    }
}
