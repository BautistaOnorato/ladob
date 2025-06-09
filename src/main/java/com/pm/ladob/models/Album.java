package com.pm.ladob.models;

import com.pm.ladob.enums.ProductFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String title;
    private String description;
    private BigDecimal price;
    private LocalDate releaseDate;
    private Integer stock;

    @Enumerated(EnumType.STRING)
    private ProductFormat format;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @Column(unique = true)
    private String frontcoverUrl;

    @Column(unique = true)
    private String backCoverUrl;

    @OneToMany(mappedBy = "album", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Song> songs;

    private Integer discount;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "record_label_id")
    private RecordLabel recordLabel;
}
