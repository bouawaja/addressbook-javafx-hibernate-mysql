package test;

import com.julianjupiter.addressbook.dao.ContactDao;
import com.julianjupiter.addressbook.dao.ContactDaoImpl;
import com.julianjupiter.addressbook.dao.FactureDaoImpl;
import com.julianjupiter.addressbook.entity.Contact;
import com.julianjupiter.addressbook.entity.Facture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FactureRepositoryTest {

    private static EntityManagerFactory emf;
    private static EntityManager em;
    private FactureDaoImpl factureDao;

   @BeforeAll
    static void setupEntityManager(){
        emf = Persistence.createEntityManagerFactory("addressbook-persistence-unit");
    }

    @BeforeEach
    void setup(){
       em = emf.createEntityManager();
       factureDao = new FactureDaoImpl();
    }

    @AfterAll
    static void closeEntityManager(){
        if (em == null) {
            return;
        }
        em.close();
    }

    private Contact createTestContact() {
        Contact contact = new Contact();
        contact.setFirstName("Sophie");
        contact.setLastName("BEN-GUERDANE");
        contact.setAddress("Paris 1eme");
        contact.setMobileNumber("123456789");
        contact.setEmailAddress("john.doe@example.com");
        contact.setCreatedAt(OffsetDateTime.now());
        ContactDao contactDao = new ContactDaoImpl();
        contactDao.save(contact);

        return contact;
    }


    @Test
    void testAddFacture(){
       Contact contact = createTestContact();
        Facture facture = new Facture();
        facture.setContact(contact);
        facture.setFactureNumber("F004");
        facture.setDescription("Test unitaire description");
        facture.setTotal(1200.000);
        facture.setPaid(false);
        facture.setCreatedAt(OffsetDateTime.now());
        facture.setUpdatedAt(OffsetDateTime.now());

        factureDao.save(facture);
        Optional<Facture> factureOptional = factureDao.findLastInsertedFacture();
         assertTrue(factureOptional.isPresent());
         assertEquals("F004", factureOptional.get().getFactureNumber());
    }

    @Test
    void tastAddNewFactureToContact(){
        Long contactId = 7L;
        Facture nouvelleFacture = new Facture();
        nouvelleFacture.setFactureNumber("F12346");
        nouvelleFacture.setDescription("Achat d'équipement sanitaire");
        nouvelleFacture.setTotal(3500.0);
        nouvelleFacture.setPaid(true);

        Optional<Facture> factureAjoute = factureDao.addFactureToContact(contactId, nouvelleFacture);
        assertTrue(factureAjoute.isPresent());
        assertEquals("F12346", factureAjoute.get().getFactureNumber());


    }
    @Test
    void testFinAllFacture(){
       List<Facture> factures = factureDao.findAll();
        assertFalse(factures.isEmpty());
        assertEquals("F12346", factures.get(0).getFactureNumber());
    }

    @Test
    void testFindFactureByLastName(){
      List<Facture> factures = factureDao.findFacturesByContactNameOrLastename("Philippe");
        assertFalse(factures.isEmpty());
        assertEquals("F12346", factures.get(1).getFactureNumber());
    }

    @Test
    void testUpdateFacture(){
        // ID de la facture existante
        Long factureId = 1L;

        // Récupérer la facture existante
        Optional<Facture> factureOptional = factureDao.findById(factureId);
        assertTrue(factureOptional.isPresent(), "La facture avec l'ID " + factureId + " est introuvable.");

        Facture facture = factureOptional.get();

        // Modifier un champ de la facture
        facture.setPaid(true);

        // Mettre à jour la facture
        Optional<Facture> updatedFactureOptional = factureDao.updateFactureById(factureId, facture);

        // Vérifier que la mise à jour a réussi
        assertTrue(updatedFactureOptional.isPresent(), "La mise à jour de la facture a échoué.");
        Facture updatedFacture = updatedFactureOptional.get();

        // Valider les modifications
        assertTrue(updatedFacture.isPaid(), "Le statut de paiement de la facture n'a pas été mis à jour.");

    }
    @Test
    void testFindAllFacturesArePaid(){
        // Récupérer toutes les factures payées
        List<Facture> factures = factureDao.findFacturesByIsPaid(true);

        assertFalse(factures.isEmpty(), "Aucune facture payée n'a été trouvée.");

        factures.forEach(facture -> assertTrue(facture.isPaid(),
                "La facture avec le numéro " + facture.getFactureNumber() + " n'est pas marquée comme payée."));

        assertTrue(factures.stream().anyMatch(facture -> "F001".equals(facture.getFactureNumber())),
                "La facture avec le numéro 'F001' n'est pas présente parmi les factures payées.");

    }

    @Test
    void testDeleteFactureById(){

       Long factureId = 3L;
        factureDao.deleteById(factureId);
        Optional<Facture> deletedFacture = factureDao.findById(factureId);
        assertFalse(deletedFacture.isPresent(), "La facture n'a pas été correctement supprimée.");
    }

    @Test
    void testFindFactureByNumeroFacture(){
        Contact contact = createTestContact();
        Facture facture = new Facture();
        facture.setFactureNumber("F12348");
        facture.setDescription("Facture pour test de recherche");
        facture.setTotal(1200.0);
        facture.setPaid(false);
        facture.setContact(contact);


        factureDao.save(facture);

       List<Facture> factures = factureDao.findByFactureNumbre("F12348");
        assertFalse(factures.isEmpty(), "Aucune facture trouvée avec le numéro 'F12346'.");

        // Vérifier que la facture trouvée correspond
        assertEquals(1, factures.size(), "Plusieurs factures trouvées avec le même numéro.");
        assertEquals("F12348", factures.get(0).getFactureNumber());
        assertEquals(contact.getId(), factures.get(0).getContact().getId());
    }

}
