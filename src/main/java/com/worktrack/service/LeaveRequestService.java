package com.worktrack.service;
import com.worktrack.dto.EmployeeDto;
import com.worktrack.dto.LeaveRequestDto;
import com.worktrack.mapper.LeaveRequestMapper;
import com.worktrack.model.LeaveRequest;
import com.worktrack.model.User;
import com.worktrack.repository.LeaveRequestRepository;
import com.worktrack.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeaveRequestService {

    @Autowired
    private LeaveRequestRepository leaveRequestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private LeaveRequestMapper leaveRequestMapper;

    public LeaveRequestDto submit(LeaveRequestDto leaveRequestDto){
        //dobijanje trenutnog korisnika iz SecurityContext-a
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //kreiranje LeaveRequest-a
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());
        leaveRequest.setUser(user);
        leaveRequest.setFeedback("pending...");
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);

        leaveRequestRepository.save(leaveRequest);

        LeaveRequestDto requestDto = leaveRequestMapper.toLeaveRequestDto(leaveRequest);

        sendEmailToManagement(requestDto.getEmployeeDto());

        return requestDto;
    }

    private void sendEmailToManagement(EmployeeDto employee){
        User manager = userRepository.findFirstByRole(User.Role.MANAGER)
                .orElseThrow(() -> new RuntimeException("Manager not found"));

        //slanje mejla
        String subject = "New Leave Request Submitted";
        String text = "Employee " + employee.getFirstname() + " "+employee.getLastname()+" has submitted a leave request. " +
                "Please review the request.";

        emailSenderService.sendEmail(manager.getEmail(),subject,text);
    }

    //to employee
    public LeaveRequestDto updateLeaveRequest(Long id, LeaveRequest.LeaveStatus status, String feedback){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave Request not found"));

        leaveRequest.setStatus(status);
        leaveRequest.setFeedback(feedback);

        leaveRequestRepository.save(leaveRequest);

        LeaveRequestDto requestDtoUpdate = leaveRequestMapper.toLeaveRequestDto(leaveRequest);

        sendFeedbackEmail(requestDtoUpdate);

        return requestDtoUpdate;
    }

    private void sendFeedbackEmail(LeaveRequestDto leaveRequestDto){
        EmployeeDto employee = leaveRequestDto.getEmployeeDto();

        String subject = "Leave Request Status Update";
        String text = "Dear " + employee.getFirstname() + " " + employee.getLastname() + ",\n\n" +
                "Your leave request from " + leaveRequestDto.getStartDate() + " to " + leaveRequestDto.getEndDate() + " has been reviewed.\n" +
                "Status: " + leaveRequestDto.getStatus() + "\n" +
                "Feedback: " + leaveRequestDto.getFeedback() + "\n\n";

        emailSenderService.sendEmail(employee.getEmail(), subject,text);
    }

    //crud
    public List<LeaveRequestDto> getAll(){
        List<LeaveRequest> leaveRequests = leaveRequestRepository.findAll();

        return leaveRequests.stream()
                .map(leaveRequestMapper::toLeaveRequestDto)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        boolean exists = leaveRequestRepository.existsById(id);

        if(!exists){
            throw new IllegalStateException(
                    "LeaveRequest with id "+id+" does not exists");
        }
        leaveRequestRepository.deleteById(id);
    }

    public LeaveRequestDto findById(Long id){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave Request with id "+id+ " not found."));

        return leaveRequestMapper.toLeaveRequestDto(leaveRequest);
    }

    public LeaveRequestDto update(Long id, LeaveRequestDto leaveRequestDto){
        LeaveRequest leaveRequest = leaveRequestRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Leave Request not found"));

        leaveRequest.setStartDate(leaveRequestDto.getStartDate());
        leaveRequest.setEndDate(leaveRequestDto.getEndDate());

        leaveRequestRepository.save(leaveRequest);

        LeaveRequestDto requestDtoUpdate = leaveRequestMapper.toLeaveRequestDto(leaveRequest);

        return requestDtoUpdate;
    }

}
