package dev.jort.copilot;

public class Test {
    public static void main(String[] args) {
        test();
        test(1);
    }

    public static void test(int...args){
        if (args == null){
            System.out.println("null");
            return;
        }
        System.out.println(args.length);
    }
}
