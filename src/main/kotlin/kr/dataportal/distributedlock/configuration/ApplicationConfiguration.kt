package kr.dataportal.distributedlock.configuration

import kr.dataportal.distributedlock.domain.Domain
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

/**
 * @Author Heli
 */
@ComponentScan(basePackageClasses = [Domain::class])
@Configuration
internal class ApplicationConfiguration
