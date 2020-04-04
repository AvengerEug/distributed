package com.eugene.sumarry.aop;


public class UserServiceImplProxy1 implements UserService {

    private UserService userService;

    public UserServiceImplProxy1(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void findUsers() {
        System.out.println("before1");
        userService.findUsers();
        System.out.println("after1");
    }
}
