package com.vk.importimages;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class RenamerTest {
    
    @Test
    public void testToStardard() {
        assertThat(Renamer.toStandard("2015-05-09 15-59-09.jpg")).isEqualTo("2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("2015-05-09 15-59-09_mobi.jpg")).isEqualTo("2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("20150509_155909.jpg")).isEqualTo("2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("Foo_20150509 155909.jpg")).isEqualTo("Foo_2015-05-09 15-59-09_mobi.jpg");
        assertThat(Renamer.toStandard("Foo_20150509 155909_HDR.jpg")).isEqualTo("Foo_2015-05-09 15-59-09_HDR_mobi.jpg");
    }
    
}
