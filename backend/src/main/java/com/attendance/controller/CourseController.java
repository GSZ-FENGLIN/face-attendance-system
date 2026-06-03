package com.attendance.controller;

import com.attendance.dto.PageResult;
import com.attendance.dto.Result;
import com.attendance.entity.Course;
import com.attendance.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/course")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public Result list(@RequestParam(required = false) String keyword,
                       @RequestParam(required = false) Long teacherId,
                       @RequestParam(required = false) String className,
                       @RequestParam(defaultValue = "1") int page,
                       @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Course> result = courseService.listCourses(keyword, teacherId, className, page, pageSize);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        return Result.success(courseService.getCourse(id));
    }

    @PostMapping("/add")
    public Result add(@RequestBody Course course) {
        courseService.addCourse(course);
        return Result.success();
    }

    @PutMapping("/update")
    public Result update(@RequestBody Course course) {
        courseService.updateCourse(course);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return Result.success();
    }
}
