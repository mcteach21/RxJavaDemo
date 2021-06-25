package mc.apps;

import rx.Observable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {
    private static final Logger logger=Logger.getLogger(Main.class.getSimpleName());
    private static final LogManager logManager = LogManager.getLogManager();
    static{
        try {
            logManager.readConfiguration(Main.class.getClassLoader().getResourceAsStream("log.properties"));
        } catch (IOException exception) {
            logger.log(Level.SEVERE, "loading configuration error..",exception);
        }
    }

    public static void main(String[] args) {
        System.out.println(Main.class.getClassLoader().getResource("log.properties"));

        ObservableDemo1();
        ObservableDemo2();
    }

    static String[] letters = {"a", "b", "c", "d", "e"};

    public static void ObservableDemo1(){
        display("Observale : Create and Suscribe!");

        Observable<String> observable = Observable.from(letters);

        final String[] result = {""};
        observable.subscribe(
                i -> {result[0] += i;
                    try {
                        Thread.sleep(200);
                        logger.log(Level.INFO,result[0]);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                },  //OnNext
                Throwable::printStackTrace, //OnError
                () -> result[0] += "_Completed" //OnCompleted
        );
        logger.log(Level.INFO,"result = "+result[0]);
        //assertTrue(result[0].equals("abcdefg_Completed"));
    }
    private static void ObservableDemo2() {
        display("Observale : Transformations and Conditional Operators!");

        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        String voys = "aeiouy";

        //        System.out.println("Voyelles :");
        //        //Stream
        //        Stream<Character> charStream = alphabet.chars().mapToObj(i->(char)i);
        //        charStream.filter(i->voys.contains(i.toString())).map(x->x.toString().toUpperCase()).forEach(System.out::print);
        //
        //        //Observer :
        //        Observable<String> observable = Observable.from(alphabet.split(""));
        //        observable
        //                .filter(i->voys.contains(i))
        //                .map(String::toUpperCase)
        //                .forEach(System.out::print);

        final String[] result = {""};

        //map
        // Observable<String> observable = Observable.from(alphabet.split(""));
        Observable.from(alphabet.split(""))             // Observable<String>
                .filter(letter->voys.contains(letter))       // filter..
                .map(String::toUpperCase)                   // transforms items emitted
                .subscribe(letter -> result[0] += letter);

        logger.log(Level.INFO,"result (map) = "+result[0]);

        result[0]="";

        //flatmap
        String[][] array2d = new String[][]{{"java", "kotlin"}, {"html", "css"}, {"javascript", "jquery"}};
        // Observable<String[]> observable = Observable.from(array2d);
        Observable.from(array2d)                          // = Observable<String[]>
                .flatMap(s->Observable.from(s))           // = Observable<String> (Observable imbriqué : Nested Observable)
                //.flatMap(s -> flatArray(s))
                .map(String::toUpperCase)
                .subscribe(x->result[0] += x +" | ");
        logger.log(Level.INFO,"result (flatmap) = "+result[0]);

        //scan
        Observable.from(letters)
                .scan(new StringBuilder(), StringBuilder::append)
                .subscribe(x->logger.info("StringBuilder - Result (scan) : "+x.toString()));
        
        //GroupBy

        Integer[] numbers = new Random().ints(0,100).limit(10).boxed().toArray(Integer[]::new);
        //IntStream.range(1,10).boxed().toArray(Integer[]::new);
        Observable.from(numbers)
                .takeWhile(number -> number > 50)
                .groupBy(n->n%2==0?"P":"I")
                .subscribe(group->{
                    //logger.info("Suscribe - Group : "+group.getKey());
                    group.subscribe(number-> logger.info("Subscribe (Group) : "+group.getKey()+" => "+number));
                });


    }
    static Observable<String> flatArray(String[] array) {
        return Observable.from(array);
    }


    private static void display(String title) {
        String txt = "**** "+title+" ****";
        IntStream.range(0, txt.length()).forEach(i->System.out.print("*"));
        System.out.println();
        System.out.println(txt);
        IntStream.range(0, txt.length()).forEach(i->System.out.print("*"));
        System.out.println();
    }
}
