package jp.hayamiti.utils;

final public class MyLog {
    public MyLog(){

    }
    final public static void info(String LOG_TAG, String message){
        System.out.print("[");
        System.out.print(LOG_TAG);
        System.out.print("]");
        System.out.print("[");
        System.out.print("info");
        System.out.print("]");
        System.out.print(message);
        System.out.println("");
    }

    final public static void error(String LOG_TAG, String message){
        System.out.print("[");
        System.out.print(LOG_TAG);
        System.out.print("]");
        System.out.print("[");
        System.out.print("error");
        System.out.print("]");
        System.out.print(message);
        System.out.println("");
    }
}
