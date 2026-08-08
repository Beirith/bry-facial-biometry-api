package com.isaquebeirith.bry_facial_biometry_api.service;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import com.isaquebeirith.bry_facial_biometry_api.biometry.comparation.FacialTemplateComparator;
import com.isaquebeirith.bry_facial_biometry_api.biometry.generation.FacialTemplateGenerator;
import com.isaquebeirith.bry_facial_biometry_api.dto.VerificationResponseDTO;
import com.isaquebeirith.bry_facial_biometry_api.exception.FacialTemplateNotFoundException;
import com.isaquebeirith.bry_facial_biometry_api.exception.FeatureVectorGenerationException;
import com.isaquebeirith.bry_facial_biometry_api.exception.UserNotFoundException;
import com.isaquebeirith.bry_facial_biometry_api.model.FacialTemplate;
import com.isaquebeirith.bry_facial_biometry_api.model.User;
import com.isaquebeirith.bry_facial_biometry_api.repository.FacialTemplateRepository;
import com.isaquebeirith.bry_facial_biometry_api.repository.UserRepository;
import com.isaquebeirith.bry_facial_biometry_api.util.FeatureVectorConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacialTemplateService {
    private final FacialTemplateRepository facialTemplateRepository;
    private final UserRepository  userRepository;

    private final FacialTemplateGenerator facialTemplateGenerator;
    private final FacialTemplateComparator facialTemplateComparator;

    public void createFacialTemplate(User user)  {
        FacialTemplate newFacialTemplate = new FacialTemplate();
        newFacialTemplate.setUser(user);

        byte[] featureVector = generateFeatureVector(user.getPicture());

        newFacialTemplate.setFeatureVector(featureVector);

        facialTemplateRepository.save(newFacialTemplate);
    }

    public void updateFacialTemplate(User user)  {
        Optional<FacialTemplate> oldFacialTemplate = facialTemplateRepository.findByUserId(user.getId());

        if (oldFacialTemplate.isPresent()) {
            FacialTemplate template = oldFacialTemplate.get();
            byte[] featureVector = generateFeatureVector(user.getPicture());
            template.setFeatureVector(featureVector);
            facialTemplateRepository.save(template);
        } else  {
            createFacialTemplate(user);
        }
    }

    private byte[] generateFeatureVector(byte[] pictureBytes)  {
        Image image;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureBytes);
            image = ImageFactory.getInstance().fromInputStream(inputStream);
        } catch (Exception e) {
            throw new FeatureVectorGenerationException("Erro ao gerar vetor de características a partir da foto.");
        }

        float[] floatVector = facialTemplateGenerator.generate(image);

        return FeatureVectorConverter.toByteArray(floatVector);
    }

    public VerificationResponseDTO verifyFacialTemplate(String cpf, byte[] pictureBytes) {
        User user = userRepository.findByCpf(cpf).orElseThrow(
                () -> new UserNotFoundException("Não há nenhum usuário cadastrado com o CPF " + cpf));

        FacialTemplate storedFacialTemplate = facialTemplateRepository.findByUserId(user.getId()).orElseThrow(
                () -> new FacialTemplateNotFoundException("O usuário fornecido não possui foto cadastrada.")
        );

        Image image;
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureBytes);
            image = ImageFactory.getInstance().fromInputStream(inputStream);
        } catch (Exception e) {
            throw new FeatureVectorGenerationException("Erro ao gerar vetor de características a partir da foto.");
        }

        float[] storedVector = FeatureVectorConverter.toFloatArray(storedFacialTemplate.getFeatureVector());
        float[] newVector = facialTemplateGenerator.generate(image);

        float similarity = facialTemplateComparator.calculateSimilarity(storedVector, newVector);
        boolean matches = facialTemplateComparator.matches(similarity);

        VerificationResponseDTO response = new VerificationResponseDTO();
        response.setMatches(matches);
        response.setSimilarityScore(similarity);

        return response;
    }
}
