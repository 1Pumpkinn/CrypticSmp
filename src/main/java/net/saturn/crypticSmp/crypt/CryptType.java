package net.saturn.crypticSmp.crypt;

import net.saturn.crypticSmp.crypt.types.AquarionCrypt;
import net.saturn.crypticSmp.crypt.types.EclipserraCrypt;
import net.saturn.crypticSmp.crypt.types.SolariCrypt;
import net.saturn.crypticSmp.crypt.types.TerrakaiCrypt;
import net.saturn.crypticSmp.crypt.types.ThundrosCrypt;
import net.saturn.crypticSmp.crypt.types.UmbraeCrypt;
import net.saturn.crypticSmp.crypt.types.ZepharaCrypt;
import org.bukkit.entity.Player;

public enum CryptType {
    SOLARI("Solari - Crypt of the Sun", new SolariCrypt()),
    UMBRAE("Umbrae - Crypt of the Shadow", new UmbraeCrypt()),
    TERRAKAI("Terrakai - Crypt of the Earth", new TerrakaiCrypt()),
    ZEPHARA("Zephara - Crypt of the Wind", new ZepharaCrypt()),
    NYTHERIS("Nytheris - Crypt of the Nether", new net.saturn.crypticSmp.crypt.types.NytherisCrypt()),
    AQUARION("Aquarion - Crypt of the Sea", new AquarionCrypt()),
    THUNDROS("Thundros - Crypt of the Storm", new ThundrosCrypt()),
    ECLIPSERRA("Eclipserra - Crypt of the Moon", new EclipserraCrypt());

    private final String displayName;
    private final net.saturn.crypticSmp.crypt.CryptAbility ability;

    CryptType(String displayName, net.saturn.crypticSmp.crypt.CryptAbility ability) {
        this.displayName = displayName;
        this.ability = ability;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void applyPassiveEffects(Player player) {
        ability.applyPassive(player);
    }

    public void useAbility1(Player player, CryptManager manager) {
        ability.useAbility1(player, manager);
    }

    public void useAbility2(Player player, CryptManager manager) {
        ability.useAbility2(player, manager);
    }

    public String getAbility1Name() {
        return ability.getAbility1Name();
    }

    public String getAbility2Name() {
        return ability.getAbility2Name();
    }
}