package bsu.famcs.chat.util;

import bsu.famcs.chat.model.Message;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public final class MessageUtil {

    public static final String TOKEN = "token";
    public static final String MESSAGES = "messages";
    private static final String TN = "TN";
    private static final String EN = "EN";
    private static final String NAME = "name";
    private static final String MESSAGE = "message";
    private static final String ID = "id";

    private MessageUtil() {
    }

    public static String getToken(int index) {
        Integer number = index;
        return TN + number + EN;
    }

    public static int getIndex(String token) {
        return (Integer.valueOf(token.substring(2, token.length() - 2)));
    }

    public static String generateCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy, HH:mm");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Europe/Minsk"));
        return dateFormat.format(new Date());
    }

    public static JSONObject stringToJson(String data) throws ParseException {
        JSONParser jsonParser = new JSONParser();
        return (JSONObject) jsonParser.parse(data.trim());
    }

    public static Message jsonToMessage(JSONObject json) {
        String userName = (String)json.get(NAME);
        String msgText = ((String)json.get(MESSAGE)).trim();
        String id = ((String)json.get(ID));
        if (userName != null && msgText != null) {
            Message msg =  new Message(userName, msgText, id, generateCurrentDate());
            return msg;
        }
        return null;
    }
}
