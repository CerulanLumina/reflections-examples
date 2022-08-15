package morecommands;

import api.Command;
import api.CommandHolder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandHolder
public class MoreCommands {

    @Command(command = "list")
    public static void methodNameDoesNotMatter() {
        try {
            Files.list(Paths.get(System.getProperty("user.dir"))).map(Path::toString).forEach(System.out::println);
        } catch (IOException e) {
            System.err.println("Error while listing");
        }
    }

}
