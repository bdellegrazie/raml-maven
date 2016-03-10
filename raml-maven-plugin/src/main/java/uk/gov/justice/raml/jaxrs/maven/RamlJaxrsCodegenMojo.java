package uk.gov.justice.raml.jaxrs.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import uk.gov.justice.raml.core.Configuration;
import uk.gov.justice.raml.core.Generator;
import uk.gov.justice.raml.io.FileTreeScanner;
import uk.gov.justice.raml.io.files.parser.RamlFileParser;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.Collection;

import static java.text.MessageFormat.format;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

@Mojo(name = "generate", requiresProject = true, threadSafe = false, requiresDependencyResolution = COMPILE_PLUS_RUNTIME, defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class RamlJaxrsCodegenMojo extends AbstractMojo {

    private static final String FAILED_TO_CLEAN_DIRECTORY = "Failed to clean directory: {0}";
    private static final String LOOKING_FOR_RAML_FILES = "Looking for RAML files in and below: {0}";
    private static final String SOURCE_DIRECTORY_MUST_BE_PROVIDED = "SourceDirectory must be provided";
    private static final String THE_PROVIDED_PATH_DOESN_T_REFER_TO_A_VALID_DIRECTORY = "The provided path doesn''t refer to a valid directory: {0}";

    private Generator generator;

    @Parameter(property = "generatorName", required = true)
    private String generatorName;

    @Parameter(defaultValue = "${project}")
    private MavenProject project;

    /**
     * Target directory for generated Java source files.
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources")
    private File outputDirectory;

    /**
     * Directory location of the RAML file(s).
     */
    @Parameter(property = "sourceDirectory", defaultValue = "${basedir}/src/raml")
    private File sourceDirectory;

    /**
     * Base package name used for generated Java classes.
     */
    @Parameter(property = "basePackageName", required = true)
    private String basePackageName;


    public RamlJaxrsCodegenMojo() {
    }

    RamlJaxrsCodegenMojo(Generator generator) {
        this.generator = generator;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        instantiateGenerator();
        verify(sourceDirectory);
        prepare(outputDirectory);
        project.addCompileSourceRoot(outputDirectory.getPath());
        project.addTestCompileSourceRoot(outputDirectory.getPath());
        process(ramlFiles());
    }

    private void instantiateGenerator() {
        if (generator == null) {
            try {
                Class<?> clazz = Class.forName(generatorName);
                Constructor<?> constructor = clazz.getConstructor();
                generator = (Generator) constructor.newInstance();
            } catch (ClassNotFoundException|NoSuchMethodException|IllegalAccessException|InstantiationException|InvocationTargetException e) {
                throw new IllegalArgumentException(String.format("Could not instantiate generator %s", generatorName), e);
            }
        }
    }

    private void process(Collection<Path> ramlFiles) throws MojoExecutionException {
        try {
            new RamlFileParser()
                    .parse(sourceDirectory.toPath(), ramlFiles)
                    .stream()
                    .forEach(raml -> generator.run(raml, configuration()));
        } catch (final Exception e) {
            throw new MojoExecutionException("Error while running generator", e);
        }
    }

    private Configuration configuration() {
        final Configuration configuration = new Configuration();

        configuration.setBasePackageName(basePackageName);
        configuration.setOutputDirectory(outputDirectory);
        configuration.setSourceDirectory(sourceDirectory);
        return configuration;
    }

    private void verify(File sourceDirectory2) throws MojoExecutionException {
        if (sourceDirectory2 == null) {
            throw new MojoExecutionException(SOURCE_DIRECTORY_MUST_BE_PROVIDED);
        }
    }

    private void prepare(File outputDirectory) throws MojoExecutionException {
        try {
            FileUtils.forceMkdir(outputDirectory);
            FileUtils.cleanDirectory(outputDirectory);
        } catch (final IOException ioe) {
            throw new MojoExecutionException(format(FAILED_TO_CLEAN_DIRECTORY, outputDirectory), ioe);
        }
    }

    private Collection<Path> ramlFiles() throws MojoExecutionException {

        final String[] includes = {"**/*.raml"};
        final String[] excludes = {};

        if (!sourceDirectory.isDirectory()) {
            throw new MojoExecutionException(
                    format(THE_PROVIDED_PATH_DOESN_T_REFER_TO_A_VALID_DIRECTORY, sourceDirectory));
        }

        getLog().info(format(LOOKING_FOR_RAML_FILES, sourceDirectory));

        try {
            return new FileTreeScanner().find(sourceDirectory.toPath(), includes, excludes);
        } catch (IOException ex) {
            throw new MojoExecutionException("Failed to scan for RAML files", ex);
        }
    }

    void setSourceDirectory(File sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    void setProject(MavenProject project) {
        this.project = project;
    }

    void setBasePackageName(String basePackageName) {
        this.basePackageName = basePackageName;
    }

    void setGeneratorName(String generatorName) {
        this.generatorName = generatorName;
    }
}
