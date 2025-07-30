package org.example.hugmeexp.domain.recruitment.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.example.hugmeexp.global.common.exception.code.ErrorStatus;

public class RecruitmentNotFoundException extends BaseCustomException {

    public RecruitmentNotFoundException(){
        super(ErrorStatus.RECRUITMENT_NOT_FOUND.getHttpStatus(),
              ErrorStatus.RECRUITMENT_NOT_FOUND.getMessage(),
              ErrorStatus.RECRUITMENT_NOT_FOUND.getHttpStatus().value());
    }
}
