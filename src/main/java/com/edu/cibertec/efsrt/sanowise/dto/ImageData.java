package com.edu.cibertec.efsrt.sanowise.dto;

import org.springframework.core.io.ByteArrayResource;
import java.io.Serial;
import java.io.Serializable;

public record ImageData(

        ByteArrayResource imageResource,
        String text

) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}