package com.isaquebeirith.bry_facial_biometry_api.configuration;

import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.repository.UserRepository;
import com.isaquebeirith.bry_facial_biometry_api.service.FacialTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Component
@RequiredArgsConstructor
public class SeedRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final FacialTemplateService facialTemplateService;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        String[][] seedUsers = {
                {"John Lennon", "11111111111", "seed-beatles/john.jpg"},
                {"Paul McCartney", "22222222222", "seed-beatles/paul.jpg"},
                {"George Harrison", "33333333333", "seed-beatles/george.jpg"},
                {"Ringo Starr", "44444444444", "seed-beatles/ringo.jpg"},
                {"Isaque Beirith", "55555555555", "seed-beatles/isaque.jpg"}
        };

        for (String[] seedUser : seedUsers) {
            try {
                byte[] pictureBytes = readResourceFile(seedUser[2]);

                User user = new User();
                user.setName(seedUser[0]);
                user.setCpf(seedUser[1]);
                user.setPicture(pictureBytes);

                userRepository.save(user);
                facialTemplateService.createFacialTemplate(user);
            } catch (Exception e) {
                System.out.println("Erro ao cadastrar usuário de exemplo " + seedUser[0] + ": " + e.getMessage());
            }
        }
    }

    private byte[] readResourceFile(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        try (InputStream inputStream = resource.getInputStream()) {
            return inputStream.readAllBytes();
        }
    }
}