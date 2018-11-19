package com.darkguardsman.launchwrapper.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/18.
 */
public class RunnableStreamWriter implements Runnable {

    private final String name;
    private final InputStream stream;

    public RunnableStreamWriter(String name, InputStream stream) {
        this.name = name;
        this.stream = stream;
    }

    @Override
    public void run() {
        try {
            System.out.println("Thread '" + name + "'writer started");
            //Read output and dump to console
            BufferedReader is = new BufferedReader(new InputStreamReader(stream));

            String line;
            while ((line = is.readLine()) != null) {
                System.out.println(line);
            }
        } catch (Exception e) {
            System.out.println("Thread '" + name + "' has hit an unexpected error");
            e.printStackTrace();
        }
        System.out.println("Thread '" + name + "'writer done");
    }
}
