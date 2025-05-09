package com.mein.projekt.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "artikel")
@NamedQueries({
    @NamedQuery(
        name = "Artikel.findLatestByLand",
        query = "SELECT a FROM Artikel a WHERE a.land = :land AND a.status = 'approved' ORDER BY a.jahr DESC"
    )
})
public class Artikel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "nr")
    private Long id;

    @Column(name = "beschreibung")
    private String beschreibung;

    @Column(name = "land", nullable = false)
    private String land;

    @Column(name = "status", nullable = false)
    private String status = "pending";

    @Column(name = "jahr", nullable = false)
    private Integer jahr;

    @Column(name = "co2ausstoss")
    private Double co2Ausstoss;
    
    @Column(name = "einheit")
    private String einheit = "Tonnen";
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "erstellt_am")
    @Temporal(TemporalType.DATE)
    private Date erstelltAm;

    @OneToMany(mappedBy = "artikel", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Bewertung> bewertungen = new ArrayList<>();

    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        createdAt = now;
        updatedAt = now;
        erstelltAm = now;
        status = "pending";
    }

    public Artikel() {
        this.status = "pending";
        this.erstelltAm = new Date();
        this.co2Ausstoss = 0.0;
    }
    
    public Artikel(String land, Double co2Ausstoss, String beschreibung, Integer jahr) {
        this.land = land;
        this.co2Ausstoss = co2Ausstoss;
        this.beschreibung = beschreibung;
        this.jahr = jahr;
        this.status = "pending";
        this.erstelltAm = new Date();
    }

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBeschreibung() { return beschreibung; }
    public void setBeschreibung(String beschreibung) { this.beschreibung = beschreibung; }
    
    public String getLand() { return land; }
    public void setLand(String land) { this.land = land; }
    
    public Integer getJahr() { return jahr; }
    public void setJahr(Integer jahr) { this.jahr = jahr; }
    
    public Double getCo2Ausstoss() { return co2Ausstoss; }
    public void setCo2Ausstoss(Double co2Ausstoss) { this.co2Ausstoss = co2Ausstoss; }
    
    public String getEinheit() { return einheit; }
    public void setEinheit(String einheit) { this.einheit = einheit; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Date getErstelltAm() { return erstelltAm; }
    public void setErstelltAm(Date erstelltAm) { this.erstelltAm = erstelltAm; }
    
    public List<Bewertung> getBewertungen() { return bewertungen; }
    public void addBewertung(Bewertung bewertung) { bewertungen.add(bewertung); }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    
    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
    
    public User getCreatedBy() { return createdBy; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    
    public User getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(User updatedBy) { this.updatedBy = updatedBy; }
}
