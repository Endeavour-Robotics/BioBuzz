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
@Autonomous(name = "Red_Test1", group = "AutoTests")
public class AutonRed_Test1 extends LinearOpMode {

    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(31.3675, 9.8, 270);
    private final Pose forwardtoShoot = poseFactory.of(46.9864, 14.9269, 270);
    private final Pose gotoHPStart = poseFactory.of(46.9864, 14.9269, 270);
    private final Pose gotoHP = poseFactory.of(12.4531, 20.758, 270);
    private final Pose point3Start = poseFactory.of(12.4531, 20.758, 270);
    private final Pose point3 = poseFactory.of(10.0707, 10.1494, 270);
    private final Pose crabtoShootStart = poseFactory.of(10.0707, 10.1494, 270);
    private final Pose crabtoShoot = poseFactory.of(53.6793, 126.4737, 270);
    private final Pose crabtoShootControl1 = poseFactory.of(8.4845, 8.7582, 0);
    private final Pose crabtoShootControl2 = poseFactory.of(27.3694, 94.972, 0);
    private final Pose crabtoHPStart = poseFactory.of(53.6793, 126.4737, 270);
    private final Pose crabtoHP = poseFactory.of(12.6324, 20.4765, 270);
    private final Pose crabtoHPControl1 = poseFactory.of(19.9797, 105.646, 0);
    private final Pose point6Start = poseFactory.of(12.6324, 20.4765, 270);
    private final Pose point6 = poseFactory.of(12.0734, 12.1345, 270);
    private final Pose crabtoShoot_2Start = poseFactory.of(12.0734, 12.1345, 270);
    private final Pose crabtoShoot_2 = poseFactory.of(47.1304, 15.3637, 270);
    private final Pose point8Start = poseFactory.of(47.1304, 15.3637, 270);
    private final Pose point8 = poseFactory.of(31.5633, 13.3226, 90);

    // Autonomous routine
    public Command autoRoutine() {
        return sequential(
                follow(follower, forwardtoShoot()),
                follow(follower, gotoHP()),
                follow(follower, path3()),
                follow(follower, crabtoShoot()),
                follow(follower, crabtoHP()),
                follow(follower, path6()),
                follow(follower, crabtoShoot_2()),
                follow(follower, path8())
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

    public Path forwardtoShoot() {
        return Paths.line(start, forwardtoShoot).linear(start, forwardtoShoot);
    }

    public Path gotoHP() {
        return Paths.line(gotoHPStart, gotoHP).linear(gotoHPStart, gotoHP);
    }

    public Path path3() {
        return Paths.line(point3Start, point3).linear(point3Start, point3);
    }

    public Path crabtoShoot() {
        return Paths.curve(crabtoShootStart, crabtoShootControl1, crabtoShootControl2, crabtoShoot).linear(crabtoShootStart, crabtoShoot);
    }

    public Path crabtoHP() {
        return Paths.curve(crabtoHPStart, crabtoHPControl1, crabtoHP).linear(crabtoHPStart, crabtoHP);
    }

    public Path path6() {
        return Paths.line(point6Start, point6).linear(point6Start, point6);
    }

    public Path crabtoShoot_2() {
        return Paths.line(crabtoShoot_2Start, crabtoShoot_2).linear(crabtoShoot_2Start, crabtoShoot_2);
    }

    public Path path8() {
        return Paths.line(point8Start, point8).linear(point8Start, point8);
    }
}
