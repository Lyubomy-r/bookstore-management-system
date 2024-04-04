package com.manage.bookstore.bookstore.exception.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidResponse {
    private int status;
    List<String> errorsList;
    private Timestamp timeStamp;
}
