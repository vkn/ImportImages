package com.vk.importimages;

import java.nio.file.FileSystems;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ImporterTest {
    
    @Test
    public void testGetTargetDirPath() {
        String workingDir = System.getProperty("user.dir");
        String sep = FileSystems.getDefault().getSeparator();
        Importer importer = new Importer(null, null, workingDir + "/src/test/resources/photos");
        assertThat(importer.getTargetDirPath("2015-05-09 15-59-09.jpg")).endsWith(String.format("%s2015%s2015-05-09", sep, sep));
        assertThat(importer.getTargetDir("2015", "2015-03-31").getName()).isEqualTo("2015-03-31 Barcelona");
        assertThat(importer.getTargetDir("2015", "2015-01-01").getName()).isEqualTo("2015-01-01");
    }
    
}
