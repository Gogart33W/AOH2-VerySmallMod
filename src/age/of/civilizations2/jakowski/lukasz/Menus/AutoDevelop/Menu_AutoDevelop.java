package age.of.civilizations2.jakowski.lukasz.Menus.AutoDevelop;

import age.of.civilizations2.jakowski.lukasz.Button.Button_Transparent;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Civilization;
import age.of.civilizations2.jakowski.lukasz.IMGManager;
import age.of.civilizations2.jakowski.lukasz.Images;
import age.of.civilizations2.jakowski.lukasz.Menu;
import age.of.civilizations2.jakowski.lukasz.Renderer;
import age.of.civilizations2.jakowski.lukasz.SFXManager;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextTitleStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu_AutoDevelop - професійне, адаптивне та візуально вивірене вікно
 * автоматичного розвитку провінцій держави.
 */
public class Menu_AutoDevelop extends Menu {
    private static int autoDevelopMenuID = -1;
    private static Menu_AutoDevelop instance = null;

    // Збережені координати для точного малювання рамки діалогу
    private int dialogStartX = 0;
    private int dialogStartY = 0;
    private int dialogWidth = 0;
    private int dialogHeight = 0;
    private int col1XPos = 0;
    private int col2XPos = 0;
    private int colWidth = 0;
    private int columnsStartY = 0;
    private int columnsHeight = 0;

    // Головні кнопки
    private int idCloseTop = -1;
    private int idCloseBottom = -1;
    private int idToggleMaster = -1;
    private int idRunNow = -1;
    private int idCatchAll = -1;

    // Модулі автоматизації (ліва колонка)
    private int idToggleAssimilate = -1;
    private int idToggleFestival = -1;
    private int idToggleDevelopment = -1;
    private int idToggleEconomy = -1;
    private int idEcoM10 = -1;
    private int idEcoP10 = -1;
    private int idEco80 = -1;
    private int idEco100 = -1;
    private int idEco120 = -1;
    private int idEco150 = -1;
    private int idToggleNotifications = -1;

    // Пріоритети (права колонка)
    private int[] idPrioRow = new int[4];
    private int[] idPrioUp = new int[4];
    private int[] idPrioDown = new int[4];
    private int idPrioPresetBalanced = -1;
    private int idPrioPresetEconomy = -1;
    private int idPrioPresetStability = -1;

    // Захисні резерви ресурсів
    private int idResMove0 = -1;
    private int idResMove2 = -1;
    private int idResMove4 = -1;
    private int idResMove6 = -1;
    private int idResGold0 = -1;
    private int idResGold1k = -1;
    private int idResGold5k = -1;
    private int idResGold10k = -1;

    public Menu_AutoDevelop() {
        this.rebuild();
    }

    public static void open() {
        try {
            AutoDevelopManager.loadConfig();
            if (instance == null || autoDevelopMenuID < 0) {
                instance = new Menu_AutoDevelop();
                autoDevelopMenuID = CFG.menus.addNewMenuToViewID(CFG.menus.IN_GAME_MENU, instance);
            } else {
                instance.rebuild();
            }

            instance.setVisibleM(true);
            CFG.menus.setOrderOfMenuID(autoDevelopMenuID);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    public static void close() {
        if (instance != null) {
            instance.setVisibleM(false);
        }
    }

    /**
     * fitText - вимірює РЕАЛЬНУ ширину рядка шрифтом гри (CFG.glyphLay / CFG.fontMain)
     * і скорочує текст з додаванням "..." так, щоб він гарантовано вмістився в maxWidthPx.
     */
    public static String fitText(String text, int maxWidthPx) {
        return fitText(text, maxWidthPx, 0);
    }

    public static String fitText(String text, int maxWidthPx, int fontID) {
        if (text == null || text.isEmpty() || maxWidthPx <= 0) {
            return "";
        }
        try {
            if (CFG.glyphLay != null && CFG.fontMain != null && fontID < CFG.fontMain.size() && CFG.fontMain.get(fontID) != null) {
                CFG.glyphLay.setText(CFG.fontMain.get(fontID), text);
                if (CFG.glyphLay.width <= (float)maxWidthPx) {
                    return text;
                }

                String ellipsis = "...";
                CFG.glyphLay.setText(CFG.fontMain.get(fontID), ellipsis);
                float ellW = CFG.glyphLay.width;
                if (ellW >= (float)maxWidthPx) {
                    return "";
                }

                int low = 1;
                int high = text.length() - 1;
                String best = "";
                while (low <= high) {
                    int mid = (low + high) / 2;
                    String candidate = text.substring(0, mid) + ellipsis;
                    CFG.glyphLay.setText(CFG.fontMain.get(fontID), candidate);
                    if (CFG.glyphLay.width <= (float)maxWidthPx) {
                        best = candidate;
                        low = mid + 1;
                    } else {
                        high = mid - 1;
                    }
                }
                return best.isEmpty() ? ellipsis : best;
            }
        } catch (Exception ignored) {
        }

        // Запасний розрахунок для headless оточення або відсутності графічного контексту
        int approxCharW = 11;
        int maxChars = maxWidthPx / approxCharW;
        if (text.length() <= maxChars) {
            return text;
        }
        if (maxChars <= 3) {
            return text.substring(0, Math.max(1, maxChars));
        }
        return text.substring(0, maxChars - 3) + "...";
    }

    public static int measureTextWidth(String text) {
        return measureTextWidth(text, 0);
    }

    public static int measureTextWidth(String text, int fontID) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        try {
            if (CFG.glyphLay != null && CFG.fontMain != null && fontID < CFG.fontMain.size() && CFG.fontMain.get(fontID) != null) {
                CFG.glyphLay.setText(CFG.fontMain.get(fontID), text);
                return (int)CFG.glyphLay.width;
            }
        } catch (Exception ignored) {
        }
        return text.length() * 11;
    }

    /**
     * InfoCell - спеціальний елемент для відображення неклікабельних інформаційних комірок.
     * Не є вимкненою кнопкою (не малює блідого 0.45f тексту й блідої підкладки),
     * а малює контрастний, чіткий текст по центру елегантного бейджа з тінню.
     */
    public static class InfoCell extends MenuElemUI {
        private String sText = "";
        private int iTextWidth = 0;
        private int iTextHeight = CFG.TEXT_HEIGHT_DEFAULT;
        private Color textColor = new Color(0.90f, 0.92f, 0.96f, 1.0f);
        private Color bgColor = new Color(0.10f, 0.13f, 0.19f, 0.75f);
        private Color borderColor = new Color(CFG.COLOR_FLAG_FRAME.r, CFG.COLOR_FLAG_FRAME.g, CFG.COLOR_FLAG_FRAME.b, 0.35f);
        private boolean drawBorder = true;

        public InfoCell(String sText, int iPosX, int iPosY, int iWidth, int iHeight) {
            this(sText, iPosX, iPosY, iWidth, iHeight, true);
        }

        public InfoCell(String sText, int iPosX, int iPosY, int iWidth, int iHeight, boolean drawBorder) {
            this.typeOfMenuElemUI = MenuElemUI.TypeOfMenuElemUI.TEXT;
            this.setPosX(iPosX);
            this.setPosY(iPosY);
            this.setWidthE(iWidth);
            this.setHeightE(iHeight);
            this.setClickable(false);
            this.setVisibleE(true);
            this.drawBorder = drawBorder;
            this.setTextE(sText);
        }

        public InfoCell setColors(Color text, Color bg, Color border) {
            if (text != null) this.textColor = text;
            if (bg != null) this.bgColor = bg;
            if (border != null) this.borderColor = border;
            return this;
        }

        @Override
        public void setTextE(String sText) {
            this.sText = (sText != null) ? sText : "";
            try {
                if (this.sText.length() > 0 && CFG.glyphLay != null && CFG.fontMain != null && !CFG.fontMain.isEmpty()) {
                    CFG.glyphLay.setText(CFG.fontMain.get(this.fontID), this.sText);
                    this.iTextWidth = (int)CFG.glyphLay.width;
                    this.iTextHeight = (int)CFG.glyphLay.height;
                    if (this.iTextHeight == 0) this.iTextHeight = CFG.TEXT_HEIGHT_DEFAULT;
                } else {
                    this.iTextWidth = this.sText.length() * 10;
                    this.iTextHeight = CFG.TEXT_HEIGHT_DEFAULT;
                }
            } catch (Exception ex) {
                this.iTextWidth = this.sText.length() * 10;
                this.iTextHeight = CFG.TEXT_HEIGHT_DEFAULT;
            }
        }

        @Override
        public String getTextE() {
            return this.sText;
        }

        @Override
        public int getTextWidthU() {
            return this.iTextWidth;
        }

        @Override
        public int getTextHeight() {
            return this.iTextHeight;
        }

        @Override
        public void drawE(SpriteBatch oSB, int iTranslateX, int iTranslateY) {
            this.drawE(oSB, iTranslateX, iTranslateY, false, false);
        }

        @Override
        public void drawE(SpriteBatch oSB, int iTranslateX, int iTranslateY, boolean isActive, boolean scrollableY) {
            int x = this.getPosXE() + iTranslateX;
            int y = this.getPosY() + iTranslateY;
            int w = this.getWidthE();
            int h = this.getHeightE();

            try {
                if (this.bgColor != null && this.bgColor.a > 0.0f) {
                    oSB.setColor(this.bgColor);
                    IMGManager.getIMG(Images.pix255).draw(oSB, x, y, w, h);
                }

                if (this.drawBorder && this.borderColor != null && this.borderColor.a > 0.0f) {
                    oSB.setColor(this.borderColor);
                    IMGManager.getIMG(Images.pix255).draw(oSB, x, y, w, 1);
                    IMGManager.getIMG(Images.pix255).draw(oSB, x, y + h - 1, w, 1);
                    IMGManager.getIMG(Images.pix255).draw(oSB, x, y, 1, h);
                    IMGManager.getIMG(Images.pix255).draw(oSB, x + w - 1, y, 1, h);
                }

                oSB.setColor(Color.WHITE);

                int textX = x + (w - this.iTextWidth) / 2;
                int textY = y + (h - this.iTextHeight) / 2;
                Renderer.drawTextWithShadow(oSB, this.fontID, this.sText, textX, textY, this.textColor);
            } finally {
                oSB.setColor(Color.WHITE);
                try {
                    if (CFG.fontMain != null && this.fontID < CFG.fontMain.size() && CFG.fontMain.get(this.fontID) != null) {
                        CFG.fontMain.get(this.fontID).setColor(Color.WHITE);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    /*
     * ЗНАХІДКИ ДОСЛІДЖЕННЯ ДЕКОМПІЛЬОВАНОГО КОДУ (game.jar):
     * 1. Button_Classic розширює ButtonM. Якщо передати boolean isClickable=false,
     *    ButtonM малює підкладку з альфою 0.45f, а текст кольором COLOR_BUTTON_MENU_TEXT_NOT_CLICKABLE
     *    (блідо-сірий). Тому Button_Classic(..., false) не можна використовувати для інфо-рядків.
     * 2. Конструктор Button_Classic з параметром boolean checkboxState вмикає внутрішній чекбокс
     *    (ButtonM$3), який МАЛЮЄ ЗЕЛЕНИЙ ГРАДІЄНТ при true і ЧЕРВОНИЙ ГРАДІЄНТ при false.
     *    Конструктор БЕЗ checkboxState вимикає чекбокс (ButtonM$4), і градієнт ВЗАГАЛІ НЕ МАЛЮЄТЬСЯ
     *    (кнопка залишається нейтральною темною). Тому червоний/зелений градієнт використовується
     *    виключно для ON/OFF тумблерів та ОДНОГО вибраного елемента в групах.
     * 3. Ширина тексту вимірюється через CFG.glyphLay.setText(CFG.fontMain.get(fontID), text) -> width.
     *    Button_Classic не вміє автоскейлити або обрізати текст: якщо текст довший за ширину кнопки,
     *    він просто малюється з центром у x + w/2 - textWidth/2 і вилазить на сусідні кнопки з обох боків.
     *    Хелпер fitText(...) запобігає цьому шляхом бінарного скорочення тексту під реальну ширину.
     */
    public void rebuild() {
        List<MenuElemUI> menuElements = new ArrayList<MenuElemUI>();
        boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null) 
                ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська") 
                : true;

        int gw = CFG.GAMEWIDTH;
        int gh = CFG.GAMEHEIGHT;
        int pad = CFG.PADD;

        // 1. Адаптивні пропорційні розміри модального діалогу під реальну роздільну здатність
        int menuW = Math.min((int)(gw * 0.94f), Math.min(940, gw - pad * 4));
        int startX = (gw - menuW) / 2;

        int titleH = Math.max(38, Math.min(48, (int)(gh * 0.065f)));
        int statusH = Math.max(24, Math.min(28, (int)(gh * 0.038f)));
        int masterBtnH = Math.max(42, Math.min(50, (int)(gh * 0.068f)));
        int colHeaderH = Math.max(26, Math.min(30, (int)(gh * 0.040f)));
        int btnH = Math.max(38, Math.min(44, (int)(gh * 0.058f)));
        int smallBtnH = Math.max(30, Math.min(36, (int)(gh * 0.048f)));
        int actionBtnH = Math.max(42, Math.min(48, (int)(gh * 0.065f)));
        int closeBottomH = Math.max(36, Math.min(42, (int)(gh * 0.055f)));

        int gapS = Math.max(2, pad / 2);
        int gapM = Math.max(4, pad);
        int gapL = Math.max(6, (int)(pad * 1.5f));

        int colGap = Math.max(6, pad * 2);
        int colW = (menuW - colGap) / 2;
        int col1X = startX;
        int col2X = startX + colW + colGap;

        int padText = pad * 2;

        // Синхронізована висота 8 рядків колонок:
        // 1. Header (colHeaderH + gapM)
        // 2..5. 4 модулі / 4 пріоритети (4 * (btnH + gapM))
        // 6. Цільова екон. / Пресети (smallBtnH + gapM)
        // 7. Відсотки % / Резерв ходів (smallBtnH + gapM)
        // 8. Сповіщення / Резерв золота (smallBtnH)
        int syncedColumnsH = colHeaderH + gapM + (4 * (btnH + gapM)) + (smallBtnH + gapM) + (smallBtnH + gapM) + smallBtnH;

        int totalDialogH = (titleH + gapM)
                + (statusH + gapS)
                + (statusH + gapL)
                + (masterBtnH + gapL)
                + syncedColumnsH
                + gapL
                + (actionBtnH + gapM)
                + closeBottomH;

        // Ідеальне вертикальне центрування на екрані (враховуючи реальний GAMEHEIGHT)
        int startY = Math.max(pad * 2, (gh - totalDialogH) / 2);
        int curY = startY;

        this.dialogStartX = startX;
        this.dialogStartY = startY;
        this.dialogWidth = menuW;
        this.dialogHeight = totalDialogH;
        this.col1XPos = col1X;
        this.col2XPos = col2X;
        this.colWidth = colW;

        // 2. Верхній рядок: Заголовок та кнопка закриття
        int closeBtnW = Math.max(110, btnH * 2);
        String rawTitle = isUkr ? "АВТО-РОЗВИТОК ПРОВІНЦІЙ" : "PROVINCE AUTO-DEVELOPMENT";
        int titleAvailW = menuW - closeBtnW - pad * 2;
        TextTitleStyle titleElem = new TextTitleStyle(fitText(rawTitle, titleAvailW - padText), -1, startX, curY, titleAvailW, titleH);
        menuElements.add(titleElem);

        Button_Classic closeTopBtn = new Button_Classic(isUkr ? "Закрити" : "Close", -1, startX + menuW - closeBtnW, curY, closeBtnW, titleH, true);
        menuElements.add(closeTopBtn);
        this.idCloseTop = menuElements.size() - 1;
        curY += titleH + gapM;

        // 3. Дані держави та ресурси (адаптивні нерівні комірки: назві держави виділяється ~38% ширини, решта ділять залишок)
        int playerCivID = -1;
        try {
            if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                playerCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
            }
        } catch (Exception ignored) {
        }
        Civilization pCiv = (playerCivID > 0) ? CFG.core.getCiv(playerCivID) : null;

        float movPts = (pCiv != null) ? (float)pCiv.getMovemPoints() / 10.0f : 0.0f;
        float dipPts = (pCiv != null) ? (float)pCiv.getDiploPoints() / 10.0f : 0.0f;
        long gold = (pCiv != null) ? pCiv.getGold() : 0L;
        String civName = (pCiv != null) ? pCiv.getCivName() : (isUkr ? "Держава" : "Civilization");
        int numProvs = (pCiv != null) ? pCiv.getNumOfProvs() : 0;

        int statusGap = Math.max(3, pad);
        int cell1W = (int)(menuW * 0.38f);
        int remStatusW = menuW - cell1W - statusGap * 3;
        int restCellW = remStatusW / 3;

        // Поетапне адаптивне скорочення суфіксу провінцій перед урізанням самої назви держави
        String c1;
        String pFull = " (" + numProvs + (isUkr ? " провінцій)" : " provs)");
        String pShort1 = " (" + numProvs + (isUkr ? " пров.)" : " prov.)");
        String pShort2 = " (" + numProvs + (isUkr ? " пр.)" : " pr.)");
        String pNumOnly = " (" + numProvs + ")";

        int maxC1TextW = cell1W - padText;
        if (measureTextWidth(civName + pFull) <= maxC1TextW) {
            c1 = civName + pFull;
        } else if (measureTextWidth(civName + pShort1) <= maxC1TextW) {
            c1 = civName + pShort1;
        } else if (measureTextWidth(civName + pShort2) <= maxC1TextW) {
            c1 = civName + pShort2;
        } else if (measureTextWidth(civName + pNumOnly) <= maxC1TextW) {
            c1 = civName + pNumOnly;
        } else {
            c1 = fitText(civName + pNumOnly, maxC1TextW);
        }

        String c2 = (isUkr ? "Ходи: " : "Moves: ") + CFG.getPrecision2(movPts, 10);
        String c3 = (isUkr ? "Дипломатія: " : "Diplo: ") + CFG.getPrecision2(dipPts, 10);
        String c4 = (isUkr ? "Золото: " : "Gold: ") + CFG.getNumberWthSpaces("" + gold);

        int curStatusX = startX;
        menuElements.add(new InfoCell(c1, curStatusX, curY, cell1W, statusH));
        curStatusX += cell1W + statusGap;
        menuElements.add(new InfoCell(fitText(c2, restCellW - padText), curStatusX, curY, restCellW, statusH));
        curStatusX += restCellW + statusGap;
        menuElements.add(new InfoCell(fitText(c3, restCellW - padText), curStatusX, curY, restCellW, statusH));
        curStatusX += restCellW + statusGap;
        menuElements.add(new InfoCell(fitText(c4, restCellW - padText), curStatusX, curY, restCellW, statusH));
        curY += statusH + gapS;

        // 4. Рядок діагностики потреб провінцій (розбито на 4 чіткі комірки InfoCell)
        int needAssi = (playerCivID > 0) ? AutoDevelopManager.getNeedAssimilateCount(playerCivID) : 0;
        int needFest = (playerCivID > 0) ? AutoDevelopManager.getNeedFestivalCount(playerCivID) : 0;
        int needEco = (playerCivID > 0) ? AutoDevelopManager.getNeedEconomyCount(playerCivID) : 0;
        int needDev = (playerCivID > 0) ? AutoDevelopManager.getNeedDevelopmentCount(playerCivID) : 0;

        String d1 = (isUkr ? "Асим: " : "Assim: ") + needAssi;
        String d2 = (isUkr ? "Ярмарок: " : "Fest: ") + needFest;
        String d3 = (isUkr ? "Екон: " : "Eco: ") + needEco;
        String d4 = (isUkr ? "Науки: " : "Dev: ") + needDev;

        Color alertCol = new Color(0.92f, 0.78f, 0.35f, 1.0f);
        int diagCellW = (menuW - statusGap * 3) / 4;
        InfoCell ic1 = new InfoCell(fitText(d1, diagCellW - padText), startX, curY, diagCellW, statusH);
        InfoCell ic2 = new InfoCell(fitText(d2, diagCellW - padText), startX + (diagCellW + statusGap), curY, diagCellW, statusH);
        InfoCell ic3 = new InfoCell(fitText(d3, diagCellW - padText), startX + (diagCellW + statusGap) * 2, curY, diagCellW, statusH);
        InfoCell ic4 = new InfoCell(fitText(d4, diagCellW - padText), startX + (diagCellW + statusGap) * 3, curY, diagCellW, statusH);

        if (needAssi > 0) ic1.setColors(alertCol, null, null);
        if (needFest > 0) ic2.setColors(alertCol, null, null);
        if (needEco > 0) ic3.setColors(alertCol, null, null);
        if (needDev > 0) ic4.setColors(alertCol, null, null);

        menuElements.add(ic1);
        menuElements.add(ic2);
        menuElements.add(ic3);
        menuElements.add(ic4);
        curY += statusH + gapL;

        // 5. Головний майстер-перемикач (великий банер з checked градієнтом)
        String masterText = isUkr 
                ? (AutoDevelopManager.autoDevelopEnabled ? "[ВКЛ] АВТО-РОЗВИТОК УВІМКНЕНО (ПРАЦЮЄ ЩОХОДУ)" : "[ВИКЛ] АВТО-РОЗВИТОК ВИМКНЕНО")
                : (AutoDevelopManager.autoDevelopEnabled ? "[ON] AUTO-DEVELOPMENT RUNS EVERY TURN" : "[OFF] AUTO-DEVELOPMENT DISABLED");
        Button_Classic masterBtn = new Button_Classic(fitText(masterText, menuW - padText * 2), -1, startX, curY, menuW, masterBtnH, true, AutoDevelopManager.autoDevelopEnabled);
        menuElements.add(masterBtn);
        this.idToggleMaster = menuElements.size() - 1;
        curY += masterBtnH + gapL;

        this.columnsStartY = curY;

        // =========================================================================
        // СИНХРОНІЗОВАНІ РЯДКИ ЛІВОЇ ТА ПРАВОЇ КОЛОНОК (РЯДОК ЗА РЯДКОМ ПО ВИСОТІ)
        // =========================================================================

        int rowY = this.columnsStartY;

        // --- РЯДОК 1: ЗАГОЛОВКИ КОЛОНОК (InfoCell, тиха підкладка, не вимкнена кнопка!) ---
        String modHeader = isUkr ? "--- МОДУЛІ АВТОМАТИЗАЦІЇ ---" : "--- AUTOMATION MODULES ---";
        InfoCell h1 = new InfoCell(fitText(modHeader, colW - padText), col1X, rowY, colW, colHeaderH, false);
        h1.setColors(CFG.COLOR_TEXT_NUM_OF_PROVINCES, new Color(0.08f, 0.11f, 0.18f, 0.85f), null);
        menuElements.add(h1);

        String prioHeader = isUkr ? "--- ЧЕРГА ПРІОРИТЕТІВ (1 -> 4) ---" : "--- PRIORITY ORDER (1 -> 4) ---";
        InfoCell h2 = new InfoCell(fitText(prioHeader, colW - padText), col2X, rowY, colW, colHeaderH, false);
        h2.setColors(CFG.COLOR_TEXT_NUM_OF_PROVINCES, new Color(0.08f, 0.11f, 0.18f, 0.85f), null);
        menuElements.add(h2);
        rowY += colHeaderH + gapM;

        // --- РЯДОК 2: АСИМІЛЯЦІЯ / ПРІОРИТЕТ 1 ---
        String assiText = isUkr 
                ? (AutoDevelopManager.autoAssimilate ? "[ВКЛ] Асиміляція (до 100% стабільності)" : "[ВИКЛ] Асиміляція (стабільність)")
                : (AutoDevelopManager.autoAssimilate ? "[ON] Assimilation (to 100% stability)" : "[OFF] Assimilation");
        Button_Classic assiBtn = new Button_Classic(fitText(assiText, colW - padText), -1, col1X, rowY, colW, btnH, true, AutoDevelopManager.autoAssimilate);
        menuElements.add(assiBtn);
        this.idToggleAssimilate = menuElements.size() - 1;

        int upDownW = btnH;
        int prioNameW = colW - (upDownW * 2 + pad * 2);
        this.addPriorityRow(menuElements, 0, col2X, rowY, prioNameW, upDownW, btnH, isUkr, padText);
        rowY += btnH + gapM;

        // --- РЯДОК 3: ЯРМАРКИ / ПРІОРИТЕТ 2 ---
        String festText = isUkr 
                ? (AutoDevelopManager.autoFestival ? "[ВКЛ] Проведення ярмарок (щастя)" : "[ВИКЛ] Проведення ярмарок (щастя)")
                : (AutoDevelopManager.autoFestival ? "[ON] Organize Festivals (happiness)" : "[OFF] Organize Festivals (happiness)");
        Button_Classic festBtn = new Button_Classic(fitText(festText, colW - padText), -1, col1X, rowY, colW, btnH, true, AutoDevelopManager.autoFestival);
        menuElements.add(festBtn);
        this.idToggleFestival = menuElements.size() - 1;

        this.addPriorityRow(menuElements, 1, col2X, rowY, prioNameW, upDownW, btnH, isUkr, padText);
        rowY += btnH + gapM;

        // --- РЯДОК 4: НАУКА / ПРІОРИТЕТ 3 ---
        String devText = isUkr 
                ? (AutoDevelopManager.autoInvestDevelopment ? "[ВКЛ] Дослідження (до рівня науки)" : "[ВИКЛ] Дослідження (наука)")
                : (AutoDevelopManager.autoInvestDevelopment ? "[ON] Research (to Civ Tech Level)" : "[OFF] Research Development");
        Button_Classic devBtn = new Button_Classic(fitText(devText, colW - padText), -1, col1X, rowY, colW, btnH, true, AutoDevelopManager.autoInvestDevelopment);
        menuElements.add(devBtn);
        this.idToggleDevelopment = menuElements.size() - 1;

        this.addPriorityRow(menuElements, 2, col2X, rowY, prioNameW, upDownW, btnH, isUkr, padText);
        rowY += btnH + gapM;

        // --- РЯДОК 5: ЕКОНОМІКА / ПРІОРИТЕТ 4 ---
        String ecoText = isUkr 
                ? (AutoDevelopManager.autoInvestEconomy ? "[ВКЛ] Розвиток економіки (інвестиції)" : "[ВИКЛ] Розвиток економіки (інвестиції)")
                : (AutoDevelopManager.autoInvestEconomy ? "[ON] Economy Investments" : "[OFF] Economy Investments");
        Button_Classic ecoBtn = new Button_Classic(fitText(ecoText, colW - padText), -1, col1X, rowY, colW, btnH, true, AutoDevelopManager.autoInvestEconomy);
        menuElements.add(ecoBtn);
        this.idToggleEconomy = menuElements.size() - 1;

        this.addPriorityRow(menuElements, 3, col2X, rowY, prioNameW, upDownW, btnH, isUkr, padText);
        rowY += btnH + gapM;

        // --- РЯДОК 6: ПІДПИС ЦІЛІ ЕКОНОМІКИ / ПРЕСЕТИ ПРІОРИТЕТІВ (КОРОТКІ НАЗВИ, ПОВНА ШИРИНА) ---
        String ecoTargetLabel = isUkr 
                ? ("Цільова економіка: " + AutoDevelopManager.targetEcoPercentage + "% від населення")
                : ("Target Economy: " + AutoDevelopManager.targetEcoPercentage + "% of Pop");
        menuElements.add(new InfoCell(fitText(ecoTargetLabel, colW - padText), col1X, rowY, colW, smallBtnH));

        // 3 пресети на повну ширину правої колонки, короткі назви через fitText
        int pPresetW = (colW - pad * 2) / 3;
        boolean isBal = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[1] == AutoDevelopManager.PRIO_FESTIVAL);
        boolean isEco = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ECONOMY);
        boolean isStab = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[1] == AutoDevelopManager.PRIO_DEVELOPMENT);

        String tBal = fitText(isUkr ? "Баланс" : "Balance", pPresetW - padText);
        String tEco = fitText(isUkr ? "Економ." : "Economy", pPresetW - padText);
        String tStab = fitText(isUkr ? "Стабіл." : "Stability", pPresetW - padText);

        Button_Classic pBal = isBal 
                ? new Button_Classic(tBal, -1, col2X, rowY, pPresetW, smallBtnH, true, true)
                : new Button_Classic(tBal, -1, col2X, rowY, pPresetW, smallBtnH, true);
        menuElements.add(pBal);
        this.idPrioPresetBalanced = menuElements.size() - 1;

        Button_Classic pEco = isEco 
                ? new Button_Classic(tEco, -1, col2X + pPresetW + pad, rowY, pPresetW, smallBtnH, true, true)
                : new Button_Classic(tEco, -1, col2X + pPresetW + pad, rowY, pPresetW, smallBtnH, true);
        menuElements.add(pEco);
        this.idPrioPresetEconomy = menuElements.size() - 1;

        Button_Classic pStab = isStab 
                ? new Button_Classic(tStab, -1, col2X + (pPresetW + pad) * 2, rowY, pPresetW, smallBtnH, true, true)
                : new Button_Classic(tStab, -1, col2X + (pPresetW + pad) * 2, rowY, pPresetW, smallBtnH, true);
        menuElements.add(pStab);
        this.idPrioPresetStability = menuElements.size() - 1;
        rowY += smallBtnH + gapM;

        // --- РЯДОК 7: КНОПКИ РЕГУЛЮВАННЯ ЕКОНОМІКИ / РЕЗЕРВ ХОДІВ ---
        int bW = (colW - pad * 5) / 6;
        int bStep = bW + pad;

        Button_Classic btnM10 = new Button_Classic("-10%", -1, col1X, rowY, bW, smallBtnH, true);
        menuElements.add(btnM10);
        this.idEcoM10 = menuElements.size() - 1;

        Button_Classic btnP10 = new Button_Classic("+10%", -1, col1X + bStep, rowY, bW, smallBtnH, true);
        menuElements.add(btnP10);
        this.idEcoP10 = menuElements.size() - 1;

        Button_Classic p80 = (AutoDevelopManager.targetEcoPercentage == 80)
                ? new Button_Classic("80%", -1, col1X + bStep * 2, rowY, bW, smallBtnH, true, true)
                : new Button_Classic("80%", -1, col1X + bStep * 2, rowY, bW, smallBtnH, true);
        menuElements.add(p80);
        this.idEco80 = menuElements.size() - 1;

        Button_Classic p100 = (AutoDevelopManager.targetEcoPercentage == 100)
                ? new Button_Classic("100%", -1, col1X + bStep * 3, rowY, bW, smallBtnH, true, true)
                : new Button_Classic("100%", -1, col1X + bStep * 3, rowY, bW, smallBtnH, true);
        menuElements.add(p100);
        this.idEco100 = menuElements.size() - 1;

        Button_Classic p120 = (AutoDevelopManager.targetEcoPercentage == 120)
                ? new Button_Classic("120%", -1, col1X + bStep * 4, rowY, bW, smallBtnH, true, true)
                : new Button_Classic("120%", -1, col1X + bStep * 4, rowY, bW, smallBtnH, true);
        menuElements.add(p120);
        this.idEco120 = menuElements.size() - 1;

        Button_Classic p150 = (AutoDevelopManager.targetEcoPercentage == 150)
                ? new Button_Classic("150%", -1, col1X + bStep * 5, rowY, bW, smallBtnH, true, true)
                : new Button_Classic("150%", -1, col1X + bStep * 5, rowY, bW, smallBtnH, true);
        menuElements.add(p150);
        this.idEco150 = menuElements.size() - 1;

        // Резерв ходів (підпис InfoCell, лише вибране значення має checked стиль)
        int resLabelW = Math.max(90, (int)(colW * 0.22f));
        int res4W = (colW - resLabelW - pad * 4) / 4;
        int resStep = res4W + pad;

        String resMovesTitle = isUkr ? "Ходи:" : "Moves:";
        menuElements.add(new InfoCell(fitText(resMovesTitle, resLabelW - padText), col2X, rowY, resLabelW, smallBtnH));

        Button_Classic rM0 = (AutoDevelopManager.minMovePointsReserve == 0)
                ? new Button_Classic("0.0", -1, col2X + resLabelW + pad, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("0.0", -1, col2X + resLabelW + pad, rowY, res4W, smallBtnH, true);
        menuElements.add(rM0);
        this.idResMove0 = menuElements.size() - 1;

        Button_Classic rM2 = (AutoDevelopManager.minMovePointsReserve == 20)
                ? new Button_Classic("2.0", -1, col2X + resLabelW + pad + resStep, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("2.0", -1, col2X + resLabelW + pad + resStep, rowY, res4W, smallBtnH, true);
        menuElements.add(rM2);
        this.idResMove2 = menuElements.size() - 1;

        Button_Classic rM4 = (AutoDevelopManager.minMovePointsReserve == 40)
                ? new Button_Classic("4.0", -1, col2X + resLabelW + pad + resStep * 2, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("4.0", -1, col2X + resLabelW + pad + resStep * 2, rowY, res4W, smallBtnH, true);
        menuElements.add(rM4);
        this.idResMove4 = menuElements.size() - 1;

        Button_Classic rM6 = (AutoDevelopManager.minMovePointsReserve == 60)
                ? new Button_Classic("6.0", -1, col2X + resLabelW + pad + resStep * 3, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("6.0", -1, col2X + resLabelW + pad + resStep * 3, rowY, res4W, smallBtnH, true);
        menuElements.add(rM6);
        this.idResMove6 = menuElements.size() - 1;
        rowY += smallBtnH + gapM;

        // --- РЯДОК 8: ТУМБЛЕР СПОВІЩЕНЬ / РЕЗЕРВ ЗОЛОТА ---
        String notifText = isUkr 
                ? (AutoDevelopManager.showNotifications ? "[ВКЛ] Сповіщення про дії" : "[ВИКЛ] Сповіщення про дії")
                : (AutoDevelopManager.showNotifications ? "[ON] Action Notifications" : "[OFF] Action Notifications");
        Button_Classic notifBtn = new Button_Classic(fitText(notifText, colW - padText), -1, col1X, rowY, colW, smallBtnH, true, AutoDevelopManager.showNotifications);
        menuElements.add(notifBtn);
        this.idToggleNotifications = menuElements.size() - 1;

        // Резерв золота (підпис InfoCell, лише вибране значення має checked стиль)
        String resGoldTitle = isUkr ? "Золото:" : "Gold:";
        menuElements.add(new InfoCell(fitText(resGoldTitle, resLabelW - padText), col2X, rowY, resLabelW, smallBtnH));

        Button_Classic rG0 = (AutoDevelopManager.minGoldReserve == 0)
                ? new Button_Classic("0", -1, col2X + resLabelW + pad, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("0", -1, col2X + resLabelW + pad, rowY, res4W, smallBtnH, true);
        menuElements.add(rG0);
        this.idResGold0 = menuElements.size() - 1;

        Button_Classic rG1k = (AutoDevelopManager.minGoldReserve == 1000)
                ? new Button_Classic("1 000", -1, col2X + resLabelW + pad + resStep, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("1 000", -1, col2X + resLabelW + pad + resStep, rowY, res4W, smallBtnH, true);
        menuElements.add(rG1k);
        this.idResGold1k = menuElements.size() - 1;

        Button_Classic rG5k = (AutoDevelopManager.minGoldReserve == 5000)
                ? new Button_Classic("5 000", -1, col2X + resLabelW + pad + resStep * 2, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("5 000", -1, col2X + resLabelW + pad + resStep * 2, rowY, res4W, smallBtnH, true);
        menuElements.add(rG5k);
        this.idResGold5k = menuElements.size() - 1;

        Button_Classic rG10k = (AutoDevelopManager.minGoldReserve == 10000)
                ? new Button_Classic("10 000", -1, col2X + resLabelW + pad + resStep * 3, rowY, res4W, smallBtnH, true, true)
                : new Button_Classic("10 000", -1, col2X + resLabelW + pad + resStep * 3, rowY, res4W, smallBtnH, true);
        menuElements.add(rG10k);
        this.idResGold10k = menuElements.size() - 1;
        rowY += smallBtnH;

        this.columnsHeight = rowY - this.columnsStartY;

        // ==========================================
        // НИЖНЯ ПАНЕЛЬ: ДІЇ (ЗАПУСК ТА ЗАКРИТТЯ)
        // ==========================================
        curY = rowY + gapL;

        String runText = isUkr ? "ЗАПУСТИТИ ЗАРАЗ (НА ПОТОЧНОМУ ХОДІ)" : "RUN AUTOMATION NOW (THIS TURN)";
        Button_Classic runBtn = new Button_Classic(fitText(runText, menuW - padText), -1, startX, curY, menuW, actionBtnH, true);
        menuElements.add(runBtn);
        this.idRunNow = menuElements.size() - 1;
        curY += actionBtnH + gapM;

        String closeTextBottom = isUkr ? "Повернутися до гри" : "Return to Game";
        Button_Classic closeBtnBottom = new Button_Classic(fitText(closeTextBottom, menuW - padText), -1, startX, curY, menuW, closeBottomH, true);
        menuElements.add(closeBtnBottom);
        this.idCloseBottom = menuElements.size() - 1;
        curY += closeBottomH;

        this.dialogHeight = curY - startY;

        // Поглинач кліків поза діалогом
        this.idCatchAll = menuElements.size();
        menuElements.add(new Button_Transparent(0, 0, gw, gh, true));

        this.initMenu(null, 0, 0, gw, gh, menuElements);
        super.setVisibleM(false);
    }

    private void addPriorityRow(List<MenuElemUI> menuElements, int i, int col2X, int rowY, int prioNameW, int upDownW, int btnH, boolean isUkr, int padText) {
        int prioType = AutoDevelopManager.priorities[i];
        String name = AutoDevelopManager.getPriorityName(prioType, isUkr);
        String rowText = "[ " + (i + 1) + " ] " + name;

        Button_Classic prioBtn = new Button_Classic(fitText(rowText, prioNameW - padText), -1, col2X, rowY, prioNameW, btnH, true);
        menuElements.add(prioBtn);
        this.idPrioRow[i] = menuElements.size() - 1;

        boolean canUp = (i > 0);
        Button_Classic upBtn = new Button_Classic("^", -1, col2X + prioNameW + CFG.PADD, rowY, upDownW, btnH, canUp);
        menuElements.add(upBtn);
        this.idPrioUp[i] = menuElements.size() - 1;

        boolean canDown = (i < 3);
        Button_Classic downBtn = new Button_Classic("v", -1, col2X + prioNameW + CFG.PADD + upDownW + CFG.PADD, rowY, upDownW, btnH, canDown);
        menuElements.add(downBtn);
        this.idPrioDown[i] = menuElements.size() - 1;
    }

    @Override
    public final void actionEL(int iID) {
        if (iID == this.idCloseTop || iID == this.idCloseBottom) {
            close();
            return;
        }

        // Майстер-тумблер
        if (iID == this.idToggleMaster) {
            AutoDevelopManager.autoDevelopEnabled = !AutoDevelopManager.autoDevelopEnabled;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Модулі
        if (iID == this.idToggleAssimilate) {
            AutoDevelopManager.autoAssimilate = !AutoDevelopManager.autoAssimilate;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idToggleFestival) {
            AutoDevelopManager.autoFestival = !AutoDevelopManager.autoFestival;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idToggleDevelopment) {
            AutoDevelopManager.autoInvestDevelopment = !AutoDevelopManager.autoInvestDevelopment;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idToggleEconomy) {
            AutoDevelopManager.autoInvestEconomy = !AutoDevelopManager.autoInvestEconomy;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Кроки регулювання економіки
        if (iID == this.idEcoM10) {
            AutoDevelopManager.targetEcoPercentage = Math.max(10, AutoDevelopManager.targetEcoPercentage - 10);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEcoP10) {
            AutoDevelopManager.targetEcoPercentage = Math.min(300, AutoDevelopManager.targetEcoPercentage + 10);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEco80) {
            AutoDevelopManager.targetEcoPercentage = 80;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEco100) {
            AutoDevelopManager.targetEcoPercentage = 100;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEco120) {
            AutoDevelopManager.targetEcoPercentage = 120;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEco150) {
            AutoDevelopManager.targetEcoPercentage = 150;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Сповіщення
        if (iID == this.idToggleNotifications) {
            AutoDevelopManager.showNotifications = !AutoDevelopManager.showNotifications;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Пріоритети (переміщення вгору / вниз)
        for (int i = 0; i < 4; i++) {
            if (iID == this.idPrioUp[i] && i > 0) {
                int temp = AutoDevelopManager.priorities[i];
                AutoDevelopManager.priorities[i] = AutoDevelopManager.priorities[i - 1];
                AutoDevelopManager.priorities[i - 1] = temp;
                AutoDevelopManager.saveConfig();
                this.rebuild();
                return;
            }
            if (iID == this.idPrioDown[i] && i < 3) {
                int temp = AutoDevelopManager.priorities[i];
                AutoDevelopManager.priorities[i] = AutoDevelopManager.priorities[i + 1];
                AutoDevelopManager.priorities[i + 1] = temp;
                AutoDevelopManager.saveConfig();
                this.rebuild();
                return;
            }
            if (iID == this.idPrioRow[i]) {
                if (i < 3) {
                    int temp = AutoDevelopManager.priorities[i];
                    AutoDevelopManager.priorities[i] = AutoDevelopManager.priorities[i + 1];
                    AutoDevelopManager.priorities[i + 1] = temp;
                } else {
                    int temp = AutoDevelopManager.priorities[3];
                    AutoDevelopManager.priorities[3] = AutoDevelopManager.priorities[0];
                    AutoDevelopManager.priorities[0] = temp;
                }
                AutoDevelopManager.saveConfig();
                this.rebuild();
                return;
            }
        }

        // Пресети пріоритетів
        if (iID == this.idPrioPresetBalanced) {
            AutoDevelopManager.priorities = new int[]{
                    AutoDevelopManager.PRIO_ASSIMILATE,
                    AutoDevelopManager.PRIO_FESTIVAL,
                    AutoDevelopManager.PRIO_ECONOMY,
                    AutoDevelopManager.PRIO_DEVELOPMENT
            };
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idPrioPresetEconomy) {
            AutoDevelopManager.priorities = new int[]{
                    AutoDevelopManager.PRIO_ECONOMY,
                    AutoDevelopManager.PRIO_DEVELOPMENT,
                    AutoDevelopManager.PRIO_ASSIMILATE,
                    AutoDevelopManager.PRIO_FESTIVAL
            };
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idPrioPresetStability) {
            AutoDevelopManager.priorities = new int[]{
                    AutoDevelopManager.PRIO_ASSIMILATE,
                    AutoDevelopManager.PRIO_DEVELOPMENT,
                    AutoDevelopManager.PRIO_FESTIVAL,
                    AutoDevelopManager.PRIO_ECONOMY
            };
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Резерви ходів
        if (iID == this.idResMove0) {
            AutoDevelopManager.minMovePointsReserve = 0;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idResMove2) {
            AutoDevelopManager.minMovePointsReserve = 20;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idResMove4) {
            AutoDevelopManager.minMovePointsReserve = 40;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idResMove6) {
            AutoDevelopManager.minMovePointsReserve = 60;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Резерви золота
        if (iID == this.idResGold0) {
            AutoDevelopManager.minGoldReserve = 0;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idResGold1k) {
            AutoDevelopManager.minGoldReserve = 1000;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idResGold5k) {
            AutoDevelopManager.minGoldReserve = 5000;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idResGold10k) {
            AutoDevelopManager.minGoldReserve = 10000;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Запуск зараз
        if (iID == this.idRunNow) {
            int playerCivID = -1;
            try {
                if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                    playerCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
                }
            } catch (Exception ignored) {
            }

            int count = (playerCivID > 0) ? AutoDevelopManager.developForCiv(playerCivID, false) : 0;
            if (count > 0) {
                try {
                    CFG.SFXManager.playSound(SFXManager.SFX_GOLD);
                } catch (Exception ignored) {
                }
            }
            this.rebuild();
            return;
        }

        if (iID == this.idCatchAll) {
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
        // 1. Повноекранне напівпрозоре затемнення
        oSB.setColor(new Color(0.02f, 0.03f, 0.05f, 0.88f));
        IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);

        // 2. Основна карточка модального вікна (точно за розрахованими межами)
        int pad = CFG.PADD * 2;
        oSB.setColor(new Color(0.07f, 0.09f, 0.14f, 0.95f));
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY - pad, this.dialogWidth + pad * 2, this.dialogHeight + pad * 2);

        // 3. Тонка рамка навколо діалогу (фірмовий золотистий колір гри)
        oSB.setColor(new Color(CFG.COLOR_FLAG_FRAME.r, CFG.COLOR_FLAG_FRAME.g, CFG.COLOR_FLAG_FRAME.b, 0.70f));
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY - pad, this.dialogWidth + pad * 2, 1);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY + this.dialogHeight + pad - 1, this.dialogWidth + pad * 2, 1);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY - pad, 1, this.dialogHeight + pad * 2);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX + this.dialogWidth + pad - 1, this.dialogStartY - pad, 1, this.dialogHeight + pad * 2);

        // 4. М'які підкладки під ліву та праву колонки (однакової висоти!)
        oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.22f));
        IMGManager.getIMG(Images.pix255).draw(oSB, this.col1XPos - CFG.PADD, this.columnsStartY - CFG.PADD, this.colWidth + CFG.PADD * 2, this.columnsHeight + CFG.PADD * 2);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.col2XPos - CFG.PADD, this.columnsStartY - CFG.PADD, this.colWidth + CFG.PADD * 2, this.columnsHeight + CFG.PADD * 2);

        oSB.setColor(Color.WHITE);

        this.beginClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.drawMenuM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.endClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }
}
