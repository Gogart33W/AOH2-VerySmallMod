package age.of.civilizations2.jakowski.lukasz.Menus.Vassal;

import age.of.civilizations2.jakowski.lukasz.Button.Button_Transparent;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic;
import age.of.civilizations2.jakowski.lukasz.Button.GameN.ButtonN_Civs;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Core.Core;
import age.of.civilizations2.jakowski.lukasz.IMGManager;
import age.of.civilizations2.jakowski.lukasz.Images;
import age.of.civilizations2.jakowski.lukasz.Menu;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextStyle;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextTitleStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu_InGame_AnnexVassal - діалогове вікно мирного приєднання васала
 * з перевіркою всіх необхідних вимог (стосунки, термін васалітету, бажання свободи, мир).
 */
public class Menu_InGame_AnnexVassal extends Menu {
    private static int annexMenuID = -1;
    private static int annexBgMenuID = -1;
    private static Menu_InGame_AnnexVassal instance = null;
    private static Menu_InGame_AnnexVassal_BG bgInstance = null;

    private int vassalCivID = -1;
    private int idConfirmBtn = -1;
    private int idCancelBtn = -1;
    private VassalManager.AnnexationStatus currentStatus = null;

    public Menu_InGame_AnnexVassal(int vassalCivID) {
        this.rebuild(vassalCivID);
    }

    public static class Menu_InGame_AnnexVassal_BG extends Menu {
        public Menu_InGame_AnnexVassal_BG() {
            List<MenuElemUI> elems = new ArrayList<MenuElemUI>();
            elems.add(new Button_Transparent(0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, true) {
                @Override
                public void actionElem(int iID) {
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
            oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.70f));
            IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);
            oSB.setColor(Color.WHITE);
            super.draw(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        }
    }

    public static void open(int vassalCivID) {
        try {
            // Перед відкриттям синхронізуємо васалів
            int playerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
            VassalManager.syncVassalsWithLord(playerCiv);

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
        int menuWidth = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 1.9f), CFG.GAMEWIDTH - CFG.PADD * 4);
        int elemW = menuWidth - CFG.PADD * 4;
        int elemX = CFG.PADD * 2;
        int btnH = CFG.BUTTON_H;
        int textH = (int)(CFG.TEXT_HEIGHT_DEFAULT * 1.25f);
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

        // 3. Інформація про васала
        String vassalName = CFG.core.getCiv(vassalCivID).getCivName();
        int numProvs = CFG.core.getCiv(vassalCivID).getNumOfProvs();
        long numPop = CFG.core.getCiv(vassalCivID).countPop();
        int numArmy = CFG.core.getCiv(vassalCivID).getNumberOfUnits();

        String line1 = isUkr
                ? ("Держава: " + vassalName + "  •  Провінцій: " + numProvs)
                : ("Civilization: " + vassalName + "  •  Provinces: " + numProvs);
        TextStyle txt1 = new TextStyle(line1, -1, elemX, curY, elemW, textH);
        menuElements.add(txt1);
        curY += textH + CFG.PADD;

        String line2 = isUkr
                ? ("Населення: " + CFG.getNumberWthSpaces("" + numPop) + "  •  Армія: " + CFG.getNumberWthSpaces("" + numArmy))
                : ("Population: " + CFG.getNumberWthSpaces("" + numPop) + "  •  Army: " + CFG.getNumberWthSpaces("" + numArmy));
        TextStyle txt2 = new TextStyle(line2, -1, elemX, curY, elemW, textH);
        menuElements.add(txt2);
        curY += textH + CFG.PADD * 2;

        // 4. Перевірка та відображення вимог анексії
        this.currentStatus = VassalManager.checkAnnexation(lordCivID, vassalCivID);

        String reqTitle = isUkr ? "─── ВИМОГИ ДЛЯ ПРИЄДНАННЯ ───" : "─── ANNEXATION REQUIREMENTS ───";
        TextStyle txtReq = new TextStyle(reqTitle, -1, elemX, curY, elemW, textH);
        menuElements.add(txtReq);
        curY += textH + CFG.PADD;

        // Стосунки
        String relStr = (this.currentStatus.relationOk ? "✓ " : "✕ ") +
                (isUkr ? "Стосунки з васалом: " : "Relations: ") +
                CFG.getPrecision2(this.currentStatus.relation, 10) + " / +" + (int)this.currentStatus.reqRelation;
        TextStyle txtRel = new TextStyle(relStr, -1, elemX, curY, elemW, textH);
        menuElements.add(txtRel);
        curY += textH + CFG.PADD;

        // Термін васалітету
        String yearsStr = (this.currentStatus.yearsOk ? "✓ " : "✕ ") +
                (isUkr ? "Термін васалітету: " : "Vassal duration: ") +
                this.currentStatus.yearsAsVassal + " / " + this.currentStatus.reqYears + (isUkr ? " р. (" : " yrs (") +
                this.currentStatus.turnsAsVassal + " / " + this.currentStatus.reqTurns + (isUkr ? " х.)" : " turns)");
        TextStyle txtYears = new TextStyle(yearsStr, -1, elemX, curY, elemW, textH);
        menuElements.add(txtYears);
        curY += textH + CFG.PADD;

        // Бажання свободи
        String libStr = (this.currentStatus.libertyDesireOk ? "✓ " : "✕ ") +
                (isUkr ? "Бажання свободи: " : "Liberty desire: ") +
                CFG.getPrecision2(this.currentStatus.libertyDesire, 10) + "% / <" + (int)this.currentStatus.maxLibertyDesire + "%";
        TextStyle txtLib = new TextStyle(libStr, -1, elemX, curY, elemW, textH);
        menuElements.add(txtLib);
        curY += textH + CFG.PADD;

        // Стан миру
        String peaceStr = (this.currentStatus.peaceOk ? "✓ " : "✕ ") +
                (isUkr ? "Стан миру: " : "Peace status: ") +
                (this.currentStatus.peaceOk ? (isUkr ? "Мирний" : "Peace") : (isUkr ? "У стані війни" : "At War"));
        TextStyle txtPeace = new TextStyle(peaceStr, -1, elemX, curY, elemW, textH);
        menuElements.add(txtPeace);
        curY += textH + CFG.PADD * 2;

        // 5. Кнопки підтвердження та скасування
        int halfBtnW = (elemW - CFG.PADD * 2) / 2;
        String confirmText = isUkr ? "✓ Приєднати васала" : "✓ Annex Vassal";
        Button_Classic confirmBtn = new Button_Classic(confirmText, -1, elemX, curY, halfBtnW, btnH, this.currentStatus.canAnnex) {
            @Override
            public boolean getIsClickable() {
                return currentStatus != null && currentStatus.canAnnex;
            }
        };
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
            if (this.currentStatus != null && this.currentStatus.canAnnex) {
                int playerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
                VassalManager.performAnnexation(playerCiv, this.vassalCivID);
                close();
            } else {
                boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
                CFG.toastM.addM(isUkr ? "Не всі вимоги виконано для мирного приєднання!" : "Requirements not met!", CFG.COLOR_NEGATIVE_2);
            }
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
}
