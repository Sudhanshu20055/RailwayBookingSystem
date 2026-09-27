import java.util.*;

/*
 * =====================================================================
 *  RAILWAY TICKET BOOKING SYSTEM
 *  Console (Command Prompt) based project using core Java OOP concepts:
 *   - Abstraction   : abstract class Person
 *   - Inheritance   : User, PassengerInfo extend Person
 *   - Encapsulation : private fields + public getters/setters
 *   - Polymorphism  : displayInfo() overridden in subclasses
 *
 *  Run:
 *      javac RailwayBookingSystem.java
 *      java RailwayBookingSystem
 * =====================================================================
 */
public class RailwayBookingSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        RailwayService service = new RailwayService();
        MainMenu menu = new MainMenu(sc, service);
        menu.start();
        sc.close();
    }
}

/* ======================== ABSTRACTION / BASE CLASS ======================== */
abstract class Person {
    protected String name;
    protected int age;
    protected char gender;

    public Person(String name, int age, char gender) {
        this.name = name;
        this.age = age;
        this.gender = gender;
    }

    public String getName() { return name; }
    public int getAge() { return age; }
    public char getGender() { return gender; }

    public abstract void displayInfo();
}

/* ======================== USER (registered account) ======================== */
class User extends Person {
    private String userId;
    private String mobileNumber;   // exactly 10 digits - validated
    private String password;       // validated strong password

    public User(String name, int age, char gender, String mobileNumber, String password) {
        super(name, age, gender);
        this.mobileNumber = mobileNumber;
        this.password = password;
        this.userId = "USR" + mobileNumber.substring(mobileNumber.length() - 4)
                + (int) (Math.random() * 90 + 10);
    }

    public String getUserId() { return userId; }
    public String getMobileNumber() { return mobileNumber; }
    public boolean checkPassword(String pwd) { return password.equals(pwd); }

    @Override
    public void displayInfo() {
        System.out.println("User ID   : " + userId);
        System.out.println("Name      : " + name);
        System.out.println("Age       : " + age);
        System.out.println("Gender    : " + gender);
        System.out.println("Mobile No : " + mobileNumber);
    }
}

/* ======================== PASSENGER (for a booking) ======================== */
class PassengerInfo extends Person {
    private String seatNumber;

    public PassengerInfo(String name, int age, char gender) {
        super(name, age, gender);
        this.seatNumber = "NA";
    }

    public void setSeatNumber(String s) { this.seatNumber = s; }
    public String getSeatNumber() { return seatNumber; }

    @Override
    public void displayInfo() {
        System.out.println("   Name: " + name + " | Age: " + age +
                " | Gender: " + gender + " | Seat: " + seatNumber);
    }
}

/* ================================ TRAIN ================================ */
class Train {
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private String departureTime;
    private String arrivalTime;
    private int totalSeats;
    private int availableSeats;
    private double farePerSeat;

    public Train(String trainNumber, String trainName, String source, String destination,
                 String departureTime, String arrivalTime, int totalSeats, double farePerSeat) {
        this.trainNumber = trainNumber;
        this.trainName = trainName;
        this.source = source;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.farePerSeat = farePerSeat;
    }

    public String getTrainNumber() { return trainNumber; }
    public String getTrainName() { return trainName; }
    public String getSource() { return source; }
    public String getDestination() { return destination; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public double getFarePerSeat() { return farePerSeat; }

    public boolean bookSeats(int count) {
        if (count <= availableSeats) {
            availableSeats -= count;
            return true;
        }
        return false;
    }

    public void cancelSeats(int count) {
        availableSeats += count;
        if (availableSeats > totalSeats) availableSeats = totalSeats;
    }

    public void displayTrain() {
        System.out.println("-----------------------------------------------------");
        System.out.printf("Train No: %-8s Name: %-25s%n", trainNumber, trainName);
        System.out.printf("From: %-12s To: %-12s%n", source, destination);
        System.out.printf("Dep: %-8s Arr: %-8s%n", departureTime, arrivalTime);
        System.out.printf("Fare/Seat: Rs.%-8.2f Available Seats: %d/%d%n",
                farePerSeat, availableSeats, totalSeats);
        System.out.println("-----------------------------------------------------");
    }
}

/* ================================ TICKET ================================ */
class Ticket {
    private String pnr;
    private Train train;
    private String bookedByMobile;
    private List<PassengerInfo> passengers;
    private String journeyDate;
    private double totalFare;
    private String status; // CONFIRMED / CANCELLED

    public Ticket(String pnr, Train train, String bookedByMobile, List<PassengerInfo> passengers,
                   String journeyDate, double totalFare) {
        this.pnr = pnr;
        this.train = train;
        this.bookedByMobile = bookedByMobile;
        this.passengers = passengers;
        this.journeyDate = journeyDate;
        this.totalFare = totalFare;
        this.status = "CONFIRMED";
    }

    public String getPnr() { return pnr; }
    public Train getTrain() { return train; }
    public String getBookedByMobile() { return bookedByMobile; }
    public List<PassengerInfo> getPassengers() { return passengers; }
    public double getTotalFare() { return totalFare; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void displayTicket() {
        System.out.println("=====================================================");
        System.out.println("PNR No       : " + pnr);
        System.out.println("Status       : " + status);
        System.out.println("Train        : " + train.getTrainNumber() + " - " + train.getTrainName());
        System.out.println("Route        : " + train.getSource() + " -> " + train.getDestination());
        System.out.println("Journey Date : " + journeyDate);
        System.out.println("Passengers   :");
        for (PassengerInfo p : passengers) p.displayInfo();
        System.out.printf("Total Fare   : Rs.%.2f%n", totalFare);
        System.out.println("=====================================================");
    }
}

/* ================================ VALIDATOR ================================ */
class Validator {
    // Exactly 10 digits, must start with 6-9 (standard Indian mobile format)
    private static final String MOBILE_REGEX = "^[6-9][0-9]{9}$";
    // 8-15 chars, at least 1 lowercase, 1 uppercase, 1 digit, 1 special char
    private static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!_-]).{8,15}$";
    private static final String NAME_REGEX = "^[a-zA-Z ]{3,30}$";
    private static final String DATE_REGEX = "^\\d{2}-\\d{2}-\\d{4}$";
    private static final String PNR_REGEX = "^\\d{10}$";

    public static boolean isValidMobile(String mobile) {
        if (mobile == null) return false;
        if (!mobile.matches("\\d+")) return false; // must be all digits
        if (mobile.length() != 10) return false;   // NOT more, NOT less than 10 digits
        return mobile.matches(MOBILE_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }

    public static boolean isValidName(String name) {
        return name != null && name.matches(NAME_REGEX);
    }

    public static boolean isValidAge(int age) {
        return age > 0 && age <= 120;
    }

    public static boolean isValidGender(char g) {
        char up = Character.toUpperCase(g);
        return up == 'M' || up == 'F' || up == 'O';
    }

    public static boolean isValidDate(String date) {
        return date != null && date.matches(DATE_REGEX);
    }

    public static boolean isValidPNR(String pnr) {
        return pnr != null && pnr.matches(PNR_REGEX);
    }
}

/* ================================ SERVICE / CORE LOGIC ================================ */
class RailwayService {
    private Map<String, User> users = new HashMap<>();     // key = mobile number
    private List<Train> trains = new ArrayList<>();
    private Map<String, Ticket> tickets = new HashMap<>();  // key = PNR
    private Random rand = new Random();

    public RailwayService() {
        loadSampleTrains();
    }

    private void loadSampleTrains() {
        trains.add(new Train("12301", "Howrah Rajdhani", "Delhi", "Howrah", "16:55", "09:55", 50, 1500.0));
        trains.add(new Train("12951", "Mumbai Rajdhani", "Delhi", "Mumbai", "16:25", "08:15", 50, 1650.0));
        trains.add(new Train("12009", "Shatabdi Express", "Delhi", "Chandigarh", "07:20", "10:45", 60, 750.0));
        trains.add(new Train("12622", "Tamil Nadu Express", "Delhi", "Chennai", "22:30", "07:15", 45, 1800.0));
        trains.add(new Train("12432", "Trivandrum Rajdhani", "Delhi", "Trivandrum", "11:25", "05:15", 40, 2100.0));
    }

    public boolean isMobileRegistered(String mobile) { return users.containsKey(mobile); }

    public String registerUser(String name, int age, char gender, String mobile, String password) {
        User u = new User(name, age, gender, mobile, password);
        users.put(mobile, u);
        return u.getUserId();
    }

    public User login(String mobile, String password) {
        User u = users.get(mobile);
        if (u != null && u.checkPassword(password)) return u;
        return null;
    }

    public List<Train> searchTrains(String source, String destination) {
        List<Train> result = new ArrayList<>();
        for (Train t : trains) {
            if (t.getSource().equalsIgnoreCase(source) && t.getDestination().equalsIgnoreCase(destination)) {
                result.add(t);
            }
        }
        return result;
    }

    public Train getTrainByNumber(String number) {
        for (Train t : trains) if (t.getTrainNumber().equals(number)) return t;
        return null;
    }

    private String generatePNR() {
        String pnr;
        do {
            long num = 1000000000L + (long) (rand.nextDouble() * 9000000000L);
            pnr = String.valueOf(num);
        } while (tickets.containsKey(pnr));
        return pnr;
    }

    public Ticket bookTicket(User user, Train train, String journeyDate, List<PassengerInfo> passengers) {
        int count = passengers.size();
        if (!train.bookSeats(count)) return null;
        double fare = train.getFarePerSeat() * count;
        String pnr = generatePNR();
        Ticket ticket = new Ticket(pnr, train, user.getMobileNumber(), passengers, journeyDate, fare);
        tickets.put(pnr, ticket);
        return ticket;
    }

    public String cancelTicket(String pnr, String mobile) {
        Ticket t = tickets.get(pnr);
        if (t == null) return "No ticket found with this PNR.";
        if (!t.getBookedByMobile().equals(mobile)) return "This ticket does not belong to you.";
        if (t.getStatus().equals("CANCELLED")) return "Ticket is already cancelled.";
        t.setStatus("CANCELLED");
        t.getTrain().cancelSeats(t.getPassengers().size());
        return String.format("Ticket cancelled successfully. Refund amount: Rs.%.2f", t.getTotalFare());
    }

    public Ticket getTicket(String pnr) { return tickets.get(pnr); }

    public List<Ticket> getUserTickets(String mobile) {
        List<Ticket> list = new ArrayList<>();
        for (Ticket t : tickets.values()) if (t.getBookedByMobile().equals(mobile)) list.add(t);
        return list;
    }
}

/* ================================ CONSOLE MENU ================================ */
class MainMenu {
    private Scanner sc;
    private RailwayService service;
    private User currentUser;

    public MainMenu(Scanner sc, RailwayService service) {
        this.sc = sc;
        this.service = service;
    }

    public void start() {
        System.out.println("=================================================");
        System.out.println("   WELCOME TO RAILWAY TICKET BOOKING SYSTEM");
        System.out.println("=================================================");
        boolean exit = false;
        while (!exit) {
            if (currentUser == null) {
                exit = showGuestMenu();
            } else {
                showUserMenu();
            }
        }
        System.out.println("Thank you for using Railway Ticket Booking System. Safe journey!");
    }

    private boolean showGuestMenu() {
        System.out.println("\n---------- MAIN MENU ----------");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Exit");
        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1: registerFlow(); break;
            case 2: loginFlow(); break;
            case 3: return true;
            default: System.out.println("Invalid choice. Try again.");
        }
        return false;
    }

    private void registerFlow() {
        System.out.println("\n-- New User Registration --");

        String name;
        while (true) {
            System.out.print("Enter Full Name: ");
            name = sc.nextLine().trim();
            if (Validator.isValidName(name)) break;
            System.out.println("Invalid name! Use only letters/spaces (3-30 characters).");
        }

        int age;
        while (true) {
            age = readInt("Enter Age: ");
            if (Validator.isValidAge(age)) break;
            System.out.println("Invalid age! Enter a value between 1 and 120.");
        }

        char gender;
        while (true) {
            System.out.print("Enter Gender (M/F/O): ");
            String g = sc.nextLine().trim();
            if (g.length() == 1 && Validator.isValidGender(g.charAt(0))) {
                gender = Character.toUpperCase(g.charAt(0));
                break;
            }
            System.out.println("Invalid gender! Enter M, F or O.");
        }

        String mobile;
        while (true) {
            System.out.print("Enter Mobile Number (exactly 10 digits): ");
            mobile = sc.nextLine().trim();
            if (mobile.length() > 10) {
                System.out.println("Invalid! Mobile number cannot be more than 10 digits.");
                continue;
            }
            if (mobile.length() < 10) {
                System.out.println("Invalid! Mobile number cannot be less than 10 digits.");
                continue;
            }
            if (!Validator.isValidMobile(mobile)) {
                System.out.println("Invalid mobile number! Must contain only digits and start with 6-9.");
                continue;
            }
            if (service.isMobileRegistered(mobile)) {
                System.out.println("This mobile number is already registered. Please login instead.");
                continue;
            }
            break;
        }

        String password;
        while (true) {
            System.out.print("Enter Password (8-15 chars, must include uppercase, lowercase,\n" +
                    "digit and special character e.g. @#$%): ");
            password = sc.nextLine();
            if (Validator.isValidPassword(password)) break;
            System.out.println("Weak password! Please follow the rules shown above.");
        }

        System.out.print("Confirm Password: ");
        String confirm = sc.nextLine();
        if (!password.equals(confirm)) {
            System.out.println("Passwords do not match. Registration cancelled.");
            return;
        }

        String uid = service.registerUser(name, age, gender, mobile, password);
        System.out.println("Registration successful! Your User ID: " + uid);
        System.out.println("You can now login using your mobile number and password.");
    }

    private void loginFlow() {
        System.out.println("\n-- User Login --");
        System.out.print("Enter Mobile Number: ");
        String mobile = sc.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();

        User u = service.login(mobile, password);
        if (u == null) {
            System.out.println("Invalid mobile number or password.");
        } else {
            currentUser = u;
            System.out.println("Login successful! Welcome, " + u.getName());
        }
    }

    private void showUserMenu() {
        System.out.println("\n---------- USER MENU (" + currentUser.getName() + ") ----------");
        System.out.println("1. Search Trains");
        System.out.println("2. Book Ticket");
        System.out.println("3. Cancel Ticket");
        System.out.println("4. View My Bookings");
        System.out.println("5. View Ticket by PNR");
        System.out.println("6. Logout");
        int choice = readInt("Enter choice: ");
        switch (choice) {
            case 1: searchTrainFlow(); break;
            case 2: bookTicketFlow(); break;
            case 3: cancelTicketFlow(); break;
            case 4: viewBookingsFlow(); break;
            case 5: viewTicketByPNRFlow(); break;
            case 6:
                currentUser = null;
                System.out.println("Logged out successfully.");
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private void searchTrainFlow() {
        System.out.print("\nEnter Source Station: ");
        String src = sc.nextLine().trim();
        System.out.print("Enter Destination Station: ");
        String dest = sc.nextLine().trim();

        List<Train> results = service.searchTrains(src, dest);
        if (results.isEmpty()) {
            System.out.println("No trains found between " + src + " and " + dest + ".");
            return;
        }
        System.out.println("\nAvailable Trains:");
        for (Train t : results) t.displayTrain();
    }

    private void bookTicketFlow() {
        System.out.print("\nEnter Train Number to book (see Search Trains for options): ");
        String tno = sc.nextLine().trim();
        Train train = service.getTrainByNumber(tno);
        if (train == null) {
            System.out.println("Train not found.");
            return;
        }
        train.displayTrain();
        if (train.getAvailableSeats() <= 0) {
            System.out.println("Sorry, no seats available on this train.");
            return;
        }

        String date;
        while (true) {
            System.out.print("Enter Journey Date (dd-mm-yyyy): ");
            date = sc.nextLine().trim();
            if (Validator.isValidDate(date)) break;
            System.out.println("Invalid date format! Use dd-mm-yyyy.");
        }

        int numPassengers;
        while (true) {
            numPassengers = readInt("Enter number of passengers: ");
            if (numPassengers <= 0) {
                System.out.println("Number of passengers must be at least 1.");
                continue;
            }
            if (numPassengers > train.getAvailableSeats()) {
                System.out.println("Only " + train.getAvailableSeats() + " seats available. Enter a smaller number.");
                continue;
            }
            break;
        }

        List<PassengerInfo> passengers = new ArrayList<>();
        for (int i = 1; i <= numPassengers; i++) {
            System.out.println("-- Passenger " + i + " Details --");
            String pname;
            while (true) {
                System.out.print("Name: ");
                pname = sc.nextLine().trim();
                if (Validator.isValidName(pname)) break;
                System.out.println("Invalid name! Use only letters/spaces (3-30 characters).");
            }
            int page;
            while (true) {
                page = readInt("Age: ");
                if (Validator.isValidAge(page)) break;
                System.out.println("Invalid age! Enter a value between 1 and 120.");
            }
            char pgender;
            while (true) {
                System.out.print("Gender (M/F/O): ");
                String g = sc.nextLine().trim();
                if (g.length() == 1 && Validator.isValidGender(g.charAt(0))) {
                    pgender = Character.toUpperCase(g.charAt(0));
                    break;
                }
                System.out.println("Invalid gender! Enter M, F or O.");
            }
            PassengerInfo p = new PassengerInfo(pname, page, pgender);
            p.setSeatNumber(train.getTrainNumber() + "-S" + (train.getTotalSeats() - train.getAvailableSeats() + i));
            passengers.add(p);
        }

        Ticket ticket = service.bookTicket(currentUser, train, date, passengers);
        if (ticket == null) {
            System.out.println("Booking failed. Seats not available.");
            return;
        }
        System.out.println("\nBooking Successful! Here is your ticket:");
        ticket.displayTicket();
    }

    private void cancelTicketFlow() {
        System.out.print("\nEnter PNR Number to cancel (10 digits): ");
        String pnr = sc.nextLine().trim();
        if (!Validator.isValidPNR(pnr)) {
            System.out.println("Invalid PNR format! PNR must be exactly 10 digits.");
            return;
        }
        String result = service.cancelTicket(pnr, currentUser.getMobileNumber());
        System.out.println(result);
    }

    private void viewBookingsFlow() {
        List<Ticket> myTickets = service.getUserTickets(currentUser.getMobileNumber());
        if (myTickets.isEmpty()) {
            System.out.println("\nNo bookings found.");
            return;
        }
        System.out.println("\nYour Bookings:");
        for (Ticket t : myTickets) {
            t.displayTicket();
        }
    }

    private void viewTicketByPNRFlow() {
        System.out.print("\nEnter PNR Number (10 digits): ");
        String pnr = sc.nextLine().trim();
        if (!Validator.isValidPNR(pnr)) {
            System.out.println("Invalid PNR format! PNR must be exactly 10 digits.");
            return;
        }
        Ticket t = service.getTicket(pnr);
        if (t == null) {
            System.out.println("No ticket found with this PNR.");
            return;
        }
        if (!t.getBookedByMobile().equals(currentUser.getMobileNumber())) {
            System.out.println("This ticket does not belong to you.");
            return;
        }
        t.displayTicket();
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
