package com.julianjupiter.addressbook.dao;

import com.julianjupiter.addressbook.entity.Facture;

import java.util.List;
import java.util.Optional;

public interface FactureDao extends Dao<Facture, Long> {

    static FactureDao create() { return new FactureDaoImpl();}
    Optional<Facture> findLastInsertedFacture();

    List<Facture> findByFactureNumbre(String factureNumbre);

    List<Facture> findFacturesByContactNameOrLastename(String name);

    List<Facture> findFacturesByIsPaid(boolean isPaid);

    Optional<Facture> addFactureToContact(Long contactId, Facture facture);

    Optional<Facture> updateFactureById(Long factureId, Facture updatedFacture);

}
