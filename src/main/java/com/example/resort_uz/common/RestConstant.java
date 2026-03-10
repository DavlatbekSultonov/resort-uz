package com.example.resort_uz.common;

public class RestConstant {
    String PHONE_NUMBER_REGEX = "^[+]*[(]{0,1}[0-9]{1,4}[)]{0,1}[-\\s\\./0-9]*$";
    String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";

    String AUTH_API = "api/v1/auth";
    String SIGN_UP_API = "sign-up";
    String BASE_SECURE_PATH = "/api/v1/secure/";
    String BASE_OPEN_APIS = "/api/v1/open/";
    String AUTHORIZATION_HEADER = "Authorization";
    String TOKEN_TYPE = "Bearer ";

}
