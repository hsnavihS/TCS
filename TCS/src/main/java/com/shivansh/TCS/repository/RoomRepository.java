package com.shivansh.TCS.repository;

import com.shivansh.TCS.model.*;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
    public Optional<Room> findById(Long id);

    public void deleteById(Long id);
}
