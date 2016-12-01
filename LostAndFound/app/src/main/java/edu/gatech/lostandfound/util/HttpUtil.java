package edu.gatech.lostandfound.util;

/**
 * Created by abhishekchatterjee on 11/30/16.
 */
public class HttpUtil {
    public static final String BASE_URL = "http://73.82.210.153:8080/lost_and_found";
    public static final String REGISTER_USER_ENDPOINT = BASE_URL + "/register_user";
    public static final String REPORT_LOST_OBJECT_ENDPOINT = BASE_URL + "/report_lost_object";
    public static final String REPORT_FOUND_OBJECT_ENDPOINT = BASE_URL + "/report_found_object";
    public static final String GET_POTENTIAL_FOUND_OBJECTS_ENDPOINT = BASE_URL + "/find_matching_objects";
    public static final String CLAIM_OBJECT_ENDPOINT = BASE_URL +  "/claim_object";
    public static final String SEND_IMAGE_ENDPOINT = BASE_URL + "/image_upload";
    public static final String GET_IMAGE_ENDPOINT = BASE_URL + "/get_image";

    private HttpUtil() {

    }
}
