package org.firstinspires.ftc.teamcode.rw.sys;

import java.util.List;

public interface seasonConfigurer {
    void apply(configClasses config, List<String> warnings, List<String> errors);
}
