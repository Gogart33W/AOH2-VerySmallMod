package age.of.civilizations2.jakowski.lukasz.NextGen;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.GameCalendar;
import age.of.civilizations2.jakowski.lukasz.MapA.BuildingsManager;
import age.of.civilizations2.jakowski.lukasz.Province;
import java.util.ArrayList;
import java.util.List;

/**
 * LogisticsManager - реалістична система постачання та військового виснаження (Attrition).
 * 
 * 1. Ліміт постачання провінції (Supply Limit):
 *    - Залежить від населення, економіки, розвитку доріг/інфраструктури, рівня форту та табору постачання.
 *    - Гори, пустелі, болота та снігові пустки мають суворі штрафи до ліміту.
 * 2. Виснаження (Attrition):
 *    - Перевищення ліміту військ спричиняє щоходові небойові втрати (3% - 7%).
 *    - Перебування на ворожій або окупованій території без табору постачання викликає додаткове виснаження.
 * 3. Логістична інфраструктура (Залізниця / Дороги / Табори):
 *    - Рівень технологічного розвитку провінції (DeveLvl) суттєво підвищує пропускну здатність постачання.
 *    - Зведення таборів постачання захищає війська від виснаження.
 */
public class LogisticsManager {
    public static boolean logisticsEnabled = true;
    public static boolean autoBuildSupplyCamps = true;
    public static int lastTurnPlayerAttritionLosses = 0;

    /**
     * Розраховує ліміт постачання (в чисельності вояків) для заданої провінції та цивілізації.
     */
    public static int getProvinceSupplyLimit(int provID, int civID) {
        try {
            if (provID < 0 || provID >= CFG.core.getProvinSize()) return 5000;
            Province p = CFG.core.getProv(provID);
            if (p.getSeaProv()) return 20000; // Морські провінції мають морський ліміт флоту/десанту
            if (p.getWastelandLvl() >= 0) return 500; // Пустка

            int pop = p.getPop().getPops();
            int eco = p.getEco();
            float deve = p.getDeveLvl();
            int supplyLvl = p.getLvlOfSupply();
            int fortLvl = p.getLvlOfFort();

            // Базова ємність від цивільного населення та економічної активності
            float baseCap = (float)pop / 1000.0f + (float)eco * 1.5f;

            // Множник доріг та технологічного розвитку провінції (інфраструктура)
            float infraMult = 1.0f + Math.min(1.0f, deve * 0.45f);

            // Множник рельєфу місцевості (Terrain)
            float terrainMult = 1.0f;
            int terrain = p.getTerrainTypeID();
            // Залежно від ID рельєфу (гори, пустеля, джунглі - суворі умови)
            if (terrain == 1 || terrain == 2) {
                terrainMult = 0.65f; // Гори / височини
            } else if (terrain == 3 || terrain == 4) {
                terrainMult = 0.55f; // Пустеля / сухий степ
            } else if (terrain == 5) {
                terrainMult = 0.70f; // Болота / хащі
            } else if (terrain == 0) {
                terrainMult = 1.15f; // Рівнини / родючі поля
            }

            float rawLimit = baseCap * infraMult * terrainMult;

            // Бонуси спеціальних споруд
            rawLimit += (float)supplyLvl * 6500.0f; // Табір постачання дає +6500 ліміту
            rawLimit += (float)fortLvl * 2500.0f;   // Форт забезпечує військові склади на +2500

            // Перевірка контролю над провінцією та ворожості місцевості
            int provCiv = p.getCivId();
            if (provCiv != civID) {
                boolean atWar = false;
                try {
                    atWar = CFG.core.getCivsAtWar(civID, provCiv);
                } catch (Exception ignored) {}

                if (atWar) {
                    // Ворожа територія: місцеве населення чинить опір, лінії постачання розтягнуті
                    // Якщо немає власного табору постачання — ємність падає на 60%
                    if (supplyLvl == 0) {
                        rawLimit *= 0.40f;
                    } else {
                        rawLimit *= 0.75f;
                    }
                } else {
                    // Чужа мирна територія з доступом / васал
                    rawLimit *= 0.85f;
                }
            }

            return Math.max(1200, (int)rawLimit);
        } catch (Exception ex) {
            return 5000;
        }
    }

    /**
     * Щоходова симуляція логістики: виснаження армій, втрати, логістичні сповіщення.
     */
    public static void runLogisticsTurn() {
        if (!logisticsEnabled) return;
        try {
            int playerCivID = -1;
            try {
                if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                    playerCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
                }
            } catch (Exception ignored) {}

            int playerTotalAttritionLosses = 0;
            int numProvinces = CFG.core.getProvinSize();

            for (int i = 0; i < numProvinces; i++) {
                Province p = CFG.core.getProv(i);
                if (p.getSeaProv() || p.getWastelandLvl() >= 0) continue;

                int civsSize = p.getCivsSize();
                if (civsSize <= 0) continue;

                // Збираємо кожну армію в провінції
                for (int c = 0; c < civsSize; c++) {
                    int armyCivID = p.getCivId(c);
                    if (armyCivID <= 0) continue;

                    int armyCount = p.getArmyCivID1(armyCivID);
                    if (armyCount <= 50) continue;

                    int supplyLimit = getProvinceSupplyLimit(i, armyCivID);
                    int losses = 0;

                    // 1. Виснаження від перевантаження провінції (Over-capacity Attrition)
                    if (armyCount > supplyLimit) {
                        int excess = armyCount - supplyLimit;
                        float excessRatio = (float)excess / (float)supplyLimit;
                        float attritionRate = Math.min(0.08f, 0.03f + excessRatio * 0.04f);
                        losses += Math.max(1, (int)((float)excess * attritionRate));
                    }

                    // 2. Виснаження на ворожій/чужій території при відсутності табору постачання
                    int provOwner = p.getCivId();
                    if (provOwner != armyCivID && provOwner > 0) {
                        boolean isWar = false;
                        try {
                            isWar = CFG.core.getCivsAtWar(armyCivID, provOwner);
                        } catch (Exception ignored) {}

                        if (isWar && p.getLvlOfSupply() == 0) {
                            // Ворожий глибокий тил без логістичної бази: базова втрата 1.5% щоходу
                            losses += Math.max(1, (int)((float)armyCount * 0.015f));
                        }
                    }

                    if (losses > 0) {
                        int newArmy = Math.max(0, armyCount - losses);
                        p.updateArmy4(armyCivID, newArmy);

                        if (armyCivID == playerCivID) {
                            playerTotalAttritionLosses += losses;
                        }

                        // ШІ або авто-будівництво: якщо армія велика і страждає від виснаження, будуємо табір постачання
                        if (armyCount >= 10000 && p.getLvlOfSupply() < BuildingsManager.getSupply_MaxLevel()) {
                            if (provOwner == armyCivID && BuildingsManager.canBuildSupply(i)) {
                                if (CFG.core.getCiv(armyCivID).getGold() >= BuildingsManager.getSupply_BuildCost(armyCivID, i) * 2) {
                                    BuildingsManager.buildSupply(armyCivID, i);
                                }
                            }
                        }
                    }
                }
            }

            lastTurnPlayerAttritionLosses = playerTotalAttritionLosses;

            // Сповіщення гравця про логістичні втрати
            if (playerTotalAttritionLosses > 0 && playerCivID > 0) {
                boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                        ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                        : true;

                String msg = isUkr
                        ? ("⚠️ Військове виснаження: втрачено " + CFG.getNumberWthSpaces("" + playerTotalAttritionLosses) + " вояків через дефіцит постачання! Розосередьте війська або зведіть табори постачання.")
                        : ("⚠️ Military Attrition: lost " + CFG.getNumberWthSpaces("" + playerTotalAttritionLosses) + " soldiers due to lack of supplies! Disperse troops or build supply camps.");

                CFG.toastM.addM(msg, CFG.COLOR_NEGATIVE_2);
                CFG.toastM.setTimeInView(4500);
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
