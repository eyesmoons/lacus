package com.lacus.admin.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UploadDTO {

    private String url;
    private String fileName;
    private String newFileName;
    private String originalFilename;

}
