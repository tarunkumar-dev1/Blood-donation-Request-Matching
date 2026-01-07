package com.example.blood.util;

import com.example.blood.model.ContactMessage;
import com.example.blood.model.Donor;
import com.example.blood.model.MatchRequest;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JsonUtil {
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private JsonUtil() {}

    public static String escape(String input) {
        if (input == null) return "";
        return input
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // Generic method to convert Map to JSON
    public static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) sb.append(",");
            first = false;
            sb.append("\"").append(escape(entry.getKey())).append("\":");
            sb.append(valueToJson(entry.getValue()));
        }
        sb.append("}");
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    private static String valueToJson(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escape((String) value) + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else if (value instanceof Map) {
            return toJson((Map<String, Object>) value);
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.stream()
                    .map(JsonUtil::valueToJson)
                    .collect(Collectors.joining(",", "[", "]"));
        } else {
            return "\"" + escape(value.toString()) + "\"";
        }
    }

    public static String donorToJson(Donor donor) {
        return new StringBuilder()
                .append('{')
                .append("\"id\":\"").append(escape(donor.getId())).append("\",")
                .append("\"name\":\"").append(escape(donor.getName())).append("\",")
                .append("\"dob\":\"").append(escape(donor.getDob())).append("\",")
                .append("\"bloodType\":\"").append(escape(donor.getBloodType())).append("\",")
                .append("\"phone\":\"").append(escape(donor.getPhone())).append("\",")
                .append("\"email\":\"").append(escape(donor.getEmail())).append("\",")
                .append("\"address\":\"").append(escape(donor.getAddress())).append("\",")
                .append("\"pincode\":\"").append(escape(donor.getPincode())).append("\",")
                .append("\"center\":\"").append(escape(donor.getCenter())).append("\",")
                .append("\"history\":\"").append(escape(donor.getHistory())).append("\",")
                .append("\"surgeries\":\"").append(escape(donor.getSurgeries())).append("\",")
                .append("\"createdAt\":\"").append(escape(ISO.format(donor.getCreatedAt()))).append("\"")
                .append('}')
                .toString();
    }

    public static String donorsToJson(List<Donor> donors) {
        return donors.stream().map(JsonUtil::donorToJson).collect(Collectors.joining(",", "[", "]"));
    }

    public static String matchToJson(MatchRequest request) {
        return new StringBuilder()
                .append('{')
                .append("\"id\":\"").append(escape(request.getId())).append("\",")
                .append("\"fullname\":\"").append(escape(request.getFullname())).append("\",")
                .append("\"email\":\"").append(escape(request.getEmail())).append("\",")
                .append("\"bloodtype\":\"").append(escape(request.getBloodtype())).append("\",")
                .append("\"pincode\":\"").append(escape(request.getPincode())).append("\",")
                .append("\"note\":\"").append(escape(request.getNote())).append("\",")
                .append("\"createdAt\":\"").append(escape(ISO.format(request.getCreatedAt()))).append("\"")
                .append('}')
                .toString();
    }

    public static String matchesToJson(List<MatchRequest> matches) {
        return matches.stream().map(JsonUtil::matchToJson).collect(Collectors.joining(",", "[", "]"));
    }

    public static String contactToJson(ContactMessage message) {
        return new StringBuilder()
                .append('{')
                .append("\"id\":\"").append(escape(message.getId())).append("\",")
                .append("\"name\":\"").append(escape(message.getName())).append("\",")
                .append("\"email\":\"").append(escape(message.getEmail())).append("\",")
                .append("\"message\":\"").append(escape(message.getMessage())).append("\",")
                .append("\"createdAt\":\"").append(escape(ISO.format(message.getCreatedAt()))).append("\"")
                .append('}')
                .toString();
    }

    public static String contactsToJson(List<ContactMessage> messages) {
        return messages.stream().map(JsonUtil::contactToJson).collect(Collectors.joining(",", "[", "]"));
    }

    public static String matchToAdminJson(com.example.blood.model.Match match) {
        return new StringBuilder()
                .append('{')
                .append("\"matchId\":\"").append(escape(match.getMatchId())).append("\",")
                .append("\"donorName\":\"").append(escape(match.getDonorName())).append("\",")
                .append("\"donorPhone\":\"").append(escape(match.getDonorPhone())).append("\",")
                .append("\"requestName\":\"").append(escape(match.getRequestName())).append("\",")
                .append("\"requestEmail\":\"").append(escape(match.getRequestEmail())).append("\",")
                .append("\"bloodType\":\"").append(escape(match.getBloodType())).append("\",")
                .append("\"status\":\"").append(escape(match.getStatus())).append("\",")
                .append("\"priority\":\"").append(escape(match.getPriority())).append("\"")
                .append('}')
                .toString();
    }
}
