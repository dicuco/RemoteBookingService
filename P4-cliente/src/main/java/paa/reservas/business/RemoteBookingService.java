package paa.reservas.business;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import paa.reservas.model.Booking;
import paa.reservas.model.Hotel;

public class RemoteBookingService implements BookingService{
	private ObjectMapper mapper;
	private final String BASE_URL = "http://localhost:8080/p4-servidor/BookingServer?action="; 
	
	public RemoteBookingService() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}
	
	private static String pideURL(String url) throws BookingServiceException {
		System.err.println(url);
		try {
			HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
			int code = conn.getResponseCode();
			switch(code) {
			case 200:
				return new String(conn.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
			case 400:
				throw new BookingServiceException(new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));
			default:
				throw new RuntimeException("Ha pasado algo que no sabemos qué es");
			}
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	@Override
	public Hotel createHotel(String name, String address, int stars, double longitude, double latitude, int doubleRooms,
			int singleRooms) throws BookingServiceException {
		String url= BASE_URL + "createHotel&name="+ URLEncoder.encode(name, StandardCharsets.UTF_8)+ 
				"&address="+URLEncoder.encode(address, StandardCharsets.UTF_8) + "&stars="+stars +"&longitude="+ longitude +"&latitude="+ latitude + "&doubleRooms="+
			doubleRooms+ "&singleRooms="+ singleRooms;
		String respuesta= pideURL(url);
		Hotel h=new Hotel();
		try {
			h= mapper.readValue(respuesta, Hotel.class);
			return h;
		}catch(JsonMappingException e) {
			throw new BookingServiceException("JsonMappingException");
		}catch(JsonProcessingException e) {
			throw new BookingServiceException("JsonProcessingException");
		}
	}

	@Override
	public Hotel findHotel(Long hotelCode) {
		String url =  BASE_URL + "findHotel&hotelCode=" + hotelCode;
		try {
			String respuesta = pideURL(url);
			Hotel h = mapper.readValue(respuesta, Hotel.class);
			h.getBookings().size();
			return h;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public List<Hotel> findAllHotels() {
		String url =  BASE_URL + "listHotels";
		try {
			String respuesta = pideURL(url);
			List<Hotel> c = mapper.readValue(respuesta, new TypeReference<List<Hotel>>() {});
			for(Hotel hotel:c) {
				hotel.getBookings().size();	
				/*
				 Ultima modificacion
				 */
			}
			return c;
		}catch(JsonMappingException e) {
			throw new BookingServiceException("JsonMappingException");
		}catch(JsonProcessingException e) {
			throw new BookingServiceException("JsonProcessingException");
		} catch (Exception e) {
			throw new BookingServiceException("Excepcion general");
		}
	}

	@Override
	public int occupiedDoubleRooms(Long hotelCode, LocalDate date) throws BookingServiceException {
		String url = BASE_URL + "occupiedDoubleRooms&hotelCode=" + hotelCode +"&date="+ date;
		try {
			String respuesta= pideURL(url);
			int c=mapper.readValue(respuesta, Integer.class);
			return c;
		}catch(Exception e) {
			return 0;
		}
	}

	@Override
	public int occupiedSingleRooms(Long hotelCode, LocalDate date) throws BookingServiceException {
		String url = BASE_URL + "occupiedSingleRooms&hotelCode=" + hotelCode +"&date="+ date;
		try {
			String respuesta= pideURL(url);
			int c=mapper.readValue(respuesta, Integer.class);
			return c;
		}catch(Exception e) {
			return 0;
		}
	}

	@Override
	public Booking makeBooking(Long hotelCode, int numberOfPeople, String travellerName, LocalDate arrivalDate,
			LocalDate departureDate, LocalDate operationDate) throws BookingServiceException {
		// TODO Auto-generated method stub
		String url= BASE_URL+"makeBooking&hotelCode="+hotelCode+"&numberOfPeople="+numberOfPeople+"&travellerName="+URLEncoder.encode(travellerName, StandardCharsets.UTF_8)
				+ "&arrivalDate="+arrivalDate+"&departureDate="+departureDate+"&operationDate="+operationDate;
		String respuesta=pideURL(url);
		Booking b=new Booking();
		try {
			b=mapper.readValue(respuesta, Booking.class);
			return b;
		}catch(Exception e) {
			e.printStackTrace();
			throw new BookingServiceException("Error al crear la reserva");
		}
	}

	@Override
	public void cancelBooking(Long bookingCode, LocalDate operationDate) throws BookingServiceException {
		String url = BASE_URL+"cancelBooking&bookingCode="+bookingCode+"&operationDate="+operationDate;
		try {
			pideURL(url);
		}catch(Exception e) {
			throw new BookingServiceException("Error al cancelar la reserva");
		}
	}

}