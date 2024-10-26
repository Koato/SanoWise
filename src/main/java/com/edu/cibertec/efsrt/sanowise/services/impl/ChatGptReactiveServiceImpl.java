package com.edu.cibertec.efsrt.sanowise.services.impl;

import com.edu.cibertec.efsrt.sanowise.dto.request.RequestDto;
import com.edu.cibertec.efsrt.sanowise.dto.responsegpt.ResponseGpt;
import com.edu.cibertec.efsrt.sanowise.services.ChatGptReactiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
        String texto = getTexto(request);

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
        return formateResponse(response.map(esponse -> esponse.getChoices().getFirst().getMessage().getContent()).block());
    }

    private Mono<String> formateResponse(String inputText) {
        try {
            // Crear un ObjectMapper
            ObjectMapper mapper = new ObjectMapper();

            // Crear el nodo raíz
            ObjectNode rootNode = mapper.createObjectNode();

            // Extraer el producto, calificación e ingredientes
            String[] lines = inputText.split("\n");
            String veredict = lines[0].split(":")[1].trim();
            int rating = Integer.parseInt(lines[1].split(":")[1].trim());
            String ingredientes = lines[2].split(":")[1].trim();

            rootNode.put("veredict", veredict);
            rootNode.put("rating", rating);
            rootNode.put("ingredientes", ingredientes);

            // Crear el array de sugerencias
            ArrayNode suggestionsArray = mapper.createArrayNode();

            int contador = 1;
            // Extraer sugerencias (a partir de la línea 4 en adelante)
            for (int i = 5; i < lines.length; i += 2) {
                String suggestionName = lines[i].replaceAll("^\\d+\\.\\s*", "").trim();
                int suggestionRating = Integer.parseInt(lines[i + 1].split(":")[1].trim());

                ObjectNode suggestionNode = mapper.createObjectNode();
                suggestionNode.put("id", contador);
                suggestionNode.put("name", suggestionName);
                suggestionNode.put("rating", suggestionRating);
                suggestionsArray.add(suggestionNode);
                contador++;
            }

            // Añadir el array de sugerencias al nodo raíz
            rootNode.set("suggestions", suggestionsArray);

            // Convertir el nodo raíz a JSON en formato String
            var response = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
            return Mono.just(response);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getTexto(RequestDto request) {
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
        return texto.toString();
    }
}

