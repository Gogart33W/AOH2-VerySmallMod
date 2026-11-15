package age.of.civilizations2.jakowski.lukasz.NextGen;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.GameCalendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VassalAutonomyManager - поглиблена система управління васалами та шкала автономії:
 * 
 * 4 Рівні автономії:
 * - Рівень 1 (75%-100%): Протекторат / Військова марка (висока автономія, символічна данина 5%, сильний військовий внесок).
 * - Рівень 2 (50%-74%): Васальне князівство (класичний васал, данина 15% золота).
 * - Рівень 3 (25%-49%): Залежна колонія / Домініон (висока інтеграція, данина 25% золота, право будівництва).
 * - Рівень 4 (0%-24%): Інтегрована автономія (готова до мирної анексії, данина 35% золота).
 * 
 * Заборона на сепаратні альянси: васали не можуть укладати союзи без відома сюзерена.
 */
public class VassalAutonomyManager {
    // Збереження поточної автономії для кожного васала: vassalCivID -> float (0.0 to 100.0)
    private static final Map<Integer, Float> autonomy = new ConcurrentHashMap<Integer, Float>();

    public enum AutonomyLevel {
        PROTECTORATE,  // Протекторат
        VASSAL,        // Васал
        DEPENDENCY,    // Залежна територія / Колонія
        INTEGRATED     // Інтегрована автономія
    }

    /**
     * Отримує поточну автономію васала (дефолт 65.0%).
     */
    public static float getAutonomy(int vassalCivID) {
        Float val = autonomy.get(vassalCivID);
        if (val == null) {
            val = Float.valueOf(65.0f);
            autonomy.put(vassalCivID, val);
        }
        return val.floatValue();
    }

    public static void setAutonomy(int vassalCivID, float newAutonomy) {
        autonomy.put(vassalCivID, Math.max(0.0f, Math.min(100.0f, newAutonomy)));
    }

    public static void removeVassal(int vassalCivID) {
        autonomy.remove(vassalCivID);
    }

    /**
     * Зниження автономії васала сюзереном (прискорює анексію, але дратує васала).
     * Вартість: 250 золота. Знижує автономію на 10%, підвищує бажання свободи на 12%, погіршує стосунки на -12.
     */
    public static boolean decreaseAutonomy(int vassalCivID, int lordCivID) {
        try {
            if (vassalCivID <= 0 || lordCivID <= 0) return false;
            long lordGold = CFG.core.getCiv(lordCivID).getGold();
            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;

            if (lordGold < 250L) {
                CFG.toastM.addM(isUkr ? "Недостатньо золота! Потрібно 250 золота для централізації влади." : "Not enough gold! 250 gold required to centralize power.", CFG.COLOR_NEGATIVE_2);
                return false;
            }

            float curAut = getAutonomy(vassalCivID);
            if (curAut <= 5.0f) {
                CFG.toastM.addM(isUkr ? "Автономія вже на мінімальному рівні!" : "Autonomy is already at minimum!", CFG.COLOR_NEGATIVE_2);
                return false;
            }

            CFG.core.getCiv(lordCivID).setGold(lordGold - 250L);
            setAutonomy(vassalCivID, curAut - 10.0f);

            // Підвищення бажання свободи та погіршення стосунків
            float curLib = CFG.core.getCiv(vassalCivID).getVassalLibertyDesire();
            CFG.core.getCiv(vassalCivID).setVassalLibertyDesire(Math.min(100.0f, curLib + 12.0f));

            float curRel = CFG.core.getCiv(vassalCivID).getRelationD(lordCivID);
            CFG.core.getCiv(vassalCivID).setRelationD(lordCivID, Math.max(-100.0f, curRel - 12.0f));

            String vassalName = CFG.core.getCiv(vassalCivID).getCivName();
            String msg = isUkr
                    ? ("📉 Автономію " + vassalName + " знижено до " + CFG.getPrecision2(getAutonomy(vassalCivID), 10) + "%! Бажання свободи: " + (int)(curLib + 12.0f) + "%.")
                    : ("📉 Autonomy of " + vassalName + " reduced to " + CFG.getPrecision2(getAutonomy(vassalCivID), 10) + "%! Liberty desire: " + (int)(curLib + 12.0f) + "%.");

            CFG.toastM.addM(msg, CFG.COLOR_POSITIVE);
            try {
                CFG.SFXManager.playSound(age.of.civilizations2.jakowski.lukasz.SFXManager.SFX_CLICK3);
            } catch (Exception ignored) {}
            return true;
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
            return false;
        }
    }

    /**
     * Надання привілеїв васалу (підвищує автономію на 10%, але заспокоює його: -20% бажання свободи, +25 стосунки).
     */
    public static boolean increaseAutonomy(int vassalCivID, int lordCivID) {
        try {
            if (vassalCivID <= 0 || lordCivID <= 0) return false;
            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;

            float curAut = getAutonomy(vassalCivID);
            if (curAut >= 95.0f) {
                CFG.toastM.addM(isUkr ? "Автономія вже на максимальному рівні!" : "Autonomy is already at maximum!", CFG.COLOR_NEGATIVE_2);
                return false;
            }

            setAutonomy(vassalCivID, curAut + 10.0f);

            // Зниження бажання свободи та покращення стосунків
            float curLib = CFG.core.getCiv(vassalCivID).getVassalLibertyDesire();
            CFG.core.getCiv(vassalCivID).setVassalLibertyDesire(Math.max(0.0f, curLib - 20.0f));

            float curRel = CFG.core.getCiv(vassalCivID).getRelationD(lordCivID);
            CFG.core.getCiv(vassalCivID).setRelationD(lordCivID, Math.min(100.0f, curRel + 25.0f));

            String vassalName = CFG.core.getCiv(vassalCivID).getCivName();
            String msg = isUkr
                    ? ("📈 " + vassalName + " надано нові привілеї та автономію (" + CFG.getPrecision2(getAutonomy(vassalCivID), 10) + "%). Бажання свободи впало до " + (int)Math.max(0.0f, curLib - 20.0f) + "%, стосунки +25!")
                    : ("📈 Granted privileges to " + vassalName + " (Autonomy: " + CFG.getPrecision2(getAutonomy(vassalCivID), 10) + "%). Liberty desire fell to " + (int)Math.max(0.0f, curLib - 20.0f) + "%, relations +25!");

            CFG.toastM.addM(msg, CFG.COLOR_POSITIVE);
            try {
                CFG.SFXManager.playSound(age.of.civilizations2.jakowski.lukasz.SFXManager.SFX_CLICK3);
            } catch (Exception ignored) {}
            return true;
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
            return false;
        }
    }

    /**
     * Визначає рівень статусу васала.
     */
    public static AutonomyLevel getAutonomyLevel(int vassalCivID) {
        float aut = getAutonomy(vassalCivID);
        if (aut >= 75.0f) return AutonomyLevel.PROTECTORATE;
        if (aut >= 50.0f) return AutonomyLevel.VASSAL;
        if (aut >= 25.0f) return AutonomyLevel.DEPENDENCY;
        return AutonomyLevel.INTEGRATED;
    }

    public static String getAutonomyLevelName(int vassalCivID, boolean isUkr) {
        AutonomyLevel lvl = getAutonomyLevel(vassalCivID);
        float val = getAutonomy(vassalCivID);
        switch (lvl) {
            case PROTECTORATE:
                return (isUkr ? "Протекторат / Марка (" : "Protectorate (") + CFG.getPrecision2(val, 10) + "%)";
            case VASSAL:
                return (isUkr ? "Васальне князівство (" : "Vassal State (") + CFG.getPrecision2(val, 10) + "%)";
            case DEPENDENCY:
                return (isUkr ? "Залежна колонія (" : "Dependency / Colony (") + CFG.getPrecision2(val, 10) + "%)";
            case INTEGRATED:
                return (isUkr ? "Інтегрована автономія (" : "Integrated Territory (") + CFG.getPrecision2(val, 10) + "%)";
            default:
                return "";
        }
    }

    /**
     * Щоходова обробка васальних відносин: збір данини, зміна автономії, контроль союзів.
     */
    public static void runVassalAutonomyTurn() {
        try {
            int numCivs = CFG.core.getCivsSize();
            int playerCiv = -1;
            try {
                if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                    playerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
                }
            } catch (Exception ignored) {}

            long totalTributeForPlayer = 0L;

            for (int vCiv = 1; vCiv < numCivs; vCiv++) {
                int lordCiv = CFG.core.getCiv(vCiv).getPuppetOfCiv();
                if (lordCiv <= 0 || lordCiv == vCiv) continue; // Не є васалом
                if (CFG.core.getCiv(vCiv).getNumOfProvs() <= 0) continue;

                // 1. Заборона на самостійні сепаратні альянси
                int vassalAlliance = CFG.core.getCiv(vCiv).getAlliance();
                int lordAlliance = CFG.core.getCiv(lordCiv).getAlliance();
                if (vassalAlliance > 0 && vassalAlliance != lordAlliance) {
                    // Анулюємо несанкціонований альянс
                    CFG.core.getCiv(vCiv).setAlliance(lordAlliance);
                }

                // 2. Розрахунок та сплата данини сюзерену
                float aut = getAutonomy(vCiv);
                float tributeRate = 0.05f; // Базова данина 5%
                if (aut < 25.0f) {
                    tributeRate = 0.30f; // Інтегрована: 30%
                } else if (aut < 50.0f) {
                    tributeRate = 0.22f; // Залежна колонія: 22%
                } else if (aut < 75.0f) {
                    tributeRate = 0.14f; // Васал: 14%
                }

                long vassalGold = CFG.core.getCiv(vCiv).getGold();
                if (vassalGold > 50L) {
                    long tributeGold = Math.max(1L, (long)((float)vassalGold * tributeRate * 0.15f));
                    tributeGold = Math.min(tributeGold, vassalGold - 20L);

                    if (tributeGold > 0) {
                        CFG.core.getCiv(vCiv).setGold(vassalGold - tributeGold);
                        CFG.core.getCiv(lordCiv).setGold(CFG.core.getCiv(lordCiv).getGold() + tributeGold);

                        if (lordCiv == playerCiv) {
                            totalTributeForPlayer += tributeGold;
                        }
                    }
                }

                // 3. Динаміка шкали автономії:
                // - Добрі стосунки (>50) та сильніший сюзерен знижують автономію на користь сюзерена
                // - Погані стосунки (<0) та високе бажання свободи збільшують автономію
                float relation = CFG.core.getCiv(vCiv).getRelationD(lordCiv);
                float liberty = CFG.core.getCiv(vCiv).getVassalLibertyDesire();

                if (relation >= 50.0f && liberty < 30.0f) {
                    // Поступова інтеграція до сюзерена (-0.75% автономії за хід)
                    setAutonomy(vCiv, aut - 0.75f);
                } else if (liberty > 50.0f || relation < 0.0f) {
                    // Зростання прагнення незалежності (+1.2% автономії за хід)
                    setAutonomy(vCiv, aut + 1.20f);
                }
            }

            if (totalTributeForPlayer > 0 && playerCiv > 0) {
                boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                        ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                        : true;
                String msg = isUkr
                        ? ("💰 Отримано данину від васалів: +" + CFG.getNumberWthSpaces("" + totalTributeForPlayer) + " золота.")
                        : ("💰 Collected vassal tribute: +" + CFG.getNumberWthSpaces("" + totalTributeForPlayer) + " gold.");
                CFG.toastM.addM(msg, CFG.COLOR_POSITIVE);
                CFG.toastM.setTimeInView(3500);
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
