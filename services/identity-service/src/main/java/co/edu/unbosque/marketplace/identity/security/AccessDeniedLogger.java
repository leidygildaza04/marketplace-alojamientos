package co.edu.unbosque.marketplace.identity.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class AccessDeniedLogger implements AccessDeniedHandler {

    private static final Logger log =
            LoggerFactory.getLogger(AccessDeniedLogger.class);

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException exception)
            throws IOException, ServletException {

        String user = request.getUserPrincipal() != null
                ? request.getUserPrincipal().getName()
                : "anonymous";

        String path = request.getRequestURI();

        log.warn("security.access_denied user={} path={} result={}",
                user, path, "DENIED");

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
    }
}
