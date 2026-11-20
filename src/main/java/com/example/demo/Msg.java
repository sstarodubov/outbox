package com.example.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public record Msg(String f2, String f3, int f4, int f5) {

    private static final String CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    public static int generateInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static String generate(int length) {
        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(
                    CHARACTERS.charAt(ThreadLocalRandom.current().nextInt(CHARACTERS.length()))
            );
        }
        return result.toString();
    }

    public static List<Msg> generateRnd(int n) {
        var list = new ArrayList<Msg>(n);
        for (int i = 0; i < n; i++) {
            list.add(new Msg(generate(10), generate(10), generateInt(), generateInt()));
        }
        return list;
    }
}
