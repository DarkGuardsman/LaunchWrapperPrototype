package com.darkguardsman.launchwrapper.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/18.
 */
public class Utils {

    /**
     * Converts a string to milliseconds value
     * @param timeString
     * @return
     */
    public static int stringToMiliSeconds(String timeString) {

        if (timeString == null || timeString.trim().isEmpty()) {
            return -1;
        }
        timeString = timeString.toLowerCase().trim();

        if (timeString.endsWith("m")) {
            return 1000 * 60 * Integer.parseInt(timeString.substring(0, timeString.length() - 1));
        } else if (timeString.endsWith("ms")) {
            return Integer.parseInt(timeString.substring(0, timeString.length() - 2));
        } else if (timeString.endsWith("s")) {
            return 1000 * Integer.parseInt(timeString.substring(0, timeString.length() - 1));
        } else if (timeString.endsWith("h")) {
            return 1000 * 60 * 60 * Integer.parseInt(timeString.substring(0, timeString.length() - 1));
        }
        return -2;
    }



    /**
     * Converts arguments into a hashmap for usage
     *
     * @param args
     * @return
     */
    public static Map<String, String> loadArgs(String... args) {
        final HashMap<String, String> map = new HashMap();
        if (args != null) {
            String currentArg = null;
            String currentValue = "";
            for (int i = 0; i < args.length; i++) {
                String next = args[i].trim();
                if (next == null) {
                    throw new IllegalArgumentException("Null argument detected in launch arguments");
                } else if (next.startsWith("-")) {
                    if (currentArg != null) {
                        map.put(currentArg, currentValue);
                        currentValue = "";
                    }

                    if (next.contains("=")) {
                        String[] split = next.split("=");
                        currentArg = split[0].substring(1).trim();
                        if (split.length > 1) {
                            currentValue = split[1].trim();
                            if (split.length > 2) {
                                for (int l = 2; l < split.length; l++) {
                                    currentValue += "=" + split[l];
                                }
                            }
                        } else {
                            currentValue = "";
                        }
                    } else {
                        currentArg = next.substring(1).trim();
                    }
                } else if (currentArg != null) {
                    if (!currentValue.isEmpty()) {
                        currentValue += ",";
                    }
                    currentValue += next.replace("\"", "").replace("'", "").trim();
                } else {
                    throw new IllegalArgumentException("Value has no argument associated with it [" + next + "]");
                }
            }
            //Add the last loaded value to the map
            if (currentArg != null) {
                map.put(currentArg, currentValue);
            }
        }
        return map;
    }
}
