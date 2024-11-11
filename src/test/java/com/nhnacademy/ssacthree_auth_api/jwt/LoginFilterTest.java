//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.mockito.Mockito.any;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.nhnacademy.ssacthree_auth_api.domain.Member;
//import com.nhnacademy.ssacthree_auth_api.jwt.JWTUtil;
//import com.nhnacademy.ssacthree_auth_api.jwt.LoginFilter;
//import com.nhnacademy.ssacthree_auth_api.repository.MemberRepository;
//import com.nhnacademy.ssacthree_auth_api.repository.RefreshTokenRepository;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletResponse;
//import java.time.LocalDateTime;
//import java.util.List;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.mock.web.MockHttpServletResponse;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//
//public class LoginFilterTest {
//
//    @Mock
//    private JWTUtil jwtUtil;
//
//    @Mock
//    private RefreshTokenRepository refreshTokenRepository;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @InjectMocks
//    private LoginFilter loginFilter;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testSuccessfulAuthentication() throws Exception {
//        // given
//        String memberLoginId = "testUser";
//        String role = "ROLE_USER";
//        String accessToken = "accessToken";
//        String refreshToken = "refreshToken";
//        Long accessTokenExpired = 30 * 60 * 1000L;
//        Long refreshTokenExpired = 120 * 60 * 1000L;
//
//        Member member = new Member(1L, 1L, memberLoginId, "test", "20000101", LocalDateTime.now(),
//            null, "ACTIVE", 0);
//
//        // Mock JWTUtil and repository responses
//        when(jwtUtil.createJwt("access", memberLoginId, role, accessTokenExpired)).thenReturn(
//            accessToken);
//        when(jwtUtil.createJwt("refresh", memberLoginId, role, refreshTokenExpired)).thenReturn(
//            refreshToken);
//        when(memberRepository.findByMemberLoginId(memberLoginId)).thenReturn(member);
//
//        // Set up AuthenticationManager to return a valid Authentication object
//        Authentication auth = new UsernamePasswordAuthenticationToken(memberLoginId, "testPassword",
//            List.of(new SimpleGrantedAuthority(role)));
//        when(authenticationManager.authenticate(
//            any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
//
//        // Mock request and response
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        FilterChain filterChain = mock(FilterChain.class);
//
//        // Set request parameters
//        request.setParameter("memberLoginId", memberLoginId);
//        request.setParameter("memberPassword", "testPassword");
//
//        // when
//        loginFilter.doFilter(request, response, filterChain);
//
//        // then
//        Cookie accessCookie = response.getCookie("access-token");
//        Cookie refreshCookie = response.getCookie("refresh-token");
//
//        assertNotNull(accessCookie, "Access token cookie should not be null");
//        assertNotNull(refreshCookie, "Refresh token cookie should not be null");
//        assertEquals("accessToken", accessCookie.getValue());
//        assertEquals("refreshToken", refreshCookie.getValue());
//
//        // RefreshToken 저장 확인
//        verify(refreshTokenRepository).save(any());
//        verify(memberRepository).save(any(Member.class));
//    }
//
//
//    @Test
//    void testUnsuccessfulAuthentication() throws Exception {
//        // given
//        MockHttpServletRequest request = new MockHttpServletRequest();
//        MockHttpServletResponse response = new MockHttpServletResponse();
//        FilterChain filterChain = mock(FilterChain.class);
//
//        // Set invalid request parameters
//        request.setParameter("memberLoginId", "wrongUser");
//        request.setParameter("memberPassword", "wrongPassword");
//
//        // Mock AuthenticationManager to throw exception for invalid credentials
//        doThrow(new RuntimeException()).when(authenticationManager)
//            .authenticate(any(UsernamePasswordAuthenticationToken.class));
//
//        // when
//        loginFilter.doFilter(request, response, filterChain);
//
//        // then
//        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
//    }
//}
