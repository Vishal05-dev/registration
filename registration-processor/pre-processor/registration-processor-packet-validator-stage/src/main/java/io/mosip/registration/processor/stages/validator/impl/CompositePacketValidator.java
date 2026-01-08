package io.mosip.registration.processor.stages.validator.impl;

import java.io.IOException;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.PacketManagerException;
import io.mosip.registration.processor.core.exception.RegistrationProcessorCheckedException;
import io.mosip.registration.processor.core.packet.dto.packetvalidator.PacketValidationDto;
import io.mosip.registration.processor.core.spi.packet.validator.PacketValidator;

@Component
@Primary
public class CompositePacketValidator implements PacketValidator {

    private static Logger regProcLogger = RegProcessorLogger.getLogger(CompositePacketValidator.class);

    @Autowired
    private PacketValidatorImpl packetValidatorImpl;

    /** The reference validator. */
    @Autowired(required = false)
    @Qualifier("referenceValidatorImpl")
    @Lazy
    private PacketValidator referenceValidatorImpl;

    @Override
    public boolean validate(String id, String process, PacketValidationDto packetValidationDto) throws ApisResourceAccessException, RegistrationProcessorCheckedException, IOException, JsonProcessingException, PacketManagerException {
        long validateStartTime = System.currentTimeMillis();
        boolean isValid = packetValidatorImpl.validate(id, process, packetValidationDto);
        regProcLogger.info(LoggerFileConstant.SESSIONID.toString(),
                LoggerFileConstant.REGISTRATIONID.toString(), "PacketValidatorStage",
                "Time taken for CompositePacketValidator.validate for rid - " + id + " - " + (System.currentTimeMillis() - validateStartTime) + " (ms)");
        if (isValid)
            isValid = referenceValidatorImpl.validate(id, process, packetValidationDto);
        return isValid;
    }
}
