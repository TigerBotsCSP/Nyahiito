/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
//import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import frc.robot.commands.TurnInPlaceCommand;
import frc.robot.subsystems.ChassisSubsystem;
import frc.robot.subsystems.ClimberSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.ShooterFeederSubsystem;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.StartEndCommand;

/**
 * This class is where the bulk of the robot should be declared.  Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls).  Instead, the structure of the robot
 * (including subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainerFinal {
  // The robot's subsystems
  private final ChassisSubsystem m_ChassisSubsystem = new ChassisSubsystem();
  private final IntakeSubsystem m_IntakeSubsystem = new IntakeSubsystem();
  private final ShooterFeederSubsystem m_ShooterFeederSubsystem = new ShooterFeederSubsystem();
  private final ClimberSubsystem m_ClimberSubsystem = new ClimberSubsystem();

  // Controller Definitions
  private final XboxController driver = new XboxController(0);
  private final XboxController operator = new XboxController(1);

  /**
   * The container for the robot.  Contains subsystems, OI devices, and commands.
   */
  public RobotContainerFinal() {
    // The Drive Command
    m_ChassisSubsystem.setDefaultCommand(new RunCommand(()-> 
      m_ChassisSubsystem.drive(driver.getLeftY(), -driver.getLeftX()), 
      m_ChassisSubsystem));

    // Intake Motor Command
    m_IntakeSubsystem.setDefaultCommand(new RunCommand(() -> {
      m_IntakeSubsystem.stopIntake();
    }, m_IntakeSubsystem));

    m_ShooterFeederSubsystem.setDefaultCommand(new RunCommand(() -> {
      if (operator.getRightTriggerAxis() > 0.05) {
        m_ShooterFeederSubsystem.setFeed(-operator.getRightTriggerAxis());
        m_ShooterFeederSubsystem.setAntiJam(-operator.getRightTriggerAxis());
      } else {
        m_ShooterFeederSubsystem.setFeed(operator.getLeftTriggerAxis());
        m_ShooterFeederSubsystem.setAntiJam(operator.getLeftTriggerAxis());
      }
    }, m_ShooterFeederSubsystem));

    // Climber Subsystem Default
    m_ClimberSubsystem.setDefaultCommand(new RunCommand(() -> {
      m_ClimberSubsystem.climb(operator.getLeftY());
    }, m_ClimberSubsystem));

    // Configure the button bindings
    configureButtonBindings();
  }

  /**
   * Use this method to define your button->command mappings.  Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a
   * {@link edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {
    // Driver Manual Override
    new JoystickButton(driver, Constants.Control.kXButton)
      .whenPressed(new InstantCommand(
        ()-> Constants.Control.kManualOverride = !Constants.Control.kManualOverride)
        );

    // Controls for the intake
    new JoystickButton(driver, Constants.Control.kYButton)
      .whenPressed(new InstantCommand(() -> m_IntakeSubsystem.changeLock(), m_IntakeSubsystem));

    new JoystickButton(driver, Constants.Control.kAButton)
      .whileHeld(new InstantCommand(() -> m_IntakeSubsystem.setIntake(1), m_IntakeSubsystem));
    new JoystickButton(driver, Constants.Control.kBButton)
      .whileHeld(new InstantCommand(() -> m_IntakeSubsystem.setIntake(-1), m_IntakeSubsystem));

    new JoystickButton(operator, Constants.Control.kYButton)
      .whenPressed(() -> {
        m_ChassisSubsystem.gyroReset();
        m_ChassisSubsystem.encoderReset();
      });
 }

}