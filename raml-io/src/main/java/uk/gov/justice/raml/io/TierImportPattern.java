package uk.gov.justice.raml.io;

import java.util.regex.Pattern;

/**
 * Created by jcooke on 27/01/16.
 */
public final class TierImportPattern {

    public static final Pattern ALL = Pattern.compile(".*\\.raml");

    private TierImportPattern(){

    }
}
