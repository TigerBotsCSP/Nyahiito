package frc.robot;

import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import edu.wpi.first.apriltag.AprilTagDetection;
import edu.wpi.first.apriltag.AprilTagDetector;
import edu.wpi.first.apriltag.AprilTagPoseEstimator;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.CvSink;
import edu.wpi.first.cscore.CvSource;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Vision {
    AprilTagDetector m_detector;
    AprilTagPoseEstimator m_estimator;
    ArrayList<Integer> m_detectedTags = new ArrayList<Integer>();

    Vision() {
        m_detector = new AprilTagDetector();
        m_detector.addFamily("tag16h5", 0);

        AprilTagPoseEstimator.Config poseEstConfig = new AprilTagPoseEstimator.Config(
                0.1524, 699.3778103158814, 677.7161226393544, 345.6059345433618, 207.12741326228522);
        m_estimator = new AprilTagPoseEstimator(poseEstConfig);
    }

    public void startVision() {
        Thread visionThread = new Thread(() -> visionThread());
        visionThread.setDaemon(true);
        visionThread.start();
    }

    public boolean tagDetected(int tag) {
        return m_detectedTags.contains(tag);
    }

    private void visionThread() {
        // Begin the camera
        UsbCamera camera = CameraServer.startAutomaticCapture();

        // Set the resolution
        camera.setResolution(640, 480);

        // Get a CvSink. This will capture Mats from the camera
        CvSink cvSink = CameraServer.getVideo();
        // Setup a CvSource. This will send images back to the Dashboard
        CvSource outputStream = CameraServer.putVideo("Detected", 640, 480);

        // Mats are very memory expensive. Lets reuse this Mat.
        Mat mat = new Mat();
        Mat grayMat = new Mat();

        // Instantiate these  once
        Scalar outlineColor = new Scalar(0, 255, 0);
        Scalar crossColor = new Scalar(0, 0, 255);

        // This cannot be 'true'. The program will never exit if it is. This
        // lets the robot stop this thread when restarting robot code or
        // deploying.
        while (!Thread.interrupted()) {
            // Tell the CvSink to grab a frame from the camera and put it
            // in the source mat. If there is an error notify the output.
            if (cvSink.grabFrame(mat) == 0) {
                // Send the output the error.
                outputStream.notifyError(cvSink.getError());
                // Skip the rest of the current iteration
                continue;
            }

            Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);

            AprilTagDetection[] detections = m_detector.detect(grayMat);

            // Have not seen any tags yet, let's refresh
            m_detectedTags.clear();

            for (AprilTagDetection detection : detections) {
                // Ooh, we saw this tag
                m_detectedTags.add(detection.getId());

                // Draw lines around the tag
                for (var i = 0; i <= 3; i++) {
                    var j = (i + 1) % 4;
                    var pt1 = new Point(detection.getCornerX(i), detection.getCornerY(i));
                    var pt2 = new Point(detection.getCornerX(j), detection.getCornerY(j));
                    Imgproc.line(mat, pt1, pt2, outlineColor, 2);
                }

                // Mark the center of the tag
                var cx = detection.getCenterX();
                var cy = detection.getCenterY();
                var ll = 10;
                Imgproc.line(mat, new Point(cx - ll, cy), new Point(cx + ll, cy), crossColor, 2);
                Imgproc.line(mat, new Point(cx, cy - ll), new Point(cx, cy + ll), crossColor, 2);

                // Identify the tag
                Imgproc.putText(mat, Integer.toString(detection.getId()), new Point(cx + ll, cy),
                        Imgproc.FONT_HERSHEY_SIMPLEX,
                        1, crossColor, 3);
            }

            // Give the output stream a new image to display
            outputStream.putFrame(mat);
        }

        m_detector.close();
    }
}
