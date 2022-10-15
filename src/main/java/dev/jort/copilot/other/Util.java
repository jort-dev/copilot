package dev.jort.copilot.other;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Util {

    public static boolean containsAny(String string, String... stringsToCheck) {
        for (String s : stringsToCheck) {
            if (string.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public static boolean arrayContains(int numberToCheck, int... numbers) {
        if(numbers == null){
            return false;
        }
        for (int number : numbers) {
            if (number == numberToCheck) {
                return true;
            }
        }
        return false;
    }

    public static String colorString(String s, String hex) {
        return "<col=" + hex + ">" + s + "</col>";
    }

    public static String listToString(List<? extends Object> items) {
        return items.stream().map(Object::toString)
                .collect(Collectors.joining(", "));
    }

    public static int[] concatArrays(int[] arr1, int... arr2) {
        return Stream.concat(Arrays.stream(arr1).boxed(), Arrays.stream(arr2).boxed()).mapToInt(i -> i).toArray();
    }

    public static String capitalize(String str){
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
