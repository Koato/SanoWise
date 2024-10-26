package com.edu.cibertec.efsrt.sanowise.services.impl;

import com.edu.cibertec.efsrt.sanowise.dto.request.RequestDto;
import com.edu.cibertec.efsrt.sanowise.dto.responsegpt.ResponseGpt;
import com.edu.cibertec.efsrt.sanowise.services.ChatGptReactiveService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class ChatGptReactiveServiceImpl implements ChatGptReactiveService {

    private final WebClient webClient;

    @Value("${openai.api.key}")
    private String apiKey;

    public Mono<String> getChatGptResponse(String message) {
        String requestBody = """
                {
                  "model": "gpt-4o-mini",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(message);
        System.out.println(requestBody);

        return webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<String> pruebaImagen(RequestDto request) {
        StringBuilder texto = new StringBuilder("Respondeme con un 'Producto Saludable' o con un 'Producto NO Saludable'");
        texto.append(", tambien con una 'Calificación' del producto del 1 al 5 ");
        texto.append(" e indicame 2 'Ingredientes' del producto. ");
        texto.append(" Ofreceme 3 productos mejores con su respectiva calificiacion, no es necesario los ingredientes ");
        texto.append(", teniendo en cuenta mis preferencias: ");
        request.getPreferencias().stream()
                .map(String::toString) // Convierte cada preferencia a texto; puedes personalizar el metodo si es necesario
                .forEach(preferenciaTexto -> texto.append(preferenciaTexto).append(", ")); // Añade cada preferencia en una nueva línea

        texto.append(" Despues de la tercera sugerencia ya no agregues más texto ");
        texto.append(" En producto responde ##Producto: y Mejores Alternativas responde ##Mejores Alternativas: ");
        texto.append(" Responde cada punto en 1 linea diferente con la menos cantidad de * posibles ");
        // El texto ahora contiene todas las preferencias en formato de texto
        System.out.println(texto);

        // Crear el cuerpo de la solicitud
        String body = String.format("""
            {
                "model": "gpt-4o-mini",
                "messages": [
                    {
                        "role": "user",
                        "content": [
                            {
                                "type": "text",
                                "text": "%s"
                            },
                            {
                                "type": "image_url",
                                "image_url": {
                                    "url": "data:image/%s;base64,%s"
                                }
                            }
                        ]
                    }
                ]
            }
            """, texto, request.getImageExtension(), request.getImageBase64());

        System.out.println(body);
        // Enviar la solicitud a la API de OpenAI
        var response = webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .header("Accept", "application/json")
                .retrieve()
                .bodyToMono(ResponseGpt.class);

        // Imprimir la respuesta
        response.subscribe(res -> {
            // Acceder a los valores
            System.out.println("ID: " + res.getId());
            System.out.println("Model: " + res.getModel());
            System.out.println("Response Content: " + res.getChoices().get(0).getMessage().getContent());
        });
        return response.map(esponse -> esponse.getChoices().getFirst().getMessage().getContent());
    }
}

