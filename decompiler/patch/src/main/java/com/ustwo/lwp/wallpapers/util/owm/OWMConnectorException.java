package com.ustwo.lwp.wallpapers.util.owm;

import com.ustwo.lwp.wallpapers.util.owm.enums.ErrorResult;

public class OWMConnectorException extends Exception{
    public OWMConnectorException(ErrorResult errorResult){
        super(errorResult.getMessage());
    }

    public OWMConnectorException(String message) {
        super(message);
    }
}
