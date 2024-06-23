package com.unibuc.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DuplicateEntityException extends BaseException {
    public DuplicateEntityException(String message){
        super(message);
    }
}
