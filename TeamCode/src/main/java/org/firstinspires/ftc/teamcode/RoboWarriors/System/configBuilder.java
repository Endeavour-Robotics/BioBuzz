package org.firstinspires.ftc.teamcode.RoboWarriors.System;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import com.google.gson.JsonParseException;

import com.qualcomm.robotcore.util.ReadWriteFile;

import java.io.File;

public class configBuilder {
    public boolean hasErrors() { return !errors.isEmpty(); }
    public boolean hasWarnings() { return !warnings.isEmpty(); }
    public boolean has(String key) { return motors.containsKey(key); }
    private boolean outOf_Range(Double value, double low, double high) { return value < low || value > high; }
    private final String[] req_motors = { "fl", "fr", "bl", "br" };
    private final Map<String, DcMotor> motors = new HashMap<>();

    // RUNTIME VARIABLES
    private static final double DEFAULT_SPEED_LIMIT = 0.8;
    public Map<String, bot.MotorEquation> kinematics = new HashMap<>();
    public configClasses config;
    public final List<String> errors = new ArrayList<>();
    public final List<String> warnings = new ArrayList<>();
    Gson gson = new Gson();

    public void init(File path, HardwareMap hwMap, seasonConfigurer season) {
        config = null;
        errors.clear();
        warnings.clear();
        motors.clear();

        // Try to read file from $sd-f-dir (allows for changing without pushing
        String json;
        try {
            json = ReadWriteFile.readFile(path);
        } catch (RuntimeException e) {
            errors.add("Failed to read " + path.getName() + ": " + e.getMessage());
            return;
        }

        loadAndValidate(json, path.toString(), season);

        if (hasErrors()) return;

        setupMotors(hwMap);

        kinematics.put("br", (fb, rt, cw) -> fb - rt + cw);
        kinematics.put("bl", (fb, rt, cw) -> fb + rt - cw);
        kinematics.put("fr", (fb, rt, cw) -> fb - rt - cw);
        kinematics.put("fl", (fb, rt, cw) -> fb + rt + cw);
    }

    void loadAndValidate(String json, String source, seasonConfigurer season) {
        try {
            JsonReader reader = new JsonReader(new StringReader(json));
            reader.setLenient(true);
            config = gson.fromJson(reader, configClasses.class);
        } catch (JsonParseException e) {
            errors.add("Bad JSON in " + source + ": " + e.getMessage());
        } catch (RuntimeException e) {
            errors.add("Unexpected error while loading " + source + ": " + e.getMessage());
        }

        if (config == null) {
            if (errors.isEmpty()) errors.add("Config file is empty: " + source);
            return;
        }

        if (season != null) season.apply(config, warnings, errors);
        validate();
    }

    double getDefaultSpeedLimit(String key) {
        Double lim = config.motorPowers.speedLimits.get(key);
        if (lim == null) {
            return DEFAULT_SPEED_LIMIT;
        } else {
            return lim;
        }
    }

    private void validateMotorConfig(String key, List<String> entry) {
        if (entry == null) {
            errors.add("Motor '" + key + "' has no entry");
            return;
        }

        if (entry.size() != 4) {
            errors.add("Motor '" + key + "' needs 4 values [name, direction, runMode, zeroPower], found " + entry.size());
            return;
        }

        String name = entry.get(0);
        if (name == null || name.isEmpty()) {
            errors.add("Motor '" + key + "': hardware name (first value) is empty");
        }

        invalidEnum(DcMotor.Direction.class, entry.get(1), key, "direction");
        invalidEnum(DcMotor.RunMode.class, entry.get(2), key, "run mode");
        invalidEnum(DcMotor.ZeroPowerBehavior.class, entry.get(3), key, "zero power");
    }

    private void validateNumbers() {
        if (config.controls == null) {
            warnings.add("Controls not set in config. Default values used.");
            config.controls = new configClasses.Controls();

            config.controls.deadZone = 0.15;
            config.controls.invert = false;
            config.controls.scheme = "joystick";
        } if (config.controls.deadZone == null) {
            warnings.add("'deadZone' key not set in config. Default 0.15");
            config.controls.deadZone = 0.15;
        } if (outOf_Range(config.controls.deadZone, 0.0, 1.0)) {
            errors.add("Dead Zone out of range [0-1]");
        } if (config.controls.invert == null) {
            warnings.add("'Invert' key not set in config. Default FALSE");
            config.controls.invert = false;
        }

        if (config.motorPowers == null || config.motorPowers.speedLimits == null) {
            errors.add("Key: 'speedLimits' missing from config.");
        } else {
            for (String key : config.motorPowers.speedLimits.keySet()) {
                Double limit = config.motorPowers.speedLimits.get(key);
                if (limit == null) {
                    warnings.add("Motor limit has null value for: " + key + ". Default applied (0.8).");
                    config.motorPowers.speedLimits.replace(key, DEFAULT_SPEED_LIMIT);
                } else if (limit <= 0 || limit > 1) {
                    errors.add("Motor: " + key + " limit range outside of [> 0 to 1].");
                }

                if (!config.hardware.motorConfig.containsKey(key)) {
                    errors.add("Motor: '" + key + "' has limit but not in 'motorConfig'.");
                }
            }
        }
    }

    private <E extends Enum<E>> boolean invalidEnum(Class<E> type, String value, String key, String what) {
        if (value == null) {
            errors.add("Motor '" + key + "': " + what + " is null");
            return true;
        }
        try {
            Enum.valueOf(type, value);
            return false;
        } catch (IllegalArgumentException e) {
            errors.add("Motor '" + key + "': '" + value + "' is not a valid " + what);
            return true;
        }
    }

    private void setupMotors(HardwareMap hwMap) {
        for (String key : config.hardware.motorConfig.keySet()) {
            List<String> entry = config.hardware.motorConfig.get(key);
            try {
                // entry.get(0) is the name the CH knows; e.g. 'FrontLeft'
                DcMotor motor = hwMap.get(DcMotor.class, entry.get(0));
                motor.setDirection(DcMotor.Direction.valueOf(entry.get(1)));
                motor.setMode(DcMotor.RunMode.valueOf(entry.get(2)));
                motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.valueOf(entry.get(3)));
                motors.put(key, motor);
            } catch (IllegalArgumentException e) {
                // hwMap.get throws this when no device has that name on the CH
                errors.add("Motor '" + key + "': no device named '" + entry.get(0) + "' on the robot");
            }
        }
    }

    public void validate() {
        if (config.meta == null || config.meta.configName == null) {
            warnings.add("meta.configName is missing");
        }
        if (config.hardware == null || config.hardware.motorConfig == null) {
            errors.add("hardware.motorConfig is missing");
            return;
        }
        Map<String, List<String>> mc = config.hardware.motorConfig;

        for (String key : req_motors) {
            if (!mc.containsKey(key)) errors.add("Required motor '" + key + "' is missing");
        }

        for (String key : mc.keySet()) {
            validateMotorConfig(key, mc.get(key));
        }

        validateNumbers();
    }

    public DcMotor motor(String key) {
        DcMotor m = motors.get(key);
        if (m == null) throw new MissingHardwareException(
                "Config has no motor '" + key + "'. Available: " + motors.keySet());
        return m;
    }

    public DcMotor fl() { return motor("fl"); }
    public DcMotor fr() { return motor("fr"); }
    public DcMotor bl() { return motor("bl"); }
    public DcMotor br() { return motor("br"); }
}
