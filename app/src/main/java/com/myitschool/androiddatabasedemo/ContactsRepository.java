package com.myitschool.androiddatabasedemo;

import java.util.List;

/**
 * @author Vladimir Ulyanov
 */

public interface ContactsRepository {
    List<Person> getContacts();
    Person getPerson(int id);
    Person insert(Person p);
    void update(Person p);
    void delete(Person p);
}
