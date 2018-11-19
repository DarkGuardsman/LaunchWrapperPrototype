package com.darkguardsman.launchwrapper;

import com.darkguardsman.launchwrapper.util.StringHelpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/18.
 */
public class Main {

    public static final String ARG_UI = "ui";
    public static final String ARG_PATH = "jarpath";
    public static final String ARG_PROGRAM = "pgargs";
    public static final String ARG_VM = "vmargs";
    public static final String ARG_EXIT_TIME = "exittime";

    public static void main(String... argsStringArray) {
        System.out.println("Starting wrapper...");
        Map<String, String> args = loadArgs(argsStringArray);

        if (args.containsKey(ARG_UI)) {
            System.out.println("Running as GUI");
            //TODO load UI

            System.out.println("Done..");
            System.exit(0);
            return;
        } else if (args.containsKey(ARG_PATH)) {
            System.out.println("Running as command line");
            try {
                runJar(args.get(ARG_PATH), args.get(ARG_VM), args.get(ARG_PROGRAM), getExitTime(args.get(ARG_EXIT_TIME)));

                System.out.println("Done..");
                System.exit(0);
                return;

            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(1);
            }
        } else {
            System.err.println("ERROR: " + ARG_PATH + "=\"Path/to/jar.jar\" argument is required to know what jar to launch");
            System.exit(1);
        }
    }

    private static int getExitTime(String timeString) {

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
        throw new IllegalArgumentException("Invalid input for " + ARG_EXIT_TIME + " of " + timeString + "." +
                "Supported values start with a whole number and end with one of the follow: m ms s h. " +
                "With m being minutes, ms being miliseconds, s being seconds, and h hours");
    }

    public static void runJar(String path, String vmArgs, String programArgs, int exitTime) throws IOException, InterruptedException {

        System.out.println("Path: " + path);
        System.out.println("VM Args: " + vmArgs);
        System.out.println("Program Args: " + programArgs);
        System.out.println("Exit Wait Time: " + exitTime + "ms\n");

        //Default args for run
        List<String> args = new ArrayList();
        args.add("java");

        //If we have jvm args
        if (vmArgs != null) {

            String[] split = vmArgs.split("\\s+");
            for (String s : split) {
                s = s.trim();
                if (!s.isEmpty()) {
                    args.add(s);
                }
            }
        }

        args.add("-jar");
        args.add(path);

        //If we have command args
        if (programArgs != null) {

            String[] split = programArgs.split("\\s+");
            for (String s : split) {
                s = s.trim();
                if (!s.isEmpty()) {
                    args.add(s);
                }
            }
        }
        //Build process and start
        ProcessBuilder pb = new ProcessBuilder(args);

        long startTime = System.currentTimeMillis();
        Process proc = pb.start();
        System.out.println("Process Started");

        //Collect output on a second thread
        Thread thread = new Thread(() -> {
            try {
                System.out.println("Thread writer started");
                //Read output and dump to console
                BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                String line;
                while ((line = is.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Thread writer done");
        });
        thread.start();

        Thread thread2 = new Thread(() -> {
            try {
                System.out.println("Thread writer started");
                //Read output and dump to console
                BufferedReader is = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

                String line;
                while ((line = is.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Thread writer done");
        });
        thread2.start();

        //Wait for process toe end
        int exitCode;
        if (exitTime > 0) {
            //Set a time out
            System.out.println("Entering sleep");
            Thread.sleep(exitTime);
            System.out.println("Awake");
            if (proc.isAlive()) {
                System.out.println("Application is alive, force quitting");
                proc.destroy();
            }
            Thread.sleep(1000);
            exitCode = proc.exitValue();
        } else {
            exitCode = proc.waitFor();
        }
        System.out.println("Exit code: " + exitCode);

        long endTime = System.currentTimeMillis();
        System.out.println("Runtime: " + StringHelpers.formatNanoTime(TimeUnit.MILLISECONDS.toNanos(endTime - startTime)));
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
