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
        // ? NCP uses websockets for fast communication between the client, the server,
        // and the robot.
        // ? The client and server should be connected to the radio network for
        // successful routine.
        try {
            ncpFactory = new WebSocketFactory();
            ncpWebSocket = ncpFactory.createSocket(ncpServerURL, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ! Nyahiito Control Panel: Core function, handles initial connection and
    // messages
    public void core() {
        try {
            ncpWebSocket.addListener(new WebSocketAdapter() {
                @Override
                public void onTextMessage(WebSocket websocket, String message) throws Exception {
                    JsonObject rootObject = JsonParser.parseString(message).getAsJsonObject();

                    // ? Execute Request: If the message is one, execute a command.
                    // ! Be careful executing commands, Dwayne! Not sure if you know how to type though
                    if (rootObject.has("Execute")) {
                        String command = rootObject.getAsJsonObject("Execute").getAsString();
                        log(command);
                        exec(command);
                    } else {
                        // * NCP Protocol: Websocket messages get sent from the control panel, to the Node server, and finally to this code.
                        // ? Messages are in the JSON format where all data is in the core object.

                        // Obtain variables and push changes
                        JsonObject varsObject = rootObject.getAsJsonObject("Variables");
                        Constants.armSpeed = varsObject.get("Arm Speed").getAsDouble();
                        Constants.armLengthOutPOV = varsObject.get("Arm Out POV").getAsInt();
                        Constants.armLengthInPOV = varsObject.get("Arm In POV").getAsInt();
                        Constants.joystickDriftSafety = varsObject.get("Joystick Drift").getAsDouble();

                        log("Updated successfully.");
                    }
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

        // Create the variables object with its values
        JsonObject varsObject = new JsonObject();
        varsObject.addProperty("Arm Speed", Constants.armSpeed);
        varsObject.addProperty("Arm In POV", Constants.armLengthInPOV);
        varsObject.addProperty("Arm Out POV", Constants.armLengthOutPOV);
        varsObject.addProperty("Joystick Drift", Constants.joystickDriftSafety);

        // Add all child objects
        json.add("Variables", varsObject);

        // ? Normal Publish: Send the normal data for the client
        ncpWebSocket.sendText(json.toString());
    }

    public void log(String message) {
        // ? Log Publish: Send a message to display in the client's terminal
        ncpWebSocket.sendText("{\"Log\":\"" + message + "\"}");
    }

    // ! Don't pull a sudo rm -rf /
    public void exec(String cmd) {
        try {
            // TODO: Test this, doesn't work with Windows simulation
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
