package group12.cpen391.patienttracker;

import junit.framework.Assert;

import org.junit.Test;
import static org.junit.Assert.*;


import group12.cpen391.patienttracker.serverMessageParsing.Translator;

/**
 * Created by Sean on 2017-03-15.
 */

public class TranslatorTest {
    @Test
    public void parse_gps_test() throws Exception{
        String res = Translator.parseGPS("[(0, 1, 2017-03-15 17:32:42, 49.254902, -123.236439)," +
                "(1, 1, 2017-03-15 08:02:01, 49.257967, -123.238470)]").toString();
        assertEquals("[lat/lng: (49.254902,-123.236439), lat/lng: (49.257967,-123.23847)]", res);

        assertEquals(Translator.parseGPS("[]"), null);
    }
}
