// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import com.google.gson.Gson;

import edu.wpi.first.math.geometry.Rotation2d;

public class Robot extends TimedRobot {

  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    // Instantiate our RobotContainer.  This will perform all our button bindings, and put our
    // autonomous chooser on the dashboard.
  m_robotContainer = new RobotContainer();
    
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    // Runs the Scheduler.  This is responsible for polling buttons, adding newly-scheduled
    // commands, running already-scheduled commands, removing finished or interrupted commands,
    // and running subsystem periodic() methods.  This must be called from the robot's periodic
    // block in order for anything in the Command-based framework to work.
    CommandScheduler.getInstance().run();
  }

  /**
   * This function is called once each time the robot enters Disabled mode.
   */
  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  /**
   * This autonomous runs the autonomous command selected by your {@link RobotContainer} class.
   */
  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    // schedule the autonomous command (example)
    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    // This makes sure that the autonomous stops running when
    // teleop starts running. If you want the autonomous to
    // continue until interrupted by another command, remove
    // this line or comment it out.
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }

    m_robotContainer.teleopInit();
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testInit() {
    // Cancels all running commands at the start of test mode.
    CommandScheduler.getInstance().cancelAll();
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {}

//---------------------------------------------------------------------------------
//---------------------------------------------------------------------------------
//---------------------------------------------------------------------------------

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

  // ? Start teleop with APS mode
  boolean configUseAPS = true;

  // ? Start with specific pathway
  // * APS must be off
  String configPathwayFile = "C:/users/admin/desktop/nyahiito/0000.json";

  // * Teleop APS Loader
  // TODO: Move this + any APS functions to its own class
  public void apSysLoad() {
    Gson gson = new Gson();
    ArrayList<ArrayList<Double>> data = new ArrayList<>();

    // TODO: Test this.
    try (BufferedReader br = new BufferedReader(new FileReader(configPathwayFile))) {
      String line;
      while ((line = br.readLine()) != null) {
        ArrayList<Double> row = gson.fromJson(line, ArrayList.class);
        data.add(row);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    System.out.println("Data loaded. Have fun in autonomous!");
  }

  @Override
  public void teleopPeriodic() {
    if (configUseAPS) {
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
    } else {
      apSysLoad();
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
        // ! robotX just moves the robot on the 2D X-axis. it doesn't rotate like the
        // actual robot.
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
      // ! Missense: in autonomous, this won't work b/c of how the data is recorded
      // for the button.
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
