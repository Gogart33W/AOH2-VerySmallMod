package age.of.civilizations2.jakowski.lukasz.NextGen;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.GameCalendar;
import age.of.civilizations2.jakowski.lukasz.Province;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * RevolutionManager - система ідеологічних криз, революцій та повномасштабних громадянських війн.
 * 
 * 1. Моніторинг стабільності та народного гніву:
 *    - Якщо середня стабільність держави падає нижче 35% і щастя < 30%, країна входить у революційну кризу.
 * 2. Розкол держави (Civil War Fracture):
 *    - Замість пасивних повстанців утворюється повноцінна політична фракція (Revolutionary Faction).
 *    - 30%-45% найбільш нестабільних провінцій переходять під прапор опозиції.
 *    - Формується революційна армія, обирається повстанська столиця і розпочинається війна за владу.
 * 3. Возз'єднання та відновлення порядку:
 *    - Після розгрому повстанської фракції держава возз'єднується з бонусом стабільності (+25%).
 */
public class RevolutionManager {
    public static boolean revolutionsEnabled = true;

    // Відстеження активних громадянських війн: loyalistCivID -> rebelCivID
    private static final Map<Integer, Integer> activeCivilWars = new ConcurrentHashMap<Integer, Integer>();

    // Лічильник критичної нестабільності в ходах: civID -> consecutiveTurnsUnstable
    private static final Map<Integer, Integer> unstableTurns = new ConcurrentHashMap<Integer, Integer>();

    /**
     * Щоходова перевірка революційного стану всіх держав світу.
     */
    public static void checkRevolutionsTurn() {
        if (!revolutionsEnabled) return;
        try {
            int numCivs = CFG.core.getCivsSize();

            // 1. Перевірка завершення активних громадянських війн
            List<Integer> finishedWars = new ArrayList<Integer>();
            for (Map.Entry<Integer, Integer> entry : activeCivilWars.entrySet()) {
                int loyalistID = entry.getKey();
                int rebelID = entry.getValue();

                if (CFG.core.getCiv(rebelID).getNumOfProvs() == 0) {
                    finishedWars.add(loyalistID);
                    onCivilWarWon(loyalistID, rebelID);
                } else if (CFG.core.getCiv(loyalistID).getNumOfProvs() == 0) {
                    finishedWars.add(loyalistID);
                    onRevolutionVictory(rebelID, loyalistID);
                }
            }
            for (int finished : finishedWars) {
                activeCivilWars.remove(finished);
            }

            // 2. Моніторинг нестабільності та спалаху нових громадянських війн
            for (int i = 1; i < numCivs; i++) {
                if (CFG.core.getCiv(i).getNumOfProvs() < 4) continue; // Карликові країни не розколюються
                if (activeCivilWars.containsKey(i) || activeCivilWars.containsValue(i)) continue;

                float stab = CFG.core.getCiv(i).getStabilityCiv();
                int happi = CFG.core.getCiv(i).getHappiness();

                if (stab < 0.38f && happi < 38) {
                    int turns = unstableTurns.get(i) != null ? unstableTurns.get(i) + 1 : 1;
                    unstableTurns.put(i, turns);

                    // Якщо держава нестабільна 3 ходи поспіль — спалахує громадянська війна
                    if (turns >= 3) {
                        triggerCivilWar(i);
                        unstableTurns.remove(i);
                    }
                } else {
                    unstableTurns.remove(i);
                }
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    /**
     * Запускає розкол країни та початок громадянської війни.
     */
    public static boolean triggerCivilWar(final int civID) {
        try {
            int numProvs = CFG.core.getCiv(civID).getNumOfProvs();
            if (numProvs < 4) return false;

            // Шукаємо вільну цивілізацію (з 0 провінцій) для ролі повстанської фракції
            int rebelCivID = -1;
            int totalCivs = CFG.core.getCivsSize();
            for (int i = 1; i < totalCivs; i++) {
                if (i != civID && CFG.core.getCiv(i).getNumOfProvs() == 0 && !activeCivilWars.containsValue(i)) {
                    rebelCivID = i;
                    break;
                }
            }
            if (rebelCivID <= 0) return false;

            // Збираємо провінції цивілізації та сортуємо за найменшою стабільністю
            List<Integer> provList = new ArrayList<Integer>();
            int capProv = CFG.core.getCiv(civID).getCapitalProvID();

            for (int i = 0; i < numProvs; i++) {
                int pID = CFG.core.getCiv(civID).getProvID(i);
                if (pID >= 0 && pID != capProv && !CFG.core.getProv(pID).getSeaProv()) {
                    provList.add(pID);
                }
            }

            Collections.sort(provList, new Comparator<Integer>() {
                @Override
                public int compare(Integer p1, Integer p2) {
                    return Float.compare(CFG.core.getProv(p1).getProviStability(), CFG.core.getProv(p2).getProviStability());
                }
            });

            // Відриваємо 35-45% найбільш бунтівних провінцій
            int splitCount = Math.max(1, (int)((float)provList.size() * 0.40f));
            List<Integer> rebelProvinces = new ArrayList<Integer>();
            for (int i = 0; i < splitCount; i++) {
                rebelProvinces.add(provList.get(i));
            }

            // Передаємо провінції повстанській фракції
            int rebelCapProv = rebelProvinces.get(0);
            int totalRebelArmy = 0;

            for (int pID : rebelProvinces) {
                int defArmy = Math.max(500, (int)((float)CFG.core.getProv(pID).getPop().getPops() * 0.035f));
                totalRebelArmy += defArmy;

                CFG.core.getProv(pID).updateArmy4(civID, 0);
                CFG.core.getProv(pID).setTrueOwnerOfProv(rebelCivID);
                CFG.core.getProv(pID).setCivId(rebelCivID, false);
                CFG.core.getProv(pID).updateArmy4(rebelCivID, defArmy);
                CFG.core.getProv(pID).setHappi(0.75f);
                CFG.core.getProv(pID).setRevRisk(0.0f);
            }

            // Встановлюємо столицю повстанців
            CFG.core.getProv(rebelCapProv).setIsCapital(true);
            CFG.core.getCiv(rebelCivID).setCapitalProvID(rebelCapProv);
            CFG.core.getCiv(rebelCivID).updateNumberOfUnits();
            CFG.core.getCiv(civID).updateNumberOfUnits();

            CFG.core.buildCivilizationsRegions_TextOver(civID);
            CFG.core.buildCivilizationsRegions_TextOver(rebelCivID);

            // Оголошуємо тотальну громадянську війну між лоялістами та повстанцями
            CFG.core.declareWar(rebelCivID, civID, false);
            activeCivilWars.put(civID, rebelCivID);

            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;
            String countryName = CFG.core.getCiv(civID).getCivName();

            String warNotice = isUkr
                    ? ("💥 ГРОМАДЯНСЬКА ВІЙНА: У державі " + countryName + " спалахнула революція! " + splitCount + " провінцій повстали проти уряду.")
                    : ("💥 CIVIL WAR: Revolution broke out in " + countryName + "! " + splitCount + " provinces rose up against the government.");

            CFG.toastM.addM(warNotice, CFG.COLOR_NEGATIVE_2);
            CFG.toastM.setTimeInView(6000);

            return true;
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
            return false;
        }
    }

    /**
     * Перемога лоялістів: возз'єднання нації та відновлення порядку.
     */
    private static void onCivilWarWon(int loyalistID, int rebelID) {
        try {
            float curStab = CFG.core.getCiv(loyalistID).getStabilityCiv();
            CFG.core.getCiv(loyalistID).setStabilityCiv(Math.min(1.0f, curStab + 0.25f));

            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;
            String name = CFG.core.getCiv(loyalistID).getCivName();

            String winMsg = isUkr
                    ? ("🎉 ПЕРЕМОГА В ГРОМАДЯНСЬКІЙ ВІЙНІ: Заколот у " + name + " повністю придушено! Національну стабільність підвищено (+25%).")
                    : ("🎉 CIVIL WAR VICTORY: Rebellion in " + name + " has been suppressed! National stability increased (+25%).");

            CFG.toastM.addM(winMsg, CFG.COLOR_POSITIVE);
            CFG.toastM.setTimeInView(5000);
        } catch (Exception ignored) {}
    }

    /**
     * Перемога революції: повстанська фракція бере під контроль усю державу.
     */
    private static void onRevolutionVictory(int rebelID, int loyalistID) {
        try {
            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;
            String revMsg = isUkr
                    ? "✊ РЕВОЛЮЦІЯ ПЕРЕМОГЛА: Старий режим повалено, владу перебрали революційні сили!"
                    : "✊ REVOLUTION TRIUMPHS: The old regime was overthrown, revolutionary forces have taken power!";
            CFG.toastM.addM(revMsg, CFG.COLOR_POSITIVE);
            CFG.toastM.setTimeInView(5000);
        } catch (Exception ignored) {}
    }
}
