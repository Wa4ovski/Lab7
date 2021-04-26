package model;


import util.FileManager;

import java.util.Objects;
import java.time.LocalDateTime;

/**
 * Person data class.
 */

public class Person implements Comparable<Person> {
    private LocalDateTime birthday; //Поле не может быть null
    private long height; //Значение поля должно быть больше 0
    private String passportID; //Значение этого поля должно быть уникальным, Длина строки не должна быть больше 38, Поле не может быть null

    /**
     * @param birthday - birthday of person
     * @param height - height of person
     * @param passportID - passport ID of person
     */
    public Person(LocalDateTime birthday, int height, String passportID) {
        this.birthday = birthday;
        this.height = height;
        this.passportID = passportID;
    }

    public Person() {
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getBirthday() {
        return birthday;
    }

    public long getHeight() {
        return height;
    }

    public String getPassportID() {
        return passportID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return height == person.height &&
                Objects.equals(birthday, person.birthday) &&
                Objects.equals(passportID, person.passportID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(birthday, height, passportID);
    }

    @Override
    public String toString() {
       return  "Person{" +
                "birthday=" + FileManager.localDateTimeToString(birthday) +
                ", height=" + height +
                ", passportID='" + passportID +
                '}';
    }

    @Override
    public int compareTo(Person o) {
        return Long.compare(this.height, o.getHeight());
    }
}