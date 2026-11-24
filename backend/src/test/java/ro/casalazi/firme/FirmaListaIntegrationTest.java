package ro.casalazi.firme;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import ro.casalazi.utilizatori.UtilizatorCurent;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.flyway.locations=classpath:db/migration",
        "spring.jpa.open-in-view=false",
        "casalazi.dev.utilizator-curent-id=a0000000-0000-0000-0000-000000000001"
})
class FirmaListaIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17");

    private static final UUID CABINET_A = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID CABINET_B = UUID.fromString("22222222-2222-2222-2222-222222222222");

    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Value("${local.server.port}")
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private UtilizatorCurent utilizatorCurent;

    @BeforeEach
    void seedIsolatedDatabase() {
        jdbc.update("DELETE FROM utilizator");
        jdbc.update("DELETE FROM firma");
        jdbc.update("DELETE FROM cabinet");
        jdbc.update("INSERT INTO cabinet (id, denumire) VALUES (?, ?)", CABINET_A, "Cabinet A");
        jdbc.update("INSERT INTO cabinet (id, denumire) VALUES (?, ?)", CABINET_B, "Cabinet B");
        insertFirma("f0000000-0000-0000-0000-000000000001", CABINET_A, "Zeta SRL", "18452300", true);
        insertFirma("f0000000-0000-0000-0000-000000000002", CABINET_A, "Alpha SRL", "30117720", true);
        insertFirma("f0000000-0000-0000-0000-000000000003", CABINET_A, "Arhivata SRL", "27115304", false);
        // Același CUI este permis în alt cabinet.
        insertFirma("f0000000-0000-0000-0000-000000000101", CABINET_B, "Alta firma SRL", "18452300", true);
    }

    private void insertFirma(String id, UUID cabinetId, String denumire, String cui, boolean activ) {
        jdbc.update("""
                INSERT INTO firma (id, cabinet_id, denumire, cui, nr_reg_com, adresa, platitor_tva, activ)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """, UUID.fromString(id), cabinetId, denumire, cui, "J16/1/2020", "Adresă de test", true, activ);
    }

    private JsonNode getLista() throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + port + "/api/firme"))
                .timeout(Duration.ofSeconds(10)).GET().build();
        try (var client = HttpClient.newHttpClient()) {
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertEquals(200, response.statusCode());
            assertTrue(response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
            return mapper.readTree(response.body());
        }
    }

    @Test
    void cabinetAReceivesOnlyItsActiveCompaniesInAlphabeticalOrder() throws Exception {
        when(utilizatorCurent.cabinetId()).thenReturn(CABINET_A);

        JsonNode lista = getLista();

        assertTrue(lista.isArray());
        assertEquals(2, lista.size());
        assertEquals("Alpha SRL", lista.get(0).path("denumire").asText());
        assertEquals("Zeta SRL", lista.get(1).path("denumire").asText());
        assertEquals("f0000000-0000-0000-0000-000000000002", lista.get(0).path("id").asText());
        assertEquals("f0000000-0000-0000-0000-000000000001", lista.get(1).path("id").asText());
    }

    @Test
    void cabinetBDoesNotReceiveCompaniesFromCabinetA() throws Exception {
        when(utilizatorCurent.cabinetId()).thenReturn(CABINET_B);

        JsonNode lista = getLista();

        assertEquals(1, lista.size());
        assertEquals("f0000000-0000-0000-0000-000000000101", lista.get(0).path("id").asText());
    }

    @Test
    void cabinetWithoutCompaniesReceivesEmptyArray() throws Exception {
        UUID cabinetGol = UUID.fromString("33333333-3333-3333-3333-333333333333");
        jdbc.update("INSERT INTO cabinet (id, denumire) VALUES (?, ?)", cabinetGol, "Cabinet gol");
        when(utilizatorCurent.cabinetId()).thenReturn(cabinetGol);

        JsonNode lista = getLista();

        assertTrue(lista.isArray());
        assertTrue(lista.isEmpty());
    }

    @Test
    void responseContainsOnlyTheDocumentedDtoFields() throws Exception {
        when(utilizatorCurent.cabinetId()).thenReturn(CABINET_A);

        JsonNode firma = getLista().get(0);

        Set<String> fields = new HashSet<>();
        firma.properties().forEach(property -> fields.add(property.getKey()));
        assertEquals(Set.of("id", "denumire", "cui", "nrRegCom", "adresa", "platitorTva", "creatLa"), fields);
        assertEquals("30117720", firma.path("cui").asText());
        assertEquals("J16/1/2020", firma.path("nrRegCom").asText());
        assertEquals("Adresă de test", firma.path("adresa").asText());
        assertTrue(firma.path("platitorTva").isBoolean());
        assertTrue(firma.path("platitorTva").asBoolean());
        assertDoesNotThrow(() -> Instant.parse(firma.path("creatLa").asText()));
    }
}
