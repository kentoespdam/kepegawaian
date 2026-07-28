package id.perumdamts.kepegawaian.config.appwrite;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.perumdamts.kepegawaian.config.AppwriteProperties;
import id.perumdamts.kepegawaian.dto.appwrite.AppwriteUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Unit test for {@link AppwriteClient#validateToken(String)}.
 * Uses {@link MockRestServiceServer} to mock HTTP calls — no network required.
 */
class AppwriteClientTest {

    private static final String ENDPOINT = "http://test-appwrite.local/v1";
    private static final String PROJECT_ID = "test-project-123";
    private static final String API_KEY = "test-api-key-456";
    private static final String TOKEN = "test.jwt.token";

    private AppwriteClient client;
    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        AppwriteProperties properties = new AppwriteProperties();
        properties.setEndpoint(ENDPOINT);
        properties.setProjectId(PROJECT_ID);
        properties.setApiKey(API_KEY);

        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        client = new AppwriteClient(restClient, properties);
    }

    @Test
    void validateToken_shouldCallAccountEndpoint() {
        server.expect(requestTo(ENDPOINT + "/account"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Appwrite-JWT", TOKEN))
                .andRespond(withSuccess("""
                        {"name":"Test User","email":"test@perumdamts.com","status":true}
                        """, MediaType.APPLICATION_JSON));

        AppwriteUser result = client.validateToken(TOKEN);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@perumdamts.com", result.getEmail());
        assertTrue(result.getStatus());
        server.verify();
    }

    @Test
    void validateToken_shouldSetJwtHeader() {
        server.expect(requestTo(ENDPOINT + "/account"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Appwrite-JWT", TOKEN))
                .andRespond(withSuccess("{\"name\":\"test\"}", MediaType.APPLICATION_JSON));

        assertNotNull(client.validateToken(TOKEN));
        server.verify();
    }

    @Test
    void validateToken_shouldSetDefaultHeaders() {
        server.expect(requestTo(ENDPOINT + "/account"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("X-Appwrite-Response-Format", "1.0.0"))
                .andExpect(header("X-Appwrite-Project", PROJECT_ID))
                .andExpect(header("X-Appwrite-Key", API_KEY))
                .andRespond(withSuccess("{\"name\":\"test\"}", MediaType.APPLICATION_JSON));

        assertNotNull(client.validateToken(TOKEN));
        server.verify();
    }

    @Test
    void validateToken_shouldReturnUserWithFullResponse() {
        String json = """
                {
                  "$id": "usr_789",
                  "name": "Budi Santoso",
                  "registration": "2026-01-15T08:30:00.000Z",
                  "status": true,
                  "email": "budi@perumdamts.com",
                  "phone": "+62123456789",
                  "emailVerification": true,
                  "phoneVerification": false,
                  "prefs": {"roles": ["ADMIN", "USER"]}
                }
                """;

        server.expect(requestTo(ENDPOINT + "/account"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Appwrite-JWT", TOKEN))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        AppwriteUser result = client.validateToken(TOKEN);

        assertNotNull(result);
        assertEquals("Budi Santoso", result.getName());
        assertEquals("budi@perumdamts.com", result.getEmail());
        assertEquals("+62123456789", result.getPhone());
        assertTrue(result.getStatus());
        assertTrue(result.getEmailVerification());
        assertFalse(result.getPhoneVerification());
        assertNotNull(result.getPrefs());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority())));
        server.verify();
    }

    @Test
    void validateToken_shouldReturnNullOnServerError() {
        server.expect(requestTo(ENDPOINT + "/account"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        AppwriteUser result = client.validateToken(TOKEN);
        assertNull(result);
        server.verify();
    }

    @Test
    void validateToken_shouldReturnNullOnBadRequest() {
        server.expect(requestTo(ENDPOINT + "/account"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withBadRequest());

        AppwriteUser result = client.validateToken(TOKEN);
        assertNull(result);
        server.verify();
    }

    @Test
    void validateToken_shouldReturnNullOnUnauthorized() {
        server.expect(requestTo(ENDPOINT + "/account"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withUnauthorizedRequest());

        AppwriteUser result = client.validateToken(TOKEN);
        assertNull(result);
        server.verify();
    }

    @Test
    void jacksonCanDeserializeDollarPrefixedId() throws Exception {
        // The $id field uses a $ prefix that can trip up Jackson's introspection.
        // This test verifies Jackson's ObjectMapper can deserialize it,
        // which is the same engine used by RestClient's HttpMessageConverter chain.
        ObjectMapper mapper = new ObjectMapper();
        String json = """
                {"$id": "usr_789"}
                """;

        AppwriteUser result = mapper.readValue(json, AppwriteUser.class);
        assertEquals("usr_789", result.get$id());
    }
}
