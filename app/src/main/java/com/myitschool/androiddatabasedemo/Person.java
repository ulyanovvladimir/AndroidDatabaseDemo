package com.myitschool.androiddatabasedemo;

/**
 * Created by teacher on 16.03.18.
 */

class Person {
    private Long id;
    private String fullname;
    private String phone;
    private String category;

    public Person(int id, String fullName, String phone) {

    }

    public Person(Long id, String fullname, String phone) {
        this.id = id;
        this.fullname = fullname;
        this.phone = phone;
    }

    public Person(String fullname, String phone) {
        this.fullname = fullname;
        this.phone = phone;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
