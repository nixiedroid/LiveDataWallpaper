package com.ustwo.lwp.wallpapers.util.owm.enums;

public enum ErrorResult {
    NO_INTERNET("You are not connected to internet"),
    INVALID_API_KEY("API key is invalid"),
    INTERNAL_INVALID_URL("INTERNAL: API url could not be created"),
    INTERNAL_TOO_MUCH_REQUESTS("INTERNAL: Amount of requests to API Exceeded 60 requests per minute"),
    INTERNAL_FAILED_TO_PARSE_JSON("INTERNAL: Failed to parse JSON"),
    INTERNAL_SERVER_ERROR("Server is down"),
    INTERNAL_FAILED_TO_PARSE_JSON_NULL("INTERNAL: Failed to parse JSON. Null pointer Exception"),
    UNKNOWN("Unknown error");
    private final String message;

    ErrorResult(String message) {
        this.message = message;
    }
    public String getMessage(){
        return message;
    }
}
