package com.julianjupiter.addressbook.dao;

import com.julianjupiter.addressbook.entity.Contact;
import com.julianjupiter.addressbook.entity.Facture;
import com.julianjupiter.addressbook.util.PersistenceManager;

import javax.persistence.EntityTransaction;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class FactureDaoImpl implements FactureDao {
    Logger logger = Logger.getLogger(FactureDaoImpl.class.getName());
    @Override
    public List<Facture> findAll() {
        var entityManager = PersistenceManager.entityManager();

        try{
            return entityManager.createQuery("select f from Facture f", Facture.class)
                    .getResultList();

        }finally{
            entityManager.close();
        }

    }

    @Override
    public Optional<Facture> findById(Long id) {
        var entityManager = PersistenceManager.entityManager();
        try{
            return Optional.ofNullable(entityManager.find(Facture.class, id));
        }finally {
            entityManager.close();
        }
    }

    @Override
    public void save(Facture facture) {
        var entityManager = PersistenceManager.entityManager();
        EntityTransaction transaction = null;

        try {
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Si l'ID est null, on suppose qu'il s'agit d'une nouvelle facture
            if (facture.getId() == null) {
                facture.setCreatedAt(OffsetDateTime.now());
                facture.setUpdatedAt(OffsetDateTime.now());
                entityManager.persist(facture); // Persistance de la nouvelle facture
            } else {
                facture.setUpdatedAt(OffsetDateTime.now()); // Mise à jour uniquement de updatedAt
                entityManager.merge(facture); // Mise à jour de l'entité existante
            }

            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        } finally {
            entityManager.close();
        }
    }

    @Override
    public void deleteById(Long id) {
        var factureOptional = findById(id);
        factureOptional.ifPresent(facture -> {
            var entityManager = PersistenceManager.entityManager();
            EntityTransaction transaction = null;
            try{
                transaction = entityManager.getTransaction();
                transaction.begin();
                facture = entityManager.contains(facture) ? facture : entityManager.merge(facture);
                entityManager.remove(facture);
                transaction.commit();
            }catch (Exception e) {
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
            }finally {
                entityManager.close();
            }
        });
    }

    @Override
    public List<Facture> findByFactureNumbre(String factureNumber) {
        var entityManager = PersistenceManager.entityManager();
        try{
            return entityManager.createQuery("SELECT f FROM Facture f WHERE UPPER(f.factureNumber) LIKE : factureNumbre", Facture.class)
                    .setParameter("factureNumbre","%"+ factureNumber.toUpperCase() +"%")
                    .getResultList();
        }finally {
            entityManager.close();
        }
    }

    @Override
    public List<Facture> findFacturesByContactNameOrLastename(String name) {
        var entityManager = PersistenceManager.entityManager();
        try{
            return entityManager.createQuery("SELECT f FROM Facture f JOIN f.contact c "+
                    "WHERE UPPER(c.lastName) LIKE : name"+
                                    " OR UPPER(c.firstName) LIKE : name",
                            Facture.class)
                    .setParameter("name","%"+ name.toUpperCase() +"%")
                    .getResultList();
        }finally {
            entityManager.close();
        }
    }

    @Override
    public List<Facture> findFacturesByIsPaid(boolean isPaid) {
        var entityManager = PersistenceManager.entityManager();
        try {
            return entityManager.createQuery(
                            "SELECT f FROM Facture f WHERE f.isPaid = :isPaid", Facture.class)
                    .setParameter("isPaid", isPaid)
                    .getResultList();
        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Facture> addFactureToContact(Long contactId, Facture facture) {
        var entityManager = PersistenceManager.entityManager();
        EntityTransaction transaction = null;

        try {
            // Démarrer la transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Rechercher le contact par son ID
            Contact contact = entityManager.find(Contact.class, contactId);

            if (contact == null) {
                logger.warning("Contact avec l'ID " + contactId + " introuvable.");
                return Optional.empty();
            }

            // Associer la facture au contact
            facture.setContact(contact);
            facture.setCreatedAt(OffsetDateTime.now());
            facture.setUpdatedAt(OffsetDateTime.now());
            // Persister la facture
            entityManager.persist(facture);
            transaction.commit();

            return Optional.of(facture);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Optional.empty();

        } finally {
            entityManager.close();
        }
    }

    @Override
    public Optional<Facture> updateFactureById(Long factureId, Facture updatedFacture) {
        var entityManager = PersistenceManager.entityManager();
        EntityTransaction transaction = null;

        try {
            // Démarrer la transaction
            transaction = entityManager.getTransaction();
            transaction.begin();

            // Rechercher la facture par son ID
            Facture existingFacture = entityManager.find(Facture.class, factureId);

            if (existingFacture == null) {
                logger.warning("Facture " + factureId + " n'existe pas ");
                return Optional.empty();
            }

            // Mettre à jour les champs de la facture
            existingFacture.setFactureNumber(updatedFacture.getFactureNumber());
            existingFacture.setDescription(updatedFacture.getDescription());
            existingFacture.setTotal(updatedFacture.getTotal());
            existingFacture.setPaid(updatedFacture.isPaid());
            existingFacture.setUpdatedAt(OffsetDateTime.now());

            // Si le contact est mis à jour
            if (updatedFacture.getContact() != null) {
                existingFacture.setContact(updatedFacture.getContact());
            }

            // Persister les modifications
            entityManager.merge(existingFacture);
            transaction.commit();

            return Optional.of(existingFacture);

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return Optional.empty();

        } finally {
            entityManager.close();
        }
    }


    public Optional<Facture> findLastInsertedFacture() {
        var entityManager = PersistenceManager.entityManager();
        try {
            return entityManager.createQuery(
                            "SELECT f FROM Facture f ORDER BY f.id DESC", Facture.class)
                    .setMaxResults(1)
                    .getResultList()
                    .stream()
                    .findFirst();
        } finally {
            entityManager.close();
        }
    }

}
