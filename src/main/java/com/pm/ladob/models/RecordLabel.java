package com.pm.ladob.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "record_label")
public class RecordLabel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String name;

    @Column(unique = true)
    private String imageUrl;

    private String description;

    @OneToMany(mappedBy = "recordLabel", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Album> albums;
}
