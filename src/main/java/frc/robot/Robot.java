// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.ADIS16448_IMU;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import java.util.ArrayList;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

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
    // Tag detection
    if (m_limeLight.tagDetected(0)) {
      System.out.println("Tag 0 detected!");
    }

    // Arms
    if (m_drive.m_controller.getAButton()) {
      m_arm.toggleArm();
    }

    // arm in/out
    if (m_drive.m_controller.getLeftBumperPressed()) {
      
      m_drive.m_armIO.setVoltage(4);
    }
    else if (m_drive.m_controller.getRightBumperPressed()) {
      m_drive.m_armIO.setVoltage(-4);
    }
    else {
      m_drive.m_armIO.setVoltage(0);
    }

    if (m_drive.m_controller.getXButtonPressed()) {
      m_drive.m_armRotate.setVoltage(4);
    }
    else if (m_drive.m_controller.getYButtonPressed()) {
      m_drive.m_armRotate.setVoltage(-4);
    }
    else {
      m_drive.m_armRotate.setVoltage(0);
    }

    // Show field on dashboard
    SmartDashboard.putData(m_field);

    // Field Simulation (WIP)
    /*
     * ADIS16448_IMU m_gyro = new ADIS16448_IMU();
     * DifferentialDriveOdometry m_odometry = new DifferentialDriveOdometry(
     * m_gyro.getRotation2d(),
     * m_leftEncoder.getDistance(), m_rightEncoder.getDistance(),
     * new Pose2d(5.0, 13.5, new Rotation2d()));
     * 
     * m_field.setRobotPose(m_odometry.getPoseMeters());
     */
  }

  double robotX = 1;
  double robotY = 1;
  Rotation2d d = new Rotation2d(0, 0);
  /** This function is called once each time the robot enters teleoperated mode. */
  @Override
  public void teleopInit() {
    m_actions.clear();
    m_field.setRobotPose(m_drive.m_controller.getLeftX(), m_drive.m_controller.getLeftY(), d);
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
   m_drive.toggleDrive();

   ArrayList<Double> data = new ArrayList<Double>();
   data.add(m_drive.m_controller.getLeftY());
   data.add(m_drive.m_controller.getRightY());
   m_actions.add(data);

   m_encoders.pushPeriodic();

   // Simulator, not accurate
   if (true) {
    robotX += m_drive.m_controller.getLeftX() / 10;
    robotY += -m_drive.m_controller.getLeftY() / 10;
      m_field.setRobotPose(robotX, robotY, d);
   }

    /* NetworkTableInstance instance = NetworkTableInstance.getDefault();
    instance.setServerTeam(0);
    NetworkTable table = instance.getTable("limelight");
    NetworkTableEntry tv = table.getEntry("tid");

    double value = tv.getDouble(59.0);
    System.out.println("Value: " + value); */
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
