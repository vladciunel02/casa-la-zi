package ro.casalazi.anaf;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import ro.casalazi.errors.anaf.AnafIndisponibilException;

import java.net.SocketTimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class AnafClientTest {

    private static final String URL = "https://anaf.example/api/PlatitorTvaRest/v9/tva";

    private MockRestServiceServer server;
    private AnafClient client;

    @BeforeEach
    void configureazaClientul() {
        var builder = RestClient.builder().baseUrl("https://anaf.example");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new AnafClient(builder.build());
    }

    @Test
    void un404CuCuiulConfirmatPoateFiPastratCaRezultatGol() {
        server.expect(requestTo(URL)).andRespond(withStatus(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"found":[],"notFound":[999999991]}
                        """));

        assertTrue(client.cauta("999999991").isEmpty());
        server.verify();
    }

    @Test
    void raspunsulHtmlNuDevineRezultatGol() {
        server.expect(requestTo(URL)).andRespond(withSuccess(
                "<html><title>Request Rejected</title></html>", MediaType.TEXT_HTML));

        assertThrows(AnafIndisponibilException.class, () -> client.cauta("9999999994"));
        server.verify();
    }

    @Test
    void timeoutulNuDevineRezultatGol() {
        server.expect(requestTo(URL)).andRespond(withException(new SocketTimeoutException()));

        assertThrows(AnafIndisponibilException.class, () -> client.cauta("1590082"));
        server.verify();
    }
}
