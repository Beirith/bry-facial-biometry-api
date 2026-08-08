package com.isaquebeirith.bry_facial_biometry_api.biometry.generation;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import com.isaquebeirith.bry_facial_biometry_api.biometry.detection.FacialDetector;
import com.isaquebeirith.bry_facial_biometry_api.biometry.recognition.FacialFeatureExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacialTemplateGenerator {
    private final FacialDetector facialDetector;
    private final FacialFeatureExtractor facialFeatureExtractor;

    public float[] generate(Image image) throws Exception {
        DetectedObjects detections = facialDetector.detect(image);
        List<DetectedObjects.DetectedObject> faces = detections.items();

        //ToDO: padronizar exceções
        if (faces.isEmpty()) {
            throw new RuntimeException("Nenhum rosto detectado na imagem.");
        }

        //ToDO: padronizar exceções
        if (faces.size() > 1) {
            throw new RuntimeException("Mais de um rosto detectado na imagem.");
        }

        DetectedObjects.DetectedObject face = faces.getFirst();
        Image croppedFace = cropFace(image, face);

        return facialFeatureExtractor.extract(croppedFace);
    }

    private Image cropFace(Image image, DetectedObjects.DetectedObject face) {
        Rectangle rect = face.getBoundingBox().getBounds();

        int x = (int) (rect.getX() * image.getWidth());
        int y = (int) (rect.getY() * image.getHeight());
        int width = (int) (rect.getWidth() * image.getWidth());
        int height = (int) (rect.getHeight() * image.getHeight());

        return image.getSubImage(x, y, width, height);
    }

}
