package com.isaquebeirith.bry_facial_biometry_api.service;

import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.repository.FacialTemplateRepository;
import com.isaquebeirith.bry_facial_biometry_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FacialTemplateService facialTemplateService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findByUserId(Long userId) {
        return userRepository.findById(userId);
    }

    public Optional<User> findByCpf(String cpf) {
        return userRepository.findByCpf(cpf);
    }

    @Transactional
    public User create(User user) {
        if (userRepository.findByCpf(user.getCpf()).isPresent()) {
            throw new IllegalArgumentException("Já existe um usuário com esse CPF cadastrado no sistema!");
        }

        User newUser = userRepository.save(user);
        facialTemplateService.createFacialTemplate(newUser);

        return newUser;
    }
}
