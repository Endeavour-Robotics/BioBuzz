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
public class AutonPathRed1 extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    // Every point one the field put into coordinates.
    private final Pose start = poseFactory.of(56.4707, 10.0293, 90);
    private final Pose shoot1 = poseFactory.of(56.2599, 15.5007, 270);
    private final Pose intake1Start = poseFactory.of(56.2599, 15.5007, 270);
    private final Pose intake1 = poseFactory.of(10.522, 10.7949, 270);
    private final Pose shoot2Start = poseFactory.of(10.522, 10.7949, 270);
    private final Pose shoot2 = poseFactory.of(58.1363, 106.6846, 111);
    private final Pose shoot2Control1 = poseFactory.of(26.8082, 111.8487, 0);
    private final Pose intake2Start = poseFactory.of(58.1363, 106.6846, 111);
    private final Pose intake2 = poseFactory.of(46.4265, 127.6871, 90);
    private final Pose shoot3Start = poseFactory.of(46.4265, 127.6871, 90);
    private final Pose shoot3 = poseFactory.of(45.7644, 113.2425, 180);
    private final Pose intake3 = poseFactory.of(13.8914, 46.6887, 180);
    private final Pose intake3Control1 = poseFactory.of(26.1334, 87.8554, 0);
    private final Pose shoot4Start = poseFactory.of(13.8914, 46.6887, 180);
    private final Pose shoot4 = poseFactory.of(57.3037, 30.3973, 0);

    // Autonomous routine in order.
    public Command autoRoutine() {
        return sequential(
                follow(follower, shoot1()), // Moves forward then shoots the four pollen inside the robot.
                follow(follower, intake1()), // Goes to the corner of the field and intakes the pollen.
                follow(follower, shoot2()), // Goes to the other side of the field and shoots four balls.
                follow(follower, intake2()), // Goes to the flower and intakes four balls.
                follow(follower, shoot3()), // Drives back and shoots four balls.
                follow(follower, intake3()), // Goes to the second flower and intakes four balls.
                follow(follower, shoot4()) // Goes back to the other side and shoots four.
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
