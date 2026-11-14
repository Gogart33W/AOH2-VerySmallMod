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
 * Menu_AutoDevelop - професійне повноекранне вікно налаштування та запуску автоматичного розвитку держави.
 */
public class Menu_AutoDevelop extends Menu {
    private static int autoDevelopMenuID = -1;
    private static Menu_AutoDevelop instance = null;

    // Кнопки закриття та майстер-тумблер
    private int idCloseTop = -1;
    private int idToggleMaster = -1;

    // Пріоритети
    private int[] idPrioRow = new int[4];
    private int[] idPrioUp = new int[4];
    private int[] idPrioDown = new int[4];
    private int idPrioPresetBalanced = -1;
    private int idPrioPresetEconomy = -1;
    private int idPrioPresetStability = -1;

    // Резерви
    private int idResMove0 = -1;
    private int idResMove2 = -1;
    private int idResMove4 = -1;
    private int idResMove6 = -1;

    private int idResGold0 = -1;
    private int idResGold1k = -1;
    private int idResGold5k = -1;
    private int idResGold10k = -1;

    // Модулі
    private int idToggleAssimilate = -1;
    private int idStab90 = -1;
    private int idStab95 = -1;
    private int idStab100 = -1;

    private int idToggleFestival = -1;
    private int idHappi70 = -1;
    private int idHappi80 = -1;
    private int idHappi90 = -1;

    private int idToggleEconomy = -1;
    private int idEcoPercLabel = -1;
    private int idEcoM50 = -1;
    private int idEcoM10 = -1;
    private int idEcoP10 = -1;
    private int idEcoP50 = -1;
    private int idEco80 = -1;
    private int idEco100 = -1;
    private int idEco120 = -1;
    private int idEco150 = -1;
    private int idEco200 = -1;

    private int idToggleDevelopment = -1;
    private int idToggleNotifications = -1;

    // Дії
    private int idRunNow = -1;
    private int idCloseBottom = -1;
    private int idCatchAll = -1;

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

        int menuW = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 2.3f), CFG.GAMEWIDTH - CFG.PADD * 4);
        int startX = (CFG.GAMEWIDTH - menuW) / 2;
        int btnH = Math.max(CFG.BUTTON_H * 7 / 10, CFG.TEXT_HEIGHT_DEFAULT + CFG.PADD * 2);
        int smallBtnH = Math.max(CFG.BUTTON_H * 3 / 5, CFG.TEXT_HEIGHT_DEFAULT + CFG.PADD);
        int colGap = CFG.PADD * 2;
        int colW = (menuW - colGap) / 2;
        int col1X = startX;
        int col2X = startX + colW + colGap;

        int curY = CFG.PADD * 2;

        // 1. Верхній рядок: Заголовок та кнопка закриття
        int titleH = btnH * 9 / 10;
        int closeBtnW = btnH * 3;
        String titleText = isUkr ? "АВТОМАТИЗАЦІЯ ДЕРЖАВИ ТА ПРОВІНЦІЙ" : "STATE & PROVINCE AUTOMATION";
        TextTitleStyle titleElem = new TextTitleStyle(titleText, -1, startX, curY, menuW - closeBtnW - CFG.PADD, titleH);
        menuElements.add(titleElem);

        Button_Classic closeTopBtn = new Button_Classic(isUkr ? "Закрити" : "Close", -1, startX + menuW - closeBtnW, curY, closeBtnW, titleH, true);
        menuElements.add(closeTopBtn);
        this.idCloseTop = menuElements.size() - 1;
        curY += titleH + CFG.PADD;

        // 2. Отримуємо дані гравця
        int playerCivID = -1;
        try {
            if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                playerCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
            }
        } catch (Exception ignored) {
        }

        Civilization pCiv = (playerCivID > 0) ? CFG.core.getCiv(playerCivID) : null;

        // 3. Рядок назви держави
        String civLineText = (pCiv != null)
                ? (isUkr ? ("Держава: " + pCiv.getCivName() + " (" + pCiv.getNumOfProvs() + " провінцій)")
                         : ("Civilization: " + pCiv.getCivName() + " (" + pCiv.getNumOfProvs() + " provinces)"))
                : (isUkr ? "Керування розвитком імперії" : "Empire development management");
        Button_Classic civLine = new Button_Classic(civLineText, -1, startX, curY, menuW, smallBtnH, false);
        menuElements.add(civLine);
        curY += smallBtnH + CFG.PADD;

        // 4. Три окремі плашки ресурсів (щоб ніколи не обрізався текст)
        int badgeW = (menuW - CFG.PADD * 2) / 3;
        float movPts = (pCiv != null) ? (float)pCiv.getMovemPoints() / 10.0f : 0.0f;
        float dipPts = (pCiv != null) ? (float)pCiv.getDiploPoints() / 10.0f : 0.0f;
        long gold = (pCiv != null) ? pCiv.getGold() : 0L;

        String bMov = isUkr ? ("Ходи: " + CFG.getPrecision2(movPts, 10)) : ("Moves: " + CFG.getPrecision2(movPts, 10));
        String bDip = isUkr ? ("Дипломатія: " + CFG.getPrecision2(dipPts, 10)) : ("Diplo: " + CFG.getPrecision2(dipPts, 10));
        String bGld = isUkr ? ("Золото: " + CFG.getNumberWthSpaces("" + gold)) : ("Gold: " + CFG.getNumberWthSpaces("" + gold));

        menuElements.add(new Button_Classic(bMov, -1, startX, curY, badgeW, smallBtnH, false));
        menuElements.add(new Button_Classic(bDip, -1, startX + badgeW + CFG.PADD, curY, badgeW, smallBtnH, false));
        menuElements.add(new Button_Classic(bGld, -1, startX + (badgeW + CFG.PADD) * 2, curY, badgeW, smallBtnH, false));
        curY += smallBtnH + CFG.PADD;

        // 5. Рядок живої діагностики потреб провінцій
        int needAssi = (playerCivID > 0) ? AutoDevelopManager.getNeedAssimilateCount(playerCivID) : 0;
        int needFest = (playerCivID > 0) ? AutoDevelopManager.getNeedFestivalCount(playerCivID) : 0;
        int needEco = (playerCivID > 0) ? AutoDevelopManager.getNeedEconomyCount(playerCivID) : 0;
        int needDev = (playerCivID > 0) ? AutoDevelopManager.getNeedDevelopmentCount(playerCivID) : 0;
        int totalNeed = needAssi + needFest + needEco + needDev;

        String diagText;
        if (totalNeed > 0) {
            diagText = isUkr 
                    ? ("Потребують дій: " + needAssi + " асим. | " + needFest + " ярмар. | " + needEco + " екон. | " + needDev + " науки")
                    : ("Require action: " + needAssi + " assim. | " + needFest + " fest. | " + needEco + " eco. | " + needDev + " dev.");
        } else {
            diagText = isUkr 
                    ? "Усі провінції в ідеальному стані! (Цілі розвитку повністю досягнуто)" 
                    : "All provinces meet development targets perfectly!";
        }
        Button_Classic diagBar = new Button_Classic(diagText, -1, startX, curY, menuW, smallBtnH, false, totalNeed > 0);
        menuElements.add(diagBar);
        curY += smallBtnH + CFG.PADD * 2;

        int columnsStartY = curY;

        // ==========================================
        // ЛІВА КОЛОНКА: СТРАТЕГІЯ, ПРІОРИТЕТИ, РЕЗЕРВИ
        // ==========================================
        int leftY = columnsStartY;

        // Головний перемикач
        String masterText = isUkr 
                ? (AutoDevelopManager.autoDevelopEnabled ? "[ВКЛ] АВТО-УПРАВЛІННЯ (ЩОХОДУ)" : "[ВИКЛ] АВТО-УПРАВЛІННЯ (ВИМКНЕНО)")
                : (AutoDevelopManager.autoDevelopEnabled ? "[ON] AUTO-MANAGEMENT (EVERY TURN)" : "[OFF] AUTO-MANAGEMENT (DISABLED)");
        Button_Classic masterBtn = new Button_Classic(masterText, -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoDevelopEnabled);
        menuElements.add(masterBtn);
        this.idToggleMaster = menuElements.size() - 1;
        leftY += btnH + CFG.PADD * 2;

        // Заголовок пріоритетів
        String prioHeader = isUkr ? "--- ПОРЯДОК ПРІОРИТЕТІВ (1 -> 4) ---" : "--- PRIORITY ORDER (1 -> 4) ---";
        Button_Classic prioHeadElem = new Button_Classic(prioHeader, -1, col1X, leftY, colW, smallBtnH, false);
        menuElements.add(prioHeadElem);
        leftY += smallBtnH + CFG.PADD;

        // 4 рядки пріоритетів з текстовими кнопками [ВГОРУ] та [ВНИЗ]
        int upBtnW = btnH * 11 / 10;
        int prioNameW = colW - (upBtnW + CFG.PADD) * 2;

        for (int i = 0; i < 4; i++) {
            int prioType = AutoDevelopManager.priorities[i];
            String name = AutoDevelopManager.getPriorityName(prioType, isUkr);
            String rowText = "[ " + (i + 1) + " ] " + name;

            // Кнопка назви пріоритету
            Button_Classic prioBtn = new Button_Classic(rowText, -1, col1X, leftY, prioNameW, btnH, true);
            menuElements.add(prioBtn);
            this.idPrioRow[i] = menuElements.size() - 1;

            // Кнопка [ВГОРУ]
            boolean canUp = (i > 0);
            String upText = isUkr ? "ВГОРУ" : "UP";
            Button_Classic upBtn = new Button_Classic(upText, -1, col1X + prioNameW + CFG.PADD, leftY, upBtnW, btnH, canUp);
            menuElements.add(upBtn);
            this.idPrioUp[i] = menuElements.size() - 1;

            // Кнопка [ВНИЗ]
            boolean canDown = (i < 3);
            String downText = isUkr ? "ВНИЗ" : "DOWN";
            Button_Classic downBtn = new Button_Classic(downText, -1, col1X + prioNameW + CFG.PADD + upBtnW + CFG.PADD, leftY, upBtnW, btnH, canDown);
            menuElements.add(downBtn);
            this.idPrioDown[i] = menuElements.size() - 1;

            leftY += btnH + CFG.PADD;
        }

        // Пресети пріоритетів
        int pPresetW = (colW - CFG.PADD * 2) / 3;
        boolean isBal = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[1] == AutoDevelopManager.PRIO_FESTIVAL);
        boolean isEco = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ECONOMY);
        boolean isStab = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[2] == AutoDevelopManager.PRIO_DEVELOPMENT);

        Button_Classic pBal = new Button_Classic(isUkr ? "Баланс" : "Balanced", -1, col1X, leftY, pPresetW, smallBtnH, true, isBal);
        menuElements.add(pBal);
        this.idPrioPresetBalanced = menuElements.size() - 1;

        Button_Classic pEco = new Button_Classic(isUkr ? "Економіка" : "Economy", -1, col1X + pPresetW + CFG.PADD, leftY, pPresetW, smallBtnH, true, isEco);
        menuElements.add(pEco);
        this.idPrioPresetEconomy = menuElements.size() - 1;

        Button_Classic pStab = new Button_Classic(isUkr ? "Стабільність" : "Stability", -1, col1X + (pPresetW + CFG.PADD) * 2, leftY, pPresetW, smallBtnH, true, isStab);
        menuElements.add(pStab);
        this.idPrioPresetStability = menuElements.size() - 1;
        leftY += smallBtnH + CFG.PADD * 2;

        // Заголовок резервів
        String resHeader = isUkr ? "--- ЗАХИСНІ РЕЗЕРВИ РЕСУРСІВ ---" : "--- SAFETY RESERVES ---";
        Button_Classic resHeadElem = new Button_Classic(resHeader, -1, col1X, leftY, colW, smallBtnH, false);
        menuElements.add(resHeadElem);
        leftY += smallBtnH + CFG.PADD;

        // Резерв очок ходів
        float moveResVal = (float)AutoDevelopManager.minMovePointsReserve / 10.0f;
        String moveResText = isUkr ? ("Не витрачати ходи: " + moveResVal + " (залишати для армії)") : ("Keep Move Points: " + moveResVal + " (for army)");
        Button_Classic moveResLabel = new Button_Classic(moveResText, -1, col1X, leftY, colW, smallBtnH, false);
        menuElements.add(moveResLabel);
        leftY += smallBtnH + CFG.PADD;

        int m4W = (colW - CFG.PADD * 3) / 4;
        Button_Classic rM0 = new Button_Classic("0.0", -1, col1X, leftY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 0);
        menuElements.add(rM0);
        this.idResMove0 = menuElements.size() - 1;

        Button_Classic rM2 = new Button_Classic("2.0", -1, col1X + m4W + CFG.PADD, leftY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 20);
        menuElements.add(rM2);
        this.idResMove2 = menuElements.size() - 1;

        Button_Classic rM4 = new Button_Classic("4.0", -1, col1X + (m4W + CFG.PADD) * 2, leftY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 40);
        menuElements.add(rM4);
        this.idResMove4 = menuElements.size() - 1;

        Button_Classic rM6 = new Button_Classic("6.0", -1, col1X + (m4W + CFG.PADD) * 3, leftY, m4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 60);
        menuElements.add(rM6);
        this.idResMove6 = menuElements.size() - 1;
        leftY += smallBtnH + CFG.PADD;

        // Резерв золота
        String goldResText = isUkr ? ("Резерв скарбниці: " + CFG.getNumberWthSpaces("" + AutoDevelopManager.minGoldReserve) + " золота") : ("Treasury Reserve: " + CFG.getNumberWthSpaces("" + AutoDevelopManager.minGoldReserve) + " gold");
        Button_Classic goldResLabel = new Button_Classic(goldResText, -1, col1X, leftY, colW, smallBtnH, false);
        menuElements.add(goldResLabel);
        leftY += smallBtnH + CFG.PADD;

        Button_Classic rG0 = new Button_Classic("0", -1, col1X, leftY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 0);
        menuElements.add(rG0);
        this.idResGold0 = menuElements.size() - 1;

        Button_Classic rG1k = new Button_Classic("1 000", -1, col1X + m4W + CFG.PADD, leftY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 1000);
        menuElements.add(rG1k);
        this.idResGold1k = menuElements.size() - 1;

        Button_Classic rG5k = new Button_Classic("5 000", -1, col1X + (m4W + CFG.PADD) * 2, leftY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 5000);
        menuElements.add(rG5k);
        this.idResGold5k = menuElements.size() - 1;

        Button_Classic rG10k = new Button_Classic("10 000", -1, col1X + (m4W + CFG.PADD) * 3, leftY, m4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 10000);
        menuElements.add(rG10k);
        this.idResGold10k = menuElements.size() - 1;
        leftY += smallBtnH + CFG.PADD;

        // ==========================================
        // ПРАВА КОЛОНКА: МОДУЛІ ТА ПАРАМЕТРИ
        // ==========================================
        int rightY = columnsStartY;

        // Заголовок модулів
        String modHeader = isUkr ? "--- МОДУЛІ АВТОМАТИЗАЦІЇ ---" : "--- AUTOMATION MODULES ---";
        Button_Classic modHeadElem = new Button_Classic(modHeader, -1, col2X, rightY, colW, smallBtnH, false);
        menuElements.add(modHeadElem);
        rightY += smallBtnH + CFG.PADD;

        // Модуль 1: Асиміляція
        String assiText = isUkr 
                ? (AutoDevelopManager.autoAssimilate ? "[ВКЛ] Асиміляція провінцій" : "[ВИКЛ] Асиміляція провінцій")
                : (AutoDevelopManager.autoAssimilate ? "[ON] Province Assimilation" : "[OFF] Province Assimilation");
        Button_Classic assiBtn = new Button_Classic(assiText, -1, col2X, rightY, colW, btnH, true, AutoDevelopManager.autoAssimilate);
        menuElements.add(assiBtn);
        this.idToggleAssimilate = menuElements.size() - 1;
        rightY += btnH + CFG.PADD;

        int s3W = (colW - CFG.PADD * 2) / 3;
        Button_Classic stab90 = new Button_Classic(isUkr ? "< 90% стаб." : "< 90% stab.", -1, col2X, rightY, s3W, smallBtnH, true, AutoDevelopManager.targetStability == 90);
        menuElements.add(stab90);
        this.idStab90 = menuElements.size() - 1;

        Button_Classic stab95 = new Button_Classic(isUkr ? "< 95% стаб." : "< 95% stab.", -1, col2X + s3W + CFG.PADD, rightY, s3W, smallBtnH, true, AutoDevelopManager.targetStability == 95);
        menuElements.add(stab95);
        this.idStab95 = menuElements.size() - 1;

        Button_Classic stab100 = new Button_Classic(isUkr ? "100% стаб." : "100% stab.", -1, col2X + (s3W + CFG.PADD) * 2, rightY, s3W, smallBtnH, true, AutoDevelopManager.targetStability == 100);
        menuElements.add(stab100);
        this.idStab100 = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD * 2;

        // Модуль 2: Ярмарки
        String festText = isUkr 
                ? (AutoDevelopManager.autoFestival ? "[ВКЛ] Проведення ярмарок (щастя)" : "[ВИКЛ] Проведення ярмарок (щастя)")
                : (AutoDevelopManager.autoFestival ? "[ON] Organize Festivals (happiness)" : "[OFF] Organize Festivals (happiness)");
        Button_Classic festBtn = new Button_Classic(festText, -1, col2X, rightY, colW, btnH, true, AutoDevelopManager.autoFestival);
        menuElements.add(festBtn);
        this.idToggleFestival = menuElements.size() - 1;
        rightY += btnH + CFG.PADD;

        Button_Classic hap70 = new Button_Classic(isUkr ? "< 70% щастя" : "< 70% hap.", -1, col2X, rightY, s3W, smallBtnH, true, AutoDevelopManager.targetHappiness == 70);
        menuElements.add(hap70);
        this.idHappi70 = menuElements.size() - 1;

        Button_Classic hap80 = new Button_Classic(isUkr ? "< 80% щастя" : "< 80% hap.", -1, col2X + s3W + CFG.PADD, rightY, s3W, smallBtnH, true, AutoDevelopManager.targetHappiness == 80);
        menuElements.add(hap80);
        this.idHappi80 = menuElements.size() - 1;

        Button_Classic hap90 = new Button_Classic(isUkr ? "< 90% щастя" : "< 90% hap.", -1, col2X + (s3W + CFG.PADD) * 2, rightY, s3W, smallBtnH, true, AutoDevelopManager.targetHappiness == 90);
        menuElements.add(hap90);
        this.idHappi90 = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD * 2;

        // Модуль 3: Економіка
        String ecoText = isUkr 
                ? (AutoDevelopManager.autoInvestEconomy ? "[ВКЛ] Розвиток економіки" : "[ВИКЛ] Розвиток економіки")
                : (AutoDevelopManager.autoInvestEconomy ? "[ON] Economy Investments" : "[OFF] Economy Investments");
        Button_Classic ecoBtn = new Button_Classic(ecoText, -1, col2X, rightY, colW, btnH, true, AutoDevelopManager.autoInvestEconomy);
        menuElements.add(ecoBtn);
        this.idToggleEconomy = menuElements.size() - 1;
        rightY += btnH + CFG.PADD;

        int samplePop = 100000;
        int sampleTarget = (int)((long)samplePop * AutoDevelopManager.targetEcoPercentage / 100L);
        String percText = isUkr
                ? ("Ціль: " + AutoDevelopManager.targetEcoPercentage + "% населення (100к нас. -> " + (sampleTarget / 1000) + "к екон.)")
                : ("Target: " + AutoDevelopManager.targetEcoPercentage + "% pop (100k pop -> " + (sampleTarget / 1000) + "k eco)");
        Button_Classic percInfo = new Button_Classic(percText, -1, col2X, rightY, colW, smallBtnH, false);
        menuElements.add(percInfo);
        this.idEcoPercLabel = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD;

        // Кроки +/-
        int bW4 = (colW - CFG.PADD * 3) / 4;
        Button_Classic btnM50 = new Button_Classic("-50%", -1, col2X, rightY, bW4, smallBtnH, true);
        menuElements.add(btnM50);
        this.idEcoM50 = menuElements.size() - 1;

        Button_Classic btnM10 = new Button_Classic("-10%", -1, col2X + bW4 + CFG.PADD, rightY, bW4, smallBtnH, true);
        menuElements.add(btnM10);
        this.idEcoM10 = menuElements.size() - 1;

        Button_Classic btnP10 = new Button_Classic("+10%", -1, col2X + (bW4 + CFG.PADD) * 2, rightY, bW4, smallBtnH, true);
        menuElements.add(btnP10);
        this.idEcoP10 = menuElements.size() - 1;

        Button_Classic btnP50 = new Button_Classic("+50%", -1, col2X + (bW4 + CFG.PADD) * 3, rightY, bW4, smallBtnH, true);
        menuElements.add(btnP50);
        this.idEcoP50 = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD;

        // Пресети цільової економіки
        int bW5 = (colW - CFG.PADD * 4) / 5;
        Button_Classic p80 = new Button_Classic("80%", -1, col2X, rightY, bW5, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 80);
        menuElements.add(p80);
        this.idEco80 = menuElements.size() - 1;

        Button_Classic p100 = new Button_Classic("100%", -1, col2X + bW5 + CFG.PADD, rightY, bW5, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 100);
        menuElements.add(p100);
        this.idEco100 = menuElements.size() - 1;

        Button_Classic p120 = new Button_Classic("120%", -1, col2X + (bW5 + CFG.PADD) * 2, rightY, bW5, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 120);
        menuElements.add(p120);
        this.idEco120 = menuElements.size() - 1;

        Button_Classic p150 = new Button_Classic("150%", -1, col2X + (bW5 + CFG.PADD) * 3, rightY, bW5, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 150);
        menuElements.add(p150);
        this.idEco150 = menuElements.size() - 1;

        Button_Classic p200 = new Button_Classic("200%", -1, col2X + (bW5 + CFG.PADD) * 4, rightY, bW5, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 200);
        menuElements.add(p200);
        this.idEco200 = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD * 2;

        // Модуль 4: Дослідження
        String devText = isUkr 
                ? (AutoDevelopManager.autoInvestDevelopment ? "[ВКЛ] Дослідження (до рівня держави)" : "[ВИКЛ] Дослідження (до рівня держави)")
                : (AutoDevelopManager.autoInvestDevelopment ? "[ON] Research Dev (up to Civ Tech Level)" : "[OFF] Research Dev (up to Civ Tech Level)");
        Button_Classic devBtn = new Button_Classic(devText, -1, col2X, rightY, colW, btnH, true, AutoDevelopManager.autoInvestDevelopment);
        menuElements.add(devBtn);
        this.idToggleDevelopment = menuElements.size() - 1;
        rightY += btnH + CFG.PADD;

        // Сповіщення
        String notifText = isUkr 
                ? (AutoDevelopManager.showNotifications ? "[ВКЛ] Сповіщення про виконані дії" : "[ВИКЛ] Сповіщення про виконані дії")
                : (AutoDevelopManager.showNotifications ? "[ON] Action Notifications" : "[OFF] Action Notifications");
        Button_Classic notifBtn = new Button_Classic(notifText, -1, col2X, rightY, colW, smallBtnH, true, AutoDevelopManager.showNotifications);
        menuElements.add(notifBtn);
        this.idToggleNotifications = menuElements.size() - 1;
        rightY += smallBtnH + CFG.PADD;

        // ==========================================
        // НИЖНЯ ПАНЕЛЬ: ДІЇ (ЗАПУСК ТА ЗАКРИТТЯ)
        // ==========================================
        int bottomY = Math.max(leftY, rightY) + CFG.PADD * 2;

        int actionBtnH = btnH * 11 / 10;
        String runText = isUkr ? "ЗАПУСТИТИ ЗАРАЗ (НА ПОТОЧНОМУ ХОДІ)" : "RUN AUTOMATION NOW (THIS TURN)";
        Button_Classic runBtn = new Button_Classic(runText, -1, startX, bottomY, menuW, actionBtnH, true);
        menuElements.add(runBtn);
        this.idRunNow = menuElements.size() - 1;
        bottomY += actionBtnH + CFG.PADD;

        String closeTextBottom = isUkr ? "Повернутися до гри" : "Return to Game";
        Button_Classic closeBtnBottom = new Button_Classic(closeTextBottom, -1, startX, bottomY, menuW, btnH, true);
        menuElements.add(closeBtnBottom);
        this.idCloseBottom = menuElements.size() - 1;
        bottomY += btnH + CFG.PADD * 2;

        // Поглинач кліків поза діалогом
        this.idCatchAll = menuElements.size();
        menuElements.add(new Button_Transparent(0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, true));

        this.initMenu(null, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, menuElements);
        super.setVisibleM(false);
    }

    @Override
    public final void actionEL(int iID) {
        if (iID == this.idCloseTop || iID == this.idCloseBottom) {
            close();
            return;
        }

        if (iID == this.idToggleMaster) {
            AutoDevelopManager.autoDevelopEnabled = !AutoDevelopManager.autoDevelopEnabled;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Пріоритети: зміна порядку
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
                    AutoDevelopManager.PRIO_DEVELOPMENT };
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idPrioPresetEconomy) {
            AutoDevelopManager.priorities = new int[]{ 
                    AutoDevelopManager.PRIO_ECONOMY, 
                    AutoDevelopManager.PRIO_DEVELOPMENT, 
                    AutoDevelopManager.PRIO_ASSIMILATE, 
                    AutoDevelopManager.PRIO_FESTIVAL };
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idPrioPresetStability) {
            AutoDevelopManager.priorities = new int[]{ 
                    AutoDevelopManager.PRIO_ASSIMILATE, 
                    AutoDevelopManager.PRIO_FESTIVAL, 
                    AutoDevelopManager.PRIO_DEVELOPMENT, 
                    AutoDevelopManager.PRIO_ECONOMY };
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Резерв очок ходів
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

        // Резерв золота
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

        // Модуль 1: Асиміляція
        if (iID == this.idToggleAssimilate) {
            AutoDevelopManager.autoAssimilate = !AutoDevelopManager.autoAssimilate;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idStab90) {
            AutoDevelopManager.targetStability = 90;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idStab95) {
            AutoDevelopManager.targetStability = 95;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idStab100) {
            AutoDevelopManager.targetStability = 100;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Модуль 2: Ярмарки
        if (iID == this.idToggleFestival) {
            AutoDevelopManager.autoFestival = !AutoDevelopManager.autoFestival;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idHappi70) {
            AutoDevelopManager.targetHappiness = 70;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idHappi80) {
            AutoDevelopManager.targetHappiness = 80;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idHappi90) {
            AutoDevelopManager.targetHappiness = 90;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Модуль 3: Економіка
        if (iID == this.idToggleEconomy) {
            AutoDevelopManager.autoInvestEconomy = !AutoDevelopManager.autoInvestEconomy;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEcoM50) {
            AutoDevelopManager.targetEcoPercentage = Math.max(10, AutoDevelopManager.targetEcoPercentage - 50);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEcoM10) {
            AutoDevelopManager.targetEcoPercentage = Math.max(10, AutoDevelopManager.targetEcoPercentage - 10);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEcoP10) {
            AutoDevelopManager.targetEcoPercentage = Math.min(1000, AutoDevelopManager.targetEcoPercentage + 10);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }
        if (iID == this.idEcoP50) {
            AutoDevelopManager.targetEcoPercentage = Math.min(1000, AutoDevelopManager.targetEcoPercentage + 50);
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
        if (iID == this.idEco200) {
            AutoDevelopManager.targetEcoPercentage = 200;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        // Модуль 4: Дослідження
        if (iID == this.idToggleDevelopment) {
            AutoDevelopManager.autoInvestDevelopment = !AutoDevelopManager.autoInvestDevelopment;
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

        // ДІЯ: ЗАПУСК ЗАРАЗ
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
        // Повноекранне затемнення
        oSB.setColor(new Color(0.04f, 0.06f, 0.09f, 0.95f));
        IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);

        // Градієнти зверху та знизу
        oSB.setColor(new Color(CFG.COLOR_GRADIENT_DARK_BLUE.r, CFG.COLOR_GRADIENT_DARK_BLUE.g, CFG.COLOR_GRADIENT_DARK_BLUE.b, 0.6f));
        IMGManager.getIMG(Images.gradient).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.BUTTON_H * 2);
        IMGManager.getIMG(Images.gradient).draw(oSB, 0, CFG.GAMEHEIGHT - CFG.BUTTON_H * 2, CFG.GAMEWIDTH, CFG.BUTTON_H * 2, false, true);

        // Центральна карточка діалогу
        int menuW = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 2.3f), CFG.GAMEWIDTH - CFG.PADD * 4);
        int startX = (CFG.GAMEWIDTH - menuW) / 2;
        int colGap = CFG.PADD * 2;
        int colW = (menuW - colGap) / 2;
        int col1X = startX;
        int col2X = startX + colW + colGap;

        // Підкладка діалогу
        oSB.setColor(new Color(0.08f, 0.11f, 0.16f, 0.88f));
        IMGManager.getIMG(Images.pix255).draw(oSB, startX - CFG.PADD, CFG.PADD, menuW + CFG.PADD * 2, CFG.GAMEHEIGHT - CFG.PADD * 2);

        // Рамка діалогу
        oSB.setColor(new Color(CFG.COLOR_FLAG_FRAME.r, CFG.COLOR_FLAG_FRAME.g, CFG.COLOR_FLAG_FRAME.b, 0.6f));
        IMGManager.getIMG(Images.pix255).draw(oSB, startX - CFG.PADD, CFG.PADD, menuW + CFG.PADD * 2, 1);
        IMGManager.getIMG(Images.pix255).draw(oSB, startX - CFG.PADD, CFG.GAMEHEIGHT - CFG.PADD - 1, menuW + CFG.PADD * 2, 1);
        IMGManager.getIMG(Images.pix255).draw(oSB, startX - CFG.PADD, CFG.PADD, 1, CFG.GAMEHEIGHT - CFG.PADD * 2);
        IMGManager.getIMG(Images.pix255).draw(oSB, startX + menuW + CFG.PADD, CFG.PADD, 1, CFG.GAMEHEIGHT - CFG.PADD * 2);

        // Підкладки під ліву та праву колонки
        oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.28f));
        IMGManager.getIMG(Images.pix255).draw(oSB, col1X - CFG.PADD / 2, CFG.BUTTON_H * 5 / 2, colW + CFG.PADD, CFG.GAMEHEIGHT - CFG.BUTTON_H * 5);
        IMGManager.getIMG(Images.pix255).draw(oSB, col2X - CFG.PADD / 2, CFG.BUTTON_H * 5 / 2, colW + CFG.PADD, CFG.GAMEHEIGHT - CFG.BUTTON_H * 5);

        oSB.setColor(Color.WHITE);

        this.beginClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.drawMenuM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.endClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }
}
