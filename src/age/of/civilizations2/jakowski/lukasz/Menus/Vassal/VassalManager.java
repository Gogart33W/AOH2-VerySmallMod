package age.of.civilizations2.jakowski.lukasz.Menus.Vassal;

import age.of.civilizations2.jakowski.lukasz.Alliance;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.GameCalendar;
import age.of.civilizations2.jakowski.lukasz.GameValues.GameValues;
import age.of.civilizations2.jakowski.lukasz.HistoryLog.HistoryLog_Annexation;
import age.of.civilizations2.jakowski.lukasz.SFXManager;
import com.badlogic.gdx.Gdx;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * VassalManager - менеджер механік васалітету:
 * 1. Синхронізація релігії, державного устрою (ідеології) та альянсів васалів із сюзереном.
 * 2. Точний облік дати та ходу набуття статусу васала.
 * 3. Перевірка вимог для мирної анексії васалів.
 * 4. Налаштування порогів пропозиції васалізації (стосунки 50.0, коефіцієнт бюджету 5.0).
 */
public class VassalManager {
    // Потокобезпечне збереження року та ходу початку васалітету для кожної держави
    private static final Map<Integer, Integer> vassalStartYear = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> vassalStartTurn = new ConcurrentHashMap<Integer, Integer>();
    private static final Map<Integer, Integer> knownLords = new ConcurrentHashMap<Integer, Integer>();

    static {
        applyDiplomacyValues();
    }

    /**
     * Встановлює необхідні пороги для пропозиції васалізації:
     * стосунки >= 50.0, коефіцієнт бюджету = 5.0
     */
    public static void applyDiplomacyValues() {
        try {
            if (GameValues.gvAiDiplomacy != null) {
                GameValues.gvAiDiplomacy.OFFER_VASSALIZATION_BUDGET_RATIO_TO_ACCEPT = 5.0f;
                GameValues.gvAiDiplomacy.OFFER_VASSALIZATION_MIN_RELATION = 50.0f;
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    /**
     * Синхронізує релігію, державний устрій та альянси усіх васалів із їхніми сюзеренами по всьому світу.
     * Також оновлює точний облік початку васалітету.
     */
    public static void syncAllVassals() {
        try {
            applyDiplomacyValues();
            for (int i = 1; i < CFG.core.getCivsSize(); i++) {
                if (CFG.core.getCiv(i).getNumOfProvs() <= 0) {
                    knownLords.remove(i);
                    vassalStartYear.remove(i);
                    vassalStartTurn.remove(i);
                    continue;
                }
                int lordID = CFG.core.getCiv(i).getPuppetOfCiv();
                if (lordID > 0 && lordID != i && lordID < CFG.core.getCivsSize() && CFG.core.getCiv(lordID).getNumOfProvs() > 0) {
                    Integer lastLord = knownLords.get(i);
                    if (lastLord == null || lastLord.intValue() != lordID) {
                        // Зафіксовано новий васалітет
                        knownLords.put(i, lordID);
                        if (GameCalendar.TURNID <= 2) {
                            // Якщо це початок гри — васал від старту сценарію
                            vassalStartTurn.put(i, 1);
                            vassalStartYear.put(i, GameCalendar.currYear);
                        } else {
                            // Якщо набуто статус під час гри — фіксуємо точний поточний хід і рік
                            vassalStartTurn.put(i, GameCalendar.TURNID);
                            vassalStartYear.put(i, GameCalendar.currYear);
                        }
                    }
                    syncVassalWithLord(lordID, i);
                } else {
                    knownLords.remove(i);
                    vassalStartYear.remove(i);
                    vassalStartTurn.remove(i);
                }
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    /**
     * Повна синхронізація конкретного васала із сюзереном:
     * - Релігія
     * - Державний устрій (ідеологія, тег)
     * - Альянс
     */
    public static void syncVassalWithLord(int lordCivID, final int vassalCivID) {
        try {
            if (lordCivID <= 0 || vassalCivID <= 0 || lordCivID == vassalCivID) return;
            if (lordCivID >= CFG.core.getCivsSize() || vassalCivID >= CFG.core.getCivsSize()) return;
            if (CFG.core.getCiv(lordCivID).getNumOfProvs() <= 0 || CFG.core.getCiv(vassalCivID).getNumOfProvs() <= 0) return;

            // 1. Синхронізація релігії
            int lordReligion = CFG.core.getCiv(lordCivID).getReligionID();
            if (CFG.core.getCiv(vassalCivID).getReligionID() != lordReligion) {
                CFG.core.getCiv(vassalCivID).setReligionID(lordReligion);
            }

            // 2. Синхронізація державного устрою (ідеології)
            int lordIdeology = CFG.core.getCiv(lordCivID).getIdeology();
            String desiredExtraTag = CFG.ideologiesMgr.getIdeologyID(lordIdeology).getExtraTag();
            String currentTag = CFG.core.getCiv(vassalCivID).getCivTag();
            boolean tagMatches = desiredExtraTag.isEmpty() 
                    ? !currentTag.contains("_") 
                    : currentTag.endsWith(desiredExtraTag);

            if (CFG.core.getCiv(vassalCivID).getIdeology() != lordIdeology || !tagMatches) {
                String realTag = CFG.ideologiesMgr.getRealTag(currentTag);
                String targetTag = realTag + desiredExtraTag;

                // Встановлюємо ідеологію безпосередньо в об'єкт цивілізації
                CFG.core.getCiv(vassalCivID).setIdeology(lordIdeology);

                // Оновлюємо тег цивілізації
                if (!currentTag.equals(targetTag)) {
                    CFG.core.getCiv(vassalCivID).setCivTag(targetTag);
                    // Перезавантажуємо прапор виключно на головному OpenGL потоці LibGDX
                    if (Gdx.app != null) {
                        try {
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        if (vassalCivID > 0 && vassalCivID < CFG.core.getCivsSize()) {
                                            CFG.core.getCiv(vassalCivID).loadFlag();
                                        }
                                    } catch (Exception ignored) {
                                    }
                                }
                            });
                        } catch (Exception ignored) {
                        }
                    }
                }

                // Гарантуємо, що id ідеології встановлено вірно
                CFG.core.getCiv(vassalCivID).setIdeology(lordIdeology);
            }

            // 3. Синхронізація альянсу
            syncVassalAllianceWithLord(lordCivID, vassalCivID);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    /**
     * Синхронізує альянс васала із альянсом сюзерена:
     * Якщо сюзерен в альянсі — васал обов'язково входить у той же альянс.
     * Якщо сюзерен не в альянсі — васал не може перебувати в альянсі самостійно.
     */
    public static void syncVassalAllianceWithLord(int lordCivID, int vassalCivID) {
        try {
            if (lordCivID <= 0 || vassalCivID <= 0 || lordCivID == vassalCivID) return;
            if (lordCivID >= CFG.core.getCivsSize() || vassalCivID >= CFG.core.getCivsSize()) return;

            int lordAlliance = CFG.core.getCiv(lordCivID).getAlliance();
            int vassalAlliance = CFG.core.getCiv(vassalCivID).getAlliance();

            if (lordAlliance > 0 && lordAlliance < CFG.core.getAlliancesSize()) {
                if (vassalAlliance != lordAlliance) {
                    if (vassalAlliance > 0 && vassalAlliance < CFG.core.getAlliancesSize()) {
                        Alliance oldAll = CFG.core.getAlliance(vassalAlliance);
                        if (oldAll != null) {
                            oldAll.removeCivilization(vassalCivID);
                        }
                    }
                    Alliance targetAll = CFG.core.getAlliance(lordAlliance);
                    if (targetAll != null) {
                        targetAll.addCivilization(vassalCivID);
                        CFG.core.getCiv(vassalCivID).setAlliance(lordAlliance);
                    }
                }
            } else if (lordAlliance == 0 && vassalAlliance > 0) {
                if (vassalAlliance < CFG.core.getAlliancesSize()) {
                    Alliance oldAll = CFG.core.getAlliance(vassalAlliance);
                    if (oldAll != null) {
                        oldAll.removeCivilization(vassalCivID);
                    }
                }
                CFG.core.getCiv(vassalCivID).setAlliance(0);
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    /**
     * Синхронізує всіх васалів вказаного сюзерена.
     */
    public static void syncVassalsWithLord(int lordCivID) {
        try {
            if (lordCivID <= 0 || lordCivID >= CFG.core.getCivsSize()) return;
            for (int i = 1; i < CFG.core.getCivsSize(); i++) {
                if (i == lordCivID) continue;
                if (CFG.core.getCiv(i).getNumOfProvs() <= 0) continue;
                if (CFG.core.getCiv(i).getPuppetOfCiv() == lordCivID) {
                    syncVassalWithLord(lordCivID, i);
                }
            }
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    /**
     * Реєструє початок васалітету (наприклад, при прийнятті пропозиції васалізації).
     */
    public static void registerVassalStart(int vassalCivID, int lordCivID) {
        knownLords.put(vassalCivID, lordCivID);
        vassalStartTurn.put(vassalCivID, GameCalendar.TURNID);
        vassalStartYear.put(vassalCivID, GameCalendar.currYear);
    }

    /**
     * Викликається при звільненні васала.
     */
    public static void onVassalLiberated(int vassalCivID) {
        knownLords.remove(vassalCivID);
        vassalStartTurn.remove(vassalCivID);
        vassalStartYear.remove(vassalCivID);
        try {
            int vassalAlliance = CFG.core.getCiv(vassalCivID).getAlliance();
            if (vassalAlliance > 0 && vassalAlliance < CFG.core.getAlliancesSize()) {
                CFG.core.getAlliance(vassalAlliance).removeCivilization(vassalCivID);
            }
            CFG.core.getCiv(vassalCivID).setAlliance(0);
        } catch (Exception ignored) {
        }
    }

    /**
     * Отримує кількість повних років васалітету від моменту набуття статусу васала.
     */
    public static int getVassalYears(int vassalCivID) {
        try {
            if (!vassalStartYear.containsKey(vassalCivID)) {
                int currentLord = CFG.core.getCiv(vassalCivID).getPuppetOfCiv();
                registerVassalStart(vassalCivID, currentLord);
                return 0;
            }
            int startYear = vassalStartYear.get(vassalCivID);
            return Math.max(0, GameCalendar.currYear - startYear);
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Отримує кількість ходів від моменту набуття статусу васала.
     */
    public static int getVassalTurns(int vassalCivID) {
        try {
            if (!vassalStartTurn.containsKey(vassalCivID)) {
                int currentLord = CFG.core.getCiv(vassalCivID).getPuppetOfCiv();
                registerVassalStart(vassalCivID, currentLord);
                return 0;
            }
            int startTurn = vassalStartTurn.get(vassalCivID);
            return Math.max(0, GameCalendar.TURNID - startTurn);
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * Клас зі статусом виконання вимог для анексії.
     */
    public static class AnnexationStatus {
        public boolean canAnnex = false;

        public float relation = 0.0f;
        public float reqRelation = 50.0f;
        public boolean relationOk = false;

        public int yearsAsVassal = 0;
        public int reqYears = 5;
        public int turnsAsVassal = 0;
        public int reqTurns = 50;
        public boolean yearsOk = false;

        public float libertyDesire = 0.0f;
        public float maxLibertyDesire = 50.0f;
        public boolean libertyDesireOk = false;

        public boolean peaceOk = false;

        public String getSummaryText(boolean isUkr) {
            StringBuilder sb = new StringBuilder();
            if (isUkr) {
                sb.append("Вимоги для мирного приєднання:\n");
                sb.append(relationOk ? "✓ " : "✕ ")
                  .append("Стосунки: ").append(CFG.getPrecision2(relation, 10))
                  .append(" / +").append((int)reqRelation).append("\n");
                sb.append(yearsOk ? "✓ " : "✕ ")
                  .append("Термін васалітету: ").append(yearsAsVassal)
                  .append(" / ").append(reqYears).append(" р. (").append(turnsAsVassal).append(" / ").append(reqTurns).append(" х.)\n");
                sb.append(libertyDesireOk ? "✓ " : "✕ ")
                  .append("Бажання свободи: ").append(CFG.getPrecision2(libertyDesire, 10))
                  .append("% / <").append((int)maxLibertyDesire).append("%\n");
                sb.append(peaceOk ? "✓ " : "✕ ")
                  .append("Стан миру: ").append(peaceOk ? "Мир" : "Війна");
            } else {
                sb.append("Peaceful annexation requirements:\n");
                sb.append(relationOk ? "✓ " : "✕ ")
                  .append("Relations: ").append(CFG.getPrecision2(relation, 10))
                  .append(" / +").append((int)reqRelation).append("\n");
                sb.append(yearsOk ? "✓ " : "✕ ")
                  .append("Vassal duration: ").append(yearsAsVassal)
                  .append(" / ").append(reqYears).append(" yrs (").append(turnsAsVassal).append(" / ").append(reqTurns).append(" turns)\n");
                sb.append(libertyDesireOk ? "✓ " : "✕ ")
                  .append("Liberty desire: ").append(CFG.getPrecision2(libertyDesire, 10))
                  .append("% / <").append((int)maxLibertyDesire).append("%\n");
                sb.append(peaceOk ? "✓ " : "✕ ")
                  .append("Peace status: ").append(peaceOk ? "Peace" : "At War");
            }
            return sb.toString();
        }
    }

    /**
     * Повна перевірка можливості мирного приєднання васала.
     */
    public static AnnexationStatus checkAnnexation(int lordCivID, int vassalCivID) {
        AnnexationStatus status = new AnnexationStatus();
        try {
            if (lordCivID <= 0 || vassalCivID <= 0 || lordCivID == vassalCivID) {
                return status;
            }
            if (CFG.core.getCiv(vassalCivID).getPuppetOfCiv() != lordCivID) {
                return status;
            }
            if (CFG.core.getCiv(vassalCivID).getNumOfProvs() <= 0) {
                return status;
            }

            // 1. Перевірка стосунків (>= 50)
            status.relation = CFG.core.getCiv(vassalCivID).getRelationD(lordCivID);
            status.relationOk = (status.relation >= status.reqRelation);

            // 2. Перевірка тривалості васалітету (>= 5 років або >= 50 ходів)
            status.yearsAsVassal = getVassalYears(vassalCivID);
            status.turnsAsVassal = getVassalTurns(vassalCivID);
            status.yearsOk = (status.yearsAsVassal >= status.reqYears || status.turnsAsVassal >= status.reqTurns);

            // 3. Бажання свободи (< 50%)
            status.libertyDesire = CFG.core.getCiv(vassalCivID).getVassalLibertyDesire();
            status.libertyDesireOk = (status.libertyDesire < status.maxLibertyDesire);

            // 4. Стан миру
            boolean lordAtWar = CFG.core.getCiv(lordCivID).isAtWarC();
            boolean vassalAtWar = CFG.core.getCiv(vassalCivID).isAtWarC();
            boolean warBetweenThem = CFG.core.getCivsAtWar(lordCivID, vassalCivID);
            status.peaceOk = (!lordAtWar && !vassalAtWar && !warBetweenThem);

            status.canAnnex = status.relationOk && status.yearsOk && status.libertyDesireOk && status.peaceOk;
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
        return status;
    }

    /**
     * Виконує мирне приєднання васала.
     */
    public static boolean performAnnexation(int lordCivID, int vassalCivID) {
        try {
            AnnexationStatus status = checkAnnexation(lordCivID, vassalCivID);
            if (!status.canAnnex) {
                boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
                String reason = isUkr ? "Не всі вимоги виконано для мирного приєднання!" : "Annexation requirements not met!";
                CFG.toastM.addM(reason, CFG.COLOR_NEGATIVE_2);
                return false;
            }

            String vassalName = CFG.core.getCiv(vassalCivID).getCivName();
            int totalProvs = CFG.core.getCiv(vassalCivID).getNumOfProvs();

            ArrayList<Integer> tempProvinces = new ArrayList<Integer>();
            for (int i = 0; i < CFG.core.getCiv(vassalCivID).getNumOfProvs(); ++i) {
                tempProvinces.add(CFG.core.getCiv(vassalCivID).getProvID(i));
            }

            for (int i = 0; i < tempProvinces.size(); ++i) {
                int provID = tempProvinces.get(i);
                if (CFG.core.getProv(provID).getCivId() != vassalCivID || CFG.core.getProv(provID).getTrueOwnerOfProv() != vassalCivID) {
                    continue;
                }
                int nArmyNewOwnerArmy = CFG.core.getProv(provID).getArmyCivID1(lordCivID);
                int vassalArmy = CFG.core.getProv(provID).getArmyCivID1(vassalCivID);

                CFG.core.getProv(provID).updateArmy4(0);
                CFG.core.getProv(provID).updateArmy4(lordCivID, 0);

                CFG.core.getProv(provID).setTrueOwnerOfProv(lordCivID);
                CFG.core.getProv(provID).setCivId(lordCivID, false);

                CFG.core.getProv(provID).setHappi(Math.min(1.0f, CFG.core.getProv(provID).getHappi() * 1.05f));
                CFG.core.getProv(provID).setRevRisk(0.0f);

                CFG.core.getProv(provID).updateArmy4(lordCivID, nArmyNewOwnerArmy + vassalArmy);

                for (int j = CFG.core.getProv(provID).getCivsSize() - 1; j >= 0; --j) {
                    int otherCiv = CFG.core.getProv(provID).getCivId(j);
                    if (otherCiv == lordCivID) continue;
                    if (CFG.core.getCiv(otherCiv).getPuppetOfCiv() == lordCivID
                            || CFG.core.getCiv(lordCivID).getPuppetOfCiv() == otherCiv
                            || (CFG.core.getCiv(otherCiv).getAlliance() > 0 && CFG.core.getCiv(otherCiv).getAlliance() == CFG.core.getCiv(lordCivID).getAlliance())
                            || CFG.core.getMilitaryAccess(otherCiv, lordCivID) > 0) {
                        continue;
                    }
                    CFG.gameAction.accessLost_MoveArmyToClosetsProvince(otherCiv, provID);
                }
            }

            if (CFG.core.getCiv(vassalCivID).getCapitalProvID() >= 0) {
                CFG.core.getProv(CFG.core.getCiv(vassalCivID).getCapitalProvID()).setIsCapital(false);
                for (int i = 0; i < CFG.core.getProv(CFG.core.getCiv(vassalCivID).getCapitalProvID()).getCitiesSize(); ++i) {
                    if (CFG.core.getProv(CFG.core.getCiv(vassalCivID).getCapitalProvID()).getCit(i).getCityLevel() != CFG.getEditorCityLevel(0)) {
                        continue;
                    }
                    CFG.core.getProv(CFG.core.getCiv(vassalCivID).getCapitalProvID()).getCit(i).setCityLevel(CFG.getEditorCityLevel(1));
                }
            }

            CFG.core.getCiv(vassalCivID).updateNumberOfUnits();
            CFG.core.getCiv(lordCivID).updateNumberOfUnits();
            tempProvinces.clear();

            CFG.core.buildCivilizationsRegions_TextOver(vassalCivID);
            CFG.core.buildCivilizationsRegions_TextOver(lordCivID);

            CFG.core.getCiv(vassalCivID).setPuppetOfCivId(vassalCivID);
            knownLords.remove(vassalCivID);
            vassalStartYear.remove(vassalCivID);
            vassalStartTurn.remove(vassalCivID);

            CFG.historyManager.addHistoryLog(new HistoryLog_Annexation(vassalCivID, lordCivID));

            int tPlayerID = CFG.core.getPlayerIDbyCivID(lordCivID);
            if (tPlayerID >= 0 && CFG.FOG_OF_WAR > 0) {
                for (int i = 0; i < CFG.core.getCiv(lordCivID).getNumOfProvs(); ++i) {
                    CFG.core.getProv(CFG.core.getCiv(lordCivID).getProvID(i)).updateFogOfWar(tPlayerID);
                }
            }

            CFG.map.getMpB().updateWorldMap_Shaders();
            CFG.menus.updateInGameTopAll(lordCivID);
            CFG.core.checkProvinceActionMenu();

            if (CFG.menus.getVisibleInGame_Tribute()) {
                CFG.menus.rebuildInGame_Tribute();
            }
            CFG.menus.setVisible_InGame_CivInfo(false);

            CFG.SFXManager.playSound(SFXManager.SFX_CLICK3);

            boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
            String msg = (isUkr ? "Васала мирно приєднано: " : "Vassal peacefully annexed: ") + vassalName + " (" + totalProvs + (isUkr ? " пров.)" : " prov.)");
            CFG.toastM.addM(msg, CFG.COLOR_POSITIVE);
            CFG.toastM.setTimeInView(4500);
            return true;
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
            return false;
        }
    }
}
