package com.hotel.reservationsystem.views;

import com.hotel.reservationsystem.controllers.ReservationController;
import com.hotel.reservationsystem.enums.BoardType;
import com.hotel.reservationsystem.models.Customer;
import com.hotel.reservationsystem.controllers.RoomController;
import com.hotel.reservationsystem.controllers.UserInputController;
import com.hotel.reservationsystem.models.Reservation;
import com.hotel.reservationsystem.models.Room;
import com.hotel.reservationsystem.models.UserInput;

import java.util.ArrayList;
import java.util.Date;

public class ReservationView {
    private static ReservationController reservationController = ReservationController.getInstance();

    public void showReservationNumberList () {
        ArrayList<Reservation> reservations = reservationController.getInstance().getAllReservations();

        String message = "";
        if (!reservations.isEmpty()) {
            for (Reservation res : reservations) {
                message += res.getReservationNumber() + ", ";
            }
        } else {
            message = "No reservations found.  ";
        }
        // Remove trailing comma from message
        if (message.contains(",")) message = message.substring(0, message.length() - 2);
        System.out.println(message + "\n");
    }

    public void showReservationByInput() {
        // Wait for input to show detail info or quit
        int input = UserInputController.returnIntInput("Voer het reserveringsnummer in voor meer informatie");
        showReservationInformation(ReservationController.getInstance().getReservationByNumber(input));
    }

    /**
     * Creates a new reservation through user input
     */
    public void addNewReservationByInput() {
        // Let the user know what's happening
        System.out.println("===================\nLet's create a reservation!\n");

        Date startDate = UserInput.returnDateInput("Enter the check-in date (dd/mm/yyyy): ");
        Date endDate = UserInput.returnDateInput("Enter the check-out date (dd/mm/yyyy): ");

        Customer customer = new Customer(); // TODO Check for existing customer (Ask user if customer is new)
        customer.setFirstName(UserInput.returnStringInput("Enter the first name of the main booker"));
        customer.setLastName(UserInput.returnStringInput("Enter the last name of the main booker"));
        customer.setAddress(UserInput.returnStringInput("Enter the address of the main booker"));
        //customer.city           = UserInput.returnStringInput("Enter the city of residence of the main booker");
        customer.setPhoneNumber(UserInput.returnStringInput("Add the phone number of the main booker"));
        customer.setEmail(UserInput.returnStringInput("Add the email of the main booker")); // TODO add email regex
        customer.setBirthday(UserInput.returnDateInput("Add the date of birth of the main booker (dd/mm/yyyy)"));

        String boardTypeInput = UserInput.returnStringInput("Enter the board type (Bed and Breakfast, Half Board, Accommodations): ");
        BoardType boardType = getBoardTypeFromInput(boardTypeInput);

        System.out.println("Which rooms do you want to reserve? The following are available:\n");
        RoomView.showAllAvailableRooms();
        ArrayList<Room> rooms = getRoomsFromInput();

        ReservationController.getInstance().createReservation(rooms, startDate, endDate, customer, boardType);
    }

    private BoardType getBoardTypeFromInput(String input) {
        BoardType type = null;

        switch (input.toLowerCase()) {
            case "bed and breakfast":
            case "bnb":
                type = BoardType.BED_AND_BREAKFAST;
                break;
            case "half board":
            case "half-board":
            case "half":
                type = BoardType.HALF_BOARD;
                break;
            case "accommodations":
            case "acc":
                type = BoardType.ACCOMMODATIONS;
                break;
            default:
                System.out.println("Please enter a valid board type.");
                getBoardTypeFromInput(input);
                break;
        }
        return type;
    }

    private ArrayList<Room> getRoomsFromInput() {
        ArrayList<Room> availableRooms = RoomController.getInstance().getAllAvailableRooms(); // TODO Use RoomController to get Rooms
        ArrayList<Room> enteredRooms = new ArrayList<>();
        String input = null;
        while (true) {
            boolean validRoomNumberInput = true;
            boolean roomAdded = false;
            input = UserInput.returnStringInput("");
            if ( !input.matches("[0-9]+") ) { // TODO
                if (input.equals("s")) {
                    break;
                }
            }
            int roomNumber = 0;
            try {
                roomNumber = Integer.parseInt(input); // Create parse error throw
            } catch (NumberFormatException nfe) {
                System.out.println("Please enter a valid room number or return to the menu with 's'.");
                validRoomNumberInput = false;
            }
            if (validRoomNumberInput) {
                for (int i = 0; i < availableRooms.size(); i++) {
                    if (roomNumber == availableRooms.get(i).getRoomNumber()) {
                        enteredRooms.add(availableRooms.get(i));
                        roomAdded = true;
                        System.out.println("Room number " + availableRooms.get(i).getRoomNumber() + " is added to your reservation. " +
                                "Press 's' to continue.");
                        break;
                    }
                }
                if (!roomAdded) {
                    System.out.println("Please enter the room number of an available room.");
                }
            }
        }
        return enteredRooms;
    }

    public void showReservationInformation (Reservation reservation) {
        System.out.println("Reserveringsnummer: " + reservation.getReservationNumber());
        System.out.println("Reserveringsdatum: " + reservation.getReservationDate());
        System.out.println("Datum van ingang: " + reservation.getStartDate());
        System.out.println("Einddatum: " + reservation.getEndDate());
        System.out.println("Totale kosten reservering: " + reservation.getTotalPrice());
        System.out.println("Naam hoofdboeker: " + reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName());
        System.out.println("Verzorgingstype: " + reservation.getBoardType().getBoardType());
        String kamers = "";
        for (Room room : reservation.getRooms()) {
            kamers += room.getRoomNumber() + ", ";
        }
        System.out.println("Kamers: " + kamers.substring(0, kamers.length() - 2));
    }
}
