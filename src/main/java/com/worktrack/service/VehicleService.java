package com.worktrack.service;

import com.worktrack.model.Company;
import com.worktrack.model.Vehicle;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Vehicle saveVehicle(Vehicle vehicle) {
        Company company = companyRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                        Company newCompany = new Company();
                        newCompany.setName("Elephant Solutions");
                        newCompany.setAddress("Atinska 10");
                        newCompany.setRegistrationNumber("1563264");
                        newCompany.setEmail("e@s");

                        Company savedCompany = companyRepository.save(newCompany);
                        return savedCompany;
                    });

        vehicle.setCompany(company);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> findAll() {
        return vehicleRepository.findAll();
    }

    public Vehicle updateVehicle(Long id, Vehicle vehicle) {
        Optional<Vehicle> _vehicle = vehicleRepository.findById(id);

        if(_vehicle.isPresent()){
            Vehicle dbvehicle = _vehicle.get();

            dbvehicle.setName(vehicle.getName());
            dbvehicle.setCount(vehicle.getCount());
            dbvehicle.setDescription(vehicle.getDescription());

            return vehicleRepository.save(dbvehicle);
        } else {
            throw new RuntimeException("Vehicle with id "+id+" not found");
        }
    }

    public void deleteVehicle(Long id) {
        boolean exists = vehicleRepository.existsById(id);

        if(!exists){
            throw new IllegalStateException(
                    "Vehicle with id "+id+" does not exists");
        }
        vehicleRepository.deleteById(id);
    }

    public Vehicle findVehicleById(Long id) {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);

        if(vehicle.isPresent()){
            return vehicle.get();
        } else {
            throw new RuntimeException("Vehicle with id " + id + " not found");
        }

    }
}
