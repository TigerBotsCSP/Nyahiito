// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import java.util.ArrayList;

import edu.wpi.first.math.geometry.Rotation2d;

public class Robot extends TimedRobot {
  Drive m_drive = new Drive();
  Arm m_arm = new Arm();
  Limelight m_limeLight = new Limelight();
  Encoders m_encoders = new Encoders();

  Field2d m_field = new Field2d();

  ArrayList<ArrayList<Double>> m_actions = new ArrayList<>(3);

  int index = 0;
  double robotX = 1;
  double robotY = 1;

  @Override
  public void robotInit() {
    m_limeLight.startLimelight();
  }

  @Override
  public void robotPeriodic() {
    // Tag detection
    if (m_limeLight.tagDetected(0)) {
      System.out.println("Tag 0 detected!");
    }

    // Drive
    if (Math.abs(m_drive.m_controller.getLeftY()) > .3) {
      double joystickX = m_drive.m_controller.getLeftX();
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

    // Arm rotation
    m_arm.setOrientation(m_drive.m_controller.getRightY());

    // Arm length
    if (m_drive.m_controller.getPOV() == 0) {
      m_arm.setLength(1);
    } else if (m_drive.m_controller.getPOV() == 180) {
      m_arm.setLength(-1);
    }

    // Intaker
    if (m_drive.m_controller.getBButton()) {
      m_arm.toggleIntaker();
    }

    // Show field on dashboard
    SmartDashboard.putData(m_field);
  }

  @Override
  public void teleopInit() {
    m_actions.clear();
    m_field.setRobotPose(m_drive.m_controller.getLeftX(), m_drive.m_controller.getLeftY(), new Rotation2d(0, 0));
  }

  @Override
  public void teleopPeriodic() {
   m_drive.toggleDrive(true);

   ArrayList<Double> data = new ArrayList<Double>();
   data.add(m_drive.m_controller.getLeftY());
   data.add(m_drive.m_controller.getRightY());
   m_actions.add(data);

   m_encoders.pushPeriodic();

   // Simulator, not accurate
   if (true) {
    robotX += m_drive.m_controller.getLeftX() / 10;
    robotY += -m_drive.m_controller.getLeftY() / 10;
      m_field.setRobotPose(1, 1,  new Rotation2d(0, 0));
   }
  }

  // Autonomous Mode
  @Override
  public void autonomousPeriodic() {
    try {
      m_drive.toggleDrive(m_actions.get(index).get(0), m_actions.get(index).get(1));
      System.out.println(m_actions.get(index).get(0));
      index++;
    } catch (Exception e) {
      index = 0;
    }
    //m_drive.toggleDrive(.5, -.5);
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
