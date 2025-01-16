package paa.reservas.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import paa.reservas.business.*;
import paa.reservas.model.Hotel;


/**
 * Servlet implementation class IncidenciaServer
 */
@WebServlet("/BookingServer")
public class BookingServer extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private BookingService service;
	private ObjectMapper mapper;
    /**
     * @see HttpServlet#HttpServlet()
     */
	public BookingServer() {
		super();
	}
    @Override
    public void init() throws ServletException {
    	super.init();
    	service = new JPABookingService("paa");
    	mapper = new ObjectMapper();
    	mapper.registerModule(new JavaTimeModule());
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String action = request.getParameter("action");
		if (null == action) {
			response.setStatus(400);
			response.getWriter().println("Falta el parámetro action");
			return;
		}
		response.setCharacterEncoding("utf-8");
		switch(action) {
		case "createHotel":
			createHotel(request, response);
			break;
		case "listHotels":
			listHotels(request, response);
			break;
		case "occupiedDoubleRooms":
			occupiedDoubleRooms(request, response);
			break;
		case "occupiedSingleRooms":
			occupiedSingleRooms(request, response);
			break;
		case "findHotel":
			findHotel(request, response);
			break;
		case "makeBooking":
			MakeBooking(request,response);
			break;
		case "cancelBooking": 
			cancelBooking(request,response);
			break;
		default:
			response.setStatus(400);
			response.getWriter().println("Acción no soportada");
			return;		
		}
	}
	 void findHotel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			Long id = Long.valueOf(request.getParameter("hotelCode"));
			mapper.writeValue(response.getWriter(), service.findHotel(id));	
		} catch (Exception e) {
			response.setStatus(400);
			response.getWriter().println("Debes proporcionar un parámetro 'id' entero " + e.getMessage());
		}
		
	}
	 //createHotel(String name, String address, int stars, double longitude, double latitude, int doubleRooms, int singleRooms)
	void createHotel(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			String name = String.valueOf(request.getParameter("name"));
			String address = String.valueOf(request.getParameter("address"));
			int stars= Integer.valueOf(request.getParameter("stars"));
			double longitude= Double.valueOf(request.getParameter("longitude"));
			double latitude= Double.valueOf(request.getParameter("latitude"));
			int doubleRooms= Integer.valueOf(request.getParameter("doubleRooms"));
			int singleRooms= Integer.valueOf(request.getParameter("singleRooms"));

			mapper.writeValue(response.getWriter(), service.createHotel(name, address, stars,longitude,latitude,doubleRooms,singleRooms));
		}catch(Exception e){
			response.setStatus(400);
			response.getWriter().println(e.getMessage());
			e.printStackTrace();
		}
		
	}
	//findAllHotels()
	void listHotels(HttpServletRequest request, HttpServletResponse response) throws IOException{
		List<Hotel> clientes = service.findAllHotels();
		mapper.writeValue(response.getWriter(), clientes);
	}
	
	//occupiedDoubleRooms(Long hotelCode, LocalDate date)
	void occupiedDoubleRooms(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			Long hotelCode= Long.valueOf(request.getParameter("hotelCode"));
			LocalDate date= LocalDate.parse(request.getParameter("date"));
			mapper.writeValue(response.getWriter(), service.occupiedDoubleRooms(hotelCode, date));
		}catch(Exception e) {
			response.setStatus(400);
			response.getWriter().println(e.getMessage());
		}
	}
	
	//occupiedSingleRooms(Long hotelCode, LocalDate date)
	void occupiedSingleRooms(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			Long hotelCode= Long.valueOf(request.getParameter("hotelCode"));
			LocalDate date= LocalDate.parse(request.getParameter("date"));
			mapper.writeValue(response.getWriter(), service.occupiedSingleRooms(hotelCode, date));
		}catch(Exception e) {
			response.setStatus(400);
			response.getWriter().println(e.getMessage());
		}
	}
	//makeBooking(Long hotelCode, int numberOfPeople, String travellerName, LocalDate arrivalDate,LocalDate departureDate, LocalDate operationDate)
	void MakeBooking(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			Long hotelCode= Long.valueOf(request.getParameter("hotelCode"));
			int numberOfPeople= Integer.valueOf(request.getParameter("numberOfPeople"));
			String travellerName = String.valueOf(request.getParameter("travellerName"));
			LocalDate arrivalDate= LocalDate.parse(request.getParameter("arrivalDate"));
			LocalDate departureDate= LocalDate.parse(request.getParameter("departureDate"));
			LocalDate operationDate= LocalDate.parse(request.getParameter("operationDate"));
			
			mapper.writeValue(response.getWriter(), service.makeBooking(hotelCode, numberOfPeople, travellerName, arrivalDate, departureDate, operationDate));
		}catch(Exception e) {
			response.setStatus(400);
			response.getWriter().println(e.getMessage());
		}
	}
	
	//cancelBooking(Long bookingCode, LocalDate operationDate)
	void cancelBooking(HttpServletRequest request, HttpServletResponse response) throws IOException{
		try {
			Long bookingCode= Long.valueOf(request.getParameter("bookingCode"));
			LocalDate operationDate= LocalDate.parse(request.getParameter("operationDate"));
			// cancelamos la reserva a parte ya que no devuelve nada y no nos puede mostar nada por pantalla y el writerValue no fucionaria
	        service.cancelBooking(bookingCode, operationDate);
	        //si no saltan errores ni nada ponemos la respuesta en 200
	        response.setStatus(200);
		}catch(Exception e) {
			response.setStatus(400);
			response.getWriter().println(e.getMessage());
		}
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
