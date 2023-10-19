package com.example.asynchronous_1_1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class HelloApplication extends Application {
    private static final int BUFFER_SIZE = 5;
    private static final int TOTAL_OPERATIONS = 15;

    private BlockingQueue<String> buffer = new ArrayBlockingQueue<>(BUFFER_SIZE);
    private int producerCount = 0;
    private int consumerCount = 0;
    private int activeProducers = 2;
    private int activeConsumers = 1;

    private List<Thread> producerThreads = new ArrayList<>();
    private List<Thread> consumerThreads = new ArrayList<>();
    private List<Circle> producerCircles = new ArrayList<>();
    private List<Circle> consumerCircles = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Producer-Consumer");

        HBox producersHBox = new HBox(50);

        for (int i = 0; i < activeProducers; i++) {
            Circle producerCircle = createCircle(Color.TRANSPARENT);
            producerCircles.add(producerCircle);
            producersHBox.getChildren().add(producerCircle);

            Thread producerThread = new Thread(() -> produce(producerCircle));
            producerThreads.add(producerThread);
            producerThread.start();
        }

        HBox consumersHBox = new HBox(50);

        for (int i = 0; i < activeConsumers; i++) {
            Circle consumerCircle = createCircle(Color.TRANSPARENT);
            consumerCircles.add(consumerCircle);
            consumersHBox.getChildren().add(consumerCircle);

            Thread consumerThread = new Thread(() -> consume(consumerCircle));
            consumerThreads.add(consumerThread);
            consumerThread.start();
        }

        VBox mainVBox = new VBox(50);
        mainVBox.getChildren().addAll(producersHBox, consumersHBox);

        StackPane root = new StackPane();
        root.getChildren().add(mainVBox);

        Scene scene = new Scene(root, 800, 500);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            for (Thread producerThread : producerThreads) {
                producerThread.interrupt();
            }
            for (Thread consumerThread : consumerThreads) {
                consumerThread.interrupt();
            }
            Platform.exit();
        });
    }

    private Circle createCircle(Color color) {
        Circle circle = new Circle(50, color);
        circle.setStroke(Color.BLACK);
        circle.setStrokeWidth(2);
        return circle;
    }

    private void produce(Circle circle) {
        try {
            while (producerCount < TOTAL_OPERATIONS && activeProducers > 0) {
                String product = "Product #" + Math.random();
                Thread.sleep((long) (Math.random() * 1000));
                Platform.runLater(() -> circle.setFill(Color.BLUE));
                Thread.sleep(5000);
                buffer.put(product);

                System.out.println("Produced: " + product);

                Platform.runLater(() -> circle.setFill(Color.GREEN));
                Thread.sleep(500);

                Platform.runLater(() -> circle.setFill(Color.TRANSPARENT));
                Thread.sleep((long) (Math.random() * 2000));
                producerCount++;

                if (producerCount >= TOTAL_OPERATIONS) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void consume(Circle circle) {
        try {
            while (consumerCount < TOTAL_OPERATIONS && activeConsumers > 0) {
                Platform.runLater(() -> circle.setFill(Color.RED));
                Thread.sleep(5000);

                String product = buffer.take();
                Platform.runLater(() -> circle.setFill(Color.GREEN));
                Thread.sleep(500);

                System.out.println("Consumed: " + product);

                Platform.runLater(() -> circle.setFill(Color.TRANSPARENT));
                Thread.sleep((long) (Math.random() * 2000) + 1000);
                consumerCount++;

                if (consumerCount >= TOTAL_OPERATIONS) {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}