package com.intelliblood.donor;

import java.io.Serializable;

public class Donor implements Comparable<Donor>, Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String bloodGroup;
    private String contact;
    private String city;
    private int age;
    private int weight;
    private int daysSinceLastDonation;
    private boolean available;
    private boolean verified;

    public Donor(String name, String bloodGroup, String contact, String city,
                 int age, int weight, int daysSinceLastDonation) {
        this.name = name;
        this.bloodGroup = bloodGroup.toUpperCase();
        this.contact = contact;
        this.city = city;
        this.age = age;
        this.weight = weight;
        this.daysSinceLastDonation = daysSinceLastDonation;
        this.available = true;
        this.verified = false;
    }

    public String getName() { return name; }
    public String getBloodGroup() { return bloodGroup; }
    public String getContact() { return contact; }
    public String getCity() { return city; }
    public int getAge() { return age; }
    public int getWeight() { return weight; }
    public int getDaysSinceLastDonation() { return daysSinceLastDonation; }
    public boolean isAvailable() { return available; }
    public boolean isVerified() { return verified; }

    public void setAvailable(boolean available) { this.available = available; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setDaysSinceLastDonation(int days) { this.daysSinceLastDonation = days; }

    public int compareTo(Donor other) {
        return Integer.compare(other.daysSinceLastDonation, this.daysSinceLastDonation);
    }

    public String toString() {
        return name + " | " + bloodGroup + " | " + city + " | " + contact +
               " | Age: " + age + " | Weight: " + weight + "kg | Days: " + daysSinceLastDonation +
               (verified ? " | Verified" : " | Unverified");
    }
}
