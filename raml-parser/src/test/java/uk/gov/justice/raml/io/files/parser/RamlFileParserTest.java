package uk.gov.justice.raml.io.files.parser;

import org.junit.Test;
import org.raml.model.Raml;
import uk.gov.justice.raml.io.FileTreeResolver;
import uk.gov.justice.raml.io.TierImportPattern;

import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jcooke on 08/03/16.
 */
public class RamlFileParserTest {

    @Test
    public void testLoadRaml() throws Exception {

        FileTreeResolver fileTreeResolver = new FileTreeResolver();

        List<Path> paths = fileTreeResolver.load(TestRaml.getTestRamlDirectory(), TierImportPattern.ALL);

        RamlFileParser ramlLoader = new RamlFileParser();

        List<Raml> ramls = ramlLoader.loadRaml(paths);

        assertNotNull(ramls.get(0));
        assertTrue(ramls.size() == 2);
        assertTrue(ramls.get(1) instanceof Raml);

    }
}