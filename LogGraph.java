
/**
 * @author Mohammad Naushad Bhat
 * @version 1.5 16/04/2018
 * @since 1.0 1/04/2018
 */

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogGraph extends Application {

    /**
     * This function checks the operating System type. This function is needed to make the program platform independent.
     * This program however supports only Windows, Mac , unix and Linux Operating Systems
     *
     * @return  It returns the suitable path based on the Operating System
     */
    public static String checkOS() {
        String store = "";
        String OS = System.getProperty("os.name").toLowerCase();
        if(OS.indexOf("win") >= 0){
            store = "C:/";
        } else if(OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("ix") > 0 ){
            store = "/home/";
        } else  if(OS.indexOf("mac") >= 0){
            store = "/home/";
        } else{
            return null;
        }
        return store;
    }

    /**
     * start method is a method in Application class. Here we override that method
     * In this method we create a graph that updates automatically
     * @param stage
     */
    @Override
    public void start(Stage stage) {
        final CategoryAxis xAxis = new CategoryAxis();
        final CategoryAxis yAxis = new CategoryAxis();

        xAxis.setLabel("Time");
        yAxis.setLabel("File Size");

        XYChart.Series<String, String> series = new XYChart.Series<>();
        series.setName("Log");

        LineChart<String,String> chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        chart.getData().add(series); //series added to chart

        Scene scene = new Scene(chart, 700, 400);
        stage.setScene(scene);

        stage.show();

        //Threads used to make the graph update automatically
        Thread updateThread = new Thread(() -> {
            while (true) {
                try {
                    try {


                        //The file ,size of which is to be saved into another file (a.txt)
                        File file = new File(checkOS() + "a.txt");
                        float store = file.length();
                        store = store/1024 ;

                    /*
                    * Here FileOutputStream takes two arguments.
                    *   One is the location of the file (b.txt) in which the log is to be written
                    *  And another is boolean. i.e; true meaning that is data in the file won't be overewritten.
                    */
                        FileOutputStream out = new FileOutputStream(checkOS() + "b.txt", true);
                        Date date = new Date();
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss"); //To show time only and not the date

                        //The lines below prints the length of file in kb on screen and the time on screen
                        System.out.println("File length: " + store + " KB" );
                        System.out.println("At time: " + simpleDateFormat.format(date));

                        //Here we store the data which is to be written into file b.txt into variable temp of String type
                        String temp = "At " + simpleDateFormat.format(date).toString() + ". The size of file is -> " + String.valueOf(store) + " KB \n";
                        //Now we convert String to byte
                        byte[] log = temp.getBytes();

                        //We write the data into file
                        out.write(log);
                        out.flush();

                        String xTime = simpleDateFormat.format(date).toString();
                        String ySize = String.valueOf(store);
                        Platform.runLater(() -> series.getData().add(new XYChart.Data<>(xTime, ySize)));


                    } catch(Exception ex) {
                        ex.printStackTrace();
                    }


                    /*
                        The program needs to write the size of file into another file after every 10 seconds
                        So, we make the thread sleep for 10 seconds (10000 ms).
                        It will repeate as it is in while loop and write the size every 10 seconds
                     */
                        Thread.sleep(10000);


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        updateThread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
