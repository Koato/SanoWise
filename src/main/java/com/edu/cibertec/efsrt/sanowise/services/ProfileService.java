package com.edu.cibertec.efsrt.sanowise.services;

import com.edu.cibertec.efsrt.sanowise.dto.request.RequestDto;
import reactor.core.publisher.Mono;

public interface ProfileService {

    Mono<String> getChatGptResponse(String message);

    Mono<String> pruebaImagen(RequestDto request);
}
