package com.rydeon.andr.app;

/**
 * Created by HP on 07/06/2018.
 */

public class AppConfig {

    public static final String PLACES = "places";
    private static String HOST = "http://192.168.43.75:5000/";

    public static String SIGNUP = HOST+ "resources/rydeon/signup";
  //  public static String LOGIN = HOST + "auth/login";
    public static String LOGIN = HOST + "resources/fbsignin";
    public static String VERIFY_CODE = HOST+"resources/rydeon/signup/verifycode";
    public static String RESEND_VERIFICATION =HOST+ "resources/rydeon/signup/resendcode";
    public static String CAR = HOST +"api/rydeon/car";
    public static String MODEL = HOST +"api/rydeon/model";
    public static String MAKE = HOST +"api/rydeon/make";
    public static String CREATE_JOURNEY = HOST +"api/rydeon/journey";
    public static String CONTENT_TYPE = "application/json;charset=utf-8";

}
