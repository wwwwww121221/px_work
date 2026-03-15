package com.pxwork.course.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pxwork.course.entity.UserCourseEnrollment;

@Mapper
public interface UserCourseEnrollmentMapper extends BaseMapper<UserCourseEnrollment> {
}
