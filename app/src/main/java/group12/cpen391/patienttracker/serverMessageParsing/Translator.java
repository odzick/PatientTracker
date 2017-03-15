package group12.cpen391.patienttracker.serverMessageParsing;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Sean on 2017-03-15.
 * Parses an SQL output statement that contains GPS information.
 * Creates a new ArrayList<LatLng> containing the results.
 */
public class Translator{
    public static ArrayList<LatLng> parseGPS(String message) {
        int lhs = message.indexOf("["), rhs = message.indexOf("]");
        String[] locations;
        ArrayList<LatLng> results = new ArrayList<>();

        if (rhs - lhs < 4) return null;
        message = message.substring(lhs + 2, rhs - 2);
        locations = message.split(Pattern.quote("),("));

        for (String loc : locations) {
            String[] fields = loc.split(",");
            if(fields.length < 5) return null;
            results.add(new LatLng(Double.parseDouble(fields[3]), Double.parseDouble(fields[4])));
        }

        return results;
    }
}
