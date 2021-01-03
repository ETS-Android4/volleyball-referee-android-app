package com.tonkar.volleyballreferee;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.tonkar.volleyballreferee.engine.game.GameType;
import com.tonkar.volleyballreferee.engine.stored.StoredLeaguesManager;
import com.tonkar.volleyballreferee.engine.stored.StoredLeaguesService;
import com.tonkar.volleyballreferee.engine.stored.dto.ApiLeague;
import com.tonkar.volleyballreferee.engine.stored.dto.ApiSelectedLeague;
import com.tonkar.volleyballreferee.engine.stored.dto.ApiUserSummary;
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
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StoredLeaguesIOTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void save() {
        ApiSelectedLeague selectedLeague1 = new ApiSelectedLeague();
        selectedLeague1.setId(UUID.randomUUID().toString());
        selectedLeague1.setCreatedBy(ApiUserSummary.VBR_USER_ID);
        selectedLeague1.setCreatedAt(System.currentTimeMillis());
        selectedLeague1.setUpdatedAt(System.currentTimeMillis());
        selectedLeague1.setKind(GameType.INDOOR);
        selectedLeague1.setName("Test League");
        selectedLeague1.setDivision("Test division");

        ApiSelectedLeague selectedLeague2 = new ApiSelectedLeague();
        selectedLeague2.setId(UUID.randomUUID().toString());
        selectedLeague2.setCreatedBy(ApiUserSummary.VBR_USER_ID);
        selectedLeague2.setCreatedAt(System.currentTimeMillis());
        selectedLeague2.setUpdatedAt(System.currentTimeMillis());
        selectedLeague2.setKind(GameType.INDOOR);
        selectedLeague2.setName("Test League Bis");
        selectedLeague2.setDivision("Test division Bis");

        StoredLeaguesService storedLeaguesService = new StoredLeaguesManager(mActivityRule.getActivity().getApplicationContext());
        storedLeaguesService.createAndSaveLeagueFrom(selectedLeague1);
        storedLeaguesService.createAndSaveLeagueFrom(selectedLeague2);
    }

    @Test
    public void writeThenRead() {
        StoredLeaguesService storedLeaguesService = new StoredLeaguesManager(mActivityRule.getActivity().getApplicationContext());

        List<ApiLeague> expectedList = new ArrayList<>();
        expectedList.add(storedLeaguesService.getLeague(GameType.INDOOR, "Test League"));
        expectedList.add(storedLeaguesService.getLeague(GameType.INDOOR, "Test League Bis"));
        List<ApiLeague> actualList = new ArrayList<>();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            StoredLeaguesManager.writeLeaguesStream(outputStream, expectedList);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            actualList = StoredLeaguesManager.readLeaguesStream(inputStream);

        } catch (IOException e) {
            e.printStackTrace();
        }

        assertEquals(expectedList, actualList);
        assertNotEquals(0, actualList.size());
    }
}
