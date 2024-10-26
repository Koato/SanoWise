package com.edu.cibertec.efsrt.sanowise.controller;

import com.edu.cibertec.efsrt.sanowise.dto.request.RequestDto;
import com.edu.cibertec.efsrt.sanowise.services.ChatGptReactiveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
        return chatGptService.pruebaImagen(request);
    }

    @GetMapping("/prueba")
    public Mono<String> prueba(@RequestBody @Valid String message) {
        return Mono.just(message);
    }

}
