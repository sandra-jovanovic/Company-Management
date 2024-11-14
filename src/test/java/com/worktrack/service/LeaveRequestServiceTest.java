package com.worktrack.service;

import com.worktrack.dto.EmployeeDto;
import com.worktrack.dto.LeaveRequestDto;
import com.worktrack.mapper.LeaveRequestMapper;
import com.worktrack.model.LeaveRequest;
import com.worktrack.model.User;
import com.worktrack.repository.LeaveRequestRepository;
import com.worktrack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class LeaveRequestServiceTest {

    @Mock
    private LeaveRequestRepository leaveRequestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private LeaveRequestMapper leaveRequestMapper;

    @InjectMocks
    private LeaveRequestService leaveRequestService;

    private LeaveRequestDto leaveRequestDto;
    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("email@email.com");
        user.setRole(User.Role.EMPLOYEE);

        leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setStartDate(LocalDate.parse("2024-11-01"));
        leaveRequestDto.setEndDate(LocalDate.parse("2024-11-05"));

        org.springframework.security.core.Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("user@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testSubmitLeaveRequest() {

        User manager = new User();
        manager.setEmail("manager@example.com");
        manager.setRole(User.Role.MANAGER);
        when(userRepository.findFirstByRole(User.Role.MANAGER)).thenReturn(Optional.of(manager));

        User user = new User();
        user.setEmail("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        EmployeeDto employeeDto = new EmployeeDto();
        employeeDto.setFirstname("Sandra");
        employeeDto.setLastname("Jovanovic");
        employeeDto.setEmail("s@j");

        LeaveRequestDto leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setStartDate(LocalDate.of(2024, 11, 1));
        leaveRequestDto.setEndDate(LocalDate.of(2024, 11, 5));
        leaveRequestDto.setEmployeeDto(employeeDto);

        when(leaveRequestMapper.toLeaveRequestDto(any(LeaveRequest.class)))
                .thenReturn(leaveRequestDto);

        when(leaveRequestRepository.save(any(LeaveRequest.class)))
                .thenReturn(new LeaveRequest());

        LeaveRequestDto result = leaveRequestService.submit(leaveRequestDto);

        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 11, 1), result.getStartDate());
        assertEquals(LocalDate.of(2024, 11, 5), result.getEndDate());

        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));

        verify(emailSenderService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void sendEmailToManager_ManagerNotFound() {

        when(userRepository.findFirstByRole(User.Role.MANAGER)).thenReturn(Optional.empty());

        LeaveRequestDto leaveRequestDto = new LeaveRequestDto();
        leaveRequestDto.setEmployeeDto(new EmployeeDto("Sandra", "Jovanovic", "s@j"));
        leaveRequestDto.setStartDate(LocalDate.of(2024, 11, 1));
        leaveRequestDto.setEndDate(LocalDate.of(2024, 11, 5));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            leaveRequestService.submit(leaveRequestDto);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testUpdateLeaveRequest() {

        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setId(1L);
        leaveRequest.setStartDate(LocalDate.of(2024, 11, 1));
        leaveRequest.setEndDate(LocalDate.of(2024, 11, 5));
        leaveRequest.setStatus(LeaveRequest.LeaveStatus.PENDING);
        leaveRequest.setFeedback("pending...");

        LeaveRequestDto updatedDto = new LeaveRequestDto();
        updatedDto.setStartDate(LocalDate.of(2024, 11, 1));
        updatedDto.setEndDate(LocalDate.of(2024, 11, 5));
        updatedDto.setStatus(LeaveRequest.LeaveStatus.APPROVED);
        updatedDto.setFeedback("Approved by Manager");

        EmployeeDto employeeDto = new EmployeeDto("Sandra", "Jovanovic", "s@j");
        updatedDto.setEmployeeDto(employeeDto);

        when(leaveRequestRepository.findById(1L)).thenReturn(Optional.of(leaveRequest));
        when(leaveRequestRepository.save(any(LeaveRequest.class))).thenReturn(leaveRequest);
        when(leaveRequestMapper.toLeaveRequestDto(any(LeaveRequest.class))).thenReturn(updatedDto);

        LeaveRequestDto result = leaveRequestService.updateLeaveRequest(1L, LeaveRequest.LeaveStatus.APPROVED, "Approved by Manager");

        assertNotNull(result);
        assertEquals("Approved by Manager", result.getFeedback());
        assertEquals(LeaveRequest.LeaveStatus.APPROVED, result.getStatus());

        verify(leaveRequestRepository, times(1)).save(any(LeaveRequest.class));
    }

    @Test
    void testGetAllLeaveRequests() {
        LeaveRequest leaveRequest1 = new LeaveRequest();
        leaveRequest1.setId(1L);
        leaveRequest1.setStartDate(LocalDate.of(2024, 11, 1));
        leaveRequest1.setEndDate(LocalDate.of(2024, 11, 5));

        LeaveRequest leaveRequest2 = new LeaveRequest();
        leaveRequest2.setId(2L);
        leaveRequest2.setStartDate(LocalDate.of(2024, 11, 1));
        leaveRequest2.setEndDate(LocalDate.of(2024, 11, 5));

        LeaveRequestDto leaveRequestDto1 = new LeaveRequestDto();
        leaveRequestDto1.setStartDate(LocalDate.of(2024, 11, 1));
        leaveRequestDto1.setEndDate(LocalDate.of(2024, 11, 5));

        LeaveRequestDto leaveRequestDto2 = new LeaveRequestDto();
        leaveRequestDto2.setStartDate(LocalDate.of(2024, 11, 1));
        leaveRequestDto2.setEndDate(LocalDate.of(2024, 11, 5));

        when(leaveRequestRepository.findAll()).thenReturn(List.of(leaveRequest1, leaveRequest2));
        when(leaveRequestMapper.toLeaveRequestDto(any(LeaveRequest.class)))
                .thenReturn(leaveRequestDto1)
                .thenReturn(leaveRequestDto2);

        List<LeaveRequestDto> leaveRequests = leaveRequestService.getAll();

        assertNotNull(leaveRequests);
        assertEquals(2, leaveRequests.size());
        assertEquals(LocalDate.of(2024, 11, 1), leaveRequests.get(0).getStartDate());
        assertEquals(LocalDate.of(2024, 11, 1), leaveRequests.get(1).getStartDate());

    }

    @Test
    void testDeleteLeaveRequest() {

        when(leaveRequestRepository.existsById(1L)).thenReturn(true);

        leaveRequestService.delete(1L);

        verify(leaveRequestRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteLeaveRequest_NotFound() {

        when(leaveRequestRepository.existsById(1L)).thenReturn(false);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            leaveRequestService.delete(1L);
        });

        assertEquals("LeaveRequest with id 1 does not exists", exception.getMessage());
    }



}

