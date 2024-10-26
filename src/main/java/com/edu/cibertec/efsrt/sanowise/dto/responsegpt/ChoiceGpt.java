package com.edu.cibertec.efsrt.sanowise.dto.responsegpt;

import lombok.Data;

import java.io.Serializable;

@Data
public class ChoiceGpt implements Serializable {

    private String index;
    private MessageGpt message;
    private String finish_reason;
}
