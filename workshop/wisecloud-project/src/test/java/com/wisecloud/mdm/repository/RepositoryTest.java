package com.wisecloud.mdm.repository;

import com.wisecloud.mdm.entity.Application;
import com.wisecloud.mdm.entity.Device;
import com.wisecloud.mdm.entity.Task;
import com.wisecloud.mdm.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class RepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveAndFindUser() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@example.com");
        user.setPasswordHash("hashed_password");

        User saved = userRepository.save(user);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(userRepository.findByUsername("admin")).isPresent();
        assertThat(userRepository.existsByUsername("admin")).isTrue();
        assertThat(userRepository.existsByUsername("nonexistent")).isFalse();
    }

    @Test
    void shouldSaveAndSearchDevice() {
        Device device = new Device();
        device.setSn("SN123456");
        device.setDeviceName("POS Terminal");
        device.setDeviceType("A920");
        device.setStatus(2);

        Device saved = deviceRepository.save(device);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getImportedAt()).isNotNull();
        assertThat(deviceRepository.existsBySn("SN123456")).isTrue();
        assertThat(deviceRepository.existsBySn("NONEXISTENT")).isFalse();

        List<Device> results = deviceRepository.findBySnContaining("SN12");
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSn()).isEqualTo("SN123456");

        List<Device> noResults = deviceRepository.findBySnContaining("ZZZZZ");
        assertThat(noResults).isEmpty();
    }

    @Test
    void shouldSaveAndFindApplication() {
        Application app = new Application();
        app.setAppName("TestApp");
        app.setAppPackage("com.test.app");
        app.setVersionNumber("1.0.0");
        app.setVersionName("v1.0");
        app.setVersionMd5("abc123md5");
        app.setAppAlias("Test Application");
        app.setAppDescription("A test application");

        Application saved = applicationRepository.save(app);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUploadedAt()).isNotNull();
        assertThat(applicationRepository.findAll()).hasSize(1);
    }

    @Test
    void shouldSaveAndFindTask() {
        Task task = new Task();
        task.setTaskName("Install Task");
        task.setTraceId("trace-001");
        task.setTaskType("install");
        task.setTargetApp("TestApp");
        task.setDeviceCount(5);

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(taskRepository.findByTraceId("trace-001")).isPresent();
        assertThat(taskRepository.findByTraceId("nonexistent")).isEmpty();
    }
}
