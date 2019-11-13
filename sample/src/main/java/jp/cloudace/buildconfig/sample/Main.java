package jp.cloudace.buildconfig.sample;

public class Main {

    public static void main(String[] args) {
        if (BuildConfig.DEBUG) {
            System.out.println("Debug mode");
        }
    }

}
