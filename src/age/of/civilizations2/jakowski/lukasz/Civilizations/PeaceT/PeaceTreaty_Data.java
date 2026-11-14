/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  age.of.civilizations2.jakowski.lukasz.CFG
 *  age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_Civs
 *  age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_Demands
 *  age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_DrawData
 *  age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_GameData
 *  age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.Vassal.PeaceTreaty_ReleaseableVassals
 *  age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.Vassal.PeaceTreaty_ReleaseableVassals_TakeConrol
 *  age.of.civilizations2.jakowski.lukasz.GameValues.GameValues
 *  age.of.civilizations2.jakowski.lukasz.MapA.Distance
 */
package age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_Civs;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_Demands;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_DrawData;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_GameData;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.Vassal.PeaceTreaty_ReleaseableVassals;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.Vassal.PeaceTreaty_ReleaseableVassals_TakeConrol;
import age.of.civilizations2.jakowski.lukasz.GameValues.GameValues;
import age.of.civilizations2.jakowski.lukasz.MapA.Distance;
import java.util.ArrayList;
import java.util.List;

public class PeaceTreaty_Data {
    public PeaceTreaty_GameData peaceTreatyGD = new PeaceTreaty_GameData();
    public List<PeaceTreaty_DrawData> drawProvOwners = new ArrayList<PeaceTreaty_DrawData>();
    public List<Integer> provsLeftToTake = new ArrayList<Integer>();
    public int iProvsLeftToTakeSize = 0;
    public boolean scoreCountDefenders = false;
    public int brushCivID = -1;
    public int playerTurnID = 0;
    public int iLastTakenID = -1;
    public static final float VASSALIZE_COST = 0.4f;
    public static final float WAR_REPARATIONS_COST = 0.1f;

    public PeaceTreaty_Data() {
    }

    public PeaceTreaty_Data(PeaceTreaty_GameData nPeaceTreaty) {
        this.peaceTreatyGD = nPeaceTreaty;
        this.brushCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
        this.playerTurnID = CFG.PLAYER_TURN_ID;
        this.prepareProvinceData(false);
        this.prepareDemansVassalsData();
    }

    public PeaceTreaty_Data(int iWarID, boolean scoreCountDefenders) {
        int i;
        ArrayList<Boolean> addDefender = new ArrayList<Boolean>();
        ArrayList<Boolean> addAggressor = new ArrayList<Boolean>();
        for (i = 0; i < CFG.core.getWar(iWarID).getDefendersSize(); ++i) {
            addDefender.add(true);
        }
        for (i = 0; i < CFG.core.getWar(iWarID).getAggressorsSize(); ++i) {
            addAggressor.add(true);
        }
        this.initPeaceTreatyData(iWarID, addDefender, addAggressor, scoreCountDefenders);
    }

    public PeaceTreaty_Data(int iWarID, List<Boolean> addDefender, List<Boolean> addAggressor, boolean scoreCountDefenders) {
        this.initPeaceTreatyData(iWarID, addDefender, addAggressor, scoreCountDefenders);
    }

    public final void AIUseVictoryPoints() {
        try {
            if (this.iProvsLeftToTakeSize > 0) {
                int i;
                int iBestCivID = -1;
                int tBestPoints = -1;
                for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                    try {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft > tBestPoints) {
                            iBestCivID = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID;
                            tBestPoints = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft;
                            continue;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft != tBestPoints || CFG.oR.nextInt(100) >= 50) continue;
                        iBestCivID = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID;
                        tBestPoints = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft;
                        continue;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                    try {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft > tBestPoints) {
                            iBestCivID = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID;
                            tBestPoints = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft;
                            continue;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft != tBestPoints || CFG.oR.nextInt(100) >= 50) continue;
                        iBestCivID = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID;
                        tBestPoints = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft;
                        continue;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                if (iBestCivID > 0 && tBestPoints > 0 && !CFG.core.getCiv(iBestCivID).getIsPlayer()) {
                    this.AI_UseVictoryPoints_CivID(iBestCivID, tBestPoints);
                }
            }
        }
        catch (StackOverflowError stackOverflowError) {
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public final void AI_UseVictoryPoints_CivID_TakeVassal(int nCivID, int pointsLeft, boolean clearPoints) {
        block19: {
            try {
                int i;
                int o;
                int i2;
                ArrayList<Integer> canVassalizeCivs = new ArrayList<Integer>();
                boolean doneCheck = false;
                for (i2 = 0; i2 < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i2) {
                    try {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID != nCivID) continue;
                        for (o = 0; o < this.peaceTreatyGD.civsDemandsAggressors.size(); ++o) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)o)).iWillBecomeVassalOfCivID >= 0 || this.getVassalization_Cost(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)o)).iCivID) > pointsLeft) continue;
                            canVassalizeCivs.add(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)o)).iCivID);
                        }
                        doneCheck = true;
                        break;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                if (!doneCheck) {
                    try {
                        for (i2 = 0; i2 < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i2) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID != nCivID) continue;
                            for (o = 0; o < this.peaceTreatyGD.civsDemandsDefenders.size(); ++o) {
                                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)o)).iWillBecomeVassalOfCivID >= 0 || this.getVassalization_Cost(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)o)).iCivID) > pointsLeft) continue;
                                canVassalizeCivs.add(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)o)).iCivID);
                            }
                            doneCheck = true;
                            break;
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                if (!canVassalizeCivs.isEmpty()) {
                    this.takeVassalize((Integer)canVassalizeCivs.get(CFG.oR.nextInt(canVassalizeCivs.size())), nCivID, nCivID);
                    this.AIUseVictoryPoints();
                    return;
                }
                if (!clearPoints) break block19;
                for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                    try {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nCivID) continue;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft = 0;
                        this.AIUseVictoryPoints();
                        return;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                    try {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nCivID) continue;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft = 0;
                        this.AIUseVictoryPoints();
                        return;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
    }

    public final void AI_UseVictoryPoints_CivID(int nCivID, int pointsLeft) {
        try {
            int i;
            ArrayList<Float> lScores = new ArrayList<Float>();
            ArrayList<Boolean> lNeigh = new ArrayList<Boolean>();
            ArrayList<Integer> toTake = new ArrayList<Integer>();
            boolean canTakeNieghProvince = false;
            float maxDistance = 1.0E-4f;
            int capitalProv = CFG.core.getCiv(nCivID).getCapitalProvID();

            for (int i2 = 0; i2 < this.iProvsLeftToTakeSize; ++i2) {
                int pID = this.provsLeftToTake.get(i2);
                if (pointsLeft < this.drawProvOwners.get(pID).iProvinceValue) continue;

                float dist = (capitalProv >= 0 && pID >= 0) ? Distance.getDistanceFromCapital(capitalProv, pID) : 1.0f;
                maxDistance = Math.max(maxDistance, dist);

                // 1. Reclaiming original cores / true owner
                if (CFG.core.getProv(pID).getTrueOwnerOfProv() == nCivID) {
                    lScores.add(Float.valueOf(this.AI_UseVictoryPoints_CivID_Score(nCivID, pID, 12.0f)));
                    lNeigh.add(true);
                    toTake.add(pID);
                    canTakeNieghProvince = true;
                    continue;
                }

                // 2. Direct land border to our owned or already demanded territory
                int landBordersOwn = 0;
                boolean touchesLastTakenConfirmed = false;
                int neighSize = CFG.core.getProv(pID).getNeighProvincesSize();

                for (int j = 0; j < neighSize; ++j) {
                    int nID = CFG.core.getProv(pID).getNeighProvinces(j);
                    if (nID < 0 || CFG.core.getProv(nID).getWastelandLvl() >= 0 || CFG.core.getProv(nID).getSeaProv()) continue;
                    if (this.drawProvOwners.get(nID).iCivID == nCivID) {
                        landBordersOwn++;
                        if (this.iLastTakenID >= 0 && nID == this.iLastTakenID) {
                            touchesLastTakenConfirmed = true;
                        }
                    }
                }

                if (landBordersOwn > 0) {
                    boolean hasCore = CFG.core.getProv(pID).getCores().getHaveACore(nCivID);
                    float baseMod = hasCore ? 8.0f : 5.0f;
                    // Compactness bonus: filling pockets (>=3 borders) or smoothing lines (2 borders)
                    float compactnessBonus = (landBordersOwn >= 3 ? 4.0f : (landBordersOwn == 2 ? 2.0f : 0.5f));
                    if (touchesLastTakenConfirmed) {
                        compactnessBonus += 1.5f;
                    }

                    lScores.add(Float.valueOf(this.AI_UseVictoryPoints_CivID_Score(nCivID, pID, baseMod + compactnessBonus)));
                    lNeigh.add(true);
                    toTake.add(pID);
                    canTakeNieghProvince = true;
                    continue;
                }

                // 3. Core province without direct land border yet
                if (CFG.core.getProv(pID).getCores().getHaveACore(nCivID)) {
                    lScores.add(Float.valueOf(this.AI_UseVictoryPoints_CivID_Score(nCivID, pID, 2.0f)));
                    lNeigh.add(true);
                    toTake.add(pID);
                    canTakeNieghProvince = true;
                    continue;
                }

                // 4. Coastal / Sea connectivity (ONLY if sharing the same sea body!)
                boolean sharesSeaWithCiv = false;
                int seaSize = CFG.core.getProv(pID).getNeighSeaProvincesSize();
                if (seaSize > 0) {
                    for (int s = 0; s < seaSize; s++) {
                        int seaID = CFG.core.getProv(pID).getNeighSeaProvinces(s);
                        if (seaID >= 0) {
                            int seaLandNeighs = CFG.core.getProv(seaID).getNeighProvincesSize();
                            for (int sl = 0; sl < seaLandNeighs; sl++) {
                                int slProvID = CFG.core.getProv(seaID).getNeighProvinces(sl);
                                if (slProvID >= 0 && this.drawProvOwners.get(slProvID).iCivID == nCivID) {
                                    sharesSeaWithCiv = true;
                                    break;
                                }
                            }
                        }
                        if (sharesSeaWithCiv) break;
                    }
                }

                if (sharesSeaWithCiv) {
                    lScores.add(Float.valueOf(this.AI_UseVictoryPoints_CivID_Score(nCivID, pID, 1.25f)));
                    lNeigh.add(true);
                    toTake.add(pID);
                    canTakeNieghProvince = true;
                    continue;
                }

                // Disconnected inland/distant enclave: strongly discouraged
                lScores.add(Float.valueOf(this.AI_UseVictoryPoints_CivID_Score(nCivID, pID, 0.005f)));
                lNeigh.add(false);
                toTake.add(pID);
            }

            if (lNeigh.isEmpty() || toTake.isEmpty()) {
                this.AI_UseVictoryPoints_CivID_TakeVassal(nCivID, pointsLeft, true);
                return;
            }
            if (!canTakeNieghProvince) {
                this.AI_UseVictoryPoints_CivID_TakeVassal(nCivID, pointsLeft, true);
                return;
            }

            // Distance weighting: scale all candidates including index 0
            for (i = lScores.size() - 1; i >= 0; --i) {
                float distFactor = 1.0f;
                if (maxDistance > 1.0f && capitalProv >= 0) {
                    float dist = Distance.getDistanceFromCapital(capitalProv, toTake.get(i));
                    distFactor = 0.75f + 0.25f * (1.0f - dist / maxDistance);
                }
                float lastTakenPen = (this.iLastTakenID == toTake.get(i)) ? 0.05f : 1.0f;
                lScores.set(i, Float.valueOf(lScores.get(i).floatValue() * distFactor * lastTakenPen));
            }

            // Find best candidate
            int tBest = 0;
            for (i = lScores.size() - 1; i > 0; --i) {
                if (lScores.get(tBest).floatValue() < lScores.get(i).floatValue()) {
                    tBest = i;
                }
            }

            if (lNeigh.get(tBest).booleanValue()) {
                if (!this.takeProvince(toTake.get(tBest), nCivID, nCivID)) {
                    for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).iCivID != nCivID) continue;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).iVictoryPointsLeft = 0;
                        this.AIUseVictoryPoints();
                        return;
                    }
                    for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).iCivID != nCivID) continue;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).iVictoryPointsLeft = 0;
                        this.AIUseVictoryPoints();
                        return;
                    }
                }
            } else {
                // Do not grab disconnected enclaves - prefer vassalization
                this.AI_UseVictoryPoints_CivID_TakeVassal(nCivID, pointsLeft, true);
                return;
            }
        }
        catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    public final float AI_UseVictoryPoints_CivID_Score(int nCivID, int nProvinceID, float modifier) {
        int neigh_OwnProvinces = 0;
        int neigh_OtherCivsProvinces = 0;
        int enclosedEnemyPockets = 0;
        try {
            for (int i = 0; i < CFG.core.getProv(nProvinceID).getNeighProvincesSize(); ++i) {
                int neighID = CFG.core.getProv(nProvinceID).getNeighProvinces(i);
                if (neighID < 0 || CFG.core.getProv(neighID).getWastelandLvl() >= 0 || CFG.core.getProv(neighID).getCivId() <= 0) continue;
                if (this.drawProvOwners.get(neighID).iCivID == nCivID) {
                    ++neigh_OwnProvinces;
                    continue;
                }
                ++neigh_OtherCivsProvinces;

                // Anti-hole pocket detection
                int otherNeighOwn = 0;
                int otherNeighTotal = 0;
                for (int k = 0; k < CFG.core.getProv(neighID).getNeighProvincesSize(); ++k) {
                    int onID = CFG.core.getProv(neighID).getNeighProvinces(k);
                    if (onID < 0 || CFG.core.getProv(onID).getWastelandLvl() >= 0 || CFG.core.getProv(onID).getSeaProv()) continue;
                    otherNeighTotal++;
                    if (this.drawProvOwners.get(onID).iCivID == nCivID || onID == nProvinceID) {
                        otherNeighOwn++;
                    }
                }
                if (otherNeighTotal > 1 && (float)otherNeighOwn / (float)otherNeighTotal >= 0.70f) {
                    enclosedEnemyPockets++;
                }
            }
            if (CFG.core.getProv(nProvinceID).getNeighSeaProvincesSize() > 0) {
                ++neigh_OwnProvinces;
            }
            neigh_OtherCivsProvinces = Math.max(1, neigh_OtherCivsProvinces);
        }
        catch (Exception ex) {
            CFG.exceptionStack(ex);
        }

        float cohesionRatio = (float)neigh_OwnProvinces / (float)(neigh_OwnProvinces + neigh_OtherCivsProvinces);
        float popFactor = (float)CFG.core.getProv(nProvinceID).getPop().getPops() / (float)Math.max(1, CFG.core.getGameScenars().getScenario_StartingPopulation());
        float ecoFactor = (float)CFG.core.getProv(nProvinceID).getEco() / (float)Math.max(1, CFG.core.getGameScenars().getScenario_StartingEconomy());
        float devFactor = CFG.core.getProv(nProvinceID).getDeveLvl();

        return modifier 
             + ((float)neigh_OwnProvinces * (modifier * 0.20f)) 
             + (modifier * cohesionRatio * 1.5f) 
             + ((float)enclosedEnemyPockets * 3.0f)
             + (0.125f * popFactor) 
             + (0.05f * ecoFactor) 
             + (0.0075f * devFactor);
    }

    public final void addProvincesLeftToTake(int nProvinceID) {
        for (int i = 0; i < this.iProvsLeftToTakeSize; ++i) {
            if (this.provsLeftToTake.get(i) != nProvinceID) continue;
            return;
        }
        this.provsLeftToTake.add(nProvinceID);
        this.iProvsLeftToTakeSize = this.provsLeftToTake.size();
    }

    public final void removeProvincesLeftToTake(int nProvinceID) {
        for (int i = 0; i < this.iProvsLeftToTakeSize; ++i) {
            if (this.provsLeftToTake.get(i) != nProvinceID) continue;
            this.provsLeftToTake.remove(i);
            this.iProvsLeftToTakeSize = this.provsLeftToTake.size();
            return;
        }
    }

    private final void initPeaceTreatyData(int iWarID, List<Boolean> addDefender, List<Boolean> addAggressor, boolean scoreCountDefenders) {
        try {
            this.peaceTreatyGD.iWarID = iWarID;
            this.peaceTreatyGD.WAR_TAG = CFG.core.getWar((int)iWarID).WAR_TAG;
            this.scoreCountDefenders = scoreCountDefenders;
            if (CFG.core.getWar((int)iWarID).rebelsWar) {
                int i;
                for (i = 0; i < CFG.core.getWar(iWarID).getDefendersSize(); ++i) {
                    try {
                        if (!addDefender.get(i).booleanValue()) continue;
                        this.peaceTreatyGD.civsDataDefenders.add(CFG.core.getWar(iWarID).getDefenders_ProvincesLost(i, addDefender, addAggressor));
                        if (CFG.core.getWar(iWarID).getWarScore_DefendersInProvinceValue_OnlyPositive(i, addDefender, addAggressor) > 0 && i == 0) {
                            this.peaceTreatyGD.civsDemandsDefenders.add(new PeaceTreaty_Demands(CFG.core.getWar(iWarID).getDefenderID(i).getCivID(), 99999));
                            continue;
                        }
                        this.peaceTreatyGD.civsDemandsDefenders.add(new PeaceTreaty_Demands(CFG.core.getWar(iWarID).getDefenderID(i).getCivID(), 0));
                        continue;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                for (i = 0; i < CFG.core.getWar(iWarID).getAggressorsSize(); ++i) {
                    try {
                        if (!addAggressor.get(i).booleanValue()) continue;
                        this.peaceTreatyGD.civsDataAggressors.add(CFG.core.getWar(iWarID).getAggressors_ProvincesLost(i, addDefender, addAggressor));
                        if (CFG.core.getWar(iWarID).getWarScore_AggressorsInProvinceValue_OnlyPositive(i, addDefender, addAggressor) > 0 && i == 0) {
                            this.peaceTreatyGD.civsDemandsAggressors.add(new PeaceTreaty_Demands(CFG.core.getWar(iWarID).getAggressorID(i).getCivID(), 99999));
                            continue;
                        }
                        this.peaceTreatyGD.civsDemandsAggressors.add(new PeaceTreaty_Demands(CFG.core.getWar(iWarID).getAggressorID(i).getCivID(), 0));
                        continue;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
            } else {
                int i;
                for (i = 0; i < CFG.core.getWar(iWarID).getDefendersSize(); ++i) {
                    try {
                        if (!addDefender.get(i).booleanValue()) continue;
                        this.peaceTreatyGD.civsDataDefenders.add(CFG.core.getWar(iWarID).getDefenders_ProvincesLost(i, addDefender, addAggressor));
                        this.peaceTreatyGD.civsDemandsDefenders.add(new PeaceTreaty_Demands(CFG.core.getWar(iWarID).getDefenderID(i).getCivID(), CFG.core.getWar(iWarID).getWarScore_DefendersInProvinceValue_OnlyPositive(i, addDefender, addAggressor)));
                        continue;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                for (i = 0; i < CFG.core.getWar(iWarID).getAggressorsSize(); ++i) {
                    try {
                        if (!addAggressor.get(i).booleanValue()) continue;
                        this.peaceTreatyGD.civsDataAggressors.add(CFG.core.getWar(iWarID).getAggressors_ProvincesLost(i, addDefender, addAggressor));
                        this.peaceTreatyGD.civsDemandsAggressors.add(new PeaceTreaty_Demands(CFG.core.getWar(iWarID).getAggressorID(i).getCivID(), CFG.core.getWar(iWarID).getWarScore_AggressorsInProvinceValue_OnlyPositive(i, addDefender, addAggressor)));
                        continue;
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
            }
            this.redistributePointsToLords();
            this.brushCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
            this.playerTurnID = CFG.PLAYER_TURN_ID;
            this.prepareProvinceData(true);
        }
        catch (Exception ex) {
            CFG.exceptionStack((Throwable)ex);
        }
    }

    public final void redistributePointsToLords() {
        if (GameValues.gvPeaceTreaty.REDISTRIBUTE_VASSAL_POINTS_TO_LORD) {
            int j;
            int i;
            int n = i = GameValues.gvPeaceTreaty.REDISTRIBUTE_VASSAL_POINTS_TO_LORD_WHEN_ARE_WAR_LEADERS ? 0 : 1;
            while (i < this.peaceTreatyGD.civsDemandsDefenders.size()) {
                if (!CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID).getIsPlayer() && CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID).getPuppetOfCiv() != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID) {
                    for (j = 0; j < this.peaceTreatyGD.civsDemandsDefenders.size(); ++j) {
                        if (i == j || CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID).getPuppetOfCiv() != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iCivID) continue;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft += ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft = 0;
                        break;
                    }
                }
                ++i;
            }
            int n2 = i = GameValues.gvPeaceTreaty.REDISTRIBUTE_VASSAL_POINTS_TO_LORD_WHEN_ARE_WAR_LEADERS ? 0 : 1;
            while (i < this.peaceTreatyGD.civsDemandsAggressors.size()) {
                if (!CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID).getIsPlayer() && CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID).getPuppetOfCiv() != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID) {
                    for (j = 0; j < this.peaceTreatyGD.civsDemandsAggressors.size(); ++j) {
                        if (i == j || CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID).getPuppetOfCiv() != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iCivID) continue;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft += ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft = 0;
                        break;
                    }
                }
                ++i;
            }
        }
    }

    public final void prepareProvinceData(boolean buildProvincesLost) {
        block121: {
            try {
                int i;
                this.drawProvOwners.clear();
                this.drawProvOwners = new ArrayList<PeaceTreaty_DrawData>();
                ArrayList<Boolean> tempParticipants = new ArrayList<Boolean>();
                for (i = 0; i < CFG.core.getCivsSize(); ++i) {
                    tempParticipants.add(false);
                }
                for (i = 0; i < this.peaceTreatyGD.civsDataDefenders.size(); ++i) {
                    if (((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).iCivID <= 0) continue;
                    tempParticipants.set(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).iCivID, true);
                }
                for (i = 0; i < this.peaceTreatyGD.civsDataAggressors.size(); ++i) {
                    if (((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i)).iCivID <= 0) continue;
                    tempParticipants.set(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i)).iCivID, true);
                }
                for (i = 0; i < CFG.core.getProvinSize(); ++i) {
                    int provID = CFG.core.getProv(i).getCivId();
                    if (provID >= 0) {
                        if (((Boolean)tempParticipants.get(CFG.core.getProv(i).getCivId())).booleanValue()) {
                            this.drawProvOwners.add(new PeaceTreaty_DrawData(CFG.core.getProv(i).getCivId(), CFG.core.getProvinceValue(i), false));
                            continue;
                        }
                        this.drawProvOwners.add(new PeaceTreaty_DrawData(CFG.core.getProv(i).getCivId() * -1, CFG.core.getProvinceValue(i), false));
                        continue;
                    }
                    this.drawProvOwners.add(new PeaceTreaty_DrawData(CFG.core.getProv(i).getCivId() * -1, CFG.core.getProvinceValue(i), false));
                }
                if (buildProvincesLost) {
                    int j;
                    int k;
                    boolean tAdd;
                    int u;
                    int i2;
                    try {
                        for (i = this.peaceTreatyGD.civsDataDefenders.size() - 1; i >= 0; --i) {
                            try {
                                for (int j2 = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.size() - 1; j2 >= 0; --j2) {
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.get((int)j2)).intValue()).isToTake = true;
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.get((int)j2)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.get(j2)).intValue()).getTrueOwnerOfProv();
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iTotalNumOfVicotryPoints += CFG.core.getProvinceValue(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.get(j2)).intValue());
                                }
                            }
                            catch (Exception ex) {
                                CFG.exceptionStack((Throwable)ex);
                            }
                            try {
                                for (int j3 = 0; j3 < CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID).getNumOfProvs(); ++j3) {
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iTotalNumOfVicotryPoints += CFG.core.getProvinceValue(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID).getProvID(j3));
                                }
                                continue;
                            }
                            catch (Exception ex) {
                                CFG.exceptionStack((Throwable)ex);
                            }
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                    try {
                        for (int i3 = this.peaceTreatyGD.civsDataAggressors.size() - 1; i3 >= 0; --i3) {
                            try {
                                for (int j4 = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i3)).lProvincesLost.size() - 1; j4 >= 0; --j4) {
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i3)).lProvincesLost.get((int)j4)).intValue()).isToTake = true;
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i3)).lProvincesLost.get((int)j4)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i3)).lProvincesLost.get(j4)).intValue()).getTrueOwnerOfProv();
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i3)).iTotalNumOfVicotryPoints += CFG.core.getProvinceValue(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i3)).lProvincesLost.get(j4)).intValue());
                                }
                            }
                            catch (Exception ex) {
                                CFG.exceptionStack((Throwable)ex);
                            }
                            try {
                                for (int j5 = 0; j5 < CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i3)).iCivID).getNumOfProvs(); ++j5) {
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i3)).iTotalNumOfVicotryPoints += CFG.core.getProvinceValue(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i3)).iCivID).getProvID(j5));
                                }
                                continue;
                            }
                            catch (Exception ex) {
                                CFG.exceptionStack((Throwable)ex);
                            }
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                    for (i2 = this.peaceTreatyGD.civsDemandsAggressors.size() - 1; i2 >= 0; --i2) {
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs = new ArrayList();
                        try {
                            for (int j6 = 0; j6 < CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getNumOfProvs(); ++j6) {
                                if (CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)).isOccupied()) continue;
                                for (u = 0; u < CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)).getCores().getCivsSize(); ++u) {
                                    if (CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)).getCores().getCivID(u) == CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)).getCivId() || CFG.core.getCiv(CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)).getCores().getCivID(u)).getNumOfProvs() != 0) continue;
                                    tAdd = true;
                                    for (k = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.size() - 1; k >= 0; --k) {
                                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.get((int)k)).iCivID != CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)).getCores().getCivID(u)) continue;
                                        tAdd = false;
                                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.get(k)).addProvince(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6));
                                        break;
                                    }
                                    if (!tAdd) continue;
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.add(new PeaceTreaty_ReleaseableVassals(CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)).getCores().getCivID(u), CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID).getProvID(j6)));
                                }
                            }
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                        try {
                            for (int o = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.size() - 1; o >= 0; --o) {
                                for (j = 0; j < CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivsSize(); ++j) {
                                    if (CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j) == CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue()).getCivId() || CFG.core.getCiv(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j)).getNumOfProvs() != 0) continue;
                                    tAdd = true;
                                    for (k = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.size() - 1; k >= 0; --k) {
                                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.get((int)k)).iCivID != CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j)) continue;
                                        tAdd = false;
                                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.get(k)).addProvince(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue());
                                        break;
                                    }
                                    if (!tAdd) continue;
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).lReleasableCivs.add(new PeaceTreaty_ReleaseableVassals(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j), ((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(o)).intValue()));
                                }
                            }
                            continue;
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                    }
                    for (i2 = this.peaceTreatyGD.civsDemandsDefenders.size() - 1; i2 >= 0; --i2) {
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs = new ArrayList();
                        try {
                            for (int j7 = 0; j7 < CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getNumOfProvs(); ++j7) {
                                if (CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)).isOccupied()) continue;
                                for (u = 0; u < CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)).getCores().getCivsSize(); ++u) {
                                    if (CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)).getCores().getCivID(u) == CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)).getCivId() || CFG.core.getCiv(CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)).getCores().getCivID(u)).getNumOfProvs() != 0) continue;
                                    tAdd = true;
                                    for (k = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.size() - 1; k >= 0; --k) {
                                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.get((int)k)).iCivID != CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)).getCores().getCivID(u)) continue;
                                        tAdd = false;
                                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.get(k)).addProvince(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7));
                                        break;
                                    }
                                    if (!tAdd) continue;
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.add(new PeaceTreaty_ReleaseableVassals(CFG.core.getProv(CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)).getCores().getCivID(u), CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID).getProvID(j7)));
                                }
                            }
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                        try {
                            for (int o = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.size() - 1; o >= 0; --o) {
                                for (j = 0; j < CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivsSize(); ++j) {
                                    if (CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j) == CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue()).getCivId() || CFG.core.getCiv(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j)).getNumOfProvs() != 0) continue;
                                    tAdd = true;
                                    for (k = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.size() - 1; k >= 0; --k) {
                                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.get((int)k)).iCivID != CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j)) continue;
                                        tAdd = false;
                                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.get(k)).addProvince(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue());
                                        break;
                                    }
                                    if (!tAdd) continue;
                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).lReleasableCivs.add(new PeaceTreaty_ReleaseableVassals(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue()).getCores().getCivID(j), ((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(o)).intValue()));
                                }
                            }
                            continue;
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                    }
                    try {
                        for (i2 = this.drawProvOwners.size() - 1; i2 >= 0; --i2) {
                            if (!this.drawProvOwners.get((int)i2).isToTake) continue;
                            this.provsLeftToTake.add(i2);
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                    this.iProvsLeftToTakeSize = this.provsLeftToTake.size();
                    break block121;
                }
                try {
                    int o;
                    int k;
                    int i4;
                    int k2;
                    boolean isAdded;
                    boolean addCiv;
                    int i5;
                    ArrayList<Boolean> addDefender = new ArrayList<Boolean>();
                    ArrayList<Boolean> addAggressor = new ArrayList<Boolean>();
                    for (i5 = 0; i5 < CFG.core.getWarsSize(); ++i5) {
                        if (!CFG.core.getWar((int)i5).WAR_TAG.equals(this.peaceTreatyGD.WAR_TAG)) continue;
                        this.peaceTreatyGD.iWarID = i5;
                        break;
                    }
                    for (i5 = 0; i5 < CFG.core.getWar(this.peaceTreatyGD.iWarID).getDefendersSize(); ++i5) {
                        addCiv = false;
                        try {
                            for (int j = this.peaceTreatyGD.civsDataDefenders.size() - 1; j >= 0; --j) {
                                if (CFG.core.getWar(this.peaceTreatyGD.iWarID).getDefenderID(i5).getCivID() != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)j)).iCivID) continue;
                                addCiv = true;
                                break;
                            }
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                        addDefender.add(addCiv);
                    }
                    for (i5 = 0; i5 < CFG.core.getWar(this.peaceTreatyGD.iWarID).getAggressorsSize(); ++i5) {
                        addCiv = false;
                        try {
                            for (int j = this.peaceTreatyGD.civsDataAggressors.size() - 1; j >= 0; --j) {
                                if (CFG.core.getWar(this.peaceTreatyGD.iWarID).getAggressorID(i5).getCivID() != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)j)).iCivID) continue;
                                addCiv = true;
                                break;
                            }
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                        addAggressor.add(addCiv);
                    }
                    try {
                        for (i5 = this.peaceTreatyGD.civsDataDefenders.size() - 1; i5 >= 0; --i5) {
                            try {
                                PeaceTreaty_Civs tempLost = CFG.core.getWar(this.peaceTreatyGD.iWarID).getDefenders_ProvincesLost(CFG.core.getWar(this.peaceTreatyGD.iWarID).getDefenderID_ByCivID(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i5)).iCivID), addDefender, addAggressor);
                                for (int j = tempLost.lProvincesLost.size() - 1; j >= 0; --j) {
                                    isAdded = false;
                                    for (k2 = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i5)).lProvincesLost.size() - 1; k2 >= 0; --k2) {
                                        if (!((Integer)tempLost.lProvincesLost.get(j)).equals(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i5)).lProvincesLost.get(k2))) continue;
                                        isAdded = true;
                                        break;
                                    }
                                    if (!isAdded) {
                                        ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i5)).lProvincesLost.add((Integer)tempLost.lProvincesLost.get(j));
                                        this.makeDemand_Province((Integer)tempLost.lProvincesLost.get(j), ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i5)).iCivID, ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i5)).iCivID, true);
                                    }
                                    tempLost.lProvincesLost.remove(j);
                                }
                                continue;
                            }
                            catch (Exception exr) {
                                CFG.exceptionStack((Throwable)exr);
                            }
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                    try {
                        for (int i6 = this.peaceTreatyGD.civsDataAggressors.size() - 1; i6 >= 0; --i6) {
                            try {
                                PeaceTreaty_Civs tempLost = CFG.core.getWar(this.peaceTreatyGD.iWarID).getAggressors_ProvincesLost(CFG.core.getWar(this.peaceTreatyGD.iWarID).getAggressorID_ByCivID(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i6)).iCivID), addDefender, addAggressor);
                                for (int j = tempLost.lProvincesLost.size() - 1; j >= 0; --j) {
                                    isAdded = false;
                                    for (k2 = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i6)).lProvincesLost.size() - 1; k2 >= 0; --k2) {
                                        if (!((Integer)tempLost.lProvincesLost.get(j)).equals(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i6)).lProvincesLost.get(k2))) continue;
                                        isAdded = true;
                                        break;
                                    }
                                    if (!isAdded) {
                                        ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i6)).lProvincesLost.add((Integer)tempLost.lProvincesLost.get(j));
                                        this.makeDemand_Province((Integer)tempLost.lProvincesLost.get(j), ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i6)).iCivID, ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i6)).iCivID, true);
                                    }
                                    tempLost.lProvincesLost.remove(j);
                                }
                                continue;
                            }
                            catch (Exception exr) {
                                CFG.exceptionStack((Throwable)exr);
                            }
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                    for (i4 = this.peaceTreatyGD.civsDataDefenders.size() - 1; i4 >= 0; --i4) {
                        try {
                            if (CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsAggressor(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).iCivID) || CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsDefender(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).iCivID)) {
                                for (int j = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.size() - 1; j >= 0; --j) {
                                    boolean removed;
                                    if (CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)).intValue()).isOccupied()) {
                                        if (CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsDefender(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)).intValue()).getCivId()) || CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsAggressor(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)).intValue()).getCivId())) {
                                            this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get((int)j)).intValue()).isToTake = true;
                                            this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get((int)j)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)).intValue()).getTrueOwnerOfProv();
                                            continue;
                                        }
                                        removed = false;
                                        block84: for (k = this.peaceTreatyGD.civsDemandsAggressors.size() - 1; k >= 0; --k) {
                                            for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)) continue;
                                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.remove(o);
                                                k = -1;
                                                removed = true;
                                                continue block84;
                                            }
                                        }
                                        if (!removed) {
                                            block86: for (k = this.peaceTreatyGD.civsDemandsDefenders.size() - 1; k >= 0; --k) {
                                                for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)) continue;
                                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.remove(o);
                                                    k = -1;
                                                    removed = true;
                                                    continue block86;
                                                }
                                            }
                                        }
                                        ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.remove(j);
                                        continue;
                                    }
                                    removed = false;
                                    block88: for (k = this.peaceTreatyGD.civsDemandsAggressors.size() - 1; k >= 0; --k) {
                                        for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)) continue;
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.remove(o);
                                            k = -1;
                                            removed = true;
                                            continue block88;
                                        }
                                    }
                                    if (!removed) {
                                        block90: for (k = this.peaceTreatyGD.civsDemandsDefenders.size() - 1; k >= 0; --k) {
                                            for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.get(j)) continue;
                                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.remove(o);
                                                k = -1;
                                                removed = true;
                                                continue block90;
                                            }
                                        }
                                    }
                                    ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i4)).lProvincesLost.remove(j);
                                }
                                continue;
                            }
                            this.peaceTreatyGD.civsDataDefenders.remove(i4);
                            this.peaceTreatyGD.civsDemandsDefenders.remove(i4);
                            continue;
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                    }
                    for (i4 = this.peaceTreatyGD.civsDataAggressors.size() - 1; i4 >= 0; --i4) {
                        try {
                            if (CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsAggressor(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).iCivID) || CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsDefender(((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).iCivID)) {
                                for (int j = ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.size() - 1; j >= 0; --j) {
                                    boolean removed;
                                    if (CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)).intValue()).isOccupied()) {
                                        if (CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsDefender(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)).intValue()).getCivId()) || CFG.core.getWar(this.peaceTreatyGD.iWarID).getIsAggressor(CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)).intValue()).getCivId())) {
                                            this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get((int)j)).intValue()).isToTake = true;
                                            this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get((int)j)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)).intValue()).getTrueOwnerOfProv();
                                            continue;
                                        }
                                        removed = false;
                                        block94: for (k = this.peaceTreatyGD.civsDemandsDefenders.size() - 1; k >= 0; --k) {
                                            for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)) continue;
                                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.remove(o);
                                                k = -1;
                                                removed = true;
                                                continue block94;
                                            }
                                        }
                                        if (!removed) {
                                            block96: for (k = this.peaceTreatyGD.civsDemandsAggressors.size() - 1; k >= 0; --k) {
                                                for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)) continue;
                                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.remove(o);
                                                    k = -1;
                                                    removed = true;
                                                    continue block96;
                                                }
                                            }
                                        }
                                        ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.remove(j);
                                        continue;
                                    }
                                    removed = false;
                                    block98: for (k = this.peaceTreatyGD.civsDemandsDefenders.size() - 1; k >= 0; --k) {
                                        for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)) continue;
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lDemands.remove(o);
                                            k = -1;
                                            removed = true;
                                            continue block98;
                                        }
                                    }
                                    if (!removed) {
                                        block100: for (k = this.peaceTreatyGD.civsDemandsAggressors.size() - 1; k >= 0; --k) {
                                            for (o = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.size() - 1; o >= 0; --o) {
                                                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.get(o) != ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.get(j)) continue;
                                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lDemands.remove(o);
                                                k = -1;
                                                removed = true;
                                                continue block100;
                                            }
                                        }
                                    }
                                    ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i4)).lProvincesLost.remove(j);
                                }
                                continue;
                            }
                            this.peaceTreatyGD.civsDataAggressors.remove(i4);
                            this.peaceTreatyGD.civsDemandsAggressors.remove(i4);
                            continue;
                        }
                        catch (Exception ex) {
                            CFG.exceptionStack((Throwable)ex);
                        }
                    }
                    try {
                        for (i4 = this.peaceTreatyGD.civsDemandsDefenders.size() - 1; i4 >= 0; --i4) {
                            for (int j = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i4)).lDemands.size() - 1; j >= 0; --j) {
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i4)).lDemands.get((int)j)).intValue()).isTaken = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i4)).iCivID;
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i4)).lDemands.get((int)j)).intValue()).iCivID = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i4)).iCivID;
                            }
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                    try {
                        for (int i7 = this.peaceTreatyGD.civsDemandsAggressors.size() - 1; i7 >= 0; --i7) {
                            for (int j = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i7)).lDemands.size() - 1; j >= 0; --j) {
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i7)).lDemands.get((int)j)).intValue()).isTaken = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i7)).iCivID;
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i7)).lDemands.get((int)j)).intValue()).iCivID = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i7)).iCivID;
                            }
                        }
                    }
                    catch (Exception ex) {
                        CFG.exceptionStack((Throwable)ex);
                    }
                }
                catch (Exception ex) {
                    CFG.exceptionStack((Throwable)ex);
                }
            }
            catch (Exception exr) {
                CFG.exceptionStack((Throwable)exr);
            }
        }
    }

    public final void prepareDemansVassalsData() {
        int u;
        int o;
        int k;
        int i;
        for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
            try {
                for (int j = 0; j < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs_TakeControl.size(); ++j) {
                    for (k = 0; k < this.peaceTreatyGD.civsDemandsAggressors.size(); ++k) {
                        if (((PeaceTreaty_ReleaseableVassals_TakeConrol)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs_TakeControl.get((int)j)).iFromCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iCivID) continue;
                        for (o = 0; o < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lReleasableCivs.size(); ++o) {
                            if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lReleasableCivs.get((int)o)).iCivID != ((PeaceTreaty_ReleaseableVassals_TakeConrol)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs_TakeControl.get((int)j)).iVassalCivID) continue;
                            for (u = 0; u < ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lReleasableCivs.get((int)o)).lProvinces.size(); ++u) {
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).lReleasableCivs.get((int)o)).lProvinces.get((int)u)).intValue()).iCivID = ((PeaceTreaty_ReleaseableVassals_TakeConrol)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs_TakeControl.get((int)j)).iVassalCivID;
                            }
                        }
                    }
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
            try {
                for (int j = 0; j < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs_TakeControl.size(); ++j) {
                    for (k = 0; k < this.peaceTreatyGD.civsDemandsDefenders.size(); ++k) {
                        if (((PeaceTreaty_ReleaseableVassals_TakeConrol)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs_TakeControl.get((int)j)).iFromCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iCivID) continue;
                        for (o = 0; o < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lReleasableCivs.size(); ++o) {
                            if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lReleasableCivs.get((int)o)).iCivID != ((PeaceTreaty_ReleaseableVassals_TakeConrol)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs_TakeControl.get((int)j)).iVassalCivID) continue;
                            for (u = 0; u < ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lReleasableCivs.get((int)o)).lProvinces.size(); ++u) {
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).lReleasableCivs.get((int)o)).lProvinces.get((int)u)).intValue()).iCivID = ((PeaceTreaty_ReleaseableVassals_TakeConrol)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs_TakeControl.get((int)j)).iVassalCivID;
                            }
                        }
                    }
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
    }

    public final int takeReleaseVassal(int iFromCivID, int nReleaseCivID, int nCivID, int pointsUsedByCivID) {
        int k;
        int o;
        int nID;
        int i;
        block4: for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != iFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsAggressors.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iCivID != nCivID) continue;
                    nID = -1;
                    for (o = 0; o < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.size(); ++o) {
                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)o)).iCivID != nReleaseCivID) continue;
                        nID = o;
                        break;
                    }
                    if (nID >= 0) {
                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID > 0) {
                            if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID == nCivID) {
                                ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID = -1;
                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft += ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get(nID)).getScoreValue();
                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).removeReleaseVassal_TakeControl(iFromCivID, nReleaseCivID);
                                for (k = 0; k < ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.size(); ++k) {
                                    if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isToTake) {
                                        if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken > 0) {
                                            if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iCivID) {
                                                int u;
                                                for (u = 0; u < this.peaceTreatyGD.civsDemandsAggressors.size(); ++u) {
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)u)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(u)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                                    }
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)u)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)u)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                                }
                                                for (u = 0; u < this.peaceTreatyGD.civsDemandsDefenders.size(); ++u) {
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)u)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(u)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                                    }
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)u)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)u)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                                }
                                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft -= this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                            }
                                        } else {
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft -= this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                        }
                                        this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken = -1;
                                        this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue()).getCivId();
                                        continue;
                                    }
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue()).getCivId();
                                }
                                return 0;
                            }
                            return ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get(nID)).getScoreValue()) {
                            return 0;
                        }
                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID = nCivID;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft -= ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get(nID)).getScoreValue();
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).addReleaseVassal_TakeControl(iFromCivID, nReleaseCivID);
                        for (k = 0; k < ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.size(); ++k) {
                            if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isToTake) {
                                if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken > 0) {
                                    int u;
                                    for (u = 0; u < this.peaceTreatyGD.civsDemandsAggressors.size(); ++u) {
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                        }
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                    }
                                    for (u = 0; u < this.peaceTreatyGD.civsDemandsDefenders.size(); ++u) {
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                        }
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                    }
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken = -1;
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue()).getTrueOwnerOfProv();
                                }
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken = nCivID;
                            }
                            this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = nReleaseCivID;
                        }
                        return nCivID;
                    }
                    i = this.peaceTreatyGD.civsDemandsDefenders.size();
                    continue block4;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        block13: for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != iFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsDefenders.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iCivID != nCivID) continue;
                    nID = -1;
                    for (o = 0; o < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.size(); ++o) {
                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)o)).iCivID != nReleaseCivID) continue;
                        nID = o;
                        break;
                    }
                    if (nID >= 0) {
                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID > 0) {
                            if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID == nCivID) {
                                ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID = -1;
                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft += ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get(nID)).getScoreValue();
                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).removeReleaseVassal_TakeControl(iFromCivID, nReleaseCivID);
                                for (k = 0; k < ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.size(); ++k) {
                                    if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isToTake) {
                                        if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken > 0) {
                                            if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iCivID) {
                                                int u;
                                                for (u = 0; u < this.peaceTreatyGD.civsDemandsDefenders.size(); ++u) {
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)u)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(u)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                                    }
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)u)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)u)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                                }
                                                for (u = 0; u < this.peaceTreatyGD.civsDemandsAggressors.size(); ++u) {
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)u)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(u)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                                    }
                                                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)u)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)u)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                                }
                                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft -= this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                            }
                                        } else {
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft -= this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                        }
                                        this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken = -1;
                                        this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue()).getCivId();
                                        continue;
                                    }
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue()).getCivId();
                                }
                                return 0;
                            }
                            return ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID;
                        }
                        if (((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get(nID)).getScoreValue() > ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft) {
                            return 0;
                        }
                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).iReleasesToCivID = nCivID;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft -= ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get(nID)).getScoreValue();
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).addReleaseVassal_TakeControl(iFromCivID, nReleaseCivID);
                        for (k = 0; k < ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.size(); ++k) {
                            if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isToTake) {
                                if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken > 0) {
                                    int u;
                                    for (u = 0; u < this.peaceTreatyGD.civsDemandsDefenders.size(); ++u) {
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                        }
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                    }
                                    for (u = 0; u < this.peaceTreatyGD.civsDemandsAggressors.size(); ++u) {
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID == this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID) {
                                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).removeDemandOnProvince(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue());
                                        }
                                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken) continue;
                                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft += this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iProvinceValue;
                                    }
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken = -1;
                                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = CFG.core.getProv(((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get(k)).intValue()).getTrueOwnerOfProv();
                                }
                                this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).isTaken = nCivID;
                            }
                            this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)nID)).lProvinces.get((int)k)).intValue()).iCivID = nReleaseCivID;
                        }
                        return nCivID;
                    }
                    i = this.peaceTreatyGD.civsDemandsAggressors.size();
                    continue block13;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        return pointsUsedByCivID;
    }

    public final int getGovernment_Cost(int nCivID) {
        try {
            int i;
            for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iTotalNumOfVicotryPoints * GameValues.gvPeaceTreaty.GOVERNMENT_COST, 1.0f);
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iTotalNumOfVicotryPoints * GameValues.gvPeaceTreaty.GOVERNMENT_COST, 1.0f);
            }
        }
        catch (Exception ex) {
            CFG.exceptionStack((Throwable)ex);
        }
        return 1;
    }

    public final int takeGovernment(int nTakeGovernmentFromCivID, int nCivID, int pointsUsedByCivID) {
        int i;
        for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nTakeGovernmentFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsAggressors.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeGovernmentTypeToCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeGovernmentTypeToCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeGovernmentTypeToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft += this.getGovernment_Cost(nTakeGovernmentFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).removeTakeGovernmentFromCivID(nTakeGovernmentFromCivID);
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getGovernment_Cost(nTakeGovernmentFromCivID)) {
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsAggressors.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeGovernmentTypeToCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeGovernmentTypeToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iVictoryPointsLeft += this.getGovernment_Cost(nTakeGovernmentFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(k)).removeTakeGovernmentFromCivID(nTakeGovernmentFromCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getGovernment_Cost(nTakeGovernmentFromCivID)) {
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeGovernmentTypeToCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft -= this.getGovernment_Cost(nTakeGovernmentFromCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).addTakeGovernmentFromCivID(nTakeGovernmentFromCivID);
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nTakeGovernmentFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsDefenders.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeGovernmentTypeToCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeGovernmentTypeToCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeGovernmentTypeToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft += this.getGovernment_Cost(nTakeGovernmentFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).removeTakeGovernmentFromCivID(nTakeGovernmentFromCivID);
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getGovernment_Cost(nTakeGovernmentFromCivID)) {
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsDefenders.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeGovernmentTypeToCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeGovernmentTypeToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iVictoryPointsLeft += this.getGovernment_Cost(nTakeGovernmentFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(k)).removeTakeGovernmentFromCivID(nTakeGovernmentFromCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getGovernment_Cost(nTakeGovernmentFromCivID)) {
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeGovernmentTypeToCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft -= this.getGovernment_Cost(nTakeGovernmentFromCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).addTakeGovernmentFromCivID(nTakeGovernmentFromCivID);
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        return pointsUsedByCivID;
    }

    public final int getReligion_Cost(int nCivID) {
        try {
            int i;
            for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iTotalNumOfVicotryPoints * GameValues.gvPeaceTreaty.RELIGION_COST, 1.0f);
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iTotalNumOfVicotryPoints * GameValues.gvPeaceTreaty.RELIGION_COST, 1.0f);
            }
        }
        catch (Exception ex) {
            CFG.exceptionStack((Throwable)ex);
        }
        return 1;
    }

    public final int takeReligion(int nTakeReligionFromCivID, int nCivID, int pointsUsedByCivID) {
        int i;
        for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nTakeReligionFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsAggressors.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeReligionToCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeReligionToCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeReligionToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft += this.getReligion_Cost(nTakeReligionFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).removeTakeReligionFromCivID(nTakeReligionFromCivID);
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getReligion_Cost(nTakeReligionFromCivID)) {
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsAggressors.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeReligionToCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeReligionToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iVictoryPointsLeft += this.getReligion_Cost(nTakeReligionFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(k)).removeTakeReligionFromCivID(nTakeReligionFromCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getReligion_Cost(nTakeReligionFromCivID)) {
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).changeReligionToCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft -= this.getReligion_Cost(nTakeReligionFromCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).addTakeReligionFromCivID(nTakeReligionFromCivID);
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nTakeReligionFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsDefenders.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeReligionToCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeReligionToCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeReligionToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft += this.getReligion_Cost(nTakeReligionFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).removeTakeReligionFromCivID(nTakeReligionFromCivID);
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getReligion_Cost(nTakeReligionFromCivID)) {
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsDefenders.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeReligionToCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeReligionToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iVictoryPointsLeft += this.getReligion_Cost(nTakeReligionFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(k)).removeTakeReligionFromCivID(nTakeReligionFromCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getReligion_Cost(nTakeReligionFromCivID)) {
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).changeReligionToCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft -= this.getReligion_Cost(nTakeReligionFromCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).addTakeReligionFromCivID(nTakeReligionFromCivID);
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        return pointsUsedByCivID;
    }

    public final int getVassalization_Cost(int nCivID) {
        try {
            int i;
            for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iTotalNumOfVicotryPoints * 0.4f, 1.0f);
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iTotalNumOfVicotryPoints * 0.4f, 1.0f);
            }
        }
        catch (Exception ex) {
            CFG.exceptionStack((Throwable)ex);
        }
        return 1;
    }

    public final int takeVassalize(int nVasslizeCivID, int nCivID, int pointsUsedByCivID) {
        int i;
        for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nVasslizeCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsAggressors.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iWillBecomeVassalOfCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iWillBecomeVassalOfCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iWillBecomeVassalOfCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft += this.getVassalization_Cost(nVasslizeCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).removeWillVassalizeCivID(nVasslizeCivID);
                            if (CFG.menus.getInGame_PeaceTreaty()) {
                                if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                                    CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                                }
                                CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                            }
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getVassalization_Cost(nVasslizeCivID)) {
                            if (CFG.menus.getInGame_PeaceTreaty()) {
                                if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                                    CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                                }
                                CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                            }
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsAggressors.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iWillBecomeVassalOfCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iWillBecomeVassalOfCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iVictoryPointsLeft += this.getVassalization_Cost(nVasslizeCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(k)).removeWillVassalizeCivID(nVasslizeCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getVassalization_Cost(nVasslizeCivID)) {
                        if (CFG.menus.getInGame_PeaceTreaty()) {
                            if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                                CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                            }
                            CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                        }
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iWillBecomeVassalOfCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft -= this.getVassalization_Cost(nVasslizeCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).addWillVassalizeCivID(nVasslizeCivID);
                    if (CFG.menus.getInGame_PeaceTreaty()) {
                        if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                            CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                        }
                        CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                    }
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nVasslizeCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsDefenders.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iWillBecomeVassalOfCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iWillBecomeVassalOfCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iWillBecomeVassalOfCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft += this.getVassalization_Cost(nVasslizeCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).removeWillVassalizeCivID(nVasslizeCivID);
                            if (CFG.menus.getInGame_PeaceTreaty()) {
                                if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                                    CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                                }
                                CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                            }
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getVassalization_Cost(nVasslizeCivID)) {
                            if (CFG.menus.getInGame_PeaceTreaty()) {
                                if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                                    CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                                }
                                CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                            }
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsDefenders.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iWillBecomeVassalOfCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iWillBecomeVassalOfCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iVictoryPointsLeft += this.getVassalization_Cost(nVasslizeCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(k)).removeWillVassalizeCivID(nVasslizeCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getVassalization_Cost(nVasslizeCivID)) {
                        if (CFG.menus.getInGame_PeaceTreaty()) {
                            if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                                CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                            }
                            CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                        }
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iWillBecomeVassalOfCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft -= this.getVassalization_Cost(nVasslizeCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).addWillVassalizeCivID(nVasslizeCivID);
                    if (CFG.menus.getInGame_PeaceTreaty()) {
                        if (!CFG.core.getCiv(nCivID).getIsPlayer()) {
                            CFG.menus.rebuildInGame_PeaceTreaty_Provinces();
                        }
                        CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                    }
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        return pointsUsedByCivID;
    }

    public final int getWarReparation_Cost(int nCivID) {
        try {
            int i;
            for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iTotalNumOfVicotryPoints * 0.1f, 1.0f);
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nCivID) continue;
                return (int)Math.max((float)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iTotalNumOfVicotryPoints * 0.1f, 1.0f);
            }
        }
        catch (Exception ex) {
            CFG.exceptionStack((Throwable)ex);
        }
        return 1;
    }

    public final int takeWarReparations(int nWarRepartionsFromCivID, int nCivID, int pointsUsedByCivID) {
        int i;
        for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != nWarRepartionsFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsAggressors.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iPaysWarReparationsToCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iPaysWarReparationsToCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iPaysWarReparationsToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft += this.getWarReparation_Cost(nWarRepartionsFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).removeWarReparationsFromCivID(nWarRepartionsFromCivID);
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getWarReparation_Cost(nWarRepartionsFromCivID)) {
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsAggressors.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iPaysWarReparationsToCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iPaysWarReparationsToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)k)).iVictoryPointsLeft += this.getWarReparation_Cost(nWarRepartionsFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(k)).removeWarReparationsFromCivID(nWarRepartionsFromCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft < this.getWarReparation_Cost(nWarRepartionsFromCivID)) {
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iPaysWarReparationsToCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)j)).iVictoryPointsLeft -= this.getWarReparation_Cost(nWarRepartionsFromCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(j)).addWarReparationsFromCivID(nWarRepartionsFromCivID);
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
            try {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != nWarRepartionsFromCivID) continue;
                for (int j = 0; j < this.peaceTreatyGD.civsDemandsDefenders.size(); ++j) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iCivID != nCivID) continue;
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iPaysWarReparationsToCivID > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iPaysWarReparationsToCivID == nCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iPaysWarReparationsToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft += this.getWarReparation_Cost(nWarRepartionsFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).removeWarReparationsFromCivID(nWarRepartionsFromCivID);
                            return 0;
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getWarReparation_Cost(nWarRepartionsFromCivID)) {
                            return 0;
                        }
                        for (int k = 0; k < this.peaceTreatyGD.civsDemandsDefenders.size(); ++k) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iPaysWarReparationsToCivID) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iPaysWarReparationsToCivID = 0;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)k)).iVictoryPointsLeft += this.getWarReparation_Cost(nWarRepartionsFromCivID);
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(k)).removeWarReparationsFromCivID(nWarRepartionsFromCivID);
                            break;
                        }
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft < this.getWarReparation_Cost(nWarRepartionsFromCivID)) {
                        return 0;
                    }
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iPaysWarReparationsToCivID = nCivID;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)j)).iVictoryPointsLeft -= this.getWarReparation_Cost(nWarRepartionsFromCivID);
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(j)).addWarReparationsFromCivID(nWarRepartionsFromCivID);
                    return nCivID;
                }
                continue;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        return pointsUsedByCivID;
    }

    public final boolean takeProvince(int nProvinceID, int nCivID, int pointsUsedByCivID) {
        block18: {
            try {
                if (nProvinceID < 0 || CFG.core.getProv(nProvinceID).getSeaProv() || !this.drawProvOwners.get((int)nProvinceID).isToTake) break block18;
                this.iLastTakenID = nProvinceID;
                if (this.drawProvOwners.get((int)nProvinceID).isTaken > 0) {
                    int i;
                    if (this.drawProvOwners.get((int)nProvinceID).iCivID == nCivID) {
                        int i2;
                        for (i2 = 0; i2 < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i2) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID == this.drawProvOwners.get((int)nProvinceID).iCivID) {
                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i2)).removeDemandOnProvince(nProvinceID);
                                this.addProvincesLeftToTake(nProvinceID);
                            }
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iCivID != this.drawProvOwners.get((int)nProvinceID).isTaken) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i2)).iVictoryPointsLeft += this.drawProvOwners.get((int)nProvinceID).iProvinceValue;
                            if (!CFG.menus.getInGame_PeaceTreaty()) continue;
                            CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                        }
                        for (i2 = 0; i2 < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i2) {
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID == this.drawProvOwners.get((int)nProvinceID).iCivID) {
                                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i2)).removeDemandOnProvince(nProvinceID);
                                this.addProvincesLeftToTake(nProvinceID);
                            }
                            if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iCivID != this.drawProvOwners.get((int)nProvinceID).isTaken) continue;
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i2)).iVictoryPointsLeft += this.drawProvOwners.get((int)nProvinceID).iProvinceValue;
                            if (!CFG.menus.getInGame_PeaceTreaty()) continue;
                            CFG.menus.rebuildInGame_PeaceTreaty_Scores();
                        }
                        this.drawProvOwners.get((int)nProvinceID).isTaken = -1;
                        this.drawProvOwners.get((int)nProvinceID).iCivID = CFG.core.getProv(nProvinceID).getTrueOwnerOfProv();
                        break block18;
                    }
                    for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID == this.drawProvOwners.get((int)nProvinceID).iCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).removeDemandOnProvince(nProvinceID);
                            this.addProvincesLeftToTake(nProvinceID);
                        }
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft += this.drawProvOwners.get((int)nProvinceID).iProvinceValue;
                    }
                    for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID == this.drawProvOwners.get((int)nProvinceID).iCivID) {
                            ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).removeDemandOnProvince(nProvinceID);
                            this.addProvincesLeftToTake(nProvinceID);
                        }
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != this.drawProvOwners.get((int)nProvinceID).isTaken) continue;
                        ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft += this.drawProvOwners.get((int)nProvinceID).iProvinceValue;
                    }
                    this.drawProvOwners.get((int)nProvinceID).isTaken = -1;
                    if (this.makeDemand_Province(nProvinceID, nCivID, pointsUsedByCivID)) {
                        this.removeProvincesLeftToTake(nProvinceID);
                        CFG.core.setActiveProvID(-1);
                        this.AIUseVictoryPoints();
                        return true;
                    }
                    CFG.core.setActiveProvID(-1);
                    this.AIUseVictoryPoints();
                    return false;
                }
                if (this.makeDemand_Province(nProvinceID, nCivID, pointsUsedByCivID)) {
                    this.removeProvincesLeftToTake(nProvinceID);
                    try {
                        CFG.core.setActiveProvID(-1);
                    }
                    catch (Exception i) {
                        // empty catch block
                    }
                    this.AIUseVictoryPoints();
                    return true;
                }
                try {
                    CFG.core.setActiveProvID(-1);
                }
                catch (Exception i) {
                    // empty catch block
                }
                this.AIUseVictoryPoints();
                return false;
            }
            catch (Exception ex) {
                CFG.exceptionStack((Throwable)ex);
            }
        }
        return false;
    }

    public final boolean makeDemand_Province(int nProvinceID, int nCivID, int pointsUsedByCivID) {
        return this.makeDemand_Province(nProvinceID, nCivID, pointsUsedByCivID, false);
    }

    public final boolean makeDemand_Province(int nProvinceID, int nCivID, int pointsUsedByCivID, boolean free_ToTrueOwner) {
        try {
            int i;
            if (nCivID != pointsUsedByCivID && CFG.core.getProv(nProvinceID).getTrueOwnerOfProv() == nCivID) {
                pointsUsedByCivID = nCivID;
            }
            if (!free_ToTrueOwner) {
                for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                    if (this.drawProvOwners.get((int)nProvinceID).isTaken > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != pointsUsedByCivID) continue;
                        if (this.drawProvOwners.get((int)nProvinceID).isTaken == pointsUsedByCivID) break;
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft >= this.drawProvOwners.get((int)nProvinceID).iProvinceValue) continue;
                        return false;
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != pointsUsedByCivID || ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft >= this.drawProvOwners.get((int)nProvinceID).iProvinceValue) continue;
                    return false;
                }
                for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                    if (this.drawProvOwners.get((int)nProvinceID).isTaken > 0) {
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != pointsUsedByCivID) continue;
                        if (this.drawProvOwners.get((int)nProvinceID).isTaken == pointsUsedByCivID) break;
                        if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft >= this.drawProvOwners.get((int)nProvinceID).iProvinceValue) continue;
                        return false;
                    }
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != pointsUsedByCivID || ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft >= this.drawProvOwners.get((int)nProvinceID).iProvinceValue) continue;
                    return false;
                }
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID == nCivID) {
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).addDemandOnProvince(nProvinceID);
                }
                if (this.drawProvOwners.get((int)nProvinceID).isTaken > 0) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != this.drawProvOwners.get((int)nProvinceID).iCivID) continue;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).removeDemandOnProvince(nProvinceID);
                    continue;
                }
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID != pointsUsedByCivID) continue;
                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iVictoryPointsLeft -= this.drawProvOwners.get((int)nProvinceID).iProvinceValue;
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID == nCivID) {
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).addDemandOnProvince(nProvinceID);
                }
                if (this.drawProvOwners.get((int)nProvinceID).isTaken > 0) {
                    if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != this.drawProvOwners.get((int)nProvinceID).iCivID) continue;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).removeDemandOnProvince(nProvinceID);
                    continue;
                }
                if (((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID != pointsUsedByCivID) continue;
                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iVictoryPointsLeft -= this.drawProvOwners.get((int)nProvinceID).iProvinceValue;
            }
            this.drawProvOwners.get((int)nProvinceID).isTaken = pointsUsedByCivID;
            this.drawProvOwners.get((int)nProvinceID).iCivID = nCivID;
            if (CFG.menus.getInGame_PeaceTreaty()) {
                CFG.menus.rebuildInGame_PeaceTreaty_Scores();
            }
        }
        catch (Exception ex) {
            CFG.exceptionStack((Throwable)ex);
        }
        return true;
    }

    public final void preparePeaceTreatyToSend(int iFromCivID) {
        try {
            int k;
            int k2;
            int numOfConnections_Enemies;
            int numOfConnections_Own;
            int j;
            int i;
            int j2;
            int i2;
            for (i2 = 0; i2 < this.peaceTreatyGD.civsDataDefenders.size(); ++i2) {
                for (j2 = 0; j2 < ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.size(); ++j2) {
                    if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get((int)j2)).intValue()).isTaken >= 0) continue;
                    this.makeDemand_Province((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).lProvincesLost.get(j2), ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).iCivID, ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i2)).iCivID, true);
                }
            }
            for (i2 = 0; i2 < this.peaceTreatyGD.civsDataAggressors.size(); ++i2) {
                for (j2 = 0; j2 < ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.size(); ++j2) {
                    if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get((int)j2)).intValue()).isTaken >= 0) continue;
                    this.makeDemand_Province((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).lProvincesLost.get(j2), ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).iCivID, ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i2)).iCivID, true);
                }
            }
            boolean updateData = false;
            for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                if (CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID).getIsPlayer()) continue;
                for (j = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.size() - 1; j >= 0; --j) {
                    numOfConnections_Own = 0;
                    numOfConnections_Enemies = 0;
                    for (k2 = 0; k2 < CFG.core.getProv(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get(j)).intValue()).getNeighProvincesSize(); ++k2) {
                        if (this.drawProvOwners.get((int)CFG.core.getProv((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get((int)j)).intValue()).getNeighProvinces((int)k2)).iCivID == ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID) {
                            ++numOfConnections_Own;
                            continue;
                        }
                        if (this.drawProvOwners.get((int)CFG.core.getProv((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get((int)j)).intValue()).getNeighProvinces((int)k2)).iCivID >= 0 && !CFG.core.getCivsAtWar(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID, this.drawProvOwners.get((int)CFG.core.getProv((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get((int)j)).intValue()).getNeighProvinces((int)k2)).iCivID)) continue;
                        ++numOfConnections_Enemies;
                    }
                    if (numOfConnections_Own > 0 || numOfConnections_Enemies <= 0 || CFG.core.getProv(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get(j)).intValue()).getNeighProvincesSize() <= 2 && CFG.core.getProv(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get(j)).intValue()).getNeighSeaProvincesSize() > 0) continue;
                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get((int)j)).intValue()).isTaken = -1;
                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get((int)j)).intValue()).iCivID = CFG.core.getProv(i).getCivId() * -1;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get(i)).removeDemandOnProvince(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lDemands.get(j)).intValue());
                    updateData = true;
                }
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                if (CFG.core.getCiv(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID).getIsPlayer()) continue;
                for (j = ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.size() - 1; j >= 0; --j) {
                    numOfConnections_Own = 0;
                    numOfConnections_Enemies = 0;
                    for (k2 = 0; k2 < CFG.core.getProv(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get(j)).intValue()).getNeighProvincesSize(); ++k2) {
                        if (this.drawProvOwners.get((int)CFG.core.getProv((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get((int)j)).intValue()).getNeighProvinces((int)k2)).iCivID == ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID) {
                            ++numOfConnections_Own;
                            continue;
                        }
                        if (this.drawProvOwners.get((int)CFG.core.getProv((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get((int)j)).intValue()).getNeighProvinces((int)k2)).iCivID >= 0 && !CFG.core.getCivsAtWar(((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID, this.drawProvOwners.get((int)CFG.core.getProv((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get((int)j)).intValue()).getNeighProvinces((int)k2)).iCivID)) continue;
                        ++numOfConnections_Enemies;
                    }
                    if (numOfConnections_Own > 0 || numOfConnections_Enemies <= 0 || CFG.core.getProv(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get(j)).intValue()).getNeighProvincesSize() <= 2 && CFG.core.getProv(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get(j)).intValue()).getNeighSeaProvincesSize() > 0) continue;
                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get((int)j)).intValue()).isTaken = -1;
                    this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get((int)j)).intValue()).iCivID = CFG.core.getProv(i).getCivId() * -1;
                    ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get(i)).removeDemandOnProvince(((Integer)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lDemands.get(j)).intValue());
                    updateData = true;
                }
            }
            if (updateData) {
                for (i = 0; i < this.peaceTreatyGD.civsDataDefenders.size(); ++i) {
                    for (j = 0; j < ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.size(); ++j) {
                        if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.get((int)j)).intValue()).isTaken >= 0) continue;
                        this.makeDemand_Province((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).lProvincesLost.get(j), ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).iCivID, ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataDefenders.get((int)i)).iCivID, true);
                    }
                }
                for (i = 0; i < this.peaceTreatyGD.civsDataAggressors.size(); ++i) {
                    for (j = 0; j < ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i)).lProvincesLost.size(); ++j) {
                        if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i)).lProvincesLost.get((int)j)).intValue()).isTaken >= 0) continue;
                        this.makeDemand_Province((Integer)((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i)).lProvincesLost.get(j), ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i)).iCivID, ((PeaceTreaty_Civs)this.peaceTreatyGD.civsDataAggressors.get((int)i)).iCivID, true);
                    }
                }
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsDefenders.size(); ++i) {
                for (j = 0; j < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.size(); ++j) {
                    for (k = ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)j)).lProvinces.size() - 1; k >= 0; --k) {
                        if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)j)).lProvinces.get((int)k)).intValue()).iCivID == ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)j)).iCivID) continue;
                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).lReleasableCivs.get((int)j)).lProvinces.remove(k);
                    }
                }
                if (iFromCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).iCivID) continue;
                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsDefenders.get((int)i)).peaceTreatyAccepted = true;
            }
            for (i = 0; i < this.peaceTreatyGD.civsDemandsAggressors.size(); ++i) {
                for (j = 0; j < ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.size(); ++j) {
                    for (k = ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)j)).lProvinces.size() - 1; k >= 0; --k) {
                        if (this.drawProvOwners.get((int)((Integer)((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)j)).lProvinces.get((int)k)).intValue()).iCivID == ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)j)).iCivID) continue;
                        ((PeaceTreaty_ReleaseableVassals)((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).lReleasableCivs.get((int)j)).lProvinces.remove(k);
                    }
                }
                if (iFromCivID != ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).iCivID) continue;
                ((PeaceTreaty_Demands)this.peaceTreatyGD.civsDemandsAggressors.get((int)i)).peaceTreatyAccepted = true;
            }
        }
        catch (Exception ex) {
            CFG.exceptionStack((Throwable)ex);
        }
    }

    public static int getProposal_Positive(boolean scoreCountDefenders) {
        int out = 0;
        return out;
    }

    public static int getProposal_Negative(boolean scoreCountDefenders) {
        int out = 0;
        return out;
    }
}
