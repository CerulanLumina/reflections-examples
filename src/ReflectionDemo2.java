import java.lang.reflect.Field;

/**
 * Basic class for properties and functions for Animals
 */
class Animal {
    /**
     * An animal's voice.
     * Once an animal is defined, their voice cannot change.
     * Java Strings are immutable, and setting this to final will ensure that.
     */
    private final String voice;

    /**
     * Create an animal with the specified voice
     * @param voice The voice for an animal
     */
    public Animal(String voice) {
        this.voice = voice;
    }

    /**
     * Instruct the animal to say hello to the console using its voice.
     */
    public void sayHello() {
        System.out.println(voice);
    }

    // Needed for non-compile java. it looks at first class in file and i don't want to move it
    public static void main(String[] args) { ReflectionDemo2.main(args); }
}

public class ReflectionDemo2 {

    public static void main(String... args) {

        // Set up our wonderful animals
        Animal cat = new Animal("meow");
        Animal dog = new Animal("woof");

        String personVoice = "Hello!";
        Animal person1 = new Animal(personVoice);
        Animal person2 = new Animal(personVoice);

        try {
            // Use black magic to read a private field
            Field field = Animal.class.getDeclaredField("voice");
            field.setAccessible(true);

            System.out.println("Person 1 will say '" + field.get(person1) + "'");
            System.out.println("Person 2 will say '" + field.get(person2) + "'");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        blackMagic(person1, "meow");
        blackMagic(person2, "woof");

        System.out.print("Cat: ");
        cat.sayHello();

        System.out.print("Dog: ");
        dog.sayHello();

        System.out.print("Person 1: ");
        person1.sayHello();

        System.out.print("Person 2: ");
        person2.sayHello();
    }

    private static void blackMagic(Animal instance, String newVoice) {
        System.out.println("    *** Abracadabra ***");
        try {
            Field voiceField = instance.getClass().getDeclaredField("voice");

            voiceField.setAccessible(true);
            voiceField.set(instance, newVoice);

        } catch (NoSuchFieldException e) {
            System.err.println("No field");
        } catch (IllegalAccessException e) {
            System.err.println("illegal access");
        }
    }

}
