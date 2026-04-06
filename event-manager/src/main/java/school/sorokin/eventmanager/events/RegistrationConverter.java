package school.sorokin.eventmanager.events;

import org.springframework.stereotype.Component;
import school.sorokin.eventmanager.events.dto.RegistrationResponseDto;
import school.sorokin.eventmanager.events.entity.RegistrationEntity;

@Component
public class RegistrationConverter {
    public RegistrationResponseDto registrationToDto(RegistrationEntity registrationEntity){
        return new RegistrationResponseDto(
                registrationEntity.getId(),
                registrationEntity.getEvent().getName(),
                registrationEntity.getUser().getLogin()
        );
    }
}
