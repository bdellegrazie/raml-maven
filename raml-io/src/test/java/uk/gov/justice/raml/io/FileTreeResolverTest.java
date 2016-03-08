package uk.gov.justice.raml.io;

import org.junit.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by jcooke on 05/02/16.
 */
public class FileTreeResolverTest {


    @Test
    public void testValidateSyntax() throws Exception {

        FileTreeResolver fileTreeResolver = new FileTreeResolver();

        List<Path> results = fileTreeResolver.load(TestRaml.getTestRamlDirectory(), TierImportPattern.ALL);

        results.stream()
                .forEach(path -> System.out.println(path));

        assertNotNull(results.get(0));

        assertTrue(results.size() == 2);
    }

}