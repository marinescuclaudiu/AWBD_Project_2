package com.unibuc.order.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BaseException extends RuntimeException{
    public BaseException(String message){
        super(message);
    }
}