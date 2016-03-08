package uk.gov.justice.raml.io;

import org.junit.Test;

import static net.trajano.commons.testing.UtilityClassTestUtil.assertUtilityClassWellDefined;

/**
 * Created by jcooke on 15/02/16.
 */
public class TierImportPatternTest {

    /**
     * Tests the private constructor & that class is well defined.
     * http://www.trajano.net/2013/04/covering-utility-classes/
     * https://github.com/trajano/commons-testing
     */
    @Test
    public void isUtilityClassWellDefined() {
        assertUtilityClassWellDefined(TierImportPattern.class);
    }

}