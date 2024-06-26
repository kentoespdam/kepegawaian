package id.perumdamts.kepegawaian.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/test")
public class TestController {
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<?> index(Principal principal){
        return ResponseEntity.ok(principal.getName());
    }
}
