package com.bns.platotrain.security;

import com.bns.platotrain.util.ConversionUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component(value = "logoutSuccess")
public class LogoutSuccess implements LogoutSuccessHandler {

    private ConversionUtil conversionUtil;

    public LogoutSuccess(ConversionUtil conversionUtil) {
        this.conversionUtil = conversionUtil;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException {
        Map<String, String> result = new HashMap<>();
        result.put( "data", "success" );
        result.put( "status", "true" );
        result.put( "statusCode", "200" );
        result.put( "statusMessage", "OK" );
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write( conversionUtil.getJSONFromObject(result) );
        httpServletResponse.setStatus(HttpServletResponse.SC_OK);
    }

}