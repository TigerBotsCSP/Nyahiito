// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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
    RobotContainer.m_intaker.setSpeed(-Constants.intakerSpeed);
  }

  @Override
  public void teleopPeriodic() {
    if (RobotContainer.m_nyads.m_executing)
      return;

    // Toggle Joysticks
    if (RobotContainer.m_driveController.getRightStickButtonPressed()) {
      Constants.useBothJoysticks = !Constants.useBothJoysticks;
    }

    // Drive Speed Toggle``
    if (RobotContainer.m_driveController.getLeftBumperPressed()) {
      if (Constants.driveSpeed == .6) {
        Constants.driveSpeed = .8;
      } else {
        Constants.driveSpeed = .6;
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
      RobotContainer.m_intaker.togglePusher();
    }

    if (RobotContainer.m_intaker.getCurrent() > Constants.currentLimit && RobotContainer.m_intaker.m_in) {
      RobotContainer.m_intaker.setSpeed(-0.0);
    } else if (RobotContainer.m_driveController.getYButtonPressed()) {
      RobotContainer.m_intaker.toggleMotor();
    }

    // Drive Mode Toggle
    if (RobotContainer.m_driveController.getXButtonPressed()) {
      RobotContainer.m_drive.toggleMode();
    }

    // Drive functionality
    if (RobotContainer.m_driveController.getRightBumper()) {
      RobotContainer.m_drive.straightDrive(0);
    } else {
      if (Constants.useBothJoysticks) {
        double speedX = RobotContainer.limit(RobotContainer.m_driveController.getRightX(), Constants.driveSpeed);
        double speedY = RobotContainer.limit(-RobotContainer.m_driveController.getLeftY(), Constants.driveSpeed);
        RobotContainer.m_drive.rotateDrive(speedX, speedY);
      } else {
        double speedX = RobotContainer.limit(-RobotContainer.m_driveController.getLeftX(), Constants.driveSpeed);
        double speedY = RobotContainer.limit(-RobotContainer.m_driveController.getLeftY(), Constants.driveSpeed);
        RobotContainer.m_drive.rotateDrive(speedX, speedY);
      }
    }

    // Arm rotation
    double armSpeed = RobotContainer.limit(-RobotContainer.m_armController.getLeftY(), Constants.armSpeed);
    RobotContainer.m_arm.setOrientation(-armSpeed);

    // Arm length
    double armLength = RobotContainer.limit(-RobotContainer.m_armController.getRightY(), Constants.armSpeed);
    RobotContainer.m_arm.setLength(armLength);

    // Stop intaker
    if (RobotContainer.m_driveController.getStartButton()) {
      RobotContainer.m_intaker.setSpeed(0);
    }
  }

  @Override
  public void autonomousInit() {
    RobotContainer.m_nyads.execute(RobotContainer.m_nyads.load(Constants.Auto.autoPath));
  }

  @Override
  public void autonomousPeriodic() {
  }
}