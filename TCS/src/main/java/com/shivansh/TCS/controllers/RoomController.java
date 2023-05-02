package com.shivansh.TCS.controllers;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shivansh.TCS.mail.*;
import com.shivansh.TCS.model.*;
import com.shivansh.TCS.repository.*;

@CrossOrigin
@RestController
@RequestMapping("/rooms")
public class RoomController {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    EmailService emailService;

    // method to create a room
    /*
     * POST /rooms/create
     * 
     * Request body:
     * {
     * "name": "Room 1",
     * "capacity": 5,
     * "location": "F Block"
     * }
     * 
     * Response body:
     * "Room created successfully"
     */
    @PostMapping("/create")
    public ResponseEntity<Object> createRoom(@RequestBody Map<String, Object> Body) {
        try {
            String name = (String) Body.get("name");
            Integer capacity = (Integer) Body.get("capacity");
            String location = (String) Body.get("location");
            Room room = new Room(name, capacity, location);
            roomRepository.save(room);
            return new ResponseEntity<>(room, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error creating room: " + e, HttpStatus.BAD_REQUEST);
        }
    }

    // method to return all rooms
    /*
     * GET /rooms/all
     * 
     * Response body:
     * [
     * {
     * "id": 1,
     * "name": "Room 1",
     * "capacity": 5,
     * "location": "F Block"
     * },
     * {
     * "id": 2,
     * "name": "Room 2",
     * "capacity": 10,
     * "location": "F Block"
     * }
     * ]
     */
    @GetMapping("/all")
    public ResponseEntity<Object> getAllRooms() {
        try {
            List<Room> rooms = roomRepository.findAll();
            return new ResponseEntity<>(rooms, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error getting rooms", HttpStatus.BAD_REQUEST);
        }
    }

    // method to delete a room by ID
    /*
     * POST /rooms/delete/{id}
     * 
     * Response body:
     * "Room deleted successfully"
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<Object> deleteRoom(@PathVariable("id") Integer id) {
        try {
            roomRepository.deleteById(id.longValue());
            return new ResponseEntity<>("Room deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error deleting room: " + e, HttpStatus.BAD_REQUEST);
        }
    }
}