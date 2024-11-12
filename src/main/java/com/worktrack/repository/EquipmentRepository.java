package com.worktrack.repository;
import com.worktrack.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipmentRepository  extends JpaRepository<Equipment, Long> {
}
