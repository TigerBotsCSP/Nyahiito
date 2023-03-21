// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Arrays;
import java.util.Collections;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends TimedRobot {
  Drive m_drive = new Drive();
  Arm m_arm = new Arm();
  Limelight m_limeLight = new Limelight();
  Ncp m_ncp = new Ncp();

  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture(0);
    CameraServer.startAutomaticCapture(1);
    
    m_limeLight.startLimelight();
    // m_ncp.core();
  }

  @Override
  public void robotPeriodic() {
    // m_ncp.log(m_ncp.apsMode);
  }

  // ! Part of the inefficient intaker debounce
  float loops = 0;
  boolean intakeDebounce = false;

  /** This function is called periodically during teleoperated mode. */
  @Override
  public void teleopPeriodic() {
    // Drive Speed Toggle
    if (m_drive.m_controller.getBButtonPressed()) {
      if (Constants.driveSpeed == .7) {
        // Turbo
        Constants.driveSpeed = .9;
      } else {
        // Normal
        Constants.driveSpeed = .7;
      }
    }
    
    // Intaker
    if (m_drive.m_controllerSide.getBButtonPressed()) {
      m_arm.toggleIntaker(); 
    }

    // Drive functionality
    if (m_drive.m_controller.getRightBumper()) {
      m_drive.rotateDrive(0, 0);
    } else {
      double speedX = Math.max(Math.min(-m_drive.m_controller.getLeftX(), Constants.driveSpeed), -Constants.driveSpeed);
      double speedY = Math.max(Math.min(-m_drive.m_controller.getLeftY(), Constants.driveSpeed), -Constants.driveSpeed);
      m_drive.rotateDrive(speedX, speedY);
    }

    // Arm rotation
    double armSpeed = Math.max(Math.min(m_drive.m_controllerSide.getLeftY(), Constants.armSpeed), -Constants.armSpeed);
    m_arm.setOrientation(armSpeed);

    // Arm length
    double armLength = Math.max(Math.min(m_drive.m_controllerSide.getRightY(), Constants.armSpeed), -Constants.armSpeed);
    m_arm.setLength(-armLength);

    // * APS: Recording
    /* if (m_ncp.apsMode.equals("record")) {
      //= Initialize the autonomous data for this frame
      ArrayList<Double> data = new ArrayList<Double>();

      data.add(m_drive.m_controller.getLeftY()); // Robot forward
      data.add(m_drive.m_controller.getLeftX()); // Robot rotation
      data.add(m_drive.m_controllerSide.getLeftY()); // Arm rotation
      data.add(m_drive.m_controllerSide.getRightY()); // Arm length
      data.add(m_drive.m_controllerSide.getBButton() ? 1.0 : 0.0); // Intaker

      // Push the data
      m_ncp.apsActions.add(data);
    }

    // * Lowest priority: NCP Publishing
    m_ncp.publish(m_limeLight.getDetectedTags());
    */
  }

  // ! Part of the inefficient intaker debounce
  float aloops = 0;
  boolean aintakeDebounce = false;

  // Autonomous Mode
  private double startTime;

  @Override
  public void autonomousInit() {
    startTime = Timer.getFPGATimestamp();
  }

  // Autonomous Mode
  @Override
  public void autonomousPeriodic() {
    if (!m_ncp.apsLoaded) {
      while (true) {
        boolean tagsSide = !Collections.disjoint(m_limeLight.getDetectedTags(), Arrays.asList(1, 3, 6, 8));
        if (tagsSide) {
          m_ncp.apl("/home/lvuser/side2.json");
          break;
        } else if (!Collections.disjoint(m_limeLight.getDetectedTags(), Arrays.asList(2, 7))) {
          // Middle
          m_ncp.apl("/home/lvuser/path_middle.json");
          break;
        }
  
        // ! Pathway Roulette: After one second of no detection, put a cone in and pray.
        if (Timer.getFPGATimestamp() - startTime > 1) {
          m_ncp.apl("/home/lvuser/path_middle.json");
          break;
        }
  
        Timer.delay(.15);
      }
    }

    if (true) {
      try {
        // * Intaker debounce
        // ! Not an efficient debounce, recreate this
        if (aintakeDebounce) {
          aloops++;
          if (aloops > 25) {
            aintakeDebounce = false;
            aloops = 0;
          }
        } else if (m_ncp.apsActions.get(m_ncp.apsIndex).get(4) == 1) {
          aintakeDebounce = true;
          m_arm.toggleIntaker();
        }

        // Drive functionality
        m_drive.rotateDrive(-m_ncp.apsActions.get(m_ncp.apsIndex).get(1), -m_ncp.apsActions.get(m_ncp.apsIndex).get(0));

        // Arm rotation
        double armSpeed = Math.max(Math.min(m_ncp.apsActions.get(m_ncp.apsIndex).get(2), Constants.armSpeed),
            -Constants.armSpeed);
        m_arm.setOrientation(armSpeed);

        // Arm length
        if (m_ncp.apsActions.get(m_ncp.apsIndex).get(3) == Constants.armLengthOutPOV) {
          m_arm.setLength(Constants.armSpeed);
        } else if (m_ncp.apsActions.get(m_ncp.apsIndex).get(3) == Constants.armLengthInPOV) {
          m_arm.setLength(-Constants.armSpeed);
        } else {
          m_arm.setLength(0);
        }
        m_ncp.apsIndex++;
      } catch (Exception e) {
       // m_ncp.apsIndex = 0;
      }
    }
  }
}