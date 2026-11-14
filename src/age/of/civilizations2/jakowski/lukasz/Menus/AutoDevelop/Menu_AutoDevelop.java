package age.of.civilizations2.jakowski.lukasz.Menus.AutoDevelop;

import age.of.civilizations2.jakowski.lukasz.Button.Button_Transparent;
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
        super.setVisibleM(false);
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

    public static String getProvincesPlural(int n, boolean isUkr) {
        if (!isUkr) return (n == 1) ? "province" : "provinces";
        int mod100 = n % 100;
        int mod10 = n % 10;
        if (mod100 >= 11 && mod100 <= 19) return "провінцій";
        if (mod10 == 1) return "провінція";
        if (mod10 >= 2 && mod10 <= 4) return "провінції";
        return "провінцій";
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
                    this.iTextHeight = CFG.TEXT_HEIGHT_DEFAULT;
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

    /**
     * DevButton - високоточна кастомна кнопка без артефактів нативного Button_Classic:
     * 1. Точні межі малювання (жодного вилізання за межі [x, y, w, h]).
     * 2. Точний хітбокс колізії (колізійна зона 1-в-1 збігається з візуальною кнопкою).
     * 3. Жодного фонового розтягування Images.btnMenuH (який у грі захардкоджений на 68px).
     * 4. Акуратні підсвічування при ховері та активному стані (ON/OFF, пресети, селектори).
     */
    public static class DevButton extends MenuElemUI {
        public enum ButtonType {
            NORMAL,
            TOGGLE,
            MASTER,
            PRESET,
            SELECTOR,
            ACTION
        }

        private String sText = "";
        private int iTextWidth = 0;
        private int iTextHeight = CFG.TEXT_HEIGHT_DEFAULT;
        private int textPosition = -1;
        private boolean isChecked = false;
        private ButtonType btnType = ButtonType.NORMAL;

        public DevButton(String sText, int textPosition, int iPosX, int iPosY, int iWidth, int iHeight, boolean isClickable) {
            this(sText, textPosition, iPosX, iPosY, iWidth, iHeight, isClickable, false, ButtonType.NORMAL);
        }

        public DevButton(String sText, int textPosition, int iPosX, int iPosY, int iWidth, int iHeight, boolean isClickable, boolean isChecked) {
            this(sText, textPosition, iPosX, iPosY, iWidth, iHeight, isClickable, isChecked, ButtonType.NORMAL);
        }

        public DevButton(String sText, int textPosition, int iPosX, int iPosY, int iWidth, int iHeight, boolean isClickable, boolean isChecked, ButtonType btnType) {
            this.typeOfMenuElemUI = MenuElemUI.TypeOfMenuElemUI.BUTTON;
            this.setPosX(iPosX);
            this.setPosY(iPosY);
            this.setWidthE(iWidth);
            this.setHeightE(iHeight);
            this.setClickable(isClickable);
            this.setVisibleE(true);
            this.textPosition = textPosition;
            this.isChecked = isChecked;
            this.btnType = (btnType != null) ? btnType : ButtonType.NORMAL;
            this.setTextE(sText);
        }

        @Override
        public void setTextE(String sText) {
            this.sText = (sText != null) ? sText : "";
            try {
                if (this.sText.length() > 0 && CFG.glyphLay != null && CFG.fontMain != null && !CFG.fontMain.isEmpty() && this.fontID < CFG.fontMain.size() && CFG.fontMain.get(this.fontID) != null) {
                    CFG.glyphLay.setText(CFG.fontMain.get(this.fontID), this.sText);
                    this.iTextWidth = (int)CFG.glyphLay.width;
                    this.iTextHeight = CFG.TEXT_HEIGHT_DEFAULT;
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
        public boolean getCheckboxSt() {
            return this.isChecked;
        }

        @Override
        public void setCheckboxSt(boolean checked) {
            this.isChecked = checked;
        }

        @Override
        public int getSFXElem() {
            return SFXManager.SFX_CLICK2;
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
            boolean clickable = this.getIsClickable();
            boolean hovered = clickable && this.getIsHovered();

            try {
                // 1. ФОНОВЕ ЗАПОВНЕННЯ (строго в межах x, y, w, h)
                Color fillCol;
                if (!clickable) {
                    fillCol = new Color(0.06f, 0.08f, 0.11f, 0.50f);
                } else if (this.btnType == ButtonType.MASTER) {
                    fillCol = this.isChecked 
                            ? new Color(0.10f, 0.28f, 0.16f, 0.95f)
                            : new Color(0.24f, 0.08f, 0.08f, 0.95f);
                } else if (this.btnType == ButtonType.TOGGLE) {
                    fillCol = this.isChecked
                            ? new Color(0.10f, 0.22f, 0.14f, 0.90f)
                            : new Color(0.10f, 0.13f, 0.18f, 0.85f);
                } else if (this.btnType == ButtonType.ACTION) {
                    fillCol = new Color(0.12f, 0.18f, 0.28f, 0.95f);
                } else if (this.isChecked) {
                    fillCol = new Color(0.16f, 0.26f, 0.42f, 0.95f);
                } else {
                    fillCol = new Color(0.10f, 0.13f, 0.18f, 0.85f);
                }

                oSB.setColor(fillCol);
                IMGManager.getIMG(Images.pix255).draw(oSB, x, y, w, h);

                // М'який градієнт верхньої половини для об'єму (native AoH2 стиль)
                if (clickable && h > 6) {
                    oSB.setColor(new Color(1.0f, 1.0f, 1.0f, 0.04f));
                    IMGManager.getIMG(Images.gradient).draw(oSB, x, y, w, h / 2);
                }

                // 2. БІЧНІ / ВЕРХНІ ІНДИКАТОРИ СТАТУСУ
                if (clickable) {
                    if (this.btnType == ButtonType.TOGGLE) {
                        Color barCol = this.isChecked 
                                ? new Color(0.25f, 0.85f, 0.38f, 0.95f)
                                : new Color(0.70f, 0.20f, 0.20f, 0.85f);
                        oSB.setColor(barCol);
                        IMGManager.getIMG(Images.pix255).draw(oSB, x, y, 4, h);
                    } else if (this.btnType == ButtonType.MASTER) {
                        Color barCol = this.isChecked 
                                ? new Color(0.30f, 0.95f, 0.45f, 1.0f)
                                : new Color(0.85f, 0.25f, 0.25f, 1.0f);
                        oSB.setColor(barCol);
                        IMGManager.getIMG(Images.pix255).draw(oSB, x, y, 5, h);
                        IMGManager.getIMG(Images.pix255).draw(oSB, x + w - 5, y, 5, h);
                    } else if (this.isChecked && (this.btnType == ButtonType.PRESET || this.btnType == ButtonType.SELECTOR)) {
                        oSB.setColor(new Color(0.45f, 0.75f, 1.0f, 0.95f));
                        IMGManager.getIMG(Images.pix255).draw(oSB, x, y, w, 2);
                    }
                }

                // 3. ХОВЕР-ПІДСВІЧУВАННЯ (виключно в межах кнопки)
                if (hovered) {
                    oSB.setColor(new Color(1.0f, 1.0f, 1.0f, 0.12f));
                    IMGManager.getIMG(Images.pix255).draw(oSB, x, y, w, h);
                }

                // 4. ТОЧНА 1-ПІКСЕЛЬНА РАМКА ПО ВЕРТИКАЛІ ТА ГОРИЗОНТАЛІ
                Color bColor;
                if (!clickable) {
                    bColor = new Color(0.30f, 0.35f, 0.40f, 0.25f);
                } else if (hovered) {
                    bColor = new Color(CFG.COLOR_FLAG_FRAME.r, CFG.COLOR_FLAG_FRAME.g, CFG.COLOR_FLAG_FRAME.b, 0.95f);
                } else if (this.btnType == ButtonType.MASTER) {
                    bColor = this.isChecked
                            ? new Color(0.28f, 0.85f, 0.40f, 0.85f)
                            : new Color(0.75f, 0.22f, 0.22f, 0.85f);
                } else if (this.btnType == ButtonType.TOGGLE && this.isChecked) {
                    bColor = new Color(0.25f, 0.75f, 0.38f, 0.75f);
                } else if (this.isChecked) {
                    bColor = new Color(0.40f, 0.70f, 1.0f, 0.90f);
                } else {
                    bColor = new Color(CFG.COLOR_FLAG_FRAME.r, CFG.COLOR_FLAG_FRAME.g, CFG.COLOR_FLAG_FRAME.b, 0.40f);
                }

                oSB.setColor(bColor);
                IMGManager.getIMG(Images.pix255).draw(oSB, x, y, w, 1);
                IMGManager.getIMG(Images.pix255).draw(oSB, x, y + h - 1, w, 1);
                IMGManager.getIMG(Images.pix255).draw(oSB, x, y, 1, h);
                IMGManager.getIMG(Images.pix255).draw(oSB, x + w - 1, y, 1, h);

                // 5. ВІДОБРАЖЕННЯ ТЕКСТУ З ТІННЮ
                Color tColor;
                if (!clickable) {
                    tColor = new Color(0.50f, 0.53f, 0.58f, 0.50f);
                } else if (hovered) {
                    tColor = CFG.COLOR_BUTTON_MENU_TEXT_HOVERED;
                } else if (this.btnType == ButtonType.MASTER) {
                    tColor = this.isChecked
                            ? new Color(0.70f, 1.0f, 0.75f, 1.0f)
                            : new Color(1.0f, 0.75f, 0.75f, 1.0f);
                } else if (this.btnType == ButtonType.TOGGLE) {
                    tColor = this.isChecked
                            ? new Color(0.80f, 0.98f, 0.85f, 1.0f)
                            : new Color(0.72f, 0.75f, 0.80f, 0.90f);
                } else if (this.isChecked) {
                    tColor = new Color(0.85f, 0.95f, 1.0f, 1.0f);
                } else {
                    tColor = new Color(0.88f, 0.90f, 0.94f, 1.0f);
                }

                int textX;
                if (this.textPosition >= 0) {
                    textX = x + Math.max(CFG.PADD * 2, this.textPosition);
                } else {
                    textX = x + Math.max(CFG.PADD, (w - this.iTextWidth) / 2);
                }
                int textY = y + (h - this.iTextHeight) / 2;
                Renderer.drawTextWithShadow(oSB, this.fontID, this.sText, textX, textY, tColor);

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

    public void rebuild() {
        boolean wasVisible = this.getVisibleM();
        List<MenuElemUI> menuElements = new ArrayList<MenuElemUI>();
        boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null) 
                ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська") 
                : true;

        int gw = CFG.GAMEWIDTH;
        int gh = CFG.GAMEHEIGHT;
        int pad = CFG.PADD;

        // 1. Адаптивні пропорційні розміри модального діалогу під реальну роздільну здатність
        int menuW = Math.min((int)(gw * 0.92f), Math.min(960, gw - pad * 4));
        int startX = (gw - menuW) / 2;

        // Коефіцієнт масштабування висоти від 720p до 1080p+ (0.0 на 720p, 1.0 на 1080p)
        float hRatio = Math.max(0.0f, Math.min(1.2f, (gh - 720f) / (1080f - 720f)));

        // Відстані по висоті (збільшено у 2 - 2.5 рази за вимогою, щоб кнопки не злипалися і не залазили одна на одну!)
        // gapS: між інформаційними рядками (було 2px -> стало 6-8px)
        // gapM: основний проміжний рядковий інтервал між кнопками (було 4px -> стало 10-13px)
        // gapL: розділовий інтервал між логічними блоками (було 6px -> стало 16-20px)
        int gapS = Math.round(6f + 2f * hRatio);
        int gapM = Math.round(10f + 3f * hRatio);
        int gapL = Math.round(16f + 4f * hRatio);

        // Висоти елементів меню
        int titleH = Math.round(38f + 10f * hRatio);
        int statusH = Math.round(24f + 5f * hRatio);
        int masterBtnH = Math.round(42f + 8f * hRatio);
        int colHeaderH = Math.round(26f + 5f * hRatio);
        int btnH = Math.round(38f + 6f * hRatio);
        int smallBtnH = Math.round(32f + 5f * hRatio);
        int actionBtnH = Math.round(42f + 8f * hRatio);
        int closeBottomH = Math.round(36f + 6f * hRatio);

        int colGap = Math.round(12f + 6f * hRatio);
        int colW = (menuW - colGap) / 2;
        int col1X = startX;
        int col2X = startX + colW + colGap;

        int padText = Math.max(8, pad * 2);

        // Синхронізована висота 8 рядків колонок:
        // 1. Header (colHeaderH + gapM)
        // 2..5. 4 модулі / 4 пріоритети (4 * (btnH + gapM))
        // 6. Цільова екон. / Пресети (smallBtnH + gapM)
        // 7. Відсотки % / Резерв ходів (smallBtnH + gapM)
        // 8. Сповіщення / Резерв золота (smallBtnH)
        int syncedColumnsH = colHeaderH + gapM + (4 * (btnH + gapM)) + (smallBtnH + gapM) + (smallBtnH + gapM) + smallBtnH;

        int totalDialogH = (titleH + gapM)
                + (statusH + gapS)
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

        DevButton closeTopBtn = new DevButton(isUkr ? "Закрити" : "Close", -1, startX + menuW - closeBtnW, curY, closeBtnW, titleH, true, false, DevButton.ButtonType.NORMAL);
        menuElements.add(closeTopBtn);
        this.idCloseTop = menuElements.size() - 1;
        curY += titleH + gapM;

        // 3. Назва держави та кількість провінцій (на повну ширину діалогу, щоб жодна держава ніколи не обрізалась!)
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

        int statusGap = Math.max(6, Math.round(6f + 3f * hRatio));

        String civFull = (isUkr ? "Держава: " : "Civ: ") + civName + " (" + numProvs + " " + getProvincesPlural(numProvs, isUkr) + ")";
        InfoCell civCell = new InfoCell(fitText(civFull, menuW - padText), startX, curY, menuW, statusH);
        civCell.setColors(new Color(0.95f, 0.95f, 1.0f, 1.0f), new Color(0.09f, 0.12f, 0.18f, 0.85f), null);
        menuElements.add(civCell);
        curY += statusH + gapS;

        // 4. Показники ресурсів (3 просторі комірки: Ходи | Дипломатія | Золото)
        int resCellW = (menuW - statusGap * 2) / 3;
        String c2 = (isUkr ? "Ходи: " : "Moves: ") + CFG.getPrecision2(movPts, 10);
        String c3 = (isUkr ? "Дипломатія: " : "Diplo: ") + CFG.getPrecision2(dipPts, 10);
        String c4 = (isUkr ? "Золото: " : "Gold: ") + CFG.getNumberWthSpaces("" + gold);

        menuElements.add(new InfoCell(fitText(c2, resCellW - padText), startX, curY, resCellW, statusH));
        menuElements.add(new InfoCell(fitText(c3, resCellW - padText), startX + resCellW + statusGap, curY, resCellW, statusH));
        menuElements.add(new InfoCell(fitText(c4, resCellW - padText), startX + (resCellW + statusGap) * 2, curY, resCellW, statusH));
        curY += statusH + gapS;

        // 5. Потреби провінцій (4 чіткі комірки InfoCell)
        int needAssi = (playerCivID > 0) ? AutoDevelopManager.getNeedAssimilateCount(playerCivID) : 0;
        int needFest = (playerCivID > 0) ? AutoDevelopManager.getNeedFestivalCount(playerCivID) : 0;
        int needEco = (playerCivID > 0) ? AutoDevelopManager.getNeedEconomyCount(playerCivID) : 0;
        int needDev = (playerCivID > 0) ? AutoDevelopManager.getNeedDevelopmentCount(playerCivID) : 0;

        String d1 = (isUkr ? "Асиміляція: " : "Assim: ") + needAssi;
        String d2 = (isUkr ? "Ярмарки: " : "Fest: ") + needFest;
        String d3 = (isUkr ? "Економіка: " : "Eco: ") + needEco;
        String d4 = (isUkr ? "Дослідження: " : "Dev: ") + needDev;

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
        DevButton masterBtn = new DevButton(fitText(masterText, menuW - padText * 2), -1, startX, curY, menuW, masterBtnH, true, AutoDevelopManager.autoDevelopEnabled, DevButton.ButtonType.MASTER);
        menuElements.add(masterBtn);
        this.idToggleMaster = menuElements.size() - 1;
        curY += masterBtnH + gapL;

        this.columnsStartY = curY;

        // =========================================================================
        // СИНХРОНІЗОВАНІ РЯДКИ ЛІВОЇ ТА ПРАВОЇ КОЛОНОК (ОКРЕМІ АКУМУЛЯТОРИ leftY / rightY)
        // =========================================================================

        int leftY = this.columnsStartY;
        int rightY = this.columnsStartY;

        // --- РЯДОК 1: ЗАГОЛОВКИ КОЛОНОК (InfoCell, тиха підкладка, не вимкнена кнопка!) ---
        String modHeader = isUkr ? "--- МОДУЛІ АВТОМАТИЗАЦІЇ ---" : "--- AUTOMATION MODULES ---";
        InfoCell h1 = new InfoCell(fitText(modHeader, colW - padText), col1X, leftY, colW, colHeaderH, false);
        h1.setColors(CFG.COLOR_TEXT_NUM_OF_PROVINCES, new Color(0.08f, 0.11f, 0.18f, 0.85f), null);
        menuElements.add(h1);
        leftY += colHeaderH + gapM;

        String prioHeader = isUkr ? "--- ЧЕРГА ПРІОРИТЕТІВ (1 -> 4) ---" : "--- PRIORITY ORDER (1 -> 4) ---";
        InfoCell h2 = new InfoCell(fitText(prioHeader, colW - padText), col2X, rightY, colW, colHeaderH, false);
        h2.setColors(CFG.COLOR_TEXT_NUM_OF_PROVINCES, new Color(0.08f, 0.11f, 0.18f, 0.85f), null);
        menuElements.add(h2);
        rightY += colHeaderH + gapM;

        int prioGap = Math.max(5, Math.round(5f + 2f * hRatio));
        int upDownW = Math.max(30, btnH - 6);
        int prioNameW = colW - (upDownW * 2 + prioGap * 2);

        // --- РЯДОК 2: АСИМІЛЯЦІЯ / ПРІОРИТЕТ 1 ---
        String assiText = isUkr 
                ? (AutoDevelopManager.autoAssimilate ? "[ВКЛ] Асиміляція (до 100% стабільності)" : "[ВИКЛ] Асиміляція (стабільність)")
                : (AutoDevelopManager.autoAssimilate ? "[ON] Assimilation (to 100% stability)" : "[OFF] Assimilation");
        DevButton assiBtn = new DevButton(fitText(assiText, colW - padText), -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoAssimilate, DevButton.ButtonType.TOGGLE);
        menuElements.add(assiBtn);
        this.idToggleAssimilate = menuElements.size() - 1;
        leftY += btnH + gapM;

        this.addPriorityRow(menuElements, 0, col2X, rightY, prioNameW, upDownW, btnH, prioGap, isUkr, padText);
        rightY += btnH + gapM;

        // --- РЯДОК 3: ЯРМАРКИ / ПРІОРИТЕТ 2 ---
        String festText = isUkr 
                ? (AutoDevelopManager.autoFestival ? "[ВКЛ] Проведення ярмарок (щастя)" : "[ВИКЛ] Проведення ярмарок (щастя)")
                : (AutoDevelopManager.autoFestival ? "[ON] Organize Festivals (happiness)" : "[OFF] Organize Festivals (happiness)");
        DevButton festBtn = new DevButton(fitText(festText, colW - padText), -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoFestival, DevButton.ButtonType.TOGGLE);
        menuElements.add(festBtn);
        this.idToggleFestival = menuElements.size() - 1;
        leftY += btnH + gapM;

        this.addPriorityRow(menuElements, 1, col2X, rightY, prioNameW, upDownW, btnH, prioGap, isUkr, padText);
        rightY += btnH + gapM;

        // --- РЯДОК 4: НАУКА / ПРІОРИТЕТ 3 ---
        String devText = isUkr 
                ? (AutoDevelopManager.autoInvestDevelopment ? "[ВКЛ] Дослідження (до рівня науки)" : "[ВИКЛ] Дослідження (наука)")
                : (AutoDevelopManager.autoInvestDevelopment ? "[ON] Research (to Civ Tech Level)" : "[OFF] Research Development");
        DevButton devBtn = new DevButton(fitText(devText, colW - padText), -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoInvestDevelopment, DevButton.ButtonType.TOGGLE);
        menuElements.add(devBtn);
        this.idToggleDevelopment = menuElements.size() - 1;
        leftY += btnH + gapM;

        this.addPriorityRow(menuElements, 2, col2X, rightY, prioNameW, upDownW, btnH, prioGap, isUkr, padText);
        rightY += btnH + gapM;

        // --- РЯДОК 5: ЕКОНОМІКА / ПРІОРИТЕТ 4 ---
        String ecoText = isUkr 
                ? (AutoDevelopManager.autoInvestEconomy ? "[ВКЛ] Розвиток економіки (інвестиції)" : "[ВИКЛ] Розвиток економіки (інвестиції)")
                : (AutoDevelopManager.autoInvestEconomy ? "[ON] Economy Investments" : "[OFF] Economy Investments");
        DevButton ecoBtn = new DevButton(fitText(ecoText, colW - padText), -1, col1X, leftY, colW, btnH, true, AutoDevelopManager.autoInvestEconomy, DevButton.ButtonType.TOGGLE);
        menuElements.add(ecoBtn);
        this.idToggleEconomy = menuElements.size() - 1;
        leftY += btnH + gapM;

        this.addPriorityRow(menuElements, 3, col2X, rightY, prioNameW, upDownW, btnH, prioGap, isUkr, padText);
        rightY += btnH + gapM;

        // --- РЯДОК 6: ПІДПИС ЦІЛІ ЕКОНОМІКИ / ПРЕСЕТИ ПРІОРИТЕТІВ (КОРОТКІ НАЗВИ, ПОВНА ШИРИНА) ---
        String ecoTargetLabel = isUkr 
                ? ("Цільова економіка: " + AutoDevelopManager.targetEcoPercentage + "% від населення")
                : ("Target Economy: " + AutoDevelopManager.targetEcoPercentage + "% of Pop");
        menuElements.add(new InfoCell(fitText(ecoTargetLabel, colW - padText), col1X, leftY, colW, smallBtnH));
        leftY += smallBtnH + gapM;

        // 3 пресети на повну ширину правої колонки, короткі назви через fitText
        int presetGap = Math.max(6, Math.round(6f + 2f * hRatio));
        int pPresetW = (colW - presetGap * 2) / 3;
        boolean isBal = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[1] == AutoDevelopManager.PRIO_FESTIVAL);
        boolean isEco = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ECONOMY);
        boolean isStab = (AutoDevelopManager.priorities[0] == AutoDevelopManager.PRIO_ASSIMILATE && AutoDevelopManager.priorities[1] == AutoDevelopManager.PRIO_DEVELOPMENT);

        String tBal = fitText(isUkr ? "Баланс" : "Balance", pPresetW - padText);
        String tEco = fitText(isUkr ? "Економіка" : "Economy", pPresetW - padText);
        String tStab = fitText(isUkr ? "Стабільність" : "Stability", pPresetW - padText);

        DevButton pBal = new DevButton(tBal, -1, col2X, rightY, pPresetW, smallBtnH, true, isBal, DevButton.ButtonType.PRESET);
        menuElements.add(pBal);
        this.idPrioPresetBalanced = menuElements.size() - 1;

        DevButton pEco = new DevButton(tEco, -1, col2X + pPresetW + presetGap, rightY, pPresetW, smallBtnH, true, isEco, DevButton.ButtonType.PRESET);
        menuElements.add(pEco);
        this.idPrioPresetEconomy = menuElements.size() - 1;

        DevButton pStab = new DevButton(tStab, -1, col2X + (pPresetW + presetGap) * 2, rightY, pPresetW, smallBtnH, true, isStab, DevButton.ButtonType.PRESET);
        menuElements.add(pStab);
        this.idPrioPresetStability = menuElements.size() - 1;
        rightY += smallBtnH + gapM;

        // --- РЯДОК 7: КНОПКИ РЕГУЛЮВАННЯ ЕКОНОМІКИ / РЕЗЕРВ ХОДІВ ---
        int ecoGap = Math.max(4, Math.round(4f + 2f * hRatio));
        int bW = (colW - ecoGap * 5) / 6;
        int bStep = bW + ecoGap;

        DevButton btnM10 = new DevButton("-10%", -1, col1X, leftY, bW, smallBtnH, true, false, DevButton.ButtonType.NORMAL);
        menuElements.add(btnM10);
        this.idEcoM10 = menuElements.size() - 1;

        DevButton btnP10 = new DevButton("+10%", -1, col1X + bStep, leftY, bW, smallBtnH, true, false, DevButton.ButtonType.NORMAL);
        menuElements.add(btnP10);
        this.idEcoP10 = menuElements.size() - 1;

        DevButton p80 = new DevButton("80%", -1, col1X + bStep * 2, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 80, DevButton.ButtonType.SELECTOR);
        menuElements.add(p80);
        this.idEco80 = menuElements.size() - 1;

        DevButton p100 = new DevButton("100%", -1, col1X + bStep * 3, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 100, DevButton.ButtonType.SELECTOR);
        menuElements.add(p100);
        this.idEco100 = menuElements.size() - 1;

        DevButton p120 = new DevButton("120%", -1, col1X + bStep * 4, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 120, DevButton.ButtonType.SELECTOR);
        menuElements.add(p120);
        this.idEco120 = menuElements.size() - 1;

        DevButton p150 = new DevButton("150%", -1, col1X + bStep * 5, leftY, bW, smallBtnH, true, AutoDevelopManager.targetEcoPercentage == 150, DevButton.ButtonType.SELECTOR);
        menuElements.add(p150);
        this.idEco150 = menuElements.size() - 1;
        leftY += smallBtnH + gapM;

        // Резерв ходів (підпис InfoCell, лише вибране значення має checked стиль)
        int resLabelW = Math.max(90, Math.round(92f + 16f * hRatio));
        int resBtnGap = Math.max(5, Math.round(5f + 2f * hRatio));
        int res4W = (colW - resLabelW - resBtnGap * 4) / 4;
        int resStep = res4W + resBtnGap;

        String resMovesTitle = isUkr ? "Ходи:" : "Moves:";
        menuElements.add(new InfoCell(fitText(resMovesTitle, resLabelW - padText), col2X, rightY, resLabelW, smallBtnH));

        DevButton rM0 = new DevButton("0.0", -1, col2X + resLabelW + resBtnGap, rightY, res4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 0, DevButton.ButtonType.SELECTOR);
        menuElements.add(rM0);
        this.idResMove0 = menuElements.size() - 1;

        DevButton rM2 = new DevButton("2.0", -1, col2X + resLabelW + resBtnGap + resStep, rightY, res4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 20, DevButton.ButtonType.SELECTOR);
        menuElements.add(rM2);
        this.idResMove2 = menuElements.size() - 1;

        DevButton rM4 = new DevButton("4.0", -1, col2X + resLabelW + resBtnGap + resStep * 2, rightY, res4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 40, DevButton.ButtonType.SELECTOR);
        menuElements.add(rM4);
        this.idResMove4 = menuElements.size() - 1;

        DevButton rM6 = new DevButton("6.0", -1, col2X + resLabelW + resBtnGap + resStep * 3, rightY, res4W, smallBtnH, true, AutoDevelopManager.minMovePointsReserve == 60, DevButton.ButtonType.SELECTOR);
        menuElements.add(rM6);
        this.idResMove6 = menuElements.size() - 1;
        rightY += smallBtnH + gapM;

        // --- РЯДОК 8: ТУМБЛЕР СПОВІЩЕНЬ / РЕЗЕРВ ЗОЛОТА ---
        String notifText = isUkr 
                ? (AutoDevelopManager.showNotifications ? "[ВКЛ] Сповіщення про виконані дії" : "[ВИКЛ] Сповіщення про виконані дії")
                : (AutoDevelopManager.showNotifications ? "[ON] Action Notifications" : "[OFF] Action Notifications");
        DevButton notifBtn = new DevButton(fitText(notifText, colW - padText), -1, col1X, leftY, colW, smallBtnH, true, AutoDevelopManager.showNotifications, DevButton.ButtonType.TOGGLE);
        menuElements.add(notifBtn);
        this.idToggleNotifications = menuElements.size() - 1;
        leftY += smallBtnH;

        // Резерв золота (підпис InfoCell, лише вибране значення має checked стиль)
        String resGoldTitle = isUkr ? "Золото:" : "Gold:";
        menuElements.add(new InfoCell(fitText(resGoldTitle, resLabelW - padText), col2X, rightY, resLabelW, smallBtnH));

        DevButton rG0 = new DevButton("0", -1, col2X + resLabelW + resBtnGap, rightY, res4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 0, DevButton.ButtonType.SELECTOR);
        menuElements.add(rG0);
        this.idResGold0 = menuElements.size() - 1;

        DevButton rG1k = new DevButton("1 000", -1, col2X + resLabelW + resBtnGap + resStep, rightY, res4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 1000, DevButton.ButtonType.SELECTOR);
        menuElements.add(rG1k);
        this.idResGold1k = menuElements.size() - 1;

        DevButton rG5k = new DevButton("5 000", -1, col2X + resLabelW + resBtnGap + resStep * 2, rightY, res4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 5000, DevButton.ButtonType.SELECTOR);
        menuElements.add(rG5k);
        this.idResGold5k = menuElements.size() - 1;

        DevButton rG10k = new DevButton("10 000", -1, col2X + resLabelW + resBtnGap + resStep * 3, rightY, res4W, smallBtnH, true, AutoDevelopManager.minGoldReserve == 10000, DevButton.ButtonType.SELECTOR);
        menuElements.add(rG10k);
        this.idResGold10k = menuElements.size() - 1;
        rightY += smallBtnH;

        int columnsEndY = Math.max(leftY, rightY);
        this.columnsHeight = columnsEndY - this.columnsStartY;

        // ==========================================
        // НИЖНЯ ПАНЕЛЬ: ДІЇ (ЗАПУСК ТА ЗАКРИТТЯ)
        // ==========================================
        curY = columnsEndY + gapL;

        String runText = isUkr ? "ЗАПУСТИТИ ЗАРАЗ (НА ПОТОЧНОМУ ХОДІ)" : "RUN AUTOMATION NOW (THIS TURN)";
        DevButton runBtn = new DevButton(fitText(runText, menuW - padText), -1, startX, curY, menuW, actionBtnH, true, false, DevButton.ButtonType.ACTION);
        menuElements.add(runBtn);
        this.idRunNow = menuElements.size() - 1;
        curY += actionBtnH + gapM;

        String closeTextBottom = isUkr ? "Повернутися до гри" : "Return to Game";
        DevButton closeBtnBottom = new DevButton(fitText(closeTextBottom, menuW - padText), -1, startX, curY, menuW, closeBottomH, true, false, DevButton.ButtonType.ACTION);
        menuElements.add(closeBtnBottom);
        this.idCloseBottom = menuElements.size() - 1;
        curY += closeBottomH;

        this.dialogHeight = curY - startY;

        // Поглинач кліків поза діалогом
        this.idCatchAll = menuElements.size();
        menuElements.add(new Button_Transparent(0, 0, gw, gh, true));

        this.initMenu(null, 0, 0, gw, gh, menuElements);
        super.setVisibleM(wasVisible);
    }

    private void addPriorityRow(List<MenuElemUI> menuElements, int i, int col2X, int rowY, int prioNameW, int upDownW, int btnH, int gap, boolean isUkr, int padText) {
        int prioType = AutoDevelopManager.priorities[i];
        String name = AutoDevelopManager.getPriorityName(prioType, isUkr);
        String rowText = "[ " + (i + 1) + " ] " + name;

        DevButton prioBtn = new DevButton(fitText(rowText, prioNameW - padText), -1, col2X, rowY, prioNameW, btnH, true, false, DevButton.ButtonType.NORMAL);
        menuElements.add(prioBtn);
        this.idPrioRow[i] = menuElements.size() - 1;

        boolean canUp = (i > 0);
        DevButton upBtn = new DevButton("^", -1, col2X + prioNameW + gap, rowY, upDownW, btnH, canUp, false, DevButton.ButtonType.NORMAL);
        menuElements.add(upBtn);
        this.idPrioUp[i] = menuElements.size() - 1;

        boolean canDown = (i < 3);
        DevButton downBtn = new DevButton("v", -1, col2X + prioNameW + gap + upDownW + gap, rowY, upDownW, btnH, canDown, false, DevButton.ButtonType.NORMAL);
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
        int colBgPad = Math.max(6, Math.min(12, CFG.PADD * 2));
        oSB.setColor(new Color(0.0f, 0.0f, 0.0f, 0.22f));
        IMGManager.getIMG(Images.pix255).draw(oSB, this.col1XPos - colBgPad, this.columnsStartY - colBgPad, this.colWidth + colBgPad * 2, this.columnsHeight + colBgPad * 2);
        IMGManager.getIMG(Images.pix255).draw(oSB, this.col2XPos - colBgPad, this.columnsStartY - colBgPad, this.colWidth + colBgPad * 2, this.columnsHeight + colBgPad * 2);

        oSB.setColor(Color.WHITE);

        this.beginClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.drawMenuM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
        this.endClipM(oSB, iTranslateX, iTranslateY, sliderMenuIsActive);
    }
}
