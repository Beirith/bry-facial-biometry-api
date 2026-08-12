package com.isaquebeirith.bry_facial_biometry_api.service;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import com.isaquebeirith.bry_facial_biometry_api.biometry.comparison.FacialTemplateComparator;
import com.isaquebeirith.bry_facial_biometry_api.biometry.generation.FacialTemplateGenerator;
import com.isaquebeirith.bry_facial_biometry_api.dto.IdentificationResponseDTO;
import com.isaquebeirith.bry_facial_biometry_api.dto.UserResponseDTO;
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

import java.io.ByteArrayInputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FacialTemplateService {
    private final FacialTemplateRepository facialTemplateRepository;
    private final UserRepository userRepository;

    private final FacialTemplateGenerator facialTemplateGenerator;
    private final FacialTemplateComparator facialTemplateComparator;

    // Necessário para parallelStream()
    private record BestMatch(User user, float score) {
    }

    public void createFacialTemplate(User user) {
        FacialTemplate newFacialTemplate = new FacialTemplate();
        newFacialTemplate.setUser(user);

        byte[] featureVector = generateFeatureVector(user.getPicture());

        newFacialTemplate.setFeatureVector(featureVector);

        facialTemplateRepository.save(newFacialTemplate);
    }

    public void updateFacialTemplate(User user) {
        Optional<FacialTemplate> oldFacialTemplate = facialTemplateRepository.findByUserId(user.getId());

        if (oldFacialTemplate.isPresent()) {
            FacialTemplate template = oldFacialTemplate.get();
            byte[] featureVector = generateFeatureVector(user.getPicture());
            template.setFeatureVector(featureVector);
            facialTemplateRepository.save(template);
        } else {
            createFacialTemplate(user);
        }
    }

    private byte[] generateFeatureVector(byte[] pictureBytes) {
        Image image = generateImage(pictureBytes);

        float[] floatVector = facialTemplateGenerator.generate(image);

        return FeatureVectorConverter.toByteArray(floatVector);
    }

    public VerificationResponseDTO verifyFacialTemplate(String cpf, byte[] pictureBytes) {
        User user = userRepository.findByCpf(cpf).orElseThrow(
                () -> new UserNotFoundException("Não há nenhum usuário cadastrado com o CPF " + cpf));

        FacialTemplate storedFacialTemplate = facialTemplateRepository.findByUserId(user.getId()).orElseThrow(
                () -> new FacialTemplateNotFoundException("O usuário fornecido não possui foto cadastrada.")
        );

        Image image = generateImage(pictureBytes);

        float[] storedVector = FeatureVectorConverter.toFloatArray(storedFacialTemplate.getFeatureVector());
        float[] newVector = facialTemplateGenerator.generate(image);

        float similarity = facialTemplateComparator.calculateSimilarity(storedVector, newVector);
        boolean matches = facialTemplateComparator.matches(similarity);

        VerificationResponseDTO response = new VerificationResponseDTO();
        response.setMatches(matches);
        response.setSimilarityScore(similarity);

        return response;
    }

    public IdentificationResponseDTO identifyFacialTemplate(byte[] pictureBytes) {
        Image image = generateImage(pictureBytes);

        float[] newVector = facialTemplateGenerator.generate(image);

        List<FacialTemplate> allTemplates = facialTemplateRepository.findAll();

        Optional<BestMatch> bestMatch = allTemplates.parallelStream()
                .map(template -> {
                    float[] storedVector = FeatureVectorConverter.toFloatArray(template.getFeatureVector());
                    float score = facialTemplateComparator.calculateSimilarity(storedVector, newVector);
                    return new BestMatch(template.getUser(), score);
                })
                .max(Comparator.comparingDouble(BestMatch::score));

        float bestScore = bestMatch.map(BestMatch::score).orElse(0.0f);
        User bestUser = bestMatch.map(BestMatch::user).orElse(null);

        IdentificationResponseDTO response = new IdentificationResponseDTO();

        if (bestUser != null && facialTemplateComparator.matches(bestScore)) {
            response.setIdentified(true);
            response.setUser(UserResponseDTO.fromEntity(bestUser));
        } else {
            response.setIdentified(false);
        }
        response.setScore(bestScore);

        return response;
    }

    private Image generateImage(byte[] pictureBytes) {
        Image image;

        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(pictureBytes);
            image = ImageFactory.getInstance().fromInputStream(inputStream);
            image = resizeIfNeeded(image);
        } catch (Exception e) {
            throw new FeatureVectorGenerationException("Erro ao gerar vetor de características a partir da foto.");
        }

        return image;
    }

    private Image resizeIfNeeded(Image image) {
        int maxDimension = 800;

        if (image.getWidth() <= maxDimension && image.getHeight() <= maxDimension) {
            return image;
        }

        double scale = (double) maxDimension / Math.max(image.getWidth(), image.getHeight());
        int newWidth = (int) (image.getWidth() * scale);
        int newHeight = (int) (image.getHeight() * scale);

        return image.resize(newWidth, newHeight, true);
    }
}
