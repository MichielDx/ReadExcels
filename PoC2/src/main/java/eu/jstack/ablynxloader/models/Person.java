package eu.jstack.ablynxloader.models;

import org.apache.logging.log4j.core.util.UuidUtil;

import java.util.Date;
import java.util.UUID;

public class Person {
    private UUID id;
    private String first_name;
    private String last_name;
    private String email;
    private String gender;
    private String ip_address;
    private Date date;
    private int age;
    private Integer hash;

    public Person() {
        this.id = UuidUtil.getTimeBasedUuid();
    }

    public UUID getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getIp_address() {
        return ip_address;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getHash() {
        return hash;
    }

    public void setHash(Integer hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", first_name='" + first_name + '\'' +
                ", last_name='" + last_name + '\'' +
                ", gender='" + gender + '\'' +
                ", ip_address='" + ip_address + '\'' +
                ", date=" + date +
                ", age=" + age +
                ", hash='" + hash + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        Integer value = 0;
        value += first_name == null ? 0 : first_name.hashCode();
        value += last_name == null ? 0 : last_name.hashCode();
        value += gender == null ? 0 : gender.hashCode();
        value += ip_address == null ? 0 : ip_address.hashCode();
        value += date == null ? 0 : date.hashCode();
        value += email == null ? 0 : email.hashCode();
        value += age;
        return value;
    }
}

