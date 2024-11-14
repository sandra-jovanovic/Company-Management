package com.worktrack.service;

import com.worktrack.model.Company;
import com.worktrack.model.Vehicle;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveVehicle() {

        Company company = new Company();
        company.setName("Test Company");
        company.setAddress("Test Address");
        company.setRegistrationNumber("12345");
        company.setEmail("test@company.com");

        when(companyRepository.findAll()).thenReturn(List.of(company));
        when(companyRepository.save(any(Company.class))).thenReturn(company);

        Vehicle vehicle = new Vehicle();
        vehicle.setName("Test Vehicle");
        vehicle.setCount(10);
        vehicle.setDescription("Test description");

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);

        assertNotNull(savedVehicle);
        assertEquals("Test Vehicle", savedVehicle.getName());
        assertEquals(10, savedVehicle.getCount());
        assertEquals("Test description", savedVehicle.getDescription());

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testFindVehicleById() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setName("Test Vehicle");
        vehicle.setCount(10);
        vehicle.setDescription("Test description");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        Vehicle foundVehicle = vehicleService.findVehicleById(1L);

        assertNotNull(foundVehicle);
        assertEquals("Test Vehicle", foundVehicle.getName());
        assertEquals(10, foundVehicle.getCount());
        assertEquals("Test description", foundVehicle.getDescription());

        verify(vehicleRepository, times(1)).findById(1L);
    }


    @Test
    void testFindVehicleById_NotFound() {
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> vehicleService.findVehicleById(1L));
        assertEquals("Vehicle with id 1 not found", exception.getMessage());
    }

    @Test
    void testFindAll() {
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setId(1L);
        vehicle1.setName("Test Vehicle 1");
        vehicle1.setCount(5);
        vehicle1.setDescription("Description 1");

        Vehicle vehicle2 = new Vehicle();
        vehicle2.setId(2L);
        vehicle2.setName("Test Vehicle 2");
        vehicle2.setCount(10);
        vehicle2.setDescription("Description 2");

        List<Vehicle> vehicleList = List.of(vehicle1, vehicle2);

        when(vehicleRepository.findAll()).thenReturn(vehicleList);

        List<Vehicle> result = vehicleService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Vehicle 1", result.get(0).getName());
        assertEquals("Test Vehicle 2", result.get(1).getName());

        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    void testDeleteVehicle() {
        when(vehicleRepository.existsById(1L)).thenReturn(true);

        vehicleService.deleteVehicle(1L);

        verify(vehicleRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteVehicleNotFound() {
        when(vehicleRepository.existsById(1L)).thenReturn(false);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> vehicleService.deleteVehicle(1L));
        assertEquals("Vehicle with id 1 does not exists", exception.getMessage());
    }

    @Test
    void testUpdateVehicle() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setName("Old Vehicle");
        vehicle.setCount(5);
        vehicle.setDescription("Old description");

        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setName("Updated Vehicle");
        updatedVehicle.setCount(10);
        updatedVehicle.setDescription("Updated description");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);


        Vehicle result = vehicleService.updateVehicle(1L, updatedVehicle);

        assertNotNull(result);
        assertEquals("Updated Vehicle", result.getName());
        assertEquals(10, result.getCount());
        assertEquals("Updated description", result.getDescription());

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicleNotFound() {
        Vehicle updatedVehicle = new Vehicle();
        updatedVehicle.setName("Updated Vehicle");
        updatedVehicle.setCount(10);
        updatedVehicle.setDescription("Updated description");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> vehicleService.updateVehicle(1L, updatedVehicle));
        assertEquals("Vehicle with id 1 not found", exception.getMessage());
    }
}

