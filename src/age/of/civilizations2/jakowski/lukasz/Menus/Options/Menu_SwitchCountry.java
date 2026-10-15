package age.of.civilizations2.jakowski.lukasz.Menus.Options;

import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic_Classic_Search;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic_CivilizationAndFlag;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.IMGManager;
import age.of.civilizations2.jakowski.lukasz.Images;
import age.of.civilizations2.jakowski.lukasz.Menu;
import age.of.civilizations2.jakowski.lukasz.Player;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextTitleStyle;
import age.of.civilizations2.jakowski.lukasz.Menus.Vassal.VassalManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Menu_SwitchCountry - повноекранне меню зміни поточної цивілізації гравця.
 * Займає весь екран (0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT), що унеможливлює
 * будь-які випадкові натискання на карту, панелі дій чи сторонні підказки під меню.
 * Містить рядок пошуку цивілізацій за назвою або тегом.
 */
public class Menu_SwitchCountry extends Menu {
    private static int switchCountryMenuID = -1;
    private static Menu_SwitchCountry instance = null;
    private static String searchQuery = "";

    private List<Integer> lCivIDs = new ArrayList<Integer>();
    private List<Integer> lCivBtnIDs = new ArrayList<Integer>();

    private int idCloseBtnTop = -1;
    private int idCloseBtnBottom = -1;
    private int idSearchBtn = -1;
    private int idClearSearchBtn = -1;
    private int idActiveProvBtn = -1;
    private int activeProvCivID = -1;
    private int idCatchAll = -1;

    public Menu_SwitchCountry() {
        this.rebuild();
    }

    public static void open() {
        try {
            VassalManager.syncAllVassals();
            if (instance == null || switchCountryMenuID < 0) {
                instance = new Menu_SwitchCountry();
                switchCountryMenuID = CFG.menus.addNewMenuToViewID(CFG.menus.IN_GAME_MENU, instance);
            } else {
                instance.rebuild();
            }

            instance.setVisibleM(true);
            CFG.menus.setOrderOfMenuID(switchCountryMenuID);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    public static void close() {
        close(true);
    }

    public static void close(boolean reopenOptions) {
        searchQuery = "";
        if (instance != null) {
            instance.setVisibleM(false);
        }
        if (reopenOptions) {
            CFG.menus.setVisible_InGame_Options(true);
        }
    }

    public void rebuild() {
        this.lCivIDs.clear();
        this.lCivBtnIDs.clear();
        List<MenuElemUI> menuElements = new ArrayList<MenuElemUI>();

        int btnW = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 1.8f), CFG.GAMEWIDTH - CFG.PADD * 6);
        int btnX = CFG.GAMEWIDTH / 2 - btnW / 2;
        int btnH = CFG.BUTTON_H;
        int titleH = CFG.BUTTON_H * 4 / 5;
        int curY = CFG.PADD * 3;

        boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");

        // 1. Заголовок
        String titleText = isUkr ? "ЗМІНА ДЕРЖАВИ" : "SWITCH CIVILIZATION";
        TextTitleStyle titleElem = new TextTitleStyle(titleText, -1, btnX, curY, btnW, titleH);
        menuElements.add(titleElem);
        curY += titleH + CFG.PADD * 2;

        // 2. Головна кнопка закриття меню зверху
        String closeTextTop = isUkr ? "✕ Повернутися до гри (Закрити)" : "✕ Return to Game (Close)";
        Button_Classic closeBtnTop = new Button_Classic(closeTextTop, -1, btnX, curY, btnW, btnH, true);
        menuElements.add(closeBtnTop);
        this.idCloseBtnTop = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        // 3. Рядок пошуку
        boolean hasFilter = !searchQuery.isEmpty();
        int clearBtnW = hasFilter ? (btnH * 3) : 0;
        int searchBtnW = hasFilter ? (btnW - clearBtnW - CFG.PADD) : btnW;

        String searchLabel = hasFilter 
                ? ((isUkr ? "🔍 Фільтр: \"" : "🔍 Filter: \"") + searchQuery + "\"")
                : (isUkr ? "🔍 Пошук держави... (Натисніть тут)" : "🔍 Search Civilization... (Click here)");

        Button_Classic_Classic_Search searchBtn = new Button_Classic_Classic_Search(
                searchLabel,
                -1,
                btnX,
                curY,
                searchBtnW,
                btnH,
                true
        );
        menuElements.add(searchBtn);
        this.idSearchBtn = menuElements.size() - 1;

        this.idClearSearchBtn = -1;
        if (hasFilter) {
            Button_Classic clearBtn = new Button_Classic(
                    isUkr ? "✕ Скинути" : "✕ Clear",
                    -1,
                    btnX + searchBtnW + CFG.PADD,
                    curY,
                    clearBtnW,
                    btnH,
                    true
            );
            menuElements.add(clearBtn);
            this.idClearSearchBtn = menuElements.size() - 1;
        }
        curY += btnH + CFG.PADD * 2;

        // 4. Кнопка швидкого переходу на країну обраної провінції
        this.idActiveProvBtn = -1;
        this.activeProvCivID = -1;
        if (CFG.core.getActiveProvID() >= 0 && !hasFilter) {
            int provCiv = CFG.core.getProv(CFG.core.getActiveProvID()).getCivId();
            int curPlayerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
            if (provCiv > 0 && provCiv != curPlayerCiv && provCiv < CFG.core.getCivsSize()
                    && CFG.core.getCiv(provCiv).getNumOfProvs() > 0) {
                this.activeProvCivID = provCiv;
                String provCivName = CFG.core.getCiv(provCiv).getCivName();
                String shortcutText = (isUkr ? "⚡ Обрана на карті провінція: " : "⚡ Selected province: ") + provCivName;
                Button_Classic_CivilizationAndFlag provBtn = new Button_Classic_CivilizationAndFlag(
                        provCiv,
                        CFG.CIV_FLAG_WIDTH + CFG.PADD * 3,
                        btnX,
                        curY,
                        btnW,
                        btnH,
                        true
                );
                provBtn.setTextE(shortcutText);
                menuElements.add(provBtn);
                this.idActiveProvBtn = menuElements.size() - 1;
                curY += btnH + CFG.PADD * 2;
            }
        }

        // 5. Список усіх живих держав (із застосуванням фільтра пошуку)
        int curPlayerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
        List<Integer> aliveCivs = new ArrayList<Integer>();
        String queryLower = searchQuery.toLowerCase().trim();

        for (int i = 1; i < CFG.core.getCivsSize(); i++) {
            if (CFG.core.getCiv(i).getNumOfProvs() > 0) {
                if (queryLower.isEmpty()) {
                    aliveCivs.add(i);
                } else {
                    String cName = CFG.core.getCiv(i).getCivName().toLowerCase();
                    String cTag = CFG.core.getCiv(i).getCivTag().toLowerCase();
                    if (cName.contains(queryLower) || cTag.contains(queryLower)) {
                        aliveCivs.add(i);
                    }
                }
            }
        }

        Collections.sort(aliveCivs, new Comparator<Integer>() {
            @Override
            public int compare(Integer c1, Integer c2) {
                if (c1 == curPlayerCiv) return -1;
                if (c2 == curPlayerCiv) return 1;
                return Integer.compare(
                        CFG.core.getCiv(c2).getNumOfProvs(),
                        CFG.core.getCiv(c1).getNumOfProvs()
                );
            }
        });

        if (aliveCivs.isEmpty() && hasFilter) {
            String notFoundText = isUkr 
                    ? ("За запитом \"" + searchQuery + "\" нічого не знайдено")
                    : ("No civilizations found for \"" + searchQuery + "\"");
            TextTitleStyle notFoundElem = new TextTitleStyle(notFoundText, -1, btnX, curY, btnW, btnH);
            menuElements.add(notFoundElem);
            curY += btnH + CFG.PADD * 2;
        }

        for (int i = 0; i < aliveCivs.size(); i++) {
            int civID = aliveCivs.get(i);
            String civName = CFG.core.getCiv(civID).getCivName();
            int provCount = CFG.core.getCiv(civID).getNumOfProvs();
            boolean isCurrent = (civID == curPlayerCiv);

            String label = (isCurrent ? (isUkr ? "★ [ВАША ПОТОЧНА КРАЇНА] " : "★ [CURRENT] ") : "")
                    + civName + " (" + provCount + (isUkr ? " пров.)" : " prov.)");

            Button_Classic_CivilizationAndFlag civBtn = new Button_Classic_CivilizationAndFlag(
                    civID,
                    CFG.CIV_FLAG_WIDTH + CFG.PADD * 3,
                    btnX,
                    curY,
                    btnW,
                    btnH,
                    !isCurrent
            );
            civBtn.setTextE(label);
            menuElements.add(civBtn);
            this.lCivIDs.add(civID);
            this.lCivBtnIDs.add(menuElements.size() - 1);
            curY += btnH + CFG.PADD;
        }

        // 6. Нижня кнопка закриття
        String closeTextBottom = isUkr ? "✕ Повернутися до гри (Закрити)" : "✕ Return to Game (Close)";
        Button_Classic closeBtnBottom = new Button_Classic(closeTextBottom, -1, btnX, curY, btnW, btnH, true);
        menuElements.add(closeBtnBottom);
        this.idCloseBtnBottom = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 4;

        // 7. Повноекранний елемент перехоплення для блокування будь-яких кліків та наведень курсору
        int totalHeight = Math.max(CFG.GAMEHEIGHT * 2, curY + CFG.GAMEHEIGHT);
        MenuElemUI catchAll = new MenuElemUI() {
            @Override
            public void drawE(SpriteBatch oSB, int iTranslateX, int iTranslateY) {}
            @Override
            public void drawE(SpriteBatch oSB, int iTranslateX, int iTranslateY, boolean isActive, boolean scrollableY) {}
            @Override
            public void actionElem(int iID) {}
            @Override
            public boolean getIsClickable() { return true; }
        };
        catchAll.typeOfMenuElemUI = MenuElemUI.TypeOfMenuElemUI.BUTTON;
        catchAll.setPosX(0);
        catchAll.setPosY(0);
        catchAll.setWidthE(CFG.GAMEWIDTH);
        catchAll.setHeightE(totalHeight);
        menuElements.add(catchAll);
        this.idCatchAll = menuElements.size() - 1;

        // Повноекранна ініціалізація з вертикальним скролінгом
        this.initMenu(null, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, menuElements, true, true, false);
    }

    @Override
    public void actionEL(int iID) {
        if (iID == this.idCloseBtnTop || iID == this.idCloseBtnBottom) {
            close();
            return;
        }

        if (iID == this.idSearchBtn) {
            boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
            com.badlogic.gdx.Gdx.input.getTextInput(new com.badlogic.gdx.Input.TextInputListener() {
                @Override
                public void input(String text) {
                    searchQuery = (text != null) ? text.trim() : "";
                    rebuild();
                }

                @Override
                public void canceled() {
                }
            }, isUkr ? "Пошук країни" : "Search Civilization", searchQuery, isUkr ? "Введіть назву країни..." : "Enter civilization name...");
            return;
        }

        if (this.idClearSearchBtn >= 0 && iID == this.idClearSearchBtn) {
            searchQuery = "";
            this.rebuild();
            return;
        }

        if (this.idActiveProvBtn >= 0 && iID == this.idActiveProvBtn) {
            if (this.activeProvCivID > 0) {
                switchToCiv(this.activeProvCivID);
            }
            close(false);
            return;
        }

        if (iID == this.idCatchAll) {
            // Клік по фону меню поглинається
            return;
        }

        int civBtnIndex = this.lCivBtnIDs.indexOf(iID);
        if (civBtnIndex >= 0 && civBtnIndex < this.lCivIDs.size()) {
            int targetCiv = this.lCivIDs.get(civBtnIndex);
            switchToCiv(targetCiv);
            close(false);
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
        // 1. Повноекранне темне тло
        oSB.setColor(new Color(0.06f, 0.08f, 0.11f, 0.97f));
        IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);

        // 2. Декоративні градієнти зверху та знизу
        oSB.setColor(new Color(CFG.COLOR_GRADIENT_DARK_BLUE.r, CFG.COLOR_GRADIENT_DARK_BLUE.g, CFG.COLOR_GRADIENT_DARK_BLUE.b, 0.5f));
        IMGManager.getIMG(Images.gradient).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.BUTTON_H * 2);
        IMGManager.getIMG(Images.gradient).draw(oSB, 0, CFG.GAMEHEIGHT - CFG.BUTTON_H * 2, CFG.GAMEWIDTH, CFG.BUTTON_H * 2, false, true);

        // 3. Центральна панель підкладки під список
        int panelW = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 1.9f), CFG.GAMEWIDTH - CFG.PADD * 2);
        int panelX = CFG.GAMEWIDTH / 2 - panelW / 2;
        oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.35f));
        IMGManager.getIMG(Images.pix255).draw(oSB, panelX, 0, panelW, CFG.GAMEHEIGHT);
        oSB.setColor(Color.WHITE);

        // 4. Відмальовка елементів списку зі скролінгом
        super.draw(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }

    /**
     * Логіка зміни поточної держави гравця.
     */
    public static void switchToCiv(int newCivID) {
        try {
            if (newCivID <= 0 || newCivID >= CFG.core.getCivsSize()) {
                return;
            }
            if (CFG.core.getCiv(newCivID).getNumOfProvs() <= 0) {
                CFG.toastM.addM("Цивілізація не має провінцій!", CFG.COLOR_NEGATIVE_2);
                return;
            }

            int nPlayerTurnID = CFG.PLAYER_TURN_ID;
            Player player = CFG.core.getPlayer(nPlayerTurnID);
            int oldCivID = player.getCivId();

            if (oldCivID == newCivID) {
                return;
            }

            // 1. Знімаємо статус гравця зі старої країни
            if (oldCivID > 0 && oldCivID < CFG.core.getCivsSize()) {
                CFG.core.getCiv(oldCivID).setIsPlayer(false);
            }

            // 2. Призначаємо нову країну гравцю
            player.setCivId(newCivID);
            CFG.SPECTATOR_MODE = false;

            // 3. Синхронізація васалів для нової країни
            VassalManager.syncAllVassals();

            // 4. Оновлення туману війни
            player.initFogOfWar();
            CFG.gameAction.buildFogOfWar(nPlayerTurnID);
            player.buildMetProvsAndCivs();

            // 5. Оновлення прапора гравця
            player.loadPlayersFlag();

            // 6. Оновлення верхнього бару ресурсів
            CFG.menus.updateInGameTopAll(newCivID);

            // 7. Оновлення карти
            CFG.map.getMpB().updateWorldMap_Shaders();

            // 8. Центрування камери на столицю нової країни
            int capitalProv = CFG.core.getCiv(newCivID).getCapitalProvID();
            if (capitalProv >= 0 && CFG.core.getProv(capitalProv).getCivId() == newCivID) {
                CFG.map.getMpC().centerToProvID(capitalProv);
                CFG.core.setActiveProvID(capitalProv);
            } else if (CFG.core.getCiv(newCivID).getNumOfProvs() > 0) {
                int firstProv = CFG.core.getCiv(newCivID).getProvID(0);
                CFG.map.getMpC().centerToProvID(firstProv);
                CFG.core.setActiveProvID(firstProv);
            }

            // 9. Перевірка панелі дій
            CFG.core.checkProvinceActionMenu();

            // 10. Меню налаштувань закривається повністю
            CFG.menus.setVisible_InGame_Options(false);

            // 11. Сповіщення
            boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
            String civName = CFG.core.getCiv(newCivID).getCivName();
            String msg = (isUkr ? "Тепер ви граєте за: " : "Now playing as: ") + civName;
            CFG.toastM.addM(msg, CFG.COLOR_TEXT_NUM_OF_PROVINCES);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
