package com.worktrack.auth;

import com.worktrack.configuration.JwtService;
import com.worktrack.model.Company;
import com.worktrack.model.User;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CompanyRepository companyRepository;

    public AuthenticationResponse register(RegisterRequest request) {
        Company company = companyRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    //nova kompanija
                    Company newCompany = new Company();
                    newCompany.setName("Elephant Solutions");
                    newCompany.setAddress("Atinska 10");
                    newCompany.setRegistrationNumber("1563264");
                    newCompany.setEmail("e@s");

                    return companyRepository.save(newCompany);
                });

        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.EMPLOYEE)
                .company(company)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
