package project.healthcare_appointment.service.schedule_service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.healthcare_appointment.dto.request.schedule_request.AddAvailableSlotRequest;
import project.healthcare_appointment.dto.request.schedule_request.CreateDoctorScheduleRequest;
import project.healthcare_appointment.dto.request.schedule_request.GenerateSlotsRequest;
import project.healthcare_appointment.dto.request.schedule_request.UpdateDoctorScheduleRequest;
import project.healthcare_appointment.dto.response.PageResponse;
import project.healthcare_appointment.dto.response.available_response.AvailableSlotResponse;
import project.healthcare_appointment.dto.response.available_response.DoctorScheduleResponse;
import project.healthcare_appointment.dto.response.available_response.SlotGenerationResult;
import project.healthcare_appointment.dto.response.specialty_response.MultipleSlotCreationResult;
import project.healthcare_appointment.exception.AppException;
import project.healthcare_appointment.exception.ErrorCode;
import project.healthcare_appointment.mapper.AvailableSlotMapper;
import project.healthcare_appointment.mapper.DoctorScheduleMapper;
import project.healthcare_appointment.model.Doctor;
import project.healthcare_appointment.model.DoctorAvailableSlot;
import project.healthcare_appointment.model.DoctorSchedule;
import project.healthcare_appointment.repository.DoctorAvailableSlotRepository;
import project.healthcare_appointment.repository.DoctorRepository;
import project.healthcare_appointment.repository.DoctorScheduleRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DoctorScheduleServiceImpl implements DoctorScheduleService {

    DoctorAvailableSlotRepository slotRepository;
    DoctorScheduleRepository scheduleRepository;
    DoctorRepository doctorRepository;
    AvailableSlotMapper availableSlotMapper;
    DoctorScheduleMapper doctorScheduleMapper;

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public MultipleSlotCreationResult addAvailableSlot(UUID doctorId, AddAvailableSlotRequest request) {
        log.info("Adding available slot for doctor: {}, date: {}, time: {}-{}",
                doctorId, request.getSlotDate(), request.getStartTime(), request.getEndTime());

        // Validate time
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        List<DoctorAvailableSlot> createdSlots = new ArrayList<>();
        List<String> skippedPeriods = new ArrayList<>();

        // Calculate slots based on duration
        LocalTime currentTime = request.getStartTime();
        int slotDuration = request.getSlotDurationMinutes();

        while (currentTime.isBefore(request.getEndTime())) {
            LocalTime slotEndTime = currentTime.plusMinutes(slotDuration);

            // Don't create slot if it would exceed the end time
            if (slotEndTime.isAfter(request.getEndTime())) {
                break;
            }

            // Check if slot already exists
            if (slotRepository.existsByDoctorIdAndSlotDateAndStartTime(doctorId, request.getSlotDate(), currentTime)) {
                skippedPeriods.add(currentTime + "-" + slotEndTime);
                log.debug("Skipping existing slot: {} to {}", currentTime, slotEndTime);
            } else {

                DoctorAvailableSlot slot = DoctorAvailableSlot.builder()
                        .doctor(doctor)
                        .slotDate(request.getSlotDate())
                        .startTime(currentTime)
                        .endTime(slotEndTime)
                        .isAvailable(true)
                        .build();

                DoctorAvailableSlot savedSlot = slotRepository.save(slot);
                createdSlots.add(savedSlot);
                log.debug("Created slot: {} to {}", currentTime, slotEndTime);
            }

            currentTime = currentTime.plusMinutes(slotDuration);
        }

        log.info("Successfully created {} slots, skipped {} periods for doctor: {}",
                createdSlots.size(), skippedPeriods.size(), doctorId);


        return MultipleSlotCreationResult.builder()
                .slotsCreated(createdSlots.size())
                .slotDate(request.getSlotDate())
                .originalStartTime(request.getStartTime())
                .originalEndTime(request.getEndTime())
                .slotDuration(slotDuration)
                .createdSlots(availableSlotMapper.toResponseList(createdSlots))
                .skippedPeriods(skippedPeriods)
                .build();
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public DoctorScheduleResponse createDoctorSchedule(UUID doctorId, CreateDoctorScheduleRequest request) {
        log.info("Creating schedule for doctor: {}, day: {}, time: {}-{}",
                doctorId, request.getDayOfWeek(), request.getStartTime(), request.getEndTime());

        // Validate time
        if (!request.getStartTime().isBefore(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_TIME_RANGE);
        }

        // Check if doctor exists
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        // Check if schedule already exists for this day
        if (scheduleRepository.existsByDoctorIdAndDayOfWeek(doctorId, request.getDayOfWeek())) {
            throw new AppException(ErrorCode.SCHEDULE_ALREADY_EXISTS);

        }

        DoctorSchedule schedule = DoctorSchedule.builder()
                .doctor(doctor)
                .dayOfWeek(request.getDayOfWeek())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .slotDuration(request.getSlotDuration())
                .isActive(request.getIsActive())
                .build();

        DoctorSchedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Successfully created schedule with ID: {}", savedSchedule.getId());

        return doctorScheduleMapper.toResponse(savedSchedule);
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public DoctorScheduleResponse updateDoctorSchedule(UUID doctorId, UUID scheduleId, UpdateDoctorScheduleRequest request) {
        log.info("Updating schedule: {} for doctor: {}", scheduleId, doctorId);

        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        // Verify schedule belongs to doctor
        if (!schedule.getDoctor().getId().equals(doctorId)) {
            throw new AppException(ErrorCode.SCHEDULE_NOT_OWNED);
        }

        // Update fields if provided
        if (request.getDayOfWeek() != null) {
            if (!schedule.getDayOfWeek().equals(request.getDayOfWeek()) &&
                    scheduleRepository.existsByDoctorIdAndDayOfWeek(doctorId, request.getDayOfWeek())) {
                throw new AppException(ErrorCode.SCHEDULE_ALREADY_EXISTS);
            }
            schedule.setDayOfWeek(request.getDayOfWeek());
        }

        if (request.getStartTime() != null) {
            schedule.setStartTime(request.getStartTime());
        }

        if (request.getEndTime() != null) {
            schedule.setEndTime(request.getEndTime());
        }

        if (request.getSlotDuration() != null) {
            schedule.setSlotDuration(request.getSlotDuration());
        }

        if (request.getIsActive() != null) {
            schedule.setIsActive(request.getIsActive());
        }

        // Validate time if both are set
        if (schedule.getStartTime() != null && schedule.getEndTime() != null &&
                !schedule.getStartTime().isBefore(schedule.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        DoctorSchedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Successfully updated schedule: {}", scheduleId);

        return doctorScheduleMapper.toResponse(savedSchedule);
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public List<DoctorScheduleResponse> getDoctorSchedules(UUID doctorId) {
        log.info("Getting schedules for doctor: {}", doctorId);

        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdOrderByDayOfWeekAsc(doctorId);
        return doctorScheduleMapper.toResponseList(schedules);
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public void deleteDoctorSchedule(UUID doctorId, UUID scheduleId) {
        log.info("Deleting schedule: {} for doctor: {}", scheduleId, doctorId);

        DoctorSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_FOUND));

        // Verify schedule belongs to doctor
        if (!schedule.getDoctor().getId().equals(doctorId)) {
            throw new AppException(ErrorCode.SCHEDULE_NOT_OWNED);
        }

        scheduleRepository.delete(schedule);
        log.info("Successfully deleted schedule: {}", scheduleId);
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public SlotGenerationResult generateSlotsFromSchedule(UUID doctorId, GenerateSlotsRequest request) {
        log.info("Generating slots for doctor: {} from {} to {}", doctorId, request.getStartDate(), request.getEndDate());

        // Validate date range
        if (!request.getStartDate().isBefore(request.getEndDate()) && !request.getStartDate().isEqual(request.getEndDate())) {
            throw new AppException(ErrorCode.INVALID_DATE_RANGE);
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new AppException(ErrorCode.DOCTOR_NOT_FOUND));

        List<DoctorSchedule> schedules = scheduleRepository.findByDoctorIdAndIsActiveTrue(doctorId);
        if (schedules.isEmpty()) {
            schedules = createDefaultSchedules(doctor);
            log.info("Created default schedules for doctor: {}", doctorId);
        }

        List<DoctorAvailableSlot> generatedSlots = new ArrayList<>();
        int slotsGenerated = 0;
        int slotsSkipped = 0;

        // Iterate through date range
        LocalDate currentDate = request.getStartDate();
        while (!currentDate.isAfter(request.getEndDate())) {
            int dayOfWeek = currentDate.getDayOfWeek().getValue(); // 1=Monday, 7=Sunday

            // Find matching schedule for this day
            for (DoctorSchedule schedule : schedules) {
                if (schedule.getDayOfWeek().equals(dayOfWeek)) {
                    List<DoctorAvailableSlot> daySlots = generateSlotsForDay(doctor, schedule, currentDate, request.getOverrideExisting());
                    generatedSlots.addAll(daySlots);
                    slotsGenerated += daySlots.size();
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        // Count existing slots if not overriding
        if (!request.getOverrideExisting()) {
            List<DoctorAvailableSlot> existingSlots = slotRepository.findByDoctorIdAndSlotDateBetween(
                    doctorId, request.getStartDate(), request.getEndDate());
            slotsSkipped = existingSlots.size();
        }

        log.info("Generated {} slots, skipped {} slots for doctor: {}", slotsGenerated, slotsSkipped, doctorId);

        return SlotGenerationResult.builder()
                .slotsGenerated(slotsGenerated)
                .slotsSkipped(slotsSkipped)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .generatedSlots(availableSlotMapper.toResponseList(generatedSlots))
                .build();
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public void deleteAvailableSlot(UUID doctorId, UUID slotId) {
        log.info("Deleting available slot: {} for doctor: {}", slotId, doctorId);

        DoctorAvailableSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new AppException(ErrorCode.SLOT_NOT_FOUND));

        // Verify slot belongs to doctor
        if (!slot.getDoctor().getId().equals(doctorId)) {
            throw new AppException(ErrorCode.SLOT_DOES_NOT_BELONG_TO_DOCTOR);
        }

        // Check if slot is booked
        if (slotRepository.existsBookedAppointment(doctorId, slot.getSlotDate(), slot.getStartTime())) {
            throw new AppException(ErrorCode.SLOT_BOOKED_CANNOT_DELETE);
        }

        slotRepository.delete(slot);
        log.info("Successfully deleted available slot: {}", slotId);
    }

    @Override
    @PreAuthorize("(hasRole('DOCTOR') and @userSecurity.isDoctorOwner(#doctorId)) or hasRole('ADMIN')")
    public PageResponse<AvailableSlotResponse> getAvailableSlots(UUID doctorId, Pageable pageable) {
        log.info("Getting available slots for doctor: {}", doctorId);

        Page<DoctorAvailableSlot> slots = slotRepository.findByDoctorIdOrderBySlotDateAscStartTimeAsc(doctorId, pageable);
        return availableSlotMapper.toResponsePage(slots);
    }

    private List<DoctorSchedule> createDefaultSchedules(Doctor doctor) {
        List<DoctorSchedule> defaultSchedules = new ArrayList<>();

        for (int dayOfWeek = 1; dayOfWeek <= 5; dayOfWeek++) {

            DoctorSchedule schedule = DoctorSchedule.builder()
                    .doctor(doctor)
                    .dayOfWeek(dayOfWeek)
                    .startTime(LocalTime.of(9, 0)) // 9:00 AM
                    .endTime(LocalTime.of(17, 0))  // 5:00 PM
                    .slotDuration(30) // 30 minutes
                    .isActive(true)
                    .build();

            DoctorSchedule savedSchedule = scheduleRepository.save(schedule);
            defaultSchedules.add(savedSchedule);
        }

        log.info("Created {} default schedules for doctor: {}", defaultSchedules.size(), doctor.getId());
        return defaultSchedules;
    }

    private List<DoctorAvailableSlot> generateSlotsForDay(Doctor doctor, DoctorSchedule schedule,
                                                          LocalDate date, Boolean overrideExisting) {
        List<DoctorAvailableSlot> slots = new ArrayList<>();

        LocalTime currentTime = schedule.getStartTime();
        int slotDuration = schedule.getSlotDuration();

        while (currentTime.isBefore(schedule.getEndTime())) {
            LocalTime slotEndTime = currentTime.plusMinutes(slotDuration);

            // Don't create slot if it would exceed the end time
            if (slotEndTime.isAfter(schedule.getEndTime())) {
                break;
            }

            // Check if slot already exists
            boolean exists = slotRepository.existsByDoctorIdAndSlotDateAndStartTime(
                    doctor.getId(), date, currentTime);

            if (!exists || overrideExisting) {
                if (exists && overrideExisting) {
                    // Delete existing slot if overriding
                    slotRepository.deleteByDoctorIdAndSlotDateAndStartTime(doctor.getId(), date, currentTime);
                }

                DoctorAvailableSlot slot = DoctorAvailableSlot.builder()
                        .doctor(doctor)
                        .slotDate(date)
                        .startTime(currentTime)
                        .endTime(slotEndTime)
                        .isAvailable(true)
                        .build();

                DoctorAvailableSlot savedSlot = slotRepository.save(slot);
                slots.add(savedSlot);
            }

            currentTime = currentTime.plusMinutes(slotDuration);
        }

        return slots;
    }
}
