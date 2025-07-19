package com.example.demo.model.domain.request;

import java.io.Serial;
import java.io.Serializable;

public class TeamQuitRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private long teamId;

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }
}
