// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import java.util.ArrayList;

import com.google.gson.*;
import java.io.*;

public class Robot extends TimedRobot {
  Drive m_drive = new Drive();
  Arm m_arm = new Arm();
  Limelight m_limeLight = new Limelight();
  Ncp m_ncp = new Ncp();

  ArrayList<ArrayList<Double>> m_actions = new ArrayList<>(3);
  int index = 0;

  @Override
  public void robotInit() {
    m_limeLight.startLimelight();
    m_ncp.core();
  }

  @Override
  public void teleopInit() {
    Gson gson = new Gson();

    try (BufferedReader br = new BufferedReader(new FileReader("/home/lvuser/p_0.json"))) {
      String line;
      while ((line = br.readLine()) != null) {
        ArrayList<Double> row = gson.fromJson(line, ArrayList.class);
        m_actions.add(row);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Autonomous loaded, have fun!");
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    // Drive functionality
    if (Math.abs(m_drive.m_controller.getLeftY()) < Constants.joystickDriftSafety) {
      double joystickX = m_drive.m_controller.getLeftX();
      // Rotation mode
      if (Math.abs(joystickX - (-1)) < Math.abs(joystickX - 1)) {
        m_drive.toggleDrive(0, joystickX);
      } else {
        m_drive.toggleDrive(joystickX, 0);
      }
    } else {
      // Forward mode
      m_drive.toggleDrive(true);
    }

    // Arm rotation
    double armSpeed = Math.max(Math.min(m_drive.m_controller.getRightY(), Constants.armSpeed), -Constants.armSpeed);
    m_arm.setOrientation(armSpeed);

    // Arm length
    if (m_drive.m_controller.getPOV() == 0) {
      m_arm.setLength(Constants.armSpeed);
    } else if (m_drive.m_controller.getPOV() == 180) {
      m_arm.setLength(-Constants.armSpeed);
    } else {
      m_arm.setLength(0);
    }

    // Emergency stop
    if (m_drive.m_controller.getBackButton()) {
      m_arm.setOrientation(0);
      m_arm.setLength(0);
      m_drive.toggleDrive(0, 0);
    }

    // Initialize the autonomous data for this frame
    ArrayList<Double> data = new ArrayList<Double>();

    data.add(m_drive.m_controller.getLeftY()); // Robot forward
    data.add(m_drive.m_controller.getLeftX()); // Robot rotation
    data.add(m_drive.m_controller.getRightY()); // Arm rotation
    data.add((double) m_drive.m_controller.getPOV()); // Arm length

    // Push the data
    m_actions.add(data);

    // * Lowest priority: NCP Publishing
    m_ncp.publish();
  }

  // Autonomous Mode
  @Override
  public void autonomousPeriodic() {
    try {
      if (index == m_actions.size() - 1) {
        // Reached the end, let's loop!
        index = 0;
      } else {
        // Drive functionality
        if (Math.abs(-m_actions.get(index).get(0)) < Constants.joystickDriftSafety) {
          double joystickX = -m_actions.get(index).get(1);
          // Rotation mode
          if (Math.abs(joystickX - (-1)) < Math.abs(joystickX - 1)) {
            m_drive.toggleDrive(0, joystickX);
          } else {
            m_drive.toggleDrive(joystickX, 0);
          }
        } else {
          // Forward mode
          m_drive.toggleDrive(-m_actions.get(index).get(0), m_actions.get(index).get(0));
        }

        // Arm rotation
        double armSpeed = Math.max(Math.min(m_actions.get(index).get(2), Constants.armSpeed), -Constants.armSpeed);
        m_arm.setOrientation(armSpeed);

        // Arm length
        if (m_actions.get(index).get(3) == Constants.armLengthOutPOV) {
          m_arm.setLength(Constants.armSpeed);
        } else if (m_actions.get(index).get(3) == Constants.armLengthInPOV) {
          m_arm.setLength(-Constants.armSpeed);
        } else {
          m_arm.setLength(0);
        }
        index++;
      }
    } catch (Exception e) {
      index = 0;
    }
  }
}