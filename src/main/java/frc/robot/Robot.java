// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.ADIS16448_IMU;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Robot extends TimedRobot {
  Drive m_drive = new Drive();
  Arm m_arm = new Arm();
  Limelight m_limeLight = new Limelight();

  Field2d m_field = new Field2d();

  @Override
  public void robotInit() {
    System.out.println("ee");
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

  /**
   * This function is called once each time the robot enters teleoperated mode.
   */
  @Override
  public void teleopInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
   /*m_drive.toggleDrive();

    NetworkTableInstance instance = NetworkTableInstance.getDefault();
    instance.setServerTeam(0);
    NetworkTable table = instance.getTable("limelight");
    NetworkTableEntry tv = table.getEntry("tid");

    double value = tv.getDouble(59.0);
    System.out.println("Value: " + value);*/
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
