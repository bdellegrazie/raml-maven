package uk.gov.justice.raml.io.files.parser;

import java.io.File;

/**
 * Created by jcooke on 01/02/16.
 */
public class TestRaml {

    private TestRaml() {}

    public static File getTestRamlDirectory() {

        return new File("src/test/resources/raml/");

    }


    public static File getTestNoRamlDirectory() {

        return new File("src/test/resources/noraml/");

    }


    public static File getTestInvalidRamlDirectory() {

        return new File("src/test/resources/invalidraml/");

    }
}
