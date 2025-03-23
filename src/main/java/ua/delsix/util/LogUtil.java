package ua.delsix.util;

import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;

@UtilityClass
@Log4j2
public class LogUtil {
    public static void codeIsNull() {
        log.error("Code parameter is missing");
    }
}
