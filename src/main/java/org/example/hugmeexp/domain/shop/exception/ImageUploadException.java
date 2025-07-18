package org.example.hugmeexp.domain.shop.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.springframework.http.HttpStatus;

public class ImageUploadException extends BaseCustomException {
    public ImageUploadException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "Image upload failed.");
    }
}
