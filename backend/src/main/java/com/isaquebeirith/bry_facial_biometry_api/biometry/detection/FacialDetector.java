package com.isaquebeirith.bry_facial_biometry_api.biometry.detection;

import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ZooModel;
import org.springframework.stereotype.Component;

@Component
public class FacialDetector {
    private final ZooModel<Image, DetectedObjects> model;

    public FacialDetector() throws Exception {
        double confThresh = 0.85;
        double nmsThresh = 0.45;
        double[] variance = {0.1, 0.2};
        int topK = 5000;
        int[][] scales = {{16, 32}, {64, 128}, {256, 512}};
        int[] steps = {8, 16, 32};

        FacialDetectionTranslator translator =
                new FacialDetectionTranslator(confThresh, nmsThresh, variance, topK, scales, steps);

        Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                .setTypes(Image.class, DetectedObjects.class)
                .optModelUrls("https://resources.djl.ai/test-models/pytorch/retinaface.zip")
                .optModelName("retinaface")
                .optTranslator(translator)
                .optEngine("PyTorch")
                .build();

        this.model = criteria.loadModel();
    }

    public DetectedObjects detect(Image image) throws Exception {
        try (Predictor<Image, DetectedObjects> predictor = model.newPredictor()) {
            return predictor.predict(image);
        }
    }
}