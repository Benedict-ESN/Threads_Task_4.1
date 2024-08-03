package ru.netology;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int TEXT_COUNT = 10_000;
    private static final int TEXT_LENGTH = 100_000;
    private static final int QUEUE_CAPACITY = 101;


    private static final BlockingQueue<String> queueA = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueB = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
    private static final BlockingQueue<String> queueC = new ArrayBlockingQueue<>(QUEUE_CAPACITY);

    private static String maxTextA;
    private static String maxTextB;
    private static String maxTextC;

    private static int maxCountA = 0;
    private static int maxCountB = 0;
    private static int maxCountC = 0;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(4);

        executor.submit(() -> {
            try {
                for (int i = 0; i < TEXT_COUNT; i++) {
                    String text = generateText("abc", TEXT_LENGTH);
                    queueA.put(text);
                    queueB.put(text);
                    queueC.put(text);
                }
                queueA.put("STOP");
                queueB.put("STOP");
                queueC.put("STOP");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.submit(() -> {
            try {
                while (true) {
                    String text = queueA.take();
                    if (text.equals("STOP")) break;
                    int countA = countChar(text, 'a');
                    if (countA > maxCountA) {
                        maxCountA = countA;
                        maxTextA = text;

                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.submit(() -> {
            try {
                while (true) {
                    String text = queueB.take();
                    if (text.equals("STOP")) break;
                    int countB = countChar(text, 'b');
                    if (countB > maxCountB) {
                        maxCountB = countB;
                        maxTextB = text;

                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.submit(() -> {
            try {
                while (true) {
                    String text = queueC.take();
                    if (text.equals("STOP")) break;
                    int countC = countChar(text, 'c');
                    if (countC > maxCountC) {
                        maxCountC = countC;
                        maxTextC = text;

                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        executor.shutdown();
        while (!executor.isTerminated()) {
            Thread.sleep(100);
        }

        System.out.println("Максимальное количество 'a': " + maxCountA);
        System.out.println("Строка: " + maxTextA.substring(0, 100) + "...");
        System.out.println("Максимальное количество 'b': " + maxCountB);
        System.out.println("Строка: " + maxTextB.substring(0, 100) + "...");
        System.out.println("Максимальное количество 'c': " + maxCountC);
        System.out.println("Строка: " + maxTextC.substring(0, 100) + "...");
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }

    private static int countChar(String text, char ch) {
        int count = 0;
        for (char c : text.toCharArray()) {
            if (c == ch) {
                count++;
            }
        }
        return count;
    }
}