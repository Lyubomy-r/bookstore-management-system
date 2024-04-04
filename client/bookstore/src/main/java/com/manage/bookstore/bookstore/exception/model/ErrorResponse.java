package com.manage.bookstore.bookstore.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public  class ErrorResponse {
    private int status;
    private String message;
    private Timestamp timeStamp;
}
