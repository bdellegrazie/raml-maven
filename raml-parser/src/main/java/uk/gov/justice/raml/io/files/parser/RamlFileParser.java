package uk.gov.justice.raml.io.files.parser;

import org.raml.model.Raml;
import org.raml.parser.loader.FileResourceLoader;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Takes a list of file paths and loads the files and parses them into a @code List<raml>
 *
 * Created by jcooke on 27/01/16.
 */
public class RamlFileParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(RamlFileParser.class);

    public RamlFileParser() {

        LOGGER.debug("RamlLoader created");

    }

    public List<Raml> loadRaml(List<Path> ramlURIs) {


        return ramlURIs
                .stream()
                .map(aFile -> {
                    LOGGER.info(new StringBuilder().append("Loading file ").append(aFile).toString());

                    return new RamlDocumentBuilder(new FileResourceLoader(aFile.toFile().getParent())).build(aFile.toFile().getName());
                })
                .collect(Collectors.toList());
    }

}
