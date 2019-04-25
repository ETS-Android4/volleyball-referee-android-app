package com.tonkar.volleyballreferee.business.team;

import android.util.Log;

import com.tonkar.volleyballreferee.api.ApiPlayer;
import com.tonkar.volleyballreferee.interfaces.GameType;
import com.tonkar.volleyballreferee.interfaces.Tags;
import com.tonkar.volleyballreferee.interfaces.team.TeamType;

import java.util.Set;
import java.util.TreeSet;

public class IndoorTeamDefinition extends TeamDefinition {

    public IndoorTeamDefinition(final String createdBy, final GameType gameType, final TeamType teamType) {
        super(createdBy, gameType, teamType);
    }

    // For GSON Deserialization
    public IndoorTeamDefinition() {
        this("", GameType.INDOOR, TeamType.HOME);
    }

    @Override
    public void removePlayer(final int number) {
        if (isLibero(number)) {
            removeLibero(number);
        }
        if (isCaptain(number)) {
            super.setCaptain(-1);
        }
        super.removePlayer(number);
    }

    @Override
    public boolean isLibero(int number) {
        return getLiberos().contains(new ApiPlayer(number, ""));
    }

    @Override
    public boolean canAddLibero() {
        int numberOfPlayers = getNumberOfPlayers();
        int numberOfLiberos = getLiberos().size();
        boolean can;

        if (numberOfPlayers < 7) {
            can = false;
        } else if (numberOfPlayers < 8) {
            can = numberOfLiberos < 1;
        } else {
            can = numberOfLiberos < 2;
        }

        return can;
    }

    @Override
    public void addLibero(final int number) {
        if (canAddLibero() && hasPlayer(number)) {
            Log.i(Tags.TEAM, String.format("Add player #%d as libero of %s team", number, getTeamType().toString()));
            getLiberos().add(new ApiPlayer(number, ""));
        }
    }

    @Override
    public void removeLibero(final int number) {
        if (hasPlayer(number) && isLibero(number)) {
            Log.i(Tags.TEAM, String.format("Remove player #%d as libero from %s team", number, getTeamType().toString()));
            getLiberos().remove(new ApiPlayer(number, ""));
        }
    }

    @Override
    public void setCaptain(int number) {
        if (hasPlayer(number)) {
            Log.i(Tags.TEAM, String.format("Set player #%d as captain of %s team", number, getTeamType().toString()));
            super.setCaptain(number);
        }
    }

    @Override
    public boolean isCaptain(int number) {
        return number == getCaptain();
    }

    @Override
    public Set<Integer> getPossibleCaptains() {
        Set<Integer> possibleCaptains = new TreeSet<>();

        for (ApiPlayer player : getPlayers()) {
            if (!isLibero(player.getNum())) {
                possibleCaptains.add(player.getNum());
            }
        }

        return possibleCaptains;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj == this) {
            result = true;
        } else if (obj instanceof IndoorTeamDefinition) {
            IndoorTeamDefinition other = (IndoorTeamDefinition) obj;
            result = super.equals(other);
        }

        return result;
    }

}
