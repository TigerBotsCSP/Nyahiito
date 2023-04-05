// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;

public class Robot extends TimedRobot {
  Drive m_drive = new Drive();
  Arm m_arm = new Arm();
  Limelight m_limeLight = new Limelight();
  Ncp m_ncp = new Ncp();
  Timer m_tTimer = new Timer();
  Timer m_aTimer = new Timer();

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

  @Override
  public void robotPeriodic() {
  }

  // ! Part of the inefficient intaker debounce
  float loops = 0;
  boolean intakeDebounce = false;
  boolean atTeleop = false;

  @Override
  public void teleopInit() {
    atTeleop = true;
  }

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

    // Arm Speed Toggle
    if (m_drive.m_controllerSide.getYButtonPressed()) {
      if (Constants.armSpeed == .6) {
        // Turbo
        Constants.armSpeed = .8;
      } else {
        // Normal
        Constants.armSpeed = .6;
      }
    }

    // Intaker
    if (m_drive.m_controllerSide.getAButton()) {
      m_arm.close();
    } else if (m_drive.m_controllerSide.getBButton()) {
      m_arm.open();
    }

    // Drive functionality
    if (m_drive.m_controller.getRightBumper()) {
      m_drive.rotateDrive(0, 0);
    } else {
      double speedX = Math.max(Math.min(-m_drive.m_controller.getLeftX(), Constants.driveSpeed), -Constants.driveSpeed);
      double speedY = Math.max(Math.min(-m_drive.m_controller.getLeftY(), Constants.driveSpeed), -Constants.driveSpeed);
      m_drive.rotateDrive(speedX, speedY);
    }

    // ? Drive arm functionality (main one is prioritized)
    // Arm rotation
    if (Math.abs(m_drive.m_controllerSide.getLeftY()) > .2) {
      double armSpeed = Math.max(Math.min(m_drive.m_controllerSide.getLeftY(), Constants.armSpeed),
          -Constants.armSpeed);
      m_arm.setOrientation(armSpeed);
    } else {
      double DarmSpeed = Math.max(Math.min(m_drive.m_controller.getRightY(), Constants.armSpeed),
          -Constants.armSpeed);
      m_arm.setOrientation(DarmSpeed);
    }

    // Arm length
    if (Math.abs(m_drive.m_controllerSide.getRightY()) > .2) {
      double armLength = Math.max(Math.min(m_drive.m_controllerSide.getRightY(), Constants.armSpeed),
          -Constants.armSpeed);
      m_arm.setLength(-armLength);
    } else {

      double DarmLength = Math.max(
          Math.min((m_drive.m_controller.getPOV() == 0 ? -.6 : (m_drive.m_controller.getPOV() == 180 ? .6 : 0)),
              Constants.armSpeed),
          -Constants.armSpeed);
      m_arm.setLength(-DarmLength);
    }

    /*
     * // * APS: Recording
     * if (m_ncp.apsMode.equals("record")) {
     * // Initialize the autonomous data for this frame
     * ArrayList<Double> data = new ArrayList<Double>();
     * 
     * data.add(m_drive.m_controller.getLeftY()); // Robot forward
     * data.add(m_drive.m_controller.getLeftX()); // Robot rotation
     * data.add(m_drive.m_controllerSide.getLeftY()); // Arm rotation
     * data.add(m_drive.m_controllerSide.getRightY()); // Arm length
     * data.add(m_drive.m_controllerSide.getBButton() ? 1.0 :
     * (m_drive.m_controllerSide.getAButton() ? 2.0 : 0)); // Intaker
     * data.add(m_drive.m_controller.getRightBumperPressed() ? 1.0 : 0.0); // Brakes
     * data.add(m_tTimer.get()); // Time
     * 
     * // Push the data
     * m_ncp.apsActions.add(data);
     * }
     * 
     * // * Lowest priority: NCP Publishing
     * m_ncp.publish(m_limeLight.getDetectedTags());
     */

    // ? All Lite stuff
    if (m_ncp.liteMode.equals("Record")) {
      ArrayList<Double> data = new ArrayList<Double>();

      data.add(m_drive.m_controller.getLeftY()); // Robot forward
      data.add(m_drive.m_controller.getLeftX()); // Robot rotation
      data.add(m_drive.m_controllerSide.getLeftY()); // Arm rotation
      data.add(m_drive.m_controllerSide.getRightY()); // Arm length
      data.add(m_drive.m_controllerSide.getBButton() ? 1.0 : (m_drive.m_controllerSide.getAButton() ? 2.0 : 0)); // Intaker
      data.add(m_drive.m_controller.getRightBumperPressed() ? 1.0 : 0.0); // Brakes
      data.add(m_tTimer.get()); // Time 6
      data.add((Double) RobotController.getBatteryVoltage()); // Voltage 7

      // Push the data
      m_ncp.apsActions.add(data);
    }

    /*if (m_ncp.liteMode == "Reverse") {
      // * Will play as auto but during teleop for ease
      // * Voltage and time is in the data, try out aligning times (don't)
      if (true) {
        try {
          for (int i = m_ncp.apsActions.size() - 1; i >= 0; i--) {
            if (m_ncp.apsActions.get(i).get(4) == 1) {
              m_arm.open();
            } else if (m_ncp.apsActions.get(i).get(4) == 2) {
              m_arm.close();
            }

            // Drive functionality
            if (m_ncp.apsActions.get(i).get(5) == 1) {
              m_drive.rotateDrive(0, 0);
            } else {
              double AspeedX = Math.max(Math.min(-m_ncp.apsActions.get(i).get(1), Constants.driveSpeed),
                  -Constants.driveSpeed);
              double AspeedY = Math.max(Math.min(-m_ncp.apsActions.get(i).get(0), Constants.driveSpeed),
                  -Constants.driveSpeed);
              m_drive.rotateDrive(AspeedX, AspeedY);
            }

            // Arm rotation
            double AarmSpeed = Math.max(Math.min(m_ncp.apsActions.get(i).get(2), Constants.armSpeed),
                -Constants.armSpeed);
            m_arm.setOrientation(AarmSpeed);

            // Arm length
            double AarmLength = Math.max(Math.min(m_ncp.apsActions.get(i).get(3), Constants.armSpeed),
                -Constants.armSpeed);
            m_arm.setLength(-AarmLength);
          }
          m_ncp.apsIndex = m_ncp.apsActions.size(); // Set index to end of list
        } catch (Exception e) {
          // m_ncp.apsIndex = 0;
          // ? Gyro
          if (m_ncp.apsPath == "/home/lvuser/9401.json") {
            double ms = 14000 - (thetime.get() * 1000);
            while (ms > 0) {
              ms = 14000 - (thetime.get() * 1000);
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
              // Here, we assume that a positive output means moving forward, and a negative
              // output means moving backward
              m_drive.straightDrive(-output * .8);

              Timer.delay(0.02); // Wait for 0.02 seconds to simulate a sample time
            }
          }
        }
      }
    }

    if (m_ncp.liteDoAuto) {
      // * Will play as auto but during teleop for ease
      // * Voltage and time is in the data, try out aligning times (don't)
      if (true) {
        try {
          if (m_ncp.apsActions.get(m_ncp.apsIndex).get(4) == 1) {
            m_arm.open();
          } else if (m_ncp.apsActions.get(m_ncp.apsIndex).get(4) == 2) {
            m_arm.close();
          }

          // Drive functionality
          if (m_ncp.apsActions.get(m_ncp.apsIndex).get(5) == 1) {
            m_drive.rotateDrive(0, 0);
          } else {
            double AspeedX = Math.max(Math.min(-m_ncp.apsActions.get(m_ncp.apsIndex).get(1), Constants.driveSpeed),
                -Constants.driveSpeed);
            double AspeedY = Math.max(Math.min(-m_ncp.apsActions.get(m_ncp.apsIndex).get(0), Constants.driveSpeed),
                -Constants.driveSpeed);
            m_drive.rotateDrive(AspeedX, AspeedY);
          }

          // Arm rotation
          double AarmSpeed = Math.max(Math.min(m_ncp.apsActions.get(m_ncp.apsIndex).get(2), Constants.armSpeed),
              -Constants.armSpeed);
          m_arm.setOrientation(AarmSpeed);

          // Arm length
          double AarmLength = Math.max(Math.min(m_ncp.apsActions.get(m_ncp.apsIndex).get(3), Constants.armSpeed),
              -Constants.armSpeed);
          m_arm.setLength(-AarmLength);

          m_ncp.apsIndex++;
        } catch (Exception e) {
          // m_ncp.apsIndex = 0;
          // ? Gyro
          if (m_ncp.apsPath == "/home/lvuser/9401.json") {
            double ms = 14000 - (thetime.get() * 1000);
            while (ms > 0) {
              ms = 14000 - (thetime.get() * 1000);
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
              // Here, we assume that a positive output means moving forward, and a negative
              // output means moving backward
              m_drive.straightDrive(-output * .8);

              Timer.delay(0.02); // Wait for 0.02 seconds to simulate a sample time
            }
          }
        }
      }
    }*/
  }

  // ! Part of the inefficient intaker debounce
  float aloops = 0;
  boolean aintakeDebounce = false;

  boolean ncpDoGyro = false;

  @Override
  public void autonomousInit() {
    m_aTimer.start();
  }

  // ? Autonomous timer, stops auto at 14.9s
  Timer thetime = new Timer();

  // Autonomous Mode
  @Override
  public void autonomousPeriodic() {
    if (!m_ncp.apsLoaded) {
      thetime.start();
      while (true) {
        m_ncp.apl("/home/lvuser/6017.json");
        break;
      }
    };

    if (true) {
      try {
        if (m_ncp.apsActions.get(m_ncp.apsIndex).get(4) == 1) {
          m_arm.open();
        } else if (m_ncp.apsActions.get(m_ncp.apsIndex).get(4) == 2) {
          m_arm.close();
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
        double armLength = Math.max(Math.min(m_ncp.apsActions.get(m_ncp.apsIndex).get(3), Constants.armSpeed),
            -Constants.armSpeed);
        m_arm.setLength(-armLength);

        m_ncp.apsIndex++;
      } catch (Exception e) {
        // m_ncp.apsIndex = 0;
        if (m_ncp.apsPath == "/home/lvuser/9401.json") {
          ncpDoGyro = true;
        }

        // ! Not used??
        if (m_ncp.apsPath == "/home/lvuser/9401.json") {
          double ms = 14900 - (thetime.get() * 1000);
          while (ms > 0) {
            ms = 14900 - (thetime.get() * 1000);
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
            // Here, we assume that a positive output means moving forward, and a negative
            // output means moving backward
            m_drive.straightDrive(-output * .8);

            Timer.delay(0.02); // Wait for 0.02 seconds to simulate a sample time
          }
        }
      }
    }
  }
}