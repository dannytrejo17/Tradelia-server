package com.tradelia.Service;

import com.tradelia.exception.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class DemoModeService {

    public static final String DEMO_WRITE_MESSAGE =
            "Accion deshabilitada en modo demo. Esta es una version de solo lectura.";

    @Value("${app.demo-mode:false}")
    private boolean demoMode;

    public boolean isDemoMode() {
        return demoMode;
    }

    public void ensureWriteAllowed() {
        if (demoMode) {
            throw new ApiException(HttpStatus.FORBIDDEN, DEMO_WRITE_MESSAGE);
        }
    }
}
