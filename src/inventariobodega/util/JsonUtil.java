package util;

import java.util.Collection;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String obj(Map<String, ?> fields) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ?> entry : fields.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append('"').append(escape(entry.getKey())).append("\":");
            sb.append(value(entry.getValue()));
        }
        sb.append('}');
        return sb.toString();
    }

    public static String array(Collection<?> items) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : items) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            sb.append(value(item));
        }
        sb.append(']');
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static String value(Object raw) {
        if (raw == null) {
            return "null";
        }
        if (raw instanceof String s) {
            return "\"" + escape(s) + "\"";
        }
        if (raw instanceof Number || raw instanceof Boolean) {
            return raw.toString();
        }
        if (raw instanceof Map<?, ?> map) {
            return obj((Map<String, ?>) map);
        }
        if (raw instanceof Collection<?> collection) {
            return array(collection);
        }
        return "\"" + escape(raw.toString()) + "\"";
    }

    public static String getString(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            return null;
        }
        int colon = json.indexOf(':', keyIndex);
        int startQuote = json.indexOf('"', colon + 1);
        if (startQuote < 0) {
            return null;
        }
        int endQuote = json.indexOf('"', startQuote + 1);
        return json.substring(startQuote + 1, endQuote);
    }

    public static Integer getInt(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            return null;
        }
        int colon = json.indexOf(':', keyIndex) + 1;
        int end = colon;
        while (end < json.length() && ",}]\n\r ".indexOf(json.charAt(end)) < 0) {
            end++;
        }
        String number = json.substring(colon, end).trim();
        if (number.isEmpty() || "null".equals(number)) {
            return null;
        }
        return Integer.parseInt(number);
    }

    public static Double getDouble(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            return null;
        }
        int colon = json.indexOf(':', keyIndex) + 1;
        int end = colon;
        while (end < json.length() && ",}]\n\r ".indexOf(json.charAt(end)) < 0) {
            end++;
        }
        String number = json.substring(colon, end).trim();
        if (number.isEmpty() || "null".equals(number)) {
            return null;
        }
        return Double.parseDouble(number);
    }
}
