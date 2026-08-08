package com.isaquebeirith.bry_facial_biometry_api.biometry.generation;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.output.Rectangle;
import com.isaquebeirith.bry_facial_biometry_api.biometry.detection.FacialDetector;
import com.isaquebeirith.bry_facial_biometry_api.biometry.recognition.FacialFeatureExtractor;
import com.isaquebeirith.bry_facial_biometry_api.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FacialTemplateGenerator {
    private final FacialDetector facialDetector;
    private final FacialFeatureExtractor facialFeatureExtractor;

    public float[] generate(Image image)  {
        DetectedObjects detections;

        try {
            detections = facialDetector.detect(image);
        } catch (Exception e) {
            throw new FacialDetectionException("Erro ao realizar detecção de rostos na imagem.");
        }

        List<DetectedObjects.DetectedObject> faces = detections.items();

        if (faces.isEmpty()) {
            throw new NoFaceDetectedException("Nenhum rosto detectado na imagem.");
        }

        if (faces.size() > 1) {
            throw new MoreThanOneFaceDetectedException("Mais de um rosto detectado na imagem.");
        }

        DetectedObjects.DetectedObject face = faces.getFirst();
        Image croppedFace = cropFace(image, face);

        try {
            return facialFeatureExtractor.extract(croppedFace);
        } catch (Exception e) {
            throw new FacialExtractionException("Erro ao realizar extração de características na imagem.");
        }
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
