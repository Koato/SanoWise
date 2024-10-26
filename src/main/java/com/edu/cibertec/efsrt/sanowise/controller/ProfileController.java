package com.edu.cibertec.efsrt.sanowise.controller;

import com.edu.cibertec.efsrt.sanowise.dto.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sano")
@CrossOrigin(origins = "http://localhost:3000")
public class ProfileController {

    @GetMapping("/profile")
    public Mono<Profile> queryChatGpt() {
        var profile = new Profile();
        profile.setName("Andy Gomez");
        profile.setEmail("speed21@outlook.com");
        profile.setPreferences(List.of("Evitar productos con gluten", "Preferir productos orgánicos", "Sin azúcar añadido"));
        return Mono.just(profile);
    }
}
