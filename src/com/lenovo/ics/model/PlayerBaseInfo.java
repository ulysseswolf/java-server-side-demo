package com.lenovo.ics.model;

import java.io.Serializable;

public class PlayerBaseInfo implements Serializable {

    private static final long serialVersionUID = 5009693592904749380L;
    protected String playerId;
    protected String teamId;
    protected String team;
    protected String firstName;
    protected String lastName;
    protected String cnFirstName;
    protected int Star;
    protected String displayName;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCnFirstName() {
        return displayName==null?cnFirstName:displayName;
    }

    public void setCnFirstName(String cnFirstName) {
        this.cnFirstName = displayName==null?cnFirstName:displayName;
    }

	public String getTeam() {
		return team;
	}

	public void setTeam(String team) {
		this.team = team;
	}

	public int getStar() {
		return Star;
	}

	public void setStar(int star) {
		Star = star;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
    

}
