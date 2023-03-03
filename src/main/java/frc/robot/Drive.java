package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj.SPI;
import frc.robot.Constants;
import frc.robot.util.RPMMonitor;    

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;

public class Drive {

    private final Encoder rightEncoder;
    private final Encoder leftEncoder;
  
    private final AHRS gyro;
    
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


        // Assigns encoders to their ports
        leftEncoder = new Encoder(Constants.EncoderPorts.kLeftEncoderA, Constants.EncoderPorts.kLeftEncoderB, false);
        rightEncoder = new Encoder(Constants.EncoderPorts.kRightEncoderA, Constants.EncoderPorts.kRightEncoderB, true);

        // Sets the Gyro Port
        gyro = new AHRS(SPI.Port.kMXP);
        m_drive.setMaxOutput(1);
  }

  public void drive(double speed, double angle) {
    m_drive.arcadeDrive(speed, angle);
  }

  public void sideDrive(double leftMotorSpeed, double rightMotorSpeed) {
    m_left.set(leftMotorSpeed);
    m_right.set(rightMotorSpeed);
  }
    
    public void toggleDrive(boolean oneStickMode) {
        // One-stick Mode
        if (oneStickMode) {
            m_drive.tankDrive(-m_controller.getLeftY(), m_controller.getLeftY());
        } else {
            m_drive.tankDrive(-m_controller.getLeftY(), m_controller.getRightY());
        }
    }

    public void toggleDrive(double leftSpeed, double rightSpeed) {
        m_drive.tankDrive(leftSpeed, rightSpeed);
    }
    
    public double gyroAngle(){
        return gyro.getAngle();
      }
    
    public void gyroReset(){
      gyro.reset();
    }

    public double gyroYaw() {
      return gyro.getYaw();
    }
    
    // @Override
    // public void periodic() {
    //   rpm.monitor(encoderAverage());

    //   // Puts a Number of variables to SmartDashboard
    //   SmartDashboard.putNumber("Gyro", gyro.getAngle());
    //   SmartDashboard.putNumber("Left Encoder", leftEncoder.get());
    //   SmartDashboard.putNumber("Right Encoder", rightEncoder.get());
    //   SmartDashboard.putNumber("Chassis RPM", getRotationsPerMinute());
    //   SmartDashboard.putNumberArray("ChassisDisplacement", new double[]{gyro.getDisplacementX() * 39.37, gyro.getDisplacementY() * 39.37});
    // }

    public double encoderAverage() {
      return (leftEncoder.get() + rightEncoder.get()) / 2;
    }

    public double getDistance(){
      return (leftEncoder.get() + rightEncoder.get()) / (Constants.EncoderPorts.ENCODER_COUNTS_PER_INCH * 2);
    }
    
      public void encoderReset() {
        leftEncoder.reset();
        rightEncoder.reset();
      }


}