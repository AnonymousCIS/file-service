package org.anonymous.file;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "file.upload") // project-config 참고
public class FileProperties {

    private String url;

    private String path;
}