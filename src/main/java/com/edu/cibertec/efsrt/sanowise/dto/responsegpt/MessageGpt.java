package com.edu.cibertec.efsrt.sanowise.dto.responsegpt;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageGpt implements Serializable {

    private String role;
    private String content;
}
