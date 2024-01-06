package my.sns.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import my.sns.enums.AlarmType;
import my.sns.model.entity.AlarmEntity;

import java.sql.Timestamp;

@AllArgsConstructor
@Data
public class AlarmForm {

    private Integer id;
    private UserForm user;
    private AlarmType alarmType;
    private AlarmArguments args;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static AlarmForm fromEntity(AlarmEntity entity) {
        return new AlarmForm(
                entity.getId(),
                UserForm.fromEntity(entity.getUser()),
                entity.getAlarmType(),
                entity.getArgs(),
                entity.getRegisteredAt(),
                entity.getUpdatedAt(),
                entity.getDeletedAt()
        );
    }
}
