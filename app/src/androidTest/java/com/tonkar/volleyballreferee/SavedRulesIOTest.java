package com.tonkar.volleyballreferee;

import androidx.test.rule.ActivityTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.tonkar.volleyballreferee.business.data.StoredRules;
import com.tonkar.volleyballreferee.interfaces.data.StoredRulesService;
import com.tonkar.volleyballreferee.business.rules.Rules;
import com.tonkar.volleyballreferee.ui.MainActivity;

import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SavedRulesIOTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void save() {
        StoredRulesService storedRulesService = new StoredRules(mActivityRule.getActivity().getApplicationContext());

        Rules rules = storedRulesService.createRules();
        rules.setName("Test Rules 1");
        rules.setUserId("66bgghvgh55@google");
        rules.setDate(34567654L);
        rules.setSetsPerGame(15);
        rules.setPointsPerSet(22);
        rules.setTieBreakInLastSet(false);
        rules.setPointsInTieBreak(19);
        rules.setTwoPointsDifference(false);
        rules.setSanctionsEnabled(true);
        rules.setTeamTimeoutsEnabled(false);
        rules.setTeamTimeoutsPerSet(6);
        rules.setTeamTimeoutDuration(1234);
        rules.setTechnicalTimeoutsEnabled(true);
        rules.setTechnicalTimeoutDuration(756);
        rules.setGameIntervalsEnabled(true);
        rules.setGameIntervalDuration(532);
        rules.setTeamSubstitutionsPerSet(0);
        rules.setBeachCourtSwitchesEnabled(false);
        rules.setBeachCourtSwitchFrequency(6);
        rules.setBeachCourtSwitchFrequencyTieBreak(1);
        rules.setCustomConsecutiveServesPerPlayer(77);

        storedRulesService.saveRules(rules);

        rules = storedRulesService.createRules();
        rules.setName("Test Rules 2");
        rules.setUserId("byg765bvg66v@facebook");
        rules.setDate(494030L);
        rules.setSetsPerGame(1);
        rules.setPointsPerSet(99);
        rules.setTieBreakInLastSet(true);
        rules.setPointsInTieBreak(0);
        rules.setTwoPointsDifference(true);
        rules.setSanctionsEnabled(false);
        rules.setTeamTimeoutsEnabled(true);
        rules.setTeamTimeoutsPerSet(9);
        rules.setTeamTimeoutDuration(765);
        rules.setTechnicalTimeoutsEnabled(false);
        rules.setTechnicalTimeoutDuration(40);
        rules.setGameIntervalsEnabled(false);
        rules.setGameIntervalDuration(90);
        rules.setTeamSubstitutionsPerSet(2);
        rules.setBeachCourtSwitchesEnabled(true);
        rules.setBeachCourtSwitchFrequency(8);
        rules.setBeachCourtSwitchFrequencyTieBreak(9);
        rules.setCustomConsecutiveServesPerPlayer(10);

        storedRulesService.saveRules(rules);
    }

    @Test
    public void writeThenRead() {
        StoredRulesService storedRulesService = new StoredRules(mActivityRule.getActivity().getApplicationContext());

        List<Rules> expectedList = new ArrayList<>();
        expectedList.add(storedRulesService.getRules("Test Rules 1"));
        expectedList.add(storedRulesService.getRules("Test Rules 2"));

        List<Rules> actualList = new ArrayList<>();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            StoredRules.writeRulesStream(outputStream, expectedList);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            actualList = StoredRules.readRulesStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedList, actualList);
        assertNotEquals(0, actualList.size());
    }

    @Test
    public void clear() {
        StoredRulesService storedRulesService = new StoredRules(mActivityRule.getActivity().getApplicationContext());
        storedRulesService.deleteAllRules();
    }
}
