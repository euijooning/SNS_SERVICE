package my.sns.model.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.Setter;
import my.sns.dto.AlarmArguments;
import my.sns.enums.AlarmType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "\"alarm\"", indexes = { // 여기도 인덱스 걸어주기
        @Index(name = "user_id_idx", columnList = "user_id")
})
@Getter
@Setter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLDelete(sql = "UPDATE \"alarm\" SET deleted_at = NOW() WHERE id=?")
@Where(clause = "deleted_at is NULL")
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne // 이건 알람 수신자 유저!
    @JoinColumn(name = "user_id")
    private UserEntity user;

    // jsonb는 mysql에는 없는 기능 - 의존성 추가 필요
    @Type(type = "jsonb") // jsonb는 한 번 압축해서 저장 -> 여기에는 인덱스를 걸 수 있다.
    @Column(columnDefinition = "json")
    private AlarmArguments args; // 알람 타입에 대한 정보
    // 알람이 발생한 글, 댓글, 유저 정보 등을 마이그레이션 할 수 있으므로


    // 알람 타입 구분할 Enum을 추가 String 타입
    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @Column(name = "registered_at")
    private Timestamp registeredAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;


    // 자동으로 타임스탬프 이용해서 저장되도록 만들기
    @PrePersist
    void registeredAt() {
        this.registeredAt = Timestamp.from(Instant.now());
    }

    @PreUpdate
    void updatedAt() {
        this.updatedAt = Timestamp.from(Instant.now());
    }


    public static AlarmEntity of(UserEntity userEntity, AlarmType alarmType, AlarmArguments args) {
        AlarmEntity entity = new AlarmEntity();
        entity.setUser(userEntity);
        entity.setAlarmType(alarmType);
        entity.setArgs(args);
        return entity;
    }
}