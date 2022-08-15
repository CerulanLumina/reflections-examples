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
        var commands = hotloadCommands();
        var console = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            var line = console.readLine();
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
                    JarFile jarFile;
                    try {
                        jarFile = new JarFile(new File(url.toURI()));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                    URLClassLoader classLoader = URLClassLoader.newInstance(new URL[] { url }, ClassLoader.getSystemClassLoader());
                    var entries = jarFile.entries();
                    try {
                        while (entries.hasMoreElements()) {
                            JarEntry entry = entries.nextElement();
                            if (entry.isDirectory() || !entry.getName().endsWith(".class")) continue;
                            String className = entry.getName().substring(0, entry.getName().length() - 6)
                                    .replace('/', '.');
                            var clazz = classLoader.loadClass(className);

                            if (clazz.getDeclaredAnnotationsByType(CommandHolder.class).length > 0) {
                                Arrays.stream(clazz.getDeclaredMethods())
                                        .filter(method -> method.getDeclaredAnnotationsByType(Command.class).length > 0)
                                        .forEach(method -> {
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
