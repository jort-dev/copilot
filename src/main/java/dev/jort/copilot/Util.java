package dev.jort.copilot;

import java.util.List;

public class Util {

    public static boolean containsAny(String string, List<String> toCheck){
        for (String s : toCheck){
            if (string.contains(s)){
                return true; //WILLOW_DEAD contains DEAD
            }
        }
        return false;
    }

    public static String colorString(String s, String hex){
        //color string
        String result = "<col=" + hex + ">" + s + "</col>";
        return result;
    }

    public static String arrayToString(int... ints) {
        String result = "";
        for (int i : ints) {
            result += i + ", ";
        }
        return result.substring(0, result.length() - 2);
    }
}
