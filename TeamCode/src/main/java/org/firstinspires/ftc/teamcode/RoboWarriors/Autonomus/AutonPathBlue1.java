package org.firstinspires.ftc.teamcode.RoboWarriors.Autonomus;

import com.pedropathing.api.Paths;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;
import static com.pedropathing.ivy.Scheduler.schedule;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.Pedro.RoboWarriors.Constants;

@Autonomous(name = "AutoPath", group = "Autonomous")
public class AutonPathBlue1 extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(87.2426, 132.7709, 90);
    private final Pose shoot1 = poseFactory.of(86.8085, 116.7464, 90);
    private final Pose intake1Start = poseFactory.of(86.8085, 116.7464, 90);
    private final Pose intake1 = poseFactory.of(131.5905, 131.1433, 90);
    private final Pose shoot2Start = poseFactory.of(131.5905, 131.1433, 90);
    private final Pose shoot2 = poseFactory.of(83.8864, 32.1009, 270);
    private final Pose shoot2Control1 = poseFactory.of(133.7708, 59.3302, 0);
    private final Pose intake2Start = poseFactory.of(83.8864, 32.1009, 270);
    private final Pose intake2 = poseFactory.of(93.0907, 11.465, 270);
    private final Pose shoot3Start = poseFactory.of(93.0907, 11.465, 270);
    private final Pose shoot3 = poseFactory.of(85.6284, 31.1655, 0);
    private final Pose intake3 = poseFactory.of(127.4121, 94.5022, 0);
    private final Pose intake3Control1 = poseFactory.of(129.8303, 50.0407, 0);
    private final Pose shoot4Start = poseFactory.of(127.4121, 94.5022, 0);
    private final Pose shoot4 = poseFactory.of(84.1066, 107.4967, 180);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, shoot1()),
                follow(follower, intake1()),
                follow(follower, shoot2()),
                follow(follower, intake2()),
                follow(follower, shoot3()),
                follow(follower, intake3()),
                follow(follower, shoot4())
        );
    }

    @Override
    public void runOpMode() {
        Scheduler.reset();
        follower = Constants.create(hardwareMap);
        follower.setPose(start);
        follower.update();

        waitForStart();
        schedule(autoRoutine());

        while (opModeIsActive()) {
            follower.update();
            Scheduler.execute();

            telemetry.addData("x", follower.pose().x());
            telemetry.addData("y", follower.pose().y());
            telemetry.addData("heading", follower.pose().heading());

            if (follower.currentPath() != null) {
                telemetry.addData("Current path distance remaining", follower.distanceToEndpoint());
                telemetry.addData("Path number", follower.pathIndex());
            }

            telemetry.update();
        }
    }

    public Path shoot1() {
        return Paths.line(start, shoot1).constant(shoot1);
    }

    public Path intake1() {
        return Paths.line(intake1Start, intake1).linear(intake1Start, intake1);
    }

    public Path shoot2() {
        return Paths.curve(shoot2Start, shoot2Control1, shoot2).linear(shoot2Start, shoot2);
    }

    public Path intake2() {
        return Paths.line(intake2Start, intake2).linear(intake2Start, intake2);
    }

    public Path shoot3() {
        return Paths.line(shoot3Start, shoot3).linear(shoot3Start, shoot3);
    }

    public Path intake3() {
        return Paths.curve(shoot3, intake3Control1, intake3).constant(intake3);
    }

    public Path shoot4() {
        return Paths.line(shoot4Start, shoot4).linear(shoot4Start, shoot4);
    }
}

