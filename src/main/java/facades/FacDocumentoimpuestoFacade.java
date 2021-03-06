/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.FacDocumentoimpuesto;
import entities.FacDocumentosmaster;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author mario
 */
@Stateless
public class FacDocumentoimpuestoFacade extends AbstractFacade<FacDocumentoimpuesto> {

    @PersistenceContext(unitName = "com.mycompany_kalamary_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacDocumentoimpuestoFacade() {
        super(FacDocumentoimpuesto.class);
    }

    public List<FacDocumentoimpuesto> buscarByDocumentoMaster(FacDocumentosmaster documentosmaster) {
        try {
            Query query = em.createQuery("SELECT i FROM FacDocumentoimpuesto i WHERE i.facDocumentosmaster = ?1");
            query.setParameter(1, documentosmaster);
            return query.getResultList();
        } catch (Exception e) {
            return new ArrayList();
        }
    }

}
