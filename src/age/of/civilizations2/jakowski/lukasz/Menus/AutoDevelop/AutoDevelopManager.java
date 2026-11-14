package age.of.civilizations2.jakowski.lukasz.Menus.AutoDevelop;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.GameManager;
import age.of.civilizations2.jakowski.lukasz.GameValues.GameValues;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * AutoDevelopManager - автоматичний розвиток економіки та рівня досліджень у провінціях.
 */
public class AutoDevelopManager {
    public static boolean autoDevelopEnabled = false;
    public static boolean autoInvestEconomy = true;
    public static boolean autoInvestDevelopment = true;
    public static int targetEcoPercentage = 100; // цільова економіка: % від населення
    public static boolean showNotifications = true;
    public static int minGoldReserve = 0; // мінімальний резерв скарбниці

    private static boolean configLoaded = false;

    static {
        loadConfig();
    }

    public static void loadConfig() {
        if (configLoaded) return;
        configLoaded = true;
        try {
            File f = new File("auto_develop.cfg");
            if (f.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(f));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (line.startsWith("enabled=")) {
                        autoDevelopEnabled = Boolean.parseBoolean(line.substring(8));
                    } else if (line.startsWith("economy=")) {
                        autoInvestEconomy = Boolean.parseBoolean(line.substring(8));
                    } else if (line.startsWith("development=")) {
                        autoInvestDevelopment = Boolean.parseBoolean(line.substring(12));
                    } else if (line.startsWith("targetEcoPerc=")) {
                        targetEcoPercentage = Integer.parseInt(line.substring(14));
                    } else if (line.startsWith("showNotifications=")) {
                        showNotifications = Boolean.parseBoolean(line.substring(18));
                    } else if (line.startsWith("minGoldReserve=")) {
                        minGoldReserve = Integer.parseInt(line.substring(15));
                    }
                }
                reader.close();
            }
        } catch (Exception ignored) {
        }
    }

    public static void saveConfig() {
        try {
            File f = new File("auto_develop.cfg");
            FileWriter writer = new FileWriter(f);
            writer.write("enabled=" + autoDevelopEnabled + "\n");
            writer.write("economy=" + autoInvestEconomy + "\n");
            writer.write("development=" + autoInvestDevelopment + "\n");
            writer.write("targetEcoPerc=" + targetEcoPercentage + "\n");
            writer.write("showNotifications=" + showNotifications + "\n");
            writer.write("minGoldReserve=" + minGoldReserve + "\n");
            writer.close();
        } catch (Exception ignored) {
        }
    }

    public static int runAutoDevelopTurn() {
        if (!autoDevelopEnabled) {
            return 0;
        }
        int playerCivID = -1;
        try {
            if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                playerCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
            }
        } catch (Exception ex) {
            return 0;
        }
        if (playerCivID <= 0 || CFG.SPECTATOR_MODE) {
            return 0;
        }

        return developForCiv(playerCivID, true);
    }

    public static int developForCiv(final int civID, boolean isTurnAuto) {
        try {
            if (CFG.core == null || CFG.core.getCiv(civID) == null) {
                return 0;
            }

            int numOfProvs = CFG.core.getCiv(civID).getNumOfProvs();
            if (numOfProvs <= 0) {
                return 0;
            }

            List<Integer> provIDs = new ArrayList<Integer>();
            for (int i = 0; i < numOfProvs; i++) {
                int pID = CFG.core.getCiv(civID).getProvID(i);
                if (pID >= 0 && !CFG.core.getProv(pID).getSeaProv() 
                        && CFG.core.getProv(pID).getWastelandLvl() < 0 
                        && !CFG.core.getProv(pID).isOccupied()) {
                    provIDs.add(pID);
                }
            }

            if (provIDs.isEmpty()) {
                return 0;
            }

            int investedCount = 0;
            int ecoCostMove = (GameValues.gvInvestEconomy != null) 
                    ? GameValues.gvInvestEconomy.INVEST_ECO_COST_MOVEMENT_POINTS : 4;
            int devCostMove = (GameValues.gvInvestDevelopment != null) 
                    ? GameValues.gvInvestDevelopment.INVEST_DEVELOPMENT_MOVEMENT_POINTS : 4;

            // 1. АВТО-ЕКОНОМІКА
            if (autoInvestEconomy) {
                Collections.sort(provIDs, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer p1, Integer p2) {
                        int pop1 = CFG.core.getProv(p1.intValue()).getPop().getPops();
                        int pop2 = CFG.core.getProv(p2.intValue()).getPop().getPops();
                        int target1 = (int)((long)pop1 * targetEcoPercentage / 100L);
                        int target2 = (int)((long)pop2 * targetEcoPercentage / 100L);
                        int cur1 = CFG.core.getProv(p1.intValue()).getEco() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft(p1.intValue());
                        int cur2 = CFG.core.getProv(p2.intValue()).getEco() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft(p2.intValue());
                        int def1 = Math.max(0, target1 - cur1);
                        int def2 = Math.max(0, target2 - cur2);
                        return Integer.compare(def2, def1);
                    }
                });

                for (int pID : provIDs) {
                    if (CFG.core.getCiv(civID).getMovemPoints() < ecoCostMove) {
                        break;
                    }
                    if (CFG.core.getCiv(civID).getGold() <= minGoldReserve) {
                        break;
                    }
                    if (CFG.core.getCiv(civID).isInvestOrganized(pID)) {
                        continue;
                    }

                    int pop = CFG.core.getProv(pID).getPop().getPops();
                    int targetEco = (int)Math.max(1L, ((long)pop * targetEcoPercentage) / 100L);
                    int curEco = CFG.core.getProv(pID).getEco() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft(pID);

                    if (curEco >= targetEco) {
                        continue;
                    }

                    int maxGold = GameManager.invest_MaxEconomy_Gold(pID, civID);
                    if (maxGold <= 0) {
                        continue;
                    }

                    int ecoGain = GameManager.invest_EconomyByGold(pID, maxGold);
                    int neededEco = targetEco - curEco;
                    if (ecoGain > neededEco && neededEco > 0) {
                        float ratio = (float)neededEco / (float)ecoGain;
                        maxGold = Math.max(10, (int)((float)maxGold * ratio));
                    }

                    int goldToInvest = (int)Math.min((long)maxGold, CFG.core.getCiv(civID).getGold() - minGoldReserve);
                    if (goldToInvest > 0) {
                        if (GameManager.invest(pID, civID, goldToInvest)) {
                            investedCount++;
                        }
                    }
                }
            }

            // 2. АВТО-РОЗВИТОК (ДОСЛІДЖЕННЯ / ТЕХНОЛОГІЇ)
            if (autoInvestDevelopment) {
                final float techLvl = CFG.core.getCiv(civID).getTechLevel();

                Collections.sort(provIDs, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer p1, Integer p2) {
                        float cur1 = CFG.core.getProv(p1.intValue()).getDeveLvl() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft_Development(p1.intValue());
                        float cur2 = CFG.core.getProv(p2.intValue()).getDeveLvl() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft_Development(p2.intValue());
                        float gap1 = Math.max(0.0f, techLvl - cur1);
                        float gap2 = Math.max(0.0f, techLvl - cur2);
                        return Float.compare(gap2, gap1);
                    }
                });

                for (int pID : provIDs) {
                    if (CFG.core.getCiv(civID).getMovemPoints() < devCostMove) {
                        break;
                    }
                    if (CFG.core.getCiv(civID).getGold() <= minGoldReserve) {
                        break;
                    }
                    if (CFG.core.getCiv(civID).isInvestOrganized_Devel(pID)) {
                        continue;
                    }

                    float curDev = CFG.core.getProv(pID).getDeveLvl() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft_Development(pID);
                    if (curDev >= techLvl) {
                        continue;
                    }

                    int maxDevGold = GameManager.investMaxDevGold(pID, civID);
                    if (maxDevGold <= 0) {
                        continue;
                    }

                    int goldToInvest = (int)Math.min((long)maxDevGold, CFG.core.getCiv(civID).getGold() - minGoldReserve);
                    if (goldToInvest > 0) {
                        if (GameManager.investDevelopment(pID, civID, goldToInvest)) {
                            investedCount++;
                        }
                    }
                }
            }

            if (investedCount > 0 && showNotifications) {
                boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
                String msg = isUkr 
                        ? ("Авто-розвиток: вкладено в " + investedCount + " провінцій") 
                        : ("Auto-Develop: invested in " + investedCount + " provinces");
                CFG.toastM.addM(msg, CFG.COLOR_POSITIVE);
                CFG.toastM.setTimeInView(3000);
            }

            return investedCount;
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
            return 0;
        }
    }
}
