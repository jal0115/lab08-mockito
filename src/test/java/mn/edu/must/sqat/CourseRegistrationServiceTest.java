package mn.edu.must.sqat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistrationServiceTest {

    @Mock StudentRepository repository;
    @Mock EmailSender emailSender;

    CourseRegistrationService service;

    @BeforeEach
    void setUp() {
        service = new CourseRegistrationService(repository, emailSender);
    }

    // A1
    @Test
    void register_activeStudentWithPrereqs_returnsOkSavesAndSendsEmail() {
        Student s = new Student("S1", "s1@must.edu.mn", true, List.of("CS101"));
        when(repository.findStudent("S1")).thenReturn(s);
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101"));

        String result = service.register("S1", "CS201");

        assertEquals("OK", result);
        verify(repository).saveRegistration("S1", "CS201");
        verify(emailSender).send(eq("s1@must.edu.mn"), anyString(), contains("CS201"));
    }

    // A2
    @Test
    void register_unknownStudent_returnsErrorNoStudentAndNothingSavedOrSent() {
        when(repository.findStudent("S404")).thenReturn(null);

        String result = service.register("S404", "CS201");

        assertEquals("ERROR_NO_STUDENT", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
        verify(emailSender, never()).send(anyString(), anyString(), anyString());
    }

    // A3
    @Test
    void register_inactiveStudent_returnsErrorInactiveAndNoEmailInteraction() {
        Student s = new Student("S2", "s2@must.edu.mn", false, List.of("CS101"));
        when(repository.findStudent("S2")).thenReturn(s);

        String result = service.register("S2", "CS201");

        assertEquals("ERROR_INACTIVE_STUDENT", result);
        verifyNoInteractions(emailSender);
    }

    // A4
    @Test
    void register_missingPrerequisite_returnsErrorPrerequisitesAndNothingSaved() {
        Student s = new Student("S3", "s3@must.edu.mn", true, List.of());
        when(repository.findStudent("S3")).thenReturn(s);
        when(repository.courseExists("CS201")).thenReturn(true);
        when(repository.prerequisites("CS201")).thenReturn(List.of("CS101"));

        String result = service.register("S3", "CS201");

        assertEquals("ERROR_PREREQUISITES", result);
        verify(repository, never()).saveRegistration(anyString(), anyString());
    }
}
