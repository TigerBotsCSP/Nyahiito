package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

public class Drive {
    private MotorController m_frontLeft;
    private MotorController m_rearLeft;
    private MotorControllerGroup m_left;

    private MotorController m_frontRight;
    private MotorController m_rearRight;
    private MotorControllerGroup m_right;
    
    private DifferentialDrive m_drive;
    
    public XboxController m_controller;

    public Joystick m_joystickLeft;
    public Joystick m_joystickRight;

    public MotorController m_armIO;
    public MotorController m_armRotate;


    Drive() {
        m_frontLeft = new PWMSparkMax(0);
        m_rearLeft = new PWMSparkMax(1);
        m_left = new MotorControllerGroup(m_frontLeft, m_rearLeft);
        
        m_frontRight = new PWMSparkMax(2);
        m_rearRight = new PWMSparkMax(3);
        m_right = new MotorControllerGroup(m_frontRight, m_rearRight);
      
        m_drive = new DifferentialDrive(m_left, m_right);
        
        m_controller = new XboxController(0);

        m_armIO = new PWMSparkMax(4);
        m_armRotate = new PWMSparkMax(5);

        // m_joystickLeft = new Joystick(0);
        // m_joystickRight = new Joystick(1);
    }
    
    public void toggleDrive() {
        // One-stick Mode
        if (m_controller.getLeftBumper()) {
            m_drive.tankDrive(-m_controller.getLeftY(), m_controller.getLeftY());
        } else {
            m_drive.tankDrive(-m_controller.getLeftY(), m_controller.getRightY());
        }
    }

    public void toggleDrive(double leftSpeed, double rightSpeed) {
        m_drive.tankDrive(leftSpeed, rightSpeed);
    }
}