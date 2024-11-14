package com.worktrack.controller;

import com.worktrack.dto.LeaveRequestDto;
import com.worktrack.model.LeaveRequest;
import com.worktrack.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
    @RequestMapping("/api/leave-requests")
    public class LeaveRequestController {

        @Autowired
        private LeaveRequestService leaveRequestService;

        // Endpoint for submitting a leave request
        @PostMapping("/submit")
        public ResponseEntity<?> submit(@Valid @RequestBody LeaveRequestDto leaveRequestDto, BindingResult bindingResult){
            if (bindingResult.hasErrors()) {
                StringBuilder errorMessages = new StringBuilder();
                bindingResult.getAllErrors().forEach(error -> {
                    errorMessages.append(error.getDefaultMessage()).append("\n");
                });
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages.toString());
            }
            try{
                LeaveRequestDto requestDto = leaveRequestService.submit(leaveRequestDto);
                return ResponseEntity.status(HttpStatus.CREATED).body(requestDto);
            } catch(Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
            }
        }

        @PutMapping("/{id}/approve")
        public ResponseEntity<LeaveRequestDto> approveLeaveRequest(@PathVariable("id") Long id){
            try{
                LeaveRequestDto leaveRequestDto = leaveRequestService.updateLeaveRequest(id, LeaveRequest.LeaveStatus.APPROVED, "Request approved.");
                return ResponseEntity.ok(leaveRequestDto);
            } catch(Exception e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }

        @PutMapping("/{id}/reject")
        public ResponseEntity<LeaveRequestDto> rejectLeaveRequest(@PathVariable("id") Long id){
            try{
                LeaveRequestDto leaveRequestDto = leaveRequestService.updateLeaveRequest(id, LeaveRequest.LeaveStatus.REJECTED, "Request rejected.");
                return ResponseEntity.ok(leaveRequestDto);
            } catch(Exception e){
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        }

        @GetMapping
        public ResponseEntity<List<LeaveRequestDto>> getAllLeaveRequests() {
            List<LeaveRequestDto> leaveRequestDtos = leaveRequestService.getAll();
            return ResponseEntity.ok(leaveRequestDtos);
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> delete(@PathVariable("id") Long id) {
            try {
                leaveRequestService.delete(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Leave request deleted.");
            } catch (RuntimeException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Leave request not found.");
            }
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> update(@PathVariable Long id,@Valid @RequestBody LeaveRequestDto leaveRequestDto, BindingResult bindingResult){
            if (bindingResult.hasErrors()) {
                StringBuilder errorMessages = new StringBuilder();
                bindingResult.getAllErrors().forEach(error -> {
                    errorMessages.append(error.getDefaultMessage()).append("\n");
                });
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessages.toString());
            }
            try {
                LeaveRequestDto updatedLeaveRequest = leaveRequestService.update(id, leaveRequestDto);
                return new ResponseEntity<>(updatedLeaveRequest, HttpStatus.OK);
            } catch (RuntimeException e) {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        }

        @GetMapping("/{id}")
        public ResponseEntity<?> getById(@PathVariable("id") Long id){
            try{
                LeaveRequestDto leaveRequestDto = leaveRequestService.findById(id);
                return new ResponseEntity<>(leaveRequestDto, HttpStatus.OK);
            } catch (RuntimeException e){
                return new ResponseEntity<>("Id not found", HttpStatus.NOT_FOUND);
            }
        }

    }

