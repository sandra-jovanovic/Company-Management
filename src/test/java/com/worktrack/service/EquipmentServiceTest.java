package com.worktrack.service;

import com.worktrack.model.Company;
import com.worktrack.model.Equipment;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EquipmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private EquipmentService equipmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveEquipment() {

        Company company = new Company();
        company.setName("Test Company");
        company.setAddress("Test Address");
        company.setRegistrationNumber("12345");
        company.setEmail("test@company.com");

        when(companyRepository.findAll()).thenReturn(List.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        Equipment equipment = new Equipment();
        equipment.setName("Test Equipment");
        equipment.setCount(10);
        equipment.setDescription("Test description");

        when(equipmentRepository.save(any(Equipment.class))).thenReturn(equipment);

        Equipment savedEquipment = equipmentService.saveEquipment(equipment);

        assertNotNull(savedEquipment);
        assertEquals("Test Equipment", savedEquipment.getName());
        assertEquals(10, savedEquipment.getCount());
        assertEquals("Test description", savedEquipment.getDescription());

        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    void testFindEquipmentById() {

        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("Test Equipment");
        equipment.setCount(10);
        equipment.setDescription("Test description");

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));

        Equipment foundEquipment = equipmentService.findEquipmentById(1L);

        assertNotNull(foundEquipment);
        assertEquals("Test Equipment", foundEquipment.getName());
        assertEquals(10, foundEquipment.getCount());
        assertEquals("Test description", foundEquipment.getDescription());

        verify(equipmentRepository, times(1)).findById(1L);
    }

    @Test
    void testFindEquipmentById_NotFound() {
        when(equipmentRepository.findById(1L)).thenReturn(Optional.empty());

        // Testiranje bacanja izuzetka
        RuntimeException exception = assertThrows(RuntimeException.class, () -> equipmentService.findEquipmentById(1L));
        assertEquals("Equipment with id 1 not found", exception.getMessage());
    }

    @Test
    void testDeleteEquipment() {
        when(equipmentRepository.existsById(1L)).thenReturn(true);

        equipmentService.deleteEquipment(1L);

        verify(equipmentRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteEquipmentNotFound() {

        when(equipmentRepository.existsById(1L)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> equipmentService.deleteEquipment(1L));
        assertEquals("Equipment with id 1 does not exists", exception.getMessage());
    }


    @Test
    void testUpdateEquipment() {
        Equipment equipment = new Equipment();
        equipment.setId(1L);
        equipment.setName("Old Equipment");
        equipment.setCount(5);
        equipment.setDescription("Old description");

        Equipment updatedEquipment = new Equipment();
        updatedEquipment.setName("Updated Equipment");
        updatedEquipment.setCount(10);
        updatedEquipment.setDescription("Updated description");

        when(equipmentRepository.findById(1L)).thenReturn(Optional.of(equipment));
        when(equipmentRepository.save(any(Equipment.class))).thenReturn(updatedEquipment);

        Equipment result = equipmentService.updateEquipment(1L, updatedEquipment);

        assertNotNull(result);
        assertEquals("Updated Equipment", result.getName());
        assertEquals(10, result.getCount());
        assertEquals("Updated description", result.getDescription());

        verify(equipmentRepository, times(1)).save(any(Equipment.class));
    }

    @Test
    void testUpdateEquipmentNotFound() {
        Equipment updatedEquipment = new Equipment();
        updatedEquipment.setName("Updated Equipment");
        updatedEquipment.setCount(10);
        updatedEquipment.setDescription("Updated description");

        when(equipmentRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> equipmentService.updateEquipment(1L, updatedEquipment));
        assertEquals("Equipment with id 1 not found", exception.getMessage());
    }

}
