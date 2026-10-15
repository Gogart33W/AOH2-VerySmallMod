package age.of.civilizations2.jakowski.lukasz.Menus.Options;

import age.of.civilizations2.jakowski.lukasz.Button.Button_Transparent;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic;
import age.of.civilizations2.jakowski.lukasz.Button.Classic.Button_Classic_CivilizationAndFlag;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Core.Core;
import age.of.civilizations2.jakowski.lukasz.IMGManager;
import age.of.civilizations2.jakowski.lukasz.Images;
import age.of.civilizations2.jakowski.lukasz.Menu;
import age.of.civilizations2.jakowski.lukasz.Player;
import age.of.civilizations2.jakowski.lukasz.TextB.Texts.TextTitleStyle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Menu_SwitchCountry - меню зміни поточної цивілізації гравця.
 * Реалізовано модальний режим із захистом від випадкових кліків
 * та перехоплення курсору за межами вікна.
 */
public class Menu_SwitchCountry extends Menu {
    private static int switchCountryMenuID = -1;
    private static int switchCountryBGMenuID = -1;
    private static Menu_SwitchCountry instance = null;
    private static Menu_SwitchCountry_BG bgInstance = null;

    private List<Integer> lCivIDs = new ArrayList<Integer>();
    private int idCloseBtnTop = -1;
    private int idCloseBtnBottom = -1;
    private int idActiveProvBtn = -1;
    private int activeProvCivID = -1;

    public Menu_SwitchCountry() {
        this.rebuild();
    }

    /**
     * Фонове меню для затемнення та блокування кліків/наведення курсору за межами модального вікна.
     */
    public static class Menu_SwitchCountry_BG extends Menu {
        public Menu_SwitchCountry_BG() {
            List<MenuElemUI> elems = new ArrayList<MenuElemUI>();
            elems.add(new Button_Transparent(0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, true) {
                @Override
                public void actionElem(int iID) {
                    // Кліки за межами меню поглинаються і блокуються
                }
            });
            this.initMenu(null, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT, elems);
        }

        @Override
        public void actionEL(int iID) {
            // Заборона будь-яких дій за межами меню
        }

        @Override
        public void onBackPressed() {
            Menu_SwitchCountry.close();
        }

        @Override
        public void draw(SpriteBatch oSB, int iTranslateX, int iTranslateY, boolean sliderMenuIsActive) {
            // Напівпрозоре затемнення всієї гри
            oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.65f));
            IMGManager.getIMG(Images.pix255).draw(oSB, 0, 0, CFG.GAMEWIDTH, CFG.GAMEHEIGHT);
            oSB.setColor(Color.WHITE);
            super.draw(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        }
    }

    public static void open() {
        try {
            if (bgInstance == null || switchCountryBGMenuID < 0) {
                bgInstance = new Menu_SwitchCountry_BG();
                switchCountryBGMenuID = CFG.menus.addNewMenuToViewID(CFG.menus.IN_GAME_MENU, bgInstance);
            }
            if (instance == null || switchCountryMenuID < 0) {
                instance = new Menu_SwitchCountry();
                switchCountryMenuID = CFG.menus.addNewMenuToViewID(CFG.menus.IN_GAME_MENU, instance);
            } else {
                instance.rebuild();
            }

            bgInstance.setVisibleM(true);
            instance.setVisibleM(true);

            // Виставляємо порядок: фоновий блокувальник, потім вікно меню зверху
            CFG.menus.setOrderOfMenuID(switchCountryBGMenuID);
            CFG.menus.setOrderOfMenuID(switchCountryMenuID);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }

    public static void close() {
        close(true);
    }

    public static void close(boolean reopenOptions) {
        if (bgInstance != null) {
            bgInstance.setVisibleM(false);
        }
        if (instance != null) {
            instance.setVisibleM(false);
        }
        if (reopenOptions) {
            CFG.menus.setVisible_InGame_Options(true);
        }
    }

    public void rebuild() {
        this.lCivIDs.clear();
        List<MenuElemUI> menuElements = new ArrayList<MenuElemUI>();

        int menuWidth = Math.min((int)(CFG.CIV_INFO_MENU_WIDTH * 1.6f), CFG.GAMEWIDTH - CFG.PADD * 4);
        int menuHeight = Math.min((int)(CFG.GAMEHEIGHT * 0.82f), CFG.GAMEHEIGHT - CFG.BUTTON_H);
        int menuX = CFG.GAMEWIDTH / 2 - menuWidth / 2;
        int menuY = CFG.GAMEHEIGHT / 2 - menuHeight / 2;

        int btnW = menuWidth - CFG.PADD * 4;
        int btnX = CFG.PADD * 2;
        int btnH = CFG.BUTTON_H;
        int titleH = CFG.BUTTON_H * 3 / 4;
        int curY = CFG.PADD;

        boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");

        // 1. Заголовок вікна
        String titleText = isUkr ? "ЗМІНА ДЕРЖАВИ" : "SWITCH CIVILIZATION";
        TextTitleStyle titleElem = new TextTitleStyle(titleText, -1, btnX, curY, btnW, titleH);
        menuElements.add(titleElem);
        curY += titleH + CFG.PADD;

        // 2. Головна кнопка виходу з меню зверху списку
        String closeTextTop = isUkr ? "✕ Закрити вікно (Повернутися до меню)" : "✕ Close (Return to Menu)";
        Button_Classic closeBtnTop = new Button_Classic(closeTextTop, -1, btnX, curY, btnW, btnH, true);
        menuElements.add(closeBtnTop);
        this.idCloseBtnTop = menuElements.size() - 1;
        curY += btnH + CFG.PADD * 2;

        // 3. Швидке перемикання на країну поточної обраної провінції
        this.idActiveProvBtn = -1;
        this.activeProvCivID = -1;
        if (CFG.core.getActiveProvID() >= 0) {
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

        // 4. Збір та сортування живих цивілізацій
        int curPlayerCiv = CFG.core.getPlayer(CFG.PLAYER_TURN_ID).getCivId();
        List<Integer> aliveCivs = new ArrayList<Integer>();
        for (int i = 1; i < CFG.core.getCivsSize(); i++) {
            if (CFG.core.getCiv(i).getNumOfProvs() > 0) {
                aliveCivs.add(i);
            }
        }

        // Поточна цивілізація перша, далі за кількістю провінцій
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
            curY += btnH + CFG.PADD;
        }

        // 5. Додаткова кнопка виходу внизу списку
        String closeTextBottom = isUkr ? "✕ Закрити вікно" : "✕ Close Window";
        Button_Classic closeBtnBottom = new Button_Classic(closeTextBottom, -1, btnX, curY, btnW, btnH, true);
        menuElements.add(closeBtnBottom);
        this.idCloseBtnBottom = menuElements.size() - 1;
        curY += btnH + CFG.PADD;

        // Ініціалізація з можливістю закриття (хрестик в кутку) та вертикальним скролінгом
        this.initMenu(null, menuX, menuY, menuWidth, menuHeight, menuElements, true, false, true);
    }

    @Override
    public void actionEL(int iID) {
        if (iID == this.idCloseBtnTop || iID == this.idCloseBtnBottom) {
            close();
            return;
        }

        if (this.idActiveProvBtn >= 0 && iID == this.idActiveProvBtn) {
            if (this.activeProvCivID > 0) {
                switchToCiv(this.activeProvCivID);
            }
            close(false);
            return;
        }

        int firstCivIndex = (this.idActiveProvBtn >= 0) ? (this.idActiveProvBtn + 1) : (this.idCloseBtnTop + 1);
        int civIndex = iID - firstCivIndex;
        if (civIndex >= 0 && civIndex < this.lCivIDs.size()) {
            int targetCiv = this.lCivIDs.get(civIndex);
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
        // Рамка та фон модального вікна
        Core.drawMenuBG(oSB, this.getPosX() + iTranslateX, this.getPosY() + iTranslateY, this.getWidthM(), this.getHeightM());

        // Відмальовка елементів меню
        super.draw(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }

    /**
     * Повна логіка зміни держави гравця.
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

            // 3. Оновлення туману війни
            player.initFogOfWar();
            CFG.gameAction.buildFogOfWar(nPlayerTurnID);
            player.buildMetProvsAndCivs();

            // 4. Оновлення прапора гравця
            player.loadPlayersFlag();

            // 5. Оновлення верхнього бару ресурсів
            CFG.menus.updateInGameTopAll(newCivID);

            // 6. Оновлення карти
            CFG.map.getMpB().updateWorldMap_Shaders();

            // 7. Центрування камери на столицю нової країни
            int capitalProv = CFG.core.getCiv(newCivID).getCapitalProvID();
            if (capitalProv >= 0 && CFG.core.getProv(capitalProv).getCivId() == newCivID) {
                CFG.map.getMpC().centerToProvID(capitalProv);
                CFG.core.setActiveProvID(capitalProv);
            } else if (CFG.core.getCiv(newCivID).getNumOfProvs() > 0) {
                int firstProv = CFG.core.getCiv(newCivID).getProvID(0);
                CFG.map.getMpC().centerToProvID(firstProv);
                CFG.core.setActiveProvID(firstProv);
            }

            // 8. Перевірка панелі дій
            CFG.core.checkProvinceActionMenu();

            // 9. Меню налаштувань закривається повністю
            CFG.menus.setVisible_InGame_Options(false);

            // 10. Сповіщення
            boolean isUkr = CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська");
            String civName = CFG.core.getCiv(newCivID).getCivName();
            String msg = (isUkr ? "Тепер ви граєте за: " : "Now playing as: ") + civName;
            CFG.toastM.addM(msg, CFG.COLOR_TEXT_NUM_OF_PROVINCES);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
