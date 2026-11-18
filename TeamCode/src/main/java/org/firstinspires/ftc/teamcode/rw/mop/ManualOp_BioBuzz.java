package org.firstinspires.ftc.teamcode.rw.mop;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.AppUtil;
import org.firstinspires.ftc.teamcode.Console;
import org.firstinspires.ftc.teamcode.rw.sys.biobuzzConfigurer;
import org.firstinspires.ftc.teamcode.rw.sys.configBuilder;
import org.firstinspires.ftc.teamcode.rw.sys.robot;
import org.firstinspires.ftc.teamcode.rw.sys.seasonConfigurer;

import java.util.Locale;
import java.io.File;

@TeleOp(name="ManualOP", group="BioBuzz")
public class ManualOp_BioBuzz extends OpMode {
    private Console console;
    robot bot = new robot();
    Telemetry tm;
    configBuilder config = new configBuilder();
    seasonConfigurer season = new biobuzzConfigurer(); // BioBuzz season

    // RUNTIME VARIABLES
    boolean debugMode = true;
    final double fb_fix = -1;

    // PUBLICITY VARIABLES
    double fb, rt, cw;
    boolean intake = false, outtake = false;
    boolean debug;
    int takerange;

    @Override
    public void init() {
        tm = telemetry;
        tm.setDisplayFormat(Telemetry.DisplayFormat.HTML);
        console = new Console(tm);

        File file = new File(AppUtil.FIRST_FOLDER, "Robo Warriors/biobuzz31767.jsonc");

        config.init(file, hardwareMap, season);

        if (config.hasErrors()) { for (String key : config.errors) {console.error("Config", key, true);} tm.update(); }
        if (config.hasWarnings()) { for (String key : config.warnings) {console.warn("Config", key, true);} tm.update(); }

        bot.init(config);
    }

    @Override
    public void loop() {
        if (!config.config.controls.invert) { // If (false)
            fb = (fb_fix * gamepad1.left_stick_y); //forwards backwards; gamepad returns backwards
            rt = gamepad1.right_stick_x; //rotate
            cw = gamepad1.left_stick_x; //crabwalk
        } else {
            fb = (fb_fix * gamepad1.right_stick_y); //forwards backwards; gamepad returns backwards
            rt = gamepad1.left_stick_x; //rotate
            cw = gamepad1.right_stick_x; //crabwalk
        }

        if (Math.abs(fb) < config.config.controls.deadZone) fb = 0;
        if (Math.abs(rt) < config.config.controls.deadZone) rt = 0;
        if (Math.abs(cw) < config.config.controls.deadZone) cw = 0;

        if (config.has("intake")) {
            if (gamepad2.left_bumper && gamepad2.leftBumperWasReleased()) {
                intake = !intake;
            }

            if (intake) {
                bot.teleop.take("in", config.config.motorPowers.intake.get(0));
            } else {
                bot.teleop.take("in", 0);
            }
        }

        if (config.has("outtake")) {
            if (gamepad2.right_bumper && gamepad2.rightBumperWasReleased()) {
                outtake = !outtake;
            }

            if (outtake) {
                bot.teleop.take("out", config.config.motorPowers.outtake.get(takerange));
            } else {
                bot.teleop.take("out", 0);
            }

            if (gamepad2.dpad_up && gamepad2.dpadUpWasReleased() && takerange < config.config.motorPowers.outtake.size() - 1) {
                takerange++;
            }

            if (gamepad2.dpad_down && gamepad2.dpadDownWasReleased() && takerange > 0) {
                takerange--;
            }
        }

        if ((gamepad2.dpad_right | gamepad1.dpad_right) && (gamepad2.dpadRightWasReleased() | gamepad1.dpadRightWasReleased()) && debugMode) debug = !debug;
        if (debug) debug();

        bot.teleop.drive(fb, rt, cw);
    }

    public void debug() {
        console.h1("Debug Menu", "#DDD");
        console.info("In?" + intake);
        console.info("Out?" + outtake);
        console.info("Takerange:" + takerange + "Take power:" + config.config.motorPowers.outtake.get(takerange));
        console.log(String.format(Locale.ENGLISH, "Movement Values FB: %.2f | RT: %.2f | CW: %.2f", fb, rt, cw)); // Forwards|Backwards Rotate Crabwalk
        tm.update();
    }
}