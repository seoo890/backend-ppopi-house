package com.ppopi.ppopihouse.diagnosis.service;

import com.ppopi.ppopihouse.diagnosis.dto.external.ImageValidationResponse;
import org.springframework.web.multipart.MultipartFile;

public interface ImageValidationClient {

    ImageValidationResponse validate(MultipartFile image);

}
