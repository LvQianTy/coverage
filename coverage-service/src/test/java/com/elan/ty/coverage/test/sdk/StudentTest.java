package com.elan.ty.coverage.test.sdk;

import com.elan.ty.coverage.common.test.LombokDataTestUtils;
import com.elan.ty.coverage.sdk.domain.Student;
import com.elan.ty.coverage.sdk.service.StudentSdkService;
import org.junit.Assert;
import org.junit.Test;

public class StudentTest {

    @Test
    public void testStudent() {
        StudentSdkService studentSdkService = new StudentSdkService();
        Student student = new Student();
        student.setNumber("123");
        student.setName("张三");
        student.setAge(16);
        studentSdkService.save(student);

        Student result = studentSdkService.get(student.getNumber());
        Assert.assertEquals(result.getName(), student.getName());

    }

    @Test
    public void testDomain1() {
        LombokDataTestUtils.cover(Student.class);
    }

    @Test
    public void testDomain2() {
        LombokDataTestUtils.cover(Student.class.getPackage().getName());
    }

}
