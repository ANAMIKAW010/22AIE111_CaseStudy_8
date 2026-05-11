import java.util.*;
import java.time.*;

public class RailwaySystem {

    // Train class
    static class Train {
        int no; String route; double fare; int seats = 50; LocalDateTime departure;

        Train(int no, String route, double fare, LocalDateTime dep) {
            this.no = no; this.route = route; this.fare = fare; this.departure = dep;
        }
        boolean bookSeat()  { if (seats > 0) { seats--; return true; } return false; }
        void releaseSeat()  { seats++; }
        public String toString() {
            return "Train " + no + " | " + route + " | Rs." + fare + " | Seats: " + seats + " | Dep: " + departure;
        }
    }

    // Booking class
    static class Booking {
        String pnr, passenger, status = "Confirmed"; Train train; double paid;

        Booking(String pnr, String passenger, Train train) {
            this.pnr = pnr; this.passenger = passenger; this.train = train; this.paid = train.fare;
        }
        public String toString() {
            return "PNR: " + pnr + " | " + passenger + " | " + status + " | Rs." + paid;
        }
    }

    // Global data
    static Train[]   trains   = new Train[10];    static int trainCount   = 0;
    static Booking[] bookings = new Booking[100]; static int bookingCount = 0;
    static int pnrCounter = 1000;
    static boolean loggedIn = false;

    // Find train by number
    static Train findTrain(int no) {
        for (int i = 0; i < trainCount; i++)
            if (trains[i].no == no) return trains[i];
        return null;
    }

    // Find booking by PNR
    static Booking findBooking(String pnr) {
        for (int i = 0; i < bookingCount; i++)
            if (bookings[i].pnr.equals(pnr)) return bookings[i];
        return null;
    }

    // Book a ticket
    static void book(String passenger, int trainNo) {
        Train t = findTrain(trainNo);
        if (t == null)            { System.out.println("Train not found.");      return; }
        if (!t.bookSeat())        { System.out.println("No seats available.");   return; }
        String pnr = "PNR" + pnrCounter++;
        bookings[bookingCount++] = new Booking(pnr, passenger, t);
        System.out.println("Booking Successful! PNR: " + pnr);
    }

    // Cancel ticket and calculate refund
    static void cancel(String pnr) {
        Booking b = findBooking(pnr);
        if (b == null)                    { System.out.println("PNR not found.");     return; }
        if (b.status.equals("Cancelled")) { System.out.println("Already cancelled."); return; }

        long hours = Duration.between(LocalDateTime.now(), b.train.departure).toHours();
        double charge;
        if      (hours < 4)  charge = b.paid;           // < 4 hrs  : full charge
        else if (hours < 48) charge = b.paid * 0.5;     // < 48 hrs : 50% charge
        else                 charge = b.paid * 0.25;    // > 48 hrs : 25% charge

        b.status = "Cancelled";
        b.train.releaseSeat();
        System.out.println("Cancelled. Refund: Rs." + (b.paid - charge) + " | Charge: Rs." + charge);
    }

    // Main method
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        trains[trainCount++] = new Train(101, "Cochin - Bangalore", 1200, LocalDateTime.now().plusDays(2));
        trains[trainCount++] = new Train(102, "Cochin - Chennai",    950, LocalDateTime.now().plusHours(5));

        System.out.println("Welcome! Login: admin / 123");

        while (true) {
            System.out.print("\n1.Login  2.Trains  3.Book  4.PNR  5.Cancel  6.Exit\nChoice: ");
            int choice = sc.nextInt(); sc.nextLine();

            if (choice == 6) { System.out.println("Goodbye!"); break; }

            if (choice == 1) {
                System.out.print("Username: "); String u = sc.nextLine();
                System.out.print("Password: "); String p = sc.nextLine();
                if (u.equals("admin") && p.equals("123")) { loggedIn = true;  System.out.println("Login Successful!"); }
                else                                       { System.out.println("Invalid credentials."); }
                continue;
            }

            if (loggedIn == false) { System.out.println("Please login first!"); continue; }

            if (choice == 2) {
                for (int i = 0; i < trainCount; i++) System.out.println(trains[i]);

            } else if (choice == 3) {
                System.out.print("Train No: "); int tn = sc.nextInt(); sc.nextLine();
                System.out.print("Passenger Name: "); String name = sc.nextLine();
                book(name, tn);

            } else if (choice == 4) {
                System.out.print("PNR: "); Booking b = findBooking(sc.nextLine());
                System.out.println(b == null ? "PNR not found." : b);

            } else if (choice == 5) {
                System.out.print("PNR: "); cancel(sc.nextLine());
            }
        }
        sc.close();
    }
}
