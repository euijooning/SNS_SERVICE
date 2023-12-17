package my.sns.service;

import my.sns.dto.UserDto;
import my.sns.exception.SnsApplicationException;
import my.sns.model.entity.UserEntity;
import my.sns.repository.UserEntityRepository;
import my.sns.util.JwtTokenUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final String secretKey = "testSecretKey";
    private final Long expiredMs = 3600000L;

    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(userService, "secretKey", secretKey);
        ReflectionTestUtils.setField(userService, "expiredMs", expiredMs);
    }

    @Test
    void testJoin() {
        // given
        String userName = "testUser";
        String password = "testPassword";
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(userEntityRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        UserDto result = userService.join(userName, password);

        // then
        assertNotNull(result);
        assertEquals(userName, result.getUserName());

        // verify that repository methods were called
        verify(userEntityRepository, times(1)).findByUserName(userName);
        verify(userEntityRepository, times(1)).save(any());
    }

    @Test
    void testJoin_DuplicateUserName() {
        // given
        String userName = "existingUser";
        String password = "testPassword";
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(mock(UserEntity.class)));

        // when, then
        assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));

        // verify that repository method was called
        verify(userEntityRepository, times(1)).findByUserName(userName);
        // verify that save method was not called
        verify(userEntityRepository, never()).save(any());
    }

    @Test
    void testLogin() {
        // given
        String userName = "testUser";
        String password = "testPassword";
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(bCryptPasswordEncoder.encode(password));
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.matches(password, userEntity.getPassword())).thenReturn(true);

        // Mock JwtTokenUtils
        JwtTokenUtils jwtTokenUtilsMock = mock(JwtTokenUtils.class);
        when(jwtTokenUtilsMock.generateToken(eq(userName), anyString(), anyLong())).thenReturn("mockedToken");

        // Inject the mocked JwtTokenUtils into the UserService
        ReflectionTestUtils.setField(userService, "jwtTokenUtils", jwtTokenUtilsMock);

        // when
        String token = userService.login(userName, password);

        // then
        assertNotNull(token);
        assertEquals("mockedToken", token);

        // verify that repository method was called
        verify(userEntityRepository, times(1)).findByUserName(userName);
        // verify that matches method was called
        verify(bCryptPasswordEncoder, times(1)).matches(password, userEntity.getPassword());
        // verify that JwtTokenUtils.generateToken was called with non-null arguments
        verify(jwtTokenUtilsMock, times(1)).generateToken(eq(userName), anyString(), anyLong());
    }

    @Test
    void testLogin_InvalidPassword() {
        // given
        String userName = "testUser";
        String password = "testPassword";
        UserEntity userEntity = new UserEntity();
        userEntity.setPassword(bCryptPasswordEncoder.encode("differentPassword"));
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(userEntity));
        when(bCryptPasswordEncoder.matches(password, userEntity.getPassword())).thenReturn(false);

        // when, then
        assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));

        // verify that repository method was called
        verify(userEntityRepository, times(1)).findByUserName(userName);
        // verify that matches method was called
        verify(bCryptPasswordEncoder, times(1)).matches(password, userEntity.getPassword());
    }

    @Test
    void testLogin_UserNotFound() {
        // given
        String userName = "nonExistingUser";
        String password = "testPassword";
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());

        // when, then
        assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));

        // verify that repository method was called
        verify(userEntityRepository, times(1)).findByUserName(userName);
        // verify that matches method was not called
        verify(bCryptPasswordEncoder, never()).matches(any(), any());
    }
}