//package paa.reservas;
//
//import org.junit.jupiter.api.*;
//import paa.reservas.business.BookingService;
//import paa.reservas.business.BookingServiceException;
//import paa.reservas.business.JPABookingService;
//import paa.reservas.model.Booking;
//import paa.reservas.model.Hotel;
//
//import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
//import javax.persistence.Persistence;
//import javax.persistence.Query;
//import java.time.LocalDate;
//import java.util.*;
//
//
//public class BookingServiceTest {
//    BookingService bs;
//
//    // Este método se ejecuta antes de cada prueba para asegurar que independientemente de la configuración de la
//    // unidad de persistencia la base de datos esté vacía antes de cada prueba para asegurar la repetibilidad de las
//    // pruebas.
//    @BeforeEach
//    public void initializeService() {
//        this.bs = null;
//        wipeDatabase();
//        this.bs = new JPABookingService();
//    }
//
//    public void wipeDatabase() {
//        EntityManagerFactory emf = Persistence.createEntityManagerFactory("paa");
//        EntityManager em = emf.createEntityManager();
//        em.getTransaction().begin();
//        Query q = em.createQuery("DELETE FROM Booking");
//        q.executeUpdate();
//        q = em.createQuery("DELETE FROM Hotel");
//        q.executeUpdate();
//        em.getTransaction().commit();
//        em.close();
//        emf.close();
//    }
//
//    // Esta clase auxiliar se utilizará para preparar datos antes de cada prueba reduciendo la cantidad de código
//    // que repetimos entre pruebas.
//    abstract class PrepareData {
//        public void run() {
//            EntityManagerFactory factory = Persistence.createEntityManagerFactory("paa");
//            EntityManager em = factory.createEntityManager();
//            em.getTransaction().begin();
//            thingsToDo(em);
//            em.getTransaction().commit();
//            em.close();
//            factory.close();
//        }
//
//        abstract void thingsToDo(EntityManager em);
//    }
//
//
//    @Nested
//    @DisplayName("Pruebas de createHotel")
//    class CreateHotelTests {
//        @Test
//        @DisplayName("Normal")
//        void createHotelNormal() {
//            int maximumDoubleRooms = BookingService.MAXIMUM_DOUBLE_ROOM_NUMBER - BookingService.MINIMUM_DOUBLE_ROOM_NUMBER + 1;
//            int maximumSingleRooms = BookingService.MAXIMUM_SINGLE_ROOM_NUMBER - BookingService.MINIMUM_SINGLE_ROOM_NUMBER + 1;
//            for (int i = 0; i < 100; ++i) {
//                String name = "Nombre " + i;
//                String address = "Calle tal y cual, " + i;
//                int stars = new Random().nextInt(6); // Aleatorio en el intervalo [0, 6)
//                double longitude = new Random().nextDouble() * 360 - 180; // Aleatorio en el intervalo [-180, 180)
//                double latitude = new Random().nextDouble() * 180 - 90; // Aleatorio en el intervalo [-90, 90)
//                int doubleRooms = new Random().nextInt(1, maximumDoubleRooms + 1); // Aleatorio en el intervalo [1, maximumDoubleRooms)
//                int singleRooms = new Random().nextInt(1, maximumSingleRooms + 1); // Aleatorio en el intervalo [1, maximumSingleRooms)
//                try {
//                    Hotel h = bs.createHotel(name, address, stars, longitude, latitude, doubleRooms, singleRooms);
//                    Assertions.assertNotNull(h, "El hotel creado no debe ser nulo");
//                    Assertions.assertEquals(name, h.getName(), "El nombre del hotel creado no coincide con el esperado");
//                    Assertions.assertEquals(address, h.getAddress(), "La dirección del hotel creado no coincide con la esperada");
//                    Assertions.assertEquals(stars, h.getStars(), "El número de estrellas del hotel creado no coincide con el esperado");
//                    Assertions.assertEquals(longitude, h.getLongitude(), 0.0000001, "La longitud del hotel creado no coincide con la esperada");
//                    Assertions.assertEquals(latitude, h.getLatitude(), 0.0000001, "La latitud del hotel creado no coincide con la esperada");
//                    Assertions.assertEquals(doubleRooms, h.getDoubleRooms(), "El número de habitaciones dobles del hotel creado no coincide con el esperado");
//                    Assertions.assertEquals(singleRooms, h.getSingleRooms(), "El número de habitaciones individuales del hotel creado no coincide con el esperado");
//                    Assertions.assertNotNull(h.getCode(), "El código del hotel creado no debe ser nulo");
//                } catch (BookingServiceException e) {
//                    Assertions.fail("Se lanzó BookingServiceException erróneamente al crear un hotel con parámetros correctos");
//                }
//            }
//        }
//
//        @Test
//        @DisplayName("Nombre nulo")
//        void createHotelWithNullName() {
//            try {
//                bs.createHotel(null, "address", 0, 0, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con nombre nulo");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Nombre vacío")
//        void createHotelWithEmptyName() {
//            try {
//                bs.createHotel("", "address", 0, 0, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con nombre vacío");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Dirección nula")
//        void createHotelWithNullAddress() {
//            try {
//                bs.createHotel("name", null, 0, 0, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con dirección nula");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Dirección vacía")
//        void createHotelWithEmptyAddress() {
//            try {
//                bs.createHotel("name", "", 0, 0, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con dirección vacía");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Valores límite de longitud")
//        void createHotelWithInvalidLongitude() {
//            try {
//                // Estos son valores límite correctos, no debe haber excepción
//                bs.createHotel("name", "address", 0, -180, 0, 1, 1);
//                bs.createHotel("name", "address", 0, 180, 0, 1, 1);
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException con valores de longitud perfectamente válidos");
//            }
//            try {
//                bs.createHotel("name", "address", 0, -180 - 0.0000001, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con longitud demasiado baja");
//            } catch (BookingServiceException ignored) {
//            }
//            try {
//                bs.createHotel("name", "address", 0, 180 + 0.0000001, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con longitud demasiado alta");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Valores límite de latitud")
//        void createHotelWithInvalidLatitude() {
//            try {
//                // Estos son valores límite correctos, no debe haber excepción
//                bs.createHotel("name", "address", 0, 0, -90, 1, 1);
//                bs.createHotel("name", "address", 0, 0, 90, 1, 1);
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException con valores de latitud perfectamente válidos");
//            }
//            try {
//                bs.createHotel("name", "address", 0, 0, -90 - 0.0000001, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con latitud demasiado baja");
//            } catch (BookingServiceException ignored) {
//            }
//            try {
//                bs.createHotel("name", "address", 0, 0, 90 + 0.0000001, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con latitud demasiado alta");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Número negativo de habitaciones dobles")
//        void createHotelWithNegativeDoubleRooms() {
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, -1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con un número negativo de habitaciones dobles");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Número negativo de habitaciones individuales")
//        void createHotelWithNegativeSingleRooms() {
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, 0, -1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con un número negativo de habitaciones individuales");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Número máximo de habitaciones dobles")
//        void createHotelWithMaximumDoubleRooms() {
//            int maximumDoubleRooms = BookingService.MAXIMUM_DOUBLE_ROOM_NUMBER - BookingService.MINIMUM_DOUBLE_ROOM_NUMBER + 1;
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, maximumDoubleRooms, 1);
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException erróneamente al crear un hotel con el número máximo posible de habitaciones dobles");
//            }
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, maximumDoubleRooms + 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con un número de habitaciones dobles mayor del máximo");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Número máximo de habitaciones individuales")
//        void createHotelWithMaximumSingleRooms() {
//            int maximumSingleRooms = BookingService.MAXIMUM_SINGLE_ROOM_NUMBER - BookingService.MINIMUM_SINGLE_ROOM_NUMBER + 1;
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, 1, maximumSingleRooms);
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException erróneamente al crear un hotel con el número máximo posible de habitaciones individuales");
//            }
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, 1, maximumSingleRooms + 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con un número de habitaciones individuales mayor del máximo");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Cero habitaciones de algún tipo")
//        void createHotelWithZeroRooms() {
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, 0, 1);
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException erróneamente al crear un hotel con cero habitaciones dobles pero al menos una individual");
//            }
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, 1, 0);
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException erróneamente al crear un hotel con cero habitaciones individuales pero al menos una doble");
//            }
//            try {
//                bs.createHotel("name", "address", 0, 0, 0, 0, 0);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con cero habitaciones de ambos tipos");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Estrellas válidas entre 0 y 5")
//        void createHotelCheckStarRange() {
//            int minimumValidStars = 0;
//            int maximumValidStars = 5;
//            for (int i = minimumValidStars; i <= maximumValidStars; ++i) {
//                try {
//                    bs.createHotel("name", "address", i, 0, 0, 1, 1);
//                } catch (BookingServiceException ignored) {
//                    Assertions.fail("Se lanzó BookingServiceException erróneamente al crear un hotel con un número válido de estrellas");
//                }
//            }
//            try {
//                bs.createHotel("name", "address", minimumValidStars - 1, 0, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con un número de estrellas menor del mínimo");
//            } catch (BookingServiceException ignored) { /* nada */ }
//            try {
//                bs.createHotel("name", "address", maximumValidStars + 1, 0, 0, 1, 1);
//                Assertions.fail("No se lanzó BookingServiceException al crear un hotel con un número de estrellas mayor del máximo");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//    }
//
//    @Nested
//    @DisplayName("Pruebas de findHotel")
//    class FindHotelTests {
//        @Test
//        @DisplayName("Normal")
//        void findHotelNormal() {
//            Map<Long, Hotel> insertedHotels = new HashMap<>();
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    for (int i = 0; i < 50; ++i) {
//                        Hotel h = new Hotel(null, "Nombre " + i, "Calle tal y cual, " + i, i % 6, 0, 0, i, i);
//                        em.persist(h);
//                        insertedHotels.put(h.getCode(), h);
//                    }
//                }
//            }.run();
//            for (Map.Entry<Long, Hotel> entry : insertedHotels.entrySet()) {
//                Long code = entry.getKey();
//                Hotel foundHotel = bs.findHotel(code);
//                Hotel insertedHotel = entry.getValue();
//                Assertions.assertNotNull(foundHotel, "El hotel encontrado no debe ser nulo");
//                Assertions.assertEquals(insertedHotel.getName(), foundHotel.getName(), "El nombre del hotel encontrado no coincide con el esperado");
//                Assertions.assertEquals(insertedHotel.getAddress(), foundHotel.getAddress(), "La dirección del hotel encontrado no coincide con la esperada");
//                Assertions.assertEquals(insertedHotel.getStars(), foundHotel.getStars(), "El número de estrellas del hotel encontrado no coincide con el esperado");
//                Assertions.assertEquals(insertedHotel.getLongitude(), foundHotel.getLongitude(), 0.0000001, "La longitud del hotel encontrado no coincide con la esperada");
//                Assertions.assertEquals(insertedHotel.getLatitude(), foundHotel.getLatitude(), 0.0000001, "La latitud del hotel encontrado no coincide con la esperada");
//                Assertions.assertEquals(insertedHotel.getDoubleRooms(), foundHotel.getDoubleRooms(), "El número de habitaciones dobles del hotel encontrado no coincide con el esperado");
//                Assertions.assertEquals(insertedHotel.getSingleRooms(), foundHotel.getSingleRooms(), "El número de habitaciones individuales del hotel encontrado no coincide con el esperado");
//            }
//        }
//
//        @Test
//        @DisplayName("Búsqueda de hotel no existente")
//        void findHotelNonExistent() {
//            final long[] deletedHotelCode = new long[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    int randomIndex = new Random().nextInt(50);
//                    for (int i = 0; i < 50; ++i) {
//                        Hotel h = new Hotel(null, "Nombre " + i, "Calle tal y cual, " + i, i % 6, 0, 0, i, i);
//                        em.persist(h);
//                        if (i == randomIndex) {
//                            deletedHotelCode[0] = h.getCode();
//                            em.remove(h);
//                        }
//                    }
//                }
//            }.run();
//
//            Hotel foundHotel = bs.findHotel(deletedHotelCode[0]);
//            Assertions.assertNull(foundHotel, "El hotel no debe existir en la base de datos");
//        }
//
//        @Test
//        @DisplayName("Con reservas asociadas")
//        void findHotelWithBookings() {
//            final long[] hotelCode = {0};
//            Set<Booking> reservations = new HashSet<>();
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 3, 0, 0, 1, 1);
//                    em.persist(h);
//                    hotelCode[0] = h.getCode();
//                    LocalDate date = LocalDate.of(2024, 3, 25);
//                    for (int i = 0; i < 10; ++i) {
//                        Booking b1 = new Booking(null, BookingService.MINIMUM_SINGLE_ROOM_NUMBER + i, 1, "Nombre Individual " + i, date.plusDays(i), date.plusDays(i+1), h);
//                        Booking b2 = new Booking(null, BookingService.MINIMUM_DOUBLE_ROOM_NUMBER + i, 2, "Nombre Doble " + i, date.plusDays(i), date.plusDays(i+1), h);
//                        em.persist(b1);
//                        em.persist(b2);
//                        reservations.add(b1);
//                        reservations.add(b2);
//                    }
//                }
//            }.run();
//            Hotel h = bs.findHotel(hotelCode[0]);
//            Assertions.assertNotNull(h, "El hotel encontrado no debe ser nulo");
//            Assertions.assertEquals(reservations.size(), h.getBookings().size(), "El número de reservas asociadas al hotel encontrado no coincide con el esperado");
//            for (Booking b : h.getBookings()) {
//                Assertions.assertTrue(reservations.contains(b), "La reserva encontrada no existe en el conjunto de reservas esperadas");
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("Pruebas de findAllHotels")
//    class FindAllHotelsTests {
//        @Test
//        @DisplayName("Normal")
//        void findAllHotelsNormal() {
//            Map<Long, Hotel> insertedHotels = new HashMap<>();
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    for (int i = 0; i < 50; ++i) {
//                        Hotel h = new Hotel(null, "Nombre " + i, "Calle tal y cual, " + i, i % 6, 0, 0, i, i);
//                        em.persist(h);
//                        insertedHotels.put(h.getCode(), h);
//                    }
//                }
//            }.run();
//            List<Hotel> foundHotels = bs.findAllHotels();
//            Assertions.assertEquals(insertedHotels.size(), foundHotels.size(), "El número de hoteles encontrados no coincide con el esperado");
//            for (Hotel foundHotel : foundHotels) {
//                Hotel insertedHotel = insertedHotels.get(foundHotel.getCode());
//                Assertions.assertNotNull(insertedHotel, "El hotel encontrado no existe en la base de datos");
//                Assertions.assertEquals(insertedHotel.getName(), foundHotel.getName(), "El nombre del hotel encontrado no coincide con el esperado");
//                Assertions.assertEquals(insertedHotel.getAddress(), foundHotel.getAddress(), "La dirección del hotel encontrado no coincide con la esperada");
//                Assertions.assertEquals(insertedHotel.getStars(), foundHotel.getStars(), "El número de estrellas del hotel encontrado no coincide con el esperado");
//                Assertions.assertEquals(insertedHotel.getLongitude(), foundHotel.getLongitude(), 0.0000001, "La longitud del hotel encontrado no coincide con la esperada");
//                Assertions.assertEquals(insertedHotel.getLatitude(), foundHotel.getLatitude(), 0.0000001, "La latitud del hotel encontrado no coincide con la esperada");
//                Assertions.assertEquals(insertedHotel.getDoubleRooms(), foundHotel.getDoubleRooms(), "El número de habitaciones dobles del hotel encontrado no coincide con el esperado");
//                Assertions.assertEquals(insertedHotel.getSingleRooms(), foundHotel.getSingleRooms(), "El número de habitaciones individuales del hotel encontrado no coincide con el esperado");
//            }
//        }
//
//        @Test
//        @DisplayName("Base de datos vacía")
//        void findAllHotelsEmpty() {
//            List<Hotel> foundHotels = bs.findAllHotels();
//            Assertions.assertTrue(foundHotels.isEmpty(), "La lista de hoteles encontrados debe estar vacía");
//        }
//    }
//
//    @Nested
//    @DisplayName("Pruebas de occupiedSingleRooms")
//    class OccupiedSingleRoomsTests {
//        @Test
//        @DisplayName("Código de hotel nulo")
//        void occupiedSingleRoomsNullHotelCode() {
//            try {
//                bs.occupiedSingleRooms(null, LocalDate.now());
//                Assertions.fail("No se lanzó BookingServiceException al pasar un código de hotel nulo");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Fecha nula")
//        void occupiedSingleRoomsNullDate() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.occupiedSingleRooms(existing[0].getCode(), null);
//                Assertions.fail("No se lanzó BookingServiceException al pasar una fecha nula");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Hotel inexistente")
//        void occupiedSingleRoomsNonExistingHotel() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.occupiedSingleRooms(existing[0].getCode() + 1, null);
//                Assertions.fail("No se lanzó BookingServiceException al pasar un código de hotel inexistente");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Funcionamiento normal")
//        void occupiedSingleRoomsNormal() {
//            final Hotel[] hotels = new Hotel[2];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel a = new Hotel(null, "Hotel A", "Calle tal y cual", 4, 0, 0, 5, 5);
//                    em.persist(a);
//                    hotels[0] = a;
//                    em.persist(new Booking(null, 100, 1, "Viajero 1", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 7), a));
//                    em.persist(new Booking(null, 101, 1, "Viajero 2", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 5), a));
//                    em.persist(new Booking(null, 101, 1, "Viajero 3", LocalDate.of(2024, 4, 9), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 102, 1, "Viajero 4", LocalDate.of(2024, 4, 3), LocalDate.of(2024, 4, 6), a));
//                    em.persist(new Booking(null, 103, 1, "Viajero 5", LocalDate.of(2024, 4, 7), LocalDate.of(2024, 4, 10), a));
//                    em.persist(new Booking(null, 104, 1, "Viajero 6", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 200, 1, "Viajero 7", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10), a));
//                    em.persist(new Booking(null, 201, 2, "Viajero 8", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 9), a));
//                    em.persist(new Booking(null, 202, 2, "Viajero 9", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 6), a));
//                    em.persist(new Booking(null, 203, 2, "Viajero 10", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 3), a));
//                    em.persist(new Booking(null, 203, 2, "Viajero 11", LocalDate.of(2024, 4, 7), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 204, 2, "Viajero 12", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 5), a));
//
//                    Hotel b = new Hotel(null, "Hotel B", "Calle Pascual", 2, 0, 0, 5, 4);
//                    em.persist(b);
//                    hotels[1] = b;
//                    em.persist(new Booking(null, 101, 1, "Viajero 13", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 9), b));
//                    em.persist(new Booking(null, 102, 1, "Viajero 14", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 11), b));
//                    em.persist(new Booking(null, 103, 1, "Viajero 15", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 7), b));
//                    em.persist(new Booking(null, 201, 2, "Viajero 16", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 6), b));
//                    em.persist(new Booking(null, 203, 2, "Viajero 17", LocalDate.of(2024, 4, 4), LocalDate.of(2024, 4, 7), b));
//                    em.persist(new Booking(null, 204, 2, "Viajero 18", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 9), b));
//                }
//            }.run();
//            try {
//                Assertions.assertEquals(0, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 3, 31)));
//                Assertions.assertEquals(1, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 1)));
//                Assertions.assertEquals(2, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 2)));
//                Assertions.assertEquals(3, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 3)));
//                Assertions.assertEquals(3, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 4)));
//                Assertions.assertEquals(2, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 5)));
//                Assertions.assertEquals(2, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 6)));
//                Assertions.assertEquals(2, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 7)));
//                Assertions.assertEquals(2, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 8)));
//                Assertions.assertEquals(3, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 9)));
//                Assertions.assertEquals(2, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 10)));
//                Assertions.assertEquals(0, bs.occupiedSingleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 11)));
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException sin motivo.");
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("Pruebas de occupiedDoubleRooms")
//    class OccupiedDoubleRoomsTests {
//        @Test
//        @DisplayName("Código de hotel nulo")
//        void occupiedDoubleRoomsNullHotelCode() {
//            try {
//                bs.occupiedDoubleRooms(null, LocalDate.now());
//                Assertions.fail("No se lanzó BookingServiceException al pasar un código de hotel nulo");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Fecha nula")
//        void occupiedDoubleRoomsNullDate() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.occupiedDoubleRooms(existing[0].getCode(), null);
//                Assertions.fail("No se lanzó BookingServiceException al pasar una fecha nula");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Hotel inexistente")
//        void occupiedDoubleRoomsNonExistingHotel() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.occupiedDoubleRooms(existing[0].getCode() + 1, null);
//                Assertions.fail("No se lanzó BookingServiceException al pasar un código de hotel inexistente");
//            } catch (BookingServiceException ignored) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Funcionamiento normal")
//        void occupiedDoubleRoomsNormal() {
//            final Hotel[] hotels = new Hotel[2];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel a = new Hotel(null, "Hotel A", "Calle tal y cual", 4, 0, 0, 5, 5);
//                    em.persist(a);
//                    hotels[0] = a;
//                    em.persist(new Booking(null, 100, 1, "Viajero 1", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 7), a));
//                    em.persist(new Booking(null, 101, 1, "Viajero 2", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 5), a));
//                    em.persist(new Booking(null, 101, 1, "Viajero 3", LocalDate.of(2024, 4, 9), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 102, 1, "Viajero 4", LocalDate.of(2024, 4, 3), LocalDate.of(2024, 4, 6), a));
//                    em.persist(new Booking(null, 103, 1, "Viajero 5", LocalDate.of(2024, 4, 7), LocalDate.of(2024, 4, 10), a));
//                    em.persist(new Booking(null, 104, 1, "Viajero 6", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 200, 1, "Viajero 7", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10), a));
//                    em.persist(new Booking(null, 201, 2, "Viajero 8", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 9), a));
//                    em.persist(new Booking(null, 202, 2, "Viajero 9", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 6), a));
//                    em.persist(new Booking(null, 203, 2, "Viajero 10", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 3), a));
//                    em.persist(new Booking(null, 203, 2, "Viajero 11", LocalDate.of(2024, 4, 7), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 204, 2, "Viajero 12", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 5), a));
//
//                    Hotel b = new Hotel(null, "Hotel B", "Calle Pascual", 2, 0, 0, 5, 4);
//                    em.persist(b);
//                    hotels[1] = b;
//                    em.persist(new Booking(null, 101, 1, "Viajero 13", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 9), b));
//                    em.persist(new Booking(null, 102, 1, "Viajero 14", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 11), b));
//                    em.persist(new Booking(null, 103, 1, "Viajero 15", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 7), b));
//                    em.persist(new Booking(null, 201, 2, "Viajero 16", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 6), b));
//                    em.persist(new Booking(null, 203, 2, "Viajero 17", LocalDate.of(2024, 4, 4), LocalDate.of(2024, 4, 7), b));
//                    em.persist(new Booking(null, 204, 2, "Viajero 18", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 9), b));
//                }
//            }.run();
//            try {
//                Assertions.assertEquals(0, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 3, 31)));
//                Assertions.assertEquals(2, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 1)));
//                Assertions.assertEquals(3, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 2)));
//                Assertions.assertEquals(2, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 3)));
//                Assertions.assertEquals(2, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 4)));
//                Assertions.assertEquals(2, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 5)));
//                Assertions.assertEquals(2, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 6)));
//                Assertions.assertEquals(3, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 7)));
//                Assertions.assertEquals(3, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 8)));
//                Assertions.assertEquals(2, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 9)));
//                Assertions.assertEquals(1, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 10)));
//                Assertions.assertEquals(0, bs.occupiedDoubleRooms(hotels[0].getCode(), LocalDate.of(2024, 4, 11)));
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException sin motivo.");
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("Pruebas de makeBooking")
//    class MakeBookingTests {
//        @Test
//        @DisplayName("Número de personas erróneo")
//        void makeBookingWrongNumberOfPeople() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.makeBooking(existing[0].getCode(), 0, "Viajero", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva para 0 personas");
//            } catch (BookingServiceException e) { /* nada */ }
//            try {
//                bs.makeBooking(existing[0].getCode(), -1, "Viajero", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva para -1 personas");
//            } catch (BookingServiceException e) { /* nada */ }
//            try {
//                bs.makeBooking(existing[0].getCode(), 3, "Viajero", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva para 3 personas");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Nombre de viajero nulo")
//        void makeBookingNullName() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.makeBooking(existing[0].getCode(), 1, null, LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva con nombre de viajero nulo");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Nombre de viajero vacío")
//        void makeBookingEmptyName() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.makeBooking(existing[0].getCode(), 2, "", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva con nombre de viajero vacío");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Fecha de entrada nula")
//        void makeBookingNullArrivalDate() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.makeBooking(existing[0].getCode(), 2, "Viajero", null, LocalDate.of(2024, 4, 2), LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva con fecha de entrada nula");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Fecha de salida nula")
//        void makeBookingNullDepartureDate() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.makeBooking(existing[0].getCode(), 2, "Viajero", LocalDate.of(2024, 4, 1), null, LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva con fecha de salida nula");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Fecha de operación nula")
//        void makeBookingNullOperationDate() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                bs.makeBooking(existing[0].getCode(), 2, "Viajero", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), null);
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva con fecha de operación nula");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Reserva demasiado larga")
//        void makeBookingTooLongStay() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                LocalDate arrivalDate = LocalDate.of(2024, 4, 1);
//                LocalDate departureDate = arrivalDate.plusDays(BookingService.MAXIMUM_STAY_LENGTH + 1);
//                bs.makeBooking(existing[0].getCode(), 2, "Viajero", arrivalDate, departureDate, arrivalDate.minusDays(1));
//                Assertions.fail("No se lanzó BookingServiceException ante una reserva con fecha de operación nula");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Reserva individual normal en un hotel vacío")
//        void makeBookingSingleRoomEmptyHotel() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                int numberOfPeople = 1;
//                String travellerName = "Viajero";
//                LocalDate arrivalDate = LocalDate.of(2024, 4, 1);
//                LocalDate departureDate = arrivalDate.plusDays(1);
//                Booking b = bs.makeBooking(existing[0].getCode(), numberOfPeople, travellerName, arrivalDate, departureDate, arrivalDate.minusDays(1));
//                Assertions.assertNotNull(b);
//                Assertions.assertNotNull(b.getCode());
//                Assertions.assertNotNull(b.getHotel());
//                Assertions.assertEquals(existing[0].getCode(), b.getHotel().getCode(), "El código del hotel no coincide con el esperado.");
//                Assertions.assertEquals(arrivalDate, b.getArrivalDate(), "La fecha de entrada no coincide con la esperada.");
//                Assertions.assertEquals(departureDate, b.getDepartureDate(), "La fecha de entrada no coincide con la esperada.");
//                Assertions.assertEquals(numberOfPeople, b.getNumberOfPeople());
//                Assertions.assertEquals(travellerName, b.getTravellerName());
//                Assertions.assertEquals(BookingService.MINIMUM_SINGLE_ROOM_NUMBER, b.getRoomNumber(), "El número de habitación no es el esperado.");
//            } catch (BookingServiceException e) {
//                Assertions.fail("Se lanzó BookingServiceException sin motivo.");
//            }
//        }
//
//        @Test
//        @DisplayName("Reserva doble normal en un hotel vacío")
//        void makeBookingDoubleRoomEmptyHotel() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                int numberOfPeople = 2;
//                String travellerName = "Viajero";
//                LocalDate arrivalDate = LocalDate.of(2024, 4, 1);
//                LocalDate departureDate = arrivalDate.plusDays(1);
//                Booking b = bs.makeBooking(existing[0].getCode(), numberOfPeople, travellerName, arrivalDate, departureDate, arrivalDate.minusDays(1));
//                Assertions.assertNotNull(b);
//                Assertions.assertNotNull(b.getCode());
//                Assertions.assertNotNull(b.getHotel());
//                Assertions.assertEquals(existing[0].getCode(), b.getHotel().getCode(), "El código del hotel no coincide con el esperado.");
//                Assertions.assertEquals(arrivalDate, b.getArrivalDate(), "La fecha de entrada no coincide con la esperada.");
//                Assertions.assertEquals(departureDate, b.getDepartureDate(), "La fecha de entrada no coincide con la esperada.");
//                Assertions.assertEquals(numberOfPeople, b.getNumberOfPeople());
//                Assertions.assertEquals(travellerName, b.getTravellerName());
//                Assertions.assertEquals(BookingService.MINIMUM_DOUBLE_ROOM_NUMBER, b.getRoomNumber(), "El número de habitación no es el esperado.");
//            } catch (BookingServiceException e) {
//                Assertions.fail("Se lanzó BookingServiceException sin motivo.");
//            }
//        }
//
//        @Test
//        @DisplayName("Reserva individual normal en un hotel sin habitaciones individuales libres")
//        void makeBookingSingleRoomNoSingleRoomFree() {
//            final Hotel[] existing = new Hotel[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    existing[0] = h;
//                }
//            }.run();
//            try {
//                int numberOfPeople = 1;
//                String travellerName = "Viajero";
//                LocalDate arrivalDate = LocalDate.of(2024, 4, 1);
//                LocalDate departureDate = arrivalDate.plusDays(1);
//                // Esta reserva ocupará la habitación individual
//                bs.makeBooking(existing[0].getCode(), numberOfPeople, travellerName + "1", arrivalDate, departureDate, arrivalDate.minusDays(1));
//                // Esta tendrá que ir a una doble
//                Booking b = bs.makeBooking(existing[0].getCode(), numberOfPeople, travellerName, arrivalDate, departureDate, arrivalDate.minusDays(1));
//                Assertions.assertNotNull(b);
//                Assertions.assertNotNull(b.getCode());
//                Assertions.assertNotNull(b.getHotel());
//                Assertions.assertEquals(existing[0].getCode(), b.getHotel().getCode(), "El código del hotel no coincide con el esperado.");
//                Assertions.assertEquals(arrivalDate, b.getArrivalDate(), "La fecha de entrada no coincide con la esperada.");
//                Assertions.assertEquals(departureDate, b.getDepartureDate(), "La fecha de entrada no coincide con la esperada.");
//                Assertions.assertEquals(numberOfPeople, b.getNumberOfPeople());
//                Assertions.assertEquals(travellerName, b.getTravellerName());
//                Assertions.assertEquals(BookingService.MINIMUM_DOUBLE_ROOM_NUMBER, b.getRoomNumber(), "El número de habitación no es el esperado.");
//            } catch (BookingServiceException e) {
//                Assertions.fail("Se lanzó BookingServiceException sin motivo.");
//            }
//        }
//
//        @Test
//        @DisplayName("Combinaciones de reservas")
//        void makeBookingNormal() {
//            final Hotel[] hotels = new Hotel[2];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel a = new Hotel(null, "Hotel A", "Calle tal y cual", 4, 0, 0, 5, 5);
//                    em.persist(a);
//                    hotels[0] = a;
//                    em.persist(new Booking(null, 100, 1, "Viajero 1", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 7), a));
//                    em.persist(new Booking(null, 101, 1, "Viajero 2", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 5), a));
//                    em.persist(new Booking(null, 101, 1, "Viajero 3", LocalDate.of(2024, 4, 9), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 102, 1, "Viajero 4", LocalDate.of(2024, 4, 3), LocalDate.of(2024, 4, 6), a));
//                    em.persist(new Booking(null, 103, 1, "Viajero 5", LocalDate.of(2024, 4, 7), LocalDate.of(2024, 4, 10), a));
//                    em.persist(new Booking(null, 104, 1, "Viajero 6", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 200, 1, "Viajero 7", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 10), a));
//                    em.persist(new Booking(null, 201, 2, "Viajero 8", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 9), a));
//                    em.persist(new Booking(null, 202, 2, "Viajero 9", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 6), a));
//                    em.persist(new Booking(null, 203, 2, "Viajero 10", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 3), a));
//                    em.persist(new Booking(null, 203, 2, "Viajero 11", LocalDate.of(2024, 4, 7), LocalDate.of(2024, 4, 11), a));
//                    em.persist(new Booking(null, 204, 2, "Viajero 12", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 5), a));
//
//                    Hotel b = new Hotel(null, "Hotel B", "Calle Pascual", 2, 0, 0, 5, 4);
//                    em.persist(b);
//                    hotels[1] = b;
//                    em.persist(new Booking(null, 101, 1, "Viajero 13", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 9), b));
//                    em.persist(new Booking(null, 102, 1, "Viajero 14", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 11), b));
//                    em.persist(new Booking(null, 103, 1, "Viajero 15", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 7), b));
//                    em.persist(new Booking(null, 201, 2, "Viajero 16", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 6), b));
//                    em.persist(new Booking(null, 203, 2, "Viajero 17", LocalDate.of(2024, 4, 4), LocalDate.of(2024, 4, 7), b));
//                    em.persist(new Booking(null, 204, 2, "Viajero 18", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 9), b));
//                }
//            }.run();
//            try {
//                Booking d1 = bs.makeBooking(hotels[0].getCode(), 2, "Double 1", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 10), LocalDate.of(2024, 3, 31));
//                Assertions.assertEquals(204, d1.getRoomNumber());
//                Booking d2 = bs.makeBooking(hotels[0].getCode(), 2, "Double 2", LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 6), LocalDate.of(2024, 3, 31));
//                Assertions.assertEquals(201, d2.getRoomNumber());
//                Booking s1 = bs.makeBooking(hotels[0].getCode(), 1, "Single 1", LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 8), LocalDate.of(2024, 3, 31));
//                Assertions.assertEquals(101, s1.getRoomNumber());
//                Booking s2 = bs.makeBooking(hotels[0].getCode(), 1, "Single 2", LocalDate.of(2024, 4, 6), LocalDate.of(2024, 4, 9), LocalDate.of(2024, 3, 31));
//                Assertions.assertEquals(102, s2.getRoomNumber());
//                Booking s3 = bs.makeBooking(hotels[0].getCode(), 1, "Single 3", LocalDate.of(2024, 4, 3), LocalDate.of(2024, 4, 7), LocalDate.of(2024, 3, 31));
//                Assertions.assertEquals(103, s3.getRoomNumber());
//                Booking s4 = bs.makeBooking(hotels[0].getCode(), 1, "Single 4", LocalDate.of(2024, 4, 3), LocalDate.of(2024, 4, 7), LocalDate.of(2024, 3, 31));
//               System.out.print(s4);
//               Assertions.assertEquals(203, s4.getRoomNumber());
//            } catch (BookingServiceException ignored) {
//                Assertions.fail("Se lanzó BookingServiceException sin motivo.");
//            }
//        }
//    }
//
//    @Nested
//    @DisplayName("Pruebas de cancelBooking")
//    class CancelBookingTests {
//        @Test
//        @DisplayName("Código nulo")
//        void cancelBookingNullCode() {
//            try {
//                bs.cancelBooking(null, LocalDate.of(2024, 3, 31));
//                Assertions.fail("No se lanzó BookingServiceException con una reserva de código nulo");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Fecha nula")
//        void cancelBookingNullDate() {
//            final Booking[] bk = new Booking[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    bk[0] = new Booking(null, BookingService.MINIMUM_SINGLE_ROOM_NUMBER, 1, "Name", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), h);
//                    em.persist(bk[0]);
//                }
//            }.run();
//            try {
//                bs.cancelBooking(bk[0].getCode(), null);
//                Assertions.fail("No se lanzó BookingServiceException al intentar operar en una fecha nula");
//            } catch (BookingServiceException e) { /* nada */ }
//        }
//
//        @Test
//        @DisplayName("Fecha de operación posterior a la entrada")
//        void cancelBookingPastDate() {
//            final Booking[] bk = new Booking[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    bk[0] = new Booking(null, BookingService.MINIMUM_SINGLE_ROOM_NUMBER, 1, "Name", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), h);
//                    em.persist(bk[0]);
//                }
//            }.run();
//            try {
//                bs.cancelBooking(bk[0].getCode(), bk[0].getArrivalDate().plusDays(1));
//                Assertions.fail("No se lanzó BookingServiceException al intentar cancelar una reserva pasada");
//            } catch (BookingServiceException e) {
//                // IMPORTANTE: Para que esta prueba tenga sentido es necesario poder obtener la lista actualizada de
//                // reservas del hotel, para lo que findHotel tiene que funcionar correctamente
//                Hotel h = bs.findHotel(bk[0].getHotel().getCode());
//                Assertions.assertTrue(h.getBookings().contains(bk[0]), "Se ha eliminado la reserva de la base de datos indebidamente");
//            }
//        }
//
//        @Test
//        @DisplayName("Fecha de operación en el día de entrada")
//        void cancelBookingSameDate() {
//            final Booking[] bk = new Booking[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    bk[0] = new Booking(null, BookingService.MINIMUM_SINGLE_ROOM_NUMBER, 1, "Name", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), h);
//                    em.persist(bk[0]);
//                }
//            }.run();
//            try {
//                bs.cancelBooking(bk[0].getCode(), bk[0].getArrivalDate());
//                Assertions.fail("No se lanzó BookingServiceException al intentar cancelar una reserva en la fecha de entrada");
//            } catch (BookingServiceException e) {
//                // IMPORTANTE: Para que esta prueba tenga sentido es necesario poder obtener la lista actualizada de
//                // reservas del hotel, para lo que findHotel tiene que funcionar correctamente
//                Hotel h = bs.findHotel(bk[0].getHotel().getCode());
//                Assertions.assertTrue(h.getBookings().contains(bk[0]), "Se ha eliminado la reserva de la base de datos indebidamente");
//            }
//        }
//
//        @Test
//        @DisplayName("Operación normal")
//        void cancelBookingNormal() {
//            final Booking[] bk = new Booking[1];
//            new PrepareData() {
//                @Override
//                void thingsToDo(EntityManager em) {
//                    Hotel h = new Hotel(null, "Nombre", "Calle tal y cual", 4, 0, 0, 1, 1);
//                    em.persist(h);
//                    bk[0] = new Booking(null, BookingService.MINIMUM_SINGLE_ROOM_NUMBER, 1, "Name", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), h);
//                    em.persist(bk[0]);
//                }
//            }.run();
//            try {
//                bs.cancelBooking(bk[0].getCode(), bk[0].getArrivalDate().minusDays(1));
//                // IMPORTANTE: Para que esta prueba tenga sentido es necesario poder obtener la lista actualizada de
//                // reservas del hotel, para lo que findHotel tiene que funcionar correctamente
//                Hotel h = bs.findHotel(bk[0].getHotel().getCode());
//                Assertions.assertFalse(h.getBookings().contains(bk[0]), "No se ha eliminado la reserva de la base de datos");
//            } catch (BookingServiceException e) {
//                Assertions.fail("Se lanzó BookingServiceException sin motivo");
//            }
//        }
//    }
//}
