// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {  
  @Override
  public void robotInit() {
    RobotContainer.init();
  }

  @Override
  public void robotPeriodic() {
    RobotContainer.updateData();
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    if (RobotContainer.m_nyads.executing) return;

    // Drive Speed Toggle``
    if (RobotContainer.m_driveController.getBButtonPressed()) {
      if (Constants.driveSpeed == .7) {
        Constants.driveSpeed = 1;
      } else {
        Constants.driveSpeed = .7;
      }
    }

    // Arm Speed Toggle
    if (RobotContainer.m_armController.getYButtonPressed()) {
      if (Constants.armSpeed == .6) {
        Constants.armSpeed = .8;
      } else {
        Constants.armSpeed = .6;
      }
    }

    // Intaker
    if (RobotContainer.m_driveController.getAButton()) {
      RobotContainer.m_arm.close();
    } else if (RobotContainer.m_driveController.getBButton()) {
      RobotContainer.m_arm.open();
    }

    // Drive functionality
    if (RobotContainer.m_driveController.getRightBumper()) {
      RobotContainer.m_drive.rotateDrive(0, 0);
    } else {
      double speedX = RobotContainer.limit(-RobotContainer.m_driveController.getLeftX(), Constants.driveSpeed);
      double speedY = RobotContainer.limit(-RobotContainer.m_driveController.getLeftY(), Constants.driveSpeed);
      RobotContainer.m_drive.rotateDrive(speedX, speedY);
    }

    // Arm rotation
    double armSpeed = RobotContainer.limit(-RobotContainer.m_armController.getLeftY(), Constants.armSpeed);
    RobotContainer.m_arm.setOrientation(armSpeed);

    // Arm length
    double armLength = RobotContainer.limit(-RobotContainer.m_armController.getRightY(), Constants.armSpeed);
    RobotContainer.m_arm.setLength(-armLength);
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
    RobotContainer.m_nyads.execute(RobotContainer.m_nyads.load(Constants.Auto.autoPath));
  }
}