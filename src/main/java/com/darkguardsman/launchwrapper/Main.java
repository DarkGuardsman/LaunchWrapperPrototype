package com.darkguardsman.launchwrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/18.
 */
public class Main {

    public static final String ARG_UI = "ui";
    public static final String ARG_PATH = "path";
    public static final String ARG_COMMAND = "command";

    public static void main(String... argsStringArray)
    {
        System.out.println("Starting wrapper...");
        Map<String, String> args = loadArgs(argsStringArray);

        if (args.containsKey(ARG_UI))
        {
            System.out.println("Running as GUI");
            //TODO load UI

            System.out.println("Done..");
            System.exit(0);
            return;
        }
        else if(args.containsKey(ARG_PATH))
        {
            System.out.println("Running as command line");
            try {
                runJar(args.get(ARG_PATH), args.get(ARG_COMMAND));

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
        }
        else
        {
            System.err.println("ERROR: path=\"Path/to/jar.jar\" argument is required to know what jar to launch");
            System.exit(1);
        }
    }

    public static void runJar(String path, String command) throws IOException, InterruptedException {

        //Default args for run
        List<String> args = new ArrayList();
        args.add("java");
        args.add("-jar");
        args.add(path);

        //If we have command args
        if(command != null) {

           String[] split = command.split("\\s+");
           for(String s : split)
           {
               s = s.trim();
               if(!s.isEmpty())
               {
                   args.add(s);
               }
           }
        }
        //Build process and start
        ProcessBuilder pb = new ProcessBuilder(args);
        Process proc = pb.start();

        //Read output and dump to console
        BufferedReader is = new BufferedReader(new InputStreamReader(proc.getInputStream()));

        String line;
        while ((line = is.readLine()) != null) {
            System.out.println(line);
        }

        //Wait for process toe end
        int exitCode = proc.waitFor();
        System.out.println("Exit code: " + exitCode);

        //Get process output
        InputStream err = proc.getErrorStream();

        //Output error
        System.out.println("\nError Stream: ");
        byte c[]=new byte[err.available()];
        err.read(c,0,c.length);
        System.out.println(new String(c));
    }

    /**
     * Converts arguments into a hashmap for usage
     *
     * @param args
     * @return
     */
    public static Map<String, String> loadArgs(String... args)
    {
        final HashMap<String, String> map = new HashMap();
        if (args != null)
        {
            String currentArg = null;
            String currentValue = "";
            for (int i = 0; i < args.length; i++)
            {
                String next = args[i].trim();
                if (next == null)
                {
                    throw new IllegalArgumentException("Null argument detected in launch arguments");
                }
                else if (next.startsWith("-"))
                {
                    if (currentArg != null)
                    {
                        map.put(currentArg, currentValue);
                        currentValue = "";
                    }

                    if (next.contains("="))
                    {
                        String[] split = next.split("=");
                        currentArg = split[0].substring(1).trim();
                        currentValue = split[1].trim();
                        if (split.length > 2)
                        {
                            for (int l = 2; l < split.length; l++)
                            {
                                currentValue += "=" + split[l];
                            }
                        }
                    }
                    else
                    {
                        currentArg = next.substring(1).trim();
                    }
                }
                else if (currentArg != null)
                {
                    if (!currentValue.isEmpty())
                    {
                        currentValue += ",";
                    }
                    currentValue += next.replace("\"", "").replace("'", "").trim();
                }
                else
                {
                    throw new IllegalArgumentException("Value has no argument associated with it [" + next + "]");
                }
            }
            //Add the last loaded value to the map
            if (currentArg != null)
            {
                map.put(currentArg, currentValue);
            }
        }
        return map;
    }

}
