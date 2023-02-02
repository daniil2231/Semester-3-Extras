import java.io.FileReader;
import java.util.*;

import com.opencsv.CSVReader;

public class CSVReading
{
    public static void main(String[] args)
    {
        readCSV();
        printSth();
    }

    public static void readCSV () {

        List<List<String>> strings = new ArrayList<>();
        long timeLimit = 5000;//ms
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String testPlate = "['01-LX-RP']";
                Sensors sensors = new Sensors();

                CSVReader reader = null;
                try {
                    //parsing a CSV file into CSVReader class constructor
                    reader = new CSVReader(new FileReader
                            ("C:\\Users\\35988\\Desktop\\uni\\Software Engineering Semester 3\\ANPR\\realtimeresults.csv"));
                    String[] nextLine;
                    //reads one line at a time
                    while ((nextLine = reader.readNext()) != null) {
                        for (String token : nextLine) {
                            if(token.length() == 12 && !strings.contains(Arrays.asList(token))) {
                                strings.add(Arrays.asList(token));
                            }
                        }
                    }
                    for (List<String> l:
                            strings) {
                        for (String s:
                             l) {
                            System.out.println(s);
                            if(Objects.equals(s, testPlate)) {
                                sensors.start();
                            }
                        }
                    }
                    strings.clear();

                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }, 0, timeLimit);// Delay, Time Between in ms
    }

        public static void printSth () {
        System.out.print("lol\n");
    }
}
