package com.unibuc.inventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class InsufficientStockException extends BaseException{
    public InsufficientStockException(String message){
        super(message);
    }
}
