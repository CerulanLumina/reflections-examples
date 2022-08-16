import java.lang.reflect.InvocationTargetException;

public class ReflectionDemo1 {

    public static void main(String... args) {
        var console = System.console();
        //noinspection InfiniteLoopStatement
        while (true) {
            System.out.print("Enter a method to run: ");
            var input = console.readLine();
            var force = false;
            if (input.charAt(0) == '!') {
                force = true;
                input = input.substring(1);
            }
            try {
                var method = Commands.class.getDeclaredMethod(input);
                if (force) method.setAccessible(true);
                method.invoke(null);
            } catch (NoSuchMethodException e) {
                System.out.println("No such method!");
            } catch (InvocationTargetException e) {
                System.out.println("You tried to run a method on an instance of an object where it does not exist (check static)");
            } catch (IllegalAccessException e) {
                System.out.println("That was private!");
            }

        }
    }

}

class Commands {
    public static void sayHi() {
        System.out.println("Hello World!");
    }

    public static void sayFoo() {
        System.out.println("Foo Bar");
    }

    public static void exit() {
        System.exit(0);
    }

    private static void sayNo() {
        System.out.println("You shouldn't be able to access this!");
    }
}