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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AreaTest {

    @Test
    void testCalculationCorrectLowerHigher() {
        Location locA = new Location("world", 2, 4, 6);
        Location locB = new Location("world", 3, 5, 7);
        Area area = new Area(locA, locB);
        assertAreaLowerHigher(area);
        assertThat(area.getWorld(), is(equalTo("world")));
    }

    @Test
    void testCalculationReverseLowerHigher() {
        Location locA = new Location("world", 3, 5, 7);
        Location locB = new Location("world", 2, 4, 6);
        Area area = new Area(locA, locB);
        assertAreaLowerHigher(area);
    }

    @Test
    void testCalculationMixedLowerHigher() {
        Location locA = new Location("world", 3, 5, 70);
        Location locB = new Location("world", 2, 90, 6);
        Area area = new Area(locA, locB);
        assertAreaLowerHigher(area);
    }

    @Test
    void testCalculationNegativeLowerHigher() {
        Location locA = new Location("world", -3, 5, -70);
        Location locB = new Location("world", 2, -90, 6);
        Area area = new Area(locA, locB);
        assertAreaLowerHigher(area);
        assertThat(area.getLower().getX(), is(equalTo(-3)));
        assertThat(area.getLower().getY(), is(equalTo(-90)));
        assertThat(area.getLower().getZ(), is(equalTo(-70)));
        assertThat(area.getHigher().getX(), is(equalTo(2)));
        assertThat(area.getHigher().getY(), is(equalTo(5)));
        assertThat(area.getHigher().getZ(), is(equalTo(6)));
    }

    @Test
    void testInvalidMixedWorlds() {
        Location locA = new Location("world", 3, 5, 70);
        Location locB = new Location("nether", 2, 90, 6);
        assertThrows(IllegalArgumentException.class, () -> {
            new Area(locA, locB);
        });
    }

    private void assertAreaLowerHigher(Area area) {
        assertThat(area.getHigher().getX(), is(greaterThan(area.getLower().getX())));
        assertThat(area.getHigher().getY(), is(greaterThan(area.getLower().getY())));
        assertThat(area.getHigher().getZ(), is(greaterThan(area.getLower().getZ())));
    }
}
