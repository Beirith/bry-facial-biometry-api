package com.isaquebeirith.bry_facial_biometry_api.biometry.comparison;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FacialTemplateComparator {
    @Value("${biometry.similarity-threshold}")
    private float threshold;

    public float calculateSimilarity(float[] template1, float[] template2) {
        float ret = 0.0f;
        float mod1 = 0.0f;
        float mod2 = 0.0f;

        for (int i = 0; i < template1.length; ++i) {
            ret += template1[i] * template2[i];
            mod1 += template1[i] * template1[i];
            mod2 += template2[i] * template2[i];
        }

        return (float) ((ret / Math.sqrt(mod1) / Math.sqrt(mod2) + 1) / 2.0f);
    }

    public boolean matches(float similarity) {
        return similarity >= threshold;
    }
}
