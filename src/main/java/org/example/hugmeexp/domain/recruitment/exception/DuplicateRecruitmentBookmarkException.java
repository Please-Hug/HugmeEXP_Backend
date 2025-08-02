package org.example.hugmeexp.domain.recruitment.exception;

import org.example.hugmeexp.global.common.exception.BaseCustomException;
import org.example.hugmeexp.global.common.exception.code.ErrorStatus;

public class DuplicateRecruitmentBookmarkException extends BaseCustomException {

    public DuplicateRecruitmentBookmarkException(){
        super(ErrorStatus.DUPLICATE_BOOKMARK.getHttpStatus(),
                ErrorStatus.DUPLICATE_BOOKMARK.getMessage(),
                ErrorStatus.DUPLICATE_BOOKMARK.getHttpStatus().value());
    }
}
