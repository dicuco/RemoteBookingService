package paa.reservas.persistence;


import java.util.List;

import javax.persistence.EntityManager;

import paa.reservas.model.Hotel;

public class HotelJPADAO implements DAO<Hotel, Long> {
	   protected EntityManager em;

	    public HotelJPADAO(EntityManager em) {
	        this.em = em;
	    }

	    @Override
	    public Hotel findById(Long id) {
	        return em.find(Hotel.class, id);
	    }

	    @Override

	    public Hotel create(Hotel t) {
	        em.persist(t);
	        em.flush();
	        em.refresh(t);
	        return t;
	    }


	    @Override
	    public Hotel update(Hotel t) {

	        return (Hotel) em.merge(t);
	    }

	    @Override
	    public void delete(Hotel t) {
	        em.remove(t);
	    }

	    @Override
	    public List<Hotel> findAll() {
	        // Complete este método, que debe listar todos los elementos de la clase T.
	        // Necesitará hacer consultas a la base datos mediante una TypedQuery, bien
	        // empleando una sentencia JPQL o una CriteriaQuery
	        return em.createQuery("SELECT h FROM Hotel h").getResultList();
	   }
	    	    
}


