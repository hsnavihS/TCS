package com.shivansh.TCS.repository;

import com.shivansh.TCS.model.*;
import java.util.Optional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    public Optional<Room> findById(Long id);

    // public List<Room> findAllRooms();

    // public List<Room> findByCapacity(Integer capacity);

    // public List<Room> findByLocation(String location);

    // public List<Event> findByEvents(Event event);
}
