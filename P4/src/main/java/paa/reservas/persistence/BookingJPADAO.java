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
	//hacer un metodo para saber que habitaciones estan ocupadas en un periodo de tiempo
	//que devuelva los numeros de las habitaciones ocupadas
//	public List<Integer> findindRooms(Long hotelCode, LocalDate date) {
//		TypedQuery<Integer> q = em.createQuery(
//		        "SELECT b.roomNumber FROM Booking b " +
//			    "WHERE b.hotel.code = :hotelCode " +
//		        "WHERE b.departureDate > :date " +
//		        "AND (b.arrivalDate < :date OR b.arrivalDate = :date) " +
//		        "AND b.roomNumber <= :MAXIMUM_SINGLE_ROOM_NUMBER", Integer.class);
//		q.setParameter("date", date);
//	    q.setParameter("hotelCode", hotelCode);
//		q.setParameter("MAXIMUM_SINGLE_ROOM_NUMBER", 199);
//		List<Integer> result=q.getResultList();
//		int a=result.size();
//		return result;
//	}
//	public int findindRooms(Long hotelCode, LocalDate date) {
//		TypedQuery<Integer> q = em.createQuery(
//		        "SELECT b.roomNumber FROM Booking b " +
//			    "WHERE b.hotel.code = :hotelCode " +
//		        "WHERE b.departureDate > :date " +
//		        "AND (b.arrivalDate < :date OR b.arrivalDate = :date) " +
//		        "AND b.roomNumber <= :MAXIMUM_SINGLE_ROOM_NUMBER", Integer.class);
//		q.setParameter("date", date);
//	    q.setParameter("hotelCode", hotelCode);
//		q.setParameter("MAXIMUM_SINGLE_ROOM_NUMBER", 199);
//		List<Integer> result=q.getResultList();
//		int a=result.size();
//		return a;
//	}
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
