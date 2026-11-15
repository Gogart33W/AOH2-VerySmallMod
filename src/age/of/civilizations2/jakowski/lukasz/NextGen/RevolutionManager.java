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

            // 2. Моніторинг нестабільності та попередження про загрозу революції
            int playerCiv = -1;
            try {
                if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                    playerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
                }
            } catch (Exception ignored) {}

            for (int i = 1; i < numCivs; i++) {
                if (CFG.core.getCiv(i).getNumOfProvs() < 4) continue; // Карликові країни не розколюються
                if (activeCivilWars.containsKey(i) || activeCivilWars.containsValue(i)) continue;

                float stab = CFG.core.getCiv(i).getStabilityCiv();
                int happi = CFG.core.getCiv(i).getHappiness();

                if (stab < 0.38f && happi < 38) {
                    int turns = unstableTurns.get(i) != null ? unstableTurns.get(i) + 1 : 1;
                    unstableTurns.put(i, turns);

                    // Поетапні попередження гравця
                    if (i == playerCiv) {
                        boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                                ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                                : true;
                        if (turns == 1) {
                            String msg1 = isUkr
                                    ? "⚠️ КРИТИЧНЕ НЕВДОВОЛЕННЯ (1/3 ходів): Стабільність <38%! Народ бунтує. Проведіть ярмарки або інвестуйте, щоб уникнути революції!"
                                    : "⚠️ CRITICAL UNREST (1/3 turns): Stability <38%! People are angry. Hold festivals or invest to avoid revolution!";
                            CFG.toastM.addM(msg1, CFG.COLOR_NEGATIVE_2);
                            CFG.toastM.setTimeInView(5000);
                        } else if (turns == 2) {
                            String msg2 = isUkr
                                    ? "🚨 ПЕРЕДРЕВОЛЮЦІЙНИЙ СТАН (2/3 ходів): Наступного ходу розпочнеться громадянська війна! Терміново підніміть стабільність та щастя!"
                                    : "🚨 PRE-REVOLUTION CRISIS (2/3 turns): Civil war will break out next turn! Urgently raise stability and happiness!";
                            CFG.toastM.addM(msg2, CFG.COLOR_NEGATIVE_2);
                            CFG.toastM.setTimeInView(5500);
                            try {
                                CFG.SFXManager.playSound(age.of.civilizations2.jakowski.lukasz.SFXManager.SFX_WAR);
                            } catch (Exception ignored) {}
                        }
                    }

                    // Якщо держава нестабільна 3 ходи поспіль — спалахує громадянська війна
                    if (turns >= 3) {
                        triggerCivilWar(i);
                        unstableTurns.remove(i);
                    }
                } else {
                    if (unstableTurns.containsKey(i)) {
                        unstableTurns.remove(i);
                        if (i == playerCiv) {
                            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                                    : true;
                            String resolved = isUkr
                                    ? "🕊️ Кризу подолано: Громадський спокій відновлено, загрозу революції ліквідовано!"
                                    : "🕊️ Crisis resolved: Public order restored, revolution threat averted!";
                            CFG.toastM.addM(resolved, CFG.COLOR_POSITIVE);
                            CFG.toastM.setTimeInView(4000);
                        }
                    }
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

            // Налаштовуємо ідентичність революційної фракції (назва, ідеологія, кольори)
            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;
            String motherName = CFG.core.getCiv(civID).getCivName();
            int motherIdeology = CFG.core.getCiv(civID).getIdeology();

            int rebelIdeology = 1; // Республіка / Демократія за дефолтом
            String rebelName;
            if (motherIdeology == 0) {
                // Якщо була монархія — революція встановлює республіку
                rebelIdeology = 1;
                rebelName = isUkr ? ("Революційна " + motherName) : ("Revolutionary " + motherName);
            } else if (motherIdeology == 1) {
                // Якщо була республіка — радикальні повстанці
                rebelIdeology = 2; // Монархічна хунта або автократія
                rebelName = isUkr ? ("Повстанська Армія (" + motherName + ")") : ("Rebel Front of " + motherName);
            } else {
                rebelIdeology = 1;
                rebelName = isUkr ? ("Визвольний Рух (" + motherName + ")") : ("Liberation Movement of " + motherName);
            }

            try {
                CFG.core.getCiv(rebelCivID).setCivName(rebelName);
                CFG.core.getCiv(rebelCivID).setIdeology(rebelIdeology);
                CFG.core.getCiv(rebelCivID).setR(215);
                CFG.core.getCiv(rebelCivID).setG(35);
                CFG.core.getCiv(rebelCivID).setB(35);
                CFG.core.getCiv(rebelCivID).setGold(3500L);
            } catch (Exception ignored) {}

            // Встановлюємо столицю повстанців
            CFG.core.getProv(rebelCapProv).setIsCapital(true);
            CFG.core.getCiv(rebelCivID).setCapitalProvID(rebelCapProv);
            CFG.core.getCiv(rebelCivID).updateNumberOfUnits();
            CFG.core.getCiv(civID).updateNumberOfUnits();

            // Безпечно перераховуємо межі обох цивілізацій
            CFG.core.buildCivilizationRegions(civID);
            CFG.core.buildCivilizationRegions(rebelCivID);
            CFG.core.buildCivilizationsRegions_TextOver(civID);
            CFG.core.buildCivilizationsRegions_TextOver(rebelCivID);

            // Оголошуємо тотальну громадянську війну між лоялістами та повстанцями
            CFG.core.declareWar(rebelCivID, civID, false);
            activeCivilWars.put(civID, rebelCivID);

            try {
                CFG.SFXManager.playSound(age.of.civilizations2.jakowski.lukasz.SFXManager.SFX_WAR);
            } catch (Exception ignored) {}

            String warNotice = isUkr
                    ? ("🔥 ГРОМАДЯНСЬКА ВІЙНА: У державі " + motherName + " спалахнула революція! " + splitCount + " провінцій проголосили утворення '" + rebelName + "'!")
                    : ("🔥 CIVIL WAR: Revolution broke out in " + motherName + "! " + splitCount + " provinces formed '" + rebelName + "'!");

            CFG.toastM.addM(warNotice, CFG.COLOR_NEGATIVE_2);
            CFG.toastM.setTimeInView(6500);

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
