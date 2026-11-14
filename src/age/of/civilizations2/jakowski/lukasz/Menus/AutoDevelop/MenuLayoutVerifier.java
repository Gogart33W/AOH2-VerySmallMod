package age.of.civilizations2.jakowski.lukasz.Menus.AutoDevelop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.GdxNativesLoader;
import age.of.civilizations2.jakowski.lukasz.Button.MenuElemUI;
import age.of.civilizations2.jakowski.lukasz.CFG;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class MenuLayoutVerifier {

    public static class CheckResult {
        public String resName;
        public int totalElements;
        public int outOfBoundsCount = 0;
        public int overlappingPairsCount = 0;
        public int textOverflowCount = 0;
        public String details = "";
    }

    public static void initGdx() {
        try {
            GdxNativesLoader.load();
            if (Gdx.files == null) {
                Gdx.files = new Lwjgl3Files();
            }
            if (Gdx.gl == null) {
                GL20 mockGL = (GL20) Proxy.newProxyInstance(
                        GL20.class.getClassLoader(),
                        new Class[]{GL20.class},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                Class<?> rt = method.getReturnType();
                                if (rt.equals(int.class)) return 1;
                                if (rt.equals(boolean.class)) return false;
                                if (rt.equals(float.class)) return 0.0f;
                                if (rt.equals(long.class)) return 0L;
                                if (rt.equals(String.class)) return "";
                                return null;
                            }
                        }
                );
                Gdx.gl = mockGL;
                Gdx.gl20 = mockGL;
            }
            if (Gdx.graphics == null) {
                Graphics mockGraphics = (Graphics) Proxy.newProxyInstance(
                        Graphics.class.getClassLoader(),
                        new Class[]{Graphics.class},
                        new InvocationHandler() {
                            @Override
                            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                Class<?> rt = method.getReturnType();
                                if (rt.equals(int.class)) return 1000;
                                if (rt.equals(boolean.class)) return false;
                                if (rt.equals(float.class)) return 1.0f;
                                if (rt.equals(long.class)) return 0L;
                                if (rt.equals(String.class)) return "";
                                return null;
                            }
                        }
                );
                Gdx.graphics = mockGraphics;
            }
        } catch (Throwable ignored) {
        }
    }

    public static CheckResult verifyResolution(int width, int height) {
        CFG.GAMEWIDTH = width;
        CFG.GAMEHEIGHT = height;
        CFG.PADD = 5;

        CheckResult cr = new CheckResult();
        cr.resName = width + "x" + height;

        Menu_AutoDevelop menu = new Menu_AutoDevelop();

        int dialogStartX = 0, dialogStartY = 0, dialogWidth = 0, dialogHeight = 0;
        try {
            Field fStartX = Menu_AutoDevelop.class.getDeclaredField("dialogStartX");
            fStartX.setAccessible(true);
            dialogStartX = fStartX.getInt(menu);

            Field fStartY = Menu_AutoDevelop.class.getDeclaredField("dialogStartY");
            fStartY.setAccessible(true);
            dialogStartY = fStartY.getInt(menu);

            Field fWidth = Menu_AutoDevelop.class.getDeclaredField("dialogWidth");
            fWidth.setAccessible(true);
            dialogWidth = fWidth.getInt(menu);

            Field fHeight = Menu_AutoDevelop.class.getDeclaredField("dialogHeight");
            fHeight.setAccessible(true);
            dialogHeight = fHeight.getInt(menu);
        } catch (Exception ex) {
            cr.details += "Reflect error: " + ex.getMessage() + "\n";
            return cr;
        }

        int elemCount = menu.getMenuElemsSize();
        cr.totalElements = elemCount;

        List<MenuElemUI> elems = new ArrayList<>();
        // Останній елемент - idCatchAll (Button_Transparent на весь екран)
        for (int i = 0; i < elemCount - 1; i++) {
            elems.add(menu.getMenuElem(i));
        }

        // 1. Перевірка виходу за межі діалогу та екрану
        int pad = CFG.PADD;
        int dX2 = dialogStartX + dialogWidth;
        int dY2 = dialogStartY + dialogHeight;

        for (int i = 0; i < elems.size(); i++) {
            MenuElemUI elem = elems.get(i);
            int x1 = elem.getPosXE();
            int y1 = elem.getPosY();
            int w = elem.getWidthE();
            int h = elem.getHeightE();
            int x2 = x1 + w;
            int y2 = y1 + h;

            if (x1 < dialogStartX - 1 || x2 > dX2 + 1 || y1 < dialogStartY - 1 || y2 > dY2 + 1 || x1 < 0 || x2 > width || y1 < 0 || y2 > height) {
                cr.outOfBoundsCount++;
                cr.details += "Elem[" + i + " '" + elem.getTextE() + "'] out of bounds: [" + x1 + "," + y1 + "," + w + "," + h + "] in dialog [" + dialogStartX + "," + dialogStartY + "," + dialogWidth + "," + dialogHeight + "]\n";
            }

            // 3. Перевірка ширини тексту
            int textW = elem.getTextWidthU();
            int maxAllowedTextW = w - pad * 2;
            if (textW > maxAllowedTextW && elem.getTextE() != null && !elem.getTextE().isEmpty()) {
                cr.textOverflowCount++;
                cr.details += "Elem[" + i + " '" + elem.getTextE() + "'] text overflow: textW=" + textW + " > max=" + maxAllowedTextW + " (w=" + w + ")\n";
            }
        }

        // 2. Перевірка взаємного перетину (попарно)
        for (int i = 0; i < elems.size(); i++) {
            MenuElemUI e1 = elems.get(i);
            int e1_x1 = e1.getPosXE();
            int e1_y1 = e1.getPosY();
            int e1_x2 = e1_x1 + e1.getWidthE();
            int e1_y2 = e1_y1 + e1.getHeightE();

            for (int j = i + 1; j < elems.size(); j++) {
                MenuElemUI e2 = elems.get(j);
                int e2_x1 = e2.getPosXE();
                int e2_y1 = e2.getPosY();
                int e2_x2 = e2_x1 + e2.getWidthE();
                int e2_y2 = e2_y1 + e2.getHeightE();

                boolean xOverlap = Math.max(e1_x1, e2_x1) < Math.min(e1_x2, e2_x2);
                boolean yOverlap = Math.max(e1_y1, e2_y1) < Math.min(e1_y2, e2_y2);

                if (xOverlap && yOverlap) {
                    cr.overlappingPairsCount++;
                    cr.details += "Overlap between Elem[" + i + " '" + e1.getTextE() + "'] and Elem[" + j + " '" + e2.getTextE() + "']\n";
                }
            }
        }

        return cr;
    }

    public static void main(String[] args) {
        initGdx();
        System.out.println("=== HEADLESS LAYOUT VERIFICATION ===");
        System.out.println("[INFO] Note: In headless mode without game font glyph textures, text width verification relies on approxCharW (11px). Visual and pixel verification must be confirmed in-game.");
        int[][] resList = new int[][]{
                {962, 725},
                {1280, 720},
                {1366, 768},
                {1920, 1080}
        };

        boolean allPassed = true;
        for (int[] res : resList) {
            CheckResult cr = verifyResolution(res[0], res[1]);
            System.out.printf("Resolution: %-10s | Total Elements: %-2d | Out of Bounds: %-2d | Overlaps: %-2d | Text Overflow: %-2d | Status: %s\n",
                    cr.resName, cr.totalElements, cr.outOfBoundsCount, cr.overlappingPairsCount, cr.textOverflowCount,
                    (cr.outOfBoundsCount == 0 && cr.overlappingPairsCount == 0 && cr.textOverflowCount == 0) ? "PASS" : "FAIL");
            if (cr.outOfBoundsCount > 0 || cr.overlappingPairsCount > 0 || cr.textOverflowCount > 0) {
                allPassed = false;
                System.out.println(cr.details);
            }
        }

        if (allPassed) {
            System.out.println("ALL RESOLUTION CHECKS PASSED PERFECTLY!");
        } else {
            System.out.println("SOME CHECKS FAILED!");
            System.exit(1);
        }
    }
}
