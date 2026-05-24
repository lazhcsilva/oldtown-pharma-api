package br.com.oldtown.pharma.shared.utils;

public class Util {

    public static String getPrefix(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        return word.substring(0, 3);
    }

}
