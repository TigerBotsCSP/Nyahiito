package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.drive.DifferentialDrive.WheelSpeeds;
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
    public XboxController m_controllerSide;

    Drive() {
        m_frontLeft = new PWMSparkMax(2);
        m_rearLeft = new PWMSparkMax(3);
        m_left = new MotorControllerGroup(m_frontLeft, m_rearLeft);

        m_frontRight = new PWMSparkMax(4);
        m_rearRight = new PWMSparkMax(5);
        m_right = new MotorControllerGroup(m_frontRight, m_rearRight);

        m_drive = new DifferentialDrive(m_left, m_right);

        m_controller = new XboxController(0);
        m_controllerSide = new XboxController(1);
    }

    public void rotateDrive(double speed, double rotation) {
        // m_drive.arcadeDrive(speed, rotation);

        speed = MathUtil.applyDeadband(speed, 0);
        rotation = MathUtil.applyDeadband(rotation, 0);

        WheelSpeeds speeds = DifferentialDrive.arcadeDriveIK(speed, rotation, true);

        m_left.set(speeds.left * 1);
        m_right.set(speeds.right * 1.1);
    }

    public void straightDrive(double speed) {
        m_drive.tankDrive(-speed, speed);
    }
}