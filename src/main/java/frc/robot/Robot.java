// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;

public class Robot extends TimedRobot {
  Drive m_drive = new Drive();
  Arm m_arm = new Arm();
  Limelight m_limeLight = new Limelight();
  Ncp m_ncp = new Ncp();
  // I2C m_gyro = new I2C(Port.kOnboard, 55);
  BNO055 m_gBno055 = new BNO055(Port.kOnboard, 0x29);
  private static BNO055 imu;
  private final double Kp = 0.05; // Proportional gain
  private final double Ki = 0.0; // Integral gain
  private final double Kd = 0.0; // Derivative gain
  private double integral = 0.0;
  private double previous_error = 0.0;
  private double setpoint = 0.0; // The desired angle

  @Override
  public void robotInit() {
    CameraServer.startAutomaticCapture(0);
    CameraServer.startAutomaticCapture(1);
    m_limeLight.startLimelight();
    m_ncp.core();
    imu = BNO055.getInstance(BNO055.opmode_t.OPERATION_MODE_IMUPLUS,
        BNO055.vector_type_t.VECTOR_EULER);
  }

  //Override
  public void robotxPeriodic() {
    double angle = imu.getVector()[1];

    // Calculate the error and update the integral
    double error = setpoint - angle;
    integral += error * 0.02; // Sample time of 0.02 seconds
    double derivative = (error - previous_error) / 0.02;
    previous_error = error;

    // Calculate the output using the PID formula
    double output = Kp * error + Ki * integral + Kd * derivative;

    // Limit the output to a reasonable range (e.g. -1 to 1 for motor speed)
    output = Math.max(-1, Math.min(1, output));

    // Move the robot forward/backward based on the output
    // Here, we assume that a positive output means moving forward, and a negative output means moving backward
    m_drive.straightDrive(-output);

    Timer.delay(0.02); // Wait for 0.02 seconds to simulate a sample time
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

    // 180 Spin
    double spin = m_drive.m_controller.getLeftTriggerAxis();
    m_drive.rotateDrive(0, spin);

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
    double armLength = Math.max(Math.min(m_drive.m_controllerSide.getRightY(), Constants.armSpeed),
        -Constants.armSpeed);
    m_arm.setLength(-armLength);

    // * APS: Recording
    if (m_ncp.apsMode.equals("record")) {
      // Initialize the autonomous data for this frame
      ArrayList<Double> data = new ArrayList<Double>();

      data.add(m_drive.m_controller.getLeftY()); // Robot forward
      data.add(m_drive.m_controller.getLeftX()); // Robot rotation
      data.add(m_drive.m_controllerSide.getLeftY()); // Arm rotation
      data.add(m_drive.m_controllerSide.getRightY()); // Arm length
      data.add(m_drive.m_controllerSide.getBButtonPressed() ? 1.0 : 0.0); // Intaker
      data.add(m_drive.m_controller.getRightBumperPressed() ? 1.0 : 0.0); // Brakes

      // Push the data
      m_ncp.apsActions.add(data);
    }

    // * Lowest priority: NCP Publishing
    m_ncp.publish(m_limeLight.getDetectedTags());
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
        if (m_ncp.apsActions.get(m_ncp.apsIndex).get(4) == 1) {
          m_arm.toggleIntaker();
        }

        // Drive functionality
        if (m_ncp.apsActions.get(m_ncp.apsIndex).get(5) == 1) {
          m_drive.rotateDrive(0, 0);
        } else {
          double speedX = Math.max(Math.min(-m_ncp.apsActions.get(m_ncp.apsIndex).get(1), Constants.driveSpeed),
              -Constants.driveSpeed);
          double speedY = Math.max(Math.min(-m_ncp.apsActions.get(m_ncp.apsIndex).get(0), Constants.driveSpeed),
              -Constants.driveSpeed);
          m_drive.rotateDrive(speedX, speedY);
        }

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