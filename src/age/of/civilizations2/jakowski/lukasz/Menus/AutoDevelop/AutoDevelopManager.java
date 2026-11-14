package age.of.civilizations2.jakowski.lukasz.Menus.AutoDevelop;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Diplomacy.Festivals.Festival;
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
 * AutoDevelopManager - комплексна автоматизація провінцій:
 * - Розвиток економіки
 * - Розвиток інфраструктури науки / технологій
 * - Проведення ярмарок для підвищення щастя
 * - Асиміляція провінцій для стабільності
 * - Гнучкі пріоритети та захисні резерви ресурсів
 */
public class AutoDevelopManager {
    public static final int PRIO_ASSIMILATE = 0;
    public static final int PRIO_FESTIVAL = 1;
    public static final int PRIO_ECONOMY = 2;
    public static final int PRIO_DEVELOPMENT = 3;

    // Пріоритети виконання (порядок за замовчуванням: Асиміляція -> Ярмарки -> Економіка -> Дослідження)
    public static int[] priorities = new int[]{ PRIO_ASSIMILATE, PRIO_FESTIVAL, PRIO_ECONOMY, PRIO_DEVELOPMENT };

    // Головні перемикачі
    public static boolean autoDevelopEnabled = false;
    public static boolean autoAssimilate = true;
    public static boolean autoFestival = true;
    public static boolean autoInvestEconomy = true;
    public static boolean autoInvestDevelopment = true;
    public static boolean showNotifications = true;

    // Пороги та цілі
    public static int targetEcoPercentage = 100; // цільова економіка: % від населення
    public static int targetHappiness = 80;      // проводити ярмарки, якщо щастя < X%
    public static int targetStability = 100;     // асимілювати, якщо стабільність < X%

    // Захисні резерви
    public static int minGoldReserve = 0;        // мінімальний залишок золота в скарбниці
    public static int minMovePointsReserve = 0;  // мінімальний залишок очок ходів (в десятих: 20 = 2.0)

    private static boolean configLoaded = false;

    static {
        loadConfig();
    }

    public static String getPriorityName(int prio, boolean isUkr) {
        switch (prio) {
            case PRIO_ASSIMILATE:
                return isUkr ? "Асиміляція" : "Assimilation";
            case PRIO_FESTIVAL:
                return isUkr ? "Ярмарки" : "Festivals";
            case PRIO_ECONOMY:
                return isUkr ? "Економіка" : "Economy";
            case PRIO_DEVELOPMENT:
                return isUkr ? "Дослідження" : "Research";
            default:
                return "";
        }
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
                    } else if (line.startsWith("assimilate=")) {
                        autoAssimilate = Boolean.parseBoolean(line.substring(11));
                    } else if (line.startsWith("festival=")) {
                        autoFestival = Boolean.parseBoolean(line.substring(9));
                    } else if (line.startsWith("economy=")) {
                        autoInvestEconomy = Boolean.parseBoolean(line.substring(8));
                    } else if (line.startsWith("development=")) {
                        autoInvestDevelopment = Boolean.parseBoolean(line.substring(12));
                    } else if (line.startsWith("targetEcoPerc=")) {
                        targetEcoPercentage = Integer.parseInt(line.substring(14));
                    } else if (line.startsWith("targetHappiness=")) {
                        targetHappiness = Integer.parseInt(line.substring(16));
                    } else if (line.startsWith("targetStability=")) {
                        targetStability = Integer.parseInt(line.substring(16));
                    } else if (line.startsWith("minGoldReserve=")) {
                        minGoldReserve = Integer.parseInt(line.substring(15));
                    } else if (line.startsWith("minMovePointsReserve=")) {
                        minMovePointsReserve = Integer.parseInt(line.substring(21));
                    } else if (line.startsWith("showNotifications=")) {
                        showNotifications = Boolean.parseBoolean(line.substring(18));
                    } else if (line.startsWith("priorities=")) {
                        String[] parts = line.substring(11).split(",");
                        if (parts.length == 4) {
                            for (int i = 0; i < 4; i++) {
                                priorities[i] = Integer.parseInt(parts[i].trim());
                            }
                        }
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
            writer.write("assimilate=" + autoAssimilate + "\n");
            writer.write("festival=" + autoFestival + "\n");
            writer.write("economy=" + autoInvestEconomy + "\n");
            writer.write("development=" + autoInvestDevelopment + "\n");
            writer.write("targetEcoPerc=" + targetEcoPercentage + "\n");
            writer.write("targetHappiness=" + targetHappiness + "\n");
            writer.write("targetStability=" + targetStability + "\n");
            writer.write("minGoldReserve=" + minGoldReserve + "\n");
            writer.write("minMovePointsReserve=" + minMovePointsReserve + "\n");
            writer.write("showNotifications=" + showNotifications + "\n");
            writer.write("priorities=" + priorities[0] + "," + priorities[1] + "," + priorities[2] + "," + priorities[3] + "\n");
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

            int startMovePoints = CFG.core.getCiv(civID).getMovemPoints();
            int startDiploPoints = CFG.core.getCiv(civID).getDiploPoints();
            long startGold = CFG.core.getCiv(civID).getGold();

            int countAssimilate = 0;
            int countFestival = 0;
            int countEconomy = 0;
            int countDev = 0;

            int ecoCostMove = (GameValues.gvInvestEconomy != null) 
                    ? GameValues.gvInvestEconomy.INVEST_ECO_COST_MOVEMENT_POINTS : 12;
            int devCostMove = (GameValues.gvInvestDevelopment != null) 
                    ? GameValues.gvInvestDevelopment.INVEST_DEVELOPMENT_MOVEMENT_POINTS : 8;
            int festCostMove = (GameValues.gvFestival != null)
                    ? GameValues.gvFestival.COST_FESTIVAL_MOVEMENT_POINTS : 8;
            int assiCostDiplo = (GameValues.gvAssimilate != null)
                    ? GameValues.gvAssimilate.COST_ASSIMILATE_MOVEMENT : 4;

            // Виконання за чергою пріоритетів гравця
            for (int pIdx = 0; pIdx < priorities.length; pIdx++) {
                int currentPrio = priorities[pIdx];

                // 1. АСИМІЛЯЦІЯ
                if (currentPrio == PRIO_ASSIMILATE && autoAssimilate) {
                    final float maxStab = (float)targetStability / 100.0f;
                    List<Integer> needAssi = new ArrayList<Integer>();
                    for (int pID : provIDs) {
                        if (CFG.core.getProv(pID).getProviStability() < maxStab 
                                && !CFG.core.getCiv(civID).isAssimilateOrganized(pID)) {
                            needAssi.add(pID);
                        }
                    }

                    // Найменш стабільні провінції спочатку
                    Collections.sort(needAssi, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer p1, Integer p2) {
                            return Float.compare(CFG.core.getProv(p1.intValue()).getProviStability(), 
                                    CFG.core.getProv(p2.intValue()).getProviStability());
                        }
                    });

                    for (int pID : needAssi) {
                        if (CFG.core.getCiv(civID).getDiploPoints() < assiCostDiplo) {
                            break;
                        }
                        if (CFG.core.getCiv(civID).getGold() <= minGoldReserve) {
                            break;
                        }

                        int nMaxTurns = (GameValues.gvAssimilate != null) 
                                ? GameValues.gvAssimilate.ASSIMILATE_NUM_OF_TURNS_MAX : 50;
                        while (nMaxTurns >= 10 && (CFG.core.getCiv(civID).getGold() - minGoldReserve) < GameManager.assimilateCost(pID, nMaxTurns)) {
                            nMaxTurns -= 5;
                        }

                        if (nMaxTurns >= 10 && (CFG.core.getCiv(civID).getGold() - minGoldReserve) >= GameManager.assimilateCost(pID, nMaxTurns)) {
                            if (GameManager.addAssi(civID, pID, nMaxTurns)) {
                                countAssimilate++;
                            }
                        }
                    }
                }

                // 2. ЯРМАРКИ (СВЯТА ДЛЯ ЩАСТЯ)
                else if (currentPrio == PRIO_FESTIVAL && autoFestival) {
                    final float maxHappi = (float)targetHappiness / 100.0f;
                    List<Integer> needFest = new ArrayList<Integer>();
                    for (int pID : provIDs) {
                        if (CFG.core.getProv(pID).getHappi() < maxHappi 
                                && !CFG.core.getCiv(civID).isFestivalOrganized(pID)) {
                            needFest.add(pID);
                        }
                    }

                    // Найменш щасливі провінції спочатку
                    Collections.sort(needFest, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer p1, Integer p2) {
                            return Float.compare(CFG.core.getProv(p1.intValue()).getHappi(), 
                                    CFG.core.getProv(p2.intValue()).getHappi());
                        }
                    });

                    for (int pID : needFest) {
                        if (CFG.core.getCiv(civID).getMovemPoints() < festCostMove + minMovePointsReserve) {
                            break;
                        }
                        int festGold = Festival.festivalCost(pID);
                        if (CFG.core.getCiv(civID).getGold() - minGoldReserve < festGold) {
                            break;
                        }

                        if (Festival.addFestival(civID, pID)) {
                            countFestival++;
                        }
                    }
                }

                // 3. ЕКОНОМІКА
                else if (currentPrio == PRIO_ECONOMY && autoInvestEconomy) {
                    List<Integer> needEco = new ArrayList<Integer>();
                    for (int pID : provIDs) {
                        if (CFG.core.getCiv(civID).isInvestOrganized(pID)) {
                            continue;
                        }
                        int pop = CFG.core.getProv(pID).getPop().getPops();
                        int targetEco = (int)Math.max(1L, ((long)pop * targetEcoPercentage) / 100L);
                        int curEco = CFG.core.getProv(pID).getEco() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft(pID);
                        if (curEco < targetEco) {
                            needEco.add(pID);
                        }
                    }

                    // Найбільший дефіцит економіки спочатку
                    Collections.sort(needEco, new Comparator<Integer>() {
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

                    for (int pID : needEco) {
                        if (CFG.core.getCiv(civID).getMovemPoints() < ecoCostMove + minMovePointsReserve) {
                            break;
                        }
                        if (CFG.core.getCiv(civID).getGold() <= minGoldReserve) {
                            break;
                        }

                        int pop = CFG.core.getProv(pID).getPop().getPops();
                        int targetEco = (int)Math.max(1L, ((long)pop * targetEcoPercentage) / 100L);
                        int curEco = CFG.core.getProv(pID).getEco() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft(pID);

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
                                countEconomy++;
                            }
                        }
                    }
                }

                // 4. ДОСЛІДЖЕННЯ ТА ТЕХНОЛОГІЇ
                else if (currentPrio == PRIO_DEVELOPMENT && autoInvestDevelopment) {
                    final float techLvl = CFG.core.getCiv(civID).getTechLevel();
                    List<Integer> needDev = new ArrayList<Integer>();
                    for (int pID : provIDs) {
                        if (CFG.core.getCiv(civID).isInvestOrganized_Devel(pID)) {
                            continue;
                        }
                        float curDev = CFG.core.getProv(pID).getDeveLvl() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft_Development(pID);
                        if (curDev < techLvl) {
                            needDev.add(pID);
                        }
                    }

                    // Найбільший розрив з рівнем технологій спочатку
                    Collections.sort(needDev, new Comparator<Integer>() {
                        @Override
                        public int compare(Integer p1, Integer p2) {
                            float cur1 = CFG.core.getProv(p1.intValue()).getDeveLvl() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft_Development(p1.intValue());
                            float cur2 = CFG.core.getProv(p2.intValue()).getDeveLvl() + CFG.core.getCiv(civID).isInvestOrganized_EconomyLeft_Development(p2.intValue());
                            float gap1 = Math.max(0.0f, techLvl - cur1);
                            float gap2 = Math.max(0.0f, techLvl - cur2);
                            return Float.compare(gap2, gap1);
                        }
                    });

                    for (int pID : needDev) {
                        if (CFG.core.getCiv(civID).getMovemPoints() < devCostMove + minMovePointsReserve) {
                            break;
                        }
                        if (CFG.core.getCiv(civID).getGold() <= minGoldReserve) {
                            break;
                        }

                        int maxDevGold = GameManager.investMaxDevGold(pID, civID);
                        if (maxDevGold <= 0) {
                            continue;
                        }

                        int goldToInvest = (int)Math.min((long)maxDevGold, CFG.core.getCiv(civID).getGold() - minGoldReserve);
                        if (goldToInvest > 0) {
                            if (GameManager.investDevelopment(pID, civID, goldToInvest)) {
                                countDev++;
                            }
                        }
                    }
                }
            }

            int totalActions = countAssimilate + countFestival + countEconomy + countDev;

            // Оновлюємо інтерфейс верхньої панелі та інформації про провінцію
            if (totalActions > 0 || !isTurnAuto) {
                try {
                    if (CFG.menus != null) {
                        CFG.menus.updateInGameTopAll(civID);
                    }
                    if (CFG.gameAction != null) {
                        CFG.gameAction.updateInGame_ProvinceInfo();
                    }
                } catch (Exception ignored) {
                }
            }

            // Сповіщення про виконані дії
            if (totalActions > 0 && showNotifications) {
                boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
                float spentMove = Math.max(0.0f, (float)(startMovePoints - CFG.core.getCiv(civID).getMovemPoints()) / 10.0f);
                float spentDiplo = Math.max(0.0f, (float)(startDiploPoints - CFG.core.getCiv(civID).getDiploPoints()) / 10.0f);
                long spentGold = Math.max(0L, startGold - CFG.core.getCiv(civID).getGold());

                StringBuilder sb = new StringBuilder();
                if (isUkr) {
                    sb.append("Авто-розвиток: ").append(totalActions).append(" дій");
                    sb.append(" (");
                    boolean first = true;
                    if (countAssimilate > 0) { sb.append(countAssimilate).append(" асим."); first = false; }
                    if (countFestival > 0) { if (!first) sb.append(", "); sb.append(countFestival).append(" ярм."); first = false; }
                    if (countEconomy > 0) { if (!first) sb.append(", "); sb.append(countEconomy).append(" екон."); first = false; }
                    if (countDev > 0) { if (!first) sb.append(", "); sb.append(countDev).append(" розв."); }
                    sb.append(") | ");
                    if (spentMove > 0) sb.append("-").append(CFG.getPrecision2(spentMove, 10)).append(" ходів ");
                    if (spentDiplo > 0) sb.append("-").append(CFG.getPrecision2(spentDiplo, 10)).append(" дипл. ");
                    if (spentGold > 0) sb.append("-").append(spentGold).append(" зол.");
                } else {
                    sb.append("Auto-Develop: ").append(totalActions).append(" actions");
                    sb.append(" (");
                    boolean first = true;
                    if (countAssimilate > 0) { sb.append(countAssimilate).append(" assim."); first = false; }
                    if (countFestival > 0) { if (!first) sb.append(", "); sb.append(countFestival).append(" fest."); first = false; }
                    if (countEconomy > 0) { if (!first) sb.append(", "); sb.append(countEconomy).append(" eco."); first = false; }
                    if (countDev > 0) { if (!first) sb.append(", "); sb.append(countDev).append(" dev."); }
                    sb.append(") | ");
                    if (spentMove > 0) sb.append("-").append(CFG.getPrecision2(spentMove, 10)).append(" mov. ");
                    if (spentDiplo > 0) sb.append("-").append(CFG.getPrecision2(spentDiplo, 10)).append(" diplo ");
                    if (spentGold > 0) sb.append("-").append(spentGold).append(" gold");
                }
                CFG.toastM.addM(sb.toString(), CFG.COLOR_POSITIVE);
                CFG.toastM.setTimeInView(4000);
            }

            return totalActions;
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
            return 0;
        }
    }
}
