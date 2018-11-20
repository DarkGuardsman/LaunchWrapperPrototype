package test;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 11/19/18.
 */
public class TestJarMain {
    public static void main(String... args) {
        System.out.println("Start... " + args);

        //Ensure we have args
        if (args != null && args.length > 0) {

            //Get and output arg 0
            final String runType = args[0];
            System.out.println("RunType: " + runType);

            //Arg 1 is a simple test
            if (runType.equalsIgnoreCase("1")) {
                System.out.println("Hello World");

                System.out.println("Done...");
                System.exit(0);
            }
            //Arg 2 is a loop test for console length
            else if (runType.equalsIgnoreCase("2")) {
                int loop = Integer.parseInt(args[1]);
                System.out.println("LoopCount: " + loop);

                for (int i = 0; i < loop; i++) {
                    System.out.println("[" + i + "]" + Math.random());
                }

                System.out.println("Done...");
                System.exit(0);
            }
            //Feedback for bad data
            else {
                System.err.println("Error: Unknown run type '" + runType + "'");
                System.exit(1);
            }
        }
        //Feedback for bad data
        else {
            System.err.println("Error: Failed to provide run type as arg 0");
            System.exit(1);
        }
    }
}
