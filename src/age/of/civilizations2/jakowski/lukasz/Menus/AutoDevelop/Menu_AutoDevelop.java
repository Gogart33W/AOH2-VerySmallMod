package age.of.civilizations2.jakowski.lukasz.Menus.AutoDevelop;

import age.of.civilizations2.jakowski.lukasz.Button.Button_Transparent;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.IMGManager;
import age.of.civilizations2.jakowski.lukasz.Images;
import age.of.civilizations2.jakowski.lukasz.Menu;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextTitleStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.List;

/**
 * Menu_AutoDevelop - повноекранне вікно налаштування та запуску автоматичного розвитку провінцій.
 */
public class Menu_AutoDevelop extends Menu {
    private static int autoDevelopMenuID = -1;
    private static Menu_AutoDevelop instance = null;

    private int idTitle = -1;
    private int idCloseTop = -1;
    private int idToggleMaster = -1;
    private int idToggleEco = -1;
    private int idEcoPercLabel = -1;
    private int idEcoPercMinus50 = -1;
    private int idEcoPercMinus10 = -1;
    private int idEcoPercPlus10 = -1;
    private int idEcoPercPlus50 = -1;
    private int idEcoPreset80 = -1;
    private int idEcoPreset100 = -1;
    private int idEcoPreset120 = -1;
    private int idEcoPreset150 = -1;
    private int idEcoPreset200 = -1;
    private int idToggleDev = -1;
    private int idToggleNotify = -1;
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
        close(true);
    }

    public static void close(boolean reopenOptions) {
        if (instance != null) {
            instance.setVisibleM(false);
        }
        if (reopenOptions) {
            CFG.menus.setVisible_InGame_Options(true);
        }
    }

    public void rebuild() {
        List<MenuElemUI> menuElements = new ArrayList<MenuElemUI>();

        int btnW = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 1.8f), CFG.GAMEWIDTH - CFG.PADD * 6);
        int btnX = CFG.GAMEWIDTH / 2 - btnW / 2;
        int btnH = CFG.BUTTON_H;
        int titleH = CFG.BUTTON_H * 4 / 5;
        int curY = CFG.PADD * 3;

        boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");

        // 1. Заголовок
        String titleText = isUkr ? "АВТОМАТИЧНИЙ РОЗВИТОК ПРОВІНЦІЙ" : "AUTOMATIC PROVINCE DEVELOPMENT";
        TextTitleStyle titleElem = new TextTitleStyle(titleText, -1, btnX, curY, btnW, titleH);
        menuElements.add(titleElem);
        this.idTitle = menuElements.size() - 1;
        curY += titleH + CFG.PADD * 2;

        // 2. Кнопка закриття зверху
        String closeTextTop = isUkr ? "✕ Повернутися до гри (Закрити)" : "✕ Return to Game (Close)";
        Button_Classic closeBtnTop = new Button_Classic(closeTextTop, -1, btnX, curY, btnW, btnH, true);
        menuElements.add(closeBtnTop);
        this.idCloseTop = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        // 3. Головна галочка: Увімкнути/Вимкнути авто-розвиток
        String masterText = isUkr 
                ? (AutoDevelopManager.autoDevelopEnabled ? "[✓] Авто-розвиток: УВІМКНЕНО (щоходу)" : "[  ] Авто-розвиток: ВИМКНЕНО")
                : (AutoDevelopManager.autoDevelopEnabled ? "[✓] Auto-Develop: ENABLED (every turn)" : "[  ] Auto-Develop: DISABLED");
        Button_Classic masterBtn = new Button_Classic(masterText, -1, btnX, curY, btnW, btnH, true, AutoDevelopManager.autoDevelopEnabled);
        menuElements.add(masterBtn);
        this.idToggleMaster = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        // 4. Галочка: Авто-інвестиції в економіку
        String ecoText = isUkr
                ? (AutoDevelopManager.autoInvestEconomy ? "[✓] Авто-інвестиції в економіку" : "[  ] Авто-інвестиції в економіку")
                : (AutoDevelopManager.autoInvestEconomy ? "[✓] Auto-Invest in Economy" : "[  ] Auto-Invest in Economy");
        Button_Classic ecoBtn = new Button_Classic(ecoText, -1, btnX, curY, btnW, btnH, true, AutoDevelopManager.autoInvestEconomy);
        menuElements.add(ecoBtn);
        this.idToggleEco = menuElements.size() - 1;
        curY += btnH + CFG.PADD;

        // 5. Індикатор цільової економіки (% від населення)
        int samplePop = 100000;
        int sampleTarget = (int)((long)samplePop * AutoDevelopManager.targetEcoPercentage / 100L);
        String percText = isUkr
                ? ("Цільова економіка: " + AutoDevelopManager.targetEcoPercentage + "% населення (100к нас. -> " + (sampleTarget / 1000) + "к екон.)")
                : ("Target Economy: " + AutoDevelopManager.targetEcoPercentage + "% of pop (100k pop -> " + (sampleTarget / 1000) + "k eco)");
        Button_Classic percInfo = new Button_Classic(percText, -1, btnX, curY, btnW, btnH * 4 / 5, false);
        menuElements.add(percInfo);
        this.idEcoPercLabel = menuElements.size() - 1;
        curY += btnH * 4 / 5 + CFG.PADD;

        // 6. Рядок регулювання цільового % (-50%, -10%, +10%, +50%)
        int gap = CFG.PADD;
        int bW4 = (btnW - gap * 3) / 4;
        
        Button_Classic btnM50 = new Button_Classic("-50%", -1, btnX, curY, bW4, btnH, true);
        menuElements.add(btnM50);
        this.idEcoPercMinus50 = menuElements.size() - 1;

        Button_Classic btnM10 = new Button_Classic("-10%", -1, btnX + (bW4 + gap), curY, bW4, btnH, true);
        menuElements.add(btnM10);
        this.idEcoPercMinus10 = menuElements.size() - 1;

        Button_Classic btnP10 = new Button_Classic("+10%", -1, btnX + (bW4 + gap) * 2, curY, bW4, btnH, true);
        menuElements.add(btnP10);
        this.idEcoPercPlus10 = menuElements.size() - 1;

        Button_Classic btnP50 = new Button_Classic("+50%", -1, btnX + (bW4 + gap) * 3, curY, bW4, btnH, true);
        menuElements.add(btnP50);
        this.idEcoPercPlus50 = menuElements.size() - 1;
        curY += btnH + CFG.PADD;

        // 7. Рядок готових пресетів (80%, 100%, 120%, 150%, 200%)
        int bW5 = (btnW - gap * 4) / 5;
        
        Button_Classic p80 = new Button_Classic("80%", -1, btnX, curY, bW5, btnH * 3 / 4, true);
        menuElements.add(p80);
        this.idEcoPreset80 = menuElements.size() - 1;

        Button_Classic p100 = new Button_Classic("100%", -1, btnX + (bW5 + gap), curY, bW5, btnH * 3 / 4, true);
        menuElements.add(p100);
        this.idEcoPreset100 = menuElements.size() - 1;

        Button_Classic p120 = new Button_Classic("120%", -1, btnX + (bW5 + gap) * 2, curY, bW5, btnH * 3 / 4, true);
        menuElements.add(p120);
        this.idEcoPreset120 = menuElements.size() - 1;

        Button_Classic p150 = new Button_Classic("150%", -1, btnX + (bW5 + gap) * 3, curY, bW5, btnH * 3 / 4, true);
        menuElements.add(p150);
        this.idEcoPreset150 = menuElements.size() - 1;

        Button_Classic p200 = new Button_Classic("200%", -1, btnX + (bW5 + gap) * 4, curY, bW5, btnH * 3 / 4, true);
        menuElements.add(p200);
        this.idEcoPreset200 = menuElements.size() - 1;
        curY += btnH * 3 / 4 + CFG.PADD * 2;

        // 8. Галочка: Авто-розвиток рівня досліджень / технологій у провінціях
        String devText = isUkr
                ? (AutoDevelopManager.autoInvestDevelopment ? "[✓] Авто-розвиток досліджень (до рівня технологій)" : "[  ] Авто-розвиток досліджень (до рівня технологій)")
                : (AutoDevelopManager.autoInvestDevelopment ? "[✓] Auto-Develop Research (up to Civ Tech Level)" : "[  ] Auto-Develop Research (up to Civ Tech Level)");
        Button_Classic devBtn = new Button_Classic(devText, -1, btnX, curY, btnW, btnH, true, AutoDevelopManager.autoInvestDevelopment);
        menuElements.add(devBtn);
        this.idToggleDev = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        // 9. Галочка: Сповіщення
        String notifyText = isUkr
                ? (AutoDevelopManager.showNotifications ? "[✓] Сповіщення про виконані інвестиції" : "[  ] Сповіщення про виконані інвестиції")
                : (AutoDevelopManager.showNotifications ? "[✓] Show investment notifications" : "[  ] Show investment notifications");
        Button_Classic notifyBtn = new Button_Classic(notifyText, -1, btnX, curY, btnW, btnH * 4 / 5, true, AutoDevelopManager.showNotifications);
        menuElements.add(notifyBtn);
        this.idToggleNotify = menuElements.size() - 1;
        curY += btnH * 4 / 5 + CFG.PADD * 2;

        // 10. Кнопка негайного запуску на цьому ході
        String runText = isUkr ? "⚡ Інвестувати зараз (на поточному ході)" : "⚡ Run Auto-Develop Now (this turn)";
        Button_Classic runBtn = new Button_Classic(runText, -1, btnX, curY, btnW, btnH, true);
        menuElements.add(runBtn);
        this.idRunNow = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        // 11. Кнопка закриття знизу
        String closeTextBottom = isUkr ? "✕ Закрити вікно" : "✕ Close Window";
        Button_Classic closeBtnBottom = new Button_Classic(closeTextBottom, -1, btnX, curY, btnW, btnH, true);
        menuElements.add(closeBtnBottom);
        this.idCloseBottom = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        // 12. Повноекранний прозорий клікабельний фон для поглинання кліків
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

        if (iID == this.idToggleEco) {
            AutoDevelopManager.autoInvestEconomy = !AutoDevelopManager.autoInvestEconomy;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPercMinus50) {
            AutoDevelopManager.targetEcoPercentage = Math.max(10, AutoDevelopManager.targetEcoPercentage - 50);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPercMinus10) {
            AutoDevelopManager.targetEcoPercentage = Math.max(10, AutoDevelopManager.targetEcoPercentage - 10);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPercPlus10) {
            AutoDevelopManager.targetEcoPercentage = Math.min(1000, AutoDevelopManager.targetEcoPercentage + 10);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPercPlus50) {
            AutoDevelopManager.targetEcoPercentage = Math.min(1000, AutoDevelopManager.targetEcoPercentage + 50);
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPreset80) {
            AutoDevelopManager.targetEcoPercentage = 80;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPreset100) {
            AutoDevelopManager.targetEcoPercentage = 100;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPreset120) {
            AutoDevelopManager.targetEcoPercentage = 120;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPreset150) {
            AutoDevelopManager.targetEcoPercentage = 150;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idEcoPreset200) {
            AutoDevelopManager.targetEcoPercentage = 200;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idToggleDev) {
            AutoDevelopManager.autoInvestDevelopment = !AutoDevelopManager.autoInvestDevelopment;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idToggleNotify) {
            AutoDevelopManager.showNotifications = !AutoDevelopManager.showNotifications;
            AutoDevelopManager.saveConfig();
            this.rebuild();
            return;
        }

        if (iID == this.idRunNow) {
            int playerCivID = -1;
            try {
                if (CFG.core != null && CFG.core.getPlayer(CFG.PLAYER_TURN_ID) != null) {
                    playerCivID = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
                }
            } catch (Exception ignored) {
            }

            int count = (playerCivID > 0) ? AutoDevelopManager.developForCiv(playerCivID, false) : 0;
            boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
            String msg = isUkr 
                    ? ("Авто-розвиток: вкладено в " + count + " провінцій!") 
                    : ("Auto-Develop: invested in " + count + " provinces!");
            CFG.toastM.addM(msg, count > 0 ? CFG.COLOR_POSITIVE : CFG.COLOR_GOLD);
            CFG.toastM.setTimeInView(3500);
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
        // Повноекранне темне тло
        oSB.setColor(new Color(0.06f, 0.08f, 0.11f, 0.97f));
        IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);

        // Градієнти зверху та знизу
        oSB.setColor(new Color(CFG.COLOR_GRADIENT_DARK_BLUE.r, CFG.COLOR_GRADIENT_DARK_BLUE.g, CFG.COLOR_GRADIENT_DARK_BLUE.b, 0.5f));
        IMGManager.getIMG(Images.gradient).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.BUTTON_H * 2);
        IMGManager.getIMG(Images.gradient).draw(oSB, 0, CFG.GAMEHEIGHT - CFG.BUTTON_H * 2, CFG.GAMEWIDTH, CFG.BUTTON_H * 2, false, true);

        // Центральна підкладка
        int panelW = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 1.9f), CFG.GAMEWIDTH - CFG.PADD * 2);
        int panelX = CFG.GAMEWIDTH / 2 - panelW / 2;
        oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.40f));
        IMGManager.getIMG(Images.pix255).draw(oSB, panelX, 0, panelW, CFG.GAMEHEIGHT);
        oSB.setColor(Color.WHITE);

        this.beginClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.drawMenuM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.endClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }
}
