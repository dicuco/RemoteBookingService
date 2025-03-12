package paa.reservas.persistence;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import paa.reservas.model.Booking;
import paa.reservas.model.Hotel;

public class BookingJPADAO extends JPADAO<Booking,Long>{
    private EntityManager entityManager;

	public BookingJPADAO(EntityManager em) {
		super(em, Booking.class);
		// TODO Auto-generated constructor stub
	}

	public List<Integer> findNumberRooms(Long hotelCode, LocalDate arrivalDate, LocalDate departureDate) {
	    TypedQuery<Integer> q = em.createQuery(
	            "SELECT b.roomNumber FROM Booking b " +
	            "WHERE b.hotel.code = :hotelCode " +
	            "AND b.departureDate > :arrivalDate " +
	            "AND b.arrivalDate < :departureDate " +
	            "AND b.roomNumber <= :MAXIMUM_DOUBLE_ROOM_NUMBER", Integer.class);

	    q.setParameter("hotelCode", hotelCode);
	    q.setParameter("arrivalDate", arrivalDate);
	    q.setParameter("departureDate", departureDate);
	    q.setParameter("MAXIMUM_DOUBLE_ROOM_NUMBER", 300); 

	    List<Integer> result = q.getResultList();

	    return result;
	}

	
}
