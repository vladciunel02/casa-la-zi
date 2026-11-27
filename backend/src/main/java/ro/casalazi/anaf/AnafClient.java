package ro.casalazi.anaf;

import ro.casalazi.errors.anaf.AnafIndisponibilException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import tools.jackson.databind.JsonNode;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AnafClient implements RegistruAnaf {
    private final RestClient client;

    @Autowired
    public AnafClient() {
        this(clientHttp());
    }

    AnafClient(RestClient client) {
        this.client = client;
    }

    private static RestClient clientHttp() {
        var http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
        var factory = new JdkClientHttpRequestFactory(http);
        factory.setReadTimeout(Duration.ofSeconds(10));
        return RestClient.builder().baseUrl("https://webservicesp.anaf.ro")
                .requestFactory(factory).build();
    }

    @Override
    public Optional<AnafFirmaDto> cauta(String cui) {
        // anaf asteapta cui-ul ca numar si data din fusul orar al romaniei.
        long cod = Long.parseLong(cui);
        String data = LocalDate.now(ZoneId.of("Europe/Bucharest")).toString();
        try {
            return client.post().uri("/api/PlatitorTvaRest/v9/tva")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(List.of(Map.of("cui", cod, "data", data)))
                    .exchange((request, response) -> {
                        int status = response.getStatusCode().value();
                        if (status != 200 && status != 404) {
                            throw new AnafIndisponibilException(null);
                        }
                        // anaf poate confirma un cui negasit printr-un raspuns json cu status 404.
                        var rezultat = interpreteaza(response.bodyTo(JsonNode.class), cod);
                        if (status == 404 && rezultat.isPresent()) {
                            throw new AnafIndisponibilException(null);
                        }
                        return rezultat;
                    });
        } catch (RestClientException exception) {
            throw new AnafIndisponibilException(exception);
        }
    }

    private Optional<AnafFirmaDto> interpreteaza(JsonNode raspuns, long cod) {
        if (raspuns == null) {
            throw new AnafIndisponibilException(null);
        }
        JsonNode gasite = raspuns.path("found");
        JsonNode negasite = raspuns.path("notFound");
        if (gasite.isArray() && gasite.size() == 1) {
            JsonNode firma = gasite.path(0);
            JsonNode date = firma.path("date_generale");
            JsonNode tva = firma.path("inregistrare_scop_Tva").path("scpTVA");
            JsonNode inactiv = firma.path("stare_inactiv").path("statusInactivi");
            if (date.path("cui").asLong(-1) == cod && date.path("denumire").isString()
                    && tva.isBoolean() && inactiv.isBoolean()) {
                return Optional.of(new AnafFirmaDto(Long.toString(cod), date.path("denumire").asString(),
                        date.path("nrRegCom").asString(""), date.path("adresa").asString(""),
                        tva.asBoolean(), inactiv.asBoolean()));
            }
        }
        // confirmam absenta firmei doar daca anaf a inclus codul in notfound.
        if (gasite.isArray() && gasite.isEmpty() && negasite.isArray()) {
            for (JsonNode element : negasite) {
                if (element.asLong(-1) == cod) {
                    return Optional.empty();
                }
            }
        }
        throw new AnafIndisponibilException(null);
    }
}
