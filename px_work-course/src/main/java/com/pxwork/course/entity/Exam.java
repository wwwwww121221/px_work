package com.pxwork.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("exams")
public class Exam implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long courseId;

    private String title;

    private Integer duration;

    private BigDecimal weightProcess;

    private BigDecimal weightEnd;

    private BigDecimal weightPractical;

    private BigDecimal passTotalScore;

    private BigDecimal passProcessScore;

    private BigDecimal passEndScore;

    private BigDecimal passPracticalScore;

    private LocalDateTime createdAt;
}
