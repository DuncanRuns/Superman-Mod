package me.duncanruns.superman;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public final class Superman {
    public static Identifier FLIGHT_DATA = Identifier.tryParse("superman:flight_data");
    private static final double PI = Math.PI;
    private static final double TWOPI = PI * 2;

    private Superman() {
    }

    public static double angleBetween(Vec3d v1, Vec3d v2) {
        double diff1 = ((PI + Math.atan2(v1.x, v1.z) - Math.atan2(v2.x, v2.z)) % (TWOPI)) - PI;
        double diff2 = ((PI + Math.atan2(Math.sqrt(v1.x * v1.x + v1.z * v1.z), v1.y) - Math.atan2(Math.sqrt(v2.x * v2.x + v2.z * v2.z), v2.y)) % (TWOPI)) - PI;
        double out = Math.sqrt(diff1 * diff1 + diff2 * diff2);
        if (out > PI) {
            return TWOPI - out;
        }
        return out;
    }
}
