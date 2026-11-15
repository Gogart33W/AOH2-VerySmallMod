package age.of.civilizations2.jakowski.lukasz.NextGen;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.GameCalendar;
import age.of.civilizations2.jakowski.lukasz.Messages.Message;
import age.of.civilizations2.jakowski.lukasz.Messages.Truce.SignPeace.Message_WeCanSignPeace;
import age.of.civilizations2.jakowski.lukasz.Province;
import java.util.ArrayList;
import java.util.List;

/**
 * TacticalWarAIManager - розумніша тактика та оперативне мистецтво ШІ у війні:
 * 
 * 1. Концентрація сил (Operational Concentration):
 *    - Замість розпорошення крихітних загонів по 50-100 воїнів ШІ стягує внутрішні гарнізони
 *      в ударні кулаки на головному напрямку бойових дій.
 * 2. Організований відступ (Tactical Retreat):
 *    - Якщо супротивник у сусідній провінції переважає у 2.5+ рази, загін ШІ не гине в самогубній атаці,
 *      а відступає до найближчого дружнього форту або столиці, де займає оборону.
 * 3. Своєчасний мир (Capitulation Evaluation):
 *    - Якщо столицю захоплено, втрачено понад 60% території та розгромлено армію, ШІ своєчасно просить мир.
 */
public class TacticalWarAIManager {
    public static boolean tacticalAIEnabled = true;

    /**
     * Запуск щоходової тактичної оцінки для всіх воюючих цивілізацій під керуванням ШІ.
     */
    public static void runTacticalAIWarTurn() {
        if (!tacticalAIEnabled) return;
        try {
            int numCivs = CFG.core.getCivsSize();

            for (int civID = 1; civID < numCivs; civID++) {
                if (CFG.core.getCiv(civID).getIsPlayer()) continue; // Тільки для ШІ
                if (!CFG.core.getCiv(civID).isAtWarC()) continue;   // Тільки в стані війни
                if (CFG.core.getCiv(civID).getNumOfProvs() <= 0) continue;

                int numOfProvs = CFG.core.getCiv(civID).getNumOfProvs();

                // 1. Оцінка капітуляції / порятунку держави при критичному розгромі
                int capProv = CFG.core.getCiv(civID).getCapitalProvID();
                boolean capitalLost = (capProv >= 0 && CFG.core.getProv(capProv).getCivId() != civID);
                int totalUnits = CFG.core.getCiv(civID).getNumberOfUnits();

                if (capitalLost && totalUnits < 1500 && numOfProvs < 5) {
                    // Знаходимо головного ворога
                    for (int n = 1; n < numCivs; n++) {
                        if (CFG.core.getCivsAtWar(civID, n)) {
                            try {
                                CFG.core.getCiv(n).getCivDiploGD().messageBox.addMessage(new Message_WeCanSignPeace(civID));
                            } catch (Exception ignored) {}
                            break;
                        }
                    }
                }

                // 2. Тактичний відступ та концентрація армій ШІ
                for (int i = 0; i < numOfProvs; i++) {
                    int pID = CFG.core.getCiv(civID).getProvID(i);
                    if (pID < 0 || CFG.core.getProv(pID).getSeaProv()) continue;

                    int armyInProv = CFG.core.getProv(pID).getArmyCivID1(civID);
                    if (armyInProv <= 100) continue;

                    int neighSize = CFG.core.getProv(pID).getNeighProvincesSize();
                    int maxHostileArmy = 0;
                    int hostileNeighborProv = -1;
                    int safeFortNeighborProv = -1;
                    int friendlyFrontierProv = -1;

                    for (int j = 0; j < neighSize; j++) {
                        int nID = CFG.core.getProv(pID).getNeighProvinces(j);
                        if (nID < 0 || CFG.core.getProv(nID).getSeaProv() || CFG.core.getProv(nID).getWastelandLvl() >= 0) continue;

                        int nCiv = CFG.core.getProv(nID).getCivId();
                        if (nCiv > 0 && CFG.core.getCivsAtWar(civID, nCiv)) {
                            int hArmy = CFG.core.getProv(nID).getArmyCivID1(nCiv);
                            if (hArmy > maxHostileArmy) {
                                maxHostileArmy = hArmy;
                                hostileNeighborProv = nID;
                            }
                        } else if (nCiv == civID) {
                            if (CFG.core.getProv(nID).getLvlOfFort() > 0 || nID == capProv) {
                                safeFortNeighborProv = nID;
                            }
                            // Перевіряємо, чи ця дружня провінція виходить на фронт
                            int nnSize = CFG.core.getProv(nID).getNeighProvincesSize();
                            for (int k = 0; k < nnSize; k++) {
                                int nnID = CFG.core.getProv(nID).getNeighProvinces(k);
                                if (nnID >= 0 && CFG.core.getProv(nnID).getCivId() > 0 && CFG.core.getCivsAtWar(civID, CFG.core.getProv(nnID).getCivId())) {
                                    friendlyFrontierProv = nID;
                                    break;
                                }
                            }
                        }
                    }

                    // А. Організований відступ: якщо ворог у сусідній провінції переважає у 2.5+ рази,
                    // а у нас немає фортеці — відступаємо до найближчого форту або столиці
                    if (maxHostileArmy >= armyInProv * 2.5f && CFG.core.getProv(pID).getLvlOfFort() == 0 && safeFortNeighborProv >= 0) {
                        int retreatArmy = (int)((float)armyInProv * 0.85f);
                        if (retreatArmy > 100) {
                            CFG.gameAction.moveArmyAction(pID, safeFortNeighborProv, retreatArmy, civID, true, false);
                        }
                    }
                    // Б. Концентрація сил: якщо провінція у глибокому безпечному тилу,
                    // переміщуємо надлишковий гарнізон до найближчої фронтової провінції
                    else if (hostileNeighborProv < 0 && friendlyFrontierProv >= 0 && armyInProv > 500) {
                        int deployArmy = armyInProv - 300; // Залишаємо 300 воїнів гарнізону
                        if (deployArmy > 200) {
                            CFG.gameAction.moveArmyAction(pID, friendlyFrontierProv, deployArmy, civID, true, false);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
