package com.edu.cibertec.efsrt.sanowise.dto.responsegpt;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResponseGpt implements Serializable {

    private String id;
    private Long created;
    private String model;
    private List<ChoiceGpt> choices;
    private String system_fingerprint;
}
