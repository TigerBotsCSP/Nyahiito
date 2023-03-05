// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;

import java.io.IOException;
import java.util.ArrayList;

public class Robot extends TimedRobot {
  Drive m_drive = new Drive();
  Arm m_arm = new Arm();
  Limelight m_limeLight = new Limelight();
  Encoders m_encoders = new Encoders();

  Field2d m_field = new Field2d();

  ArrayList<ArrayList<Double>> m_actions = new ArrayList<>(3);
  int index = 0;

  @Override
  public void robotInit() {
    m_limeLight.startLimelight();
  }

  @Override
  public void teleopInit() {
    m_actions.clear();
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    // Drive functionality
    if (Math.abs(m_drive.m_joystick.getY()) < .3) {
      double joystickX = m_drive.m_joystick.getX();
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
    double armSpeed = Math.max(Math.min(m_drive.m_controller.getRightY(), 0.6), -0.6);
    m_arm.setOrientation(armSpeed);

    // Arm length
    if (m_drive.m_controller.getPOV() == 0) {
      m_arm.setLength(.6);
    } else if (m_drive.m_controller.getPOV() == 180) {
      m_arm.setLength(-.6);
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

    data.add(m_drive.m_joystick.getY()); // Robot forward
    data.add(m_drive.m_joystick.getX()); // Robot rotation
    data.add(m_drive.m_controller.getRightY()); // Arm rotation
    data.add((double) m_drive.m_controller.getPOV()); // Arm length

    // Push the data
    m_actions.add(data);

    m_encoders.pushPeriodic();
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
        if (Math.abs(-m_actions.get(index).get(0)) < .3) {
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
        double armSpeed = Math.max(Math.min(m_actions.get(index).get(2), 0.6), -0.6);
        m_arm.setOrientation(armSpeed);

        // Arm length
        if (m_actions.get(index).get(3) == 0) {
          m_arm.setLength(.6);
        } else if (m_actions.get(index).get(3) == 180) {
          m_arm.setLength(-.6);
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