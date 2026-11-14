package age.of.civilizations2.jakowski.lukasz.Menus.AutoDevelop;

import age.of.civilizations2.jakowski.lukasz.Button.Button_Transparent;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Civilization;
import age.of.civilizations2.jakowski.lukasz.IMGManager;
import age.of.civilizations2.jakowski.lukasz.Images;
import age.of.civilizations2.jakowski.lukasz.Menu;
import age.of.civilizations2.jakowski.lukasz.SFXManager;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextTitleStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu_AutoDevelop - елегантне, просторе та професійне вікно налаштування та запуску
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

    public void rebuild() {
        List<MenuElemUI> menuElements = new ArrayList<MenuElemUI>();
        boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");

        // 1. Адаптивні пропорційні розміри діалогу
        int menuW = Math.min(840, CFG.GAMEWIDTH - CFG.PADD * 6);
        int startX = (CFG.GAMEWIDTH - menuW) / 2;

        int availableH = CFG.GAMEHEIGHT - CFG.PADD * 6;
        int btnH = Math.min(44, Math.max(34, availableH / 21));
        int smallBtnH = Math.min(34, Math.max(26, availableH / 26));
        int titleH = btnH;

        int colGap = CFG.PADD * 3;
        int colW = (menuW - colGap) / 2;
        int col1X = startX;
        int col2X = startX + colW + colGap;

        // Розрахунок повної висоти для вертикального центрування
        int rightColContentH = smallBtnH + CFG.PADD + (4 * (btnH + CFG.PADD)) + smallBtnH + CFG.PADD * 2
                + smallBtnH + CFG.PADD + (smallBtnH * 2 + CFG.PADD * 2) + (smallBtnH * 2 + CFG.PADD * 2);
        int totalExpectedH = titleH + CFG.PADD + smallBtnH + CFG.PADD + smallBtnH + CFG.PADD * 2
                + btnH + CFG.PADD * 2 + rightColContentH + CFG.PADD * 2 + (int)(btnH * 1.15f) + CFG.PADD + btnH;

        int startY = Math.max(CFG.PADD * 2, (CFG.GAMEHEIGHT - totalExpectedH) / 2);
        int curY = startY;

        // Зберігаємо геометрію для draw()
        this.dialogStartX = startX;
        this.dialogStartY = startY;
        this.dialogWidth = menuW;
        this.col1XPos = col1X;
        this.col2XPos = col2X;
        this.colWidth = colW;

        // 2. Верхній рядок: Заголовок та кнопка закриття
        int closeBtnW = btnH * 3;
        String titleText = isUkr ? "АВТО-РОЗВИТОК ПРОВІНЦІЙ" : "PROVINCE AUTO-DEVELOPMENT";
        TextTitleStyle titleElem = new TextTitleStyle(titleText, -1, startX, curY, menuW - closeBtnW - CFG.PADD, titleH);
        menuElements.add(titleElem);

        Button_Classic closeTopBtn = new Button_Classic(isUkr ? "Закрити" : "Close", -1, startX + menuW - closeBtnW, curY, closeBtnW, titleH, true);
        menuElements.add(closeTopBtn);
        this.idCloseTop = menuElements.size() - 1;
        curY += titleH + CFG.PADD;

        // 3. Дані гравця та ресурси
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

        StringBuilder sbOverview = new StringBuilder();
        sbOverview.append(civName).append(" (").append(numProvs).append(isUkr ? " пров.)  |  " : " prov.)  |  ");
        sbOverview.append(isUkr ? "Ходи: " : "Moves: ").append(CFG.getPrecision2(movPts, 10)).append("  |  ");
        sbOverview.append(isUkr ? "Дипломатія: " : "Diplo: ").append(CFG.getPrecision2(dipPts, 10)).append("  |  ");
        sbOverview.append(isUkr ? "Золото: " : "Gold: ").append(CFG.getNumberWthSpaces("" + gold));

        Button_Classic overviewBar = new Button_Classic(sbOverview.toString(), -1, startX, curY, menuW, smallBtnH, false);
        menuElements.add(overviewBar);
        curY += smallBtnH + CFG.PADD;

        // 4. Рядок живої діагностики потреб провінцій
        int needAssi = (playerCivID > 0) ? AutoDevelopManager.getNeedAssimilateCount(playerCivID) : 0;
        int needFest = (playerCivID > 0) ? AutoDevelopManager.getNeedFestivalCount(playerCivID) : 0;
        int needEco = (playerCivID > 0) ? AutoDevelopManager.getNeedEconomyCount(playerCivID) : 0;
        int needDev = (playerCivID > 0) ? AutoDevelopManager.getNeedDevelopmentCount(playerCivID) : 0;
        int totalNeed = needAssi + needFest + needEco + needDev;

        String diagText;
        if (totalNeed > 0) {
            diagText = isUkr 
                    ? ("Потребують дій: " + needAssi + " асим.  |  " + needFest + " ярмарок  |  " + needEco + " екон.  |  " + needDev + " науки")
                    : ("Require action: " + needAssi + " assim.  |  " + needFest + " fest.  |  " + needEco + " eco.  |  " + needDev + " dev.");
        } else {
            diagText = isUkr 
                    ? "Усі провінції в ідеальному стані! (Цілі розвитку повністю досягнуто)" 
                    : "All provinces meet development targets perfectly!";
        }
        Button_Classic diagBar = new Button_Classic(diagText, -1, startX, curY, menuW, smallBtnH, false, totalNeed > 0);
        menuElements.add(diagBar);
        curY += smallBtnH + CFG.PADD * 2;

        // 5. Головний майстер-перемикач (великий помітний банер)
        String masterText = isUkr 
                ? (AutoDevelopManager.autoDevelopEnabled ? "[ВКЛ] АВТО-РОЗВИТОК ПРАЦЮЄ ЩОХОДУ" : "[ВИКЛ] АВТО-РОЗВИТОК ВИМКНЕНО")
                : (AutoDevelopManager.autoDevelopEnabled ? "[ON] AUTO-DEVELOPMENT RUNS EVERY TURN" : "[OFF] AUTO-DEVELOPMENT DISABLED");
        Button_Classic masterBtn = new Button_Classic(masterText, -1, startX, curY, menuW, btnH, true, AutoDevelopManager.autoDevelopEnabled);
        menuElements.add(masterBtn);
        this.idToggleMaster = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        this.columnsStartY = curY;

        // ==========================================
        // ЛІВА КОЛОНКА: МОДУЛІ АВТОМАТИЗАЦІЇ
        // ==========================================
        int leftY = this.columnsStartY;

        String modHeader = isUkr ? "--- МОДУЛІ АВТОМАТИЗАЦІЇ ---" : "--- AUTOMATION MODULES ---";
        menuElements.add(new Button_Classic(modHeader, -1, col1X, leftY, colW, smallBtnH, false));
        leftY += smallBtnH + CFG.PADD;

        // 1. Асиміляція
        String assiText = isUkr 
                ? (AutoDevelopManager.autoAssimilate ? "[ВКЛ] Асиміляція (до 100% стабільності)" : "[ВИКЛ] Асиміляція (стабільність)")
                : (AutoDevelopManager.autoAssimilate ? "[ON] Assimilation (to 100% stability)" : "[OFF] Assimilation");
        Button_Classic assiBtn = new Button_Classic(assiText, -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoAssimilate);
        menuElements.add(assiBtn);
        this.idToggleAssimilate = menuElements.size() - 1;
        leftY += btnH + CFG.PADD;

        // 2. Ярмарки
        String festText = isUkr 
                ? (AutoDevelopManager.autoFestival ? "[ВКЛ] Проведення ярмарок (щастя)" : "[ВИКЛ] Проведення ярмарок (щастя)")
                : (AutoDevelopManager.autoFestival ? "[ON] Organize Festivals (happiness)" : "[OFF] Organize Festivals (happiness)");
        Button_Classic festBtn = new Button_Classic(festText, -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoFestival);
        menuElements.add(festBtn);
        this.idToggleFestival = menuElements.size() - 1;
        leftY += btnH + CFG.PADD;

        // 3. Наука / Дослідження
        String devText = isUkr 
                ? (AutoDevelopManager.autoInvestDevelopment ? "[ВКЛ] Дослідження (до рівня науки держави)" : "[ВИКЛ] Дослідження (наука)")
                : (AutoDevelopManager.autoInvestDevelopment ? "[ON] Research (to Civ Tech Level)" : "[OFF] Research Development");
        Button_Classic devBtn = new Button_Classic(devText, -1, col1X, leftY, colW, btnH, true, AutoInvestDevState());
        menuElements.add(devBtn);
        this.idToggleDevelopment = menuElements.size() - 1;
        leftY += btnH + CFG.PADD;

        // 4. Економіка
        String ecoText = isUkr 
                ? (AutoDevelopManager.autoInvestEconomy ? "[ВКЛ] Розвиток економіки (інвестиції)" : "[ВИКЛ] Розвиток економіки (інвестиції)")
                : (AutoDevelopManager.autoInvestEconomy ? "[ON] Economy Investments" : "[OFF] Economy Investments");
        Button_Classic ecoBtn = new Button_Classic(ecoText, -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoInvestEconomy);
        menuElements.add(ecoBtn);
        this.idToggleEconomy = menuElements.size() - 1;
        leftY += btnH + CFG.PADD;

        // Регулювання цілі економіки
        String ecoTargetLabel = isUkr 
                ? ("Цільова економіка: " + AutoDevelopManager.targetEcoPercentage + "% від населення")
                : ("Target Economy: " + AutoDevelopManager.targetEcoPercentage + "% of Pop");
        menuElements.add(new Button_Classic(ecoTargetLabel, -1, col1X, leftY, colW, smallBtnH, false));
        leftY += smallBtnH + CFG.PADD;

        // 6 кнопок регулювання в один чистий рівний рядок
        int bW = (colW - CFG.PADD * 5) / 6;
        Button_Classic btnM10 = new Button_Classic("-10%", -1, col1X, leftY, bW, smallBtnH, true);
        menuElements.add(btnM10);
        this.idEcoM10 = menuElements.size() - 1;

        Button_Classic btnP10 = new Button_Classic("+10%", -1, col1X + bW + CFG.PADD, leftY, bW, smallBtnH, true);
        menuElements.add(btnP10);
        this.idEcoP10 = menuElements.size() - 1;

        Button_Classic p80 = new Button_Classic("80%", -1, col1X + (bW + CFG.PADD) * 2, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 80);
        menuElements.add(p80);
        this.idEco80 = menuElements.size() - 1;

        Button_Classic p100 = new Button_Classic("100%", -1, col1X + (bW + CFG.PADD) * 3, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 100);
        menuElements.add(p100);
        this.idEco100 = menuElements.size() - 1;

        Button_Classic p120 = new Button_Classic("120%", -1, col1X + (bW + CFG.PADD) * 4, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 120);
        menuElements.add(p120);
        this.idEco120 = menuElements.size() - 1;

        Button_Classic p150 = new Button_Classic("150%", -1, col1X + (bW + CFG.PADD) * 5, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 150);
        menuElements.add(p150);
        this.idEco150 = menuElements.size() - 1;
        leftY += smallBtnH + CFG.PADD * 2;

        // Сповіщення
        String notifText = isUkr 
                ? (AutoDevelopManager.showNotifications ? "[ВКЛ] Сповіщення про виконані дії" : "[ВИКЛ] Сповіщення про виконані дії")
                : (AutoDevelopManager.showNotifications ? "[ON] Notifications" : "[OFF] Notifications");
        Button_Classic notifBtn = new Button_Classic(notifText, -1, col1X, leftY, colW, smallBtnH, true, AutoDevelopManager.showNotifications);
        menuElements.add(notifBtn);
        this.idToggleNotifications = menuElements.size() - 1;
        leftY += smallBtnH;

        // ==========================================
        // ПРАВА КОЛОНКА: ПРІОРИТЕТИ ТА РЕЗЕРВИ
        // ==========================================
        int rightY = this.columnsStartY;

        String prioHeader = isUkr ? "--- ЧЕРГА ПРІОРИТЕТІВ (1 -> 4) ---" : "--- PRIORITY ORDER (1 -> 4) ---";
        menuElements.add(new Button_Classic(prioHeader, -1, col2X, rightY, colW, smallBtnH, false));
        rightY += smallBtnH + CFG.PADD;

        // 4 рядки пріоритетів без налізань
        int upDownW = Math.max(38, smallBtnH * 12 / 10);
        int prioNameW = colW - (upDownW * 2 + CFG.PADD * 2);

        for (int i = 0; i < 4; i++) {
            int prioType = AutoDevelopManager.priorities[i];
            String name = AutoDevelopManager.getPriorityName(prioType, isUkr);
            String rowText = "[ " + (i + 1) + " ] " + name;

            Button_Classic prioBtn = new Button_Classic(rowText, -1, col2X, rightY, prioNameW, btnH, true);
            menuElements.add(prioBtn);
            this.idPrioRow[i] = menuElements.size() - 1;

            boolean canUp = (i > 0);
            Button_Classic upBtn = new Button_Classic("^", -1, col2X + prioNameW + CFG.PADD, rightY, upDownW, btnH, canUp);
            menuElements.add(upBtn);
            this.idPrioUp[i] = menuElements.size() - 1;

            boolean canDown = (i < 3);
            Button_Classic downBtn = new Button_Classic("v", -1, col2X + prioNameW + CFG.PADD + upDownW + CFG.PADD, rightY, upDownW, btnH, canDown);
            menuElements.add(downBtn);
            this.idPrioDown[i] = menuElements.size() - 1;

            rightY += btnH + CFG.PADD;
        }

        // Пресети пріоритетів
        int pPresetW = (colW - CFG.PADD * 2) / 3;
        boolean isBal = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[1] == AutoDevelopManager.PRIO_FESTIVAL);
        boolean isEco = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ECONOMY);
        boolean isStab = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[1] == AutoDevelopManager.PRIO_DEVELOPMENT);

        Button_Classic pBal = new Button_Classic(isUkr ? "Баланс" : "Balanced", -1, col2X, rightY, pPresetW, smallBtnH, true, isBal);
        menuElements.add(pBal);
        this.idPrioPresetBalanced = menuElements.size() - 1;

        Button_Classic pEco = new Button_Classic(isUkr ? "Економіка" : "Economy", -1, col2X + pPresetW + CFG.PADD, rightY, pPresetW, smallBtnH, true, isEco);
        menuElements.add(pEco);
        this.idPrioPresetEconomy = menuElements.size() - 1;

        Button_Classic pStab = new Button_Classic(isUkr ? "Стабільність" : "Stability", -1, col2X + (pPresetW + CFG.PADD) * 2, rightY, pPresetW, smallBtnH, true, isStab);
        menuElements.add(pStab);
        this.idPrioPresetStability = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD * 2;

        // Захисні резерви
        String resHeader = isUkr ? "--- ЗАХИСНІ РЕЗЕРВИ РЕСУРСІВ ---" : "--- SAFETY RESERVES ---";
        menuElements.add(new Button_Classic(resHeader, -1, col2X, rightY, colW, smallBtnH, false));
        rightY += smallBtnH + CFG.PADD;

        // Резерв ходів
        float moveResVal = (float)AutoDevelopManager.minMovePointsReserve / 10.0f;
        String moveResText = isUkr ? ("Не витрачати ходи: " + moveResVal + " (для армії)") : ("Keep Move Points: " + moveResVal + " (for army)");
        menuElements.add(new Button_Classic(moveResText, -1, col2X, rightY, colW, smallBtnH, false));
        rightY += smallBtnH + CFG.PADD;

        int m4W = (colW - CFG.PADD * 3) / 4;
        Button_Classic rM0 = new Button_Classic("0.0", -1, col2X, rightY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 0);
        menuElements.add(rM0);
        this.idResMove0 = menuElements.size() - 1;

        Button_Classic rM2 = new Button_Classic("2.0", -1, col2X + m4W + CFG.PADD, rightY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 20);
        menuElements.add(rM2);
        this.idResMove2 = menuElements.size() - 1;

        Button_Classic rM4 = new Button_Classic("4.0", -1, col2X + (m4W + CFG.PADD) * 2, rightY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 40);
        menuElements.add(rM4);
        this.idResMove4 = menuElements.size() - 1;

        Button_Classic rM6 = new Button_Classic("6.0", -1, col2X + (m4W + CFG.PADD) * 3, rightY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 60);
        menuElements.add(rM6);
        this.idResMove6 = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD;

        // Резерв золота
        String goldResText = isUkr ? ("Резерв скарбниці: " + CFG.getNumberWthSpaces("" + AutoDevelopManager.minGoldReserve) + " золота") : ("Treasury Reserve: " + CFG.getNumberWthSpaces("" + AutoDevelopManager.minGoldReserve) + " gold");
        menuElements.add(new Button_Classic(goldResText, -1, col2X, rightY, colW, smallBtnH, false));
        rightY += smallBtnH + CFG.PADD;

        Button_Classic rG0 = new Button_Classic("0", -1, col2X, rightY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 0);
        menuElements.add(rG0);
        this.idResGold0 = menuElements.size() - 1;

        Button_Classic rG1k = new Button_Classic("1 000", -1, col2X + m4W + CFG.PADD, rightY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 1000);
        menuElements.add(rG1k);
        this.idResGold1k = menuElements.size() - 1;

        Button_Classic rG5k = new Button_Classic("5 000", -1, col2X + (m4W + CFG.PADD) * 2, rightY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 5000);
        menuElements.add(rG5k);
        this.idResGold5k = menuElements.size() - 1;

        Button_Classic rG10k = new Button_Classic("10 000", -1, col2X + (m4W + CFG.PADD) * 3, rightY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 10000);
        menuElements.add(rG10k);
        this.idResGold10k = menuElements.size() - 1;
        rightY += smallBtnH;

        this.columnsHeight = Math.max(leftY, rightY) - this.columnsStartY;

        // ==========================================
        // НИЖНЯ ПАНЕЛЬ: ДІЇ (ЗАПУСК ТА ЗАКРИТТЯ)
        // ==========================================
        int bottomY = this.columnsStartY + this.columnsHeight + CFG.PADD * 2;

        int actionBtnH = (int)(btnH * 1.15f);
        String runText = isUkr ? "ЗАПУСТИТИ ЗАРАЗ (НА ПОТОЧНОМУ ХОДІ)" : "RUN AUTOMATION NOW (THIS TURN)";
        Button_Classic runBtn = new Button_Classic(runText, -1, startX, bottomY, menuW, actionBtnH, true);
        menuElements.add(runBtn);
        this.idRunNow = menuElements.size() - 1;
        bottomY += actionBtnH + CFG.PADD;

        String closeTextBottom = isUkr ? "Повернутися до гри" : "Return to Game";
        Button_Classic closeBtnBottom = new Button_Classic(closeTextBottom, -1, startX, bottomY, menuW, btnH, true);
        menuElements.add(closeBtnBottom);
        this.idCloseBottom = menuElements.size() - 1;
        bottomY += btnH;

        this.dialogHeight = bottomY - startY;

        // Поглинач кліків поза діалогом
        this.idCatchAll = menuElements.size();
        menuElements.add(new Button_Transparent(0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, true));

        this.initMenu(null, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, menuElements);
        super.setVisibleM(false);
    }

    private static boolean AutoInvestDevState() {
        return AutoDevelopManager.autoInvestDevelopment;
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
        // 1. Повноекранне напівпрозоре затемнення з ефектом фокусу на модальному вікні
        oSB.setColor(new Color(0.02f, 0.03f, 0.05f, 0.88f));
        IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);

        // 2. Основна карточка модального вікна
        int pad = CFG.PADD * 2;
        oSB.setColor(new Color(0.07f, 0.09f, 0.14f, 0.94f));
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY - pad, this.dialogWidth + pad * 2, this.dialogHeight + pad * 2);

        // 3. Тонка рамка навколо діалогу (фірмовий стиль гри)
        oSB.setColor(new Color(CFG.COLOR_FLAG_FRAME.r, CFG.COLOR_FLAG_FRAME.g, CFG.COLOR_FLAG_FRAME.b, 0.65f));
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY - pad, this.dialogWidth + pad * 2, 1);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY + this.dialogHeight + pad - 1, this.dialogWidth + pad * 2, 1);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX - pad, this.dialogStartY - pad, 1, this.dialogHeight + pad * 2);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.dialogStartX + this.dialogWidth + pad - 1, this.dialogStartY - pad, 1, this.dialogHeight + pad * 2);

        // 4. М'які підкладки під ліву та праву колонки
        oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.22f));
        IMGManager.getIMG(Images.pix255).draw(oSB, this.col1XPos - CFG.PADD, this.columnsStartY - CFG.PADD / 2, this.colWidth + CFG.PADD * 2, this.columnsHeight + CFG.PADD);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.col2XPos - CFG.PADD, this.columnsStartY - CFG.PADD / 2, this.colWidth + CFG.PADD * 2, this.columnsHeight + CFG.PADD);

        oSB.setColor(Color.WHITE);

        this.beginClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.drawMenuM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.endClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }
}
