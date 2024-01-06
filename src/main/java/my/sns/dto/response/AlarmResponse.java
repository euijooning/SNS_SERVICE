package my.sns.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import my.sns.dto.AlarmArguments;
import my.sns.dto.AlarmForm;
import my.sns.enums.AlarmType;

import java.sql.Timestamp;

@AllArgsConstructor
@Getter
public class AlarmResponse {

    private Integer id;
    private AlarmType alarmType;
    private AlarmArguments alarmArgs;
    private String text;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;


    public static AlarmResponse fromAlarm(AlarmForm alarm) {
        return new AlarmResponse(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getArgs(),
                alarm.getAlarmType().getAlarmText(),
                alarm.getRegisteredAt(),
                alarm.getUpdatedAt(),
                alarm.getDeletedAt()
        );
    }
}
