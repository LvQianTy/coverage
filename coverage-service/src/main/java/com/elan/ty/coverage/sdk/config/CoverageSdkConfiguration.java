package com.elan.ty.coverage.sdk.config;

import com.elan.ty.coverage.sdk.service.StudentSdkService;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan(
        basePackageClasses = {StudentSdkService.class}
)
@Configuration
public class CoverageSdkConfiguration {
}
