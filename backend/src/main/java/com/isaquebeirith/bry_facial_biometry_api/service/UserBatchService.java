package com.isaquebeirith.bry_facial_biometry_api.service;

import com.isaquebeirith.bry_facial_biometry_api.dto.UserBatchResultDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.UserCreateBatchDTO;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class UserBatchService {
    private final UserService userService;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    public List<UserBatchResultDTO> createBatch(List<UserCreateBatchDTO> users, List<byte[]> pictures) {
        List<Future<UserBatchResultDTO>> futuresResults = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            UserCreateBatchDTO user = users.get(i);
            byte[] picture = pictures.get(i);

            Future<UserBatchResultDTO> future = executor.submit(() -> processUser(user, picture));
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

    // ToDo: Emitir erro em requests sem nome ou foto
    private UserBatchResultDTO processUser(UserCreateBatchDTO user, byte[] picture) {
        UserBatchResultDTO result = new UserBatchResultDTO();

        try {
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
}