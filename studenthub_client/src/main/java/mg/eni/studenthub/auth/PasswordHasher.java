package mg.eni.studenthub.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import mg.eni.studenthub.config.ConfigLoader;

/**
 * Utilitaire de hachage/verification des mots de passe avec BCrypt.
 * - Le pepper est lu depuis config.properties
 * - Le facteur de coût (work factor) est configurable
 */
public final class PasswordHasher {
    private static final String PEPPER = ConfigLoader.get("auth.pepper");
    private static final int COST = ConfigLoader.getInt("auth.bcrypt.cost", 12);

    private PasswordHasher() {}

    public static String hash(String rawPassword) {
        String withPepper = rawPassword + PEPPER;
        return BCrypt.withDefaults().hashToString(COST, withPepper.toCharArray());
    }

    public static boolean verify(String rawPassword, String hashed) {
        String withPepper = rawPassword + PEPPER;
        BCrypt.Result res = BCrypt.verifyer().verify(withPepper.toCharArray(), hashed);
        return res.verified;
    }
}
