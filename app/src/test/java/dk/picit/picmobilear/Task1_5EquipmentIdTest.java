package dk.picit.picmobilear;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import dk.picit.picmobilear.service.ParserService;

import static org.junit.Assert.*;

public class Task1_5EquipmentIdTest {

    private static String testString1, testString2, testString3, testString4, testString5,
            testString6, testString1Result, testString2Result, testString3Result, testString4Result,
            testString5Result, testString6Result;

    private static ParserService ps;

    @BeforeClass
    public static void setUp() throws Exception {
//    IMG_20180523_115904.jpg
        testString1 = "DACU 011268 1\n12 GB\n6\n";
        testString1Result = "DACU0112681";

//    IMG_20180523_115728.jpg
        testString2 =
                "EMCU 3658712\n22G1\n245746 3\n2261\nGROSS\nTARE\nNET\nCUBE\n30.480KG\n67.200 LB\n2.200KG\n4.850LB\n28.280KG\n62.350LB\n33.2 CUM\n1.172 CU.FT\n30.480 KGS\n67 200 LBS.\n2.200 KGS\n4.850\nZ CUFT\n1941900\nAN\nELSE\n212\n3915900\n";
        testString2Result = "EMCU3658712";

//    IMG_20180523_115714.jpg
        testString3 =
                "GL\nFNÃ‹ VI 65 87\nTAL\nICLU 245746 3\n22G1\n30.480 KGS.\nMAX. GROSS\n67200 LBS.\nINTERNATIONAL\nTARE\n2.200 KGS.\n4.850 LBS.\nNET\n28.280 KGS.\n62.350 LBS.\nCORTEN STEEL\nCONTAINER\nCU. CAP.\n33.2 CUM.\n1.172 CUFL\nCIMC\nBV 3405\n";
        testString3Result = "ICLU2457463";

//    IMG_20180523_115607.jpg
        testString4 = "ine.com\nKKFU 132415 12\n42G1\nKKFU 13 2 4 15 2\n65\n";
        testString4Result = "KKFU1324152";

//    IMG_20180523_115513.jpg
        testString5 = "KK FU 1 3 2 4 15\n";
        testString5Result = "KKFU1324152";

//    udfordring
        testString6 =
                "tex\ntex\nBSIU 1225378 B\n2261\nue Skv\nintermodal\nMAX. GROSS 30480 KGS \n67.200 LBS\n2185 KGS \n4820 LBS\n28.295 KGS\n62380 LBS \nNET\n3 1173 CU.FT\n";
        testString6Result = "BSIU2253788";

        ps = new ParserService();
    }

    @Test
    public void parseStringIsValidIMG_20180523_115904() {
        assertEquals(testString1Result, ps.visionToISO6346(testString1));
    }

    @Test
    public void parseStringIsValidIMG_20180523_115728() {
        assertEquals(testString2Result, ps.visionToISO6346(testString2));
    }


    @Test
    public void parseStringIsValidIMG_20180523_115714() {
        assertEquals(testString3Result, ps.visionToISO6346(testString3));
    }

    @Test
    public void parseStringIsValidIMG_20180523_115607() {
        assertEquals(testString4Result, ps.visionToISO6346(testString4));
    }

    @Test
    public void parseStringIsValidIMG_20180523_115513() {
        assertEquals(testString5Result, ps.visionToISO6346(testString5));
    }

    @Test
    public void parseStringIsValidUdfordring() {
        assertEquals(testString6Result, ps.visionToISO6346(testString6));
    }


}
