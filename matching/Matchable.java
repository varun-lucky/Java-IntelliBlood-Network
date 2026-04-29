package com.intelliblood.matching;

import com.intelliblood.donor.Donor;
import com.intelliblood.donor.NoDonorFoundException;
import java.util.ArrayList;

public interface Matchable {
    ArrayList<Donor> findMatches(String bloodGroup, String city) throws NoDonorFoundException;
    boolean isEligible(Donor d);
}
