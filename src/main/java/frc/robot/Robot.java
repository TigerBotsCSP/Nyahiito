// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.Gson;

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

    // TODO: Move this crap to teleopPeriodic

    // Drive
    if (Math.abs(m_drive.m_controller.getLeftY()) < .3) {
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

  boolean firstTeleop = true;

  @Override
  public void teleopInit() {
    // Simulation - Save current
    if (!firstTeleop) {
      Gson gson = new Gson();
      String json = gson.toJson(m_actions);
      String fileName = String.format("%04d", new Random().nextInt(10000));

      try (FileWriter file = new FileWriter("c:/users/admin/desktop/nyahiito/" + fileName + ".json")) {
        file.write(json);
        System.out.println("Saved as " + fileName + ".");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    // Simulation - Clear previous
    m_actions.clear();

    // Start APS
    firstTeleop = false;
  }

  @Override
  public void teleopPeriodic() {
    m_drive.toggleDrive(true);

    ArrayList<Double> data = new ArrayList<Double>();
    data.add(m_drive.m_controller.getLeftY()); // Robot forward
    data.add(m_drive.m_controller.getLeftX()); // Robot rotation
    data.add(m_drive.m_controller.getRightY()); // Arm rotation
    data.add((double) m_drive.m_controller.getPOV()); // Arm length
    data.add(m_drive.m_controller.getBButton() ? 1.0 : 0.0); // Arm intake

    m_actions.add(data);

    m_encoders.pushPeriodic();

    // Simulator, not accurate
    if (true) {
      robotX += m_drive.m_controller.getLeftX() / 10;
      robotY += -m_drive.m_controller.getLeftY() / 10;
      m_field.setRobotPose(robotX, robotY, new Rotation2d(0, 0));
    }
  }

  // *Autonomous Mode*
  @Override
  public void autonomousPeriodic() {
    try {
      ArrayList<Double> data = m_actions.get(index);

      // Robot forward RR AR AL AI
      m_drive.toggleDrive(data.get(0), data.get(1));

      // Simulator, not accurate
      if (true) {
        // ! robotX just moves the robot on the 2D X-axis. it doesn't rotate like the actual robot.
        // * Edit the 10s so it'll match the actual robot's speed. Use encoders.
        robotX += data.get(1) / 10;
        robotY += -data.get(0) / 10;
        m_field.setRobotPose(robotX, robotY, new Rotation2d(0, 0));
      }

      // Drive
      if (Math.abs(data.get(0)) < .3) {
        double joystickX = data.get(1);
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
      m_arm.setOrientation(data.get(2));

      // Arm length
      if (data.get(3) == 0) {
        m_arm.setLength(1);
      } else if (data.get(3) == 180) {
        m_arm.setLength(-1);
      }

      // Intaker
      // ! Missense: in autonomous, this won't work b/c of how the data is recorded for the button.
      if (data.get(4) == 1.0) {
        m_arm.toggleIntaker();
      }

      index++;
    } catch (Exception e) {
      index = 0;
    }
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
