package frc.robot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.*;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.simulation.DriverStationSim;

public class Ncp {
    // * Variables
    String ncpServerURL = "ws://localhost:9072/v1/client";
    WebSocketFactory ncpFactory;
    WebSocket ncpWebSocket;
    ArrayList<String> ncpLogs = new ArrayList<>();

    // * APS Variables
    boolean apsRecording = false;
    boolean apsPlaying = false;
    boolean apsHasSaved = false;
    ArrayList<ArrayList<Double>> apsActions = new ArrayList<>(3);
    int apsIndex = 0;

    private ArrayList<Integer> ncpAprilTags = new ArrayList<>();

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
                    // ! Be careful executing commands, Dwayne! Not sure if you know how to type
                    if (rootObject.has("Execute")) {
                        String command = rootObject.get("Execute").getAsString();
                        exec(command);
                    } else {
                        // * NCP Protocol: Websocket messages get sent from the control panel, to the
                        // Node server, and finally to this code.
                        // ? Messages are in the JSON format where all data is in the core object.

                        // Set robot mode
                        switch (rootObject.get("Mode").getAsString()) {
                            case "off":
                                DriverStationSim.setEnabled(false);
                                break;
                            case "teleop":
                                DriverStationSim.setEnabled(true);
                                DriverStationSim.setAutonomous(false);
                                break;
                            case "auto":
                                DriverStationSim.setEnabled(true);
                                DriverStationSim.setAutonomous(true);
                                break;
                        }

                        // Obtain variables and push changes
                        JsonObject varsObject = rootObject.getAsJsonObject("Variables");
                        Constants.armSpeed = varsObject.get("Arm Speed").getAsDouble();
                        Constants.armLengthOutPOV = varsObject.get("Arm Out POV").getAsInt();
                        Constants.armLengthInPOV = varsObject.get("Arm In POV").getAsInt();
                        Constants.joystickDriftSafety = varsObject.get("Joystick Drift").getAsDouble();

                        // Detect APS
                        if (rootObject.has("Pathway System")) {
                            String apsOption = rootObject.get("Pathway System").getAsString();
                            aps(apsOption);
                        }

                        // Detect APL
                        if (rootObject.has("Pathway Loading")) {
                            String aplPath = rootObject.get("Pathway Loading").getAsString();
                            if (aplPath != "") {
                                apl(aplPath);
                            }
                        }
                    }
                }
            });

            ncpWebSocket.connect();

            log("<b style='color: orange'>🐯 Nyahiito es aquí.</b>");
            publish();
        } catch (WebSocketException e) {
            e.printStackTrace();
        }
    }

    // ! Nyahiito Control Panel: Publish function, handles updating robot data
    public void publish() {
        // Create the main JSON Object
        JsonObject json = new JsonObject();

        // Set the mode, assuming the *test* mode is irrelevant
        String mode = RobotState.isTeleop() ? "teleop" : RobotState.isAutonomous() ? "auto" : "off";
        json.addProperty("Mode", mode);

        // Create the variables object with its values
        JsonObject varsObject = new JsonObject();
        varsObject.addProperty("Arm Speed", Constants.armSpeed);
        varsObject.addProperty("Arm In POV", Constants.armLengthInPOV);
        varsObject.addProperty("Arm Out POV", Constants.armLengthOutPOV);
        varsObject.addProperty("Joystick Drift", Constants.joystickDriftSafety);

        // Create detected AprilTags array
        JsonArray tagsArray = new JsonArray();
        for (Integer tag : ncpAprilTags) {
            tagsArray.add(tag);
        }

        // Add all child objects
        json.add("Variables", varsObject);
        json.add("Tags", tagsArray);

        // ? Normal Publish: Send the normal data for the client
        ncpWebSocket.sendText(json.toString());
    }

    // ? Overload for sending in AprilTag data
    public void publish(ArrayList<Integer> aprilTags) {
        ncpAprilTags = aprilTags;
        publish();
    }

    public void log(String message) {
        JsonObject log = new JsonObject();
        log.addProperty("Log", message);

        // ? Log Publish: Send a message to display in the client's terminal
        ncpWebSocket.sendText(log.toString());
    }

    // ! Don't pull a sudo rm -rf /
    public void exec(String cmd) {
        try {
            // * For the actual robot, it's { "/bin/sh", "-c", cmd }
            Process proc = Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", cmd });

            BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                // Call a log function with the output line
                log(line);
            }

            proc.waitFor();
        } catch (IOException | InterruptedException e) {
            // Alert that the command failed
            log("Invalid.");

            e.printStackTrace();
        }
    }

    // * Pathway Functions
    public void aps(String mode) {
        // Switch function looking real good right now
        if (mode == "reset") {
            apsActions.clear();
            apsRecording = false;
            apsPlaying = false;
            apsHasSaved = false;
        } else if (mode == "stop") {
            apsRecording = false;
            apsPlaying = false;
        } else if (mode == "play") {
            if (!apsHasSaved) {

                // Playing will also officially save the file
                String id = String.format("%04d", new Random().nextInt(10000));

                // Convert to JSON string then save
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(apsActions);

                try {
                    Files.write(Paths.get("C:\\Users\\Admin\\Desktop\\Nyahiito\\" + id + ".json"), json.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                apsHasSaved = true;
            }

            apsPlaying = true;
        } else {
            // "record"
            apsRecording = true;
        }
    }

    // TODO: Pathway Loading. Find the JSON file in /home/lvuser, load it into an
    // ArrayList, then start recording!
    public void apl(String path) {
        // Is this a real path?
        if (!new File(path).exists()) {
            return;
        }

        apsActions.clear();

        try {
            // Load file into a String
            String pathData = new String(Files.readAllBytes(Paths.get(path)));
            Gson gson = new Gson();

            // Put the actions into the Array
            apsActions = gson.fromJson(pathData, ArrayList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        apsPlaying = true;
    }
}
