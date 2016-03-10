package uk.gov.justice.raml.jaxrs.maven;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.raml.emitter.RamlEmitter;
import org.raml.model.Raml;
import uk.gov.justice.raml.core.Configuration;
import uk.gov.justice.raml.core.Generator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class RamlJaxrsCodegenMojoTest {
    private RamlJaxrsCodegenMojo pluginMojo;
    private MavenProject project;

    @Rule
    public TemporaryFolder outputFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder sourceDirectory = new TemporaryFolder();

    @Mock
    private Generator generator;

    @Before
    public void before() throws IOException {
        initMocks(this);

        pluginMojo = new RamlJaxrsCodegenMojo(generator);
        pluginMojo.setSourceDirectory(sourceDirectory.getRoot());
        pluginMojo.setOutputDirectory(outputFolder.getRoot());
        project = new MavenProject();
        pluginMojo.setProject(project);

    }

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldThrowExceptionIfSourceDirectoryNotSet() throws Exception {
        pluginMojo.setSourceDirectory(null);
        thrown.expect(MojoExecutionException.class);
        thrown.expectMessage("SourceDirectory must be provided");
        pluginMojo.execute();
    }

    @Test
    public void shouldPassConfigurationToTheGenerator() throws Exception {
        File outputDirectory = new File("oDir123");
        FileUtils.write(sourceDirectory.newFile("file1.raml"), "#%RAML 0.8");
        String basePackageName = "base.pckge.abc";

        pluginMojo.setOutputDirectory(outputDirectory);
        pluginMojo.setSourceDirectory(sourceDirectory.getRoot());
        pluginMojo.setBasePackageName(basePackageName);
        pluginMojo.execute();

        ArgumentCaptor<Configuration> configCaptor = ArgumentCaptor.forClass(Configuration.class);
        verify(generator).run(any(Raml.class), configCaptor.capture());
        Configuration configuration = configCaptor.getValue();
        assertThat(configuration.getBasePackageName(), is(basePackageName));
        assertThat(configuration.getSourceDirectory(), is(sourceDirectory.getRoot()));
        assertThat(configuration.getOutputDirectory(), is(outputDirectory));

    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddCompileSourceRootToMavenProject() throws Exception {
        File outputDir = new File("oDir1456");
        pluginMojo.setOutputDirectory(outputDir);
        pluginMojo.execute();
        assertThat((project.getCompileSourceRoots()), hasItem(outputDir.getAbsolutePath()));
    }

    @Test
    public void shouldPassRamlDocumentsToGenerator() throws Exception {

        File ramlFile = sourceDirectory.newFile("file1.raml");
        String ramlString1 = "#%RAML 0.8\nbaseUri: \"http://a:8080/\"\n";
        FileUtils.write(ramlFile, ramlString1);
        File ramlFile2 = sourceDirectory.newFile("file2.raml");
        String ramlString2 = "#%RAML 0.8\nbaseUri: \"http://b:8080/\"\n";
        FileUtils.write(ramlFile2, ramlString2);
        pluginMojo.execute();

        ArgumentCaptor<Raml> ramlCaptor = ArgumentCaptor.forClass(Raml.class);
        verify(generator, times(2)).run(ramlCaptor.capture(), any(Configuration.class));

        List<Raml> ramls = ramlCaptor.getAllValues();

        RamlEmitter emitter = new RamlEmitter();
        assertThat(
                ramls.stream()
                        .map(emitter::dump)
                        .collect(Collectors.toSet()),
                containsInAnyOrder(
                        equalTo(ramlString1),
                        equalTo(ramlString2)
                )
        );
    }

    @Test
    public void shouldCreateEmptyRamlDocumentFromEmptyFile() throws Exception {
        sourceDirectory.newFile("file3.raml");
        pluginMojo.execute();

        ArgumentCaptor<Raml> ramlCaptor = ArgumentCaptor.forClass(Raml.class);
        verify(generator).run(ramlCaptor.capture(), any(Configuration.class));

        Raml raml = ramlCaptor.getValue();

        RamlEmitter emitter = new RamlEmitter();
        assertThat(emitter.dump(raml), equalTo("#%RAML 0.8\n"));
    }

    @Test(expected = MojoExecutionException.class)
    public void shouldNotProcessInvalidRamlFile() throws Exception {

        File ramlFile = sourceDirectory.newFile("file1.raml");
        FileUtils.write(ramlFile, "abcde");
        pluginMojo.execute();
    }

    @Test
    public void shouldNotProcessNonRAMLFile() throws Exception {

        File ramlFile = sourceDirectory.newFile("file1.notraml");
        FileUtils.write(ramlFile, "#%RAML 0.8");

        pluginMojo.execute();

        verifyZeroInteractions(generator);
    }

}
