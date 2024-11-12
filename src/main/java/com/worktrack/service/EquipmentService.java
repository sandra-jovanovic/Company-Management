package com.worktrack.service;
import com.worktrack.dto.EmployeeDto;
import com.worktrack.model.Company;
import com.worktrack.model.Equipment;
import com.worktrack.model.User;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EquipmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private CompanyRepository companyRepository;

    public Equipment saveEquipment(Equipment equipment) {
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

        equipment.setCompany(company);
        return equipmentRepository.save(equipment);
    }

    public Equipment findEquipmentById(Long id) {
        Optional<Equipment> equipment = equipmentRepository.findById(id);

        if(equipment.isPresent()){
            return equipment.get();
        } else {
            throw new RuntimeException("Equipment with id " + id + " not found");
        }

    }

    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }


    public void deleteEquipment(Long id) {
        boolean exists = equipmentRepository.existsById(id);

        if(!exists){
            throw new IllegalStateException(
                    "Equipment with id "+id+" does not exists");
        }
        equipmentRepository.deleteById(id);
    }

    public Equipment updateEquipment(Long id, Equipment equipment){
        Optional<Equipment> _equipment = equipmentRepository.findById(id);

        if(_equipment.isPresent()){
            Equipment dbequipment = _equipment.get();

            dbequipment.setName(equipment.getName());
            dbequipment.setCount(equipment.getCount());
            dbequipment.setDescription(equipment.getDescription());

            return equipmentRepository.save(dbequipment);
        } else {
            throw new RuntimeException("Equipment with id "+id+" not found");
        }
    }

}
