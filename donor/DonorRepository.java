package com.intelliblood.donor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class DonorRepository<T extends Donor> implements Iterable<T> {

    private ArrayList<T> donors;
    private HashMap<String, ArrayList<T>> bloodGroupMap;
    private LinkedList<String> recentActivities;
    private HashSet<String> registeredContacts;

    public DonorRepository() {
        donors = new ArrayList<>();
        bloodGroupMap = new HashMap<>();
        recentActivities = new LinkedList<>();
        registeredContacts = new HashSet<>();
    }

    public boolean addDonor(T donor) {
        if (registeredContacts.contains(donor.getContact())) {
            return false;
        }
        donors.add(donor);
        registeredContacts.add(donor.getContact());
        String bg = donor.getBloodGroup();
        if (!bloodGroupMap.containsKey(bg)) {
            bloodGroupMap.put(bg, new ArrayList<>());
        }
        bloodGroupMap.get(bg).add(donor);
        logActivity("Donor registered: " + donor.getName() + " [" + bg + "]");
        return true;
    }

    public ArrayList<T> getAllDonors() {
        return donors;
    }

    public ArrayList<T> getByBloodGroup(String bloodGroup) {
        ArrayList<T> result = bloodGroupMap.get(bloodGroup.toUpperCase());
        if (result == null) return new ArrayList<>();
        return result;
    }

    public ArrayList<T> searchByCity(String city) {
        ArrayList<T> result = new ArrayList<>();
        for (T d : donors) {
            if (d.getCity().equalsIgnoreCase(city)) {
                result.add(d);
            }
        }
        return result;
    }

    public ArrayList<T> searchByName(String name) {
        ArrayList<T> result = new ArrayList<>();
        for (T d : donors) {
            if (d.getName().toLowerCase().contains(name.toLowerCase())) {
                result.add(d);
            }
        }
        return result;
    }

    public boolean removeDonor(String contact) {
        T toRemove = null;
        for (T d : donors) {
            if (d.getContact().equals(contact)) {
                toRemove = d;
                break;
            }
        }
        if (toRemove != null) {
            donors.remove(toRemove);
            registeredContacts.remove(contact);
            String bg = toRemove.getBloodGroup();
            if (bloodGroupMap.containsKey(bg)) {
                bloodGroupMap.get(bg).remove(toRemove);
            }
            logActivity("Donor removed: " + toRemove.getName());
            return true;
        }
        return false;
    }

    public HashMap<String, Integer> getBloodGroupStats() {
        HashMap<String, Integer> stats = new HashMap<>();
        for (String bg : bloodGroupMap.keySet()) {
            stats.put(bg, bloodGroupMap.get(bg).size());
        }
        return stats;
    }

    public int getTotalCount() { return donors.size(); }

    public LinkedList<String> getRecentActivities() { return recentActivities; }

    private void logActivity(String activity) {
        recentActivities.addFirst(activity);
        if (recentActivities.size() > 20) {
            recentActivities.removeLast();
        }
    }

    public Iterator<T> iterator() {
        return donors.iterator();
    }
}
