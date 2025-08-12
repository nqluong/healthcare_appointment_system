package project.healthcare_appointment.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import project.healthcare_appointment.repository.AppointmentRepository;
import project.healthcare_appointment.repository.DoctorRepository;
import project.healthcare_appointment.repository.PatientRepository;

import java.util.UUID;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    private final JwtUtil jwtUtil;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public boolean isCurrentUser(UUID targetUserId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            UUID currentUserId = jwtUtil.getUserIdFromJwt(jwt);

            return currentUserId.equals(targetUserId);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            String role = jwtUtil.getRoleFromJwt(jwt);

            return "ADMIN".equals(role) || "ROLE_ADMIN".equals(role);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDoctorOwner(UUID doctorId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();

            String currentUserEmail = jwtUtil.getEmailFromJwt(jwt);

            // Check if the doctor belongs to the current user
            return doctorRepository.findById(doctorId)
                    .map(doctor -> doctor.getUser().getEmail().equals(currentUserEmail))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPatientOwner(UUID patientId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();

            String currentUserEmail = jwtUtil.getEmailFromJwt(jwt);


            return patientRepository.findById(patientId)
                    .map(doctor -> doctor.getUser().getEmail().equals(currentUserEmail))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDoctorOwnerOfAppointment(UUID appointmentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            String currentUserEmail = jwtUtil.getEmailFromJwt(jwt);

            // Check if the appointment belongs to the current doctor
            return appointmentRepository.findById(appointmentId)
                    .map(appointment -> appointment.getDoctor().getUser().getEmail().equals(currentUserEmail))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPatientOwnerOfAppointment(UUID appointmentId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return false;
            }

            Jwt jwt = (Jwt) authentication.getPrincipal();
            String currentUserEmail = jwtUtil.getEmailFromJwt(jwt);

            // Check if the appointment belongs to the current patient
            return appointmentRepository.findById(appointmentId)
                    .map(appointment -> appointment.getPatient().getUser().getEmail().equals(currentUserEmail))
                    .orElse(false);
        } catch (Exception e) {
            return false;
        }
    }

    public boolean canAccessUserProfile(UUID targetUserId) {
        return isCurrentUser(targetUserId) || isAdmin();
    }
}
