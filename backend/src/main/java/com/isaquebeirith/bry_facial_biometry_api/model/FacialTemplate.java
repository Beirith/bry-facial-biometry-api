package com.isaquebeirith.bry_facial_biometry_api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "facial_templates")
public class FacialTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id",
            nullable = false, unique = true, foreignKey = @ForeignKey(
            name = "fk_facial_template_user",
            foreignKeyDefinition = "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"))
    private User user;

    @Column(name = "feature_vector", nullable = false)
    private byte[] featureVector;
}
