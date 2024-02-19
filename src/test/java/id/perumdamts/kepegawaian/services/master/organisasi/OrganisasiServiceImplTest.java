package id.perumdamts.kepegawaian.services.master.organisasi;

import id.perumdamts.kepegawaian.dto.master.organisasi.OrganisasiPostRequest;
import id.perumdamts.kepegawaian.repositories.master.OrganisasiRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class OrganisasiServiceImplTest {
    @Autowired
    private OrganisasiRepository repository;
    @Autowired
    private ApplicationContext context;
    private WebTestClient rest;
    private final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiI5MDA4MDA0NTYiLCJzZXNzaW9uSWQiOiI2NWNkNmY2YThjNzZmOWU5ZTJjNyIsImV4cCI6MTcwODMzMzE1Nn0.YxZTWaTiWxwcaxLTEjMbeDXdnXEhxUHDRUGYEbstcFA";

    @BeforeEach
    void setUp() {
        rest = WebTestClient.bindToApplicationContext(context)
                .configureClient()
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("Authorization", "Bearer " + TOKEN)
                .build();
    }

    @Test
    public void jajal(){
        OrganisasiPostRequest organisasi = new OrganisasiPostRequest();
        organisasi.setNama("PT. Perumdam");
        this.rest
                .mutate()
                .build()
                .post()
                .uri("/master/organisasi")
                .bodyValue(organisasi)
                .exchange()
                .expectStatus().isOk();
    }
}