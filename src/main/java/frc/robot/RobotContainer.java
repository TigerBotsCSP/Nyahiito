package frc.robot;

import java.io.File;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class RobotContainer {
    // Core
    public static Drive m_drive = new Drive();
    public static Arm m_arm = new Arm();
    public static Gyro m_gyro = new Gyro();
    public static Auto m_auto = new Auto();
    public static Intaker m_intaker = new Intaker();
    public static NyaDS m_nyads = new NyaDS();

    // Controllers
    public static XboxController m_driveController = new XboxController(0);
    public static XboxController m_armController = new XboxController(1);

    // Functions
    public static double limit(double value, double limit) {
        return Math.max(Math.min(value, limit), -limit);
    }

    public static void updateData() {
        // Set Shuffleboard data
        Constants.Auto.autoPath = SmartDashboard.getString("Auto Path/active", "");
        SmartDashboard.putNumber("Gyro Angle", m_gyro.getAngle());
        SmartDashboard.putNumber("Turn Rate", m_gyro.getTurnRate());
        SmartDashboard.putBoolean("Drive Speed", (Constants.driveSpeed == .7 ? false : true)); // Green if turbo
        SmartDashboard.putBoolean("Arm Speed", (Constants.armSpeed == .6 ? false : true)); // Green if turbo
        SmartDashboard.putBoolean("Intaker In", m_intaker.m_in); // Green if turbo
    }

    public static void init() {
        try {
            // Autonomous chooser
            ArrayList<String> fileNames = new ArrayList<>();

            File directory = new File("/home/lvuser/nyads/");
            File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));

            for (File file : files) {
                fileNames.add(file.getName());
            }

            SendableChooser<String> chooser = new SendableChooser<>();
            for (String fileName : fileNames) {
                chooser.addOption(fileName, fileName);
            }
            chooser.setDefaultOption(Constants.Auto.autoPath, Constants.Auto.autoPath);

            SmartDashboard.putData("Auto Path", chooser);

            // NyaDS Setup
            m_nyads.start();
        } catch (Exception e) {
            // No files :(
        }
    }
}
