package org.example.hugmeexp.domain.recruitment.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.example.hugmeexp.global.common.exception.code.ErrorStatus;

public class RecruitmentBookmarkNotFoundException extends BaseCustomException {

    public RecruitmentBookmarkNotFoundException(){
        super(ErrorStatus.RECRUITMENTBOOKMARK_NOT_FOUND.getHttpStatus(),
                ErrorStatus.RECRUITMENTBOOKMARK_NOT_FOUND.getMessage(),
                ErrorStatus.RECRUITMENTBOOKMARK_NOT_FOUND.getHttpStatus().value());
    }
}
