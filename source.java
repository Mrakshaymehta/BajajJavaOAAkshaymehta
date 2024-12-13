package OA;

import java.io.*;
import java.nio.file.*;
import java.security.*;
import java.util.*;
import org.json.*;

public class DestinationHashGenerator {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <roll_number> <json_file_path>");
            return;
        }

        String rollNumber = args[0].toLowerCase().replaceAll("\\s", "");
        String jsonFilePath = args[1];

        // Step 2: Read JSON file
        String jsonContent = Files.readString(Paths.get(jsonFilePath));
        JSONObject jsonObject = new JSONObject(jsonContent);

        // Step 3: Find first occurrence of "destination"
        String destinationValue = findFirstDestination(jsonObject);
        if (destinationValue == null) {
            System.out.println("Key 'destination' not found in the JSON file.");
            return;
        }

        // Step 4: Generate random alphanumeric string
        String randomString = generateRandomString(8);

        // Step 5: Concatenate strings and generate MD5 hash
        String concatenated = rollNumber + destinationValue + randomString;
        String md5Hash = generateMD5Hash(concatenated);

        // Step 6: Format output
        String output = md5Hash + ";" + randomString;

        // Step 7: Print the output
        System.out.println(output);
    }

    private static String findFirstDestination(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            } else if (value instanceof JSONObject) {
                String result = findFirstDestination((JSONObject) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                for (Object item : (JSONArray) value) {
                    if (item instanceof JSONObject) {
                        String result = findFirstDestination((JSONObject) item);
                        if (result != null) {
                            return result;
                        }
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
