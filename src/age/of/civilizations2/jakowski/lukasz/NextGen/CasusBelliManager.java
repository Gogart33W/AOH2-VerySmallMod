package age.of.civilizations2.jakowski.lukasz.NextGen;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.GameCalendar;
import age.of.civilizations2.jakowski.lukasz.Province;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CasusBelliManager - система приводів до війни (Casus Belli) та агресивної репутації (Aggressive Expansion / Infamy).
 * 
 * 1. Casus Belli (CB):
 *    - Повернення національних земель (Core Reclamation): якщо ворог утримує ваше національне ядро.
 *    - Історичні претензії (Historical Claims): провінції, які історично належали державі.
 *    - Сфабриковані претензії (Fabricated Claims): дипломатична підготовка війни (5-10 ходів).
 * 2. Агресивна репутація (Aggressive Expansion / Infamy):
 *    - Війна без підстави (No-CB War) спричиняє катастрофічний дипломатичний штраф:
 *      -30 стосунків з усіма сусідами, падіння стабільності на 15% та +25 очок загрози.
 *    - Війна з легітимним CB не викликає загального осуду сусідів.
 * 3. Оборонні коаліції:
 *    - Якщо загроза (AE) держави перевищує 40 очок, сусіди об'єднуються в оборонні пакти.
 */
public class CasusBelliManager {
    // Карта сфабрикованих претензій: civID -> (targetCivID -> turnsRemaining)
    private static final Map<Integer, Map<Integer, Integer>> claims = new ConcurrentHashMap<Integer, Map<Integer, Integer>>();

    // Показник агресивної репутації (Infamy / AE): civID -> поточні бали
    private static final Map<Integer, Float> aggressiveExpansion = new ConcurrentHashMap<Integer, Float>();

    /**
     * Перевіряє наявність легітимного приводу до війни (Casus Belli).
     */
    public static boolean hasCasusBelli(int fromCivID, int targetCivID) {
        try {
            if (fromCivID <= 0 || targetCivID <= 0 || fromCivID == targetCivID) return false;

            // 1. Перевірка сфабрикованих претензій
            Map<Integer, Integer> civClaims = claims.get(fromCivID);
            if (civClaims != null && civClaims.containsKey(targetCivID)) {
                return true;
            }

            // 2. Перевірка національних ядер (Core Reclamation): ворог утримує наші корінні землі
            int targetProvs = CFG.core.getCiv(targetCivID).getNumOfProvs();
            for (int i = 0; i < targetProvs; i++) {
                int pID = CFG.core.getCiv(targetCivID).getProvID(i);
                if (pID >= 0 && CFG.core.getProv(pID).getCores() != null) {
                    if (CFG.core.getProv(pID).getCores().getHaveACore(fromCivID)) {
                        return true;
                    }
                    if (CFG.core.getProv(pID).getTrueOwnerOfProv() == fromCivID) {
                        return true;
                    }
                }
            }

            // 3. Васал / сюзерен: бунт васала або покарання сюзерена
            if (CFG.core.getCiv(targetCivID).getPuppetOfCiv() == fromCivID ||
                CFG.core.getCiv(fromCivID).getPuppetOfCiv() == targetCivID) {
                return true;
            }

            // 4. Порушення перемир'я ворогом
            if (CFG.core.getCivTruce(fromCivID, targetCivID) > 0) {
                return false;
            }

            return false;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Повертає назву приводу до війни для інтерфейсу.
     */
    public static String getCasusBelliName(int fromCivID, int targetCivID, boolean isUkr) {
        if (!hasCasusBelli(fromCivID, targetCivID)) {
            return isUkr ? "Немає приводу (Війна без підстави, штраф AE)" : "No Casus Belli (Unjustified War, AE penalty)";
        }

        int targetProvs = CFG.core.getCiv(targetCivID).getNumOfProvs();
        for (int i = 0; i < targetProvs; i++) {
            int pID = CFG.core.getCiv(targetCivID).getProvID(i);
            if (pID >= 0 && CFG.core.getProv(pID).getCores() != null && CFG.core.getProv(pID).getCores().getHaveACore(fromCivID)) {
                return isUkr ? "Повернення національних земель (Core Reclamation)" : "Reconquest of National Cores";
            }
        }

        Map<Integer, Integer> civClaims = claims.get(fromCivID);
        if (civClaims != null && civClaims.containsKey(targetCivID)) {
            return isUkr ? "Дипломатична претензія (Fabricated Claim)" : "Fabricated Diplomatic Claim";
        }

        return isUkr ? "Легітимний привід до війни" : "Legitimate Casus Belli";
    }

    /**
     * Розпочинає фабрикацію претензії (Fabricate Claim).
     */
    public static boolean startClaimFabrication(int fromCivID, int targetCivID) {
        try {
            if (fromCivID <= 0 || targetCivID <= 0 || fromCivID == targetCivID) return false;
            Map<Integer, Integer> civClaims = claims.get(fromCivID);
            if (civClaims == null) {
                civClaims = new ConcurrentHashMap<Integer, Integer>();
                claims.put(fromCivID, civClaims);
            }
            civClaims.put(targetCivID, 6); // 6 ходів на підготовку претензії

            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;

            String targetName = CFG.core.getCiv(targetCivID).getCivName();
            String msg = isUkr
                    ? ("📜 Розпочато фабрикацію претензій проти " + targetName + " (готовність через 6 ходів).")
                    : ("📜 Started claim fabrication against " + targetName + " (ready in 6 turns).");
            CFG.toastM.addM(msg, CFG.COLOR_POSITIVE);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Отримує поточний рівень агресивної репутації (загрози).
     */
    public static float getAggressiveExpansion(int civID) {
        Float val = aggressiveExpansion.get(civID);
        return val != null ? val.floatValue() : 0.0f;
    }

    /**
     * Обробляє оголошення війни з перевіркою CB та застосуванням санкцій за агресію.
     */
    public static void onWarDeclared(int aggressorCivID, int defenderCivID) {
        try {
            boolean hasCB = hasCasusBelli(aggressorCivID, defenderCivID);
            boolean isPlayer = (CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId() == aggressorCivID);
            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;

            if (!hasCB) {
                // Війна без підстави (No-CB War): сильний удар по репутації та стабільності
                float curAE = getAggressiveExpansion(aggressorCivID);
                aggressiveExpansion.put(aggressorCivID, curAE + 28.0f);

                // Падіння стабільності
                float curStab = CFG.core.getCiv(aggressorCivID).getStabilityCiv();
                CFG.core.getCiv(aggressorCivID).setStabilityCiv(Math.max(0.10f, curStab - 0.15f));

                // Зіпсуття стосунків з усіма сусідами
                int numProvs = CFG.core.getCiv(aggressorCivID).getNumOfProvs();
                for (int i = 0; i < numProvs; i++) {
                    int pID = CFG.core.getCiv(aggressorCivID).getProvID(i);
                    if (pID < 0) continue;
                    int neighSize = CFG.core.getProv(pID).getNeighProvincesSize();
                    for (int n = 0; n < neighSize; n++) {
                        int nProv = CFG.core.getProv(pID).getNeighProvinces(n);
                        int nCiv = CFG.core.getProv(nProv).getCivId();
                        if (nCiv > 0 && nCiv != aggressorCivID && nCiv != defenderCivID) {
                            float rel = CFG.core.getCiv(nCiv).getRelationD(aggressorCivID);
                            CFG.core.getCiv(nCiv).setRelationD(aggressorCivID, Math.max(-100.0f, rel - 25.0f));
                        }
                    }
                }

                if (isPlayer) {
                    String warn = isUkr
                            ? "⛔ Війна без приводу (No-CB)! Стабільність знизилась на 15%, а сусіди розцінили це як віроломну агресію (+28 загрози)!"
                            : "⛔ Unjustified War (No-CB)! Stability dropped by 15%, and neighbors consider this unprovoked aggression (+28 Infamy)!";
                    CFG.toastM.addM(warn, CFG.COLOR_NEGATIVE_2);
                    CFG.toastM.setTimeInView(5000);
                }
            } else {
                // Війна з легітимним приводом: мінімальне зростання AE
                float curAE = getAggressiveExpansion(aggressorCivID);
                aggressiveExpansion.put(aggressorCivID, curAE + 4.0f);

                if (isPlayer) {
                    String ok = isUkr
                            ? "⚔️ Війну розпочато за законним приводом. Суспільство підтримує справедливу боротьбу!"
                            : "⚔️ War declared with legitimate Casus Belli. Society supports the justified struggle!";
                    CFG.toastM.addM(ok, CFG.COLOR_POSITIVE);
                    CFG.toastM.setTimeInView(4000);
                }
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    /**
     * Щоходове зменшення загрози (природне спадання) та оновлення статусу фабрикації претензій.
     */
    public static void runDiplomacyReputationTurn() {
        try {
            // 1. Природне зменшення AE (спадання на 1.25 щоходу)
            for (Map.Entry<Integer, Float> entry : aggressiveExpansion.entrySet()) {
                float val = entry.getValue();
                if (val > 0.0f) {
                    float nextVal = Math.max(0.0f, val - 1.25f);
                    aggressiveExpansion.put(entry.getKey(), nextVal);
                }
            }

            // 2. Оновлення таймерів фабрикації претензій
            int playerCiv = -1;
            try {
                if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                    playerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
                }
            } catch (Exception ignored) {}

            for (Map.Entry<Integer, Map<Integer, Integer>> cEntry : claims.entrySet()) {
                int ownerCiv = cEntry.getKey();
                Map<Integer, Integer> subMap = cEntry.getValue();
                List<Integer> readyTargets = new ArrayList<Integer>();

                for (Map.Entry<Integer, Integer> tEntry : subMap.entrySet()) {
                    int targetCiv = tEntry.getKey();
                    int turnsLeft = tEntry.getValue() - 1;
                    if (turnsLeft <= 0) {
                        readyTargets.add(targetCiv);
                    } else {
                        subMap.put(targetCiv, turnsLeft);
                    }
                }

                for (int readyCiv : readyTargets) {
                    subMap.put(readyCiv, 0); // Претензія готова та діє постійно
                    if (ownerCiv == playerCiv) {
                        boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                                ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                                : true;
                        String targetName = CFG.core.getCiv(readyCiv).getCivName();
                        String doneMsg = isUkr
                                ? ("✅ Претензію на " + targetName + " успішно сфабриковано! Отримано законний Casus Belli.")
                                : ("✅ Claim on " + targetName + " fabricated! Legitimate Casus Belli acquired.");
                        CFG.toastM.addM(doneMsg, CFG.COLOR_POSITIVE);
                    }
                }
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
