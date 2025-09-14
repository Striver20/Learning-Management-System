package com.example.lms.service;

import com.example.lms.entity.Content;
import com.example.lms.entity.Course;
import com.example.lms.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContentService {

    private final ContentRepository contentRepo;

    public Content addContentToCourse(Course course, Content content){
        content.setCourse(course);
        content.setCreatedAt(LocalDateTime.now());
        return contentRepo.save(content);
    }
    public Content save(Content content) {
        return contentRepo.save(content);
    }
    public List<Content> getContentsByCourseId(Long courseId) {
        return contentRepo.findByCourseId(courseId);
    }

    public void deleteContent(Long id) {
        contentRepo.deleteById(id);
    }

    public List<Content> getContentsByCourse(Course course){
        return contentRepo.findByCourse(course);
    }
    public Optional<Content> findById(Long id) {
        return contentRepo.findById(id);
    }
}
