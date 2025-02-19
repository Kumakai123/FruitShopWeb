package org.xiangan.fruitshopweb.entity;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 雜物
 */
@Data
@Entity
@NoArgsConstructor
@Table(name = "miscellaneous")
public class Miscellaneous {

    /**
     * 主鍵
     */
    @Column(
        nullable = false,
        updatable = false,
        length = 11
    )
    @Id
    private String id;

    /**
     * 名稱
     */
    @Basic(optional = false)
    @Column(name = "name")
    @NotNull
    private String name;

    /**
     * 花費金額
     */
    @Basic(optional = false)
    @Column(name = "amount")
    @NotNull
    private BigDecimal amount;

    /**
     * 紀錄時間
     */
    @Column(name = "date")
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "Asia/Taipei")
    private LocalDateTime recordDate;

    @PrePersist
    protected void genPrimaryKey() {
        if (id == null) {
            id = NanoIdUtils.randomNanoId(
                ThreadLocalRandom.current(),
                "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray(),
                10
            );
        }
    }

    /**
     * 默認建構子
     */
    public Miscellaneous(String name,BigDecimal amount) {
        this.name = name;
        this.amount = amount;
        this.recordDate = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Miscellaneous{" + "id=" + id + ", name='" + name + '\'' + ", amount=" + amount + '}';
    }
}
