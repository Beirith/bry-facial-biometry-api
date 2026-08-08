package com.isaquebeirith.bry_facial_biometry_api.biometry.extraction;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.translator.ImageFeatureExtractorFactory;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class FacialFeatureExtractor {
    private final ZooModel<Image, float[]> model;

    public FacialFeatureExtractor() throws Exception {
        List<Float> mean =
                Arrays.asList(
                        127.5f / 255.0f, 127.5f / 255.0f, 127.5f / 255.0f,
                        128.0f / 255.0f, 128.0f / 255.0f, 128.0f / 255.0f);
        String normalize = mean.stream().map(Object::toString).collect(Collectors.joining(","));

        Criteria<Image, float[]> criteria = Criteria.builder()
                .setTypes(Image.class, float[].class)
                .optModelUrls("https://resources.djl.ai/test-models/pytorch/face_feature.zip")
                .optModelName("face_feature")
                .optArgument("normalize", normalize)
                .optTranslatorFactory(new ImageFeatureExtractorFactory())
                .optEngine("PyTorch")
                .build();

        this.model = criteria.loadModel();
    }

    public float[] extract(Image faceImage) throws Exception {
        try (Predictor<Image, float[]> predictor = model.newPredictor()) {
            return predictor.predict(faceImage);
        }
    }
}