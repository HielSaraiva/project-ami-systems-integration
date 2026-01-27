package org.eletra.energy.business.models.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eletra.energy.business.models.Log;
import org.eletra.energy.business.models.User;

@NoArgsConstructor
@Getter
@Setter
public class ReceivedMessageDTO {
    private User user;
    private Log log;
}
