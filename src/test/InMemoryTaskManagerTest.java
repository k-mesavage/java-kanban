package test;

import service.InMemoryTaskManager;


public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    InMemoryTaskManagerTest() {
        setManager(new InMemoryTaskManager());
    }           //Set manager for TaskManagerTest
}