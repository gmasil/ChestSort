/**
 * ChestSort
 * Copyright © 2021 gmasil.de
 *
 * This file is part of ChestSort.
 *
 * ChestSort is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ChestSort is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ChestSort. If not, see <https://www.gnu.org/licenses/>.
 */
package de.headshotharp.chestsort.hibernate.dao.generic;

import java.util.function.ToIntFunction;

public class Area {

    private Location lower;
    private Location higher;

    public Area(Location a, Location b) {
        if (!a.getWorld().equals(b.getWorld())) {
            throw new IllegalArgumentException("The locations are from different worlds");
        }
        lower = new Location(a.getWorld(), getLower(a, b, Location::getX), getLower(a, b, Location::getY),
                getLower(a, b, Location::getZ));
        higher = new Location(a.getWorld(), getHigher(a, b, Location::getX), getHigher(a, b, Location::getY),
                getHigher(a, b, Location::getZ));
    }

    private int getLower(Location a, Location b, ToIntFunction<Location> func) {
        return Math.min(func.applyAsInt(a), func.applyAsInt(b));
    }

    private int getHigher(Location a, Location b, ToIntFunction<Location> func) {
        return Math.max(func.applyAsInt(a), func.applyAsInt(b));
    }

    public Location getLower() {
        return lower;
    }

    public Location getHigher() {
        return higher;
    }

    public String getWorld() {
        return lower.getWorld();
    }
}
