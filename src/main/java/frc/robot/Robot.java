// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
  public void robotPeriodic() {
    // Show field on dashboard
    SmartDashboard.putData(m_field);
  }

  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    m_actions.clear();
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
   m_drive.toggleDrive(true);

   // Drive functionality
   if (Math.abs(m_drive.m_joystick.getY()) < .3) {
    double joystickX = m_drive.m_joystick.getX();
    // Rotation mode
     if (Math.abs(joystickX - (-1)) < Math.abs(joystickX - 1)) {
       m_drive.toggleDrive(joystickX, 0);
    } else {
       m_drive.toggleDrive(0, joystickX);
     }
  } else {
    // Forward mode
    m_drive.toggleDrive(true);
  }

   ArrayList<Double> data = new ArrayList<Double>();

   data.add(m_drive.m_joystick.getY()); // Robot forward
   data.add(m_drive.m_joystick.getX()); // Robot rotation

   // Push the data
   m_actions.add(data);

   m_encoders.pushPeriodic();
  }

  // Autonomous Mode
  @Override
  public void autonomousPeriodic() {
    try {
      // Drive functionality
      if (Math.abs(-m_actions.get(index).get(0)) < .3) {
        double joystickX = -m_actions.get(index).get(1);
        // Rotation mode
        if (Math.abs(joystickX - (-1)) < Math.abs(joystickX - 1)) {
          m_drive.toggleDrive(joystickX, 0);
        } else {
          m_drive.toggleDrive(0, joystickX);
        }
      } else {
        // Forward mode
        m_drive.toggleDrive(-m_actions.get(index).get(0), m_actions.get(index).get(0));
      }

      index++;
    } catch (Exception e) {
      index = 0;
    }
  }
}