package com.worktrack.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class LeaveRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String feedback;

    @Enumerated(EnumType.STRING)
    private LeaveRequest.LeaveStatus status;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id",
                referencedColumnName = "id",
                nullable = true,
                foreignKey = @ForeignKey(name = "FK_User_LeaveRequest")
    )
    private User user;

    public enum LeaveStatus{
        PENDING,
        APPROVED,
        REJECTED
    }

}
