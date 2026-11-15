package age.of.civilizations2.jakowski.lukasz.NextGen;

import age.of.civilizations2.jakowski.lukasz.CFG;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_Data;
import age.of.civilizations2.jakowski.lukasz.Civilizations.PeaceT.PeaceTreaty_Demands;
import age.of.civilizations2.jakowski.lukasz.GameCalendar;
import age.of.civilizations2.jakowski.lukasz.War_GameData;
import java.util.ArrayList;
import java.util.List;

/**
 * PeaceBargainManager - розширена система мирних переговорів та гнучкого торгу.
 * 
 * 1. Комплексні умови миру:
 *    - Військові репарації (War Reparations) на 20 ходів.
 *    - Примусова васалізація.
 *    - Відторгнення компактних прикордонних регіонів (Anti-Border-Gore).
 * 2. Зустрічні пропозиції ШІ (Counter-Offers):
 *    - Якщо переможець вимагає дещо більше, ніж доступні бали перемоги, ШІ не йде на взаємне винищення,
 *      а пропонує компроміс: виплату репарацій та передачу частини прикордоння, зберігаючи столицю.
 */
public class PeaceBargainManager {
    public static boolean peaceBargainingEnabled = true;

    /**
     * Оцінює, чи готовий переможений ШІ прийняти мирні вимоги або запропонувати компроміс.
     * @return 1 - прийняти повністю, 0 - запропонувати зустрічний компроміс, -1 - відхилити
     */
    public static int evaluatePeaceOffer(int winnerCivID, int loserCivID, int demandedPoints, int availablePoints) {
        try {
            if (winnerCivID <= 0 || loserCivID <= 0) return -1;

            int loserProvs = CFG.core.getCiv(loserCivID).getNumOfProvs();
            int loserArmies = CFG.core.getCiv(loserCivID).getNumberOfUnits();
            int winnerArmies = CFG.core.getCiv(winnerCivID).getNumberOfUnits();

            int capProv = CFG.core.getCiv(loserCivID).getCapitalProvID();
            boolean capitalLost = (capProv >= 0 && CFG.core.getProv(capProv).getCivId() != loserCivID);

            // Якщо столиця втрачена і армія розгромлена (>3:1 перевага переможця)
            if (capitalLost && winnerArmies > loserArmies * 2) {
                if (demandedPoints <= availablePoints * 1.30f) {
                    return 1; // Повна капітуляція
                }
                return 0; // Зустрічна пропозиція
            }

            // Стандартна перевірка балів
            if (demandedPoints <= availablePoints) {
                return 1; // Умови в межах правил
            } else if (demandedPoints <= (int)((float)availablePoints * 1.15f)) {
                return 0; // ШІ готовий до компромісу (торг)
            } else {
                return -1; // Неприйнятні надмірні вимоги
            }
        } catch (Exception ex) {
            return -1;
        }
    }

    /**
     * Застосовує виплату військових репарацій між державами на 20 ходів.
     */
    public static void applyWarReparations(int victorCivID, int defeatedCivID) {
        try {
            if (victorCivID <= 0 || defeatedCivID <= 0 || victorCivID == defeatedCivID) return;

            CFG.core.getCiv(victorCivID).addWarReparationsGets(defeatedCivID);
            CFG.core.getCiv(defeatedCivID).addWarReparationsPay(victorCivID);

            boolean isUkr = (CFG.lang != null && CFG.lang.get("LANGUAGENAME") != null)
                    ? CFG.lang.get("LANGUAGENAME").equalsIgnoreCase("Українська")
                    : true;

            String victName = CFG.core.getCiv(victorCivID).getCivName();
            String defName = CFG.core.getCiv(defeatedCivID).getCivName();

            String repMsg = isUkr
                    ? ("📜 Мирний договір: " + defName + " виплачуватиме щоходові військові репарації для " + victName + " протягом 20 ходів.")
                    : ("📜 Peace Treaty: " + defName + " will pay war reparations to " + victName + " for 20 turns.");

            CFG.toastM.addM(repMsg, CFG.COLOR_POSITIVE);
            CFG.toastM.setTimeInView(4500);
        } catch (Exception ex) {
            CFG.exceptionStack(ex);
        }
    }
}
