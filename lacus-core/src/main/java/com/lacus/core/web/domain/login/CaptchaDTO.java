package com.lacus.core.web.domain.login;

import lombok.Data;

@Data
public class CaptchaDTO {

    private Boolean isCaptchaOn;
    private String uuid;
    private String img;

}
