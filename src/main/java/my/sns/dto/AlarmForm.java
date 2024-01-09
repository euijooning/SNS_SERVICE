package my.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import my.sns.enums.AlarmType;
import my.sns.model.entity.AlarmEntity;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
@Slf4j
public class AlarmForm {

    private Integer id;
    private AlarmType alarmType;
    private AlarmArguments args;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static AlarmForm fromEntity(AlarmEntity entity) {
        log.info("==== call fromEntity ====");
        return new AlarmForm(
                entity.getId(),
                entity.getAlarmType(),
                entity.getArgs(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
