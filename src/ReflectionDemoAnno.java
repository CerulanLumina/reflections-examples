import api.Command;
import api.CommandHolder;

import java.io.*;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ReflectionDemoAnno {

    public static void main(String... args) throws IOException {
        // load the commands
        var commands = hotloadCommands();

        // console input
        var console = new BufferedReader(new InputStreamReader(System.in));

        //noinspection InfiniteLoopStatement
        while (true) {
            // read the line
            var line = console.readLine();

            // get the command from the hashmap and invoke it
            Optional<Method> runCommand = Optional.ofNullable(commands.get(line));
            runCommand.ifPresentOrElse(method -> {
                try {
                    method.invoke(null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    System.err.println("An error occurred while attempting to run the command");
                }
            }, () -> {
                System.out.println("Unregistered command");
            });
        }
    }

    private static HashMap<String, Method> hotloadCommands() throws IOException {
        HashMap<String, Method> commands = new HashMap<>();


        // Load the jar files from a list
        // Might scan directories in another world
        Files.readAllLines(Paths.get("commands.list"))
                .stream()
                .map(Paths::get)
                .map(Path::toUri)
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(url -> {
                    // Load the actual jar file
                    JarFile jarFile;
                    try {
                        jarFile = new JarFile(new File(url.toURI()));
                    } catch (IOException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    // Setup a Class Loader from the Jar file
                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { url }, ClassLoader.getSystemClassLoader());

                    // Look for compiled classes in the jar
                    var entries = jarFile.entries();
                    try {
                        while (entries.hasMoreElements()) {


                            JarEntry entry = entries.nextElement();
                            if (entry.isDirectory() || !entry.getName().endsWith(".class")) continue;

                            // For every class file in the jar, load it and look for annotations
                            String className = entry.getName().substring(0, entry.getName().length() - 6)
                                    .replace('/', '.');
                            var clazz = classLoader.loadClass(className);

                            // Scan the class for the CommandHolder annotation
                            if (clazz.getDeclaredAnnotationsByType(CommandHolder.class).length > 0) {
                                Arrays.stream(clazz.getDeclaredMethods())
                                        // scan the CommandHolder class for methods annotated with Command
                                        .filter(method -> method.getDeclaredAnnotationsByType(Command.class).length > 0)
                                        .forEach(method -> {
                                            // Put the annotated method into the hashmap with its command name as the key
                                            String command = method.getDeclaredAnnotation(Command.class).command();
                                            commands.put(command, method);
                                        });
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        System.err.println("no class");
                    }

                });
        return commands;
    }

}
