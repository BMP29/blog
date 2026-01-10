package com.example.blog.service;

import com.example.blog.DTOs.ChangePasswordDTO;
import com.example.blog.DTOs.LoginUserDto;
import com.example.blog.DTOs.RegisterUserDto;
import com.example.blog.DTOs.VerifyUserDto;
import com.example.blog.model.User;
import com.example.blog.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public User signup(RegisterUserDto input) {
        User user = new User(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()));
        user.setVerificationCode(generateVerificationCode());
        user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
        user.setEnabled(false);
        sendVerificationEmail(user);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        User user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            input.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return user;
    }

    public void verifyUser(VerifyUserDto input) {
        Optional<User> optionalUser = userRepository.findByEmail(input.getEmail());
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }
            if (user.getVerificationCode().equals(input.getVerificationCode())) {
                user.setEnabled(true);
                user.setVerificationCode(null);
                user.setVerificationCodeExpiresAt(null);
                userRepository.save(user);
            } else {
                throw new RuntimeException("Invalid verification code");
            }
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void resendVerificationCode(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public void changePassword(ChangePasswordDTO input) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if(!passwordEncoder.matches(input.oldPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password.");
        }

        user.setPassword(passwordEncoder.encode(input.newPassoword()));

        this.userRepository.save(user);
    }

    private void sendVerificationEmail(User user) {
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f3f3f3; margin: 0; padding: 0;\">"
                + "<div style=\"width: 100%; max-width: 600px; margin: 0 auto; padding: 30px;\">"
                + "<div style=\"text-align: center; background-color: #4CAF50; color: #fff; padding: 20px; border-radius: 8px 8px 0 0;\">"
                + "<h2 style=\"margin: 0; font-size: 24px;\">Bem-vindo ao nosso App!</h2>"
                + "<p style=\"font-size: 16px;\">Estamos felizes em tê-lo conosco. Complete o processo abaixo para continuar.</p>"
                + "</div>"
                + "<div style=\"background-color: #ffffff; padding: 30px; border-radius: 0 0 8px 8px; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);\">"
                + "<h3 style=\"color: #333; font-size: 22px; margin-bottom: 15px;\">Código de Verificação</h3>"
                + "<p style=\"font-size: 16px; color: #555;\">Digite o código de verificação abaixo para continuar:</p>"
                + "<div style=\"text-align: center; margin-top: 20px;\">"
                + "<span style=\"font-size: 28px; font-weight: bold; color: #4CAF50; padding: 10px 20px; background-color: #f1f1f1; border-radius: 8px; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\">"
                + verificationCode
                + "</span>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #888; margin-top: 30px;\">Se você não reconhece essa solicitação, ignore este email.</p>"
                + "</div>"
                + "<div style=\"text-align: center; margin-top: 40px; font-size: 12px; color: #999;\">"
                + "<p>&copy; 2026, Nossa Empresa. Todos os direitos reservados.</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
