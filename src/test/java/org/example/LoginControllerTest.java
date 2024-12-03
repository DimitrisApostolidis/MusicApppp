package org.example;

import org.example.Controllers.LoginController;
import org.example.DataBase.DataBaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.prefs.Preferences;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginControllerTest {

    @Mock
    private DataBaseConnection dbMock;

    @Mock
    private PlaylistController playlistControllerMock;

    @Mock
    private Preferences prefsMock;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleLoginSuccessfulWithRememberMe() {
        // Arrange
        String username = "testuser";
        String password = "password123";

        when(dbMock.verifyCredentials(username, password)).thenReturn(true);
        when(dbMock.getUserId(username)).thenReturn(1);

        loginController.username_fld.setText(username);
        loginController.password_fld.setText(password);
        loginController.remember_me_chk.setSelected(true);

        // Act
        loginController.handleLogin();

        // Assert
        verify(dbMock).verifyCredentials(username, password);  // Επαλήθευση ότι κλήθηκε η verifyCredentials
        verify(prefsMock).put("username", username);  // Επαλήθευση ότι αποθηκεύτηκε το username
        verify(prefsMock).put("password", password);  // Επαλήθευση ότι αποθηκεύτηκε ο κωδικός
        assertFalse(loginController.error_lbl.isVisible());  // Επαλήθευση ότι το μήνυμα σφάλματος δεν είναι ορατό
    }

    @Test
    void testHandleLoginFailedInvalidCredentials() {
        // Arrange
        String username = "testuser";
        String password = "wrongpassword";

        when(dbMock.verifyCredentials(username, password)).thenReturn(false);

        loginController.username_fld.setText(username);
        loginController.password_fld.setText(password);

        // Act
        loginController.handleLogin();

        // Assert
        verify(dbMock).verifyCredentials(username, password);  // Επαλήθευση ότι κλήθηκε η verifyCredentials
        assertTrue(loginController.error_lbl.isVisible());  // Επαλήθευση ότι το μήνυμα σφάλματος είναι ορατό
        assertEquals("Invalid username or password", loginController.error_lbl.getText());  // Επαλήθευση ότι το μήνυμα σφάλματος είναι σωστό
    }

    @Test
    void testHandleLoginAsAdmin() {
        // Arrange
        String username = "admin";
        String password = "admin";

        loginController.username_fld.setText(username);
        loginController.password_fld.setText(password);

        // Act
        loginController.handleLogin();

        // Assert
        verify(playlistControllerMock).saveLoggedInUserId("admin");  // Επαλήθευση ότι το userId του admin αποθηκεύτηκε
        assertFalse(loginController.error_lbl.isVisible());  // Επαλήθευση ότι το μήνυμα σφάλματος δεν είναι ορατό
    }
}
