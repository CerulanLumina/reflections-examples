import java.lang.reflect.Field;

class Animal {
    private final String voice;
    public Animal(String voice) {
        this.voice = voice;
    }

    public void sayHello() {
        System.out.println(voice);
    }
}

public class ReflectionDemo2 {

    public static void main(String... args) {

        Animal cat = new Animal("meow");
        Animal dog = new Animal("woof");

        String personVoice = "Hello!";
        Animal person1 = new Animal(personVoice);
        Animal person2 = new Animal(personVoice);

        try {
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
