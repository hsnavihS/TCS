package com.shivansh.TCS.repository;

import java.util.Optional;
import com.shivansh.TCS.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    public Optional<Event> findById(Long id);

    public void deleteById(Long id);
}
