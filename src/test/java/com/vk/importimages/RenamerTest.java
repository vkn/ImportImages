package com.vk.importimages;

import java.io.File;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RenamerTest {
    
    @Test
    public void testToStardard() {
        assertThat(Renamer.toStandard("2015-05-09 15-59-09.jpg")).isEqualTo("2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("2015-05-09 15-59-09_mobi.jpg")).isEqualTo("2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("20150509_155909.jpg")).isEqualTo("2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("Foo_20150509 155909.jpg")).isEqualTo("Foo_2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("Foo_20150509 155909_HDR.jpg")).isEqualTo("Foo_2015-05-09 15-59-09_HDR_mobi.jpg");
    }
    
    @Test
    public void testGetCameraId() throws Exception {
        File file = new File("src/test/resources/photos/exif/2015-05-01 22-12-04_mobi.jpg");
        assertThat(Renamer.getCameraId(file)).isEqualTo("LG Electronics" + Renamer.CAMERA_MODEL_DELIMITER + "LG-D855");
    }
}
