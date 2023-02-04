// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;

import java.io.IOException;

public class Robot extends TimedRobot {
  // Vision vision = new Vision();

  @Override
  public void robotInit() {
    // Create a WebSocket factory. The timeout value remains 0.
    WebSocketFactory factory = new WebSocketFactory();

    // Create a WebSocket with a socket connection timeout value.
    WebSocket ws;
    try {
      ws = factory.createSocket("ws://limelight.local:5806", 5000);

      ws.addListener(new WebSocketAdapter() {
        @Override
        public void onTextMessage(WebSocket websocket, String message) throws Exception {
          if (message.contains("{\"fID\":29")) { // Temp solution
            System.out.println("Found 29.");
          }
        }
      });
      ws.connect();

    } catch (IOException e) {
      e.printStackTrace();
    } catch (WebSocketException e) {
      e.printStackTrace();
    }
  }

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    NetworkTableInstance instance = NetworkTableInstance.getDefault();
    instance.setServerTeam(0);
    NetworkTable table = instance.getTable("limelight");
    NetworkTableEntry tv = table.getEntry("tid");

    double value = tv.getDouble(59.0);
    System.out.println("Value: " + value);

    // if (vision.tagDetected(0)) {
    // System.out.println("Tag 0 Detected");
    // }

  }

  /** This function is called once each time the robot enters test mode. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
  }
}
