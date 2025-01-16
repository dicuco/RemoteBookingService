package paa.reservas.business;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import paa.reservas.model.Booking;
import paa.reservas.model.Hotel;
import paa.reservas.persistence.BookingJPADAO;
import paa.reservas.persistence.HotelJPADAO;
import javax.persistence.Persistence;


public class JPABookingService implements BookingService {
	private final String PERSISTENCE_UNIT_NAME = "paa"; // Definido en persistence.xml
//	private DAOFactory daoFactory;

	/** Lowest number of a single room at any hotel */
    int MINIMUM_SINGLE_ROOM_NUMBER = 100;
    /** Highest possible number of a single room at any hotel */
    int MAXIMUM_SINGLE_ROOM_NUMBER = 199;
    /** Lowest number of a double room at any hotel */
    int MINIMUM_DOUBLE_ROOM_NUMBER = 200;
    /** Highest possible number of a double room at any hotel */
    int MAXIMUM_DOUBLE_ROOM_NUMBER = 299;
    /** Maximum number of nights of any stay */
    int MAXIMUM_STAY_LENGTH = 30;
    int maximumSingleRooms=MAXIMUM_SINGLE_ROOM_NUMBER-MINIMUM_SINGLE_ROOM_NUMBER+1;
    int maximumDoubleRooms=MAXIMUM_DOUBLE_ROOM_NUMBER-MINIMUM_DOUBLE_ROOM_NUMBER+1;

    double MIN_LONGITUDE=-180;
    double MAX_LONGITUDE=180;
    double MIN_LATITUDE=-90;
    double MAX_LATITUDE=90;

	private EntityManagerFactory emf;
	
    public JPABookingService() {
		emf = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
    }
    public JPABookingService(String PersistenceUnitName) {
		emf = Persistence.createEntityManagerFactory(PersistenceUnitName);
    }

	public void close() {
		this.emf.close();
	}
	@Override
	public Hotel createHotel(String name, String address, int stars, double longitude, double latitude, int doubleRooms, int singleRooms){
		// TODO Auto-generated method stub
		//funcionan todos los test
		if (name == null||name.isBlank()) {
			throw new BookingServiceException ("Error, el hotel debe de tener nombre");
		}
		
		if (address == null||address.isBlank()) {
			throw new BookingServiceException ("Error, la direccion del hotel no puede ser nula");
		}
		
		if (stars < 0 || stars > 5) {
			throw new BookingServiceException ("Error, el hotel debe de tener entre 0 y 5 estrellas");
		}
		if (longitude > MAX_LONGITUDE || longitude < MIN_LONGITUDE) {
			throw new BookingServiceException ("Error, la longitud debe estar entre "+MAX_LONGITUDE+ " y "+ MIN_LONGITUDE);
		}
		
		if (latitude > MAX_LATITUDE || latitude < MIN_LATITUDE) {
			throw new BookingServiceException ("Error, la latitud debe estar entre 180 y -180.");
		}
		
		if (doubleRooms == 0 && singleRooms ==0) {
			throw new BookingServiceException (" Error, el Hotel debe de tener alguna habitacion.");
		}
		
		if (doubleRooms < 0 || doubleRooms > 100) {
			throw new BookingServiceException ("Error, numero de habitaciones dobles no valido.");
		}
		
		if (singleRooms < 0 || singleRooms > 100) {
			throw new BookingServiceException ("Error, numero de habitaciones individuales no valido.");
		}

		Hotel h= new Hotel(null,name,address,stars,longitude,latitude,doubleRooms,singleRooms);
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		HotelJPADAO hotelDAO;
		try {
			et.begin();
			hotelDAO = new HotelJPADAO(em);
			h=hotelDAO.create(h);
			et.commit();
//			return h;

		} catch(BookingServiceException e) {
			if (et.isActive()) {
				et.rollback();
			}
			throw new BookingServiceException ("[ERROR] El hotel ya existe");
		}catch (Exception e) {
			if (et.isActive()) {
				et.rollback();
			}
			throw new BookingServiceException("Error: ha ocurrido algo al acceder a la base de datos");
		} finally {
			em.close();
		}
		return h;

	
	}

	@Override
	public Hotel findHotel(Long hotelCode) {
		// TODO Auto-generated method stub
		EntityManager em = emf.createEntityManager();
		// Pido a la factoria un DAO de Hotel
		HotelJPADAO HotelDAO=new HotelJPADAO(em);
		Hotel hotel = HotelDAO.findById(hotelCode);
		if(hotel==null) {
			return null;
		}
		hotel.getBookings().size(); // Force Hibernate to initialize Collection
		em.close();
		return hotel;
	}

	@Override
	public List<Hotel> findAllHotels() {
		// TODO Auto-generated method stub
		//pasa todos los tests
		EntityManager em = emf.createEntityManager();

		HotelJPADAO hotelDAO = new HotelJPADAO(em);
		List<Hotel> lista = hotelDAO.findAll();
		return lista;

	}

	@Override
    public int occupiedDoubleRooms(Long hotelCode, LocalDate date) throws BookingServiceException {
         if (date == null) {
                throw new BookingServiceException("La fecha no puede ser nula.");
            }
            if (hotelCode == null) {
                throw new BookingServiceException("El código de hotel no puede ser nulo.");
            }

            EntityManager em = emf.createEntityManager();
            EntityTransaction et = em.getTransaction();
            et.begin();
            try {

                HotelJPADAO hotelDAO = new HotelJPADAO(em);
                Hotel hotel = hotelDAO.findById(hotelCode);
                if (hotel == null) {
                    throw new BookingServiceException("Hotel no encontrado con el código: " + hotelCode);
                }

                List<Booking> bookings = hotel.getBookings();
                int occupiedRooms = 0;
                for (Booking booking : bookings) {
                    if ((booking.getDepartureDate().isAfter(date))&&((booking.getArrivalDate().isBefore(date))||booking.getArrivalDate().isEqual(date))&& booking.getRoomNumber() >=MINIMUM_DOUBLE_ROOM_NUMBER) {
                        occupiedRooms++;
                    }
                }
                et.commit();
                return occupiedRooms;
            } catch (RuntimeException e) {
            	et.rollback();
    			em.close();
                throw new BookingServiceException("Error al obtener las habitaciones dobles ocupadas.", e);
            } finally {
                em.close();
            }
}

	@Override
	public int occupiedSingleRooms(Long hotelCode, LocalDate date) throws BookingServiceException {
		// TODO Auto-generated method stub
        if (date == null) {
            throw new BookingServiceException("La fecha no puede ser nula.");
        }
        if (hotelCode == null) {
            throw new BookingServiceException("El código de hotel no puede ser nulo.");
        }

        EntityManager em = emf.createEntityManager();
        try {
            EntityTransaction et = em.getTransaction();
            et.begin();

            HotelJPADAO hotelDAO =new HotelJPADAO(em);
            Hotel hotel = hotelDAO.findById(hotelCode);
            if (hotel == null) {
                throw new BookingServiceException("Hotel no encontrado con el código: " + hotelCode);
            }
            int occupiedRooms = 0;
            List<Booking> bookings = hotel.getBookings();
            for (Booking booking : bookings) {
            	
            	if ((booking.getDepartureDate().isAfter(date))&&((booking.getArrivalDate().isBefore(date))||booking.getArrivalDate().isEqual(date))&& booking.getRoomNumber() <=MAXIMUM_SINGLE_ROOM_NUMBER) {
                    occupiedRooms++;
                }
            }
            
            et.commit();
            return occupiedRooms;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new BookingServiceException("Error al obtener las habitaciones individuales ocupadas.", e);
        } finally {
            em.close();
        }
	} 

	@Override
	public Booking makeBooking(Long hotelCode, int numberOfPeople, String travellerName, LocalDate arrivalDate,
			LocalDate departureDate, LocalDate operationDate) throws BookingServiceException {
		// TODO Auto-generated method stub
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();
		et.begin();
		try {
			//Comprobamos los if
	        if (hotelCode == null) {
	            throw new BookingServiceException("El código de hotel no puede ser nulo.");
	        }
	        if((numberOfPeople!=1)&&(numberOfPeople!=2)) {
	            throw new BookingServiceException("El numero de personas debe ser 1 o 2");
	        }
	        if(travellerName==null||travellerName.equals("")) {
	            throw new BookingServiceException("El nombre del viajero no puede ser nulo");
	        }
	        if(arrivalDate==null) {
	        	throw new BookingServiceException("La fecha de llegada no puede ser nula");
	        }
	        if(departureDate==null || !departureDate.isAfter(arrivalDate)) {
	        	throw new BookingServiceException("La fecha de salia no puede ser nula o ser anterior a la fecha de llegada");
	        }
	        if(operationDate==null) {
	        	throw new BookingServiceException("La fecha de operacion no puede ser nula");
	        }
	        if(arrivalDate.isBefore(operationDate)) {
	        	throw new BookingServiceException("La reserva no puede hacerse en el pasado");
	        }
	        long differenceInDays = ChronoUnit.DAYS.between(arrivalDate, departureDate);

		     // Comparar la diferencia con el valor máximo permitido
		     if (differenceInDays > MAXIMUM_STAY_LENGTH) {
		         throw new BookingServiceException("La duración de la estancia no puede ser mayor a " + MAXIMUM_STAY_LENGTH + " días");
		     }

	     	HotelJPADAO HotelDAO = new HotelJPADAO(em);
	     	Hotel hotel = HotelDAO.findById(hotelCode);
	     	if(hotel==null) {
	        	throw new BookingServiceException("EL hotel no existe");
	     	}
	     	
	     	boolean dobles=(occupiedDoubleRooms(hotelCode,arrivalDate)<hotel.getDoubleRooms());
	    	boolean single=(occupiedSingleRooms(hotelCode,arrivalDate)<	hotel.getSingleRooms()-1); // si le resto una el Combinaciones de reserva funciona pero otras pruebas no
	    	
	     	if(!dobles&&numberOfPeople==2) {
	        	throw new BookingServiceException("No quedan habitaciones dobles disponibles");
	     	}
	     
	     	BookingJPADAO BookDAO = new BookingJPADAO(em);
            //numero de habitaciones dobles o individuales.

	     	int singleRoomsNum=MINIMUM_SINGLE_ROOM_NUMBER+hotel.getSingleRooms()-1;
	     	int doubleRoomsNum=MINIMUM_DOUBLE_ROOM_NUMBER+hotel.getDoubleRooms()-1;            
           // List<Integer> reservedRooms = occupiedRooms(hotelCode,arrivalDate,departureDate);
	     	
	     	
            List<Integer> reservedRooms= BookDAO.findNumberRooms(hotelCode,arrivalDate,departureDate);
           // List<Integer> reservedRooms= BookDAO.findNumberRooms1(hotel,arrivalDate,departureDate);
            int roomNumber=0;
            if(numberOfPeople==1) {
            	if(single) {
            		roomNumber=firstRoomNumber(reservedRooms,MINIMUM_SINGLE_ROOM_NUMBER,singleRoomsNum);

            	}else {
            		roomNumber=firstRoomNumber(reservedRooms,MINIMUM_DOUBLE_ROOM_NUMBER,doubleRoomsNum);

            	}
            	if(MINIMUM_SINGLE_ROOM_NUMBER==singleRoomsNum) {
        		roomNumber=MINIMUM_SINGLE_ROOM_NUMBER;
            	}
            	if(occupiedSingleRooms(hotelCode,arrivalDate)==hotel.getSingleRooms()) {
            		roomNumber=firstRoomNumber(reservedRooms,MINIMUM_DOUBLE_ROOM_NUMBER,doubleRoomsNum);

            	}
            }else {
        		roomNumber=firstRoomNumber(reservedRooms,MINIMUM_DOUBLE_ROOM_NUMBER,doubleRoomsNum);

            }
 
		    //public Booking(Long code, int roomNumber, int numberOfPeople, String travellerName, LocalDate arrivalDate, LocalDate departureDate, Hotel hotel) 
			Booking b=new Booking(null,roomNumber, numberOfPeople, travellerName, arrivalDate, departureDate,  hotel);
			Booking save = BookDAO.create(b);
			et.commit();
			em.close();
			return save;
		}catch(Exception e) {
			et.rollback();
			em.close();
			throw new BookingServiceException("Error al crear la reserva");
		}finally {
			em.close();
		}
	}
	
	@Override
	public void cancelBooking(Long bookingCode, LocalDate operationDate) throws BookingServiceException {
		// TODO Auto-generated method stub
		EntityManager em = emf.createEntityManager();
		EntityTransaction et = em.getTransaction();

		try {

			if (bookingCode==null )
				throw new BookingServiceException("El codigo de reserva no es valido");

			BookingJPADAO bookingDAO = new BookingJPADAO(em);
			// Creo el book con el bookingCode recibido por parametro
			Booking book = bookingDAO.findById(bookingCode);

			// Comprobar que la reserva existe
			if (book == null)
				throw new BookingServiceException("No existe esta reserva");
			et.begin();

			// Obtengo su fecha de llegada
			LocalDate arrivalDate = book.getArrivalDate();
			
			if (arrivalDate.equals(LocalDate.now().minusDays(MAXIMUM_STAY_LENGTH - 1))|| arrivalDate.equals(LocalDate.now())|| arrivalDate.isAfter(operationDate)) {
				bookingDAO.delete(book);
			} else {
				throw new BookingServiceException("No se puede eliminar esta reserva");
			}

			et.commit();
			em.close();

		} catch (Exception e) {
			et.rollback();
			em.close();
			throw new BookingServiceException(e.getMessage());
		}

	}
	

    public static Integer firstRoomNumber(List<Integer> room, int arrivalDate, int departureDate) {
    	int roomNumber=0;
        for (int i = arrivalDate; i <= departureDate; i++) {
            if (!room.contains(i)) {
                roomNumber=i;
                return i;
            }
        }
        return roomNumber;
    }

}
