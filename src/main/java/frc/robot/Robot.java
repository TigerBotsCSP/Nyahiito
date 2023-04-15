// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {
  @Override
  public void robotInit() {
    RobotContainer.m_gyro.init();

    RobotContainer.init();
  }

  @Override
  public void robotPeriodic() {
    RobotContainer.updateData();

    // Reset Gyro
    if (RobotContainer.m_driveController.getBackButtonPressed()) {
      RobotContainer.m_gyro.reset();
    }
  }

  @Override
  public void teleopInit() {
  }

  @Override
  public void teleopPeriodic() {
    if (RobotContainer.m_nyads.m_executing)
      return;

    // Drive Speed Toggle``
    if (RobotContainer.m_driveController.getYButtonPressed()) {
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

    // Arm Intaker
    if (RobotContainer.m_armController.getAButton()) {
      RobotContainer.m_arm.close();
    } else if (RobotContainer.m_armController.getBButton()) {
      RobotContainer.m_arm.open();
    }

    // Main Intaker
    if (RobotContainer.m_driveController.getBButtonPressed()) {
      RobotContainer.m_intaker.toggle();
    }

    // Drive Mode Toggle
    if (RobotContainer.m_driveController.getLeftBumperPressed()) {
      RobotContainer.m_drive.toggleMode();
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