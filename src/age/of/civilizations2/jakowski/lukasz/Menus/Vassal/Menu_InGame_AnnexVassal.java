package age.of.civilizations2.jakowski.lukasz.Menus.Vassal;

import age.of.civilizations2.jakowski.lukasz.Button.Button_Transparent;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic;
import age.of.civilizations2.jakowski.lukasz.Button.GameN.ButtonN_Civs;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Core.Core;
import age.of.civilizations2.jakowski.lukasz.HistoryLog.HistoryLog_Annexation;
import age.of.civilizations2.jakowski.lukasz.IMGManager;
import age.of.civilizations2.jakowski.lukasz.Images;
import age.of.civilizations2.jakowski.lukasz.Menu;
import age.of.civilizations2.jakowski.lukasz.SFXManager;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextStyle;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextTitleStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu_InGame_AnnexVassal - діалогове вікно мирного приєднання васала.
 * Дозволяє безпечно і мирно інтегрувати васала до складу імперії гравця
 * без використання ультиматумів чи війн.
 */
public class Menu_InGame_AnnexVassal extends Menu {
    private static int annexMenuID = -1;
    private static int annexBgMenuID = -1;
    private static Menu_InGame_AnnexVassal instance = null;
    private static Menu_InGame_AnnexVassal_BG bgInstance = null;

    private int vassalCivID = -1;
    private int idConfirmBtn = -1;
    private int idCancelBtn = -1;

    public Menu_InGame_AnnexVassal(int vassalCivID) {
        this.rebuild(vassalCivID);
    }

    /**
     * Фонове меню для затемнення та блокування кліків поза вікном.
     */
    public static class Menu_InGame_AnnexVassal_BG extends Menu {
        public Menu_InGame_AnnexVassal_BG() {
            List<MenuElemUI> elems = new ArrayList<MenuElemUI>();
            elems.add(new Button_Transparent(0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, true) {
                @Override
                public void actionElem(int iID) {
                    // Кліки за межами меню поглинаються
                }
            });
            this.initMenu(null, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, elems);
        }

        @Override
        public void actionEL(int iID) {
        }

        @Override
        public void onBackPressed() {
            Menu_InGame_AnnexVassal.close();
        }

        @Override
        public void draw(SpriteBatch oSB, int iTranslateX, int iTranslateY, boolean sliderMenuIsActive) {
            oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.65f));
            IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);
            oSB.setColor(Color.WHITE);
            super.draw(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        }
    }

    public static void open(int vassalCivID) {
        try {
            if (bgInstance == null || annexBgMenuID < 0) {
                bgInstance = new Menu_InGame_AnnexVassal_BG();
                annexBgMenuID = CFG.menus.addNewMenuToViewID(CFG.menus.IN_GAME_MENU, bgInstance);
            }
            if (instance == null || annexMenuID < 0) {
                instance = new Menu_InGame_AnnexVassal(vassalCivID);
                annexMenuID = CFG.menus.addNewMenuToViewID(CFG.menus.IN_GAME_MENU, instance);
            } else {
                instance.rebuild(vassalCivID);
            }

            bgInstance.setVisibleM(true);
            instance.setVisibleM(true);

            CFG.menus.setOrderOfMenuID(annexBgMenuID);
            CFG.menus.setOrderOfMenuID(annexMenuID);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    public static void close() {
        if (bgInstance != null) {
            bgInstance.setVisibleM(false);
        }
        if (instance != null) {
            instance.setVisibleM(false);
        }
    }

    public void rebuild(int vassalCivID) {
        this.vassalCivID = vassalCivID;
        int lordCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
        List<MenuElemUI> menuElements = new ArrayList<MenuElemUI>();

        boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
        int menuWidth = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 1.85f), CFG.GAMEWIDTH - CFG.PADD * 4);
        int elemW = menuWidth - CFG.PADD * 4;
        int elemX = CFG.PADD * 2;
        int btnH = CFG.BUTTON_H;
        int textH = (int)(CFG.TEXT_HEIGHT_DEFAULT * 1.2f) + CFG.PADD;
        int curY = CFG.PADD;

        // 1. Заголовок
        String title = isUkr ? "МИРНЕ ПРИЄДНАННЯ ВАСАЛА" : "PEACEFUL VASSAL ANNEXATION";
        TextTitleStyle titleElem = new TextTitleStyle(title, -1, elemX, curY, elemW, btnH * 3 / 4);
        menuElements.add(titleElem);
        curY += titleElem.getHeightE() + CFG.PADD * 2;

        // 2. Прапори обох держав
        ButtonN_Civs civsBanner = new ButtonN_Civs(vassalCivID, lordCivID, elemX, curY, elemW);
        menuElements.add(civsBanner);
        curY += civsBanner.getHeightE() + CFG.PADD * 2;

        // 3. Інформаційні рядки
        String vassalName = CFG.core.getCiv(vassalCivID).getCivName();
        int numProvs = CFG.core.getCiv(vassalCivID).getNumOfProvs();
        long numPop = CFG.core.getCiv(vassalCivID).countPop();
        int numArmy = CFG.core.getCiv(vassalCivID).getNumberOfUnits();

        String line1 = isUkr
                ? ("Ви впевнені, що бажаєте мирно приєднати державу " + vassalName + "?")
                : ("Are you sure you want to peacefully annex " + vassalName + "?");
        TextStyle txt1 = new TextStyle(line1, -1, elemX, curY, elemW, textH);
        menuElements.add(txt1);
        curY += textH + CFG.PADD;

        String line2 = isUkr
                ? ("Провінцій: " + numProvs + "  •  Населення: " + CFG.getNumberWthSpaces("" + numPop) + "  •  Армія: " + CFG.getNumberWthSpaces("" + numArmy))
                : ("Provinces: " + numProvs + "  •  Population: " + CFG.getNumberWthSpaces("" + numPop) + "  •  Army: " + CFG.getNumberWthSpaces("" + numArmy));
        TextStyle txt2 = new TextStyle(line2, -1, elemX, curY, elemW, textH);
        menuElements.add(txt2);
        curY += textH + CFG.PADD;

        String line3 = isUkr
                ? "Усі території та війська без бою перейдуть під ваш контроль."
                : "All territories and troops will merge into your realm without conflict.";
        TextStyle txt3 = new TextStyle(line3, -1, elemX, curY, elemW, textH);
        menuElements.add(txt3);
        curY += textH + CFG.PADD * 2;

        // 4. Кнопки дій: "✓ Приєднати" та "✕ Скасувати"
        int halfBtnW = (elemW - CFG.PADD * 2) / 2;
        String confirmText = isUkr ? "✓ Приєднати васала" : "✓ Annex Vassal";
        Button_Classic confirmBtn = new Button_Classic(confirmText, -1, elemX, curY, halfBtnW, btnH, true);
        menuElements.add(confirmBtn);
        this.idConfirmBtn = menuElements.size() - 1;

        String cancelText = isUkr ? "✕ Скасувати" : "✕ Cancel";
        Button_Classic cancelBtn = new Button_Classic(cancelText, -1, elemX + halfBtnW + CFG.PADD * 2, curY, halfBtnW, btnH, true);
        menuElements.add(cancelBtn);
        this.idCancelBtn = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        int menuHeight = curY;
        int menuX = CFG.GAMEWIDTH / 2 - menuWidth / 2;
        int menuY = CFG.GAMEHEIGHT / 2 - menuHeight / 2;

        this.initMenu(null, menuX, menuY, menuWidth, menuHeight, menuElements, true, false, true);
    }

    @Override
    public void actionEL(int iID) {
        if (iID == this.idConfirmBtn) {
            int playerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
            annexVassal(playerCiv, this.vassalCivID);
            close();
            return;
        }
        if (iID == this.idCancelBtn) {
            close();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        close();
    }

    @Override
    public void actionCloseMenu() {
        close();
    }

    @Override
    public void draw(SpriteBatch oSB, int iTranslateX, int iTranslateY, boolean sliderMenuIsActive) {
        Core.drawMenuBG(oSB, this.getPosX() + iTranslateX, this.getPosY() + iTranslateY, this.getWidthM(), this.getHeightM());
        super.draw(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }

    /**
     * Повна логіка мирного приєднання васала до держави сюзерена.
     */
    public static void annexVassal(int lordCivID, int vassalCivID) {
        try {
            if (lordCivID <= 0 || vassalCivID <= 0 || lordCivID == vassalCivID) {
                return;
            }
            if (CFG.core.getCiv(vassalCivID).getPuppetOfCiv() != lordCivID) {
                return;
            }
            if (CFG.core.getCiv(vassalCivID).getNumOfProvs() <= 0) {
                return;
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

                // Очищаємо старі армії для коректного оновлення
                CFG.core.getProv(provID).updateArmy4(0);
                CFG.core.getProv(provID).updateArmy4(lordCivID, 0);

                // Передаємо володіння провінцією
                CFG.core.getProv(provID).setTrueOwnerOfProv(lordCivID);
                CFG.core.getProv(provID).setCivId(lordCivID, false);

                // Мирна інтеграція: стабільність та лояльність зберігаються
                CFG.core.getProv(provID).setHappi(Math.min(1.0f, CFG.core.getProv(provID).getHappi() * 1.05f));
                CFG.core.getProv(provID).setRevRisk(0.0f);

                // Переводимо війська васала під контроль гравця
                CFG.core.getProv(provID).updateArmy4(lordCivID, nArmyNewOwnerArmy + vassalArmy);

                // Виводимо війська третіх сторін без права проходу
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

            // Зняття статусу столиці з колишньої столиці васала
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

            // Звільняємо статус васала, бо країна приєднана
            CFG.core.getCiv(vassalCivID).setPuppetOfCivId(vassalCivID);

            // Запис в історію партії
            CFG.historyManager.addHistoryLog(new HistoryLog_Annexation(vassalCivID, lordCivID));

            // Оновлення туману війни для гравця
            int tPlayerID = CFG.core.getPlayerIDbyCivID(lordCivID);
            if (tPlayerID >= 0 && CFG.FOG_OF_WAR > 0) {
                for (int i = 0; i < CFG.core.getCiv(lordCivID).getNumOfProvs(); ++i) {
                    CFG.core.getProv(CFG.core.getCiv(lordCivID).getProvID(i)).updateFogOfWar(tPlayerID);
                }
            }

            // Оновлення карти та інтерфейсу
            CFG.map.getMpB().updateWorldMap_Shaders();
            CFG.menus.updateInGameTopAll(lordCivID);
            CFG.core.checkProvinceActionMenu();

            // Оновлюємо меню данини та закриваємо меню дипломатії васала
            if (CFG.menus.getVisibleInGame_Tribute()) {
                CFG.menus.rebuildInGame_Tribute();
            }
            CFG.menus.setVisible_InGame_CivInfo(false);

            // Звуковий ефект
            CFG.SFXManager.playSound(SFXManager.SFX_CLICK3);

            // Повідомлення гравцю
            boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
            String msg = (isUkr ? "Васала мирно приєднано: " : "Vassal peacefully annexed: ") + vassalName + " (" + totalProvs + (isUkr ? " пров.)" : " prov.)");
            CFG.toastM.addM(msg, CFG.COLOR_POSITIVE);
            CFG.toastM.setTimeInView(4500);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
