package basiccommands;

import api.*;

@CommandHolder
public class BasicCommands {

    @Command(command = "hello")
    public static void sayHello() {
        System.out.println("Hello world!");
    }

    @Command(command = "goodbye")
    public static void goodbye() {
        System.out.println("Goodbye.");
        System.exit(0);
    }

}
