package com.isaquebeirith.bry_facial_biometry_api.service;

import com.isaquebeirith.bry_facial_biometry_api.dto.UserBatchResultDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.UserCreateBatchDTO;
import com.isaquebeirith.bry_facial_biometry_api.exception.MissingPictureException;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserBatchService {
    private final UserService userService;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);
    private final Validator validator;

    public List<UserBatchResultDTO> createBatch(List<UserCreateBatchDTO> newUsersBatch, List<byte[]> pictures) {
        return processBatch(newUsersBatch, pictures, this::processUser);
    }

    public List<UserBatchResultDTO> updateBatch(List<UserCreateBatchDTO> updatedUsersBatch, List<byte[]> pictures) {
        return processBatch(updatedUsersBatch, pictures, this::processUpdate);
    }

    private List<UserBatchResultDTO> processBatch(
            List<UserCreateBatchDTO> users,
            List<byte[]> pictures,
            BiFunction<UserCreateBatchDTO, byte[], UserBatchResultDTO> operation
    ) {
        List<Future<UserBatchResultDTO>> futuresResults = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            UserCreateBatchDTO user = users.get(i);
            byte[] picture = pictures.get(i);

            Future<UserBatchResultDTO> future = executor.submit(() -> operation.apply(user, picture));
            futuresResults.add(future);
        }

        List<UserBatchResultDTO> results = new ArrayList<>();
        for (Future<UserBatchResultDTO> future : futuresResults) {
            try {
                // Bloqueia a thread principal, mas as outras seguem rodandop
                results.add(future.get());
            } catch (Exception e) {
                UserBatchResultDTO errorResult = new UserBatchResultDTO();
                errorResult.setSuccess(false);
                errorResult.setError("Erro ao processar usuário: " + e.getMessage());
                results.add(errorResult);
            }
        }

        return results;
    }

    private UserBatchResultDTO processUser(UserCreateBatchDTO user, byte[] picture) {
        UserBatchResultDTO result = new UserBatchResultDTO();

        try {
            Set<ConstraintViolation<UserCreateBatchDTO>> violations = validator.validate(user);

            // ToDo: padronizar erro
            if (!violations.isEmpty()) {
                String errorMessage = violations.stream().map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(", "));
                throw new RuntimeException(errorMessage);
            }

            if (picture == null || picture.length == 0) {
                throw new MissingPictureException("É obrigatório anexar uma foto do usuário.");
            }

            User newUser = new User();
            newUser.setName(user.getName());
            newUser.setCpf(user.getCpf());
            newUser.setPicture(picture);

            User created = userService.create(newUser);

            result.setSuccess(true);
            result.setName(created.getName());
            result.setCpf(created.getCpf());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setName(user.getName());
            result.setCpf(user.getCpf());
            result.setError(e.getMessage());
        }

        return result;
    }

    private UserBatchResultDTO processUpdate(UserCreateBatchDTO userData, byte[] picture) {
        UserBatchResultDTO result = new UserBatchResultDTO();

        try {
            User user = userService.findByCpf(userData.getCpf());

            User updated = userService.update(user.getId(), userData.getName(), picture);

            result.setSuccess(true);
            result.setName(updated.getName());
            result.setCpf(updated.getCpf());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setName(userData.getName());
            result.setCpf(userData.getCpf());
            result.setError(e.getMessage());
        }

        return result;
    }
}