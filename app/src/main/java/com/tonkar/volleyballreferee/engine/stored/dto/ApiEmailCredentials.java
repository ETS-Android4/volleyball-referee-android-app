package com.tonkar.volleyballreferee.engine.stored.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor @AllArgsConstructor @Getter @Setter
public class ApiEmailCredentials {

    private String userEmail;
    private String userPassword;

}