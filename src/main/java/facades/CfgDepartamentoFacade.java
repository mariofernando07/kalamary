/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import entities.CfgDepartamento;
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
public class CfgDepartamentoFacade extends AbstractFacade<CfgDepartamento> {

    @PersistenceContext(unitName = "com.mycompany_kalamary_war_1.0PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CfgDepartamentoFacade() {
        super(CfgDepartamento.class);
    }

    public List<CfgDepartamento> buscarDepartamentosOrdenado() {
        try {
            Query query = em.createQuery("SELECT d FROM CfgDepartamento d ORDER BY d.nomDepartamento");
            return query.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

}
