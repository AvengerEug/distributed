package com.eugene.sumarry.aop;

public class UserServiceImplProxy2 implements UserService {

    private UserService userService;

    public UserServiceImplProxy2(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void findUsers() {
        System.out.println("before2");
        userService.findUsers();
        System.out.println("after2");
    }
}
