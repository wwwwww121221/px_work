package com.pxwork.course.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pxwork.course.entity.Course;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
