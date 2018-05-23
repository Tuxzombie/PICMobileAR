package dk.picit.picmobilear;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import dk.picit.picmobilear.service.VisionService;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("dk.picit.picmobilear", appContext.getPackageName());
    }

    @Before
    public void setUp() throws Exception
    {
//    IMG_20180523_115904.jpg
        String testString1 = "DACU 011268 1\n12 GB\n6\n";
        String testString1Result = "DACU0112681";

//    IMG_20180523_115728.jpg
        String testString2 = "EMCU 3658712\n22G1\n245746 3\n2261\nGROSS\nTARE\nNET\nCUBE\n30.480KG\n67.200 LB\n2.200KG\n4.850LB\n28.280KG\n62.350LB\n33.2 CUM\n1.172 CU.FT\n30.480 KGS\n67 200 LBS.\n2.200 KGS\n4.850\nZ CUFT\n1941900\nAN\nELSE\n212\n3915900\n";
        String testString2Result = "EMCU3658712";

//    IMG_20180523_115714.jpg
        String testString3 = "GL\nFNÃ‹ VI 65 87\nTAL\nICLU 245746 3\n22G1\n30.480 KGS.\nMAX. GROSS\n67200 LBS.\nINTERNATIONAL\nTARE\n2.200 KGS.\n4.850 LBS.\nNET\n28.280 KGS.\n62.350 LBS.\nCORTEN STEEL\nCONTAINER\nCU. CAP.\n33.2 CUM.\n1.172 CUFL\nCIMC\nBV 3405\n";
        String testString3Result = "ICLU2457463";

//    IMG_20180523_115607.jpg
        String testString4 = "ine.com\nKKFU 132415 12\n42G1\nKKFU 13 2 4 15 2\n65\n";
        String testString4Result = "KKFU1324152";

//    IMG_20180523_115513.jpg
        String testString5 = "KK FU 1 3 2 4 15\n";
        String testString5Result = "KKFU1324152";

//    udfordring
        String testString6 = "tex\ntex\nBSIU 1225378 B\n2261\nue Skv\nintermodal\nMAX. GROSS 30480 KGS \n67.200 LBS\n2185 KGS \n4820 LBS\n28.295 KGS\n62380 LBS \nNET\n3 1173 CU.FT\n";
        String testString6Result = "BSIU2253788";
    }
    @Test
    public void vision_isCorrect()
    {
        Context appContext = InstrumentationRegistry.getTargetContext();
        VisionService vs = new VisionService(appContext);

        assertEquals("", vs.createJSONPOST(""));
    }
}
