package ro.as_mi.sniff.sniffevents;


public class Util {

    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    public static final String EMAIL = "email";
    public static final String USER_NAME = "user_name";

    public final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public final static String SENDER_ID = "sniff-1175";

    public static String base_url = "http://sniff.as-mi.ro/services/";

    public final static String  register_url=base_url+"MobileRegister.php";
    public final static String  send_chat_url=base_url+"sendChatmessage.php";


}
