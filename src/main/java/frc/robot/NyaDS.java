package frc.robot;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class NyaDS extends WebSocketServer {
    // Variables
    public Timer m_timer = new Timer();
    public boolean m_executing = false;

    // Gyro variables
    private final double Kp = 0.05;
    private final double Ki = 0.0;
    private final double Kd = 0.0;
    private double integral = 0.0;
    private double previousError = 0.0;
    private double setPoint = 0.0;

    public NyaDS() {
        super(new InetSocketAddress(9072));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        SmartDashboard.putBoolean("NyaDS Client", true);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        SmartDashboard.putBoolean("NyaDS Client", false);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (conn == null)
            return;

        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        String event = json.get("event").getAsString();
        JsonArray schedule = json.get("data").getAsJsonArray();
        System.out.println(event);
        if (event.equals("auto_execute")) {
            execute(schedule);
        } else if (event.equals("auto_reverse")) {
            reverse(schedule);
        } else {
            save(schedule);
        }

    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
    }

    @Override
    public void onStart() {
        SmartDashboard.putBoolean("NyaDS Server", true);
    }

    public void execute(JsonArray schedule) {
        m_timer.start();

        m_executing = true;

        for (JsonElement cmd : schedule) {
            JsonObject data = cmd.getAsJsonObject();
            String type = data.get("type").getAsString();
            double time = data.get("time").getAsDouble();
            double speed = data.get("speed").getAsDouble();

            if (type.equals("Drive")) {
                RobotContainer.m_drive.straightDrive(speed);
                Timer.delay(time);
                RobotContainer.m_drive.straightDrive(0);
            } else if (type.equals("Rotate")) {
                RobotContainer.m_arm.setOrientation(speed);
                Timer.delay(time);
                RobotContainer.m_arm.setOrientation(0);
            } else if (type.equals("Length")) {
                RobotContainer.m_arm.setLength(-speed);
                Timer.delay(time);
                RobotContainer.m_arm.setLength(0);
            } else if (type.equals("Delay")) {
                Timer.delay(time);
            } else if (type.equals("Intaker")) {
                RobotContainer.m_intaker.togglePusher();
            } else if (type.equals("Mouth")) {
                RobotContainer.m_intaker.toggleMotor();
            } else if (type.equals("Open")) {
                RobotContainer.m_arm.open();
            } else if (type.equals("Close")) {
                RobotContainer.m_arm.close();
            } else if (type.equals("Balance")) {
                while (m_timer.get() < 14.9) {
                    double angle = RobotContainer.m_gyro.getGyro().getPitch();

                    // Calculate the error and update the integral
                    double error = setPoint - angle;
                    integral += error * 0.02;
                    double derivative = (error - previousError) / 0.02;
                    previousError = error;
        
                    // Calculate the output using the PID formula
                    double output = Kp * error + Ki * integral + Kd * derivative;
        
                    output = RobotContainer.limit(output, 1);
        
                    RobotContainer.m_drive.straightDrive(output);
        
                    Timer.delay(0.02);
                }

                m_timer.reset();
            }
        }
        m_executing = false;
    }

    public void reverse(JsonArray schedule) {
        m_executing = true;
        for (int i = schedule.size() - 1; i >= 0; i--) {
            JsonElement cmd = schedule.get(i);
            JsonObject data = cmd.getAsJsonObject();
            String type = data.get("type").getAsString();
            double time = data.get("time").getAsDouble();
            double speed = data.get("speed").getAsDouble();

            if (type.equals("Drive")) {
                RobotContainer.m_drive.straightDrive(speed);
                Timer.delay(time);
                RobotContainer.m_drive.straightDrive(0);
            } else if (type.equals("Rotate")) {
                RobotContainer.m_arm.setOrientation(-speed);
                Timer.delay(time);
                RobotContainer.m_arm.setOrientation(0);
            } else if (type.equals("Length")) {
                RobotContainer.m_arm.setLength(speed);
                Timer.delay(time);
                RobotContainer.m_arm.setLength(0);
            } else if (type.equals("Delay")) {
                Timer.delay(time);
            }
            ;
        }
        m_executing = false;
    }

    public void save(JsonArray schedule) {
        // Playing will also officially save the file
        String id = String.format("%04d", new Random().nextInt(10000));

        // Convert to JSON string then save
        com.google.gson.Gson gson = new GsonBuilder().create();
        String json = gson.toJson(schedule, JsonArray.class);

        try {
            Files.write(Paths.get("/home/lvuser/nyads/" + id + ".json"), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Saved as " + id + ".json");
    }

    public JsonArray load(String path) {
        try {
            // Load file into a String
            String pathData = new String(Files.readAllBytes(Paths.get("/home/lvuser/" + path)));
            Gson gson = new Gson();

            // Put the actions into the Array
            return gson.fromJson(pathData, JsonArray.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new JsonArray();
        }
    }
}