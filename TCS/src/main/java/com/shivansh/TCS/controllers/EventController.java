package com.shivansh.TCS.controllers;

import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shivansh.TCS.mail.*;
import com.shivansh.TCS.model.*;
import com.shivansh.TCS.repository.*;

@CrossOrigin
@RestController
@RequestMapping("/events")
public class EventController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    EmailService emailService;

    // this method creates a new event, takes in a list of user IDs, a room ID and
    // other event details
    /*
     * POST /events/create
     * 
     * Request body:
     * {
     * "datetime": "2021-04-20T12:00:00.000+00:00",
     * "numPeople": 5,
     * "duration": 2,
     * "importance": 5,
     * "people": [1, 2, 3],
     * "room": 1
     * }
     * 
     * Response body:
     * "Event created successfully"
     */
    @PostMapping("/create")
    public ResponseEntity<Object> createEvent(@RequestBody Map<String, Object> Body) {
        try {
            String d = (String) Body.get("datetime");
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date dateTime = sf.parse(d);

            Integer numPeople = (Integer) Body.get("numPeople");
            Integer duration = (Integer) Body.get("duration");
            Integer importance = (Integer) Body.get("importance");

            List<Integer> tempPeople = (List<Integer>) Body.get("people");
            // convert tempPeople to List<Long>
            List<Long> people = new ArrayList<>();
            for (Integer i : tempPeople) {
                people.add(i.longValue());
            }

            List<User> members = new ArrayList<>();
            List<Room> rooms = new ArrayList<>();

            Integer tempRoom = (Integer) Body.get("room");
            Long room = tempRoom.longValue();

            Event event = new Event(numPeople, duration, importance, dateTime);

            System.out.println("Created Event object");

            // for (Long id : people) {
            // System.out.println("Adding user with id " + id);
            // Optional<User> user = userRepository.findById(id);
            // if (user.isPresent()) {
            // members.add(user.get());
            // } else {
            // return new ResponseEntity<>("User with id " + id + " not found",
            // HttpStatus.NOT_FOUND);
            // }
            // }

            // event.setPeople(members);

            // System.out.println("People added");

            // Optional<Room> roomObj = roomRepository.findById(room);
            // if (roomObj.isPresent()) {
            // rooms.add(roomObj.get());
            // } else {
            // return new ResponseEntity<>("Room with id " + room + " not found",
            // HttpStatus.NOT_FOUND);
            // }

            // event.setRooms(rooms);

            // System.out.println("Room added");

            // eventRepository.save(event);

            System.out.println("Event saved");
            return new ResponseEntity<>(event, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating event: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    // this method deletes an event by ID
    /*
     * DELETE /events/delete/{id}
     * 
     * Response body:
     * "Event deleted successfully"
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<Object> deleteEvent(@PathVariable Long id) {
        try {
            Optional<Event> event = eventRepository.findById(id);
            if (event.isPresent()) {
                eventRepository.deleteById(id);
                return new ResponseEntity<>("Event deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Event with id " + id + " not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting event: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    // this method returns all events
    /*
     * GET /events/all
     * 
     * Response body:
     * [
     * {
     * "id": 1,
     * "numPeople": 5,
     * "duration": 2,
     * "importance": 5,
     * "datetime": "2021-04-20T12:00:00.000+00:00",
     * "room": {
     * "id": 1,
     * "name": "Room 1",
     * "capacity": 5,
     * "location": "Location 1"
     * },
     * "people": [
     * {
     * "id": 1,
     * "name": "User 1",
     * "email": "
     * 
     * @gmail.com",
     * "role": "ADMIN"
     * },
     * {
     * "id": 2,
     * "name": "User 2",
     * "email": "
     * 
     * @gmail.com",
     * "role": "USER"
     * },
     * {
     * "id": 3,
     * "name": "User 3",
     * "email": "
     * 
     * @gmail.com",
     * "role": "USER"
     * }
     * ]
     * },
     * {
     * "id": 2,
     * "numPeople": 5,
     * "duration": 2,
     * "importance": 5,
     * "datetime": "2021-04-20T12:00:00.000+00:00",
     * "room": {
     * "id": 1,
     * "name": "Room 1",
     * "capacity": 5,
     * "location": "Location 1"
     * },
     * "people": [
     * {
     * "id": 1,
     * "name": "User 1",
     * "email": "
     * 
     * @gmail.com",
     * "role": "ADMIN"
     * },
     * {
     * "id": 2,
     * "name": "User 2",
     * "email": "
     * 
     * @gmail.com",
     * "role": "USER"
     * },
     * {
     * "id": 3,
     * "name": "User 3",
     * "email": "
     * 
     * @gmail.com",
     * "role": "USER"
     * }
     * ]
     * }
     * ]
     * 
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllEvents() {
        try {
            List<Event> events = eventRepository.findAll();
            return new ResponseEntity<>(events, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error getting events: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    // this method returns all events belonging to a user
    /*
     * GET events/user/{id}
     * 
     * Response body:
     * [
     * {
     * "id": 1,
     * "numPeople": 5,
     * "duration": 2,
     * "importance": 5,
     * "datetime": "2021-04-20T12:00:00.000+00:00",
     * "room": {
     * "id": 1,
     * "name": "Room 1",
     * "capacity": 5,
     * "location": "Location 1"
     * },
     * "people": [
     * {
     * "id": 1,
     * "name": "User 1",
     * "email": "
     * 
     * @gmail.com",
     * "role": "ADMIN"
     * },
     * {
     * "id": 2,
     * "name": "User 2",
     * "email": "
     * 
     * @gmail.com",
     * "role": "USER"
     * },
     * {
     * "id": 3,
     * "name": "User 3",
     * "email": "
     * 
     * @gmail.com",
     * "role": "USER"
     * }
     * ]
     * }
     * ]
     */
    // @GetMapping("/user/{id}")
    // public ResponseEntity<Object> getEventsByUser(@PathVariable Long id) {
    // try {
    // Optional<User> user = userRepository.findById(id);
    // if (user.isPresent()) {
    // List<Event> events = eventRepository.findByUser(user.get());
    // return new ResponseEntity<>(events, HttpStatus.OK);
    // } else {
    // return new ResponseEntity<>("User with id " + id + " not found",
    // HttpStatus.NOT_FOUND);
    // }
    // } catch (Exception e) {
    // return new ResponseEntity<>("Error getting events", HttpStatus.BAD_REQUEST);
    // }
    // }
}