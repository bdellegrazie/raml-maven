package uk.gov.justice.raml.jaxrs.maven;

import org.raml.emitter.RamlEmitter;
import org.raml.model.Raml;
import uk.gov.justice.raml.core.Configuration;
import uk.gov.justice.raml.core.Generator;

import javax.json.Json;
import javax.json.JsonWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Generator for testing - the RAML and configuration are dumped to a JSON file.
 */
public class DummyGenerator implements Generator {

    @Override
    public Set<String> run(Raml raml, Configuration configuration) {

        Path outputPath = Paths.get(configuration.getOutputDirectory().getAbsolutePath(), "example.json");
        try {
            Files.createDirectories(outputPath.getParent());
            OutputStream outputStream = Files.newOutputStream(outputPath);
            JsonWriter writer = Json.createWriter(outputStream);
            writer.writeObject(Json.createObjectBuilder()
                    .add("raml", new RamlEmitter().dump(raml))
                            .add("configuration", Json.createObjectBuilder()
                                    .add("basePackageName", configuration.getBasePackageName())
                                    .add("sourceDirectory", configuration.getSourceDirectory().getPath())
                                    .add("outputDirectory", configuration.getOutputDirectory().getPath())
                            )
                            .build());
            writer.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
