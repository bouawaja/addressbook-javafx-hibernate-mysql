package com.julianjupiter.addressbook.entity;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name ="facture")
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "facture_number", nullable = false, unique = true)
    private String factureNumber;
    @Column(name = "description")
    private String description;
    @Column(name ="total")
    private Double total;

    @Column(name = "is_paid", nullable = false)
    private boolean isPaid;

    @Column(name ="created_at")
    private OffsetDateTime createdAt;
    @Column(name ="update_at")
    private OffsetDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "contact_id", nullable = false)
    private Contact contact;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientAcompte> clientAcomptes;


    public String getFactureNumber() {
        return factureNumber;
    }

    public void setFactureNumber(String factureNumber) {
        this.factureNumber = factureNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Contact getContact() {
        return contact;
    }

    public void setContact(Contact contact) {
        this.contact = contact;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public List<ClientAcompte> getClientAcomptes() {
        return clientAcomptes;
    }

    public void setClientAcomptes(List<ClientAcompte> clientAcomptes) {
        this.clientAcomptes = clientAcomptes;
    }

    public void addAcompte(ClientAcompte acompte) {
        clientAcomptes.add(acompte);
        acompte.setFacture(this);
    }

    public void removeAcompte(ClientAcompte acompte) {
        clientAcomptes.remove(acompte);
        acompte.setFacture(null);
    }
}
