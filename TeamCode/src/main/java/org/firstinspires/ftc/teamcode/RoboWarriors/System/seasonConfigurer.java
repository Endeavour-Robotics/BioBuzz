package org.firstinspires.ftc.teamcode.RoboWarriors.System;

import java.util.List;

public interface seasonConfigurer {
    void apply(configClasses config, List<String> warnings, List<String> errors);
}
