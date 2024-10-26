package com.edu.cibertec.efsrt.sanowise.dto.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RequestDto implements Serializable {

    private List<String> preferencias;
    private String imageBase64;
    private String imageExtension;
}
