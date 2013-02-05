package com.lenovo.ics.model;

public class NbaPlayerLiveInfo extends PlayerBaseInfo implements Comparable<NbaPlayerLiveInfo> {

	private static final long serialVersionUID = 3144824730812272926L;
	// 比赛ID
    private String matchId;
    // 上场与否 1表示上场
    private String games;
    // 首发与否 1表示是
    private String started;
    /**
     * 位置
     * 
     * <pre>
     * Guard-Forward ： 后卫|前锋
     * Center：中锋 
     * Forward：前锋 
     * Forward-Center:前锋|中锋 
     * Guard: 后卫 
     * Point Guard:控球后卫 
     * Power Forward :大前锋 
     * Shooting Guard:得分后卫
     * Small Forward : 小前锋
     * </pre>
     */
    private String position;
    // 上场时间
    private String minutes;
    // 投篮成功次数
    private String fieldMade;
    // 投篮总数
    private String fieldAtt;
    // 罚球成功次数
    private String freeMade;
    // 罚球次数
    private String freeAtt;
    // 3分成功次数
    private String threeMade;
    // 3分总数
    private String threeAtt;
    // 总得分
    private String points;
    //总篮板
    private int rebounds;
    // 前场篮板
    private String off;
    // 后场篮板
    private String def;
    // 助攻
    private String ass;
    // 抢断
    private String ste;
    // 盖帽
    private String blo;
    // 失误
    private String turn;
    // 犯规
    private String perFouls;
    // 技术犯规
    private String tecFouls;
    // 恶意犯规
    private String flag;
    // 被逐出场
    private String ejections;
    
    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getGames() {
        return games;
    }

    public void setGames(String games) {
        this.games = games;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
    }

    public String getFieldMade() {
        return fieldMade;
    }

    public void setFieldMade(String fieldMade) {
        this.fieldMade = fieldMade;
    }

    public String getFieldAtt() {
        return fieldAtt;
    }

    public void setFieldAtt(String fieldAtt) {
        this.fieldAtt = fieldAtt;
    }

    public String getFreeMade() {
        return freeMade;
    }

    public void setFreeMade(String freeMade) {
        this.freeMade = freeMade;
    }

    public String getFreeAtt() {
        return freeAtt;
    }

    public void setFreeAtt(String freeAtt) {
        this.freeAtt = freeAtt;
    }

    public String getThreeMade() {
        return threeMade;
    }

    public void setThreeMade(String threeMade) {
        this.threeMade = threeMade;
    }

    public String getThreeAtt() {
        return threeAtt;
    }

    public void setThreeAtt(String threeAtt) {
        this.threeAtt = threeAtt;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getOff() {
        return off;
    }

    public void setOff(String off) {
        this.off = off;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getAss() {
        return ass;
    }

    public void setAss(String ass) {
        this.ass = ass;
    }

    public String getSte() {
        return ste;
    }

    public void setSte(String ste) {
        this.ste = ste;
    }

    public String getBlo() {
        return blo;
    }

    public void setBlo(String blo) {
        this.blo = blo;
    }

    public String getTurn() {
        return turn;
    }

    public void setTurn(String turn) {
        this.turn = turn;
    }

    public String getPerFouls() {
        return perFouls;
    }

    public void setPerFouls(String perFouls) {
        this.perFouls = perFouls;
    }

    public String getTecFouls() {
        return tecFouls;
    }

    public void setTecFouls(String tecFouls) {
        this.tecFouls = tecFouls;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getEjections() {
        return ejections;
    }

    public void setEjections(String ejections) {
        this.ejections = ejections;
    }

	public int getRebounds() {
		return rebounds;
	}

	public void setRebounds(int rebounds) {
		this.rebounds = rebounds;
	}

	@Override
	public int compareTo(NbaPlayerLiveInfo pl) {
		Integer p1 = Integer.parseInt(points);
		Integer p2 = Integer.parseInt(pl.getPoints());
		if (p1 > p2) {
			return -1;
		} else if (p1 < p2) {
			return 1;
		} else if(rebounds > pl.getRebounds()) { 
			return -1;
		} else if (rebounds < pl.getRebounds()) {
			return 1;
		} else {
			Integer ass1 = Integer.parseInt(ass);
			Integer ass2 = Integer.parseInt(pl.getAss());
			if (ass1 > ass2) {
				return -1;
			} else if (ass1 < ass2) {
				return 1;
			}
		}
		return 0;
	}
    
}
