package com.shivansh.TCS.repository;

import java.util.List;
import java.util.Optional;
import com.shivansh.TCS.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {

    public List<Event> findByUser(User user);

    public List<Event> findByRoom(Room room);

    public Optional<Event> findById(Long id);

    // public List<Event> findByImportance(Integer importance);
}
