package com.elan.ty.coverage.sdk.service;

import com.elan.ty.coverage.sdk.domain.Student;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StudentSdkService {
    /**
     * 这里我们以map代替数据库
     */
    private static final Map<String, Student> studentMap = new HashMap<>();

    public void save(Student student) {
        studentMap.put(student.getNumber(), student);
    }

    public Student get(String number) {
        return studentMap.get(number);
    }
}
