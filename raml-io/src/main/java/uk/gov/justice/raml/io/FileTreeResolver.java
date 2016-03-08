package uk.gov.justice.raml.io;

import com.google.common.io.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * Created by jcooke on 01/02/16.
 *
 */
public class FileTreeResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTreeResolver.class);

    /**
     *
     * returns a list of Paths to files found under searchDirectory where the file name matches the supplied
     * regex pattern.
     *
     * @param searchDirectory
     * @param pattern
     * @return
     */
    public List<Path> load(File searchDirectory, Pattern pattern) {

        LOGGER.debug("Loading files from : " + searchDirectory.getAbsolutePath());

        return Files.fileTreeTraverser()
                .breadthFirstTraversal(searchDirectory)
                .toList()
                .stream()
                .filter(s -> pattern.matcher(s.getPath()).matches())
                .map(File::toPath)
                .collect(Collectors.toList());
    }
}
