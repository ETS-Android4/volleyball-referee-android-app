package com.tonkar.volleyballreferee.api;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class ApiFriendsAndRequests {

    @SerializedName("friends")
    private List<ApiFriend>        friends;
    @SerializedName("receivedFriendRequests")
    private List<ApiFriendRequest> receivedFriendRequests;
    @SerializedName("sentFriendRequests")
    private List<ApiFriendRequest> sentFriendRequests;

    public ApiFriendsAndRequests() {
        this.friends = new ArrayList<>();
        this.receivedFriendRequests = new ArrayList<>();
        this.sentFriendRequests = new ArrayList<>();
    }
}
