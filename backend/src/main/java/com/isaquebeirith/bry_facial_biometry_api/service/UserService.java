package com.isaquebeirith.bry_facial_biometry_api.service;

import com.isaquebeirith.bry_facial_biometry_api.exception.DuplicateCpfException;
import com.isaquebeirith.bry_facial_biometry_api.exception.InvalidPictureException;
import com.isaquebeirith.bry_facial_biometry_api.exception.InvalidUpdateException;
import com.isaquebeirith.bry_facial_biometry_api.exception.UserNotFoundException;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FacialTemplateService facialTemplateService;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("Não há nenhum usuário cadastrado com o ID " + id));
    }

    public User findByCpf(String cpf) {
        return userRepository.findByCpf(cpf).orElseThrow(
                () -> new UserNotFoundException("Não há nenhum usuário cadastrado com o CPF " + cpf));
    }

    @Transactional
    public User create(User user)  {
        if (userRepository.findByCpf(user.getCpf()).isPresent()) {
            throw new DuplicateCpfException("Já existe um usuário com esse CPF cadastrado no sistema.");
        }

        if (user.getPicture() == null || user.getPicture().length == 0) {
            throw new InvalidPictureException("É obrigatório anexar uma foto do usuário.");
        }

        User newUser = userRepository.save(user);
        facialTemplateService.createFacialTemplate(newUser);

        return newUser;
    }

    @Transactional
    public User update(Long id, String name, byte[] picture)  {
        User user = findById(id);

        boolean validName = (name != null && !name.isBlank());
        boolean validPicture = (picture != null && picture.length > 0);
        boolean atLeastOneFieldValid = validName || validPicture;

        if (!atLeastOneFieldValid) {
            throw new InvalidUpdateException("Informe pelo menos um campo para atualizar.");
        }

        if (validName) {
            user.setName(name);
        }

        if (validPicture) {
            user.setPicture(picture);
            facialTemplateService.updateFacialTemplate(user);
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        User user = findById(id);

        userRepository.delete(user);
    }
}
