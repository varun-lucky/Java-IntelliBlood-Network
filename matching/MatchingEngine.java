package com.intelliblood.matching;

import com.intelliblood.donor.BloodCompatibility;
import com.intelliblood.donor.Donor;
import com.intelliblood.donor.DonorRepository;
import com.intelliblood.donor.NoDonorFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

public class MatchingEngine implements Matchable {

    private DonorRepository<Donor> repository;
    private ArrayList<Donor> lastResult;
    private final LinkedList<String[]> requestQueue;

    public MatchingEngine(DonorRepository<Donor> repository) {
        this.repository = repository;
        this.lastResult = new ArrayList<>();
        this.requestQueue = new LinkedList<>();
    }

    public boolean isEligible(Donor d) {
        return EligibilityChecker.check(d);
    }

    public ArrayList<Donor> findMatches(String bloodGroup, String city) throws NoDonorFoundException {
        ArrayList<String> compatibleGroups = BloodCompatibility.getCompatibleDonorGroups(bloodGroup);
        ArrayList<Donor> matched = new ArrayList<>();

        for (String bg : compatibleGroups) {
            ArrayList<Donor> candidates = repository.getByBloodGroup(bg);
            for (Donor d : candidates) {
                if (isEligible(d)) {
                    matched.add(d);
                }
            }
        }

        if (matched.isEmpty()) {
            throw new NoDonorFoundException("No compatible eligible donors found for blood group: " + bloodGroup
                    + "\nCompatible groups searched: " + compatibleGroups.toString());
        }

        Collections.sort(matched, (a, b) -> {
            boolean aCity = a.getCity().equalsIgnoreCase(city);
            boolean bCity = b.getCity().equalsIgnoreCase(city);
            if (aCity && !bCity)
                return -1;
            if (!aCity && bCity)
                return 1;
            if (a.isVerified() && !b.isVerified())
                return -1;
            if (!a.isVerified() && b.isVerified())
                return 1;
            return b.getDaysSinceLastDonation() - a.getDaysSinceLastDonation();
        });

        lastResult = matched;
        return matched;
    }

    public void findMatchesInBackground(String bloodGroup, String city, MatchCallback callback) {
        synchronized (requestQueue) {
            requestQueue.add(new String[] { bloodGroup, city });
            requestQueue.notifyAll();
        }

        Thread workerThread = new Thread(new Runnable() {
            public void run() {
                String[] request = null;
                synchronized (requestQueue) {
                    while (requestQueue.isEmpty()) {
                        try {
                            requestQueue.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    request = requestQueue.poll();
                }

                if (request != null) {
                    try {
                        Thread.sleep(700);
                        ArrayList<Donor> result = findMatches(request[0], request[1]);
                        callback.onSuccess(result);
                    } catch (NoDonorFoundException e) {
                        callback.onError(e.getMessage());
                    } catch (InterruptedException e) {
                        callback.onError("Search was interrupted.");
                    }
                }
            }
        });

        workerThread.setDaemon(true);
        workerThread.start();
    }

    public ArrayList<Donor> getLastResult() {
        return lastResult;
    }

    public interface MatchCallback {
        void onSuccess(ArrayList<Donor> donors);

        void onError(String message);
    }
}
