package com.edu.cibertec.efsrt.sanowise.controller;

import com.edu.cibertec.efsrt.sanowise.dto.request.RequestDto;
import com.edu.cibertec.efsrt.sanowise.services.ChatGptReactiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/analyze")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatGptController {

    private final ChatGptReactiveService chatGptService;

    @PostMapping("/query")
    public Mono<String> queryChatGpt(@RequestBody @Valid String message) {
        return chatGptService.getChatGptResponse(message);
    }

    @PostMapping("/image-text")
    public Mono<String> analyzeImage(@RequestBody @Valid RequestDto request) {
        request.setPreferencias(List.of("Evitar productos con gluten", "Preferir productos orgánicos", "Sin azúcar añadido"));
        var respuesta = chatGptService.pruebaImagen(request);
        System.out.println(respuesta.block());
        return respuesta;
    }

    @GetMapping("/prueba")
    public Mono<String> prueba(@RequestBody @Valid String message) {
        return Mono.just(message);
    }

}
