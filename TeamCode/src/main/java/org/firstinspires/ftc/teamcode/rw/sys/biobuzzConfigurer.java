package org.firstinspires.ftc.teamcode.rw.sys;
import java.util.ArrayList;
import java.util.List;
public class biobuzzConfigurer implements seasonConfigurer {
    private boolean outOf_Range(Double value, double low, double high) { return value < low || value > high; }
    private boolean hasMotor(configClasses cc, String key) {
        return cc.hardware != null && cc.hardware.motorConfig != null && cc.hardware.motorConfig.containsKey(key);
    }
    @Override
    public void apply(configClasses config, List<String> warnings, List<String> errors) {
        if (config.motorPowers == null) {
            config.motorPowers = new configClasses.MotorPowers();
        }
        if (hasMotor(config, "intake")) {
            if (config.motorPowers.intake == null || config.motorPowers.intake.isEmpty()) {
                warnings.add("Array Key 'intake' not set. Using default 0.7");
                config.motorPowers.intake = new ArrayList<>();
                config.motorPowers.intake.add(0.7);
            } else {
                for (Double power : config.motorPowers.intake) {
                    if (power == null || outOf_Range(power, -1, 1))
                        errors.add("SOME value for Array Key 'intake' not in range [-1, 1]");
                }
            }
        }
        if (hasMotor(config, "outtake")) {
            if (config.motorPowers.outtake == null || config.motorPowers.outtake.isEmpty()) {
                warnings.add("Array Key 'outtake' not set. Using default [0.45, 0.65, 0.8]");
                config.motorPowers.outtake = new ArrayList<>();
                config.motorPowers.outtake.add(0.45);
                config.motorPowers.outtake.add(0.65);
                config.motorPowers.outtake.add(0.80);
            } else {
                if (config.motorPowers.outtake == null || config.motorPowers.outtake.isEmpty()) {
                    warnings.add("Array Key 'outtake' not set. Using default [0.45, 0.65, 0.8]");
                    config.motorPowers.outtake = new ArrayList<>();

                    config.motorPowers.outtake.add(0.45);
                    config.motorPowers.outtake.add(0.65);
                    config.motorPowers.outtake.add(0.80);
                } else {
                    for (Double power : config.motorPowers.outtake) {
                        if (power == null || outOf_Range(power, -1, 1)) {
                            errors.add("SOME value for Array Key 'outtake' not in range [-1, 1]");
                        }
                    }
                }
            }
        }
    }
}