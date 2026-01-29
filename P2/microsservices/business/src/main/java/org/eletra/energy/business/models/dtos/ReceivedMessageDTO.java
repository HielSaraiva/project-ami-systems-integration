package org.eletra.energy.business.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eletra.energy.business.models.entities.Log;
import org.eletra.energy.business.models.entities.User;

@NoArgsConstructor
@Getter
@Setter
public class ReceivedMessageDTO {
    private User user;
    private Log log;
}
